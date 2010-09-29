package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.f10.g7.ClassyPlayer;
import seven.f10.g7.ScrabbleBag;
import seven.f10.g7.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;

public class DeepStrategy extends AbstractStrategy {
    public DeepStrategy(ClassyPlayer owner) {
        super(owner);
    }

    public static final int STANDARD_WASTE_TOLERANCE = 1;
    public static final double SCARCITY_FEAR = 0.75; 
    public static final double NECESSITY_FEAR = 0.5;
    public static final double MAXIMUM_LOVE = 1.1;
    public static final double DESIRE_THRESHOLD = 0.6;
    public static final int MAXIMUM_BID_VALUE = 18;
    public static final double WANTONNESS = 0.4;
    
    private int wasteTolerance = -1;
    
    @Override
    public int valuate(Letter letter, double instancesRemaining,
            String rack, ArrayList<PlayerBids> bidHistory) {
        if (wasteTolerance == -1) {
            // Allow us to waste up to (2 + the number of secret letters) letters.
            wasteTolerance = (int)(rack.length()/(2)) + STANDARD_WASTE_TOLERANCE;
        }
        else
        {
            //TODO
        }
                
        int bid = (int) (MAXIMUM_BID_VALUE * ClassyPlayer.trie.calculateBid(this, rack, wasteTolerance, letter.getAlphabet()));
        
        return bid;
    }
    
    @Override
    public double resolve(char letter, String bestWordBefore, String bestWordAfter,
            double combinedLetterToChildrenRatio, double combinedAverageScoreRatio) {
        double instancesOfLetterRemaining = owner.getApproximateRemainingCount(letter);
        double wordCompletionScore = getWordCompletionScore(bestWordBefore, bestWordAfter);
        
        if(ScrabbleBag.debug)
        {
            System.err.println("BIDDING ON: " + letter);
            System.err.println("instancesOfLetterRemaining: " + instancesOfLetterRemaining);
            System.err.println("wordCompletionScore: " + wordCompletionScore);
        }
        
        double letterQualityScore = getLetterQualityScore(combinedLetterToChildrenRatio);
        double scorePotentialScore = getScorePotentialScore(combinedAverageScoreRatio);
        if(ScrabbleBag.debug)
        {
            System.err.println("letterQualityScore: " + letterQualityScore);
            System.err.println("scorePotentialScore: " + scorePotentialScore);
        }
        
        double scarcityScore = 0;
        if(scorePotentialScore >= DESIRE_THRESHOLD || letterQualityScore >= DESIRE_THRESHOLD)
            scarcityScore = getScarcityScore(instancesOfLetterRemaining);
        
        if(ScrabbleBag.debug)
        {
            System.err.println("scarcityScore: " + scarcityScore);
            System.err.println("returning: " + (wordCompletionScore + letterQualityScore + scorePotentialScore + scarcityScore) / 4);
            System.err.println();
        }
        
        return getWantonnessScore() * (wordCompletionScore + letterQualityScore + scorePotentialScore + scarcityScore) / 4;
    }
    
    private double getWantonnessScore() {
        return Math.max(wasteTolerance, 2) * WANTONNESS;
    }
    
    public void addToWasteTolerance() {
        wasteTolerance++;
    }
    
    private static double getScorePotentialScore(double combinedAverageScoreRatio) {
        double adjustedScore = Math.pow(combinedAverageScoreRatio, 2);
        return (adjustedScore > MAXIMUM_LOVE ? MAXIMUM_LOVE : adjustedScore);
    }
    
    private static double getLetterQualityScore(double combinedLetterToChildrenRatio) {
        return NECESSITY_FEAR + combinedLetterToChildrenRatio;
    }
    
    /**
     * Scores the letter based on its ability to complete a word.
     * @param letter
     * @param rack
     * @return 0.4 if it doesn't help, 0.6 if it improves a word, 0.75 if it newly completes, and 1.0 if it completes a 7 letter word. 
     */
    private static double getWordCompletionScore(String bestWordBefore, String bestWordAfter) {
        if (bestWordBefore != null && bestWordBefore.length() > 0) {
            if (bestWordAfter.length() == 7 && bestWordBefore.length() != 7) return 1.0;
            if (Word.getScore(bestWordAfter) > Word.getScore(bestWordBefore)) return 0.6;
            return 0.4;
        } else if (bestWordAfter != null && bestWordAfter.length() > 0) {
            return 0.75;
        }
        return 0;
    }
    
    private static double getScarcityScore(double instancesRemaining) {
        return (SCARCITY_FEAR / instancesRemaining);
    }
}
