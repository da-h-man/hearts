package heart;

public class Player  {

	private String name;
	private Hand hand;
	private int score;
	private int roundScore;
	private boolean human;
	private int playerNumber;
	
	public Player(String playerName, boolean h, int num) {
		this.name=playerName;
		this.human=h;
		this.playerNumber=num;
		
		//TODO: this should not be part of the constructor, but part of creating a new round
		//happens with new round anyway
		//setHand(new Hand());
	}
	
	public void setHand(Hand newHand) {
		this.hand=newHand;	
		for (int i=0;i<this.hand.size();i++) {
			this.hand.getCard(i).setOwner(this.playerNumber);
			this.hand.getCard(i).setPosition(i);
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Hand getHand() {
		return this.hand;
	}
	
	public int getPlayerNumber() {
		return this.playerNumber;
	}
	
	public void increaseScore(int byWhat) {
		this.score+=byWhat;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void setRoundScore(int newScore) {
		this.roundScore=newScore;
	}
	
	public void increaseRoundScore(int byWhat) {
		this.roundScore+=byWhat;
	}
	
	public int getRoundScore() {
		return this.roundScore;
	}
	
	public boolean isHuman() {
		return this.human;
	}
	
}
