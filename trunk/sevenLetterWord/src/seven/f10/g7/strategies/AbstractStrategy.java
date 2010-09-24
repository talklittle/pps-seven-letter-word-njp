package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.ui.Letter;
import seven.ui.PlayerBids;

public abstract class AbstractStrategy implements Strategy {

    @Override
    public abstract int valuate(Letter letter, int instancesRemaining, String rack,
            ArrayList<PlayerBids> bidHistory);
    
    protected double calculateCompetitionMultiplier(ArrayList<PlayerBids> bidHistory) {
        return 1.0;
    }
}
