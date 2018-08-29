package heart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Hand {
	
	
	private ArrayList<Card> cards;
	
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
	
	public ArrayList<Card> getHand() {
		return (ArrayList<Card>)this.cards.clone();
	}
	
	public Card getCard(int index) {
		return this.cards.get(index);
	}
	
	public void setCard(int index, Card card) {
		this.cards.set(index, card);
	}
	
	public void getNewHand(Deck deck) {
		
		for (int i=0; i<Constants.CARDS_IN_A_FULL_HAND; i++) {
			this.cards.add(deck.getCard());
		}
		
	}
	
	public void add(Card card) {
		this.cards.add(card);
	}
	
	public void remove(Card card) {
		this.cards.remove(card);
	}
	
	public void remove(int i) {
		this.cards.remove(i);
	}
	
	public void removeCard(int suit, int rank) {
		for (int i=0;i<this.size();i++) {
			if ((this.getCard(i).getRank()==rank)&&(this.getCard(i).getSuit()==suit)) {
				this.remove(this.getCard(i));
			}
		}
	}
	
	public boolean giveTo(int suit, int rank, Hand recipient) {
		if (this.hasCard(suit,  rank)) {
			Card card = this.getCard(suit, rank);
			this.remove(card);
			recipient.add(card);			
			return true;
		} else return false;
	}
	

	public boolean holdsCard(Card card, Deck deck) {
		
		if (this.cards.contains(deck.getSpecificCard(card.getSuit(), card.getRank()))) return true; else return false;
	}
	
	public Card getCard(int suit, int rank) {
		
		for (int i=0; i<this.cards.size();i++) {
			if ((this.cards.get(i).getSuit()==suit)&&(this.cards.get(i).getRank()==rank)) {
				return this.cards.get(i);
			}
		}
		return null;
	}
	
	public void sortHand() {
		Collections.sort(this.cards);
		for (int i=0;i<this.cards.size();i++) {
			this.cards.get(i).setPosition(i);
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
		
	public boolean hasCard(int suit, int rank) {
		return (this.getCard(suit, rank)!=null);
	}
	
	
	/*
	public static ArrayList<Card> getCardsOfSuit(ArrayList<Card> list, int suit) {
		ArrayList<Card> returnList = new ArrayList<Card>(list.size());
		for (int i=0; i<list.size();i++) {
			if (list.get(i).getSuit()==suit) returnList.add(list.get(i));
		}
		return returnList;
	}
	*/
	
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
		if (suit!=Constants.spades) return this.getHighestCardInSuit(suit);
		if (this.getHighestCardInSuit(suit).getRank()!=Constants.queen) 
			return this.getHighestCardInSuit(suit);
		else {
			if (this.getHighestCardBelow(suit, Constants.queen)!=null)
				return this.getHighestCardBelow(suit, Constants.queen);
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
	
	public int getSuitWithLeastCards(boolean includeHearts) {
		int answerCount=13;
		int answer=0;
		int[] cards=new int[] {0,0,0,0};
		for (int i=0;i<this.size();i++) {
			cards[this.getCard(i).getSuit()]++;
		}
		int max=4;
		if (!includeHearts) max=3;
		
		for (int i=0;i<max;i++) {
			if ((cards[i]<answerCount)&&(cards[i]>0)){
				answerCount=cards[i];
				answer=i;
			}
		}
		System.out.println("Suit with the lowest number of cards: "+answer);
		return answer;
		
	}
	
	public void removeCards(Hand cardsToRemove) {
		for (int i=0;i<cardsToRemove.size();i++) {
			this.remove(cardsToRemove.getCard(i));
		}
	}
	
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
					System.out.println("In Sylvestre: n="+n+", i="+i+", t="+t+", k="+k+" and j="+j);
					possibilities+=(Constants.combinations(n, i)*Constants.combinations(k,(t-i))*Constants.combinations(n-i,j)*Constants.combinations(k-(t-i),(t-j)));
				}
			}
		}
		//System.out.println("Overall possibilities that 2 people must go over: "+possibilities+" out of "+totalCombos+" total possibilities.");
		returnValue-=possibilities*3.0f/totalCombos;
		System.out.println("Giving a total percentage of "+returnValue+"%");
		return returnValue;
	}
	
	public Card getBestCardToLoseOwnership(Hand cardsLeft) {
		Card returnCard = this.getHand().get(0);
		float cardPercentage = this.calculateLossOfTrick(returnCard, cardsLeft);
		for (int i=1;i<this.getHand().size();i++) {
			Card cardInQuestion = this.getHand().get(i);
			float tempPercentage = this.calculateLossOfTrick(cardInQuestion, cardsLeft);
			if (tempPercentage>cardPercentage) {
				returnCard=cardInQuestion;
				cardPercentage = tempPercentage;
			}
		}
		return returnCard;
	}
	
	public int getHearts() {
		return this.getCardsOfSuit(Constants.hearts).size();
	}
	
	public float getHandValue(Hand cardsLeft) {
		float overallValue=30.0f;
		if (this.hasCard(Constants.spades, Constants.queen)) overallValue-=10;
		/*
		//remove my own cards from cardsLeft
		Hand otherCards = new Hand(cardsLeft.cards);
		System.out.println("Cloned list OtherCards contains "+otherCards.size()+" items.");
		otherCards.removeCards(this);
		//OLD: for (int i=0;i<hand.size();i++) otherCards.remove(hand.get(i));
		System.out.println("After removing my own items, the cloned list OtherCards contains "+otherCards.size()+" items.");
			*/
		for (int suit=0;suit<Constants.suitsInTheDeck;suit++) { //check quality of every suit
			if (this.hasSuit(suit)) {
				if (suit!=Constants.hearts)
					overallValue-=this.getAverageTricksPerSuit(suit, cardsLeft);
				else {
					float points = this.getAverageTricksPerSuit(suit, cardsLeft)*3;
					if (points>cardsLeft.getHearts()) points=cardsLeft.getHearts();
					overallValue-=points;
				}
			} else {
				overallValue+=0.8f;
			}
			/*
			int multiplier;
			if (suit==3) multiplier=2; else multiplier=1;
			Hand myCardsInSuit = this.getCardsOfSuit(suit);
			Hand otherCardsInSuit = otherCards.getCardsOfSuit(suit);
			if (myCardsInSuit.size()==0) { //if I don't have this suit, it's 100 points.
				overallValue+=100;
			} else if (otherCardsInSuit.size()>0) {	//both I and the rest have this suit
				//get my lowest rank
				int lowestRank = this.getRankOfLowestCardInSuit(suit);
				overallValue+=(25*multiplier*otherCardsInSuit.getCardsAbove(lowestRank));
				overallValue-=(30*multiplier*otherCardsInSuit.getCardsBelow(lowestRank));
				//now do the calculation with removing my lowest card
				myCardsInSuit.remove(0);
				if (myCardsInSuit.size()==0) { //if that was my last card, then good
					overallValue+=20;
				} else {
					//get my lowest rank
					lowestRank = this.getRankOfLowestCardInSuit(suit);
					overallValue+=(20*multiplier*otherCardsInSuit.getCardsAbove(lowestRank));
					overallValue-=(25*multiplier*otherCardsInSuit.getCardsBelow(lowestRank));
				}
			}
			*/
		}
		return overallValue;
	}
	
	public float getCardValue(Card card, Hand cardsLeft) {
		
		float initialValue = this.getHandValue(cardsLeft);
		Hand newHand = new Hand(this);
		newHand.remove(card);
		float newValue = newHand.getHandValue(cardsLeft);
		return (newValue-initialValue);
		
	}
	
	public int getKey(Hand myCards, Hand otherCards) {
		int result = 0;
		int suit = 0;
		if (myCards.size()>0) {
			suit = myCards.getCard(0).getSuit();
		} else {
			if (otherCards.size()>0) suit = otherCards.getCard(0).getSuit();
		}
		for (int i = 0; i < Constants.CARDS_IN_A_SUIT; i++) {
			if (myCards.hasCard(suit, i)) {
				result+=Math.pow(2,(25-i));
			} else {
				if (otherCards.hasCard(suit, i)) result+=Math.pow(2,(12-i));
			}
		}
		result = result * 4;
		return result;
	}
	
	/**
	 * Returns the average number of tricks made by playing all cards of a specific suit, based on the cards of the hand itself and the cards that are remaining in play.
	 * The value is obtained by a static Hashtable stored in the class Probabilities.
	 * @param suit: the suit that is being checked.
	 * @param cardsLeft: the cards left in play.
	 * @return the probability in percent, i.e. a value between 0 and 1.
	 */
	
	public float getAverageTricksPerSuit(int suit, Hand cardsLeft) {
		
		return this.calculateAverageTricksPerSuit(suit, cardsLeft);
		/*
		Hand otherCards = cardsLeft.getCardsOfSuit(suit);
		Hand mySuit = this.getCardsOfSuit(suit);
		otherCards.removeCards(mySuit);
		if ((otherCards.size()==0)||(mySuit.size()==0)) return 0;
		else return Probabilities.averageTricks(getKey(mySuit,otherCards));
		*/
	}
	
	
