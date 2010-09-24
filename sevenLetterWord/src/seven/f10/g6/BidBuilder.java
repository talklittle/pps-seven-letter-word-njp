package seven.f10.g6;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;
import seven.ui.ScrabbleValues;

public class BidBuilder {

	/**
	 * Create BidBuilder object
	 */

	private double value;
	private double posval;
	
	static {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(org.apache.log4j.Level.DEBUG);
	}
	protected Logger l = Logger.getLogger(this.getClass());
	
	public BidBuilder()
	{
		//near7 = have7 = false;
		
		//no-op

	}
	
	public void reset()
	{
		value = 0;
	}
	
	public void wonletter()
	{
		value = posval;
	}
	
	public int bid(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl,
			ArrayList<PlayerBids> cachedBids,int currentPoint, int ourID)
	{
		/*
		//bid zero if we have 7.
		if(have7)
			return 0;
		else if (near7){
			
			//Dummy word
			Word sevenWord = new Word("Dummyyy");
			return make7(bidLetter, letters, cachedBids, sevenWord, currentPoint,ourID);
			
		}

		else 
			return distance( bidLetter, letters, wordlist, slwl);
			*/
		return distance( bidLetter, letters, wordlist, slwl, cachedBids, currentPoint, ourID);
	}
	
	public void setHidden( ArrayList<Character> letters, Word[] wordlist)
	{
		double sum = 0;
		//for each word
		for(Word w: wordlist)
		{
			//get percentage
			//add percentage to total
			double prct = getPercentage(w, letters, null);
			sum += prct;
		}
		value = sum;		
	}
	
	private int distance(Letter bidLetter, ArrayList<Character> letters, Word[] wordlist, Word[] slwl, ArrayList<PlayerBids> cb,
			int cp, int id)
	{
		double sum = 0;
		//for each word
		for(Word w: wordlist)
		{
			//check if word is 7 letters
			//if so send to make7()
			if(w.length == 7)
			{	
				int b = make7(bidLetter, letters, cb, w, cp, id);
				//if near 7, bid return val
				if(b != 0)
					return b;
			}
	
			//get percentage
			//add percentage to total
			double prct = getPercentage(w, letters, bidLetter);
			sum += prct;
		}
		l.debug("word search complete");
		//get % difference
		double pdiff;
		if (value != 0)
			pdiff = (sum - value) / value;
		else
			pdiff = 0.5;
		//multiply sum by 10
		posval = sum;
		l.debug("BID " + Math.round(pdiff *10));
		return (int) Math.round(pdiff * 10);
	}
	
	private int make7(Letter bidLetter, ArrayList<Character> letters, 
			ArrayList<PlayerBids> cachedBids, Word sevenWord, int currentPoint,int ourID)
	{
		
		//determine bid price that doesn't exceed what we will get
		//1. Using bidletter and letters we have
		//1.2 Count if it's 5-6 letters then continue, else return 0
		//2. See if these word is in sevenWord, if not return 0
		//2.1. determine points you will get in 7-letter word.
		//3. Plus 50 points bonus into calculation
		//4. determine how much bids we have played so far.
		//5. Calculate how much point we have left for bidding and not going to lose point in the end.
		//6. Get percentage value of that letter.
		//7. Bid
		
		ArrayList<Character> determiningLetter = new ArrayList<Character>();
		for(int i = 0; i<letters.size();i++){
			
			determiningLetter.add(letters.get(i));
			
		}
		determiningLetter.add(bidLetter.getAlphabet());
		//1.2 Count if it's 5-6 letters then continue, else return 0
		if(determiningLetter.size()<5)
			return 0;
		else{
			//2. See if these word is in sevenWord, if not return 0.
			//not all letters have to be in sevenWord
			//Bug: duplicate letters
//			int found = 0;
//			
//			for(int j=0;j<determiningLetter.size();j++){
//				
//				for(int i = 0; i<sevenWord.word.length();i++){
//					
//					if(determiningLetter.get(j).equals(sevenWord.word.charAt(i))){
//						
//						found++;
//						
//					}
//					
//				}
//				
//			}
			
			//using Dan's percentage function.
			
			double percent = getPercentage(sevenWord,letters,bidLetter);
			
			if((percent>=(6.0/7.0))){
				//use getWordScore to calculate word score
				int points = ScrabbleValues.getWordScore(sevenWord.word);
				//4. determine how much bids we have played so far.
				int pointPlayed = 0;
				int secondHighBid = 0;
				for(int i = 0;i<cachedBids.size();i++){

					if (ourID == cachedBids.get(i).getWinnerID()) {
						secondHighBid = 0;
						for(int j = 0;j<cachedBids.get(i).getBidvalues().size();j++){
							if(secondHighBid<cachedBids.get(i).getBidvalues().get(j)){
								secondHighBid = cachedBids.get(i).getBidvalues().get(j);
							}
						}
						
						pointPlayed += secondHighBid;
						
					}
					
				}
				//5. Calculate how much point we have left for bidding and not going to lose point in the end.
				int pointsLeft = points-pointPlayed;
				//6. Get percentage value of that letter base on whole word value and point left.
				//7. Bid
				double bidLetterScore = ScrabbleValues.letterScore(bidLetter.getAlphabet());
				double missingLetterScore = points-getScoreFromCurrentLetter(sevenWord,letters)-50-bidLetterScore;
				double bidMultiplier = bidLetterScore/missingLetterScore;
				
				if((pointsLeft>0)&&(pointsLeft<=currentPoint)){
					
					return (int)bidMultiplier*pointsLeft;
					
				}else{
					
					return 0;
					
				}
				
			}else{
				// if we already gather all the letters in 7-letter word.
				return 2;
				
			}
			
		}
		
	}

	// adapt from Dan's getPercentage function.
	private double getScoreFromCurrentLetter(Word w, ArrayList<Character> letters)
	{
		double score = 0;
		ArrayList<Integer> usedletters = new ArrayList<Integer>(7);
		Character[] lets;
		lets = letters.toArray(new Character[letters.size()]);
		
		for(Character c: lets)
		{
			String word = w.word;
			int r = word.indexOf(c, 0);
			if(r==-1)
			{
				continue;
			}
			else
			{
				if(!usedletters.contains(r))
				{
					
					usedletters.add(r);
					score += ScrabbleValues.letterScore(c);
				}
				else
				{
					while(usedletters.contains(r) || r != -1)
					{
						r = word.indexOf(c, r+1);
					}
					if(r!=-1)
					{
						usedletters.add(r);
						score += ScrabbleValues.letterScore(c);
					}
				}
			}
		}
		
		return score;
	}
	
	private double getPercentage(Word w, ArrayList<Character> letters, Letter bidletter)
	{
		double total = w.length;
		double found = 0;
		ArrayList<Integer> usedletters = new ArrayList<Integer>(7);
		Character[] lets;
		if(bidletter != null)
		{
			lets = letters.toArray(new Character[letters.size()+1]);
			lets[lets.length-1] = bidletter.getAlphabet();
		}
		else
		{
			lets = letters.toArray(new Character[letters.size()]);
		}
		
		for(Character c: lets)
		{
			String word = w.word;
			int r = word.indexOf(c, 0);
			if(r==-1)
			{
				continue;
			}
			else
			{
				if(!usedletters.contains(r))
				{
					
					usedletters.add(r);
					found++;
				}
				else
				{
					while(usedletters.contains(r) || r != -1)
					{
						r = word.indexOf(c, r+1);
					}
					if(r!=-1)
					{
						usedletters.add(r);
						found++;
					}
				}
			}
		}
		
		return (found/total);
	}

	
}
