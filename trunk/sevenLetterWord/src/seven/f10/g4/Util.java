package seven.f10.g4;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Contains some static functionalities that we may need in different part of the code
 * @author flavio
 *
 */
public class Util {
	
	public static final String SCRABBLE_LETTERS_EN_US =
		"EEEEEEEEEEEEAAAAAAAAAIIIIIIIIIOOOOOOOONNNNNNRRRRRR" +
		"TTTTTTLLLLSSSSUUUUDDDDGGGBBCCMMPPFFHHVVWWYYKJXQZ";
	public final static int[] inBag = { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
	public final static char[] alphabet = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	private static Word wordInRack;
	
	public static Word getWordInRack() {
		return wordInRack;
	}
	
	public static void setWordInRack(Word setMe) {
		wordInRack = setMe;
	}
	/**
	 * Associates each Character with its array index e.g. a = 0, and z = 25;
	 */
	public static HashMap<Character, Integer> charToIndexMap;
	static
	{
		charToIndexMap = new HashMap<Character, Integer>();
		for (int i = 0; i < alphabet.length; i++) {
			charToIndexMap.put(new Character(alphabet[i]), new Integer(i));
		}	
	}
	
	
	/**
	 * Creates an HashMap that associates an Integer to each alphabet Character
	 * Initially, 0 for each alphabet letter. 
	 * Can be used keep track of frequencies or avaraged bidding amounts by players
	 * @returns
	 */
	public static HashMap<Character, Integer> createAlphabetToIntMap()
	{
		HashMap<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i = 0; i < alphabet.length; i++) {
			map.put(new Character(alphabet[i]), new Integer(0));
		}
		return map;
	}
	
	/**
	 * get the index corresponding to character c, for example a = 0, and z = 25;
	 * @param c
	 * @return
	 */
	public static int getIndexFromChar(Character c){
		return charToIndexMap.get(c);
	}
	
	/**
	 * get the char corresponding to the index i, for example 0 = a, and 25 = z;
	 * @param c
	 * @return
	 */
	public static Character getCharFromIndex(int i){
		return new Character(alphabet[i]); 
	}
	
}
