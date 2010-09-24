package seven.f10.g7;

import java.io.Serializable;
import java.util.HashSet;

public class TrieNode implements Serializable {
	private static final long serialVersionUID = -2823769644359966373L;
	
	private Word word;
	private TrieNode[] children;
	private int[] numCharOccurances;
	private int numWords;
	private int numWordsScore;
	private int numSevenLetterWords;
	private int numSevenLetterWordsScore;

	public Word getWord() {
		return word;
	}

	public boolean hasWord() {
		return word != null;
	}
	
	public int getNumWords(boolean sevenLetterWordsOnly)
	{
	    if(sevenLetterWordsOnly)
        {
            return numSevenLetterWords;
        }
        else
        {
            return numWords;
        }
	}

	public double getAverageScore(boolean sevenLetterWordsOnly)
	{
	    if(sevenLetterWordsOnly)
	    {
	        return numSevenLetterWordsScore / (double) numSevenLetterWords;
	    }
	    else
	    {
	        return numWordsScore / (double) numWords;
	    }
	}
	
	public double getCharacterCountToWordCountRatio(char c)
	{
	    return numCharOccurances[ScrabbleBag.getIndex(c)] / (double) numWords;
	}
	
	public TrieNode() {
	    word = null;
	    children = new TrieNode[26];
	    numCharOccurances = new int[26];
	    for(int i = 0; i < 26; i++)
	        numCharOccurances[i] = 0;
	    numWords = 0;
	    numWordsScore = 0;
	    numSevenLetterWords = 0;
	    numSevenLetterWordsScore = 0;
	}

	public TrieNode(Word w) {
	    this();
		word = w;
	}

	public void insert(Word w) {
		insert(w, w.getSorted(), 0, w.getSorted().length(), true);
		if(ScrabbleBag.debug) System.err.println("WORD IS: " + w);
	}

	private void insert(Word w, String sorted, int position, int length, boolean isWord) {
	    if(isWord && ScrabbleBag.debug) System.err.println(sorted.substring(0, position) + "\t" + this.word + "\t" + this.hasWord());
		if (position == length) {
			// we have created and reached the TrieNode we want
			if(isWord) {
				this.word = w; // just in case we had a partial here prior
			}
			return;
		}

		if (isWord && position == 0) {
		    HashSet<String> toSearch = ScrabbleBag.getCombinations(w.getSorted(), 0, w.length() - 1);
		    for (String s : toSearch) {
		        insert(new Word(s), s, 0, s.length(), false);
		    }
		}

		if (getChild(sorted.charAt(position)) == null)
			children[ScrabbleBag.getIndex(sorted.charAt(position))] = new TrieNode();

		numWords++;
		numWordsScore += w.getScore();
		if(w.length() == 7)
		{
		    numSevenLetterWords++;
		    numSevenLetterWordsScore += w.getScore();
		}
		
		// for every character we haven't seen before...
		for(char c : w.toString().substring(position).toCharArray())
		   numCharOccurances[ScrabbleBag.getIndex(c)]++;

		getChild(sorted.charAt(position)).insert(w, sorted, position + 1, length, isWord);
	}

	/**
	 * @param unsortedString The word to find
	 * @return The TrieNode containing that word or null if it isn't found.
	 */
	public TrieNode find(String unsortedString) {
		Word w = new Word(unsortedString);
		return find(w, w.getSorted(), 0, false, false);
	}
	
	/**
	 * @param unsortedString The word to search from
	 * @return The TrieNode containing the word or an anagram of it, or null if it isn't found.
	 */
	public TrieNode findAnagram(String unsortedString) {
		Word w = new Word(unsortedString);
		return find(w, w.getSorted(), 0, true, false);
	}
	
	/**
	 * Traverses to a specified node regardless of whether it's a word.
	 * @param unsortedString The node path to traverse.
	 * @return The node or null if it doesn't lie on a valid path.
	 */
    public TrieNode findNode(String unsortedString) {
        Word w = new Word(unsortedString);
        return find(w, w.getSorted(), 0, false, true);
    }

	/**
	 * @param w The original search string.
	 * @param sorted The search string in alphabetical order
	 * @param position Tracker of progress. Start at 0.
	 * @param includeAnagrams Whether to return Anagrams as a valid result.
	 * @param nodeMode Whether the result must be a word, or can just be a node.
	 * @return The node where the word (or an anagram of it, if includeAnagrams is true) is found, or null if it isn't found.
	 */
	private TrieNode find(Word w, String sorted, int position, boolean includeAnagrams, boolean nodeMode) {
		if ((this.hasWord() && (word.equals(w) || (includeAnagrams && word.getSorted().equals(sorted)))) ||
		        (nodeMode && position == w.length()))
			return this;
		
		if (position >= sorted.length())
			return null;
		TrieNode curr = this.getChild(sorted.charAt(position));
		if (curr == null)
			return null;
		
		return curr.find(w, sorted, position + 1, includeAnagrams, nodeMode);
	}
	
	public TrieNode findBestAverageScore(Word search, int minimumDepth) {
	    return findBestNodeMaster(search, true, minimumDepth);
	}

	public Word findBestWord(Word search) {
	    //if(ScrabbleBag.debug) System.err.println("------------ FIND BEST WORD ---------------\n" + search + "\n------------ FIND BEST WORD ---------------\n");
	    TrieNode bestNodeMaster = findBestNodeMaster(search, false, 0);
	    return bestNodeMaster != null ? bestNodeMaster.getWord() : new Word("");
	}
	
	private TrieNode findBestNodeMaster(Word search, boolean nodeMode, int minimumDepth) {
	    HashSet<String> toSearch = ScrabbleBag.getCombinations(search.getSorted());
        TrieNode bestNode = null;
        int bestScore = 0;
        
        for (String s : toSearch) {
            TrieNode temp = findBestNode((new Word(s).getSorted()), bestNode, 0, nodeMode, minimumDepth);
            if(ScrabbleBag.debug) { Word ws = new Word(s); System.err.println("TEMP WORD IS: " + temp + " with score " + (temp != null && temp.getWord() != null ? temp.getWord().getScore() : "-1") + "\tFrom search string: " + (ws != null ? ws.getSorted() : "") + " with score " + (ws != null ? ws.getScore() : "-1")); }
            if (temp != null) {
                int tempScore = (temp.hasWord() ? temp.getWord().getScore() : (int) temp.getAverageScore(false));
                
                if (bestNode == null || tempScore > bestScore) {
                    bestScore = tempScore;
                    bestNode = temp;
                }
                if(ScrabbleBag.debug) System.err.println("BEST WORD IS: " + temp);
            }
        }

        return bestNode;
	}

	private TrieNode findBestNode(String search, TrieNode bestNode, int position, boolean nodeMode, int minimumDepth) {
		if ((hasWord() && !nodeMode)
				&& ((bestNode == null) || (word.getScore() > bestNode.getWord().getScore())))
			bestNode = this;
		else if (nodeMode && position >= minimumDepth &&
		        ((bestNode == null) || (this.getAverageScore(false) > bestNode.getAverageScore(false))))
		    bestNode = this;

		if (position < search.length()) {
			TrieNode next = this.getChild(search.charAt(position));
			
			if (next != null) {
			    TrieNode temp = next.findBestNode(search, bestNode, position + 1, nodeMode, minimumDepth);
			    if(temp != null)
			        bestNode = temp;
			}
		}

		return bestNode;
	}

	public TrieNode getChild(char c) {
		return children[ScrabbleBag.getIndex(c)];
	}
	
	public String toString() {
	    return word != null ? word.toString() : "WORD_IS_NULL";
	}
}
