package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.ui.PlayerBids;
import seven.f10.g7.ClassyPlayer;

public abstract class AbstractStrategy implements Strategy {
    protected ClassyPlayer owner;
    
    public AbstractStrategy(ClassyPlayer owner) {
        this.owner = owner;
    }
    
    protected double calculateCompetitionMultiplier(ArrayList<PlayerBids> bidHistory) {
        return 1.0;
    }
}
