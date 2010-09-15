package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

public class Bidder {
	private static final String LOW = "LOW";
	private static final String MID = "MID";
	private static final String HIGH = "HIGH";
	private static final int HIGH_BID = 10;
	private static final int MID_BID = 4;
	private static final int LOW_BID = 1;
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
			System.err.println("Frequency Map "+c+frequencyMap.get(c)+" total "+numberOfSevenLetterWords);
			if(4*frequencyMap.get(c)>=numberOfSevenLetterWords){
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

	public int getBidAmount(Character targetCharacter) {
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
