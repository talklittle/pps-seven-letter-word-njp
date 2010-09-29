package seven.f10.g7.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import seven.f10.g7.ScrabbleBag;
import seven.f10.g7.Word;
import seven.f10.g7.TrieNode;
import seven.ui.ScrabbleValues;

/**
 * A class that loads the dictionary of the Scrabble-Words
 * 
 * @author Eva
 * 
 */
public class WordReader {
    private String dictionaryName = null;

    public WordReader(String dictName) {
        this.dictionaryName = dictName;
    }

    private boolean isFeasible(String w) {
    	if (w.length() > ScrabbleBag.MAX_WORD_LENGTH) {
    		return false;
    	}
    	
        char[] wChar = w.toCharArray();
        HashMap<Character, Integer> wordFrq = new HashMap<Character, Integer>();
        Integer freq;
        for (int i = 0; i < wChar.length; i++) {
            /*At least one letter of the alphabet exists*/
            if (!wordFrq.containsKey(new Character(wChar[i])))
                wordFrq.put(new Character(wChar[i]), 1);
            else {
                freq = wordFrq.get(new Character(wChar[i]));
                freq++;
                if (freq > ScrabbleValues.getLetterFrequency(new Character(
                        wChar[i])))
                    return false;
            }
        }
        
        return true;
    }

    public TrieNode buildDictionary() {
        File file = new File(this.dictionaryName);
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(file));
            String line = null;
            Word word = null;
            TrieNode trie = new TrieNode();
            input.readLine();
            while ((line = input.readLine()) != null) {
                String w = line.split(",")[1];
                word = new Word(w);
                if (isFeasible(w) && (trie.findAnagram(w) == null)) {
                    trie.insert(word);
                }
            }
            return trie;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String args[]) {

        /* Naive main serializing the TrieNode */

        WordReader wr = new WordReader("SOWPODS.txt");

        TrieNode trie = wr.buildDictionary();
        if (trie == null) {
        	if(ScrabbleBag.debug) System.err.println("Failed to build trie from dictionary.");
        } else {
	        try {
	            FileOutputStream fos = new FileOutputStream("./src/seven/f10/g7/dictionary.obj");
	            ObjectOutputStream out = new ObjectOutputStream(fos);
	            out.writeObject(trie);
	            out.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
        }
    }
}