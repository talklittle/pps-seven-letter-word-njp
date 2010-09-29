package seven.f10.g3;

import java.util.*;

public class Rack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Rack() {
		r = new ArrayList<RackLetter>();
	}

	/** Returns the rack as a character arraylist */
	public ArrayList<Character> getCharArrayList() {

		ArrayList<Character> chars = new ArrayList<Character>(r.size());
		for (int i = 0; i < r.size(); i++)
			chars.add(r.get(i).getL());

		return (chars);
	}

	/** Returns the rack as a character arraylist */
	public char[] getCharArray() {

		char[] chars = new char[r.size()];
		for (int i = 0; i < r.size(); i++)
			chars[i] = r.get(i).getL();

		Arrays.sort(chars);
		return (chars);
	}

	public void add(RackLetter l) {
		r.add(l);
	}

	public int size() {
		return r.size();
	}

	public RackLetter get(int i) {
		return (r.get(i));
	}

	public String toString() {
		String str = "";

		for (int i = 0; i < r.size(); i++)
			str += r.get(i).getL();

		return (str);
	}
	
	public void clear(){
		r = new ArrayList<RackLetter>();
	}

	private ArrayList<RackLetter> r;
}
