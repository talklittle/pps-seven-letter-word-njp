package seven.f10.g1.players;

import java.util.ArrayList;

import seven.f10.g1.base.Grp1PlayerBase;

public class NaivePlayer extends Grp1PlayerBase{
	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
	public NaivePlayer(){
		super();
		super.setStrategy(new NaiveStrategy(myRack, gameStatus, myList));
	}
	


}
