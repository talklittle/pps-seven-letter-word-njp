package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.PlayerBids;

public class Opponent {

	private ArrayList<Letter> rack=new ArrayList<Letter>();
	private Integer[] bidHistory = new Integer[26]; //the history of bidding for this player on each letter.
	private Integer spendSoFar = 0; //for each game
	private Integer id;
	public HashMap<Character, Integer> expectedBids = Util.createAlphabetToIntMap();
	
	private Logger logger = Logger.getLogger(Opponent.class);
	
	public Opponent(Integer id){
		this.id = id;
	}
	
	public void updateRack(Letter a) { 
		rack.add(a);
	}
	
	public void updateSpend(int spend) {
		logger.info("spentSoFar="+spendSoFar + " plus spend="+spend + " Player:"+id);
		spendSoFar += spend;
	}
	
	public int getSpend(){
		return spendSoFar;
	}
	
	public ArrayList<Letter> getRack() {
		return rack;
	}
	
	public void updateBid(PlayerBids lastBid) {
		Letter a = lastBid.getTargetLetter();
		bidHistory[Util.getIndexFromChar(a.getAlphabet())] = lastBid.getBidvalues().get(getID()); 
		expectedBids.put(a.getAlphabet(), lastBid.getBidvalues().get(id));
		if (getID() == lastBid.getWinnerID()) {
			int winningAmount = lastBid.getWinAmmount(); //is this reflecting the vickery auction? (is this second best?
			updateSpend(winningAmount);
			updateRack(a);
			//We need to reduce the expected bid for this player on all letters, and set it to 0 for this letter just won
			//the expected bid on the letter that he just won will be 0
			//the expected bid on all other letters is reduced by 1/3
			//if he has 7 letters now, we must assume that he got a 7 letter word and that he will be bidding 0
			double modifier;
			if(rack.size() == 7)
				modifier = 0;
			else modifier = 0.67;//every letter reduce by 1/3
			for (int i = 0; i < Util.alphabet.length; i++) {
				Character c = new Character(Util.alphabet[i]);
				int oldVal = expectedBids.get(c);
				expectedBids.put(c, (int) (modifier * oldVal)); //all letters exp bids reduced by 1/3
			}
			expectedBids.put(a.getAlphabet(), 0);//the that he just won set to 0
		}
		logger.debug("Player: "+id+", letter: "+a.getAlphabet()+", win:"+lastBid.getWinnerID()+" expected bids: "+expectedBids);
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

	public void reset(){
		rack.clear();
		spendSoFar = 0;
		bidHistory = new Integer[26];
		expectedBids = Util.createAlphabetToIntMap();
	}

}
