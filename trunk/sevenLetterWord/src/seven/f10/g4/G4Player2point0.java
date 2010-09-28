package seven.f10.g4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class G4Player2point0 implements Player {
	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
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
	
	private Logger logger = Logger.getLogger(G4Player2point0.class);
	
	//ADDED BY FLAVIO
	private static boolean first = true;
	private static String fu = "";
	protected static HashSet<Word>[]  wordSets = new HashSet[Util.alphabet.length];
	protected static int[] inBag = Arrays.copyOf(Util.inBag, Util.inBag.length);
	protected static HashSet<Word>  allSevenSet = new HashSet<Word>();
	
	static {
		dictionary = new ArrayList<Word>();
		allSevenLetterWords = new ArrayList<Word>();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"src/seven/g1/super-small-wordlist.txt"));
			try {
				for(int i = 0; i < wordSets.length; i++) {
					wordSets[i] = new HashSet<Word>();
				}
				while ((line = reader.readLine()) != null) {
					String s = line.trim();
					Word w = new Word(s);
					dictionary.add(w);
					if (s.length() == 7) {
						allSevenLetterWords.add(w);
						allSevenSet.add(w);
						for (int j = 0; j < w.getLength(); j++) {
							Character c = w.charAt(j);
							wordSets[Util.getIndexFromChar(c)].add(w);
						}
					}
				}
				
				for(int i = 0; i < 10; i++) {
					//System.err.println(Util.alphabet[i]+" "+wordSets[i]);
					double score = getExpectedScore(allSevenSet, Util.getCharFromIndex(i));
					System.err.println(Util.alphabet[i]+" "+score+", "+(score/(double)allSevenSet.size()));
				}
				double fullscore = getExpectedScore(allSevenSet, null);
				System.err.println("full score: "+(fullscore)+"avg score: "+(fullscore/(double)allSevenSet.size())+ "\n fu: "+fu);
				
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
			gameStatus.setNumHiddenLetters(secretState.getSecretLetters().size());
			id = PlayerID;
			wordInRack = createWordFromLettersOnRack(rack);
			sevenLetterWordHelper.setSevenLetterDictionary(allSevenLetterWords);
			possibleSevenLetterWords = sevenLetterWordHelper
					.getSevenLetterWords(wordInRack, gameStatus);
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
				int possiblePoints = possibleSevenLetterScore(bidLetter);
				if (possiblePoints > 0) {
					logger.debug("We are using Neetha's strategy");
					Word word=getBestWord(wordInRack);
					score = bidder.getCompletingBid(possiblePoints,word.getPoints() );
					logger.debug("Possible points" + possiblePoints + "current points : " + word.getPoints() + "current word " + word.getWord() + "Thus bid amount is " + score);
				}
				else{
					if(wordInRack.getLength()>=7 && findSevenLetterWord(wordInRack) != null) {
						logger.debug("found seven letter word");
						score = 1; // I already have a seven letter word and bid low.
					} else {
						logger.debug("We are using Nitin standard with more than 6 letters");
						logger.debug("current rack : " + wordInRack.getWord() + "current Points" + getBestWord(wordInRack).getPoints());
						score = bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
					}
				}
			}
			else{
				logger.debug("We are using Nitin standard");
				score = bidder.getBidAmount(gameStatus, bidLetter.getAlphabet(), gameStatus.opponentSpend(id), rack.size());
			}
		}
		double stl =   gameStatus.getMaxExpectedBid(bidLetter.getAlphabet());
		int scoreToLose = (int) (0.67 *  stl ); //we bid 2/3 of the maximum expected bid, we could bid stl -1 to be very aggressive, but it is more risky
		System.err.println("ScoreToLose "+scoreToLose+" Score "+score + " stl "+stl);
		if(score < scoreToLose) {
			System.err.println("We are bidding to make the others lose! "+scoreToLose+" instead of "+score);
			return Math.max(1,scoreToLose);
		}
		return Math.max(1,score);

	}

	private void checkIfWeWon(PlayerBids lastBid) {
		gameStatus.updateTurnAndGame(lastBid);
		if (id == lastBid.getWinnerID()) { // Yay we won the bid.
			points-=lastBid.getWinAmmount();
			rack.add(lastBid.getTargetLetter());
			wordInRack = createWordFromLettersOnRack(rack);
			possibleSevenLetterWords = sevenLetterWordHelper
					.getSevenLetterWords(wordInRack, gameStatus);
			bidder.setNumberOfSevenLetterWords(possibleSevenLetterWords.size());
			setCurrentRack();
		}
	}

	@Override
	public String returnWord() {
		gameStatus.newGame();
		checkIfWeWon(history.get(history.size()-1));
		Word returnMe = getBestWord(wordInRack);
		
		// reset the rack
		rack = new ArrayList<Letter>();
		
		return returnMe.getWord();
	}

	private Word getBestWord(Word currentRack) {
		String bestString = "";
		boolean foundSevenLetterWord = false;
		ArrayList<Word> sevenLetterWordsSeen = new ArrayList<Word>();
		int maxScore = -1;
		for (Word dictionaryWord : dictionary) {
			if (currentRack.isInDictionary(dictionaryWord)) {
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
			return new Word(bestString);
		else
			return new Word(getBestSevenLetterWord(sevenLetterWordsSeen));
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

	private Word findSevenLetterWord(Word rackWord) {
		for (Word dictWord : dictionary) {
			if(dictWord.getLength()==7){
				if (rackWord.isInDictionary(dictWord)) {
					logger.debug("rack is "+rackWord.getWord()+" possible dictionary Word "+dictWord.getWord());
					return dictWord;
				}
			}
		}
		return null;
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
	
	private int possibleSevenLetterScore(Letter addedLetter) {
		logger.debug("The letter for bid is " + addedLetter.getAlphabet());
		Word newWord = new Word(wordInRack.getWord().concat(addedLetter.getAlphabet().toString()));
		logger.debug("the rack we will have if we take this letter" + newWord.getWord());
		Word sevenLetterWord = findSevenLetterWord(newWord);
		if(sevenLetterWord != null) {
			logger.debug("original word " + wordInRack.getWord() +  " new Word " + newWord.getWord() + " 7 letter word " + sevenLetterWord.getWord());
			logger.debug("Completing Bid : " + sevenLetterWord.getPoints() + " word : " + sevenLetterWord.getWord());
			return sevenLetterWord.getPoints();
		}
		return 0;
	}
	
	public void setCurrentRack() {
		Util.setWordInRack(wordInRack);
	}
	
	public static double getExpectedScore(HashSet<Word> set, Character cnew) {
		double result = 0;
		double weightTotal = 0;
		Iterator<Word> it = set.iterator();
		while(it.hasNext()) {
			Word w = it.next();
			HashSet<Character> done = new HashSet<Character>();
			double wordScore = w.getPoints();
			double activePlayers = 12;
			double weightWord = 0;
			double neededLength = 7;
			for(int j = 0; j < w.getLength(); j++) {
				Character c = w.charAt(j);
				if(!done.contains(c)) {
					int needed = w.getFrequency(c);
					double weight = 0;
					if( (cnew != null && c.toString().equals(cnew.toString()))) {
						weight += 1;
						needed -= 1;
						neededLength -=1;
					}
					double numExtracted = (98/(double)98) * inBag[Util.getIndexFromChar(c)]; 
					weight +=  Math.min(needed, (numExtracted/activePlayers)) ;
					if(first){
						fu +=c+", freq: "+w.getFrequency(c)+", inBag: "+inBag[Util.getIndexFromChar(c)] +", numEx: "+numExtracted +", weightLet: "+weight+", needed: "+needed+"\n";
					}
					weightWord += weight;
					done.add(c);
				}
			}
			if (first) {
				fu += w.getWord()+", WS: "+wordScore+", WW: "+weightWord+", "+", WWN: "+(weightWord/(double)7)+", "+((weightWord/(double)7)*wordScore);
				System.err.println(fu);
				first = false;
			}
			weightTotal += weightWord/(double)7;
			result += ((weightWord/(double)7) * wordScore);
		}
		System.err.println("result before avg: "+result);
		return result/weightTotal;
	}
	
}
