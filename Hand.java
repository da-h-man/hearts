package heart;

import java.util.*;

public class Hand {
	
	
	private ArrayList<Card> cards;
	private float[] suitValue = new float[4];
	private int suitValuesCalculatedBasedOnCardsLeft = 0;
	
	public Hand(Deck deck) {
		this.cards=new ArrayList<Card>(Constants.CARDS_IN_A_FULL_HAND);
		this.getNewHand(deck);
	}
	
	public Hand(ArrayList<Card> theCards) {
		this.cards=new ArrayList<Card>(theCards.size());
		for (int i=0;i<theCards.size();i++) {
			this.cards.add(theCards.get(i));
		}
	}
	
	public Hand(Hand h) {
		this.cards=new ArrayList<Card>(h.size());
		for (int i=0;i<h.size();i++) {
			this.cards.add(h.getCard(i));
		}
	}
	
	public Hand() {
		this.cards=new ArrayList<Card>(52);
	}
	
	public void invalidateCalculations() {
		this.suitValuesCalculatedBasedOnCardsLeft = 0;
	}
	
	
	/*
	 * ***********************
	 *                       *
	 *    SUPPORT METHODS    *
	 *                       *
	 * ***********************
	 */
	
	public ArrayList<Card> getHand() {
		return (ArrayList<Card>)this.cards.clone();
	}
	
	public Card getCard(int index) {
		return this.cards.get(index);
	}
	
	public void setCard(int index, Card card) {
		this.cards.set(index, card);
		this.invalidateCalculations();
	}
	
	public void getNewHand(Deck deck) {
		
		for (int i=0; i<Constants.CARDS_IN_A_FULL_HAND; i++) {
			this.cards.add(deck.getCard());
		}
		this.invalidateCalculations();
	}
	
	public void add(Card card) {
		this.cards.add(card);
		this.invalidateCalculations();
	}
	
	
	public void addHand(Hand cards) {
		if (cards==null) return;
		for (int i=0; i < cards.size(); i++) {
			this.add(cards.getCard(i));
		}
		this.invalidateCalculations();
	}
	
	
	public void remove(Card card) {
		this.cards.remove(card);
		this.invalidateCalculations();
	}
	
	public void remove(int i) {
		this.cards.remove(i);
		this.invalidateCalculations();
	}
	
	public void removeCard(int suit, int rank) {
		for (int i=0;i<this.size();i++) {
			if ((this.getCard(i).getRank()==rank)&&(this.getCard(i).getSuit()==suit)) {
				this.remove(this.getCard(i));
			}
		}
		this.invalidateCalculations();
	}
	
	public void removeAll() {
		for (int i = 0; i < this.size(); i++) {
			this.remove(0);
		}
		this.invalidateCalculations();
	}
	
	
	public void removeCards(Hand cards) {
		if (cards==null) return;
		for (int i=0; i < cards.size(); i++) {
			this.remove(cards.getCard(i));
		}
		this.invalidateCalculations();
	}
	
	
	public boolean giveTo(int suit, int rank, Hand recipient) {
		if (this.hasCard(suit,  rank)) {
			Card card = this.getCard(suit, rank);
			this.remove(card);
			recipient.add(card);
			this.invalidateCalculations();
			recipient.invalidateCalculations();
			return true;
		} else return false;
	}
	
	public void giveTo(Player p, Hand h) {
		
		if (h==null) return;
		for (int i=0; i < h.size(); i++) {
			Card c = h.getCard(i);
			p.getHand().add(c);
			this.remove(c);
			c.setOwner(p.getPlayerNumber());
		}
		
	}

	
	public Card getCard(int suit, int rank) {
		
		for (int i=0; i<this.cards.size();i++) {
			if ((this.cards.get(i).getSuit()==suit)&&(this.cards.get(i).getRank()==rank)) {
				return this.cards.get(i);
			}
		}
		return null;
	}
	
	public Card[] toArray() {
		Card[] cards = new Card[this.size()];
		for (int i=0; i<this.cards.size();i++) {
			cards[i] = this.getCard(i);
			}
		return cards;
	}
	
	
	public String toString() {
		String answer = "";
		for (int i = 0; i<this.cards.size(); i++)  {
			Card tempCard = this.cards.get(i);
			answer += tempCard.getName()+", ";
		}
		if (answer.length()>0) answer = answer.substring(0,  answer.length()-3);
		return answer;
	}
	
	
	
	
	public void sortHand() {
		Collections.sort(this.cards);
		for (int i=0;i<this.cards.size();i++) {
			Card card = this.cards.get(i);
			card.setPosition(i);
			card.setOriginalPosition(i);
			card.setSelectedForTrade(false);
		}
	}
	
	public int size() {
		return this.cards.size();
	}
	
	public boolean hasSuit(int suit) {
		
		boolean returnValue=false;
		for (int index=0; index<this.cards.size(); index++) {
			if (this.cards.get(index).getSuit()==suit) {
				returnValue=true;
				break;
			}
		}
		return returnValue;
	}
	
	
	public int getSizeOfSuit(int suit) {
		int returnValue = 0;
		for (int index=0; index < this.cards.size(); index++) {
			if (this.cards.get(index).getSuit()==suit) {
				returnValue++;
			}
		}
		return returnValue;
	}
	
	
		
