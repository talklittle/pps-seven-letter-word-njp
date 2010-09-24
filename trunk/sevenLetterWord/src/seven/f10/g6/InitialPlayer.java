package seven.f10.g6;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

import seven.f10.g6.datamining.DataMine;
import seven.f10.g6.datamining.LetterMine;
import seven.f10.g6.datamining.DataMine.ItemSet;
import seven.f10.g6.datamining.LetterMine.LetterSet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class InitialPlayer implements Player {
	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.ERROR);
	}
	protected Logger l = Logger.getLogger(this.getClass());
	static final Word[] wordlist;
	static final Word[] sevenletterwordlist;
	BidBuilder Bid = new BidBuilder();
	// my comment!
	//more
	//DataMine mine = null;
	static {
		
		BufferedReader r;
		String line = null;
		ArrayList<Word> wtmp = new ArrayList<Word>(55000);
		ArrayList<Word> temp2 = new ArrayList<Word>(27000);
		try {
			r = new BufferedReader(new FileReader("src/textFiles/super-small-wordlist.txt"));
			while (null != (line = r.readLine())) {
				wtmp.add(new Word(line.trim()));
			}
			r = new BufferedReader(new FileReader("src/textFiles/7letterWords.txt"));
			while (null != (line = r.readLine())) {
				temp2.add(new Word(line.trim()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordlist = wtmp.toArray(new Word[wtmp.size()]);
		sevenletterwordlist = temp2.toArray(new Word[temp2.size()]);
		
		
	}

	ArrayList<Character> currentLetters;
	private int ourID;
	private ArrayList<PlayerBids> cachedBids;
	private int currentPoint = 100;

	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
		}
		
		if (null == currentLetters) {
			currentLetters = new ArrayList<Character>(25);
			ourID = PlayerID;
			for (Letter l : secretstate.getSecretLetters()) {
				currentLetters.add(l.getAlphabet());
			}
			Bid.setHidden(currentLetters, wordlist);
		} else {
			if (cachedBids.size() > 0) {
				checkBid(cachedBids.get(cachedBids.size() - 1));
			}
		}
		
		/*
		 * Get statistics for bidding price
		 */
		//IGNORED!!
		//call a priori on current letters
		/*
		String[] letlist = new String[currentLetters.size()+1];
		int i = 0;
		int maxscore = 0;
		int numwords = 0;
		for(Character c: currentLetters)
		{
			letlist[i] = c.toString();
			i++;
		}
		letlist[letlist.length -1] = bidLetter.getAlphabet().toString();
		LetterSet ls = (LetterSet) mine.getCachedItemSet(letlist);
		if(null != ls)
		{
			String[] words = ls.getWords();
			l.error("Itemset ["+ls.getKey()+"] has "+ words.length + "associated words:\n");
			numwords = words.length;
			for (String w : words) {
				//System.out.println(w);
				//l.error(w);
				Word wd = new Word(w);
				if(wd.score > maxscore)
					maxscore = wd.score;
			}
		} 
		else 
		{
			l.error("nothing found");
		}
		/*
		//commit
		//current word possible
		char c[] = new char[currentLetters.size()];
		for (int j = 0; j < c.length; j++) {
			c[j] = currentLetters.get(j);
		}
		String s = new String(c);
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		for (Word w : wordlist) {
			if (ourletters.contains(w)) {
				if (w.score > bestword.score) {
					bestword = w;
				}
			}
		}
		int maxcur = bestword.score;
		
		l.error("our stats:");
		l.error("current word value: " + maxcur);
		l.error("possible words max: " + maxscore);
		l.error("# 7 letter words: " + numwords); 
		/*
		 * End statistics calculation.
		 */
		//Ignore below
		/*
		int currentBid = 0;
		if(currentPoint == 0)
			currentBid = 0;
		else
			currentBid = currentPoint - (maxscore/currentPoint);
		l.error("Bid: " + currentBid);
		*/
		return Bid.bid(bidLetter, currentLetters, wordlist, sevenletterwordlist, cachedBids,currentPoint,ourID);
	}

	private void checkBid(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			currentLetters.add(b.getTargetLetter().getAlphabet());
			//deduct point from bidding. Amount = second highest point
			//find amount of second highest bid.
			int secondBid = 0;
			Bid.wonletter();
			for(int i = 0;i<b.getBidvalues().size();i++){
				if(secondBid<b.getBidvalues().get(i)){
					secondBid = b.getBidvalues().get(i);
				}
			}
			//deduct point
			currentPoint = currentPoint-secondBid;
		}
	}

	public void Register() {

	//	l.error("Success");
	//	mine = new LetterMine("src/seven/g4/datamining/7letterWords.txt");
	//	mine.buildIndex();
	//	ItemSet[] answer = mine.aPriori(0.000001);
		/*LetterSet i = (LetterSet) mine.getCachedItemSet(new String[]{"A","C","D","L"});
		//System.out.println("alive and well: " + answer.length + " itemsets total");
		l.error("done.");
		if (null != i) {
			String[] words = i.getWords();
			System.out.format(
					"Itemset [%s] has %d associated words:\n",
					new Object[]{i.getKey(), words.length}
			);
			l.error("Itemset ["+i.getKey()+"] has "+ words.length + "associated words:\n");
			for (String w : words) {
				//System.out.println(w);
				l.error(w);
			}
		} else {
			l.error("nothing found?");
			System.out.println("None, sorry.");
		}*/
	}

	public String returnWord() {
		checkBid(cachedBids.get(cachedBids.size() - 1));
		char c[] = new char[currentLetters.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = currentLetters.get(i);
		}
		String s = new String(c);
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		for (Word w : wordlist) {
			if (ourletters.contains(w)) {
				if (w.score > bestword.score) {
					bestword = w;
				}
			}
		}
		currentLetters = null;
		Bid.reset();
		currentPoint += bestword.score;
		return bestword.word;
	}

}

