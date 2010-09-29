package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.ui.Letter;
import seven.ui.PlayerBids;

/**
 * A general interface for a player strategy.
 * @author zts2101
 */
public interface Strategy {
    public int valuate(Letter letter, double instancesRemaining,
            String rack, ArrayList<PlayerBids> bidHistory);
    
    public double resolve(char letter, String bestWordBefore, String bestWordAfter,
            double combinedLetterToChildrenRatio, double combinedAverageScoreRatio);
}
