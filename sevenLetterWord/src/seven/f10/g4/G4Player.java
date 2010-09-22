package seven.f10.g4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class G4Player implements Player {
	private static ArrayList<Word> dictionary;
	private ArrayList<Letter> rack = new ArrayList<Letter>();
	private SevenLetterWordHelper sevenLetterWordHelper = new SevenLetterWordHelper();
	private ArrayList<PlayerBids> history;
	private static ArrayList<Word> allSevenLetterWords;
	private ArrayList<Word> possibleSevenLetterWords = new ArrayList<Word>();
	private Integer points = 100;
	private Word wordInRack = new Word("");
	private int id;
	private Bidder bidder = new Bidder();
	private Status gameStatus = new Status();

	private Integer toBeRemoved = 0;
	static {
		dictionary = new ArrayList<Word>();
		allSevenLetterWords = new ArrayList<Word>();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"src/seven/g1/super-small-wordlist.txt"));
			try {
				while ((line = reader.readLine()) != null) {
					String s = line.trim();
					Word w = new Word(s);
					dictionary.add(w);
					if (s.length() == 7) {
						allSevenLetterWords.add(w);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void Register() {
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretState, int PlayerID) {
		history = PlayerBidList;
		//System.err.println("points "+points );
		if (rack.isEmpty()) { // First Bid is about to happen
			rack.addAll(secretState.getSecretLetters());
			id = PlayerID;
			wordInRack = createWordFromLettersOnRack(rack);
			sevenLetterWordHelper.setSevenLetterDictionary(allSevenLetterWords);
			possibleSevenLetterWords = sevenLetterWordHelper
					.getSevenLetterWords(wordInRack);
			bidder.setNumberOfSevenLetterWords(possibleSevenLetterWords.size());
			bidder.setFrequencyMap(sevenLetterWordHelper.getFrequencyMap());
			if (history.size() > 0) {
				checkIfWeWon(history.get(history.size() - 1));
				
			}
			
			return bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
			
		} else {
			if(history.size()>0) checkIfWeWon(history.get(history.size() - 1));
			if (wordInRack.getLength() >= 7) {
				if (sevenLetterWordExists(wordInRack)) {
					System.err.println("found seven letter word");
					return 1; // I already have a seven letter word and bid low.
				} 
				int possiblePoints = sevenLetterWordPossible(bidLetter);
				if (possiblePoints > 0) {
					Word word=new Word(getBestWord());
					return bidder.getCompletingBid(possiblePoints,word.getPoints() );
					//bid high on this one.
				}
			}
			return bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
		}
	}

	private void checkIfWeWon(PlayerBids lastBid) {
		gameStatus.updateTurnAndGame(lastBid);
		if (id == lastBid.getWinnerID()) { // Yay we won the bid.
			points-=lastBid.getWinAmmount();
			rack.add(lastBid.getTargetLetter());
			wordInRack = createWordFromLettersOnRack(rack);
			possibleSevenLetterWords = sevenLetterWordHelper
					.getSevenLetterWords(wordInRack);
			bidder.setNumberOfSevenLetterWords(possibleSevenLetterWords.size());
		}
	}

	@Override
	public String returnWord() {
		checkIfWeWon(history.get(history.size()-1));
		return getBestWord();
	}

	private String getBestWord() {
		String bestString = "";
		boolean foundSevenLetterWord = false;
		ArrayList<Word> sevenLetterWordsSeen = new ArrayList<Word>();
		int maxScore = -1;
		for (Word dictionaryWord : dictionary) {
			if (wordInRack.isInDictionary(dictionaryWord)) {
				if (dictionaryWord.getLength() == 7) {
					foundSevenLetterWord = true;
					sevenLetterWordsSeen.add(dictionaryWord);
				}
				if (dictionaryWord.getPoints() > maxScore) {
					bestString = dictionaryWord.getWord();
					maxScore = dictionaryWord.getPoints();
				}
			}
		}
		if (!foundSevenLetterWord)
			return bestString;
		else
			return getBestSevenLetterWord(sevenLetterWordsSeen);
	}

	private Word createWordFromLettersOnRack(ArrayList<Letter> rack) {
		Word wordInRack;
		String s = "";
		for (Letter l : rack) {
			s += l.getAlphabet();
		}
		wordInRack = new Word(s);
		return wordInRack;
	}

	/*
	 * In the unlikely event we end up with more than 1 seven letter word.
	 * Choose the best.
	 */
	private String getBestSevenLetterWord(ArrayList<Word> sevenLetterWords) {
		int max = -1;
		String bestSevenLetterWord = "";
		for (Word word : sevenLetterWords) {
			if (word.getPoints() > max) {
				max = word.getPoints();
				bestSevenLetterWord = word.getWord();
			}
		}
		return bestSevenLetterWord;
	}

	private boolean sevenLetterWordExists(Word rackWord) {
		for (Word dictWord : dictionary) {
			if(dictWord.getLength()==7){
				if (wordInRack.isInDictionary(dictWord)) {
					System.err.println("seven letter word is "+wordInRack.getWord()+" "+dictWord.getWord());
					return true;
				}
			}
		}
		return false;
	}
	
	private int sevenLetterWordPossible(Letter addedLetter) {
		ArrayList<Letter> modifiedRack = new ArrayList(rack);
		modifiedRack.add(addedLetter);
		Word newWord = createWordFromLettersOnRack(modifiedRack);
		if(sevenLetterWordExists(newWord)) {
			System.err.println("Completing Bid : " + newWord + newWord.getPoints());
			return newWord.getPoints() + 50;
		}
		return 0;
	}
}
