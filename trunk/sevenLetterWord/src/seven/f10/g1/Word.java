package seven.f10.g1;

import java.util.Arrays;

import org.apache.log4j.Logger;

import seven.ui.Scrabble;


public class Word{
	
	private static final Logger logger = Logger.getLogger(Word.class);
	String word = "";
	int value = 0;
	public Word(String word, int value){
		setWord(word);
	}

	public Word(String word){
		setWord(word);
	}
	
	public static int getValue(String word){
		char[] string = word.toCharArray();
		int value = 0;
		for(char l : string){
			value += Scrabble.letterScore(l);
		}
		return value;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
		this.value = getValue(word);
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean contains(Word w){
		char[] myWord = word.toCharArray();
		char[] testWord = w.getWord().toCharArray();
		Arrays.sort(myWord);
		int location;
		for (int i=0;i<testWord.length; i++){
			location = Arrays.binarySearch(myWord, testWord[i]);
			if(Arrays.binarySearch(myWord, testWord[i]) < 0)
				return false;
			else{
				myWord[location] = '0';
				Arrays.sort(myWord);
			}
				
		}
		return true;

	}
}