public float calculateAverageTricksPerSuit(int suit, Hand cardsLeft) {
		
		Hand otherCards = cardsLeft.getCardsOfSuit(suit);
		Hand mySuit = this.getCardsOfSuit(suit);
		//System.out.println("getAverageTricksPerSuit: Cards left:"+otherCards.size() );
		otherCards.removeCards(mySuit);
		ArrayList<Integer> myCards=new ArrayList<Integer>();
		//System.out.print("My hand in suit "+Constants.suit[suit]+": ");
		for (int i=0;i<mySuit.size();i++) {
			myCards.add(mySuit.getCard(i).getRank());
			//System.out.print(mySuit.getCard(i).getRank()+"-");
		}
		Integer[] theRest=new Integer[otherCards.size()];
		//System.out.print(" Other cards: ");
		for (int i=0;i<otherCards.size();i++) {
			theRest[i]=otherCards.getCard(i).getRank();
			//System.out.print(otherCards.getCard(i).getRank()+"-");
			
		}
		//System.out.println(" ");
		//System.out.println("after removing player's cards:"+otherCards.size());
		//otherCards = otherCards.getCardsOfSuit(suit);
		int depth=otherCards.size();
		if (depth==0) return 0;
		if (mySuit.size()==0) return 0;
		
		//System.out.println("Depth of lookup:"+depth);
		ArrayList<Integer>[]cards = new ArrayList[3];
		for (int i=0;i<3;i++) cards[i]=new ArrayList<Integer>();
		int tricksMade = 0;
		int numberOfGamesRun=0;
		for (int c1=0;c1<3;c1++) {
			
			if (depth>1) {
				//next card iteration
				for (int c2=0;c2<3;c2++) {
					
					if (depth>2) {
						//next card iteration
						for (int c3=0;c3<3;c3++) {
							
							if (depth>3) {
								//next card iteration
								for (int c4=0;c4<3;c4++) {
									
									if (depth>4) {
										//next card iteration
										for (int c5=0;c5<3;c5++) {
											
											if (depth>5) {
												//next card iteration
												for (int c6=0;c6<3;c6++) {
													
													if (depth>6) {
														//next card iteration
														for (int c7=0;c7<3;c7++) {
															
															if (depth>7) {
																//next card iteration
																for (int c8=0;c8<3;c8++) {
																	
																	if (depth>8) {
																		//next card iteration
																		for (int c9=0;c9<3;c9++) {
																			
																			if (depth>9) {
																				//next card iteration
																				for (int c10=0;c10<3;c10++) {
																					
																					if (depth>10) {
																						//next card iteration
																						for (int c11=0;c11<3;c11++) {
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
																									numberOfGamesRun++;
																									tricksMade+=this.runTestGame(myCards, cards);
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
																								numberOfGamesRun++;
																								tricksMade+=this.runTestGame(myCards, cards);
																								cards[0].clear();
																								cards[1].clear();
																								cards[2].clear();
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
																						numberOfGamesRun++;
																						tricksMade+=this.runTestGame(myCards, cards);
																						cards[0].clear();
																						cards[1].clear();
																						cards[2].clear();
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
																				numberOfGamesRun++;
																				tricksMade+=this.runTestGame(myCards, cards);
																				cards[0].clear();
																				cards[1].clear();
																				cards[2].clear();
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
																		numberOfGamesRun++;
																		tricksMade+=this.runTestGame(myCards, cards);
																		cards[0].clear();
																		cards[1].clear();
																		cards[2].clear();
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
																numberOfGamesRun++;
																tricksMade+=this.runTestGame(myCards, cards);
																cards[0].clear();
																cards[1].clear();
																cards[2].clear();
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
														numberOfGamesRun++;
														tricksMade+=this.runTestGame(myCards, cards);
														cards[0].clear();
														cards[1].clear();
														cards[2].clear();
													}
												}
											} else {
												cards[c1].add(theRest[0]);
												cards[c2].add(theRest[1]);
												cards[c3].add(theRest[2]);
												cards[c4].add(theRest[3]);
												cards[c5].add(theRest[4]);
												//run the test game
												numberOfGamesRun++;
												tricksMade+=this.runTestGame(myCards, cards);
												cards[0].clear();
												cards[1].clear();
												cards[2].clear();
											}
										}
									} else {
										cards[c1].add(theRest[0]);
										cards[c2].add(theRest[1]);
										cards[c3].add(theRest[2]);
										cards[c4].add(theRest[3]);
										//run the test game
										numberOfGamesRun++;
										tricksMade+=this.runTestGame(myCards, cards);
										cards[0].clear();
										cards[1].clear();
										cards[2].clear();
									}
								}
							} else {
								cards[c1].add(theRest[0]);
								cards[c2].add(theRest[1]);
								cards[c3].add(theRest[2]);
								//run the test game
								numberOfGamesRun++;
								tricksMade+=this.runTestGame(myCards, cards);
								cards[0].clear();
								cards[1].clear();
								cards[2].clear();
							}
						}
					} else {
						cards[c1].add(theRest[0]);
						cards[c2].add(theRest[1]);
						//run the test game
						numberOfGamesRun++;
						tricksMade+=this.runTestGame(myCards, cards);
						cards[0].clear();
						cards[1].clear();
						cards[2].clear();
					}
				}
			} else {
				cards[c1].add(theRest[0]);
				//run the test game
				numberOfGamesRun++;
				tricksMade+=this.runTestGame(myCards, cards);
				cards[0].clear();
				cards[1].clear();
				cards[2].clear();
			}
		}
		float answer=(float)tricksMade/numberOfGamesRun;
		//System.out.println("Average tricks: "+answer);
		return answer;
	}

/**
 * Reads the chance of all cards in a suit coming to play based on the cards that are left in the suit.
 * The valueis read out of a static Hashtable.
 * @param suit: the suit in question
 * @param cardsLeft: all the cards left in play. A copy of the cards is created and will be reduced to only contain the cards of the suit in question.
 * @return the probability in percent that all the cards of the suit come into play.
 */
	
public float getChanceForAllCardsComingIntoPlay(int suit, Hand cardsLeft) {
		
	return this.calculateChanceForAllCardsComingIntoPlay(suit, cardsLeft);
		/*
		Hand otherCards = cardsLeft.getCardsOfSuit(suit);
		Hand mySuit = this.getCardsOfSuit(suit);
		otherCards.removeCards(mySuit);
		if ((otherCards.size()==0)||(mySuit.size()==0)) {
			return 0;
		} else {
			Integer key = getKey(mySuit, otherCards);
			return Probabilities.averageAllCardsPlayed(key);
		}
		*/
}


/**
 * This class is only called once to create the static Hashtable that is then stored in a file specified by Constants.ALL_CARDS_FILE.
 * @param suit
 * @param cardsLeft
 * @return
 */
		
public float calculateChanceForAllCardsComingIntoPlay(int suit, Hand cardsLeft) {
	
	//System.out.println("Start of calculateChanceForAllCardsComingIntoPlay");
	Hand otherCards = cardsLeft.getCardsOfSuit(suit);
	Hand mySuit = this.getCardsOfSuit(suit);
	otherCards.removeCards(mySuit);
	ArrayList<Integer> myCards=new ArrayList<Integer>();
	//System.out.print("My hand: ");
	for (int i=0;i<mySuit.size();i++) {
		myCards.add(mySuit.getCard(i).getRank());
		//System.out.print(mySuit.getCard(i).getRank()+"-");
	}
	Integer[] theRest=new Integer[otherCards.size()];
	//System.out.print(" Ohter cards: ");
	for (int i=0;i<otherCards.size();i++) {
		theRest[i]=otherCards.getCard(i).getRank();
		//System.out.print(otherCards.getCard(i).getRank()+"-");
		
	}
	//System.out.println(" ");
	int depth=otherCards.size();
	if (depth==0) return 0;
	if (mySuit.size()==0) return 0;
	ArrayList[]cards = new ArrayList[3];
	for (int i=0;i<3;i++) cards[i]=new ArrayList<Integer>();
	int tricksMade = 0;
	int numberOfGamesRun=0;
	for (int c1=0;c1<3;c1++) {
		
		if (depth>1) {
			//next card iteration
			for (int c2=0;c2<3;c2++) {
				
				if (depth>2) {
					//next card iteration
					for (int c3=0;c3<3;c3++) {
						
						if (depth>3) {
							//next card iteration
							for (int c4=0;c4<3;c4++) {
								
								if (depth>4) {
									//next card iteration
									for (int c5=0;c5<3;c5++) {
										
										if (depth>5) {
											//next card iteration
											for (int c6=0;c6<3;c6++) {
												
												if (depth>6) {
													//next card iteration
													for (int c7=0;c7<3;c7++) {
														
														if (depth>7) {
															//next card iteration
															for (int c8=0;c8<3;c8++) {
																
																if (depth>8) {
																	//next card iteration
																	for (int c9=0;c9<3;c9++) {
																		
																		if (depth>9) {
																			//next card iteration
																			for (int c10=0;c10<3;c10++) {
																				
																				if (depth>10) {
																					//next card iteration
																					for (int c11=0;c11<3;c11++) {
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
																								numberOfGamesRun++;
																								int playerLong=0;
																								for (int checkah=0; checkah<3;checkah++) {
																									if (cards[checkah].size()>=myCards.size()) playerLong=1;
																								}
																								tricksMade+=playerLong;
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
																							numberOfGamesRun++;
																							int playerLong=0;
																							for (int checkah=0; checkah<3;checkah++) {
																								if (cards[checkah].size()>=myCards.size()) playerLong=1;
																							}
																							tricksMade+=playerLong;
																							cards[0].clear();
																							cards[1].clear();
																							cards[2].clear();
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
																					numberOfGamesRun++;
																					int playerLong=0;
																					for (int checkah=0; checkah<3;checkah++) {
																						if (cards[checkah].size()>=myCards.size()) playerLong=1;
																					}
																					tricksMade+=playerLong;
																					cards[0].clear();
																					cards[1].clear();
																					cards[2].clear();
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
																			numberOfGamesRun++;
																			int playerLong=0;
																			for (int checkah=0; checkah<3;checkah++) {
																				if (cards[checkah].size()>=myCards.size()) playerLong=1;
																			}
																			tricksMade+=playerLong;
																			cards[0].clear();
																			cards[1].clear();
																			cards[2].clear();
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
																	numberOfGamesRun++;
																	int playerLong=0;
																	for (int checkah=0; checkah<3;checkah++) {
																		if (cards[checkah].size()>=myCards.size()) playerLong=1;
																	}
																	tricksMade+=playerLong;
																	cards[0].clear();
																	cards[1].clear();
																	cards[2].clear();
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
															numberOfGamesRun++;
															int playerLong=0;
															for (int checkah=0; checkah<3;checkah++) {
																if (cards[checkah].size()>=myCards.size()) playerLong=1;
															}
															tricksMade+=playerLong;
															cards[0].clear();
															cards[1].clear();
															cards[2].clear();
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
													numberOfGamesRun++;
													int playerLong=0;
													for (int checkah=0; checkah<3;checkah++) {
														if (cards[checkah].size()>=myCards.size()) playerLong=1;
													}
													tricksMade+=playerLong;
													cards[0].clear();
													cards[1].clear();
													cards[2].clear();
												}
											}
										} else {
											cards[c1].add(theRest[0]);
											cards[c2].add(theRest[1]);
											cards[c3].add(theRest[2]);
											cards[c4].add(theRest[3]);
											cards[c5].add(theRest[4]);
											//run the test game
											numberOfGamesRun++;
											int playerLong=0;
											for (int checkah=0; checkah<3;checkah++) {
												if (cards[checkah].size()>=myCards.size()) playerLong=1;
											}
											tricksMade+=playerLong;
											cards[0].clear();
											cards[1].clear();
											cards[2].clear();
										}
									}
								} else {
									cards[c1].add(theRest[0]);
									cards[c2].add(theRest[1]);
									cards[c3].add(theRest[2]);
									cards[c4].add(theRest[3]);
									//run the test game
									numberOfGamesRun++;
									int playerLong=0;
									for (int checkah=0; checkah<3;checkah++) {
										if (cards[checkah].size()>=myCards.size()) playerLong=1;
									}
									tricksMade+=playerLong;
									cards[0].clear();
									cards[1].clear();
									cards[2].clear();
								}
							}
						} else {
							cards[c1].add(theRest[0]);
							cards[c2].add(theRest[1]);
							cards[c3].add(theRest[2]);
							//run the test game
							numberOfGamesRun++;
							int playerLong=0;
							for (int checkah=0; checkah<3;checkah++) {
								if (cards[checkah].size()>=myCards.size()) playerLong=1;
							}
							tricksMade+=playerLong;
							cards[0].clear();
							cards[1].clear();
							cards[2].clear();
						}
					}
				} else {
					cards[c1].add(theRest[0]);
					cards[c2].add(theRest[1]);
					//run the test game
					numberOfGamesRun++;
					int playerLong=0;
					for (int checkah=0; checkah<3;checkah++) {
						if (cards[checkah].size()>=myCards.size()) playerLong=1;
					}
					tricksMade+=playerLong;
					cards[0].clear();
					cards[1].clear();
					cards[2].clear();
				}
			}
		} else {
			cards[c1].add(theRest[0]);
			//run the test game
			numberOfGamesRun++;
			int playerLong=0;
			for (int checkah=0; checkah<3;checkah++) {
				if (cards[checkah].size()>=myCards.size()) playerLong=1;
			}
			tricksMade+=playerLong;
			cards[0].clear();
			cards[1].clear();
			cards[2].clear();
		}
	}
	float answer=(float)tricksMade/numberOfGamesRun;
	//System.out.println("Average chance of playing whole suit: "+answer);
	return answer;
}
	


/**
 * Reads the average number of players not having a specific suit.
 * The value is read out of a static Hashtable.
 * @param suit: the suit in question
 * @param cardsLeft: all the cards left in play. A copy of the cards is created and will be reduced to only contain the cards of the suit in question.
 * @return the average number of players not having this suit, based on the player's cards and the cards left.
 */
	
public float getAverageHasSuit(int suit, Hand cardsLeft) {
		
	return this.calculateAverageHasSuit(suit, cardsLeft);
		/*
		Hand otherCards = cardsLeft.getCardsOfSuit(suit);
		Hand mySuit = this.getCardsOfSuit(suit);
		otherCards.removeCards(mySuit);
		Integer key = getKey(mySuit, otherCards);
		if ((otherCards.size()==0)||(mySuit.size()==0)) return 0;
		return Probabilities.hasSuit(key);
		*/
}



/**
 * This class is only called once to create the static Hashtable that is then stored in a file specified by Constants.HAS_SUIT_FILE.
 * 
 * 
 * @param suit
 * @param cardsLeft
 * @return
 */


	public float calculateAverageHasSuit(int suit, Hand cardsLeft) {
		
		//System.out.println("Start of getAverageHasSuit");
		Hand otherCards = cardsLeft.getCardsOfSuit(suit);
		Hand mySuit = this.getCardsOfSuit(suit);
		otherCards.removeCards(mySuit);
		ArrayList<Integer> myCards=new ArrayList<Integer>();
		//System.out.print("My hand: ");
		for (int i=0;i<mySuit.size();i++) {
			myCards.add(mySuit.getCard(i).getRank());
			//System.out.print(mySuit.getCard(i).getRank()+"-");
		}
		Integer[] theRest=new Integer[otherCards.size()];
		//System.out.print(" Ohter cards: ");
		for (int i=0;i<otherCards.size();i++) {
			theRest[i]=otherCards.getCard(i).getRank();
			//System.out.print(otherCards.getCard(i).getRank()+"-");
			
		}
		//System.out.println(" ");
		int depth=otherCards.size();
		if (depth==0) return 0;
		if (mySuit.size()==0) return 0;
		ArrayList[]cards = new ArrayList[3];
		for (int i=0;i<3;i++) cards[i]=new ArrayList<Integer>();
		int tricksMade = 0;
		int numberOfGamesRun=0;
		for (int c1=0;c1<3;c1++) {
			
			if (depth>1) {
				//next card iteration
				for (int c2=0;c2<3;c2++) {
					
					if (depth>2) {
						//next card iteration
						for (int c3=0;c3<3;c3++) {
							
							if (depth>3) {
								//next card iteration
								for (int c4=0;c4<3;c4++) {
									
									if (depth>4) {
										//next card iteration
										for (int c5=0;c5<3;c5++) {
											
											if (depth>5) {
												//next card iteration
												for (int c6=0;c6<3;c6++) {
													
													if (depth>6) {
														//next card iteration
														for (int c7=0;c7<3;c7++) {
															
															if (depth>7) {
																//next card iteration
																for (int c8=0;c8<3;c8++) {
																	
																	if (depth>8) {
																		//next card iteration
																		for (int c9=0;c9<3;c9++) {
																			
																			if (depth>9) {
																				//next card iteration
																				for (int c10=0;c10<3;c10++) {
																					
																					if (depth>10) {
																						//next card iteration
																						for (int c11=0;c11<3;c11++) {
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
																									numberOfGamesRun++;
																									for (int checkah=0; checkah<3;checkah++) {
																										if (cards[checkah].size()==0) tricksMade++;
																									}
																									cards[0].clear();
																									cards[1].clear();
																									cards[2].clear();
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
																								cards[c11].add(theRest[10]);
																								numberOfGamesRun++;
																								for (int checkah=0; checkah<3;checkah++) {
																									if (cards[checkah].size()==0) tricksMade++;
																								}
																								cards[0].clear();
																								cards[1].clear();
																								cards[2].clear();
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
																						numberOfGamesRun++;
																						for (int checkah=0; checkah<3;checkah++) {
																							if (cards[checkah].size()==0) tricksMade++;
																						}
																						cards[0].clear();
																						cards[1].clear();
																						cards[2].clear();
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
																				numberOfGamesRun++;
																				for (int checkah=0; checkah<3;checkah++) {
																					if (cards[checkah].size()==0) tricksMade++;
																				}
																				cards[0].clear();
																				cards[1].clear();
																				cards[2].clear();
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
																		numberOfGamesRun++;
																		for (int checkah=0; checkah<3;checkah++) {
																			if (cards[checkah].size()==0) tricksMade++;
																		}
																		cards[0].clear();
																		cards[1].clear();
																		cards[2].clear();
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
																numberOfGamesRun++;
																for (int checkah=0; checkah<3;checkah++) {
																	if (cards[checkah].size()==0) tricksMade++;
																}
																cards[0].clear();
																cards[1].clear();
																cards[2].clear();
															}
														}
													} else {
														cards[c1].add(theRest[0]);
														cards[c2].add(theRest[1]);
														cards[c3].add(theRest[2]);
														cards[c4].add(theRest[3]);
														cards[c5].add(theRest[4]);
														cards[c6].add(theRest[5]);
														numberOfGamesRun++;
														for (int checkah=0; checkah<3;checkah++) {
															if (cards[checkah].size()==0) tricksMade++;
														}
														cards[0].clear();
														cards[1].clear();
														cards[2].clear();
													}
												}
											} else {
												cards[c1].add(theRest[0]);
												cards[c2].add(theRest[1]);
												cards[c3].add(theRest[2]);
												cards[c4].add(theRest[3]);
												cards[c5].add(theRest[4]);
												numberOfGamesRun++;
												for (int checkah=0; checkah<3;checkah++) {
													if (cards[checkah].size()==0) tricksMade++;
												}
												cards[0].clear();
												cards[1].clear();
												cards[2].clear();
											}
										}
									} else {
										cards[c1].add(theRest[0]);
										cards[c2].add(theRest[1]);
										cards[c3].add(theRest[2]);
										cards[c4].add(theRest[3]);
										numberOfGamesRun++;
										for (int checkah=0; checkah<3;checkah++) {
											if (cards[checkah].size()==0) tricksMade++;
										}
										cards[0].clear();
										cards[1].clear();
										cards[2].clear();
									}
								}
							} else {
								cards[c1].add(theRest[0]);
								cards[c2].add(theRest[1]);
								cards[c3].add(theRest[2]);
								numberOfGamesRun++;
								for (int checkah=0; checkah<3;checkah++) {
									if (cards[checkah].size()==0) tricksMade++;
								}
								cards[0].clear();
								cards[1].clear();
								cards[2].clear();
							}
						}
					} else {
						cards[c1].add(theRest[0]);
						cards[c2].add(theRest[1]);
						numberOfGamesRun++;
						for (int checkah=0; checkah<3;checkah++) {
							if (cards[checkah].size()==0) tricksMade++;
						}
						cards[0].clear();
						cards[1].clear();
						cards[2].clear();
					}
				}
			} else {
				cards[c1].add(theRest[0]);
				numberOfGamesRun++;
				for (int checkah=0; checkah<3;checkah++) {
					if (cards[checkah].size()==0) tricksMade++;
				}
				cards[0].clear();
				cards[1].clear();
				cards[2].clear();
			}
		}
		float answer=(float)tricksMade/numberOfGamesRun;
		//System.out.println("Average players without this suit: "+answer);
		return answer;
	}
	
		
	private float runTestGame2(Hand m, int suit, Hand[] p) {
		//get my cards of the suit
		//Hand mySuit = this.getCardsOfSuit(suit);
		//ArrayList<Integer> mySuit=new ArrayList<Integer>();
		//for (int i=0;i<this.cards.size();i++) mySuit.add(this.cards.get(i).getRank());
		//clone the 3 player hands so I can manipulate them
		Hand mySuit = new Hand(m);
		Hand[] players = new Hand[3];
		//HashMap<Integer,ArrayList<Integer>> players = new HashMap<Integer,ArrayList<Integer>>();
		for (int i=0;i<3;i++) players[i]=new Hand(p[i]);
		//for (int i=0;i<3;i++) players.put(i, new ArrayList<Integer>(p.get(i)));
		int tricksMade=0;
		//loop through my cards and play a certain logic
		for (int i=0;i<mySuit.size();i++) {
			int highestRank=mySuit.getCard(i).getRank();
			//int highestRank=mySuit.get(i).intValue();
			boolean otherPlayersHaveSuit=false;
			boolean doIWin=true;
			boolean cardsLeft=false;
			for (int j=0; j<3;j++) {
				//if player can go under highest card, then he should do so
				Card cardToPlay = players[j].getHighestCardBelow(suit, highestRank);

				/*
				int cardToPlay=-1;
				int highestCardBelow=0;
				int highestCard=0;
				int lowestCard=12;
				
				for (int itemp=0; itemp<players.get(i).size();itemp++) {
					int currentCard=players.get(j).get(itemp);
					if (currentCard<lowestCard) lowestCard=currentCard;
					if (currentCard>highestCard) highestCard=currentCard;
					if (currentCard<highestRank) {
						if (currentCard>highestCardBelow) highestCardBelow=currentCard;
					}
				}
				*/
				
				if (cardToPlay==null) {
				//if (cardToPlay==-1) {
					//player can't go below highest card
					cardToPlay=players[j].getHighestCardInSuit(suit);
					//if (Math.random()>0.5) cardToPlay=highestCard; else cardToPlay=lowestCard;
					
					 if (cardToPlay!=null) {
					 
						highestRank=cardToPlay.getRank();
						doIWin=false;
					}
					/*
					highestRank=cardToPlay; 
					doIWin=false;
					 */
				}
				//remove card from hand
				if (cardToPlay!=null) {
					players[j].remove(cardToPlay);
					cardsLeft=true;
				}
				//if (cardToPlay!=-1) players.get(j).remove((Integer)cardToPlay);
			}
			if (!cardsLeft) break;
			if (doIWin) tricksMade++;			
		}
		mySuit=null;players[0]=null;players[1]=null;players[2]=null;
		return tricksMade;
	}
	
	private float runTestGame(ArrayList h, ArrayList<Integer>[] p) {

		ArrayList<Integer> mySuit = (ArrayList<Integer>)h.clone();
		ArrayList[]players = new ArrayList[3];
		for (int i=0;i<3;i++) players[i]=(ArrayList<Integer>)p[i].clone();
		/*
		System.out.print("Player hand:");
		for (int i=0;i<mySuit.size();i++) System.out.print(mySuit.get(i)+"-");
		for (int i=0;i<3;i++) {
			System.out.print(" P"+i+": ");
			for (int j=0;j<players[i].size();j++) System.out.print(players[i].get(j)+"-");
		}
		System.out.println(" ");
*/
		int tricksMade=0;
		//loop through my cards and play a certain logic
		for (int i=0;i<mySuit.size();i++) {
			int highestRank=mySuit.get(i);
			boolean otherPlayersHaveSuit=false;
			boolean doIWin=true;
			boolean cardsLeft=false;
			for (int j=0; j<3;j++) {
				//if player can go under highest card, then he should do so
				
				int cardToPlay=-1;
				int highestCardBelow=-1;
				int highestCardBelowPointer=0;
				int highestCard=-1;
				int highestCardPointer=0;

				
				for (int itemp=0; itemp<players[j].size();itemp++) {
					int currentCard=(Integer)players[j].get(itemp);

					if (currentCard>highestCard) {
						highestCard=currentCard;
						highestCardPointer=itemp;
					}
					if (currentCard<highestRank) {
						if (currentCard>highestCardBelow) {
							highestCardBelow=currentCard;
							highestCardBelowPointer=itemp;
						}
					}
				}
				
				if (highestCardBelow!=-1) {
					//go below highest card
					cardToPlay=highestCardBelowPointer;
				} else {
					if (highestCard!=-1) {
						//can't go below, go for highest card
						cardToPlay=highestCardPointer;
						doIWin=false;
						highestRank=highestCard;
					}
				}
				
				if (cardToPlay!=-1) {
					players[j].remove(cardToPlay);
					cardsLeft=true;
				}
			}
			if (!cardsLeft) break;
			if (doIWin) tricksMade++;			
		}
		mySuit=null;players[0]=null;players[1]=null;players[2]=null;
		//System.out.println("Tricks made:"+tricksMade);
		return tricksMade;
	}
	
	
	public Card getMostExpensiveCard(boolean canCost, Hand cardsLeft) {
		Card answer=null;
		float value=0f;
		for (int i=0;i<this.size();i++) {
			Card tempCard=this.getCard(i);
			if ((canCost)||(tempCard.getCost()==0)) {
				float tempValue = this.getCardValue(tempCard, cardsLeft);
				if (tempValue>=value) {
					value=tempValue;
					answer=tempCard;
				}
			}
			
		}
		
		return answer;
		}
	
	
	public float getPlayOutSpadeSafety(Hand cardsLeft) {
		//logic assumes that Q hasn't been played yet
		//if player holds queen or no spades at all, return 0 i.e. don't play spades
		
		if ((!this.hasSuit(Constants.spades))||(this.hasCard(Constants.spades,  Constants.queen))) {
			return 0;
		}
		int badCards=0;
		if (this.hasCard(Constants.spades, Constants.king)) badCards++;
		if (this.hasCard(Constants.spades, Constants.ace)) badCards++;
		if (badCards<1) {		//player has no king/ace, can play spades
			return 1;
		}
		//player has at least one bad card
		//TODO: change logic to check for chance that all spades will be pulled
		Hand tempHand = new Hand(this);
		if (badCards==2) tempHand.removeCard(Constants.spades, Constants.ace);
		float chance = 1.0f-(tempHand.getChanceForAllCardsComingIntoPlay(Constants.spades, cardsLeft)/3.0f);
		return chance;	
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
	
	
	/*
	public Card playCard(int suit, int rank) {
		
		Card playedCard = this.getCard(suit, rank);
		boolean success = this.cards.remove(playedCard);
		if (!success) System.out.println("Card not found in hand!");
		return playedCard;
	}
	*/
	
	
	public Card playCard(Card p) {
		
		this.cards.remove(p);
		for (int i=0;i<this.cards.size();i++) {
			this.cards.get(i).setPosition(i);
		}
		return p;
	}
	

}
