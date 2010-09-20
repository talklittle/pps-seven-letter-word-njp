package seven.f10.g4;
import java.util.ArrayList;
import java.util.HashMap;


public class SevenLetterWordHelper {
	private ArrayList<Word> sevenLetterDictionary=new ArrayList<Word>();
	private HashMap<Character,Integer> frequencyMap=new HashMap<Character, Integer>();
	public void setSevenLetterDictionary(ArrayList<Word> sevenLetterWords){
		this.sevenLetterDictionary=sevenLetterWords;
	}
	public ArrayList<Word> getSevenLetterDictionary(){
		return sevenLetterDictionary;
	}
	public ArrayList<Word> getSevenLetterWords(Word wordFromRack){
		frequencyMap=new HashMap<Character, Integer>();
		ArrayList<Word> sevenLetterWords=new ArrayList<Word>();
		for(Word sevenLetterWord: sevenLetterDictionary){
			if(wordFromRack.matchesPartially(sevenLetterWord)){
				sevenLetterWords.add(sevenLetterWord);
				updateLetterFrequency(sevenLetterWord);
			}
		}
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
	public HashMap<Character,Integer> getFrequencyMap(){
		return frequencyMap;
	}
	

}
