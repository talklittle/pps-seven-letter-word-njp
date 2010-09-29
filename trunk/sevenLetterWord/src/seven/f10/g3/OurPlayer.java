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

	// Variables
	private Rack currentRack;
	private ArrayList<PlayerBids> cachedBids;
	public int ourID;
	static private TrieTree<String> t;
	protected Logger l = Logger.getLogger(this.getClass());
	private String highWord = "";
	private int highWordAmt = 0;
	private static DataMine mine;
	private History h;
	private int amountBidOnRound = 0;
	private ArrayList<String> combos;

	// For use to keep track of market value of letters
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
		combos = new ArrayList<String>();
		h = new History();

		// Instantiate the market value arrays
		for (int i = 0; i < 26; i++) {
			bidSums[i] = 0;
		}

		resetRack();
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
		double bidStrategy;
		if (currentRack.size() >= 7 && haveSevenLetterWord() == true)
			bidStrategy = defaultBid(bidLetter);
		else if (sevenLetterWordLeft() == true)
			bidStrategy = comparisonBid(bidLetter);
		else
			bidStrategy = defaultBid(bidLetter);

		// Adjusted Bid
		int adjustedBid = h.adjust(bidStrategy, bidLetter, cachedBids, ourID);

		// Adjust bid to make sure that it is not extravagant - getting no
		// letters but ending up wiht a score of 100 is better than spending all
		// of our points
		int maxbid = 61;
		if (currentRack.size() < 7) {
			if (adjustedBid > 1.0 * (maxbid - amountBidOnRound)
					/ (7 - currentRack.size())) {
				adjustedBid = (maxbid - amountBidOnRound)
						/ (7 - currentRack.size());
			}
		}

		return adjustedBid;
	}

	/**
	 * Main bidding strategy - we find the number of possibilities with our
	 * current rack and compare that to the number of possibilities with the bid
	 * letter. If that is over a certain threshold we bid high
	 */
	public double comparisonBid(Letter bidLetter) {

		int b = 0;

		// Set up char array to use as our temporary rack
		char[] rack = new char[currentRack.size() + 1];
		for (int i = 0; i < currentRack.size(); i++)
			rack[i] = currentRack.get(i).getL();

		// Create two lists
		int[] sortedAmounts = new int[26];

		for (char c = 'A'; c <= 'Z'; c++) {
			if (h.letterPossiblyLeft(c)) {
				rack[rack.length - 1] = c;
				int pos = numberOfPossibilities(rack);
				sortedAmounts[c - 65] = pos;
				if (c == bidLetter.getAlphabet()) {
					b = pos;
				}
			}
		}
		Arrays.sort(sortedAmounts);
		// Determine high, medium, or low bid
		String r = "";
		for (int i = 0; i < sortedAmounts.length; i++)
			r += sortedAmounts[i] + " ";
		/*l.warn("stred: " + r);
		l.warn("bid: " + b);*/

		if (b == 0)
			return 0;

		int firstNonZero = 0;
		for (; firstNonZero < sortedAmounts.length; firstNonZero++) {
			if (sortedAmounts[firstNonZero] == 0)
				firstNonZero++;
			else
				break;
		}

		int indexb = firstNonZero;
		for (; indexb < sortedAmounts.length; indexb++)
			if (sortedAmounts[indexb] == b)
				break;

		double bidValue = 1.000 * (1 + indexb - firstNonZero)
				/ (sortedAmounts.length - firstNonZero);

		/*l.warn("firstNonZero=" + firstNonZero + " indexb=" + indexb
				+ " bidValue=" + bidValue);*/
		return bidValue;
	}

	/**
	 * A function to quickly get market value as calculated by previous bid
	 * wins.
	 */
	public int marketValue(char Letter) {
		int letterPlace = Letter - 'A';
		return bidSums[letterPlace] / h.getBidTimes(letterPlace); // return
		// winning
	}

	/**
	 * A function to be able to print the marketValue of all letters. To be used
	 * for information tracking.
	 */
	public void printMarketValues() {
		for (int i = 0; i < 26; i++) {
			char temp = (char) ('A' + i);
			l.warn("Letter: " + temp + ", Value: " + bidSums[i]
					/ h.getBidTimes(i));
		}
	}

	public int numberOfPossibilities(char[] arr) {
		int count = 0;
		System.out.println("arr: " + new String(arr));
		if (arr.length <= 5)
			count = useApriori(arr);
		else {// subdivide array
			combos.clear();
			combinations("", new String(arr), 4);
			for (int i = 0; i < combos.size(); i++) {
				count += useApriori(combos.get(i).toCharArray());
			}
		}
		return (count);
	}

	public int useApriori(char[] arr) {

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

	public double defaultBid(Letter bidLetter) {
		double lbid = 3;
		double hbid = 23;
		double bid = lbid;
		char[] c = new char[currentRack.size() + 2];
		for (int i = 0; i < currentRack.size(); i++)
			c[currentRack.size()] = bidLetter.getAlphabet();
		Arrays.sort(c);

		TrieNode<String> node = t.returnAutoNode(new String(c));

		if (node != null && node.isWord() == true) {
			bid = hbid;
		} else {
			for (char ch = 'A'; ch <= 'Z'; ch++) {
				c[0] = ch;
				Arrays.sort(c);
				node = t.returnAutoNode(new String(c));
				if (node != null && node.isWord() == true
						&& getWordAmount(node.returnWord()) > highWordAmt) {
					bid = hbid;
					ch = 'Z';
				}
			}
		}

		if (bid == lbid)// If we still have not decided we need it, then we
		// search every possible combination
		{
			String temp = getHighWord();
			if (getWordAmount(temp) > highWordAmt)
				bid = hbid;
		}

		return (bid / 26);
	}

	/** Set up rack at beginning of game - includes adding hidden letters */
	public void setupRack(int PlayerID, SecretState secretstate) {

		currentRack = new Rack();
		ourID = PlayerID;
		for (Letter l : secretstate.getSecretLetters()) {
			currentRack.add(new RackLetter(l.getAlphabet()));
			h.setNumHidden(h.getNumHidden() + 1);
		}
		setHighs();

	}

	/** Check to see if we win the bid, if so add it to your rack */
	private void checkBid(PlayerBids b) {

		h.setNumberOfRoundsPlayed(h.getNumberOfRoundsPlayed() + 1);
		h.setNumberOfPlayers(b.getBidvalues().size());

		if (ourID == b.getWinnerID()) {
			currentRack.add(new RackLetter(b.getTargetLetter().getAlphabet()));
			amountBidOnRound += b.getWinAmmount();
			setHighs();
		}
	}

	/** Reset high word and high score */
	public void setHighs() {

		String temp = getHighWord();
		if (temp != null) {
			highWord = temp;
			highWordAmt = getWordAmount(highWord);
			System.out.println(highWord + " for " + highWordAmt);
		}
	}

	public String getHighWord() {
		char[] rack = new char[currentRack.size()];
		rack = currentRack.getCharArray();

		l.trace("getHighWord(): size=" + currentRack.size());
		for (int i = 0; i < rack.length; i++)
			l.trace(rack[i]);

		Arrays.sort(rack);

		// Look in trie for words
		String temp = new String(rack);
		combinations("", temp, 0);
		if (rack.length > 0) {
			String str = search(combos);
			if (str.length() > 0)
				return (str.toUpperCase());
			else {
				str = search(combos);
				if (str.length() > 0)
					return (str.toUpperCase());
			}
		}
		return ("");
	}

	/** Return our final word back to the simulator */
	public String returnWord() {
		checkBid(cachedBids.get(cachedBids.size() - 1));
		// setHighs();
		highWord = getHighWord();
		l.trace("Rack is: " + new String(currentRack.getCharArray()));
		currentRack.clear();
		l.trace("Returning: " + highWord);
		String temp = highWord;
		resetRack();
		return (temp);
	}

	/** Called at end of rack to reset our hand */
	private void resetRack() {
		highWord = new String();
		highWordAmt = 0;
		amountBidOnRound = 0;
		combos = new ArrayList<String>();
		h.setNumHidden(0);
		h.setNumberOfRoundsPlayed(0);
		currentRack = null;
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

	private void combinations(String prefix, String s, int min) {
		if (s.length() > 0) {
			String str = prefix + s.charAt(0);
			if (str.length() > min)
				combos.add(str);
			combinations(prefix + s.charAt(0), s.substring(1), min);
			combinations(prefix, s.substring(1), min);
		}
	}

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

	public boolean becomesSevenLetter(char c) {
		// copy rack
		Rack dummyRack = new Rack();
		for (int i = 0; i < currentRack.size(); i++)
			dummyRack.add(currentRack.get(i));

		// Adds new letter to rack
		RackLetter l = new RackLetter(c);
		dummyRack.add(l);

		String str = new String(dummyRack.getCharArray());
		if (t.findWord(str))
			return true;
		else
			return false;

	}

	/**
	 * Lets us know if it is possible to get a seven letter word with the
	 * remaining letters.
	 */
	public boolean sevenLetterWordLeft() {

		boolean sevenLeft = false;

		// If the size our rack plus the number of rounds
		// left don't even add up to seven, it's impossible
		// to get a seven letter word.
		if (h.numTilesLeftToBid() + currentRack.size() > 7) {
			// Convert to string array for apriori
			String[] strarr = new String[currentRack.size()];
			for (int i = 0; i < currentRack.size(); i++) {
				strarr[i] = Character.toString(currentRack.get(i).getL());
			}

			String[] args = strarr;
			LetterSet i = (LetterSet) mine.getCachedItemSet(args);

			if (null != i) {
				String[] words = i.getWords();
				for (int j = 0; j < words.length; j++) {
					char[] temp = words[j].toCharArray();
					for (int k = 0; k < temp.length; k++) {
						Boolean left = h.letterPossiblyLeft(temp[k]);
						if (left == false) {
							j++;
						}// We could not make this word
					}// We made a word
					j = words.length;
					sevenLeft = true;
				}
			}
		}
		return sevenLeft;
	}

	/** Method only for debugging - artiicially adds to rack */
	public void addToRack(char c) {
		currentRack.add(new RackLetter(c));
		System.out.println("Rack is now: "
				+ new String(currentRack.getCharArray()));
	}

	/** Whether or not we have a seven letter word left */
	public boolean haveSevenLetterWord() {
		l.warn("In have seven letter word with: " + new String(currentRack.getCharArray()));
		combos.clear();
		combinations("", new String(currentRack.getCharArray()), 6);
		ArrayList<String> tempcombos = new ArrayList<String>(combos);
		for (int i = 0; i < tempcombos.size(); i++) {
			if(tempcombos.get(i).length() == 7){
			l.warn("combo: " + tempcombos.get(i));
			l.warn("combo: " + new String(tempcombos.get(i).toCharArray()));
			int ret = useApriori(tempcombos.get(i).toCharArray());
			l.warn("combos.get/ret" + tempcombos.get(i) + ", " + ret);
			if (ret >= 1)
				return true;
		}
		}
		return false;
	}

	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub

	}
}