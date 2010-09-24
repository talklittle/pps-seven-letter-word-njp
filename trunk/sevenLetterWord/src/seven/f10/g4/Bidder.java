package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

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
		int numTiles = 8 * gameStatus.getOpponentList().size();
		if (rackSize < 7)
			return (ASSUMED_WORD_SCORE - spentSoFar) / Math.max(2, (7 - rackSize));
		return (ASSUMED_WORD_SCORE - spentSoFar) / (2); // temp fix to make sure our bidding does not stop before getting a 7 letter word.
	}

	private float getBidMultiplier(Character targetCharacter) {
		setBidLevels();
		if(bidLevels.get(HIGH).contains(targetCharacter)){
			return HIGH_BID;
		}
		if(bidLevels.get(MID).contains(targetCharacter)){
			return MID_BID;
		}
		return LOW_BID;
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
