package seven.f10.g3;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;
import seven.f10.g3.DataMine.ItemSet;
import seven.f10.g3.LetterMine.LetterSet;
import seven.ui.*;

/**
 * @author David, Elba, and Lauren
 * 
 */
public class OurPlayer implements Player {
	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
	// Variables
	private Rack currentRack;
	private ArrayList<PlayerBids> cachedBids;
	public int ourID;
	static private TrieTree<String> t;
	private ArrayList<String> combination_list_short;
	private ArrayList<String> combination_list_long;
	protected Logger l = Logger.getLogger(this.getClass());
	private String highWord = "";
	private int highWordAmt = 0;
	private static DataMine mine;
	private History h;
	private int amountBidOnRound = 0;
	
	// To keep track of rounds played and number of
	// players we're playing against. 
	int numberOfRoundsPlayed;
	int numberOfPlayers = 0;
	
	// For use to keep track of market value of letters
	private int[] bidTimes = new int[26];
	private int[] bidSums = new int[26];

	// Letter Frequency Array, as given by Scrabble rules
	private int[] letterFrequency = { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2,
			6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1 };

	static {

		String filename = "src/seven/f10/g3/alpha-smallwordlist.txt";
		String line = "";
		t = new TrieTree<String>();

		// Initialize Trie
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				line = line.toUpperCase();
				String[] l = line.split(", ");
				t.insert(l[0], l[1]);
			}

			mine = null;
			mine = new LetterMine("src/seven/f10/g3/smallwordlist.txt");
			mine.buildIndex();
			ItemSet[] answer = mine.aPriori(0.000001);
			System.out.println("alive and well: " + answer.length
					+ " itemsets total");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/** When our player loads */
	public void Register() {
		l.trace("calling register");
		combination_list_short = new ArrayList<String>();
		combination_list_long = new ArrayList<String>();
		h = new History();

		// Instantiate the market value arrays
		for (int i = 0; i < 26; i++) {
			bidTimes[i] = 0;
			bidSums[i] = 0;
		}
		
		// Keep count of how many rounds we've played.
		// There seems to be no way to get the number 
		// people we're playing against so this is
		// kind of a hack, and messy. 
		numberOfRoundsPlayed = 0;
	}

	/** Player Bids */
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {

		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
		}
		if (null == currentRack) {
			setupRack(PlayerID, secretstate);
		} else {
			if (cachedBids.size() > 0) {
				checkBid(cachedBids.get(cachedBids.size() - 1));
			}
		}

		// Generate Bids
		String bidStrategy = "L";
		if (sevenLetterWordLeft() == true)
			bidStrategy = comparisonBid(bidLetter);
		else
			bidStrategy = defaultBid(bidLetter);

