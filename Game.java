package heart;


import java.util.*;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Game  {
	
	private Deck deck;
	private ArrayList<Player> players = new ArrayList<Player>(4);
	private Round round;
	private int roundCounter;
	private Screen content;
	private boolean gameIsFinished;
	private PlayCardEventListener listener;
	public volatile boolean mouseClicked;
	
	public Game(String person1, String person2, String person3, String person4) {
		
		this.deck = new Deck();
		this.players.add(new Player(person1,false,0));
		this.players.add(new Player(person2,false,1));
		this.players.add(new Player(person3,false,2));
		this.players.add(new Player(person4,false,3));
		this.roundCounter=0;
		this.gameIsFinished=false;
	}
	
	public Deck getDeck() {
		return this.deck;
	}
	
	public Player getPlayer(int i) {
		return this.players.get(i);
	}
	
	public Round getRound() {
		return this.round;
	}
	
	public void newRound() {
		this.round=new Round();
	}
	
	public Player nextStarter() {
		return this.round.nextToStart;
	}
	
	public Player getNextPlayer(Player currentPlayer) {
		int currentPointer = this.players.indexOf(currentPlayer);
		currentPointer++;
		if (currentPointer>3) currentPointer=0;
		return this.players.get(currentPointer);
	}
	
	public void setScreen(Screen screen) {
		this.content = screen;
	}
	
	
	public void play() {
        if (!gameIsFinished) {
        	this.newRound();
        	for (int i=0;i<4;i++) {
    			this.getPlayer(i).getHand().sortHand();
    			System.out.println(this.getPlayer(i).getName()+" has the following cards: ");
    			for (int j=0;j<13;j++) {
    				System.out.print(this.getPlayer(i).getHand().getCard(j).getName()+", ");
    			}
    		System.out.println("");
    		}
    		//WaitForConfirmationClick listener = new WaitForConfirmationClick();
    		//this.content.addMouseListener(listener);
        	
        	//call JavaFX thread to update all racks and wait till finished
        	CountDownLatch latch = new CountDownLatch(1);
        	Platform.runLater(new Runnable() {
        		@Override
        		public void run() {
        			content.setCards();
        			latch.countDown();
        		}
        	});
        	try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		this.round.play();
    		
    		
        }
	}
	
	public void showRoundResultsAndRestart() {
		
		int round = this.roundCounter;

		Integer[] roundScores = new Integer[4];
		//update totals
		for (int i=0;i<4;i++) {
			roundScores[i]=this.players.get(i).getRoundScore();
			this.players.get(i).increaseScore(roundScores[i]);
			if (this.players.get(i).getScore()>99) this.gameIsFinished=true;
		}
		
		CountDownLatch latch = new CountDownLatch(1);
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			content.showRoundResults(round, roundScores);
    			latch.countDown();
    		}
    	});
    	try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//UPDATE JOptionPane.showMessageDialog(this.content, new JScrollPane(this.scoresTable));
		System.out.println("Round finished!");
		if (!this.gameIsFinished) this.play();
	}
	
	
	
	public void setPlayCardEventListener(PlayCardEventListener theListener) {
		this.listener = theListener;
	}
	
	
	
	public void playManualCard(int suit, int rank) {
		
	}
	
	
	
	/*
	//mouse event listener
	public class WaitForConfirmationClick implements MouseListener {

		   public void mousePressed(MouseEvent evt) {
		      if (Game.this.getRound().getTrick().trickComplete) {
		    	  //remove cards from screen
				for (int i=0;i<Game.this.getRound().getTrick().cardsInTheTrick.size();i++) {
					//Game.this.content.remove(Game.this.getRound().getTrick().cardsInTheTrick.get(i));
					//only necessary if cards are actual objects on their own
				}
		    	Game.this.getRound().play();
		      }
		      Game.this.content.repaint();  // Call repaint() on the Component that was clicked.
		   }

		   public void mouseClicked(MouseEvent evt) { }
		   public void mouseReleased(MouseEvent evt) { }
		   public void mouseEntered(MouseEvent evt) { }
		   public void mouseExited(MouseEvent evt) { }

		}
	
	*/
	
	public void showDialog(String text, String title) {
       
		/*
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int answer = JOptionPane.showOptionDialog(content, text, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (answer == JOptionPane.OK_OPTION) Game.this.mouseClicked = true;
            }
        });
        */
    }//end showMyPane
	
	/*
	 ********************************
	 * Class ROUND
	 ********************************
	 */
	
	class Round {
		
		private Hand cardsPlayed;
		private int cardsPlayedCounter;
		private Player nextToStart;
		private boolean queenSpadesGone;
		private boolean heartsBroken;
		private int tricksPlayed;
		private Trick trick;
		private boolean roundComplete;
		
		public Round() {
			deck.shuffle();
			Game.this.roundCounter++;
			cardsPlayedCounter=0;
			tricksPlayed=0;
			queenSpadesGone=false;
			heartsBroken=false;
			cardsPlayed=new Hand();
			roundComplete=false;
			
			//give everyone a new hand and determine who moves first
			for (int i=0;i<4;i++) {
				players.get(i).setRoundScore(0);
				players.get(i).setHand(new Hand(deck));
				if (players.get(i).getHand().holdsCard(deck.getSpecificCard(Constants.CLUBS, Constants.TWO), deck)) nextToStart=players.get(i);
			}
		}
		
		
		
		public void play() {
			
			while (this.cardsPlayed.size()<Game.this.deck.getNumberOfCards()) {
				System.out.println("Currently played cards: "+this.cardsPlayed.size());
				System.out.println("Overall cards in the deck: "+Game.this.deck.getNumberOfCards());
				this.trick = new Trick(this.nextToStart);
				this.trick.playTrick();
			} 
			
			//display scores
			this.roundComplete = true;
			Game.this.showRoundResultsAndRestart();
			
			
		}
		
		public boolean isComplete() {
			return this.roundComplete;
		}
		
		public int numberOfSuitCardsLeft(int suit) {
			
			int cardsLeft=Constants.CARDS_IN_A_SUIT;
			
			for (int i=0;i<cardsPlayed.size();i++) {
				if (cardsPlayed.getCard(i).getSuit()==suit) cardsLeft--;
			}
			
			return cardsLeft;
		}
		
		public ArrayList<Card> suitCardsLeft(int suit) {
			ArrayList<Card> answerList = new ArrayList<Card>(Constants.CARDS_IN_A_SUIT);
			//fill ArrayList
			for (int i=0;i<Constants.CARDS_IN_A_SUIT;i++) {
				answerList.add(deck.getSpecificCard(suit, i));
			}
			//remove those that are played
			for (int i=0;i<cardsPlayed.size();i++) {
				if (cardsPlayed.getCard(i).getSuit()==suit) answerList.remove(cardsPlayed.getCard(i));
			}
			return answerList;
		}
		
		public Hand cardsLeftInPlay() {
			Hand answerList = new Hand();
			//fill Hand
			for (int suit=0; suit < Constants.SUITS_IN_THE_DECK; suit++) {
				for (int rank=0;rank<Constants.CARDS_IN_A_SUIT;rank++) {
					answerList.add(deck.getSpecificCard(suit, rank));
				}
			}
			
			//remove those that are played
			for (int i=0;i<cardsPlayed.size();i++) {
				answerList.remove(cardsPlayed.getCard(i));
			}
			
			return answerList;
		}
		
		public boolean isOnlyOneLeftWithSuit(Player p, int suit) {
			if (p.getHand().getCardsOfSuit(suit)==null) return false; //doesn't have suit!
			if (p.getHand().getCardsOfSuit(suit).size()==this.suitCardsLeft(suit).size()) return true; else return false;
		}
		
		public int higherRanksLeft(Card card) {
			
			int higherRanksLeft=12-card.getRank();
			for (int i=0;i<cardsPlayed.size();i++) {
				Card currentCard = cardsPlayed.getCard(i);
				if ((currentCard.getSuit()==card.getSuit())&&(currentCard.getRank()>card.getRank())) higherRanksLeft--;
			}
			
			return higherRanksLeft;
			
		}
		
		public int lowerRanksLeft(Card card) {
			
			int lowerRanksLeft=card.getRank();
			for (int i=0;i<cardsPlayed.size();i++) {
				Card currentCard = cardsPlayed.getCard(i);
				if ((currentCard.getSuit()==card.getSuit())&&(currentCard.getRank()<card.getRank())) lowerRanksLeft--;
			}
			
			return lowerRanksLeft;
		}
		
		public Player getNextStarter() {
			return this.nextToStart;
		}
		
		public Trick getTrick() {
			return this.trick;
		}
		
		
		
		/*
		 * **************************************
		 *  CLASS TRICK
		 * **************************************
		 */
		
		
		
		class Trick {
			
			private int suitPlayed;
			private int trickValue;
			private Card highestRankInSuit;
			private Player nextToMove;
			private Player trickWinner;
			private ArrayList<Card> cardsInTheTrick = new ArrayList<Card>(4);
			private ArrayList<Player> owners = new ArrayList<Player>(4);
			private boolean trickComplete;
			
			public Trick(Player startingPlayer) {
				this.suitPlayed=0;
				this.trickValue=0;
				this.highestRankInSuit=null;
				this.trickWinner=startingPlayer;
				this.nextToMove=startingPlayer;
				this.cardsInTheTrick.clear();
				this.owners.clear();
				this.trickComplete=false;
				Game.this.mouseClicked=false;
			}
			
			public boolean isComplete() {
				return trickComplete;
			}
			
			public int cardsInTheTrick() {
				return cardsInTheTrick.size();
			}
			
			public ArrayList<Card> getTrickCards() {
				return this.cardsInTheTrick;
			}
			
			public void playTrick() {
				while (cardsInTheTrick()<4) {
					this.playCard(this.nextToMove);
					//UPDATE content.updateCards(this.nextToMove.getPlayerNumber());
				}
							
				//TODO: add a confirmation screen
				//update score
				this.trickWinner.increaseRoundScore(this.trickValue);
				if (this.trickValue>4) Round.this.queenSpadesGone=true;
				
				//set starter for next trick
				Round.this.nextToStart=this.trickWinner;
				
				//Game.this.showDialog(this.trickWinner.getName()+" has won the trick and has now " + this.trickWinner.getRoundScore() + " points.", this.trickWinner.getName() + " won the trick");
				this.trickComplete=true;
				while (!Game.this.mouseClicked) {}
				
				//remove cards from trick
				for (int i=0; i<cardsInTheTrick(); i++) {
					cardsInTheTrick.get(i).setOwner(-1);
				}
				Game.this.mouseClicked=false;
						
			}
			
			public void playCard(Player p) {
				
				Card playedCard;
				//play
				if (p.isHuman()) playedCard=this.humanMove(p.getHand()); else playedCard=this.computerMove(p.getHand());
				System.out.println(p.getName()+" plays the "+playedCard.getName());
				//now we need to animate the card going into the trick
				//and tell the card that it's in the trick
				int cardPosition = playedCard.getOriginalPosition();
				playedCard.setPosition(-1*(this.cardsInTheTrick()+1));
				//trigger animation
				System.out.println("Triggering a new onPlayCard event");
				CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					content.playCard(playedCard, cardPosition);
					latch.countDown();
					});
				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//if the first card: determine suit played
				if(this.cardsInTheTrick.isEmpty()) {
					this.suitPlayed=playedCard.getSuit();
					this.highestRankInSuit=playedCard;
				}
				//if suit is suit played then check if current card is highest card
				if (playedCard.getSuit()==this.suitPlayed) {
					if (playedCard.getRank()>this.highestRankInSuit.getRank()) {
						this.highestRankInSuit=playedCard;
						this.trickWinner=p;
					}
				}
				//update trick value
				this.trickValue=this.trickValue+playedCard.getCost();
				System.out.println("Trick value now is "+this.trickValue);
				this.cardsInTheTrick.add(playedCard);
				Round.this.cardsPlayed.add(playedCard);
				this.owners.add(p);
				//update screen
				//Game.this.content.setLayers();
				//UPDATE Game.this.content.repaint();
				
				//next player
				this.nextToMove=Game.this.getNextPlayer(this.nextToMove);
				
			}
			
			public Player getWhoIsNext() {
				return this.nextToMove;
			}
			
			public Player getWhoWonTheTrick() {
				return this.trickWinner;
			}
			
			public int getNumberOfCardsInTrick() {
				return this.cardsInTheTrick.size();
			}
			
			public ArrayList<Card> getCardsInTrick() {
				return this.cardsInTheTrick;
			}
			
			public ArrayList<Player> getOwners() {
				return this.owners;
			}
			
			public Card computerMove(Hand h) {
				
				Hand cardsLeft = Round.this.cardsLeftInPlay();
				System.out.println("QoS gone: "+Round.this.queenSpadesGone);
				
				//if we're in the first trick, there is no risk of gaining points, hence play biggest card
				if (Round.this.cardsPlayed.size()<4) {
					//starting player needs to play 2 of clubs
					if (Round.this.cardsPlayed.size()==0) return h.playCard(Game.this.deck.getSpecificCard(Constants.CLUBS, Constants.TWO));
					else { 
						System.out.println("WohAAA!");
						if (h.hasSuit(this.suitPlayed)) return h.playCard(h.getHighestCardInSuit(this.suitPlayed));
						else return h.playCard(h.getMostExpensiveCard(false, cardsLeft));
					}
				}
				if (this.cardsInTheTrick.isEmpty()) {
					//first to play card. 3 possibilities: QoS is still out there / I have the QoS / QoS already played
					//if the queen is still out there, check if I should go for spades
					if (!Round.this.queenSpadesGone) {
						if (h.hasCard(Constants.spades,  Constants.queen)) {
							//go for best card to lose
							//TODO: include code for what to do if hearts are broken
							//TODO: if player is blank on one suit, he better try to lose ownership, especially late in the game
							//TODO: No good if the suit with the least cards is spades!
							System.out.println("WohA!");
							return h.playCard(h.getHighestCardInSuit(h.getSuitWithLeastCards(false)));
							
						} else {
							//if I don't have spades, I need to play the card that is most likely to go through
							if (h.hasSuit(Constants.spades)) {
								float estimate = h.getPlayOutSpadeSafety(cardsLeft);
								System.out.println("Estimate to safely play spades: "+estimate);
								if (estimate>0.9f) {
									//we either don't have K or A, or we have so many spades that it's cool anyways
									System.out.println("WohAA!");
									return h.playCard(h.getHighestCardBelow(Constants.spades,  Constants.queen));
								} else {
									//check if the chance that my best card will go through is higher than the chance that I can safely play spades
									float safestCard = h.calculateLossOfTrick(h.getBestCardToLoseOwnership(cardsLeft), cardsLeft);
									if (estimate>safestCard) {
										//play spades
										System.out.println("WohAB!");
										return h.playCard(h.getHighestCardBelow(Constants.spades,  Constants.queen));
									} else {
										//play card that will least likely result in getting trick
										System.out.println("WohAC!");
										return h.playCard(h.getBestCardToLoseOwnership(cardsLeft));
									}
									
								} 
							} else {
								//doesn't have spades
								//TODO: strategy for optimizing cards when QoS still out there and player doesn't have spades
								return h.playCard(h.getBestCardToLoseOwnership(cardsLeft));
								
							}
						}
						
					}
					//play the card that is most likely to move ownership or play a "painful" card
					//TODO: I should also calculate in if I know that someone doesn't have this suit
					//how do we run through the test to see if a card is painful?
					//let's just see what the values are
					System.out.print("Values for suits: ");
					for (int i=0; i<4; i++) {
						System.out.println("test");
						float a = h.getAverageHasSuit(i, Round.this.cardsLeftInPlay());
						System.out.println("test 2");
						float b = h.getChanceForAllCardsComingIntoPlay(i,  Round.this.cardsLeftInPlay());
						System.out.println("test 3");
						System.out.println(Constants.SUIT_NAME[i]+": "+h.getAverageHasSuit(i, Round.this.cardsLeftInPlay())+", "+h.getChanceForAllCardsComingIntoPlay(i, Round.this.cardsLeftInPlay()));
					}
					System.out.println("WohB!");
					return h.playCard(h.getBestCardToLoseOwnership(Round.this.cardsLeftInPlay()));
				} else {
					//not first to play card
					if (h.hasSuit(this.suitPlayed)) {
						//must play suit
						//if I'm the last and the trick is cheap, then I might as well go over
							//this doesn't always make sense, e.g. if there's no chance that I'll get any further points anyhow or if the suit is hearts - would need to create additional logic
						if (this.cardsInTheTrick.size()==3) {
							if (this.trickValue<2) {
								System.out.println("WohCA!");
								return h.playCard(h.getHighestNoCostCardInSuit(this.suitPlayed));
							}
						}
						Card playCard=h.getHighestCardBelow(this.highestRankInSuit);
						if (playCard==null) {
							//if the hand holds no card below the highest card played, play highest card of the suit
								//here, we would also need to add additional logic
							//should check what the highest number of points is that I could get with this trick
							//should then check the different likelihood of winning the trick per card
							if (!Round.this.queenSpadesGone)  {
								//here, I would first need to check if I have the QoS and spades is played
								System.out.println("WohCB!");
								return h.playCard(h.getLowestCardInSuit(this.suitPlayed)); 
							}
							else {
								System.out.println("WohDD!");
								return h.playCard(h.getHighestCardInSuit(this.suitPlayed));
							}
						}  //otherwise play the highest card in the hand that's below the highest card played
						else {
							System.out.println("WohEE!");
							return h.playCard(playCard);
						}
					} else {
						//doesn't have the suit played
						float initialValue = h.getHandValue(Round.this.cardsLeftInPlay());
						System.out.println("Current value of hand:"+initialValue);
						Card cardToPlay=h.getCard(0);
						float highestValue=h.getCardValue(cardToPlay, Round.this.cardsLeftInPlay());
						for (int i=1;i<h.size();i++) {
							float tempValue = h.getCardValue(h.getCard(i),Round.this.cardsLeftInPlay());
							System.out.println("Card value of "+h.getCard(i).getName()+": "+tempValue);
							if (tempValue>highestValue) {
								highestValue=tempValue;
								cardToPlay=h.getCard(i);
							}
						}
						System.out.println("WohFF!");
						return h.playCard(cardToPlay);
					}
				}
				
			}
			
			public Card humanMove(Hand h) {
				
				//wait until manual card clicked
				return h.getCard(0);
				
			}
			
		}
		
		
	}
	


}
