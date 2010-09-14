package seven.f10.g4;

import seven.ui.Scrabble;

public class Word {
	private Integer points;
	private String word;
	private Integer length;
	public Word(String s){
		word=s;
		points=0;
		setLength(word.length());
		points=Scrabble.getWordScore(word);
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
	
	

}
