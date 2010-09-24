package seven.f10.g7;

import java.io.Serializable;
import java.util.Arrays;

import seven.ui.Scrabble;

/**
 * A class for storing a word and getting its anagram-proof alphabetized
 * form and for sorting it by that form.
 * @author zts2101
 */
public final class Word implements Comparable<Word>, Serializable {
	private static final long serialVersionUID = 7796670526061010552L;
	
	private String theWord;
    
    public Word(String theWord) throws IllegalArgumentException {
        this.theWord = theWord.toUpperCase();
        
        // Check the validity of the word.
        char[] letters = this.theWord.toCharArray();
        for(char c : letters) {
            if(!ScrabbleBag.isUppercaseLetter(c)) {
                throw new IllegalArgumentException("Words must only include letters. " + theWord + " is invalid.");
            }
        }
    }
    
    /**
     * Used to get the sorted string for sorting and statistics.
     * @return A string with the same letters as this word sorted alphabetically.
     */
    public String getSorted() {
        char[] chars = this.theWord.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
    
    /**
     * @override
     */
    public String toString() {
        return this.theWord;
    }
    
    public int getScore() {
        int baseScore = Scrabble.getWordScore(this.theWord);
        return baseScore + ((baseScore > 0 && this.theWord.length() == 7) ? 50 : 0);
    }
    
    public static int getScore(String s) {
        int baseScore = Scrabble.getWordScore(s);
        return baseScore + ((baseScore > 0 && s.length() == 7) ? 50 : 0);
    }
    
    /**
     * @return The length of the string
     */
    public int length() {
        return theWord.length();
    }
    
    @Override
    public int compareTo(Word other) {
        if(other == null) {
            return 1;
        }
        
        return this.getSorted().compareTo(other.getSorted());
    }
    
    @Override
    public boolean equals(Object other) {
        if(other == null || !(other instanceof Word)) {
            return false;
        } else {
            return this.theWord.equals(((Word) other).toString());
        }
    }
}
