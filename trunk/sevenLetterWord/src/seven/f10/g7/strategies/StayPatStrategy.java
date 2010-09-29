package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.f10.g7.ClassyPlayer;
import seven.f10.g7.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;

public class StayPatStrategy extends AbstractStrategy {    
    public StayPatStrategy(ClassyPlayer owner) {
        super(owner);
    }

    public int valuate(Letter letter, double instancesRemaining, String rack,
            ArrayList<PlayerBids> bidHistory) {
        int currentScore = ClassyPlayer.trie.findBestWord(new Word(rack)).getScore();
        int possibleScore = ClassyPlayer.trie.findBestWord(new Word(rack + letter.getAlphabet())).getScore();
        int difference = Math.max((possibleScore - currentScore), 0);
        
        return difference - 1; // -1 means we gain a point if we gain the character!
    }
    
    public double resolve(char letter, String bestWordBefore, String bestWordAfter,
            double combinedLetterToChildrenRatio, double combinedAverageScoreRatio) {
        return 0;
    }
}
