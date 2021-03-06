package heart;


import java.io.File;
import java.io.IOException;

import javafx.scene.image.*;

public class Card implements Comparable {
	
	//private String[] suitName = {"Clubs", "Diamonds", "Spades", "Hearts"};
	//private String[] valueName = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
	//private String[] imgName = {"4", "1", "3", "2"};
	private String cardName;
	private int suit;
	private int rank;
	private int cost;
	private int sortValue;
	private volatile int owner;
	private volatile int position;
	private int originalPosition;
	private boolean selectedForTrade;
	
	private volatile Image img;
	
	
	public Card(int theSuit, int value) {
		this.cardName = Constants.VALUE_NAME[value]+" of "+Constants.SUIT_NAME[theSuit];
		String fileName = "../resources/"+Constants.IMG_NAME[theSuit]+"_"+(value+1)+".png";
	    this.img = new Image(this.getClass().getResourceAsStream(fileName), 91, 133, false, true);
		this.rank = value;
		this.suit = theSuit;
		this.sortValue = (theSuit*13)+value;
		this.cost = 0;
		this.owner = 0;
		this.position = 0;
		this.originalPosition = 0;
		this.selectedForTrade = false;
		if (theSuit==Constants.HEARTS) {
			cost = 1;
		} else if ((theSuit==Constants.SPADES)&&(value==Constants.QUEEN)) {
			cost = 13;
		} 
		
	}
	
	public boolean isSelectedForTrade() {
		return selectedForTrade;
	}

	public void setSelectedForTrade(boolean selectedForTrade) {
		this.selectedForTrade = selectedForTrade;
	}

	public String getName() {
		return this.cardName;
	}
	
	public String toString() {
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
	
	public int getOriginalPosition() {
		return this.originalPosition;
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
	
	public void setOriginalPosition(int newPosition) {
		this.originalPosition = newPosition;
	}
	

}
