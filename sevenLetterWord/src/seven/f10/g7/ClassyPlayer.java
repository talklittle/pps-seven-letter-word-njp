package seven.f10.g7;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import seven.f10.g7.strategies.DeepStrategy;
import seven.f10.g7.strategies.EarlyStrategy;
import seven.f10.g7.strategies.StayPatStrategy;
import seven.f10.g7.strategies.Strategy;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.ScrabbleValues;
import seven.ui.SecretState;

class Opponent {   
    int id;
    int minBid;
    int maxBid;
    int currentScore=100;
    HashMap<Character,Integer> previousBids;
}

public class ClassyPlayer implements Player {
    
    public static TrieNode trie;

    private final Logger logger = Logger.getLogger(this.getClass());

    private int id;
    private int numberOfSecretLetters = -1;
    private int currentScore;
    private int lettersRemaining = -1;
    private Strategy strategy = null;
    private int numberOfPlayers = -1;
    private int sumSpent = 0;
    private String letterRack;
    private ArrayList<PlayerBids> pastBids;
    /* HashMap keeping track of how many letters are remaining */
    private HashMap<Character, Integer> lettersInTheBag;
    private int lastBidAmount;
    private boolean newRound = true;
    
    public ClassyPlayer() {
        currentScore = 100;
        lettersInTheBag = new HashMap<Character, Integer>(
                ScrabbleBag.NUMBER_OF_LETTERS);
    }

    public int getRemainingCount(Character c) {
        
        return this.lettersInTheBag.get(c);
        
    }
    
    public double getApproximateRemainingCount(Character c) {
        
        return  (this.lettersInTheBag.get(c)-((ScrabbleValues.getLetterFrequency(c)/(double)ScrabbleBag.NUMBER_OF_LETTERS)*this.numberOfSecretLetters));
         
    }
    
    @Override
    public void Register() {
        if (trie == null) {
            try {
                ObjectInputStream dObj = new ObjectInputStream(
                        new FileInputStream("./src/seven/f10/g7/dictionary.obj"));
                trie = (TrieNode) dObj.readObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearNewRound(SecretState secretstate,
            ArrayList<String> PlayerList, ArrayList<PlayerBids> PlayerBidList,
            int PlayerID) {
        newRound=false;
        numberOfSecretLetters = secretstate.getSecretLetters().size();
        lettersRemaining = (8 * PlayerList.size()) - numberOfSecretLetters;
        this.numberOfPlayers=PlayerList.size();
        id = PlayerID;
        letterRack = "";
        for (Letter l : secretstate.getSecretLetters())
            letterRack += l.getAlphabet();
        pastBids = PlayerBidList;
        sumSpent = 0;
        
        if(numberOfSecretLetters > 1) {
            strategy = new DeepStrategy(this);
        } else {
            strategy = new EarlyStrategy(this);
        }
        
        Character chr = ScrabbleBag.STARTING_CHARACTER;
        for (int i = 0; i < ScrabbleBag.NUMBER_OF_LETTERS; i++) {
            lettersInTheBag.put(chr, ScrabbleValues.getLetterFrequency(chr));
            chr++;
        }
        int snum = secretstate.getSecretLetters().size();
        for (int i = 0; i < snum; i++) {
            Integer appears = lettersInTheBag.get(secretstate
                    .getSecretLetters().get(i).getAlphabet());
            lettersInTheBag.put(secretstate
                    .getSecretLetters().get(i).getAlphabet(),--appears);
        }
    }

    @Override
    public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,
            int total_rounds, ArrayList<String> PlayerList,
            SecretState secretstate, int PlayerID) {

        /* Called at the start of each round */
        if (newRound) {
            this.clearNewRound(secretstate, PlayerList, PlayerBidList, PlayerID);
            
        } else {
            this.updatePreviousBid(pastBids.get(pastBids.size() - 1));
        }

        int bidAmount = strategy.valuate(bidLetter, getApproximateRemainingCount(bidLetter.getAlphabet()),
                letterRack, PlayerBidList);

        lastBidAmount = bidAmount;
        return bidAmount;
    }

    private void updatePreviousBid(PlayerBids recentBid) {
        /* Keep track that this letter is not available in the bag anymore */
        Integer appears = lettersInTheBag.get(recentBid.getTargetLetter().getAlphabet());
        lettersInTheBag.put(recentBid.getTargetLetter().getAlphabet(),--appears);
        
      //  System.err.println("Appear" +lettersInTheBag.get(recentBid.getTargetLetter().getAlphabet()));

        if (recentBid.getWinnerID() == id) {
            this.currentScore -= recentBid.getWinAmmount();
            this.letterRack += recentBid.getTargetLetter().getAlphabet();
            this.sumSpent += recentBid.getWinAmmount();
            
            if(strategy instanceof EarlyStrategy) {
                strategy = new DeepStrategy(this);
            }
            
            if(lastBidAmount == 0 && strategy instanceof DeepStrategy)
                ((DeepStrategy)strategy).addToWasteTolerance();
            
            if (letterRack.length() - (numberOfSecretLetters / 2) > 7 && trie.findBestWord(new Word(letterRack)).getScore() > 15 ||
                    trie.findBestWord(new Word(letterRack)).length() == 7) {
                strategy = new StayPatStrategy(this);
            }
        } else {
            // Remove from trie infeasible words
        }
        lettersRemaining--;
    }

    @Override
    public String returnWord() {
        updatePreviousBid(pastBids.get(pastBids.size() - 1));
        Word bestWord = trie.findBestWord(new Word(letterRack));
        this.newRound=true;
        return (bestWord != null ? bestWord.toString().toUpperCase() : "");
    }

    @Override
    public void updateScores(ArrayList<Integer> scores) {
             
    }
}
