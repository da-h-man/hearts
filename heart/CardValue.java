package heart;

public class CardValue implements Comparable {
	
	private Card card;
	private int value;
	
	
	public CardValue(Card theCard, int theValue) {
		this.card=theCard;
		this.value=theValue;
	}
	
	public int getValue() {
		return this.value;
	}
	
	@Override
	public int compareTo(Object compareCard) {
		
		return (this.value-((CardValue)compareCard).getValue());
	}

}
