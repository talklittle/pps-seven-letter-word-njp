package seven.f10.g3;

import java.io.*;
import java.util.*;

public class Manipulate7Words {

	public static void main(String args[]) {

		try {
			// Open File
			FileInputStream fstream = new FileInputStream(
					"src/seven/f10/g3/smallwordlist.txt");

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			// Read File Line By Line
			FileWriter f7 = new FileWriter("src/seven/f10/g3/alpha-smallwordlist7-allcombos.txt");
			BufferedWriter out7 = new BufferedWriter(f7);
			while ((strLine = br.readLine()) != null) {
					if(strLine.length() == 7)
						out7.write(combosToWrite(strLine));
			}

			// Close the stream
			in.close();
			out7.close();
			System.out.println("finished");

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/* Orders a string */
	public static String Sort(String old_string) {
		char[] c = old_string.toCharArray();
		Arrays.sort(c);
		return (new String(c));
	}
	
	public static String combosToWrite(String str){
		String ret = "";
		combination_list = new ArrayList<String>();
		combinations("", str);
		for(int i = 0; i < combination_list.size(); i++){
			ret += (combination_list.get(i) + ", " + str + "\n");
		}
		return(ret);
	}
	
	private static void combinations(String prefix, String s) {
		if (s.length() > 0) {
			String str = prefix + s.charAt(0);
			if (str.length() > 4) {
				combination_list.add(str);
			} 
			combinations(prefix + s.charAt(0), s.substring(1));
			combinations(prefix, s.substring(1));
		}
	}
	
	private int len = 4;
	private static ArrayList<String> combination_list;

}
