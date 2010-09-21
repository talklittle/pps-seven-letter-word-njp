package seven.f10.g1;

import java.util.ArrayList;
import java.util.Hashtable;

public class AprioriCalculator
{
	/* 
	 * 1. Traverse dictionary and calculate the frequency of each subset of size one (i.e. the frequency of each letter in the dictionary)
	 * 2. Weed out the 10% least frequent letters. 
	 * 3. Traverse the dictionary again, calculating the frequency of the subsets of size 2, where each letter in the subset was considered a LARGE itemset in the 
	 * 	  previous pass. 
	 * 4. Terminate with the list of subsets of size RACK + BID_LETTER 
	 * 
	 * 
	 */
	
	public DictionaryAnalyzer analyzer;
	public Hashtable<Character, Integer> letterFrequencies;
	
	public void calculateAPriori()
	{
		// 1. Traverse dictionary and calculate the frequency of each subset of size one (i.e. the frequency of each letter in the dictionary)
		//read in word list WITHOUT recording the face value of the words
		ArrayList<Word> wordList = analyzer.readInWordList(false);
		letterFrequencies = analyzer.calcFrequenciesLetters(wordList);
		
		//2. Weed out the 10% least frequent letters. 
		
		
	}
	
	
	
	
	
	
	
}
