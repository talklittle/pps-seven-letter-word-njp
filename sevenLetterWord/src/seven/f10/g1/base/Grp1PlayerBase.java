package seven.f10.g1.base;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import seven.g0.Word;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public abstract class Grp1PlayerBase implements Player {

	private final static Logger logger = Logger.getLogger(Grp1PlayerBase.class);
	protected Rack myRack = new Rack();
	protected static WordList myList;
	static {
		BufferedReader r;
		String line = null;
		ArrayList<Word> wtmp = new ArrayList<Word>(55000);
		try {
			r = new BufferedReader(new FileReader(
					"src/seven/g1/super-small-wordlist.txt"));
			while (null != (line = r.readLine())) {
				wtmp.add(new Word(line.trim()));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myList = new WordList(wtmp);
	}
	protected GameStatus gameStatus = new GameStatus();
	protected Strategy myStrategy;
	protected boolean isFirstBid = true;
	protected int myID;
	

	@Override
	public void Register() {
		// TODO Auto-generated method stub

	}
	
	public void setStrategy(Strategy myStrategy){
		this.myStrategy = myStrategy;
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {

		if (isFirstBid) {
			initialRoundSetup(gameStatus, myRack, total_rounds, PlayerList,
					secretstate, PlayerID);
			isFirstBid = false;
		} else {
			updateGameStatus(gameStatus, bidLetter, PlayerBidList, PlayerID, myRack);
		}

		int bidAmmount = myStrategy.getBidAmmount(bidLetter.getAlphabet());
		return bidAmmount;
	}

	private static void initialRoundSetup(GameStatus gameStatus, Rack rack,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		gameStatus.initPlayers(PlayerList, PlayerID);

		for (Letter l : secretstate.getSecretLetters()) {
			logger.debug("Adding hidden Letters: " + l.getAlphabet());
			rack.add(l);
		}

	}

	private static void updateGameStatus(GameStatus gameStatus,
			Letter bidLetter, ArrayList<PlayerBids> PlayerBidList, 
			int PlayerID, Rack rack) {
		
		int lastIndex = PlayerBidList.size() - 1;
		PlayerBids bids = PlayerBidList.get(lastIndex);
		int winner = bids.getWinnerID();
		Letter letterWon = bids.getTargetLetter();
		
		if( PlayerID == winner) {
			rack.add(letterWon);
		} else {
			gameStatus.addToOpponents(winner, letterWon);
		}

	}

	@Override
	public String returnWord() {
		// TODO: add last letter
		// updateGameStatus(gameStatus, bidLetter, PlayerBidList);

		String word = myList.getOptimalWord(myRack);
		String rackString = "";
		for (Letter l : myRack) {

			rackString += l.getAlphabet();
			
		}
		logger.debug("Rack Letters = " + rackString);
		//Reset for next round
		isFirstBid = true;
		myRack.clear();
		gameStatus.nextRound();
		myStrategy.nextRound();
		
		return word;
	}

}
