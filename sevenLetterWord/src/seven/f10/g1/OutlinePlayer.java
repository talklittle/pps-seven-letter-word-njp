/**
 * Modified source of seven.g0.StingPlayer
 */
package seven.f10.g1;


import java.util.ArrayList;
import java.io.*;

import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class OutlinePlayer implements Player {

	private static final Logger logger = Logger.getLogger(OutlinePlayer.class);
	static final Word[] wordlist;

	static {
		BufferedReader r;
		String line = null;
		ArrayList<Word> wtmp = new ArrayList<Word>(55000);
		try {
			r = new BufferedReader(new FileReader("src/seven/g1/super-small-wordlist.txt"));
			while (null != (line = r.readLine())) {
				wtmp.add(new Word(line.trim()));
			}
		} catch (FileNotFoundException e) {
			logger.error("Word list file not found!", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Word list file read error!", e);
			e.printStackTrace();
		}
		wordlist = wtmp.toArray(new Word[wtmp.size()]);
	}

	ArrayList<Character> currentLetters;
	private int ourID;
	private ArrayList<PlayerBids> cachedBids;

	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		if (PlayerBidList.isEmpty()) {
			cachedBids = PlayerBidList;
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

		return 0;
	}

	private void checkBid(PlayerBids b) {
		if (ourID == b.getWinnerID()) {
			currentLetters.add(b.getTargetLetter().getAlphabet());
		}
	}

	public void Register() {
		// no-op
	}

	public String returnWord() {
		//Need to put letter in rack if we won that last round
		checkBid(cachedBids.get(cachedBids.size() - 1));
		
		String s = "";
		for (char l : currentLetters) {
			s += l;
		}
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		for (Word w : wordlist) {
			if (ourletters.contains(w)) {
				if (w.getValue() > bestword.getValue()) {
					bestword = w;
				}

			}
		}
		currentLetters = null;
		return bestword.word;
	}

}
