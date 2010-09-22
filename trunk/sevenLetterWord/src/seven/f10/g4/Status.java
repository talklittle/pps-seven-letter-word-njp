package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.GameResult;

public class Status {

	private Integer game=0;
	private Integer turn=0;
	private Integer noOfPlayers=0;
	private HashMap<Integer, Integer> winningBids = new HashMap<Integer, Integer>(); //what was the winning bid on each letter
	private HashMap<Integer, Integer> scoreSoFar = new HashMap<Integer, Integer>(); //(id, score)
	private ArrayList<Opponent> opponentList = new ArrayList<Opponent>();
	
	public Integer getNoOfPlayers() {
		return noOfPlayers;
	}

	public void setNoOfPlayers(Integer noOfPlayers) {
		this.noOfPlayers = noOfPlayers;
	}

	public ArrayList<Opponent> getOpponentList() {
		return opponentList;
	}

	public void addOpponentToList(Integer id) {
		if(!opponentList.contains(new Opponent(id))) {
			this.opponentList.add(new Opponent(id));
		}
	}

	public void updateTurnAndGame(PlayerBids lastBid) {
		turn++;
		if(turn == 8*noOfPlayers) {
			game++;
			turn = 0;
		}
		Letter a = lastBid.getTargetLetter();
		winningBids.put(a.getValue(),lastBid.getWinAmmount());
		addOpponentToList(lastBid.getWinnerID());
		for(int i = 0; i < opponentList.size(); i++) {
			Opponent o = opponentList.get(i);
			o.updateBid(lastBid);
		}
	}
	
	private void resetGame() {
		turn = 0;
		winningBids = new HashMap<Integer, Integer>(); //what was the winning bid on each letter
		scoreSoFar = new HashMap<Integer, Integer>(); //(id, score)
		resetOpponents();
	}
	
	private void resetOpponents(){
		Iterator<Opponent> it = opponentList.iterator();
		while (it.hasNext()) {
			Opponent o = it.next();
			o.reset();
		}
	}

	public void updateScore(Opponent o, int value) {
		Integer scoreNow = scoreSoFar.get(o);
		scoreSoFar.put(o.getID(), scoreNow + value);
	}
	
	public Integer opponentScore(int id) {
		return scoreSoFar.get(id);
	}
	
	public Integer opponentScore(Opponent o) {
		return scoreSoFar.get(o.getID());
	}
	
	public Integer opponentSpend(int id) {
		for (Opponent o : opponentList) {
			if (o.getID() == id) {
				return opponentSpend(o);
			}
		}
		// FIXME should throw exception
		return 0;
	}
	
	public Integer opponentSpend(Opponent o) {
		return o.getSpend();
	}
	public Integer winningBid(Letter l) {
		return winningBids.get(l.getValue());
	}

	public int getMaxExpectedBid(Character targetCharacter) {
		Iterator<Opponent> it = opponentList.iterator();
		int max = 0;
		while (it.hasNext()) {
			Opponent o = it.next();
			if (max < o.expectedBids.get(targetCharacter))
				max = o.expectedBids.get(targetCharacter);
		}
		return max;
	}
}
