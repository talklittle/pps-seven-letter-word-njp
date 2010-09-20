package seven.f10.g4;
import java.util.HashMap;

import seven.ui.Scrabble;

public class Word {
	public static final double PARTIAL_PERCENTAGE=.50;
	private Integer points;
	private String word;
	private Integer length;
	private int[] frequency=new int[27];
	public Word(String s){
		this.word=s;
		int score=0;
		this.setLength(word.length());
		for(int i=0;i<this.length;i++){
			this.frequency[Integer.valueOf(s.charAt(i))-Integer.valueOf('A')]++;
			score+=Scrabble.letterScore(s.charAt(i));
		}
		this.setPoints(score);
		
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getWord() {
		return word;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public Integer getPoints() {
		return points;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getLength() {
		return length;
	}
	public boolean isInDictionary(Word dictionaryWord){
		for(int i=0;i<26;i++){
			if(this.frequency[i]<dictionaryWord.frequency[i]){
				return false;
			}
		}
		return true;
	}
	/*If atleast 50% of the letters in a word match a seven letter word its a partial match*/
	public boolean matchesPartially(Word dictionaryWord){
		String partialWord=this.getWord();
		int count=0;
		int length=partialWord.length();
		for(int i=0;i<length;i++){
			if(dictionaryWord.frequency[Integer.valueOf(partialWord.charAt(i))-Integer.valueOf('A')]>0){
				count++;
			}
		}
		if(count>=Math.ceil(length*PARTIAL_PERCENTAGE)){
			return true;
		}
		else{
			return false;
		}
	}
	
}
