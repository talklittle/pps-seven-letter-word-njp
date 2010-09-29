package seven.f10.g1.base;

import java.util.ArrayList;
import java.util.Arrays;

import seven.ui.Letter;

public class Rack extends ArrayList<Letter>{
	public Rack(){
		super();
	}
	@Override
	public String toString(){
		
		return new String(this.toCharArray());
	}
	
	
	public char[] toCharArray(){
		char[] x = new char[this.size()];
		for(int  y = 0; y < this.size(); y++){
			x[y] = this.get(y).getAlphabet();
		}
		Arrays.sort(x);
		return x;
	}
	
	public String[] toStringArray(){
		char[] sortedArray = this.toCharArray();
		String[] x = new String[this.size()];
		for(int  y = 0; y < this.size(); y++){
			x[y] = new String(sortedArray[y]+ "");
		}
		return x;
	}

	
	

}