		// Adjusted Bid
		int adjustedBid = h.adjust(bidStrategy, bidLetter, cachedBids,
				ourID);
		l.trace("So far bid: " + amountBidOnRound);
		if(adjustedBid > 15){ //Make sure that we are not bidding too much
			l.trace("Reduced bid!");
			adjustedBid  = 15;
		}
		return adjustedBid;
	}

	/**
	 * Main bidding strategy - we find the number of possibilities with our
	 * current rack and compare that to the number of possibilities with the bid
	 * letter. If that is over a certain threshold we bid high
	 */
	public String comparisonBid(Letter bidLetter) {

		int b = 0;

		// Set up char array to use as our temporary rack
		char[] rack = new char[currentRack.size() + 1];
		for (int i = 0; i < currentRack.size(); i++)
			rack[i] = currentRack.get(i).getL();

		// Create two lists
		int[] sortedAmounts = new int[26];

		for (char c = 'A'; c <= 'Z'; c++) {
			if (letterPossiblyLeft(c)) {
				rack[rack.length - 1] = c;
				int pos = numberOfPossibilities(rack);
				sortedAmounts[c - 65] = pos;
				if (c == bidLetter.getAlphabet()) {
					b = pos;
				}
			}
		}
		
		//Determine high, medium, or low bid
		Arrays.sort(sortedAmounts);
		if (b > sortedAmounts[5])
			return ("H");
		else if (b > sortedAmounts[10])
			return ("M");
		else
			return ("L");
	}

	/**
	 * A function to quickly get market value as calculated by previous bid
	 * wins.
	 */
	public int marketValue(char Letter) {
		int letterPlace = Letter - 'A';
		return bidSums[letterPlace] / bidTimes[letterPlace]; // return winning
	}

	/** 
	 * A function to be able to print the marketValue of all letters.
	 * To be used for information tracking. 
	 */
	public void printMarketValues() {
		for (int i = 0; i < 26; i++)
		{
			char temp = (char) ('A' + i);
			l.trace("Letter: " + temp + ", Value: " + bidSums[i]
					/ bidTimes[i]);
		}
	}

	public int numberOfPossibilities(char[] arr) {

		int count = 0;
		String[] strarr = new String[arr.length];
		for (int i = 0; i < arr.length; i++) {
			strarr[i] = Character.toString(arr[i]);
		}

		String[] args = strarr;
		LetterSet i = (LetterSet) mine.getCachedItemSet(args);

		if (null != i) {
			String[] words = i.getWords();
			count = words.length;
		}

		return (count);
	}

	public String defaultBid(Letter bidLetter) {
		String bid = "L";
		char[] c = new char[currentRack.wantSize() + 2];
		for (int i = 0; i < currentRack.wantSize(); i++)
			if (currentRack.get(i).getWant() == true)
				c[i] = currentRack.get(i).getL();
		c[currentRack.wantSize()] = bidLetter.getAlphabet();
		Arrays.sort(c);

		TrieNode<String> node = t.returnAutoNode(new String(c));

		if (node != null && node.isWord() == true) {
			bid = "H";

		} else {
			for (char ch = 'A'; ch <= 'Z'; ch++) {
				c[0] = ch;
				Arrays.sort(c);
				node = t.returnAutoNode(new String(c));
				if (node != null && node.isWord() == true
						&& getWordAmount(node.returnWord()) > highWordAmt) {
					bid = "H";
					ch = 'Z';
				}
			}
		}

		if (bid == "L")// If we still have not decided we need it, then we
		// search every possible combination
		{
			String temp = getHighWord();
			if (getWordAmount(temp) > highWordAmt)
				bid = "H";
		}

		return (bid);
	}

	/** Set up rack at beginning of game - includes adding hidden letters */
	public void setupRack(int PlayerID, SecretState secretstate) {

		currentRack = new Rack();
		ourID = PlayerID;
		for (Letter l : secretstate.getSecretLetters()) {
			currentRack.add(new RackLetter(l.getAlphabet(), false));
		}
		setHighs();

	}

	/** Check to see if we win the bid, if so add it to your rack */
	private void checkBid(PlayerBids b) {
		Boolean want = false;
		numberOfRoundsPlayed++; 		// We've played a round
		
		// This will run every time we play, and its value
		// should not change.
		// It's bad design but it works. 
		numberOfPlayers = b.getBidvalues().size();
		
		// check to see if we actually wanted it
		if (ourID == b.getWinnerID() && b.getWinAmmount() > 0)
			want = true;

		if (ourID == b.getWinnerID()) {
			currentRack.add(new RackLetter(b.getTargetLetter().getAlphabet(),
					want));
			amountBidOnRound += b.getWinAmmount();
			setHighs();
		}

		// get bid info to add to the market value statistics
		// int letterPlace = b.getTargetLetter().getAlphabet() - 'A'; // get
		// letter place
		// bidTimes[letterPlace]++; // got bid on
		// bidSums[letterPlace]+= b.getWinAmmount(); // add to win amount

		// to print the market values at the end of bidding.
		// printMarketValues();
	}

	/** Reset high word and high score */
	public void setHighs() {

		l.trace("In set highs");
		
		String temp = getHighWord();
		if (temp != null) {
			highWord = temp;
			currentRack.resetWants(highWord);
			highWordAmt = getWordAmount(highWord);
			System.out.println("Just set highs to: " + highWord + ", "
					+ highWordAmt);
		}
	}

	public String getHighWord() {
		
		l.trace("Looking for rack with: " + new String(currentRack.getCharArray()));

		char[] rack = new char[currentRack.size()];
		rack = currentRack.getCharArray();
		Arrays.sort(rack);

		// Look in trie for words
		String temp = new String(rack);
		combinations("", temp);
		if (rack.length > 0) {
			String str = search(combination_list_long);
			if (str.length() > 0)
				return (str.toUpperCase());
			else {
				str = search(combination_list_short);
				if (str.length() > 0)
					return (str.toUpperCase());
			}
		}
		return ("");
	}

	/** Return our final word back to the simulator */
	public String returnWord() {
		//setHighs();
		highWord = getHighWord();
		l.trace("Rack is: " + new String(currentRack.getCharArray()));
		currentRack.clear();
		l.trace("Returning: " + highWord);
		String temp = highWord;
		highWord = new String();
		highWordAmt = 0;
		amountBidOnRound = 0;
		combination_list_long = new ArrayList<String>();
		combination_list_short = new ArrayList<String>();
		return (temp);
	}

	private String search(ArrayList<String> combination_list) {

		String word = "";
		int amount = 0;
		for (int i = 0; i < combination_list.size(); i++) {
			TrieNode<String> node = t.returnAutoNode(combination_list.get(i)
					.toUpperCase());
			if (node != null && node.isWord() == true) {
				int amt = getWordAmount(node.returnWord());
				if (amt > amount) {
					word = node.returnWord();
					amount = amt;
				}

			}
		}
		return (word);
	}

	private void combinations(String prefix, String s) {
		if (s.length() > 0) {
			String str = prefix + s.charAt(0);
			if (str.length() > 3) {
				combination_list_long.add(str);
			} else {
				combination_list_short.add(str);
			}
			combinations(prefix + s.charAt(0), s.substring(1));
			combinations(prefix, s.substring(1));
		}
	}

	/*
	 * private ArrayList<String> sort_by_length(ArrayList<String> old_list) {
	 * 
	 * l.trace("here"); int i = 0; int j = 0; Boolean keepgoing = true; while
	 * (keepgoing == true) { keepgoing = false; for (i = 0; i < old_list.size();
	 * i++) { for (j = 0; j < old_list.size(); j++) { if
	 * (old_list.get(i).length() < old_list.get(j).length()) {
	 * l.trace("before: " + old_list.get(i)); Collections.swap(old_list, i, j);
	 * l.trace("after: " + old_list.get(i)+ "\n"); keepgoing = true; } } } i =
	 * 0; } l.trace("returning"); return old_list; }
	 */

	public int getWordAmount(String word) {

		char[] c = word.toCharArray();
		int amt = 0;
		for (int i = 0; i < c.length; i++) {
			amt += Scrabble.letterScore(c[i]);
		}
		if (c.length == 7)
			amt += 50;

		return (amt);
	}

	
	/**
	 * A simple function to return the number of letters left
	 * to bid on, i.e. number of rounds left.
	 * @return
	 */
	public int numRoundsLeft() {
		// For each player, 8 letters are put in the bag. 
		// So to see how many letters we have left to bid on, 
		// AKA the number of rounds left, we subtract nOP*8 - nORP. 
		return (numberOfPlayers*8) - numberOfRoundsPlayed;
	}
	
	public boolean becomesSevenLetter(char c)
	{	
		// copy rack
		Rack dummyRack = new Rack();
		for(int i = 0; i < currentRack.size(); i++)
			dummyRack.add(currentRack.get(i));
		
		// Adds new letter to rack
		RackLetter l = new RackLetter(c, true);
		dummyRack.add(l);
		
		String str = new String(dummyRack.getCharArray());
		if(t.findWord(str))
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * Lets us know if it is possible to get a seven letter word with the
	 * remaining letters. 
	 */
	public boolean sevenLetterWordLeft() {
				
		/*if (sevenLetterWordLeft == false)
			return(false);*/
		
		boolean sevenLeft = true;
		
		// If the size our rack plus the number of rounds
		// left don't even add up to seven, it's impossible
		// to get a seven letter word. 
		//
		// This is the most basic case.
		if(this.numRoundsLeft() + currentRack.size() < 7)
			sevenLeft = false;
		
		//get seven letter words from apriori
		//make sure letters left in bag
		String[] strarr = new String[currentRack.size()];
		for (int i = 0; i < currentRack.size(); i++) {
			strarr[i] = Character.toString(currentRack.get(i).getL());
		}

		String[] args = strarr;
		LetterSet i = (LetterSet) mine.getCachedItemSet(args);

		if (null != i) {
			String[] words = i.getWords();
			for(int j = 0; j < words.length; j++){
				char[] temp = words[j].toCharArray();
				for(int k = 0; k < temp.length; k++){
					Boolean left = letterPossiblyLeft(temp[k]);
					if(left == false){
						j++;
					}//We could not make this word
				}
				j = words.length;
			}
		}	
		sevenLeft = true;
		return sevenLeft;		
	}

	/**
	 * Returns whether it's even possible that a certain
	 * letter is still in the bag to play on.
	 * 
	 * Depends on scrabble letter frequency.  
	 */
	public boolean letterPossiblyLeft(char Letter) {

		if (bidTimes[Letter - 'A'] == letterFrequency[Letter - 'A'])
			return false;
		else
			return true;
	}

	/** Method only for debugging - artiicially adds to rack */
	public void addToRack(char c) {
		currentRack.add(new RackLetter(c, true));
		System.out.println("Rack is now: "
				+ new String(currentRack.getCharArray()));
	}
}
