/*
 * Blake Arnold
 * Elizabeth Kierstead
 * Archana Balakrishnan
 */

package seven.f10.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.String;
import org.apache.log4j.Logger;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class GroupUnoPlayer implements Player, Runnable {


	// Variables for entire game
	protected static Logger logger = Logger.getLogger(GroupUnoPlayer.class);
	private static DictionaryAnalyzer analyzer; 
	private static int NUMBER_OF_SCRABBLE = 80;

	// Per round variables
	private int round = 0;
	private int tiles = NUMBER_OF_SCRABBLE;
	private ArrayList<Word> wordList;
	private ArrayList<Letter> rack = new ArrayList<Letter>();
	private ArrayList<Letter> cachedLetters = new ArrayList<Letter>();;
	
	private char[][] need_list = new char[26][2]; // 2-dim array to store the
													// frequency of every needed
													// letter

	private ArrayList<Word> possibleWords;
	private char[] alphabets = new char[26];
	private int[] values = new int[26];
	
	
	public GroupUnoPlayer()
	{
		analyzer = new DictionaryAnalyzer(this, logger);
		wordList = analyzer.readInWordList(true);
		
	}

	@Override
	public void Register() {
		for (int n = 0, letter = 'A'; n < 26; n++, letter++) {
			need_list[n][1] = 0;
			need_list[n][0] = (char) letter;
			alphabets[n] = (char) letter;
			values[n] = 0;
		}

	}

	public int numforletter(char letter)
	{
		return letter - 'A';
	}

	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
			int total_rounds, ArrayList<String> PlayerList,
			SecretState secretstate, int PlayerID) {
		
		
		if (tiles == NUMBER_OF_SCRABBLE) {
			for (Letter letter : secretstate.getSecretLetters()){
				rack.add(letter);
				logger.debug("adding to rack:" + letter.getAlphabet());
			}
		} else {
			
			if (PlayerBidList.get(PlayerBidList.size()-1).getWinnerID() == PlayerID){
				rack.add(cachedLetters.get(cachedLetters.size()-1));
				//possibleWords = findPossibleWords();
				logger.debug("adding to rack:" + cachedLetters.get(cachedLetters.size()-1).getAlphabet() + "winner :" + PlayerBidList.get(PlayerBidList.size()-1).getWonBy());
			}
		}
		cachedLetters.add(bidLetter);
		int bidAmount = getBidAmmountNiave(bidLetter.getAlphabet());
		updateGameStatus(PlayerList.size(), total_rounds, secretstate
				.getTotalLetters());
		if(!shouldBid()){
			bidAmount = 0;
		}

		return bidAmount;
	}
	
	private int getBidAmmountNiave(char letter){
		int rackSize = rack.size();
		int [] bids = {12, 10, 7 };
		if(rackSize < 2){
			for(int i = 0; i < bids.length; i++){
				bids[i] = bids[i]*2;
			}
		}
		if(rackSize >7){
			for(int i = 0; i < bids.length; i++){
				bids[i] = bids[2-i]/2;
			}
		}
		letter = Character.toUpperCase(letter);
		switch (letter) {
		case 'S' : 
		case 'T' :
		case 'A' :
		case 'R' :
		case 'L' :
		case 'I' :
		case 'N' :
		case 'E' : return bids[0];
		case 'M' :
		case 'B' :
		case 'F' :
		case 'G' : return bids[1];
		default : return bids[2];
		}
	}
	private int getBidAmmountFrequency(Letter bidLetter){
		//new Thread(this).run();
		
		
		//int bidMultiplier = 3;

		//int bidAmount = bidLetter.getValue() * bidMultiplier;
		
		int bidAmount =0;
		int hashvalue = 0;
		
		/*
		for (int s = 0;  s < analyzer.calc.letterFrequencies.size(); s++)
		{
			for (int t = s+1; t<analyzer.calc.letterFrequencies.size(); t++)
			{
			
			int valueT = analyzer.calc.letterFrequencies.get(new Character(alphabets[t]));
			int valueS = analyzer.calc.letterFrequencies.get(new Character(alphabets[s]));
			
			if (valueT > valueS)
			{
				int temp = analyzer.calc.letterFrequencies.get(new Character(alphabets[t]));
				analyzer.calc.letterFrequencies.put(new Character(alphabets[t]), );
			}
				
				
				
			}
			
		}
		*/
		
		
		//copying the frequencies of letters from hastable to need_list
		for (int s = 0;  s < analyzer.calc.letterFrequencies.size(); s++)
		{
			if (analyzer.calc.letterFrequencies.get(new Character(alphabets[s]))== null)
			{
				hashvalue = 0;
			}
			else
				hashvalue = analyzer.calc.letterFrequencies.get(new Character(alphabets[s]));
			
			values[s] = hashvalue;
			
		}
		
		//sorting need_list
		for (int outer=0; outer<26; outer++)
		{
			for(int inner=outer+1; inner<26; inner++)
			{
					
				if (values[inner] > values[outer])
				{
					int temp = values[inner];
					values[inner] = values[outer];
					values[outer] = (char) temp;
					
					char tempc = alphabets[inner];
					alphabets[inner] = alphabets[outer];
					alphabets[outer] = (char) temp;
				}
			}
		}
		
		
		//assigning bid amounts depending on the frequencies of letters
		for (int top3=0; top3<3; top3++)
		{
			if (bidLetter.getAlphabet() == values[top3])
				bidAmount = 12;
		}
		
		for (int next5=3; next5<8; next5++)
		{
			if (bidLetter.getAlphabet() == values[next5])
				bidAmount = 10;
		}
		
		
		for (int last2=8; last2<10; last2++)
		{
			if (bidLetter.getAlphabet() == values[last2])
				bidAmount = 5;
		}
		

		return bidAmount;
	}

	private void updateGameStatus(int numOfPlayers, int tRounds, int numSecret) {
		
		// first subtract initial tiles out of bag
		if (tiles == 80) {
			tiles = tiles - numOfPlayers * numSecret;
			round++;
		}

		// now subtract this round's tile
		tiles--;
		// check if new round
		if (tiles == 0) {
			tiles = 80;
			round++;
		}
		// check if new game
		if (tRounds == round) {

		}
	}

	@Override
	public String returnWord() {
		
		return getOptimalWord();
	}

