package seven.f10.group5smarterplayer;
import java.io.*;
import java.util.Scanner;

public class get7letterwords {
	public static void main(String[] args) throws Exception
	{
		Scanner input = new Scanner(new File("src/seven/f10/g5/wordlistnoanagrams.txt"));
		PrintWriter output = new PrintWriter("src/seven/f10/g5/7LetterWordsNoAnagrams.txt");
		String word;
		input.useDelimiter("\n");
		while(input.hasNextLine())
		{
			word = input.next();
			if(word.length() == 8)
				output.print(word);
		}
		output.close();
	}
	
}
