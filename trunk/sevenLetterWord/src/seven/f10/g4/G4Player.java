package seven.f10.g4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

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
	private Status gameStatus = new Status(this);
	
	private Logger logger = Logger.getLogger(G4Player.class);

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
	
	public int getId(){
		return id;
	}

	@Override
	public void Register() {
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretState, int PlayerID) {
		history = PlayerBidList;
		if (gameStatus.getGame() == 0 && gameStatus.getTurn() == 0) {
			gameStatus.initOpponents(PlayerList);
		}
		logger.debug("points "+points );
		int score;
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
			score = bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
			
		} 
		else {
			if(history.size()>0) checkIfWeWon(history.get(history.size() - 1));
			if (wordInRack.getLength() >= 6) {
				int possiblePoints = sevenLetterWordPossible(bidLetter);
				if (possiblePoints > 0) {
					logger.debug("We are using Neetha's strategy");
					Word word=new Word(getBestWord());
					score = bidder.getCompletingBid(possiblePoints,word.getPoints() );
					
					//bid high on this one.
				}
				//else if(wordInRack.getLength()>=8){
				//	logger.debug("We went into Flavio's restriction");
				//	score = 1 ; //  stop bidding when we have more than 8 letter and no 7 letter word.
				//}
				else{
					if(wordInRack.getLength()>=7){
						if (sevenLetterWordExists(wordInRack)) {
							logger.debug("found seven letter word");
							score = 1; // I already have a seven letter word and bid low.
						}
					}	
					logger.debug("We are using Nitin standard with more than 6 letters");
					score = bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
				}

			}
			else{
				logger.debug("We are using Nitin standard");
				score = bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
			}
		}
		double stl =   gameStatus.getMaxExpectedBid(bidLetter.getAlphabet());
		int scoreToLose = (int) (0.66 *  stl );
		logger.debug("ScoreToLose "+scoreToLose+" Score "+score + " stl "+stl);
		if(score < scoreToLose) {
			logger.debug("We are bidding to make the others lose! "+scoreToLose+" instead of "+score);
			return scoreToLose;
		}
		return score;

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
		gameStatus.newGame();
		checkIfWeWon(history.get(history.size()-1));
		String returnMe = getBestWord();
		
		// reset the rack
		rack = new ArrayList<Letter>();
		
		return returnMe;
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
					logger.debug("seven letter word is "+wordInRack.getWord()+" "+dictWord.getWord());
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
			logger.debug("Completing Bid : " +newWord.getPoints());
			return newWord.getPoints();
		}
		return 0;
	}
}
