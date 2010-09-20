package seven.g4;

import java.util.ArrayList;

public abstract class PatternMining {

	
	abstract ArrayList<String> getFrequentPatterns();
	
	abstract ArrayList<String> getFixedNumberPatterns();
	
	/*
	 *  Ratio = support(pattern) / support(Most frequent pattern)
	 */
	abstract void setRatioThreshold(double n);
	
	abstract void setFixedNumberThreshold(int n);
	
	
	
}
