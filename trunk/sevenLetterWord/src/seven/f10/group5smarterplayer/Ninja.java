package seven.f10.group5smarterplayer;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

import seven.g1.Word;
import seven.ui.GameController;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.ScrabbleValues;
import seven.ui.SecretState;


public class Ninja implements Player {
	//word or getword()?

	static final Word[] wordlist;
	protected Logger log = Logger.getLogger(GameController.class);
	static private HashMap freq = new HashMap(26);
	static {
		BufferedReader r;
		String line = null;
		ArrayList<Word> wtmp = new ArrayList<Word>(55000);
		try {
			r = new BufferedReader(new FileReader("src/seven/f10/g5/group5wordlist.txt")); //place here the file of just the 7 letter words without anagrams 
			while (null != (line = r.readLine())) {
				wtmp.add(new Word(line.trim()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordlist = wtmp.toArray(new Word[wtmp.size()]);
			
		
	}

	ArrayList<Character> currentLetters;
	
	int numBidsInThisRound = 0;
	private int ourID;
	private ArrayList<PlayerBids> cachedBids;
	int n = 0; //get rid of this later
	int numLettersSoFar;
	int previousBet;
	double expectedScore;
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
		}
		
		int numBidsSoFar = PlayerBidList.size() + 1;
		int numBidsInThisRound = numBidsSoFar % (8*PlayerList.size());
		
		numLettersSoFar = PlayerBidList.size() + secretstate.getSecretLetters().size()*PlayerList.size();
		if(numLettersSoFar % (8*PlayerList.size()) == 0) //new round
		{
			currentLetters = null;
			initHashMap();
		}

		if (null == currentLetters) {
			currentLetters = new ArrayList<Character>();
			ourID = PlayerID;
			for (Letter l : secretstate.getSecretLetters()) {
				currentLetters.add(l.getAlphabet());
			}
		} else {
			if (cachedBids.size() > 0) {
				checkBid(cachedBids.get(cachedBids.size() - 1));
			}
		}
		
		
		
        // Get a set of the entries
        Set set = freq.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        //Map me= (MapEntry)i.
        Integer prevfreq= (Integer)freq.get(bidLetter.getAlphabet());
        freq.put( (char)bidLetter.getAlphabet(), new Integer (prevfreq+1));
        
        printHashMap();
        
		log.debug("\ncurrent letters: " + currentLetters);
		double expectedScoreWithoutLetterUpForBid = getExpectedScoreFromLetters(currentLetters);
		log.debug(" expected score without letter up for bid: " + expectedScoreWithoutLetterUpForBid);
		
		double expectedScoreIfWinLetter = getExpectedScoreFromLetters(currentLetters, bidLetter);
		log.debug("the letter being bid on is: " + bidLetter.getAlphabet());
		log.debug(" expected score if you win letter: " + expectedScoreIfWinLetter);
		
		/*if(n == 0 || n == 1 || n == 2)
		{
			n++;
			return 30;
		}*/
		previousBet = (int)(expectedScoreIfWinLetter - expectedScoreWithoutLetterUpForBid);
		expectedScore = expectedScoreIfWinLetter;
		if(expectedScoreIfWinLetter - expectedScoreWithoutLetterUpForBid < .5)
			return 0;
		else if(expectedScoreIfWinLetter - expectedScoreWithoutLetterUpForBid < 6)
		{
			return (int)(expectedScoreIfWinLetter - expectedScoreWithoutLetterUpForBid + 4.0); // + 4.0 to make it slightly more aggressive when betting a little 
		}
		
		return (int)(expectedScoreIfWinLetter - expectedScoreWithoutLetterUpForBid + .5); 
		//return 0;

	}
	
	
	private void printHashMap()
	{
		Set set = freq.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
		while(i.hasNext())
		{
			Map.Entry me = (Map.Entry)i.next();
			//log.debug(me.getKey() + ": " + me.getValue());
		}
	}
	
	private void initHashMap()
	{
		
		freq.put((char)'A', new Integer(0));
		freq.put((char)'B', new Integer(0));
		freq.put((char)'C', new Integer(0));
		freq.put((char)'D', new Integer(0));
		freq.put((char)'E', new Integer(0));
		freq.put((char)'F', new Integer(0));
		freq.put((char)'G', new Integer(0));
		freq.put((char)'H', new Integer(0));
		freq.put((char)'I', new Integer(0));
		freq.put((char)'J', new Integer(0));
		freq.put((char)'K', new Integer(0));
		freq.put((char)'L', new Integer(0));
		freq.put((char)'M', new Integer(0));
		freq.put((char)'N', new Integer(0));
		freq.put((char)'O', new Integer(0));
		freq.put((char)'P', new Integer(0));
		freq.put((char)'Q', new Integer(0));
		freq.put((char)'R', new Integer(0));
		freq.put((char)'S', new Integer(0));
		freq.put((char)'T', new Integer(0));
		freq.put((char)'U', new Integer(0));
		freq.put((char)'V', new Integer(0));
		freq.put((char)'W', new Integer(0));
		freq.put((char)'X', new Integer(0));
		freq.put((char)'Y', new Integer(0));
		freq.put((char)'Z', new Integer(0));
	}
	
	private void checkBid(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			log.debug("previous bet was " + previousBet);
			if(previousBet >= -3 && expectedScore > 0)
				{
					currentLetters.add(b.getTargetLetter().getAlphabet());
					log.debug("letter added");
				}
			else
			{
				log.debug("Previous bet was less than -3 or took expected score to 0 so this letter not added to currentLetters");
			}
		}
	}

	private double getExpectedScoreFromLetters(ArrayList<Character> letters, Letter additionalLetter)
	{
	
		ArrayList<Character> newLetters = new ArrayList<Character>();
		for (Character l : letters) {
			newLetters.add(l);
		}
		
		newLetters.add(additionalLetter.getAlphabet());
		return getExpectedScoreFromLetters(newLetters);
	}
	
	private double getExpectedScoreFromLetters(ArrayList<Character> letters)
	{
		double expectedScore = 0.0;
		for(int i = 0; i < wordlist.length; i++)
		{
			//log.debug("Current word: " + wordlist[i].word + "\n");
			char[] wordCopy = new char[wordlist[i].word.length()];
			for(int p = 0; p < wordlist[i].word.length(); p++)
			{
				wordCopy[p] = wordlist[i].word.charAt(p);
			}
			boolean wordHasAllLetters = true;
			for(int j = 0; j < letters.size(); j++)
			{
				boolean hasLetter = false;
				for(int w = 0; w < wordlist[i].word.length(); w++)
				{
					if(wordCopy[w] == letters.get(j) && hasLetter == false)
					{
						hasLetter = true;
						//log.debug("found " + wordCopy[w] + " at index " + w);
						wordCopy[w] = '_'; //get rid of that letter because it's already been seen
						//log.debug("the wordCopy char array is now: " + wordCopy);
					}
				}
				if(hasLetter == false)
				{
					wordHasAllLetters = false;
				}
			}
			if(wordHasAllLetters == true) //the current word in the dictionary has all of the letters in our rack. 
			{
				double expectedScoreFromThisWord = getExpectedScoreFromWord(wordlist[i], wordCopy);
				expectedScore = expectedScore + expectedScoreFromThisWord;
			}
			else
			{
				//log.debug("the word " + wordlist[i].word + " cannot be formed from your letters");
			}
		}
		return expectedScore;
	}
	
	private double getExpectedScoreFromWord(Word wordObj, char[] wordCopy)
	{
		String word = wordObj.word;
		double prob = 1.0;
		int numLettersNeeded = 0;
		for(int i = 0; i < word.length(); i++)
		{
			if(word.charAt(i) == wordCopy[i])
			{
				int num = (Integer)freq.get(word.charAt(i));
				prob = prob * (ScrabbleValues.getLetterFrequency(word.charAt(i)) - num)/(98.0 - numBidsInThisRound); //change this later to incorporate letters that you already know are on the table.
				numLettersNeeded++;
			}
		}
		
		prob = prob * factorial(numLettersNeeded);
		//log.debug("      probability of getting this word is" + prob);
		//log.debug("the raw score of this word is" + ScrabbleValues.getWordScore(wordObj.word));
		//log.debug("the amount you are likely to bet to get this word is" + 5*numLettersNeeded);
		double expectedScore = prob * (ScrabbleValues.getWordScore(wordObj.word) - 5*numLettersNeeded); //the " - 5*numLettersNeeded" is used to account for how many points you will lose in bidding for these letters.  It is assumed that you will lose about 5 per letter, but look into this more to see what a better average is (look at how much you end up paying on average). Maybe this value will be different depending on the letter (do you always end up betting more for a's then for z's?)
		//log.debug("      the expected score is: " + expectedScore);
		
		return expectedScore; 
	}

	
	//check that this is working 
	private int factorial(int x)
	{
		int answer = 1;
		for(int i = 1; i <= x; i++)
		{
			answer = answer * i;
		}
		return answer;
	}

	public void Register() {
		// no-op
	}



	public String returnWord() {
		//word or getword()?
		//remove all log.debugs at the end
		
		checkBid(cachedBids.get(cachedBids.size() - 1));
		char[] c= new char[currentLetters.size()];
    	for(int i=0; i<currentLetters.size();i++){
    		 c[i]= currentLetters.get(i);
    	}

    	String s = String.valueOf(c);
    	Word open= new Word(s);
    	log.info("Current Letters are: [" + s + "]");

    	int bestscore = 0;
    	String bestword = "";
    	for (Word candidate : wordlist) {
    		if(open.issubsetof(candidate)){
    			log.debug("word that matches: " + candidate.getWord());
    			if (candidate.score > bestscore) {
    				bestscore = candidate.score;
    				bestword = candidate.getWord();
    				log.trace("New best word: " + bestword + " (" + bestscore + ")");
    			}
    		}
    	}

    	log.debug(bestword);
    	//score += bestscore;
        // tell "bid" that we are about to begin a new round
    	//first = true;

    	return bestword;
    }


	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
	
}
