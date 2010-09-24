package seven.f10.g1.base;


public abstract class Strategy {
	protected Rack myRack;
	protected GameStatus gameStatus;
	protected WordList wordList;

	public Strategy(Rack myRack, GameStatus gameStatus, WordList myList) {
		this.myRack = myRack;
		this.gameStatus = gameStatus;
	}

	public abstract int getBidAmmount(char letter);

	public abstract void nextRound();



}
