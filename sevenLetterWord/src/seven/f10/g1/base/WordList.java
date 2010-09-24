package seven.f10.g1.base;

import java.util.ArrayList;

import seven.g0.Word;

public class WordList {
	ArrayList<Word> wordList;
	public WordList(ArrayList<Word> words){
		wordList = words;
	}
	
	public String getOptimalWord(Rack r){
		
		char c[] = new char[r.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = r.get(i).getAlphabet();
		}
		String s = new String(c);
		Word ourletters = new Word(s);
		Word bestword = new Word("");
		for (Word w : wordList) {
			if (ourletters.contains(w)) {
				if (w.score > bestword.score) {
					bestword = w;
				}

			}
		}
		return bestword.word;
		
	}
}
