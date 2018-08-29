package heart;


import java.io.File;
import java.io.IOException;

import javafx.scene.image.*;

public class Card implements Comparable {
	
	private String[] suitName = {"Clubs", "Diamonds", "Spades", "Hearts"};
	private String[] valueName = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
	private String[] imgName = {"4", "1", "3", "2"};
	private String cardName;
	private int suit;
	private int rank;
	private int cost;
	private int sortValue;
	private volatile int owner;
	private volatile int position;
	
	private volatile Image img;
	
	
	public Card(int suit, int value) {
		this.cardName = valueName[value]+" of "+suitName[suit];
		String fileName;
		fileName="../resources/"+imgName[suit]+"_"+(value+1)+".png";
	    this.img = new Image(this.getClass().getResourceAsStream(fileName), 91, 133, false, true);
		this.rank=value;
		this.suit=suit;
		this.sortValue=(suit*13)+value;
		this.cost=0;
		this.owner=0;
		this.position=0;
		if (suit==Constants.hearts) {
			this.cost=1;
		} else if ((suit==Constants.spades)&&(value==Constants.queen)) {
			this.cost=13;
		} 
		
	}
	
     /*  
	public void paintComponent(Graphics g) {
		this.setBounds(this.xLocation,this.yLocation,this.xSize+2, this.ySize+2);
		//g.fillRoundRect(0, 0, this.xSize+2, this.ySize+2, 4, 4);
		g.drawImage(this.getImage(), 1, 1, this.xSize, this.ySize, this);
		System.out.println("Card "+this.cardName+" is being painted.");
		
    }
    */
	
	public String getName() {
		return this.cardName;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public int getSuit() {
		return this.suit;
	}
	
	public int getRank() {
		return this.rank;
	}
	
	public int getSortValue() {
		return this.sortValue;
	}
	
	@Override
	public int compareTo(Object compareCard) {
		
		return (this.getSortValue()-((Card)compareCard).getSortValue());
	}
	
	public Image getImage() {
		return this.img;
	}
	
	/**
	 * returns the number of the player who holds this card.
	 * Possible values are 0-3 for the relevant player
	 * or -1 if the card has no owner i.e. has already been played.
	 */
	public int getOwner() {
		return this.owner;
	}
	
	/**
	 * returns the position of the card within a hand.
	 * A position of -1 indicates the card being in the trick.
	 */
	
	public int getPosition() {
		return this.position;
	}
	
	/**
	 * sets the number of the player who holds this card.
	 * Possible values are 0-3 for the relevant player
	 * or -1 if the card has no owner i.e. has already been played.
	 */
	public void setOwner(int newOwner) {
		this.owner = newOwner;
	}
	
	/**
	 * sets the position of the card within a hand.
	 * A negative position indicates the card being in the trick.
	 */
	
	public void setPosition(int newPosition) {
		this.position = newPosition;
	}
	

}
