package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.ui.Letter;
import seven.ui.PlayerBids;

/**
 * A general interface for a player strategy.
 * @author zts2101
 */
public interface Strategy {
    public int valuate(Letter letter, int instancesRemaining,
            String rack, ArrayList<PlayerBids> bidHistory);
}
