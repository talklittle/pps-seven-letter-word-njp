/**
 * BiddingPlayer.java
 * 
 * A simple modified Scrabble player that does some pre-processing work to
 * determine how much to bid based on the likelihood of a 7-letter word with
 * the given hand of letters.
 * 
 * Borrows code from Jon Bell's StingyPlayer.java which is used to determine
 * a high-scoring word given a set of letters. 
 * 
 * Group 9:
 * 	John Graham
 * 	Monica Ramirez-Santana
 *  Daniel Wilkey
 */

package seven.f10.g9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class BiddingPlayer implements Player {
	// create the logger object
	protected Logger l = Logger.getLogger(this.getClass());

	// used to track the frequency of letters in 7-letter words
	static final int[] gameLetters = new int[] { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1,
			1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1 };
	static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static ArrayList<LinkedList<String>> sevenLetterSingles = new ArrayList<LinkedList<String>>(26);
	static ArrayList<LinkedList<String>> sevenLetterDoubles = new ArrayList<LinkedList<String>>(26);
	static ArrayList<LinkedList<String>> sevenLetterTriples = new ArrayList<LinkedList<String>>(26);
	static ArrayList<LinkedList<String>> sevenLetterQuads = new ArrayList<LinkedList<String>>(26);
	static final char[] blacklist = {'J', 'Q', 'V', 'W', 'X', 'Z'};
	static final int POINT_THRESHOLD = 57;
	static final int DEFLATOR = 15;
	static final int IMPROVEMENT_BID = 5;
	static LinkedList<String> allWords = new LinkedList<String>();
	
	private LinkedList<String> remainingWords = new LinkedList<String>();
	private int beginScore;
	private int totalBids;
	private int currentBid;
	private ArrayList<Character> allLetters;
	private Word currentBestWord;
	private int minBid;
	private boolean bidOn = true;

	static ArrayList<Word> smallWordList;

	// borrowed from Jon Bell's stingyplayer, used to determine the best word
	// given a hand of letters
	static Word[] wordList = new Word[267751];
	ArrayList<Character> currentLetters;
	private int ourID;
	private ArrayList<PlayerBids> cachedBids;

	static {

		smallWordList = new ArrayList<Word>();

		// instantiate the array of linked lists
		for (int x = 0; x < 26; x++) {
			sevenLetterSingles.add(new LinkedList<String>());

			// doubles
			if (gameLetters[x] >= 2)
				sevenLetterDoubles.add(new LinkedList<String>());
			else
				sevenLetterDoubles.add(null);

			// triples
			if (gameLetters[x] >= 3)
				sevenLetterTriples.add(new LinkedList<String>());
			else
				sevenLetterTriples.add(null);

			// quads
			if (gameLetters[x] >= 4)
				sevenLetterQuads.add(new LinkedList<String>());
			else
				sevenLetterQuads.add(null);
		}

		try {
			// scan through the list of words
			File wordFile = new File("src/seven/g1/super-small-wordlist.txt");
			Scanner wordListScanner = new Scanner(wordFile);
			wordListScanner.nextLine();

			int wordCount = 0;

			// if word is 7 letters, add it to hashset and track each letter
			while (wordListScanner.hasNextLine()) {
				String word = wordListScanner.nextLine();

				smallWordList.add(new Word(word));

				wordList[wordCount] = new Word(word);
				wordCount++;

				if (word.length() == 7) {

					allWords.add(word);

					// add the word to the list of 7-letter words, don't add
					// word if duplicate letter
					for (int x = 0; x < 7; x++) {
						char letter = word.charAt(x);

						ArrayList<LinkedList<String>> wordLists = new ArrayList<LinkedList<String>>();
						wordLists.add(sevenLetterSingles.get(letter - 65));
						wordLists.add(sevenLetterDoubles.get(letter - 65));
						wordLists.add(sevenLetterTriples.get(letter - 65));
						wordLists.add(sevenLetterQuads.get(letter - 65));

						// check for duplicate values
						int dupCount = 0;
						for (int y = 0; y < 7; y++) {
							if (word.charAt(y) == letter)
								dupCount++;
						}

						for (int y = 1; y < 5; y++) {
							LinkedList<String> l = wordLists.get(y - 1);

							if ((l != null)
									&& (dupCount >= y)
									&& (gameLetters[letter - 65] >= y)
									&& (l.isEmpty() || (!l.isEmpty() && !l
											.getLast().equals(word))))
								l.add(word);
						}
					}
				}
			}

			Collections.sort(smallWordList);
		} 
		catch (FileNotFoundException e) {
			// does nothing...
		}
	}

	public void Register() {
		// does nothing so far...
	}

	/**
	 * Returns a numeric bid for a letter.
	 */
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID)
	{

		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
			ourID = PlayerID;
		}

		// called at the beginning of the round
		if (currentLetters == null)
		{
			initiateFirstBidInRound(PlayerList, secretstate);
		}
		else if (cachedBids.size() > 0)
		{
			// check to see if we won the last letter
			checkBid(cachedBids.get(cachedBids.size() - 1));
		}

		// are we too slow?
		long now = System.currentTimeMillis();
		
		currentBid++;
		
		int maxBid=0;
		if(currentLetters.size() > 6)
			maxBid = minBid;
		else
			maxBid = (int) ((double) (POINT_THRESHOLD - beginScore + secretstate.getScore()) / (double) (7 - currentLetters.size()));
		
		if(maxBid > DEFLATOR && currentBid < (2.0/3.0) * (double)totalBids)
			maxBid = DEFLATOR;
		
		char currentLetter = bidLetter.getAlphabet();
		LinkedList<String> potentialWords = calcPotentialWords(currentLetter);
		
		if(allLetters.size() > 4)
		{
			ArrayList<Character> potentialLetters = new ArrayList<Character>(allLetters);
			potentialLetters.add(currentLetter);
			Word potentialBestWord = calcBestWord(potentialLetters);
			
			if(currentBestWord == null)
				currentBestWord = calcBestWord(allLetters);
			
			// if we've found a better 7-letter word than what we have, bet the difference between these 2 words 
			if (currentBestWord.length == 7)
			{
				if(currentBestWord.score < potentialBestWord.score)
				{
					l.trace("better word: " + potentialBestWord.word + ": " + potentialBestWord.score + " vs " + currentBestWord.score);
					return Math.max(potentialBestWord.score - currentBestWord.score, minBid);
				}
				
				bidOn = false;
				return minBid;
			}
			else if(potentialBestWord.length == 7)
			{
				l.trace("potential: " + potentialBestWord.word);
				return maxBid;
			}
			// if the next letter opens up the number of potential 7-letter words, bid for it!
			else if(potentialWords.size() == 0) 
			{
				bidOn = false;
				
				LinkedList<String> curAvailableWords = findAvailableWords(allLetters);
				LinkedList<String> potAvailableWords = findAvailableWords(potentialLetters);

				if(curAvailableWords.size() < potAvailableWords.size())
				{
					return IMPROVEMENT_BID;
				}
				
				return minBid;
			}
		}
		
		l.trace("time first half: " + (System.currentTimeMillis() - now));

		int potentialScore = calcPotentialScore(currentLetter);

		int rank = 1;
		int maxScore = potentialScore;
		for (char c = 'A'; c < 'Z' + 1; c++) {
			int curScore = calcPotentialScore(c);
			if (curScore > potentialScore) {
				rank++;
			}
			if(curScore > maxScore)
				maxScore = curScore;
		}
		
		l.trace("time second half: " + (System.currentTimeMillis() - now ));

		int bid = (int) ((double) maxBid * ((double)potentialScore / (double)maxScore));
		
		if(rank > 18 || bid <= minBid)
		{
			bidOn = false;
			return minBid;
		}
		
		return bid;
	}

	/**
	 * Sets up bidding parameters at the beginning of a round.
	 * @param PlayerList
	 * @param secretstate
	 */
	private void initiateFirstBidInRound(ArrayList<String> PlayerList, SecretState secretstate) 
	{
		minBid = 0;
		if(PlayerList.size() < 4)
			minBid = 3;
		ArrayList<Letter> letters = secretstate.getSecretLetters();
		totalBids = PlayerList.size() * ( 8 - letters.size() );
		currentBid = 0;
		remainingWords = allWords;
		beginScore = secretstate.getScore();
		currentLetters = new ArrayList<Character>();
		allLetters = new ArrayList<Character>();
		currentBestWord = null;
		
		if (letters.size() > 0)
		{
			for (int i = 0; i < letters.size(); i++)
			{
				boolean blacklisted = false;
				for(int j=0; j<blacklist.length; j++)
				{
					if(letters.get(i).getAlphabet() == blacklist[j])
						blacklisted = true;
				}
				
				if(!blacklisted)
				{
					remainingWords = calcPotentialWords(letters.get(i).getAlphabet());
					currentLetters.add(letters.get(i).getAlphabet());
				}
				allLetters.add(letters.get(i).getAlphabet());
			}
		}
	}

	/**
	 * Finds the best possible word given our hand of letters.
	 */
	public String returnWord()
	{
		checkBid(cachedBids.get(cachedBids.size() - 1));
		Word bestword = calcBestWord(allLetters);
		
		currentLetters = null;
		allLetters = null;
		return bestword.word;
	}
	
	/**
	 * Given an ArrayList of characters (a potential hand), returns the best possible word.
	 * @param letters
	 * @return the best word possible
	 */
	private Word calcBestWord(ArrayList<Character> letters)
	{
		char c[] = new char[letters.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = letters.get(i);
		}
		
		String s = new String(c);
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		
		for (int i=smallWordList.size()-1; i>0; i--) {
			Word w = smallWordList.get(i);
			if(w.length == 6 && bestword.length == 7)
				break;
			if (ourletters.contains(w))
			{
				if (w.length == 7 && w.score < 50)
					w.score += 50;

				if (w.score > bestword.score) {
					bestword = w;
				}

			}
		}
		
		return bestword;
	}

	/**
	 * If we were the winner of the previous bid, we perform actions on the letter we just won.
	 * @param b
	 */
	private void checkBid(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			allLetters.add(b.getTargetLetter().getAlphabet());
			if (currentLetters.size() < 7 && bidOn == true) {
				remainingWords = calcPotentialWords(b.getTargetLetter().getAlphabet());
				currentLetters.add(b.getTargetLetter().getAlphabet());
			}
			if(allLetters.size() > 4)
				currentBestWord = calcBestWord(allLetters);
		}
		bidOn = true;
	}

	/**
	 * Finds the intersection of two LinkedLists of Strings, determining the words that are found 
	 * in both lists.
	 * @param list1
	 * @param list2
	 * @return
	 */
	private LinkedList<String> intersect(LinkedList<String> list1, LinkedList<String> list2) 
	{
		LinkedList<String> newList = new LinkedList<String>();
		ListIterator<String> itr1 = (ListIterator<String>) list1.iterator();
		ListIterator<String> itr2 = (ListIterator<String>) list2.iterator();

		if (itr1.hasNext() && itr2.hasNext())
		{
			String s1 = itr1.next();
			String s2 = itr2.next();
			boolean done = false;
			while (!done) {
				int compResult = s1.compareTo(s2);
				if (compResult == 0) {
					newList.add(s1);

					if (itr1.hasNext() && itr2.hasNext())
					{
						s1 = itr1.next();
						s2 = itr2.next();
					} 
					else
					{
						done = true;
					}
				}
				else if (compResult < 0 && itr1.hasNext()) 
				{
					s1 = itr1.next();
				}
				else if (compResult > 0 && itr2.hasNext())
				{
					s2 = itr2.next();
				}
				else
				{
					done = true;
				}
			}
		}

		return newList;
	}

	/**
	 * Returns how many of a single given letter are in our hand.
	 * @param letter the letter we want to count in our hand
	 * @return the number of given letters in our hand
	 */
	private int numCopies(char letter)
	{
		int copies = 0;
		for (char c : currentLetters)
		{
			if (letter == c)
				copies++;
		}

		return copies;
	}

	/**
	 * Creates a list of all the 7-letter words that are possible (with current hand) given a new character.
	 * @param letter
	 * @return a linkedlist of all the potential words
	 */
	private LinkedList<String> calcPotentialWords(char letter)
	{
		int copies = numCopies(letter);

		LinkedList<String> temp = new LinkedList<String>();

		if (copies == 0)
		{
			temp = intersect(remainingWords, sevenLetterSingles.get(letter - 65));
		}
		else if (copies == 1 && sevenLetterDoubles.get(letter - 65) != null)
		{
			temp = intersect(remainingWords, sevenLetterDoubles.get(letter - 65));
		}
		else if (copies == 2 && sevenLetterTriples.get(letter - 65) != null)
		{
			temp = intersect(remainingWords, sevenLetterTriples.get(letter - 65));
		}
		else if (copies == 3 && sevenLetterQuads.get(letter - 65) != null)
		{
			temp = intersect(remainingWords, sevenLetterQuads.get(letter - 65));
		}

		return temp;
	}
	
	/**
	 * 
	 * @param letter
	 * @return
	 */
	private int calcPotentialScore(char letter)
	{
		int copies = numCopies(letter);

		int score = 0;

		if (copies == 0)
		{
			score = countIntersect(remainingWords, sevenLetterSingles.get(letter - 65));
		}
		else if (copies == 1 && sevenLetterDoubles.get(letter - 65) != null)
		{
			score = countIntersect(remainingWords, sevenLetterDoubles.get(letter - 65));
		}
		else if (copies == 2 && sevenLetterTriples.get(letter - 65) != null)
		{
			score = countIntersect(remainingWords, sevenLetterTriples.get(letter - 65));
		}
		else if (copies == 3 && sevenLetterQuads.get(letter - 65) != null)
		{
			score = countIntersect(remainingWords, sevenLetterQuads.get(letter - 65));
		}

		return score;
	}
	
	/**
	 * Like intersect(), but counts the score of each available 7-letter word instead of creating
	 * a list with each new word. 
	 * @param list1
	 * @param list2
	 * @return
	 */
	private int countIntersect(LinkedList<String> list1, LinkedList<String> list2) 
	{
		int total = 0;
		
		ListIterator<String> itr1 = (ListIterator<String>) list1.iterator();
		ListIterator<String> itr2 = (ListIterator<String>) list2.iterator();

		if (itr1.hasNext() && itr2.hasNext())
		{
			String s1 = itr1.next();
			String s2 = itr2.next();
			boolean done = false;
			while (!done) {
				int compResult = s1.compareTo(s2);
				if (compResult == 0) {
					Word w = new Word(s1);
					total += w.score;

					if (itr1.hasNext() && itr2.hasNext())
					{
						s1 = itr1.next();
						s2 = itr2.next();
					} 
					else
					{
						done = true;
					}
				}
				else if (compResult < 0 && itr1.hasNext()) 
				{
					s1 = itr1.next();
				}
				else if (compResult > 0 && itr2.hasNext())
				{
					s2 = itr2.next();
				}
				else
				{
					done = true;
				}
			}
		}

		return total;
	}

	@Override
	public void updateScores(ArrayList<Integer> scores) {
		//  Auto-generated method stub
		
	}
	
	/**
	 * Finds all the 7-letter words that are within 1 character of creating.
	 * @param letters
	 * @return
	 */
	private LinkedList<String> findAvailableWords(ArrayList<Character> letters) 
	{
		String s = "";
		for(int i=0; i<letters.size(); i++)
		{
			s += letters.get(i);
		}

		LinkedList<String> newList = new LinkedList<String>();
		ListIterator<String> itr1 = (ListIterator<String>) allWords.iterator();

		if (itr1.hasNext())
		{
			String current;
			while (itr1.hasNext()) {
				current = itr1.next();
				
				if( withinOneLetter(s, current) == true )
				{
					newList.add(s);
				}
			}
		}

		return newList;
	}
	
	/**
	 * Determines if a 7-letter word is missing at most 1 character from our rack to be created. 
	 * @param letters
	 * @param current
	 * @return
	 */
	private boolean withinOneLetter(String letters, String current)
	{
		// TODO can be made faster w/ a hashset
		boolean found;
		int numMissingLetters = 0;
		for(int i=0; i<current.length(); i++)
		{
			found = false;
			for(int j=0; j<letters.length(); j++)
			{
				if(current.charAt(i) == letters.charAt(j))
				{
					found = true;
					j = letters.length();
				}
			}
			if( !found ){
				if(numMissingLetters == 0)
					numMissingLetters++;
				else
					return false;
			}
		}
		return true;
	}
}
