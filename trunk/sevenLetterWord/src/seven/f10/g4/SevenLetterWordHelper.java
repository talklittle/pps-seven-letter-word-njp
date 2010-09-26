package seven.f10.g4;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import seven.ui.Letter;


public class SevenLetterWordHelper {
	private ArrayList<Word> sevenLetterDictionary=new ArrayList<Word>();
	private HashMap<Character,Integer> frequencyMap=new HashMap<Character, Integer>();
	
	private Status gameStatus;
	
	private Logger logger = Logger.getLogger(SevenLetterWordHelper.class);
	
	
	public void setStatus(Status status) {
		gameStatus = status;
	}
	public void setSevenLetterDictionary(ArrayList<Word> sevenLetterWords){
		this.sevenLetterDictionary=sevenLetterWords;
	}
	public ArrayList<Word> getSevenLetterDictionary(){
		return sevenLetterDictionary;
	}
	public ArrayList<Word> getSevenLetterWords(Word wordFromRack, Status gameStatus){
		frequencyMap=new HashMap<Character, Integer>();
		ArrayList<Word> sevenLetterWords=new ArrayList<Word>();
		for(Word sevenLetterWord: sevenLetterDictionary){
			if(wordFromRack.matchesPartially(sevenLetterWord)){
				sevenLetterWords.add(sevenLetterWord);
				updateLetterFrequency(sevenLetterWord);
			}
		}
		sevenLetterWords = removeUnreachableWords(sevenLetterWords, gameStatus, wordFromRack);
		return sevenLetterWords;
	}
	
	private void updateLetterFrequency(Word word){
		for(int i=0;i<7;i++){
			if(frequencyMap.get(word.getWord().charAt(i))==null){
				frequencyMap.put(word.getWord().charAt(i),1);
			}
			else{
				frequencyMap.put(word.getWord().charAt(i),frequencyMap.get(word.getWord().charAt(i))+1);
			}
		}
	}
	
	
	private ArrayList<Word> removeUnreachableWords(ArrayList<Word> currentWords, Status gameStatus, Word rack) {
		
		ArrayList<Word> keepWords = new ArrayList<Word>();
		int remaining = 0;
		if(gameStatus == null)
			return currentWords;
		
		String remainingLettersString = gameStatus.getRemainingBagString() + rack.getWord();
		Word remainingLettersWord = new Word(remainingLettersString);
		for (Word w : currentWords) {
			if (remainingLettersWord.isInDictionary(w)) {
				keepWords.add(w);
			}
		}
		logger.debug("keepWords.size="+keepWords.size() + " currentWords.size="+currentWords.size());
		return keepWords;
	}
	
	public HashMap<Character,Integer> getFrequencyMap(){
		return frequencyMap;
	}
	

}
