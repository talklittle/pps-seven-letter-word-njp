package seven.f10.g1.players;

import seven.f10.g1.base.Grp1PlayerBase;

public class NaivePlayer extends Grp1PlayerBase{

	public NaivePlayer(){
		super();
		super.setStrategy(new NaiveStrategy(myRack, gameStatus, myList));
	}
	


}
