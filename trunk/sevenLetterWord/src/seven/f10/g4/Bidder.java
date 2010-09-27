package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Bidder {
	private static final String LOW = "LOW";
	private static final String MID = "MID";
	private static final String HIGH = "HIGH";
	private static final float HIGH_BID = 0.90f;
	private static final float MID_BID = 0.60f;
	private static final float LOW_BID = 0.40f;
	private static final int ASSUMED_WORD_SCORE = 50;
	
	private SevenLetterWordHelper sevenLetterWordHelper;
	private int numberOfSevenLetterWords;
	private HashMap<String, ArrayList<Character>> bidLevels;
	private HashMap<Character,Integer> frequencyMap;
	
	private Logger logger = Logger.getLogger(G4Player.class);
	
	public void setBidLevels() {
		bidLevels=new HashMap<String, ArrayList<Character>>();
		sevenLetterWordHelper=new SevenLetterWordHelper(); 
		ArrayList<Character> highBids=new ArrayList<Character>();
		ArrayList<Character> midBids=new ArrayList<Character>();
		ArrayList<Character> lowBids=new ArrayList<Character>();
		for(Character c: frequencyMap.keySet()){
			
			
			if(2*frequencyMap.get(c)>=numberOfSevenLetterWords){
				highBids.add(c);
			}
			else if(2*frequencyMap.get(c)<numberOfSevenLetterWords && 4*frequencyMap.get(c)>=numberOfSevenLetterWords){
				midBids.add(c);
			}
			else{
				lowBids.add(c);
			}
		}
		bidLevels.put(HIGH, highBids);
		bidLevels.put(MID, midBids);
		bidLevels.put(LOW, lowBids);
	}

	
	public int getCompletingBid(int possiblePoints,int currentPoints) {
		return possiblePoints - currentPoints ;
	}
	
	public int getBidAmount(Status gameStatus, Character targetCharacter, int spentSoFar, int rackSize) {
		int scoreToWin = (int) (getBidBase(gameStatus, targetCharacter, spentSoFar, rackSize) * getBidMultiplier(targetCharacter));
		return scoreToWin;
	}
	
	private int getBidBase(Status gameStatus, Character targetCharacter, int spentSoFar, int rackSize) {
		int remainingInBag = Math.max(1, gameStatus.getRemainingBag(targetCharacter));
		int numTiles = gameStatus.getAuctionsRemaining();
		int averageSevenScore = getAverageSevenScore(targetCharacter, gameStatus);
		if (rackSize < 7)
			return (getAverageSevenScore(targetCharacter, gameStatus) - spentSoFar) / Math.max(2, (7 - rackSize));
		return (ASSUMED_WORD_SCORE - spentSoFar) / (2); // temp fix to make sure our bidding does not stop before getting a 7 letter word.
	}

	private float getBidMultiplier(Character targetCharacter) {
		setBidLevels();
		
		float RETURN_BID;
		if(bidLevels.get(HIGH).contains(targetCharacter)){
			RETURN_BID = HIGH_BID;
		}
		else if(bidLevels.get(MID).contains(targetCharacter)){
			RETURN_BID = MID_BID;
		}
		else {
			RETURN_BID = LOW_BID;
		}
		
		if(Util.getWordInRack()!= null && Util.getWordInRack().getWord().contains(targetCharacter.toString())) {
			logger.debug("word in Rack" + Util.getWordInRack().getWord());
			logger.debug("Bid level original" + RETURN_BID);
			if(RETURN_BID == HIGH_BID) {
				RETURN_BID = MID_BID;
			} else if(RETURN_BID == MID_BID) {
				RETURN_BID = LOW_BID;
			}
			logger.debug("Bid level changed" + RETURN_BID);
		}
		return RETURN_BID;
	}
	
	private int getAverageSevenScore(Character targetCharacter, Status gameStatus) {
		int sumSevenScore = 0;
		int numSevenLetterWords = 0;
		return ASSUMED_WORD_SCORE;
	}

	public void setNumberOfSevenLetterWords(int numberOfSevenLetterWords) {
		this.numberOfSevenLetterWords = numberOfSevenLetterWords;
	}

	public int getNumberOfSevenLetterWords() {
		return numberOfSevenLetterWords;
	}

	public void setFrequencyMap(HashMap<Character,Integer> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}

	public HashMap<Character,Integer> getFrequencyMap() {
		return frequencyMap;
	}
	 
}
