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
			//if he has 1 letter, then reduce each expected bid by 0.85 on all others letters
			//if 2 letters, 0.7
			//if 3 letters, 0.55
			//if 4 letters, 0.40
			//if 5 letters, 0.25
			//if 6 letters, 0.10
			//if 7 letters just set them all to 0
			double modifier;
			//rack size has to be larger than 0, because he just won
			if(rack.size() == 1)
				modifier = 0.85;
			else if(rack.size() == 2)
				modifier = 0.70;
			else if(rack.size() == 3)
				modifier = 0.55;
			else if(rack.size() == 4)
				modifier = 0.40;
			else if(rack.size() == 5)
				modifier = 0.25;
			else if(rack.size() == 6)
				modifier = 0.10;
			else modifier = 0;
			for (int i = 0; i < Util.alphabet.length; i++) {
				Character c = new Character(Util.alphabet[i]);
				int oldVal = expectedBids.get(c);
				expectedBids.put(c, (int) (modifier * oldVal));
			}
			expectedBids.put(a.getAlphabet(), 0);
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
