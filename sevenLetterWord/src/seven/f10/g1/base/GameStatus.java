package seven.f10.g1.base;

import java.util.ArrayList;

import seven.ui.Letter;

public class GameStatus {
	private ArrayList<Opponent> opponents;
	private int currentRound = 1;
	private int totalRounds;
	private Sack scrabbleSack;
	/**
	 * Initialize opponents
	 * @param playerList
	 */
	public void initPlayers(ArrayList<String> playerList, int myID) {
		// TODO Auto-generated method stub
		
	}
	public void addToOpponents(int winner, Letter letterWon) {
		// TODO Auto-generated method stub
		
	}
	public void nextRound() {
		currentRound++;
		
	}
	
}
