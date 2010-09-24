Group 3:

Our player is relatively simple at this point, but uses some strategies which we wish to 
expand on later into the project.  In its bidding, the player is conservative, and uses the 
bidding history of fellow players in each round to calculate a proper "high" winning bid and 
a "low" passing bid.  The player always bids the low bid unless they determine that the next
letter will either make a word or bring them one letter away from a word. 


The player initially loads the dictionary by way of an alphabetized 
trie tree. Each word in the dictionary has had its letters put in alphabetical order, so 
for example, the word "mill" can be found in our trie by traversing the word "illm".  This 
method allows us for quick comparison of our rack contents to the possible words we can 
create at that moment.

Files submitted:
OurPlayer.java - main player file
alpha-smallwordlist.txt - the dictionary that our player uses formatted alphabatized word, word
TrieTree.java - Our trie tree
TrieNode.java - Trie Node

EndTester.java - program to debug results
Manipulate Words - program toalphabatize letters in word to make our input textfile
TestTrie.java - program to test the trie structure