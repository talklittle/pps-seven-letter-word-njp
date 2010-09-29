package seven.f10.g1.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.log4j.Logger;

import seven.ui.Letter;

public class GameStatus {
	private final Logger logger = Logger.getLogger(GameStatus.class);

	
	private HashMap<Integer, Opponent> opponents;
	private int currentRound = 1;
	private int totalRounds;
	private Sack scrabbleSack;
	private HashMap<Character, Integer> historicalBids;
	private HashMap<Character, Integer> historicalWinPrice;
	private ArrayList<Integer> historicalBid;
	private int totalBids = 0;
	private int totalWonPrice = 0;
	private int totalBidPrice = 0;
	
	
	public GameStatus(){
		historicalBids = new HashMap<Character, Integer>();
		opponents = new HashMap<Integer, Opponent>();
		historicalBid = new  ArrayList<Integer>();
		historicalWinPrice = new HashMap<Character, Integer>();
	}
	
	
	/**
	 * Initialize opponents
	 * @param playerList
	 */
	public void initPlayers(ArrayList<String> playerList, int myID) {
		
		
	}
	
	public void addToOpponents(int playerID, Letter letterBidding, int bidPrice, int winPrice, boolean won) {
		
		Opponent op = opponents.get(playerID);
		
		if(op == null){
			opponents.put(playerID, new Opponent());
			 op = opponents.get(playerID);
		}
		
		if(!won)
			historicalBid.add(bidPrice);
		
		int histTmp;
		totalBids++;
		
		
		if(won){
			totalWonPrice += winPrice;
			char letter = letterBidding.getAlphabet();
			if(historicalBids.containsKey(letter))
				histTmp = historicalBids.remove(letter);
			else
				histTmp = winPrice;
			historicalBids.put(letterBidding.getAlphabet(), (winPrice + histTmp)/2);
			
			changeOpponentBidLevel(playerID);
			totalBidPrice += winPrice;
			if(historicalWinPrice.containsKey(letter))
				histTmp = historicalWinPrice.remove(letterBidding.getAlphabet());
			else
				histTmp = winPrice;
			historicalWinPrice.put(letterBidding.getAlphabet(), (winPrice + histTmp)/2);
		}
		
		
		
		
	
		
	}
	
	private void changeOpponentBidLevel(int id){
		Integer[] tmpIntArray = new Integer[historicalBid.size()];
		tmpIntArray = historicalBid.toArray(tmpIntArray);
		for(Integer i : tmpIntArray )
			System.out.println(" Historical Bids: " +i);
		Opponent op = opponents.get(id);
		if(tmpIntArray != null && tmpIntArray.length > 2){
			Arrays.sort(tmpIntArray);
			double q1 = getQuater(tmpIntArray, 1);
			double q3 = getQuater(tmpIntArray, 3);
		
			System.out.println(q1 + " d " +q3);
		
			if(op.getWeightedBid() < q1)
				op.setBiddingLevel(0);
			else if(op.getWeightedBid() < q3)
				op.setBiddingLevel(2);
			else
				op.setBiddingLevel(1);
		} else {
				
			op.setBiddingLevel(1);
		}
		
		
	}
	
	public double getQuater(int q){
		Integer[] tmpIntArray = new Integer[historicalBid.size()];
		tmpIntArray = historicalBid.toArray(tmpIntArray);
		if(tmpIntArray != null && tmpIntArray.length > 2){
			Arrays.sort(tmpIntArray);
			return getQuater(tmpIntArray, q);
		} 
		//TODO: return default bid
		return 3;
	}
	public Integer[] removeDeadZone(Integer[] tmpIntArray){
		if(tmpIntArray.length < 2)
			return null;
		int i;
		for(i = tmpIntArray.length - 1; tmpIntArray[i] == null; i--);
		Integer[] returnArray = new Integer[i+1];
		for( ; i >= 0; i--)
			returnArray[i] = tmpIntArray[i];
		return returnArray;
	}
	public boolean isOutlier(Integer[] numbers, int num){

		if(numbers.length < 2)
			return false;
		Arrays.sort(numbers);
		double q1 = getQuater(numbers, 1);
		double q3 = getQuater(numbers, 3);
		

		double iqr = q3-q1;
		

		double low = q1-1.5*iqr;
		double high = q3+1.5*iqr;
		
		System.out.println(" q1: "+ q1  +" q3: "+ q3 + " iqr: " + iqr +" Quater high: " + high + "Quater low: " + low);
		
		if(num < low || num > high)
			return true;
		else
			return false;

		
	}
	
	public double getQuater(Integer[] numbers, int quater){
		double percentHigh;
		if(quater < 1 || quater > 3)
			throw new IndexOutOfBoundsException("Quater has to be between 1 and 3");
		if (quater == 1)
			percentHigh = .25;
		else if (quater == 3)
			percentHigh = .75;
		else
			percentHigh = .5;
		
		double key = (numbers.length + 1 ) / (4.0 / quater);
		if( (int)key != key){
			if(Math.floor(key) > 0 && Math.ceil(key) < numbers.length){
				System.out.println("Quater key: " + key + " Percenthigh: " + percentHigh + " num low: " + numbers[(int) Math.floor(key) - 1] + " num high: " + numbers[(int) Math.ceil(key) - 1]);
				return (1 - percentHigh) * (numbers[(int) Math.floor(key) - 1]) +  percentHigh * numbers[(int) Math.ceil(key) - 1];
			} else {
				System.out.println("Quater key: " + key);
				return (1 - percentHigh) * (numbers[0]) +  percentHigh * numbers[1];
				
			}
		} else {
			System.out.println("Quater key: " + key);
			return numbers[(int) key - 1];
		}
		
	}
	public void nextRound() {
		currentRound++;
	}


	public HashMap<Character, Integer> getHistoricalBids() {
		return historicalBids;
	}


	public HashMap<Character, Integer> getHistoricalWinPrice() {
		return historicalWinPrice;
	}
	
	public void addToOpponents(int winner, Letter letterWon) 
	{
		
		
		
	}
	public HashMap<Integer, Opponent> getOpponents() {
		return opponents;
	}
}
