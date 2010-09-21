package seven.f10.g1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public class DictionaryAnalyzer
{
	protected static Logger logger;
	private GroupUnoPlayer player; 
	public AprioriCalculator calc;
	
	public DictionaryAnalyzer(GroupUnoPlayer player, Logger logger)
	{
		this.player = player;
		this.logger = logger;
		run();
	}
	
	public void run()
	{
		calc = new AprioriCalculator();
		calc.analyzer = this;
		calc.calculateAPriori();
	}
	/**
	 * Reads in small word list file.
	 * @return alphabetized array list where each element consists of a word and its face value 
	 */
	public ArrayList<Word> readInWordList(boolean withFaceValue) {
		BufferedReader r;
		String line = null;
		ArrayList<Word> wordList = new ArrayList<Word>();
		

		try {
			r = new BufferedReader(new FileReader(
					"SOWPODS.txt")); 

			
			//SKIP first line of SOWPODS.txt
			r.readLine();

			while (null != (line = r.readLine())) {
				String[] wd_list = line.split(",");
				if (withFaceValue)
					wordList.add(new Word(wd_list[1].trim(), new Integer(wd_list[0])));
				else
					wordList.add(new Word(wd_list[1].trim()));
			}
				
		} catch (FileNotFoundException e) {
			logger.trace("Word List File Not Found!");
		} catch (IOException e) {
			logger.trace("Problem reading word list file.");
		}
		return wordList;

	}
	
	public Hashtable<Character,Integer> calcFrequenciesLetters(ArrayList<Word> wordList)
	{
		final Hashtable<Character, Integer> letterFrequencies = new Hashtable<Character, Integer>();
		int formerFrequency;
		
		for(Word word: wordList)
		{
			char[] letters = word.word.toCharArray();
			
			for(int i = 0; i < letters.length; i++)
			{
				if (letterFrequencies.get(new Character(letters[i])) != null)
				formerFrequency = letterFrequencies.get(new Character(letters[i]));
				else
					formerFrequency = 0;	
				letterFrequencies.put(new Character(letters[i]), formerFrequency++);
			}
		}
		
		return letterFrequencies;
	}
}
