package seven.f10.g4;

public class WordHelper {

	public final static String[] frequentPairs = { "HT", "EH",  "AN" ,"ER" ,"IN" ,"NO" ,"AT" ,"DN" ,"ST", "ES" ,"EN" ,"OF" ,"ET", "DE", "OR", "IT" ,"HI" ,"AS", "OT" };
	
	public boolean containsFrequentPair(Word wordOnRack, Word wordInDictionary) {
		
		char[]  have = wordOnRack.getWord().toCharArray();
		char[] want = wordInDictionary.getWord().toCharArray();
		char[] unmatched = new char[want.length];
		int matched = 0;
		
		//sort the letters of each word
		java.util.Arrays.sort(have);
		java.util.Arrays.sort(want);
		
		for(int i = 0, j=0, k = 0; i < have.length && j < want.length; ) { 
			if(have[i] == want[j]) {
				matched++;
				i++;
				j++;
			}else if (have[i] < want[j]) {
				i++;
			}else {
				unmatched[k] = want[j];
				k++;
				j++;
			}
		}
		
		//partial match needs this condition
		if(matched > have.length / 2) {
		
			String rest = new String(unmatched);
		
			for(String s : frequentPairs) {
				if(rest.contains(s)) {
					return true;
				}
			}
		}
		return false;
	}
}