	public boolean hasCard(int suit, int rank) {
		return (this.getCard(suit, rank)!=null);
	}
	
	public boolean hasCard(Card theCard) {
		if (theCard == null) return false;
		else return this.hasCard(theCard.getSuit(), theCard.getRank());
	}
	
	public Card getQoS() {
		return this.getCard(Constants.SPADES, Constants.QUEEN);
	}
	
	
	public Hand getCardsOfSuit(int suit) {
		Hand answerList = new Hand();
		for (int index=0; index<this.cards.size(); index++) {
			//System.out.println("getCardsOfSuit for suit "+suit+": Total index="+this.cards.size()+", currently at: "+index);
			if (this.cards.get(index).getSuit()==suit) {
				answerList.add(this.cards.get(index));
			}
		}
		//System.out.println("GetCardsOfSuit: Found a total of "+answerList.size()+" cards for suit "+Constants.suit[suit]);
		return answerList;
	}
	
	
	
	public Hand getPointCards() {
		
		Hand resultHand = new Hand();
		
		for (int index=0; index < this.cards.size(); index++) {
			//System.out.println("getCardsOfSuit for suit "+suit+": Total index="+this.cards.size()+", currently at: "+index);
			if (this.cards.get(index).getCost() > 0) {
				resultHand.add(this.cards.get(index));
			}
		}
		
		return resultHand;
	}
	
	
	
	public int getCardsBelow(Card theCardInQuestion) {
		int answer=0;
		for (int i=0;i<this.cards.size();i++) {
			if ((this.cards.get(i).getSuit()==theCardInQuestion.getSuit())&&(this.cards.get(i).getRank()<theCardInQuestion.getRank())) answer++;
		}
		return answer;
	}
	
	
	public int getCardsBelow(int rank) {
		int answer=0;
		for (int i=0;i<this.size();i++) {
			if (this.getCard(i).getRank()<rank) answer++;
		}
		return answer;
	}
	
	
	public int getCardsAbove(Card theCardInQuestion) {
		int answer=0;
		for (int i=0;i<this.cards.size();i++) {
			if ((this.cards.get(i).getSuit()==theCardInQuestion.getSuit())&&(this.cards.get(i).getRank()>theCardInQuestion.getRank())) answer++;
		}
		return answer;
	}
	
	
	public int getCardsAbove(int rank) {
		int answer=0;
		for (int i=0;i<this.size();i++) {
			if (this.getCard(i).getRank()>rank) answer++;
		}
		return answer;
	}
	
	
	public Card getHighestCardBelow(Card theCardInQuestion) {
		int theSuit=theCardInQuestion.getSuit();
		int theRank=theCardInQuestion.getRank();
		return this.getHighestCardBelow(theSuit, theRank);
	}
	
	
	public Card getHighestCardBelow(int theSuit, int theRank) {
		Card answer=null;
		for (int i=0; i<this.cards.size();i++) {
			Card tempCard=this.cards.get(i);
			if (tempCard.getSuit()==theSuit) {
				if (tempCard.getRank()<theRank) {
					if (answer==null) {
						answer=tempCard;
					} else if (answer.getRank()<tempCard.getRank()) answer=tempCard;
				}
			}
		}
		return answer;
	}
	
	
	public Card getHighestCardInSuit(int suit) {
		Card answer=null;
		for (int i=0; i<this.cards.size();i++) {
			Card tempCard=this.cards.get(i);
			if (tempCard.getSuit()==suit) {
				if (answer==null) {
					answer=tempCard;
				} else if (answer.getRank()<tempCard.getRank()) answer=tempCard;
			}
		}
		return answer;
	}
	
	
	public Card getHighestNoCostCardInSuit(int suit) {
		
		if (suit != Constants.SPADES) {
			return this.getHighestCardInSuit(suit);
		}
		
		if (this.getHighestCardInSuit(suit).getRank()!=Constants.QUEEN) {
			return this.getHighestCardInSuit(suit);
		} else {
			if (this.getHighestCardBelow(suit, Constants.QUEEN)!=null)
				return this.getHighestCardBelow(suit, Constants.QUEEN);
			else return this.getHighestCardInSuit(suit);
		}
	}
		
	
	public Card getLowestCardInSuit(int suit) {
		Card answer=null;
		for (int i=0; i<this.cards.size();i++) {
			Card tempCard=this.cards.get(i);
			if (tempCard.getSuit()==suit) {
				if (answer==null) {
					answer=tempCard;
				} else if (answer.getRank()>tempCard.getRank()) answer=tempCard;
			}
		}
		return answer;
	}
	
	
	public int getRankOfLowestCardInSuit(int suit) {
		int answer=Constants.CARDS_IN_A_SUIT;
		for (int i=0; i<this.cards.size();i++) {
			if (this.cards.get(i).getRank()<answer) answer=this.cards.get(i).getRank();
		}
		return answer;
	}
	
	
	public int getSuitWithLeastCards(boolean includeSpades, boolean includeHearts) {
		int currentMin = 13;
		int answer = 0;
		
		int[] cards=new int[] {0,0,0,0};
		
		for (int i=0;i<this.size();i++) {
			cards[this.getCard(i).getSuit()]++;
		}
		
		if (!includeSpades) cards[Constants.SPADES] = 0;
		if (!includeHearts) cards[Constants.HEARTS] = 0;
		
		for (int i=0;i<4;i++) {
			if ((cards[i] < currentMin) && (cards[i] > 0)){
				currentMin = cards[i];
				answer = i;
			}
		}
		System.out.println("Suit with the lowest number of cards: "+answer);
		return answer;
	}
	

	
	
	
	
	
	
	
	/*
	 * ******************************************************
	 *                       								*
	 *    				CARD LOGIC METHODS    				*
	 *                      								*
	 * ******************************************************
	 */
	
	
	
	
	
	
	
	
	/**
	 * 
	 * calculateLossOfTrick returns the chance of the cards being distributed in a way that at least one person
	 * must play a card higher than my card
	 * 
	 */
	
