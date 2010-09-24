package seven.f10.g3;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * TestTrie.java
 * 
 * Tests the TrieTree / TrieNode classes.
 * 
 * @author Lep2128
 * 
 */
class TestTrie {

	public static void main(String[] args) throws IOException {

		//filename = args[0];
		filename = "src/seven/f10/g3/alpha-smallwordlist7-allcombos.txt";
		
		TrieTree<String> t = new TrieTree<String>();
		Scanner scan = new Scanner(System.in);

		System.out.println("Loading Dictonary. Standby...");

		reader = new BufferedReader(new FileReader(filename));

		//Read each line and then add word to trie
		try {
			while ((line = reader.readLine()) != null) {

				line = line.toLowerCase();
				String[] l = line.split(", ");
				t.insert(l[0], l[1]);
			}

			System.out.println("Dictionary loaded!");

			// Allows user to search for words
			while (finding) {
				System.out.println();
				System.out.println("Please enter a word");
				String find = scan.next();

				//System.out.println(t.findWord(find));
				//TrieNode<String> node = t.returnAutoNode(find);
				//System.out.println(node.isWord());
				//System.out.println(node.returnWord());
				t.autoComplete(find);
				
				System.out.println("go again? (1) for yes");
				int go = scan.nextInt();

				if (go != 1)
					finding = false;

			}}
		
		catch (Exception e) {
			System.out.println("Error: Input not recognized.");
			System.exit(1);
		}
		
		System.out.println("Program Finished");

	}

	static int go = 1;
	static BufferedReader reader;
	static String line;
	static String filename;
	static boolean finding = true;
}