package seven.f10.g3;

/**
 * TrieTree.java
 * 
 * Inserts, searches, and returns children of TrieNodes.
 * 
 * To insert into the free, first we check to see if that word is in the tree.
 * If it is we do nothing. Otherwise we start by breaking the word up into up to
 * 10 "subwords" to ensure that the word is properly placed in the tree. For
 * example apple will be broken into a, ap, app, appl, and finally apple. We
 * insert these "shortWords" into the tree. To ensure that these short words are
 * not printed when we do our lookup we set their boolean "word" value to false.
 * Now we are ready to insert the whole word. Again we check to see if our
 * entire word is in the tree. It if is it is because there is a "sub" word that
 * is equal to our new word (aka we first ate apple and then we wanted to add
 * at). Here we wont need to add another node "at" we just need the tree to know
 * that "at" is a word and not a "shortWord." So we call a method that changes
 * this boolean word value to true. Otherwise we need to insert the word.
 * 
 * Finding a node is similar to inserting a node in that we scan through all of
 * the children looking for a match, comparing character by character.
 * 
 * ReturnNode () Returns a node using almost the exact same method we used to
 * find a string. This is useful when we need to change the boolean word value
 * of a node.
 * 
 * 
 * @author lep2128 (based on BinarySearchTree.java by Weiss)
 */
public class TrieTree<AnyType extends Comparable<? super AnyType>> {
	/**
	 * Construct the tree by adding the null root
	 */
	public TrieTree() {

		root = new TrieNode("root", 0, false, "");
	}

	/**
	 * Inserts a word into the tree. First we check to see if that word is in
	 * the tree. If it is we do nothing. Otherwise we start by breaking the word
	 * up into up to 10 "subwords" to ensure that the word is properly placed in
	 * the tree. For example apple will be broken into a, ap, app, appl, and
	 * finally apple. We insert these "shortWords" into the tree. To ensure that
	 * these short words are not printed when we do our lookup we set their
	 * boolean "word" value to false. Now we are ready to insert the whole word.
	 * Again we check to see if our entire word is in the tree. It if is it is
	 * because there is a "sub" word that is equal to our new word (aka we first
	 * ate apple and then we wanted to add at). Here we wont need to add another
	 * node "at" we just need the tree to know that "at" is a word and not a
	 * "shortWord." So we call a method that changes this boolean word value to
	 * true. Otherwise we need to insert the word.
	 * 
	 * @param x
	 *            the item to insert.
	 */
	public void insert(String x, String realWord) {

		String word = (String) x;
		String temp = "";
		shortWord = ""; // shorter version of the word that we are adding, used
		// to ensure appropriate sub trees
		int h = 0; // root will be at "height" -1
		int c = 0;

		if (find(word, h, c, root, temp) == true
				&& returnAutoNode(word).isWord() == true)
			;// If word has already been added, do nothing

		else {

			// Break up word to ensure appropriate sub trees
			for (int i = 0; i < word.length() && i < breakUpConstant; i++) {
				shortWord += word.charAt(i);
				// If we don't find the shortWord as a root then we add it
				if (find(shortWord, h, c, root, temp) == false)
					insert(shortWord, temp, h, root, c, false, "");

			}

			// now insert whole word
			if (find(word, h, c, root, temp) == true
					&& returnNode(word, h, c, root, temp).isWord() == false) {

				returnNode(word, h, c, root, temp).changeWord(realWord);
			}

			else
				insert(word, temp, h, root, c, true, realWord);
		}

	}

	/**
	 * Private method to insert TrieNode
	 * 
	 * @param w
	 *            whole word
	 * @param temp
	 *            string
	 * @param h
	 *            height of node (not that important when implementing tree, but
	 *            good for debugging and understanding struture)
	 * @param n
	 *            the node
	 * @param c
	 *            the char value used as we move through the characters in the
	 *            word
	 * @param whole
	 *            whether or not we are inserting a whole word
	 * @return
	 */
	private TrieNode<AnyType> insert(String w, String temp, int h,
			TrieNode<AnyType> n, int c, boolean whole, String realWord) {

		h++;
		temp += w.charAt(c);

		for (int i = 0; i < n.children.size(); i++) {// scans through all
			// children

			String child = (String) n.children.get(i).element;
			if (temp.compareTo(child) == 0 && w.length() > c + 1) {
				c++;
				return insert(w, temp, h, n.children.get(i), c, whole, realWord);
			}

		}

		return add(w, h, n, whole, realWord);

	}

	/**
	 * Adds a new node
	 * 
	 * @param w
	 * @param h
	 * @param n
	 * @param word
	 * @return
	 */
	public TrieNode<AnyType> add(String w, int h, TrieNode<AnyType> n,
			boolean word, String realWord) {

		TrieNode<AnyType> tempNode = new TrieNode(w, h, word, realWord);
		n.children.add(tempNode);
		return tempNode;
	}