	public float calculateLossOfTrick(Card card, Hand cardsLeft) {
		
		System.out.println("calculating loss of trick for "+card.getName());
		//reduce cardsLeft by own hand
		Hand otherCards = new Hand(cardsLeft);
		otherCards.removeCards(this);
		//get cards that are higher than mine
		int n=otherCards.getCardsAbove(card);
		//get cards that are lower than mine
		int m=otherCards.getCardsBelow(card);
		//get total cards left with other players
		int r=otherCards.size();
		//get total cards left with a single player
		int t=r/3;
		//get number of normal cards
		int k=r-n-m;
		//System.out.println("n: "+n+", m: "+m+", t: "+t+", r: "+r+", k: "+k);
		if (n==0) return 0f;
		if (m==0) return 1.0f;
		//calculate total positive combinations
		long possibilities=0;
		long totalCombos=Constants.combinations(r, t);
		int max=n;
		if (max>t) max=t;
		for (int i=1;i<=max;i++) {
			if (k>=(t-i)) {
				//System.out.println("possibilities for i="+i+": "+(Constants.combinations(n, i)*Constants.combinations(k, t-i)));
				possibilities+=Constants.combinations(n, i)*Constants.combinations(k,t-i);
			} 
		}
		//System.out.println("Possibilities that "+card.getName()+" will go through with "+(n+m)+" cards remaining outside is "+possibilities+" out of a total of "+totalCombos);
		float returnValue = possibilities*3.0f/totalCombos;
		//System.out.println("Which gives a value of "+returnValue);
		//due to the law of Sylvester, we can't just multiply the probability by 3. We need to deduct the probability that 2 people both need to go over.
		if ((m>t)||(n==1)) {
			System.out.println("Finished calculation, as it's not possible that 2 players can go over.");
			System.out.println("Chance of "+card.getName()+" getting lost: "+returnValue);
			return returnValue; //not possible that 2 players are above my card
		}
		//now we need to calculate the chance that 2 players are above my card
		possibilities=0;
		totalCombos=Constants.combinations(r, t)*Constants.combinations((r-t),t);
		//System.out.println("total combinations of cards for 3 players: "+totalCombos);
		max=(n-1);
		if (max>t) max=t;
		for (int i=1;i<=max;i++) {
			int max2=n-i;
			if (max2>t) max2=t;
			for (int j=1;j<=max2;j++) {
				if (((2*t)-i-j)<=k) { //check if this combination leaves enough "normal" cards
					possibilities+=(Constants.combinations(n, i)*Constants.combinations(k,(t-i))*Constants.combinations(n-i,j)*Constants.combinations(k-(t-i),(t-j)));
				}
			}
		}
		//System.out.println("Overall possibilities that 2 people must go over: "+possibilities+" out of "+totalCombos+" total possibilities.");
		returnValue-=possibilities*3.0f/totalCombos;
		System.out.println("Giving a total percentage of "+returnValue+"%");
		return returnValue;
	}
	
	/**
	 * 
	 * getBestCardToLoseOwnership calculates each card's chance of not going through and returns the card with
	 * the highest probability
	 * 
	 * @param cardsLeft cards remaining with the other players
	 * @param includeSpades TRUE will include spade cards in the calculation
	 * @param includeHearts TRUE will include hearts cards in the calculation
	 * @return
	 */
	
