package seven.f10.g3;

public class RackLetter {
	
	public RackLetter(char c, Boolean w){
		
		this.l = c;
		this.want = w;
	}
	
	public char getL() {
		return l;
	}
	
	public void setL(char l) {
		this.l = l;
	}
	public Boolean getWant() {
		return want;
	}
	public void setWant(Boolean want) {
		this.want = want;
	}

	private Boolean want;
	private char l;
}
