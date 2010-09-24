package seven.f10.g1.players;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.log4j.Logger;

import seven.f10.g1.base.GameStatus;
import seven.f10.g1.base.Rack;
import seven.f10.g1.base.Strategy;
import seven.f10.g1.base.WordList;
import seven.f10.g1.miner.Trie;
import seven.ui.Letter;

public class NaiveStrategy extends Strategy {
	private static final Logger logger = Logger.getLogger(NaiveStrategy.class);
	private static String[] bigrams = new String[] { "TH", "HE", "IN", "ER",
			"AN", "RE", "ND", "AT", "ON", "NT", "HA", "ES", "ST", "EN", "ED",
			"TO", "IT", "OU", "EA", "HI", "IS", "OR", "TI", "AS", "TE", "ET",
			"NG", "OF", "AL", "DE", "SE", "LE", "SA", "SI", "AR", "VE", "RA",
			"LD", "UR" };

	private static String[] trigrams = new String[] {

	"AND", "THA", "ENT", "ING", "ION", "TIO", "FOR", "NDE", "HAS", "NCE",
			"EDT", "TIS", "OFT", "STH", "MEN"

	};
	private Trie myTrie;

	public NaiveStrategy(Rack myRack, GameStatus gameStatus, WordList myList) {
		super(myRack, gameStatus, myList);
		myTrie = new Trie();
		myTrie.loadWords(trigrams);
		myTrie.loadWords(bigrams);

	}

	private boolean makesNew2Gram(char letter) {
		char[] racklet;
		for (Letter l : myRack) {
			racklet = new char[] { l.getAlphabet(), letter };
			Arrays.sort(racklet);
			if (myTrie.stringExist(new String(racklet)) >= 0)
				return true;
		}
		return false;
	}

	private boolean makesNew3Gram(char letter) {
		char[] racklet;
		for (int i = 0; i < myRack.size(); i++) {
			for (int j = 0; i != j && i < myRack.size(); j++) {
				racklet = new char[] { myRack.get(i).getAlphabet(),
						myRack.get(j).getAlphabet(), letter };
				Arrays.sort(racklet);
				if (myTrie.stringExist(new String(racklet)) >= 0)
					return true;
			}
		}
		return false;
	}

	int[] frequency = new int[26];

	@Override
	public int getBidAmmount(char letter) {
		updateFrequency();

		int rackSize = myRack.size();
		int[] bids = { 6, 5, 4 };

		/*
		 * if(rackSize <= 2){ for(int i = 0; i < bids.length; i++){ bids[i] =
		 * bids[i]*2; logger.debug("first 2 letters, increasing bid"); } }
		 */
		if (rackSize > 7) {
			for (int i = 0; i < bids.length; i++) {
				bids[i] = bids[2 - i] / 2;

			}
			logger.debug("7 letters, inversing and decreasing bid");
		}

		if (getFrequency(letter) > 2) {

			for (int i = 0; i < bids.length; i++) {

				bids[i] = bids[i] / 3;
			}
			logger.debug("large letter frequency, decreasing bid");

		} else {

			if (makesNew3Gram(letter) == true) {
				for (int i = 0; i < bids.length; i++) {

					bids[i] = bids[i] * 3;

				}
				logger.debug("bigram, increasing bid");

			} else

			if (makesNew2Gram(letter) == true) {
				for (int i = 0; i < bids.length; i++) {
					bids[i] = (int) (bids[i] * 2);

				}
				logger.debug("bigram, increasing bid");

			}
		}

		if (letter == 'S') {
			for (int i = 0; i < bids.length; i++) {

				bids[i] = (int) (bids[i] * 1.5);
				logger.debug("Letter S, increasing bid");
			}
		} else

		if (letter == 'U') {
			for (int i = 0; i < bids.length; i++) {

				bids[i] = bids[i] / 2;
				logger.debug("Letter U, decreasing bid");
			}
		}

		letter = Character.toUpperCase(letter);
		switch (letter) {
		case 'S':
		case 'T':
		case 'A':
		case 'R':
		case 'L':
		case 'I':
		case 'N':
		case 'E':
			return bids[0];
		case 'H':
		case 'O':
		case 'D':
			return bids[1];
		default:
			return bids[2];
		}
	}

	private int getFrequency(char a) {
		return frequency[a - 'A'];
	}

	private void updateFrequency() {
		for (int i = 0; i < frequency.length; i++)
			frequency[i] = 0;
		for (Letter l : myRack) {
			frequency[l.getAlphabet() - 'A']++;
		}
	}

	@Override
	public void nextRound() {
		// TODO Auto-generated method stub

	}

}