	/**
	 * Finds a string in the tree. This is similar to inserting a node in that
	 * we scan through all of the children looking for a match, comparing
	 * character by character.
	 * 
	 * @param w
	 * @return if the string is a node
	 */
	public boolean find(String w) {

		String temp = "";
		int h = 0;
		int c = 0;

		return find(w, h, c, root, temp);
	}

	/**
	 * Private method to find a node.
	 * 
	 * @param w
	 * @param h
	 * @param c
	 * @param n
	 * @param temp
	 * @return
	 */
	private boolean find(String w, int h, int c, TrieNode<AnyType> n,
			String temp) {

		h++;
		temp += w.charAt(c);

		for (int i = 0; i < n.children.size(); i++) {// scans through all
			// children

			String child = (String) n.children.get(i).element;

			if (temp.compareTo(child) == 0 && w.length() == child.length()) {// equal
				// and
				// same
				// length
				return true;
			}

			if (temp.compareTo(child) == 0 && w.length() > c + 1) {// here only
				// a
				// substring
				// matches
				c++;
				return find(w, h, c, n.children.get(i), temp);
			}

		}

		return false;

	}

	/**
	 * Returns a node using almost the exact same method we used to find a
	 * string. This is useful when we need to change the boolean word value of a
	 * node.
	 * 
	 * @param w
	 * @param h
	 * @param c
	 * @param n
	 * @param temp
	 * @return
	 */
	public TrieNode<AnyType> returnNode(String w, int h, int c,
			TrieNode<AnyType> n, String temp) {

		h++;

		// start at the root and scan its children; match with the first letter
		temp += w.charAt(c);

		for (int i = 0; i < n.children.size(); i++) {// scans through all
			// children looking for
			// match

			String child = (String) n.children.get(i).element;

			if (temp.compareTo(child) == 0 && w.length() > c + 1) {
				c++;
				return returnNode(w, h, c, n.children.get(i), temp);
			}

			else if (temp.compareTo(child) == 0
					&& n.children.get(i).isWord() == false) {
				return n.children.get(i);
			}

		}

		return null;
	}

	/**
	 * Test if the tree is logically empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * Use to do the autocomplete. Does this by returning the root corresponding
	 * to what was entered and then calling the print children method to print
	 * all of thats subtrees
	 * 
	 * @param w
	 */
	public void autoComplete(String w) {

		String temp = "";

		if (find(w) == true) {

			TrieNode<AnyType> tempRoot = returnAutoNode(w);
			if (tempRoot == null)
				;// do nothing
			else {
				printChildren(tempRoot);

			}
		}

		else
			System.out.println("Sorry, your string was not found");
	}

	/** Determines if input is word */
	public boolean findWord(String w) {

		if (find(w) == true) {

			System.out.println("found word");
			TrieNode<AnyType> tempRoot = returnAutoNode(w);
			return (tempRoot.isWord());
		}
		
		return(false);
	}

	/**
	 * Print the tree contents in sorted order.
	 */
	public void printChildren() {
		if (isEmpty())
			System.out.println("Empty tree");
		else
			printChildren(root);
	}

	/**
	 * Internal method to print a subtree in sorted order based on a preorder
	 * traversal (parent then child).
	 * 
	 * @param t
	 *            the node that roots the subtree.
	 */
	private void printChildren(TrieNode<AnyType> t) {
		if (t != null) {

			// Print element
			if (t.isWord())
				System.out.println(t.element);

			// Print children
			for (int i = 0; i < t.children.size(); i++)
				printChildren(t.children.get(i));

		}
	}

	public TrieNode<AnyType> returnAutoNode(String w) {

		String temp = "";
		int h = 0;
		int c = 0;

		return returnAutoNode(w, h, c, root, temp);

	}

	private TrieNode<AnyType> returnAutoNode(String w, int h, int c,
			TrieNode<AnyType> n, String temp) {

		h++;
		temp += w.charAt(c);

		for (int i = 0; i < n.children.size(); i++) {// scans through all
			// children looking for
			// match

			String child = (String) n.children.get(i).element; // make string
			// or child to
			// compare to

			if (temp.compareTo(child) == 0 && w.length() == child.length()) {// equal
				// and
				// same
				// length
				return n.children.get(i);
			}

			if (temp.compareTo(child) == 0 && w.length() > c + 1) {// here only
				// a
				// substring
				// matches
				c++;
				return returnAutoNode(w, h, c, n.children.get(i), temp);
			}

		}

		return null;
	}

	private TrieNode<AnyType> root;
	Integer index = -1;
	public static String shortWord;
	public static int breakUpConstant = 10; // the max number of subtrees that
	// we
	// will break every word into

}