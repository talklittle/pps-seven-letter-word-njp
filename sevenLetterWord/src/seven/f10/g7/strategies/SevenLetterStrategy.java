package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.f10.g7.TrieNode;
import seven.f10.g7.ClassyPlayer;
import seven.ui.Letter;
import seven.ui.PlayerBids;

public class SevenLetterStrategy implements Strategy {

    private double cutOff=0.05;
    
    @Override
    public int valuate(Letter letter, int instancesRemaining,
            String rack, ArrayList<PlayerBids> bidHistory) {
        int bidAmount = 1;
        TrieNode baseNode = ClassyPlayer.trie.findNode(rack);
        if(baseNode != null) {
            TrieNode nextNode = baseNode.getChild(letter.getAlphabet());
            if(nextNode != null) {
                int previouslyReachable = baseNode.getNumWords(true);
                int nowReachable = nextNode.getNumWords(true);
                
                //System.err.println("Now reachable " + nowReachable + " prev " + prevReachable);
             
                /*If the letter being bid on restricts significantly*/
                if(((double) nowReachable) / ((double) previouslyReachable) >= this.cutOff) {
                    if(rack.length()<8)
                        bidAmount = (30)/(7-rack.length());
                }
            }
        }
        return bidAmount;
    }

}