	public Card getBestCardToLoseOwnership(Hand cardsLeft, boolean includeSpades, boolean includeHearts) {
		Card returnCard = this.getHand().get(0);
		float cardPercentage = this.calculateLossOfTrick(returnCard, cardsLeft);
		for (int i=1;i<this.getHand().size();i++) {
			Card cardInQuestion = this.getHand().get(i);
			int suit = cardInQuestion.getSuit();
			//only check cards of a suit that is legal
			if ((suit < 2)||((suit==Constants.SPADES)&&(includeSpades))||((suit==Constants.HEARTS)&&(includeHearts))) {
				float tempPercentage = this.calculateLossOfTrick(cardInQuestion, cardsLeft);
				if (tempPercentage>cardPercentage) {
					returnCard=cardInQuestion;
					cardPercentage = tempPercentage;
				}
			}
			
		}
		return returnCard;
	}
	

	
	public float getHandValue(Hand cardsLeft) {
		
		/*
		 * Did not present good results for following situations:
		 * - first trick, doesn't have clubs, has heaps of diamonds, has QoS, still ends up playing 9 of spades.
		 *   Makes sense in terms of reducing the number of tricks made, but doesn't factor in QoS...
		 */
		
		float overallValue = 0f;
		
		for (int suit=0; suit < Constants.SUITS_IN_THE_DECK; suit++) {
			overallValue += this.calculate(suit, cardsLeft, Constants.AVG_POINTS_PER_SUIT);
		}
		
		/*
		if (this.hasCard(Constants.SPADES, Constants.QUEEN)) overallValue-=10;

		for (int suit=0; suit < Constants.SUITS_IN_THE_DECK; suit++) { //check quality of every suit
			if (this.hasSuit(suit)) {
				if (suit != Constants.HEARTS)
					overallValue-=this.getAverageTricksPerSuit(suit, cardsLeft);
				else {
					float points = this.getAverageTricksPerSuit(suit, cardsLeft)*3;
					if (points>cardsLeft.getHearts()) points=cardsLeft.getHearts();
					overallValue-=points;
				}
			} else {
				overallValue+=0.8f;
			}
		}
		*/
		System.out.println("Overall value of this hand: " + overallValue);
		return overallValue;
	}
	
	
	/*
	 * getCardValue is calculating the value of each card based on "points per suit if I have that card" vs. "points per suit if someone else had that card"
	 * There would need to be a different calculation for "...if I have that card" vs. "...if this card is played and out of the game"
	 */
	
	
	public float getCardValue(Card card, Hand cardsLeft) {
		
		
		System.out.println("Calculating card value of " + card.toString());
		
		float overallValue = 0.0f;
		
		if (this.suitValuesCalculatedBasedOnCardsLeft != cardsLeft.size()) {		//the suit values for this specific situation haven't yet been calculated
			System.out.println("Need to calculate value of suits.");
			for (int suit=0; suit < Constants.SUITS_IN_THE_DECK; suit++) {		//fill each suit with a suit value
				suitValue[suit] = this.calculate(suit, cardsLeft, Constants.AVG_POINTS_PER_SUIT);
				overallValue += suitValue[suit];
			}
			this.suitValuesCalculatedBasedOnCardsLeft = cardsLeft.size();
		} else {			//we can use the existing suit values that were previously calculated
			overallValue = this.suitValue[0] + this.suitValue[1] + this.suitValue[2] + this.suitValue[3];
		}
		
		Hand newHand = new Hand(this);
		Hand newCardsLeft = new Hand(cardsLeft);
		newHand.remove(card);
		newCardsLeft.add(card);
		int relevantSuit = card.getSuit();
		overallValue = overallValue - this.suitValue[relevantSuit] + newHand.calculate(relevantSuit, newCardsLeft, Constants.AVG_POINTS_PER_SUIT);
		
		System.out.println("Card value is " + overallValue);
		return overallValue;
		
	}
	
	/*
	 * gets the cost of the most expensive card in the hand and removes that card from play if removeFromHand = true
	 */
	
	public int getCostOfMostExpensiveCard(boolean removeFromHand) {
		
		Card answer=null;
		int maxCost = 0;
		
		for (int i=0; i < this.cards.size(); i++) {
			Card tempCard = this.cards.get(i);
			if (tempCard.getCost() > maxCost) {
				answer=tempCard;
				maxCost = tempCard.getCost();
			} 
		}
		
		if (removeFromHand) this.remove(answer);
		return maxCost;
		
	}
	
	
	
	/**
	 * Returns the average number of tricks made by playing all cards of a specific suit, based on the cards of the hand itself and the cards that are remaining in play.
	 * The value is obtained by a static Hashtable stored in the class Probabilities.
	 * @param suit: the suit that is being checked.
	 * @param cardsLeft: the cards left in play.
	 * @return the probability in percent, i.e. a value between 0 and 1.
	 */
	
	public float getAverageTricksPerSuit(int suit, Hand cardsLeft) {
		
		return this.calculate(suit, cardsLeft, Constants.AVG_TRICKS_PER_SUIT);

	}
	
	
	
