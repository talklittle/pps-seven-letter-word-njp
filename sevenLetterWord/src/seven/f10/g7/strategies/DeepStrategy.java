package seven.f10.g7.strategies;

import java.util.ArrayList;

import seven.f10.g7.ClassyPlayer;
import seven.f10.g7.ScrabbleBag;
import seven.f10.g7.TrieNode;
import seven.f10.g7.Word;
import seven.ui.Letter;
import seven.ui.PlayerBids;

public class DeepStrategy implements Strategy {
    public static final int STANDARD_WASTE_TOLERANCE = 1;
    public static final double SCARCITY_FEAR = 0.75; 
    public static final double NECESSITY_FEAR = 0.5;
    public static final double MAXIMUM_LOVE = 1.1;
    public static final double DESIRE_THRESHOLD = 0.5;
    public static final double INERTIA = 1.1;
    public static final int MAXIMUM_BID_VALUE = 12;
    
    private int wasteTolerance = -1;
    
    @Override
    public int valuate(Letter letter, int instancesRemaining,
            String rack, ArrayList<PlayerBids> bidHistory) {
        if (wasteTolerance == -1) {
            // Allow us to waste up to (2 + the number of secret letters) letters.
            wasteTolerance = rack.length() + STANDARD_WASTE_TOLERANCE;
        }
        
        Word before = new Word(rack);
        Word after = new Word(rack + letter.getAlphabet());
        int reqLet = Math.max(rack.length() - wasteTolerance, 1);
        TrieNode bestNodeBefore = ClassyPlayer.trie.findBestAverageScore(before, reqLet);
        Word bestWordBefore = ClassyPlayer.trie.findBestWord(before);
        TrieNode bestNodeAfter = ClassyPlayer.trie.findBestAverageScore(after, reqLet);
        Word bestWordAfter = ClassyPlayer.trie.findBestWord(after);
        
        int bid = MAXIMUM_BID_VALUE;
        
        double wordCompletionScore = getWordCompletionScore(bestWordBefore, bestWordAfter);
        if (wordCompletionScore != 1.0) {
            double necessityScore = getLetterNecessityScore(letter.getAlphabet(), bestNodeBefore);
            //TODO: Make this responsible coding. It should consider permutations instead.
            if (necessityScore == Double.NaN && bestWordBefore.length() < 5) {
                necessityScore = -0.1;
            }
            double scarcityScore = (necessityScore > DESIRE_THRESHOLD ? getScarcityScore(instancesRemaining) : 0);
            double nodeQualityScore = getNodeQualityScore(bestNodeBefore, bestNodeAfter);
        
            bid = (int) (MAXIMUM_BID_VALUE * ((wordCompletionScore + necessityScore + nodeQualityScore + scarcityScore) / 4));
            
            if (ScrabbleBag.debug) {
               System.err.println("=========== Bidding Report ===========");
               System.err.println("For adding "+letter.getAlphabet()+" to rack "+rack);
               System.err.println("Best Word Before:\t"+bestWordBefore.toString());
               System.err.println("Best Word After:\t"+bestWordAfter.toString());
               System.err.println("Word completion score:\t"+wordCompletionScore);
               System.err.println("\nNecessity score:\t"+necessityScore);
               System.err.println("Scarcity Score:\t"+scarcityScore);
               System.err.println("\nNode Quality Score:\t"+nodeQualityScore);
               System.err.println("\nFinal Bid Value:\t"+bid);
               System.err.println("======================================\n");
            }
        }
        
        return bid;
    }
    
    public void addToWasteTolerance() {
        wasteTolerance++;
    }
    
    /**
     * Scores the letter based on its ability to complete a word.
     * @param letter
     * @param rack
     * @return 0.4 if it doesn't help, 0.6 if it improves a word, 0.75 if it newly completes, and 1.0 if it completes a 7 letter word. 
     */
    private double getWordCompletionScore(Word bestWordBefore, Word bestWordAfter) {
        if (bestWordBefore != null && bestWordBefore.length() > 0) {
            if (bestWordAfter.length() == 7 && bestWordBefore.length() != 7) return 1.0;
            if (bestWordAfter.getScore() > bestWordBefore.getScore()) return 0.6;
            return 0.4;
        } else if (bestWordAfter != null && bestWordAfter.length() > 0) {
            return 0.75;
        }
        return 0;
    }
    
    private double getLetterNecessityScore(char c, TrieNode bestNodeBefore) {
        return (bestNodeBefore == null ? NECESSITY_FEAR : (NECESSITY_FEAR + bestNodeBefore.getCharacterCountToWordCountRatio(c)));
    }
    
    private double getNodeQualityScore(TrieNode bestNodeBefore, TrieNode bestNodeAfter) {
        if (bestNodeBefore != null && bestNodeAfter != null) {
            double afterScore = (bestNodeAfter.getAverageScore(true) > 0 ? bestNodeAfter.getAverageScore(true) : 0.01);
            double beforeScore = (bestNodeBefore.getAverageScore(true) > 0 ? bestNodeBefore.getAverageScore(true) : 0.01);
            if(beforeScore != afterScore) {
                double ratio = afterScore / (beforeScore * INERTIA);
                return Math.pow((ratio > MAXIMUM_LOVE ? MAXIMUM_LOVE : ratio), 2);
            }
        }
        return SCARCITY_FEAR;
    }
    
    private double getScarcityScore(int instancesRemaining) {
        return (SCARCITY_FEAR / instancesRemaining);
    }
}
