package seven.f10.g4;

import java.util.ArrayList;

import seven.ui.Letter;

public class Opponent {

	private ArrayList<Letter> rack=new ArrayList<Letter>();
	private Integer[] bidHistory = new Integer[26]; //the history of bidding for this player on each letter.
	private Integer spendSoFar = 0; //for each game
	private Integer id;
	
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
	
	public void resetRack() {
		rack.clear();
	}
	
	public void bidUpdate(Letter a, int value) {
		bidHistory[Util.getIndexFromChar(a.getAlphabet())] = value; //check this!
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