	public float calculate(int suit, Hand cardsLeftInPlay, int target) {
		
		
		Hand mySuit					= this.getCardsOfSuit(suit);			// my cards of that suit
		Hand otherCards				= new Hand(cardsLeftInPlay);			// clone cards left
		otherCards.removeCards(this);										// remove all my cards
		Hand costCards				= otherCards.getPointCards();			// all cards remaining in play that have a cost (i.e. all hearts & QoS)
		otherCards 					= otherCards.getCardsOfSuit(suit); 		// reduce to relevant suit
		
		costCards.removeCards(otherCards);		//if we play hearts or spades, the "other cards" need to be removed from the "cost cards"
		
		final int depth				= otherCards.size();
		final int myDepth			= mySuit.size();
		ArrayList<Integer> myCards 	= new ArrayList<Integer>();
		
		for (int i=0; i < myDepth; i++) {
			myCards.add(mySuit.getCard(i).getRank());
		}
		
		Integer[] theRest = new Integer[depth];
		
		for (int i=0; i < depth; i++) {
			theRest[i] = otherCards.getCard(i).getRank();
		}
		
		if (depth==0) {
			System.out.println("No other cards of this suit exist. Returning 0"); //is this always correct? we don't want to be caught in this situation
			return 0;
		}
		if (mySuit.size()==0) {
			System.out.println("Hand doesn't have this suit. Returning 0");
			return 0;
		}
		
		ArrayList[] cards = new ArrayList[3];
		for (int i=0;i<3;i++) cards[i]=new ArrayList<Integer>();
		
		int positiveScore = 0;
		int totalScore = 0;
		//some additional looping logic "LL" reduces number of loops as we don't differentiate between players 1,2,3
		for (int c1=0;c1<1;c1++) {		//LL: we loop the first card just once
			
			if (depth>1) {
				//next card iteration
				for (int c2=0;c2<2;c2++) { //LL: 2nd card only loops to 1
					
					if (depth>2) {
						//next card iteration
						for (int c3=0;c3<3;c3++) {
							if ((c2!=0)||(c3<2)) {			//LL:only execute the 3rd loop if not all previous cards belong to player 1
								if (depth>3) {
									//next card iteration
									for (int c4=0;c4<3;c4++) {
										if((c4<2)||(c2+c3>0)) {		//LL:only execut 3rd loop if not all previous cards belong to 1st player
											if (depth>4) {
												//next card iteration
												for (int c5=0;c5<3;c5++) {
													if ((c5<2)||(c2+c3+c4>0)) {
														if (depth>5) {
															//next card iteration
															for (int c6=0;c6<3;c6++) {
																if ((c6<2)||(c2+c3+c4+c5>0)) {
																	if (depth>6) {
																		//next card iteration
																		for (int c7=0;c7<3;c7++) {
																			if ((c7<2)||(c2+c3+c4+c5+c6>0)) {
																				if (depth>7) {
																					//next card iteration
																					for (int c8=0;c8<3;c8++) {
																						if ((c8<2)||(c2+c3+c4+c5+c6+c7>0)) {
																							if (depth>8) {
																								//next card iteration
																								for (int c9=0;c9<3;c9++) {
																									if ((c9<2)||(c2+c3+c4+c5+c6+c7+c8>0)) {
																										if (depth>9) {
																											//next card iteration
																											for (int c10=0;c10<3;c10++) {
																												if ((c10<2)||(c2+c3+c4+c5+c6+c7+c8+c9>0)) {
																													if (depth>10) {
																														//next card iteration
																														for (int c11=0;c11<3;c11++) {
																															if ((c11>2)||(c2+c3+c4+c5+c6+c7+c8+c9+c10>0)) {
																																if (depth>11) {
																																	//next card iteration
																																	for (int c12=0;c12<3;c12++) {
																																		cards[c1].add(theRest[0]);
																																		cards[c2].add(theRest[1]);
																																		cards[c3].add(theRest[2]);
																																		cards[c4].add(theRest[3]);
																																		cards[c5].add(theRest[4]);
																																		cards[c6].add(theRest[5]);
																																		cards[c7].add(theRest[6]);
																																		cards[c8].add(theRest[7]);
																																		cards[c9].add(theRest[8]);
																																		cards[c10].add(theRest[9]);
																																		cards[c11].add(theRest[10]);
																																		cards[c12].add(theRest[11]);
																																		//run the test game
																																		totalScore++;
																																		positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																																		cards[0].clear();
																																		cards[1].clear();
																																		cards[2].clear();
																																	}
																																} else {
																																	//run the test game
																																	cards[c1].add(theRest[0]);
																																	cards[c2].add(theRest[1]);
																																	cards[c3].add(theRest[2]);
																																	cards[c4].add(theRest[3]);
																																	cards[c5].add(theRest[4]);
																																	cards[c6].add(theRest[5]);
																																	cards[c7].add(theRest[6]);
																																	cards[c8].add(theRest[7]);
																																	cards[c9].add(theRest[8]);
																																	cards[c10].add(theRest[9]);
																																	cards[c11].add(theRest[10]);
																																	//run the test game
																																	totalScore++;
																																	positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																																	cards[0].clear();
																																	cards[1].clear();
																																	cards[2].clear();
																																}
																															}
																														}
																													} else {
																														cards[c1].add(theRest[0]);
																														cards[c2].add(theRest[1]);
																														cards[c3].add(theRest[2]);
																														cards[c4].add(theRest[3]);
																														cards[c5].add(theRest[4]);
																														cards[c6].add(theRest[5]);
																														cards[c7].add(theRest[6]);
																														cards[c8].add(theRest[7]);
																														cards[c9].add(theRest[8]);
																														cards[c10].add(theRest[9]);
																														//run the test game
																														totalScore++;
																														positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																														cards[0].clear();
																														cards[1].clear();
																														cards[2].clear();
																													}
																												}
																											}
																										} else {
																											cards[c1].add(theRest[0]);
																											cards[c2].add(theRest[1]);
																											cards[c3].add(theRest[2]);
																											cards[c4].add(theRest[3]);
																											cards[c5].add(theRest[4]);
																											cards[c6].add(theRest[5]);
																											cards[c7].add(theRest[6]);
																											cards[c8].add(theRest[7]);
																											cards[c9].add(theRest[8]);
																											//run the test game
																											totalScore++;
																											positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																											cards[0].clear();
																											cards[1].clear();
																											cards[2].clear();
																										}
																									}
																								}
																							} else {
																								cards[c1].add(theRest[0]);
																								cards[c2].add(theRest[1]);
																								cards[c3].add(theRest[2]);
																								cards[c4].add(theRest[3]);
																								cards[c5].add(theRest[4]);
																								cards[c6].add(theRest[5]);
																								cards[c7].add(theRest[6]);
																								cards[c8].add(theRest[7]);
																								//run the test game
																								totalScore++;
																								positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																								cards[0].clear();
																								cards[1].clear();
																								cards[2].clear();
																							}
																						}
																					}
																				} else {
																					cards[c1].add(theRest[0]);
																					cards[c2].add(theRest[1]);
																					cards[c3].add(theRest[2]);
																					cards[c4].add(theRest[3]);
																					cards[c5].add(theRest[4]);
																					cards[c6].add(theRest[5]);
																					cards[c7].add(theRest[6]);
																					//run the test game
																					totalScore++;
																					positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																					cards[0].clear();
																					cards[1].clear();
																					cards[2].clear();
																				}
																			}
																		}
																	} else {
																		cards[c1].add(theRest[0]);
																		cards[c2].add(theRest[1]);
																		cards[c3].add(theRest[2]);
																		cards[c4].add(theRest[3]);
																		cards[c5].add(theRest[4]);
																		cards[c6].add(theRest[5]);
																		//run the test game
																		totalScore++;
																		positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
																		cards[0].clear();
																		cards[1].clear();
																		cards[2].clear();
																	}
																}
															}
														} else {
															cards[c1].add(theRest[0]);
															cards[c2].add(theRest[1]);
															cards[c3].add(theRest[2]);
															cards[c4].add(theRest[3]);
															cards[c5].add(theRest[4]);
															//run the test game
															totalScore++;
															positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
															cards[0].clear();
															cards[1].clear();
															cards[2].clear();
														}
													}
												}
											} else {
												cards[c1].add(theRest[0]);
												cards[c2].add(theRest[1]);
												cards[c3].add(theRest[2]);
												cards[c4].add(theRest[3]);
												//run the test game
												totalScore++;
												positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
												cards[0].clear();
												cards[1].clear();
												cards[2].clear();
											}
										}
									}
								} else {
									cards[c1].add(theRest[0]);
									cards[c2].add(theRest[1]);
									cards[c3].add(theRest[2]);
									//run the test game
									totalScore++;
									positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
									cards[0].clear();
									cards[1].clear();
									cards[2].clear();
								}
							}
						}
					} else {
						cards[c1].add(theRest[0]);
						cards[c2].add(theRest[1]);
						//run the test game
						totalScore++;
						positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
						cards[0].clear();
						cards[1].clear();
						cards[2].clear();
					}
				}
			} else {
				cards[c1].add(theRest[0]);
				//run the test game
				totalScore++;
				positiveScore += this.getSingleScore(suit, myCards, cards, costCards, target);
				cards[0].clear();
				cards[1].clear();
				cards[2].clear();
			}
		}
		float answer=(float)positiveScore/totalScore;
		System.out.println("Average score for calculation " + target + ": " + answer);
		return answer;
	}
	
	
	
