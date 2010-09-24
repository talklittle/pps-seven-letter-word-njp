package seven.f10.g3;

public class AlphabetFrequency
{
	static double [] alphabet={12028, 3996, 5942, 6627, 16068, 
		2563, 5338, 4599, 12154, 577, 
		2718, 8664, 4941, 9584, 9426, 
		4711, 360, 10727, 13216, 8382, 
		6765, 1623, 2285, 629, 3298, 760};
	
	public static void main(String[] args)
	{
		int i;
		for (i=0; i<26; i++)
		{
			System.out.print((int)(alphabet[i]/1500)+", ");
			if (i%5==0)
				System.out.println();
		}
	}
}