//	/**
//	 * Reads in small word list file.
//	 * @return 
//	 */
//	private static ArrayList<Word> readInWordList() {
//		BufferedReader r;
//		String line = null;
//		ArrayList<Word> wordList = new ArrayList<Word>(defaultSize);
//		
//
//		try {
//			r = new BufferedReader(new FileReader(
//					"SOWPODS.txt")); 
//
//			
//			//SKIP first line of SOWPODS.txt
//			r.readLine();
//
//			while (null != (line = r.readLine())) {
//				String[] wd_list = line.split(",");
//				
//				wordList.add(new Word(wd_list[1].trim(), new Integer(wd_list[0])));
//				
//			}
//		} catch (FileNotFoundException e) {
//			logger.trace("Word List File Not Found!");
//		} catch (IOException e) {
//			logger.trace("Problem reading word list file.");
//		}
//		return wordList;
//
//	}
	//TODO: fix should bid
	private boolean shouldBid(){
		return true;
	}

	/**
	 * finds list of possible words given the rack at any particular time
	 * 
	 * @return
	 * 
	 */
	synchronized private ArrayList<Word> findPossibleWords() {
		/*scan through the entire wordlist looking at words one at a time.
		for each word in the wordlist, look for each letter that is on the rack.
		if the word does not contain each letter on the rack, remove it*/
		possibleWords = new ArrayList<Word>();
		
		
		for( Word word: wordList){
			String myRackString = "";
			for (Letter x :rack){
				myRackString += x.getAlphabet();
			}
			Word myRack = new Word(myRackString);

			
			for (int j = 0; j < rack.size(); j++) {
				
				Character letter = rack.get(j).getAlphabet();
				// if a particular letter on the rack is not found in the word,
				// remove it from the list of possible words
				if (myRack.contains(word)) {
					possibleWords.add(word);
					logger.debug("adding word" + word.getWord());
					
					for (int k = 0; k < word.getWord().length(); k++)
					{
							int num = numforletter(word.getWord().toCharArray()[k]);
							need_list[num][1]++;
							
										
						
					}
					
					
					
					}
				
					//remove letters on the rack from need_list
					for (int l=0;l<26;l++)
					{
						if ((need_list[l][0] == letter) && (need_list[l][1]>0)) need_list[l][1]--;
					}
				
				
				}
		}
			
			//sorting need_list according to freq
			
			for (int outer=0; outer<26; outer++)
			{
				for(int inner=outer+1; inner<26; inner++)
				{
						
					if (need_list[inner][1] > need_list[outer][1])
					{
						int temp = need_list[inner][1];
						need_list[inner][1]= need_list[outer][1];
						need_list[outer][1] = (char) temp;
						
						char tempc = need_list[inner][0];
						need_list[inner][0] = need_list[outer][0];
						need_list[outer][0] = tempc;
					}
				}
			}
			
			
			
			return possibleWords;
		}

		
	
	
	
	private String getOptimalWord(){
		findPossibleWords();
		for (int j = 0; j < rack.size(); j++) {
			
			Character letter = rack.get(j).getAlphabet();
			logger.debug("Rack Letter ("+ (j+1) +"/"+rack.size()+")"+ letter );
		}
		Word optimalWord = new Word("", 0);
		for (Word word : possibleWords) {
			
			//finds best 7 letter word
			int value;
			if(word.getWord().length() <= 7){
				value = word.getValue();
				if(word.getWord().length() == 7){
					value += 50; 
					logger.debug("7 letters: " + word.getWord() + value);
				}
				if(optimalWord.getValue() < value){
					optimalWord = word;
				}
			}
		}

		return optimalWord.getWord();
	}

	@Override
	public void run() {
		findPossibleWords();
		
	}

	@Override
	public void updateScores(ArrayList<Integer> scores) {
		// TODO Auto-generated method stub
		
	}
	
	
}

