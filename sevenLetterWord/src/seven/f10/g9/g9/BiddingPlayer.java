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

package seven.f10.g9.g9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import seven.g0.Word;
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

	private final int POINT_THRESHOLD = 57;
	private static LinkedList<String> allWords = new LinkedList<String>();
	private LinkedList<String> remainingWords = new LinkedList<String>();
	private int beginScore;
	
	////////////////////////////////
	static ArrayList<Word> smallWordList;
	////////////////////////////////
	
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
				//String word = line.split(",")[1];

				////////////////////////////////
				smallWordList.add(new Word(word));
				////////////////////////////////
				
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

//			for (int z = 0; z < 26; z++) {
//				System.err.print("\n" + alphabet.charAt(z));
//				System.err.print("\t" + sevenLetterSingles.get(z).size());
//				if (sevenLetterDoubles.get(z) != null)
//					System.err.print("\t" + sevenLetterDoubles.get(z).size());
//				if (sevenLetterTriples.get(z) != null)
//					System.err.print("\t" + sevenLetterTriples.get(z).size());
//				if (sevenLetterQuads.get(z) != null)
//					System.err.print("\t" + sevenLetterQuads.get(z).size());
//			}
		} catch (FileNotFoundException e) {
			// does nothing...
		}
	}

	public void Register() {
		// does nothing so far...
	}

	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, int total_rounds, ArrayList<String> PlayerList, SecretState secretstate, int PlayerID) {
		
		///////////////////////////////////////
		///////////////////////////////////////
		///////////////////////////////////////
		if (PlayerBidList.isEmpty()) {
			cachedBids1 = PlayerBidList;
		}

		if (null == currentLetters1) {
			currentLetters1 = new ArrayList<Character>();
			ourID = PlayerID;
			for (Letter l : secretstate.getSecretLetters()) {
				currentLetters1.add(l.getAlphabet());
			}
		} else {
			if (cachedBids1.size() > 0) {
				checkBid1(cachedBids1.get(cachedBids1.size() - 1));
			}
		}
		///////////////////////////////////////
		///////////////////////////////////////
		///////////////////////////////////////
		
		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
			ourID = PlayerID;
		}

		if (currentLetters == null)
		{
			remainingWords = allWords;
			beginScore = secretstate.getScore();
			currentLetters = new ArrayList<Character>();
			ArrayList<Letter> letters = secretstate.getSecretLetters();
			if (letters.size() > 0)
			{
				remainingWords = sevenLetterSingles.get(letters.get(0).getAlphabet() - 65);
				currentLetters.add(letters.get(0).getAlphabet());
				for (int i = 1; i < letters.size(); i++) {
					currentLetters.add(letters.get(i).getAlphabet());
					remainingWords = calcPotentialWords(letters.get(i).getAlphabet());
				}
			}
		}
		else if (cachedBids.size() > 0) {
			checkBid(cachedBids.get(cachedBids.size() - 1));
		}

		if (currentLetters.size() > 6)
		{
			return 0;
		}

		int maxBid = (int) ((double) (POINT_THRESHOLD - beginScore + secretstate.getScore()) / (double) (7 - currentLetters.size()));
		char currentLetter = bidLetter.getAlphabet();

		int potentialWords = calcPotentialWords(currentLetter).size();

		if (currentLetters.size() > 5 && potentialWords > 0)
		{
			//System.err.println("potential: " + calcPotentialWords(currentLetter).get(0));
			return maxBid;
		}
		else if (potentialWords == 0)
		{
			l.trace("0 possible words");
			return 0;

		}

		int rank = 1;
		for (int i = 0; i < sevenLetterSingles.size(); i++)
		{
			int currCopies = numCopies((char)(i + 65));
			if(currCopies == 0)
			{
				if (i != currentLetter - 65 && intersect(remainingWords, sevenLetterSingles.get(i)).size() > potentialWords)
				{
					rank++;
				}
			}
			else if(currCopies ==1 && sevenLetterDoubles.get(i) != null)
			{
				if (i != currentLetter - 65 && intersect(remainingWords, sevenLetterDoubles.get(i)).size() > potentialWords)
				{
					rank++;
				}
			}
			else if(currCopies ==2 && sevenLetterTriples.get(i) != null)
			{
				if (i != currentLetter - 65 && intersect(remainingWords, sevenLetterTriples.get(i)).size() > potentialWords)
				{
					rank++;
				}
			}
			else if(currCopies ==3 && sevenLetterQuads.get(i) != null)
			{
				if (i != currentLetter - 65 && intersect(remainingWords, sevenLetterQuads.get(i)).size() > potentialWords)
				{
					rank++;
				}
			}			
		}

		int bid = (int) ((double) maxBid * ((double) (27 - rank) / 26.0));
		return bid;
	}

	
	ArrayList<Character> currentLetters1;
	private ArrayList<PlayerBids> cachedBids1;
	
	public String returnWord()
	{
//		checkBid(cachedBids.get(cachedBids.size() - 1));
//		char c[] = new char[currentLetters.size()];
//		for (int i = 0; i < currentLetters.size() && i < 7; i++) {
//			c[i] = currentLetters.get(i);
//		}
//
//		String s = new String(c);
//		Word ourletters = new Word(s);
//		Word bestword = new Word("");
//		for (Word w : wordList) {
//		if (ourletters.contains(w)) {
//			
//			int tempScore = w.score;
//			if(w.length == 7)
//				tempScore += 50;
//			
//			if (tempScore > bestword.score) {
//				bestword = w;
//			}
//
//		}
//		}
//
//		currentLetters = null;
//		return bestword.word;
		
		checkBid1(cachedBids1.get(cachedBids1.size() - 1));
		char c[] = new char[currentLetters1.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = currentLetters1.get(i);
		}
		String s = new String(c);
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		for (Word w : smallWordList) {
			if (ourletters.contains(w)) {
				
				int tempScore = w.score;
				if(w.length == 7)
					tempScore += 50;
				
				if (tempScore > bestword.score) {
					bestword = w;
				}

			}
		}
		currentLetters = null;
		currentLetters1 = null;
		return bestword.word;
	}

	private void checkBid1(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			currentLetters1.add(b.getTargetLetter().getAlphabet());
		}
	}
	
	private void checkBid(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			if(currentLetters.size() < 7 && b.getWinAmmount() > 0)
			{
				currentLetters.add(b.getTargetLetter().getAlphabet());
				remainingWords = intersect(remainingWords, sevenLetterSingles.get(b.getTargetLetter().getAlphabet() - 65));
			}
		}
	}

	private LinkedList<String> intersect(LinkedList<String> list1, LinkedList<String> list2)
	{
		LinkedList<String> newList = new LinkedList<String>();
		ListIterator<String> itr1 = (ListIterator<String>) list1.iterator();
		ListIterator<String> itr2 = (ListIterator<String>) list2.iterator();

		if(itr1.hasNext() && itr2.hasNext())
		{
			String s1 = itr1.next();
			String s2 = itr2.next();
			while( s1 != null)
			{
				int compResult = s1.compareTo(s2);
				if( compResult == 0 )
				{
					newList.add(s1);
					
					if( itr1.hasNext() && itr2.hasNext() )
					{
						s1 = itr1.next();
						s2 = itr2.next();
					}
					else
					{
						s1 = null;
					}
				}
				else if( compResult < 0 && itr1.hasNext() )
				{
					s1 = itr1.next();
				}
				else if( compResult > 0 && itr2.hasNext() )
				{
					s2 = itr2.next();
				}
				else
				{
					s1 = null;
				}
			}
		}
		return newList;
	}

	private int numCopies(char letter)
	{
		int copies = 0;
		for(char c : currentLetters)
		{
			if(letter == c)
				copies++;
		}

		return copies;
	}
	
	private LinkedList<String> calcPotentialWords(char letter)
	{
		int copies = numCopies(letter);

		if( copies == 0 )
		{
			return intersect(remainingWords, sevenLetterSingles.get(letter - 65));
		}
		else if( copies == 1  && sevenLetterDoubles.get(letter - 65) != null)
		{
			return intersect(remainingWords, sevenLetterDoubles.get(letter - 65));
		}
		else if( copies == 2  && sevenLetterTriples.get(letter - 65) != null)
		{
			return intersect(remainingWords, sevenLetterTriples.get(letter - 65));
		}
		else if( copies == 3  && sevenLetterQuads.get(letter - 65) != null)
		{
			return intersect(remainingWords, sevenLetterQuads.get(letter - 65));
		}

		return null;
	}

	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
}