	public float getSingleScore(int suit, ArrayList h, ArrayList<Integer>[] p, Hand costCards, int target) {
		
		switch (target) {
		
		case Constants.AVG_POINTS_PER_SUIT:
			return this.runTestGame(suit,  h,  p,  costCards);
			
		case Constants.AVG_TRICKS_PER_SUIT:
			return this.runTestGame(suit,  h,  p,  null);
			
		case Constants.AVG_CARDS_LEFT_AFTER_BLANK:
			int cardsLeftThisTurn = 0;
			int myDepth = h.size();
			
			for (int player=0; player < 3; player++) {
				if (p[player].size() > myDepth) cardsLeftThisTurn += p[player].size() - myDepth;
				}
			
			return cardsLeftThisTurn;
			
		case Constants.CHANCE_OF_ALL_CARDS_COMING_INTO_PLAY:
			int playerLong=0;
			
			for (int player=0; player < 3; player++) {
				if (p[player].size() >= h.size()) playerLong=1;
			}				
			
			return playerLong;
	
		case Constants.AVG_PLAYERS_HAVING_SUIT: //this calculates players NOT having suit?
			int hasSuit = 0;
			
			for (int player=0; player < 3; player++) {
				if (p[player].size()==0) hasSuit++;
			}
			
			return hasSuit;
			
		default: return 0f;
		}
	}
	

	
	private float runTestGame(int suit, ArrayList h, ArrayList<Integer>[] p, Hand costCards) {
	
		ArrayList<Integer> mySuit 	= (ArrayList<Integer>)h.clone();
		Collections.sort(mySuit);
		ArrayList[]players 			= new ArrayList[4];
		Hand todaysCostCards		= new Hand(costCards);
		
		for (int i=0;i<3;i++) {
			players[i]=(ArrayList<Integer>)p[i].clone();
			Collections.sort(players[i]);
		}
	
		int tricksMade = 0;
		int pointsMade = 0;
		
		//System.out.println("Doing a trial game for suit " + suit);
		//System.out.println("Cards:");
		//System.out.print("Me: ");
		//for (int i=0; i< mySuit.size();i++) System.out.print(Constants.VALUE_NAME[mySuit.get(i)] + " ");
		for (int i=0; i< 3; i++) {
			//System.out.println();
			//System.out.print("Player " + i + ": ");
			//for (int j=0; j<players[i].size();j++) System.out.print(Constants.VALUE_NAME[(Integer)players[i].get(j)] + " ");
		}
		//System.out.println();
		//System.out.println("Cost cards: " + todaysCostCards);
		
		//first, we loop through all my cards and let me be 1st player
		for (int i=0; i < mySuit.size(); i++) {
			
			int highestRank				= mySuit.get(i);
			boolean otherPlayersHaveSuit= false;
			boolean doIWin				= true;
			boolean cardsLeft			= false;
			int trickScore 				= 0;
			
			//System.out.println("I play card " + Constants.VALUE_NAME[highestRank]);
			
			if (suit == Constants.HEARTS) trickScore++;
			if ((suit == Constants.SPADES) && (highestRank == Constants.QUEEN)) trickScore += 13;
			
			for (int j=0; j<3;j++) {
				
				int cardToPlay				= -1;
				int highestCardBelow		= -1;
				int highestCardBelowPointer	= -1;
				int highestCard				= -1;
				int highestCardPointer		= -1;
				
				//define highest card
				
				if (players[j].size() > 0) {
					
					highestCardPointer = players[j].size()-1;
					highestCard = (int)players[j].get(highestCardPointer);
					
				}
				
				
				//define highest card below highestRank
				for (int itemp = highestCardPointer; itemp >= 0; itemp--) { 
					
					int currentCard = (int)players[j].get(itemp);
	
					if (currentCard < highestRank) {
						
							highestCardBelow = currentCard;
							highestCardBelowPointer = itemp;
							break;
					}
				}
				
				
				//special logic for Spades
				
				if (suit == Constants.SPADES) {			//suit is Spades
					
					//QoS should be highest card below even if we hold K
					
					if ((highestCardBelow == Constants.KING)&&(players[j].contains((int)Constants.QUEEN))) {
						
						highestCardBelow = Constants.QUEEN;
						highestCardBelowPointer = players[j].indexOf((int)Constants.QUEEN);
						
					}
					
					//if K or A is highest card, we should go lower
					
					while ((highestCard > Constants.QUEEN) && (highestCardPointer > 0)) {
						
						highestCardPointer --;
						highestCard = (int)players[j].get(highestCardPointer);
						
					}
					
					//if Q is highest card, we should choose one lower
					
					if ((highestCard == Constants.QUEEN) && (highestCardPointer > 0)) {
						
						highestCardPointer --;
						highestCard = (int)players[j].get(highestCardPointer);
						
					}
				}
				
				
				if (highestCardBelow!=-1) {					//if I can remain below highest card, I should
					
					cardToPlay = highestCardBelowPointer;
					
				} else {
					
					if (highestCard != -1) {				//else I should play the highest card

						cardToPlay = highestCardPointer;
						doIWin = false;
						highestRank = highestCard;
					}
				}
				
				if (cardToPlay != -1) { 		// player has the suit
					
					//System.out.println("Player " + j + " plays the " + Constants.VALUE_NAME[(Integer)players[j].get(cardToPlay)]);
					
					cardsLeft = true;
					if (suit == Constants.HEARTS) trickScore++;
					if ((suit == Constants.SPADES) && ((int)players[j].get(cardToPlay) == Constants.QUEEN)) trickScore += 13;
					players[j].remove(cardToPlay);
					
				} else {						// player doesn't have suit and plays a cost card
					
					//System.out.println("Player " + j + " doesn't have this suit.");
					int cost = todaysCostCards.getCostOfMostExpensiveCard(true);
					trickScore += cost;
				}
			}
			
			if (!cardsLeft) break;
			if (doIWin) {
				//System.out.println("I won the trick and get " + trickScore + " points.");
				tricksMade++;
				pointsMade += trickScore;
			}
			
		}
		
		mySuit=null;players[0]=null;players[1]=null;players[2]=null;
		
		if (costCards == null) {
			return tricksMade/2;
		} else {
			//System.out.println("In this test game, I made " + pointsMade + " points.");
			return pointsMade/2;
		}
	}
	
	
	
