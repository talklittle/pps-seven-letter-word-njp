package seven.f10.g1;

import java.util.ArrayList;

public class Subset 
{
	private int frequency; 
	private ArrayList<Character> charList;
	
	public Subset(ArrayList<Character> charList, int frequency)
	{
		this.frequency = frequency;
		this.charList = charList;
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public ArrayList<Character> getCharList()
	{
		return charList;
	}
}
