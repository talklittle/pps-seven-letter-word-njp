package seven.f10.g1.base;

public class Opponent {
	private Rack rack;
	private int biddingLevel;
	private int weightedBids;
	
	public static final int HIGH = 2;
	public static final int MEDIUM = 1;
	public static final int LOW = 0;
	
	public Opponent(){
		biddingLevel = LOW;
	}
	
	public void setBiddingLevel(int biddingLevel) {
		this.biddingLevel = biddingLevel;
	}
	public int getBiddingLevel() {
		return biddingLevel;
	}

	public void putBid(int bid) {
		weightedBids = (weightedBids + bid) / 2;
	}

	public int getWeightedBid() {
		return weightedBids;
	}
	
}