	/**
	 * Reads the chance of all cards in a suit coming to play based on the cards that are left in the suit.
	 * The valueis read out of a static Hashtable.
	 * @param suit: the suit in question
	 * @param cardsLeft: all the cards left in play. A copy of the cards is created and will be reduced to only contain the cards of the suit in question.
	 * @return the probability in percent that all the cards of the suit come into play.
	 */
		
	public float getChanceForAllCardsComingIntoPlay(int suit, Hand cardsLeft) {
			
		return this.calculate(suit, cardsLeft, Constants.CHANCE_OF_ALL_CARDS_COMING_INTO_PLAY);
	
	}
	
			
	
	
	public SuitEvaluation getBestSuitToDitch(Hand cardsLeft, boolean includeSpades, boolean includeHearts) {
		
		float cardsCounter = 0;
		int suit = 0;
		for (int i=0; i<4; i++) {
			
			if ((i==Constants.SPADES)&&(!includeSpades)) continue;
			if ((i==Constants.HEARTS)&&(!includeHearts)) continue;
			
			float tempCounter = this.calculate(i,  cardsLeft, Constants.AVG_CARDS_LEFT_AFTER_BLANK);
			if (tempCounter > cardsCounter) {
				//we've got a new top value
				cardsCounter = tempCounter;
				suit = i;
			}
		}
		
		return new SuitEvaluation(suit, cardsCounter);
		
	}
	
	
	
