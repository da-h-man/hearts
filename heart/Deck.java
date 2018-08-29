package heart;
import java.util.*;

public class Deck {
	
	//variables
	private ArrayList<Card> deck = new ArrayList<Card>(52);
	private int cardPointer;

	//constructor
    public Deck() {
    	this.cardPointer=0;
        for ( int suit = 0; suit < 4; suit++ ) {
            for ( int value = 0; value < 13; value++ ) {
                deck.add(new Card(suit, value));
                cardPointer++;
            }
        }
    	this.shuffle();
    }

    /**
     * Put all the used cards back into the deck,
     * and shuffle it into a random order.
     */
    public void shuffle() {
    	
    	for ( int i = deck.size()-1; i > 0; i-- ) {
            int rand = (int)(Math.random()*(i+1));
            Card temp = deck.get(i);
            deck.set(i,deck.get(rand));
            deck.set(rand, temp);
        }
    	this.cardPointer=deck.size()-1;
    }


    /**
     * Deals one card from the deck and returns it.
     * @throws IllegalStateException if no more cards are left.
     */
    public Card getCard() {
    	Card nextCard = deck.get(cardPointer);
    	cardPointer--;
    	return nextCard;
    }
    
    public Card getCard(int i) {
    	return this.deck.get(i);
    }
    
    public int getNumberOfCards() {
    	return this.deck.size();
    }
    
    public Card getSpecificCard(int suit, int rank) {
    	
    	for (int i=0; i<this.deck.size();i++) {
    		if ((this.deck.get(i).getSuit()==suit)&&(this.deck.get(i).getRank()==rank)) return this.deck.get(i);
    	}
    	
    	return null;
    	
    }
	
}
