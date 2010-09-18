package seven.f10.g4;

import java.util.ArrayList;

import seven.ui.Letter;

public class Opponent {

	private ArrayList<Letter> rack=new ArrayList<Letter>();
	private Integer[] bidHistory = new Integer[26]; //the history of bidding for this player on each letter.
	private Integer spendSoFar = 0; //for each game
	private Integer id;
	
	public void updateRack(Letter a) { 
		rack.add(a);
	}
	
	public void updateSpend(int spend) {
		spendSoFar += spend;
	}
	
	public void resetSpend() {
		spendSoFar = 0;
	}
	
	public void resetRack() {
		rack.clear();
	}
	
	public void bidUpdate(Letter a, int value) {
		bidHistory[a.getAlphabet() - 'A'] = value; //check this!
	}
	
	public Integer getID() {
		return id;
	}
}
