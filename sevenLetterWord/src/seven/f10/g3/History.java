package seven.f10.g3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import org.apache.log4j.Logger;
import seven.ui.Letter;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class History
{

	private ArrayList<BidLog> bidLogList;
	private final int[] associatedValue={8, 2, 3, 4, 10, 1, 3, 3, 8, 0, 1, 5,
		3, 6, 6, 3, 0, 7, 8, 5, 4, 1, 1, 0, 2, 0};
	private int[] totalLetters={9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8,
		2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
	private ArrayList[] marketValue;
	private ArrayList<Integer> allBids;
	protected Logger l=Logger.getLogger(this.getClass());
	private int[] lettersLeft;
	private int numberOfPlayers=0, numHidden=0, numberOfRoundsPlayed,
		totalLettersInBag=98;
	private double strengthArr[][];
	private final double L=0;
	private final double M=.25;
	private final double H=0.7;
	private final double lStrategy=1.000*10/26;
	private final double hStrategy=1.000*21/26;	

	public History()
	{
		bidLogList=new ArrayList<BidLog>();
		marketValue=new ArrayList[26];
		allBids=new ArrayList();
		lettersLeft=new int[26];
	}

	public int adjust(double bidStrategy, Letter bidLetter,
		ArrayList<PlayerBids> cachedBids, int ourID)
	{
		// strategy
		double strength=bidStrategy*H;
		
		double associatedStrength=(strength+0.1)*2;
		double bid=0;
		int bidLetterIndex=bidLetter.getAlphabet()-'A';

		// round other than first round
		if (cachedBids.size()!=0)
		{
			int np=cachedBids.get(0).getBidvalues().size();
			PlayerBids lastBids=cachedBids.get(cachedBids.size()-1);
			for (int i=0; i<lastBids.getBidvalues().size(); i++)
				allBids.add(lastBids.getBidvalues().get(i));
			Letter lastLetter=lastBids.getTargetLetter();
			int lastLetterIndex=lastLetter.getAlphabet()-'A';
			
			// update market value
			if (marketValue[lastLetterIndex]==null)
				marketValue[lastLetterIndex]=new ArrayList<Integer>();
			ListIterator<Integer> it=lastBids.getBidvalues().listIterator();
			while (it.hasNext())
				marketValue[lastLetterIndex].add(it.next());

			// update bidTimes
			lettersLeft[lastLetterIndex]++;

			double overallAffect=.33;
			int indexm=-1;
			double m=0;
			if (marketValue[bidLetterIndex]==null)
			{
				m=associatedValue[bidLetterIndex]*associatedStrength;
			}
			else
			{
				indexm=(int)(Math.round(strength*marketValue[bidLetterIndex].size()));
				if (indexm==marketValue[bidLetterIndex].size())
					indexm--;
				Collections.sort(marketValue[bidLetterIndex]);
				m=(1-overallAffect)*(Integer)(marketValue[bidLetterIndex].get(indexm));
			}

			int indexa=(int)(Math.round(strength*allBids.size()));
			if (indexa==allBids.size())
				indexa--;
			Collections.sort(allBids);
			double o=overallAffect*allBids.get(indexa);

			// account for the number of letters left in the bag
			double adj=2-1.000*(totalLettersSeen()-numHidden)/(totalLettersInBag-numHidden);
			// account for number of letters left of this letter
			double adj2=1.5-(1.000*(totalLetters[bidLetterIndex]-lettersLeft[bidLetterIndex])/totalLetters[bidLetterIndex]);
			bid=(m+o); // add the .5 to account for rounding when we cast to int
			if (bidStrategy>hStrategy)
				// bid = bid * adj * adj2;
				bid=bid*adj;
			//l.warn(adj+", "+adj2+", "+m+", "+o);
			/*
			 * //l.warn("adj: " + adj); //l.warn("adj2: " + adj2);
			 */

			// Never bid 0 in a two player round
			if (np<=4)
			{
				//l.warn("Since its a two player game we are adjusting our bid");
				if (bidStrategy<lStrategy)// make sure that statistics are not skewed
					bid=.8*bid;
				if (bid<1)
					bid=1;
			}
			/*l.warn("Strategy is: "+bidStrategy+" and bid is: "+bid
				+" on letter: "+bidLetter.getAlphabet());*/
		}
		else
		{ // Only used on the first round
			bid=associatedValue[bidLetterIndex]*associatedStrength;
		}
		return (int)(bid+0.5);
	}

	/**
	 * Returns whether it's even possible that a certain letter is still in the
	 * bag to play on.
	 * 
	 * Depends on scrabble letter frequency.
	 */
	public boolean letterPossiblyLeft(char Letter)
	{

		if (lettersLeft[Letter-'A']==totalLetters[Letter-'A'])
			return false;
		else
			return true;
	}

	/** Getter method for bid times */
	public int getBidTimes(int letterPlace)
	{
		return lettersLeft[letterPlace];
	}

	/**
	 * A simple function to return the number of letters left to bid on, i.e.
	 * number of rounds left. For each player, 8 letters are put in the bag.
	 * 
	 * @return number left
	 */
	public int numTilesLeftToBid()
	{
		return (numberOfPlayers*(8-numHidden))-numberOfRoundsPlayed;
	}

	public void setNumberOfRoundsPlayed(int i)
	{
		numberOfRoundsPlayed=i;
	}

	public int getNumberOfRoundsPlayed()
	{
		return numberOfRoundsPlayed;
	}

	/** Return total number of letters that will be seen in this game */
	public int totalLettersSeen()
	{
		return (numberOfPlayers*8);
	}

	public int getNumHidden()
	{
		return numHidden;
	}

	public void setNumHidden(int numHidden)
	{
		this.numHidden=numHidden;
	}

	public void setNumberOfPlayers(int size)
	{
		numberOfPlayers=size;
	}
}