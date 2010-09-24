package seven.f10.g3;

import java.util.ArrayList;

/**
 * TrieNode.java
 * 
 * Class to represent a trie node. Store the element (string), height at which
 * the element is stored at (though this is more useful for debugging purposes
 * than actual implementations), and an array list of its children. I chose to
 * use an array list so that if a node did not have all 26 children we could
 * save space. Finally it stores a boolean, word, which tells us whether or not
 * this is actually a word in the dictionary. This is important because, other
 * than this boolean, "prefix" nodes created based on prefixes in words rather
 * than dictionary words are almost identical to actual words. By storing
 * whether or not this is actually a word when we return our subtrees we can be
 * sure to exclude this.
 * 
 * 
 * @author Lep2128
 * 
 * @param <AnyType>
 */
class TrieNode<AnyType> {

	/**
	 * Constructs a trie node
	 * 
	 * @param theElement
	 * @param tempHeight
	 */
	TrieNode(AnyType theElement, int tempHeight, boolean w, String s) {
		element = theElement;
		height = tempHeight;
		children = new ArrayList<TrieNode>();
		word = w;
		realWord = s;
	}

	/**
	 * Outputs the data in the element to a string
	 * 
	 * @return e
	 */
	public String toString() {

		String e = element.toString();
		return e;
	}

	/**
	 * Returns length so that we know when we have reached the end of the word
	 * as we compare the characters
	 * 
	 * @return size
	 */
	public int returnSize() {

		String temp = (String) element;
		return temp.length();
	}

	/**
	 * Tells us whether or not this node is atually a dictioanry word
	 * 
	 * @return word
	 */
	public boolean isWord() {
		return word;
	}
	
	public String returnWord(){
		
		return (realWord);
		
	}

	/**
	 * Changes boolean value of word. Used when something that is initially
	 * stored a a prefix is later stored as a word. This way we simply change
	 * the boolean rather than add a duplicate word. For example, if we add
	 * attic we woudld add nodes ad a, at, att, atti, and attic although only
	 * "attic" would have a true word value. If we later add at we simply change
	 * what was only a prefix node to instead be a word node.
	 */
	public void changeWord(String r) {
		word = true;
		realWord = r;
	}

	AnyType element; // The data in the node
	public ArrayList<TrieNode> children;
	private int height = 0;
	private boolean word;
	private String realWord;

}