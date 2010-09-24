package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.f10.g7.ClassyPlayer;
import seven.f10.g7.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;

public class StayPatStrategy extends AbstractStrategy {
    public static final int CEILING_BID = 30;
    
    public int valuate(Letter letter, int instancesRemaining, String rack,
            ArrayList<PlayerBids> bidHistory) {
        int currentScore = ClassyPlayer.trie.findBestWord(new Word(rack)).getScore();
        int possibleScore = ClassyPlayer.trie.findBestWord(new Word(rack + letter.getAlphabet())).getScore();
        int difference = Math.max((possibleScore - currentScore), 0);
        
        return Math.min(difference - 1, CEILING_BID); // -1 means we gain a point if we gain the character!
    }
}