	public int getFreeSuits(boolean includeSpades, boolean includeHearts) {
		int emptySuits = 0;
		
		for (int i=0; i<4; i++) {
			if ((i==Constants.SPADES)&&(!includeSpades)) continue;
			if ((i==Constants.HEARTS)&&(!includeHearts)) continue;
			
			if (!this.hasSuit(i)) emptySuits++;
			
		}
		
		return emptySuits;
	}
		
	
	
	/**
	 * Reads the average number of players not having a specific suit.
	 * The value is read out of a static Hashtable.
	 * @param suit: the suit in question
	 * @param cardsLeft: all the cards left in play. A copy of the cards is created and will be reduced to only contain the cards of the suit in question.
	 * @return the average number of players not having this suit, based on the player's cards and the cards left.
	 */
		
	public float getAverageHasSuit(int suit, Hand cardsLeft) {
			
		return this.calculate(suit, cardsLeft, Constants.AVG_PLAYERS_HAVING_SUIT);
	
	}
	
	
		
		
	public Card getBestCardToLose(boolean canCost, Hand cardsLeft, Hand ignoreCards) {
		
		//TODO: this currently assumes that the to-lose card is always traded and not just discarded
		
		if (ignoreCards == null) ignoreCards = new Hand();		//so we get no null pointer errors
		Card answer=null;
		float value=100f;
		
		for (int i=0; i < this.size(); i++) {
			
			Card tempCard = this.getCard(i);
			
			if (((canCost) || (tempCard.getCost()==0)) && (!ignoreCards.hasCard(tempCard))) { 	//if can't cost, choose only no-cost cards and don't evaluate cards that are part of the ignoreCards hand
				float tempValue = this.getCardValue(tempCard, cardsLeft);
				if (tempValue <= value) {
					value  = tempValue;
					answer = tempCard;
				}
			}
			
		}
		System.out.println("Best card to lose is the " + answer);
		return answer;
		}
	
	
	public float getPlayOutSpadeSafety(Hand cardsLeft) {
		//logic assumes that Q hasn't been played yet
		//if player holds queen or no spades at all, return 0 i.e. don't play spades
		
		if ((!this.hasSuit(Constants.SPADES))||(this.hasCard(Constants.SPADES,  Constants.QUEEN))) {
			return 0;
		}
		int badCards=0;
		if (this.hasCard(Constants.SPADES, Constants.KING)) badCards++;
		if (this.hasCard(Constants.SPADES, Constants.ACE)) badCards++;
		if (badCards<1) {		//player has no king/ace, can play spades
			return 1;
		}
		//player has at least one bad card
		//TODO: change logic to check for chance that all spades will be pulled
		Hand tempHand = new Hand(this);
		if (badCards==2) tempHand.removeCard(Constants.SPADES, Constants.ACE);
		float chance = 1.0f-(tempHand.getChanceForAllCardsComingIntoPlay(Constants.SPADES, cardsLeft)/3.0f);
		return chance;	
	}
	
	
	public Card playCard(Card p) {
		
		this.cards.remove(p);
		for (int i=0;i<this.cards.size();i++) {
			this.cards.get(i).setPosition(i);
		}
		this.suitValuesCalculatedBasedOnCardsLeft = 0;
		//this.sortHand();
		return p;
	}
	

}
