package seven.f10.g4;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import seven.ui.Letter;
import seven.ui.Player;
import seven.ui.PlayerBids;
import seven.ui.SecretState;

public class G4Player implements Player {
	private ArrayList<Word> dictionary;
	private ArrayList<Letter> rack;
	private ArrayList<PlayerBids> history;
	private Integer points;
	private int id;
	@Override
	public void Register(){
		dictionary=new ArrayList<Word>();
		rack=new ArrayList<Letter>();
		points=100;
		String line=null;
		try {
			BufferedReader reader=new BufferedReader(new FileReader("src/seven/g1/super-small-wordlist.txt"));
			try {
				while((line=reader.readLine())!=null){
					dictionary.add(new Word(line.trim()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void populateRack(SecretState secretState){
		for(Letter letter:secretState.getSecretLetters()){
			rack.add(letter);
		}
		
	}
	@Override
	public int Bid(Letter bidLetter, ArrayList<PlayerBids> PlayerBidList,int total_rounds, ArrayList<String> PlayerList,SecretState secretState, int PlayerID) {
		history=PlayerBidList;
		if(rack.isEmpty()){
			populateRack(secretState);
			id=PlayerID;
		}
		else{
			if(history.size()>0){
				checkIfWeWon(history.get(history.size()-1));
			}
		}
		return 0;
	}
    
	private void checkIfWeWon(PlayerBids lastBid) {
		if(id==lastBid.getWinnerID()){   
			rack.add(lastBid.getTargetLetter());
		}
	}
	@Override
	public String returnWord() {
		return null;
	}

}
