package seven.f10.g7;

import java.util.HashSet;

/**
 * A collection of rules-related utility methods.
 * @author zts2101
 */
public class ScrabbleBag {
    public static final int NUMBER_OF_LETTERS = 26;
    public static final int MAX_WORD_LENGTH = 7;
    public static final char STARTING_CHARACTER='A';
    public static final boolean debug = false;
    
    /**
     * @param c A letter to check.
     * @return Whether c is a uppercase English letter.
     */
    public static boolean isUppercaseLetter(char c) {
        if(c < 'A' || c > 'Z') {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * @param c A uppercase english letter.
     * @return The Caesarian Shift value of that letter minus one.
     * @throws IllegalArgumentException
     */
    public static int getIndex(char c) throws IllegalArgumentException {
        if(!isUppercaseLetter(c)) throw new IllegalArgumentException("Argument was not a letter");
        return (c - 'A');
    }
    
    public static HashSet<String> getCombinations(String s) {
        return ScrabbleBag.getCombinations(s, 1, Math.min(s.length(), MAX_WORD_LENGTH));
    }
    
    public static HashSet<String> getCombinationsOfLength(String s, int length) {
        return ScrabbleBag.getCombinations(s, length, length);
    }
    
    public static HashSet<String> getCombinations(String s, int min, int max) {
        char[] letters = s.toCharArray();
        HashSet<String> result = new HashSet<String>();
        for(int i = (int)(Math.pow(2, min))-1; i < Math.pow(2, s.length()); i++)
        {
            if(Integer.bitCount(i) <= max)
            {
                String word = "";
                for(int pos = 0; pos < s.length(); pos++) {
                    int bit = (int) Math.pow(2, pos);
                    if((i & bit) == bit)
                        word += letters[pos];
                }
                result.add(word);                
            }
        }
        
        return result;
    }
}
