package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

import seven.ui.Letter;
import seven.ui.PlayerBids;

public class Opponent {

	private ArrayList<Letter> rack=new ArrayList<Letter>();
	private Integer[] bidHistory = new Integer[26]; //the history of bidding for this player on each letter.
	private Integer spendSoFar = 0; //for each game
	private Integer id;
	public HashMap<Character, Integer> expectedBids = Util.createAlphabetToIntMap();
	
	public Opponent(Integer id){
		this.id = id;
	}
	
	public void updateRack(Letter a) { 
		rack.add(a);
	}
	
	public void updateSpend(int spend) {
		spendSoFar += spend;
	}
	
	public void resetSpend() {
		spendSoFar = 0;
	}
	
	public int getSpend(){
		return spendSoFar;
	}
	
	public void resetRack() {
		rack.clear();
	}
	
	public void updateBid(PlayerBids lastBid) {
		Letter a = lastBid.getTargetLetter();
		bidHistory[Util.getIndexFromChar(a.getAlphabet())] = lastBid.getBidvalues().get(getID()); 
		expectedBids.put(a.getAlphabet(), lastBid.getBidvalues().get(id));
		if (getID() == lastBid.getWinnerID()) {
			int winningAmount = lastBid.getWinAmmount(); //is this reflecting the vickery auction? (is this second best?
			updateSpend(winningAmount);
			updateRack(a);
			expectedBids = Util.createAlphabetToIntMap();
		}
		
		
	}
	
	public Integer getID() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Opponent other = (Opponent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
