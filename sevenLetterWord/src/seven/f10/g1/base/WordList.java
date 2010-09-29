package seven.f10.g1.base;

import java.util.ArrayList;


public class WordList {
	ArrayList<Word> wordList;
	public WordList(){
	}
	
	public WordList(ArrayList<Word> wtmp){
		wordList = wtmp;
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
