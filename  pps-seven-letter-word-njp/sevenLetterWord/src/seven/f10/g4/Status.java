package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.GameResult;

public class Status {

	private Integer game=0;
	private Integer turn=0;
	private Integer noOfPlayers=0;
	private HashMap<Letter, Integer> winningBids = new HashMap<Letter, Integer>(); //what was the winning bid on each letter
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
		if(opponentList.contains(new Opponent(id))) {
			this.opponentList.add(new Opponent(id));
		}
	}

	public void updateTurnAndGame(PlayerBids lastBid) {
		turn++;
		if(turn == 8*noOfPlayers) {
			game++;
		}
		Letter a = lastBid.getTargetLetter();
		winningBids.put(a,lastBid.getWinAmmount());
		addOpponentToList(lastBid.getWinnerID());
		for(int i = 0; i < opponentList.size(); i++) {
			Opponent o = opponentList.get(i);
			if (o.getID() == lastBid.getWinnerID()) {
				o.updateSpend(lastBid.getWinAmmount()); //is this reflecting the vickery auction? (is this second best?
			}
		}
	}
	
	public void updateScore(Opponent o, int value) {
		Integer scoreNow = scoreSoFar.get(o);
		scoreSoFar.put(o.getID(), scoreNow + value);
	}
	
	
	public Integer opponentScore(Opponent o) {
		return scoreSoFar.get(o);
	}
	
	public Integer winningBid(Letter l) {
		return winningBids.get(l);
	}
}
