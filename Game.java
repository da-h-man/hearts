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
	public int gameStatus;
	//round variables
	private Hand cardsPlayed;
	private Player nextToStart;
	private boolean queenSpadesGone;
	private boolean heartsBroken;
	private boolean roundComplete;
	//trick variables
	private int suitPlayed;
	private int trickValue;
	private Card highestRankInSuit;
	private Player nextToMove;
	private Player trickWinner;
	private ArrayList<Card> cardsInTheTrick = new ArrayList<Card>(4);
	private ArrayList<Player> owners = new ArrayList<Player>(4);
	private boolean trickComplete;
	
	
	public Game(String person1, String person2, String person3, String person4) {
		
		this.deck = new Deck();
		this.players.add(new Player(person1,false,0));
		this.players.add(new Player(person2,false,1));
		this.players.add(new Player(person3,false,2));
		this.players.add(new Player(person4,true,3));
		this.roundCounter=0;
		this.gameIsFinished=false;
		this.gameStatus = Constants.STATUS_NO_INPUT_ALLOWED;
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
		return this.nextToStart;
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
	
	
	public void startNewGame() {
		this.roundCounter=0;
		this.gameIsFinished=false;
		this.startNewRound();
	}
	
	
	public void startNewRound() {
		this.deck.shuffle();
		this.roundCounter++;
		this.queenSpadesGone = false;
		this.heartsBroken = false;
		this.cardsPlayed = new Hand();
		this.roundComplete = false;
		
		//give everyone a new hand and determine who moves first
		for (int i=0;i<4;i++) {
			this.players.get(i).setRoundScore(0);
			this.players.get(i).setHand(new Hand(this.deck));
			this.players.get(i).getHand().sortHand();
			System.out.println(this.getPlayer(i).getName()+" has the following cards: ");
			for (int j=0;j<13;j++) {
				System.out.print(this.getPlayer(i).getHand().getCard(j).getName() + ", ");
			}
			System.out.println("");
			
			//TODO: this logic needs to be put elsewhere, in case I give the 2 of clubs away
			if (this.players.get(i).getHand().hasCard(Constants.CLUBS, Constants.TWO)) nextToStart=players.get(i);
		}
		
		this.updateRacks();
    	
    	if (this.roundCounter % 4 == 0) { 		//every fourth round, we don't get to discard cards
    		this.startNewTrick();
    	} else {
    		Platform.runLater(() -> content.showMessage(AlertType.INFORMATION, "Trade Cards", "Choose 3 cards to swap!", Constants.DONT_WAIT));
        	this.gameStatus = Constants.STATUS_SELECT_TRADE_CARDS;
    	}
    	
	}
	
	private void updateRacks() {
		
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
			e.printStackTrace();
		}
		
	}
	
	
	public void startNewTrick() {
		
		this.suitPlayed = 0;
		this.trickValue = 0;
		this.highestRankInSuit = null;
		this.trickWinner = this.nextToStart;
		this.nextToMove = this.nextToStart;
		this.cardsInTheTrick.clear();
		this.owners.clear();
		this.trickComplete = false;
		Game.this.mouseClicked = false;
		
		if (!this.nextToMove.isHuman()) {
			
			this.playCard();
			
		} else {
			
			this.gameStatus = Constants.STATUS_AWAITING_PLAYER_MOVE;
		}
		
	}
	
	
	
	public void humanTradeCard(int player, int suit, int rank) {
		
		Card card = this.players.get(player).getHand().getCard(suit,  rank);
		boolean added = this.players.get(player).addTradeCard(suit, rank);
		
		CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				content.playSelectionAnimation(player, card.getOriginalPosition(), added);
			} finally {
				latch.countDown();
			}
		});
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.players.get(player).getNoOfTradeCards() == Constants.NUMBER_OF_TRADES_ALLOWED) {
			this.gameStatus = Constants.STATUS_NO_INPUT_ALLOWED;
			
			this.computerTradeCard();
			this.swapTradeCards();
			this.startNewTrick();
		}
		
	}
	
	
	
	private void computerTradeCard() {
		
		Platform.runLater(() -> content.showWaitForMe(true));
		
		//step 1: every computer gets to pick 3 cards to discard
		for (int i = 0; i < Constants.NUMBER_OF_PLAYERS; i++) {
			
			Player currentPlayer = this.players.get(i);
			
			if (!currentPlayer.isHuman()) {					//for every computer player...
				
				System.out.println("Selecting trade cards for player" + i);
				Hand tempHand = new Hand(currentPlayer.getHand());
				Hand tempSwap = this.getSwapPlayer(currentPlayer.getPlayerNumber()).getTradeCards();
				
				if (tempSwap != null) System.out.println("Trade cards of swap player are " + tempSwap);
				else System.out.println("Swap hand is null");
				
				tempHand.addHand(tempSwap);
					
				for (int j = 0; j < Constants.NUMBER_OF_TRADES_ALLOWED; j++) {
					
					System.out.println("Choosing trade card #" + j);
					Card tempCard = tempHand.getBestCardToLose(true, this.cardsLeftInPlay(), tempSwap);
					currentPlayer.addTradeCard(tempCard);
					tempHand.remove(tempCard);
					
				}
			}
		}
		
	}
	
	
	private void swapTradeCards() {
		
		Hand cardsToAnimate = new Hand();
		
		for (int i = 0; i < Constants.NUMBER_OF_PLAYERS; i++) {
			
			Player currentPlayer = this.players.get(i);
			Player swapMate = this.getSwapPlayer(i);										//step 2: we determine who each person will swap with
			System.out.println("Swap mate: " + swapMate.getPlayerNumber());
			
			if (swapMate.isHuman()) {
				Platform.runLater(() -> content.removeEffects(swapMate.getPlayerNumber()));
				cardsToAnimate = new Hand(currentPlayer.getTradeCards());
			}
			
			currentPlayer.getHand().giveTo(swapMate,  currentPlayer.getTradeCards());		//step 3: we give the new cards to each player
			
			currentPlayer.resetTradeCards();
			
		}
		
		for (int i = 0; i < Constants.NUMBER_OF_PLAYERS; i++) {
			this.players.get(i).getHand().sortHand(); 										//step 4: we re-assign the cards to the new players
		}
		
		System.out.println("All done, now updating the racks with the new cards");
		this.updateRacks();
																							//step 5: we animate the new cards that the human player receives
		Platform.runLater(() -> content.showWaitForMe(false));
		
		Card[] cards = cardsToAnimate.toArray();
		Platform.runLater(() -> content.playFadeInAnimation(cards));
		for (int i = 0; i < this.deck.getNumberOfCards(); i++) {
			Card c = this.deck.getCard(i);
			System.out.println("Card " + c.getName() + " belongs to player " + c.getOwner() + " in position " + c.getOriginalPosition());
		}		
	}
	
	
	
	private Player getSwapPlayer(int location) {
		
		switch(this.roundCounter % 4) {
		case 0:
			break;
		case 1: //swap with the person on the left
			location++;
			if (location > 3) location = 0;
			break;
		case 2:
			location--;
			if (location < 0) location = 3;
			break;
		case 3:
			location = location - 2;
			if (location < 0) location = location + 4;
			break;
		default:
			break;
		}
		return this.getPlayer(location);
	}
	
	
	
	public void playCard() {
		
		Card playedCard = computerMove(this.nextToMove.getHand());
		this.finalizeMove(playedCard, this.nextToMove);
		
	}
	
	
	public Card computerMove(Hand h) {
		
		Hand cardsLeft = cardsLeftInPlay();
		
		if (this.cardsPlayed.size() < 4) {			//if we're in the first trick, there is no risk of gaining points, hence play biggest card
			
			if (this.cardsPlayed.size() == 0) {										//if we're first player...
				return h.playCard(Game.this.deck.getSpecificCard(Constants.CLUBS, Constants.TWO));		//... we need to play 2 of clubs
			}
			else { 
				System.out.println("WohAAA!");
				
				if (h.hasSuit(this.suitPlayed)) {									//if we have clubs
					return h.playCard(h.getHighestCardInSuit(this.suitPlayed));		//play highest card
				}
				else {
					return h.playCard(h.getBestCardToLose(false, cardsLeft, null));		//play dearest no-cost card to lose
				}
			}
		}
		
		if (this.cardsInTheTrick.isEmpty()) {								//first to play card. 3 possibilities: QoS is still out there / I have the QoS / QoS already played
			
			if (!this.queenSpadesGone) {									//if the queen is still out there, check if I should go for spades
				
				if (h.hasCard(Constants.SPADES,  Constants.QUEEN)) {
					
					//overall strategy should be: 
					// (1) check if I am empty on a suit. 
					// (2) if no, try to get blank on a suit
					// (3) if yes, lose ownership
					// (4) alternative approach: if blanking a suit is not likely possible, check if we are better off ridding the other's spades
					
					System.out.println("WohA!");
					
					//check if we're free on one or more suits - but we don't check if all others might be free as well
					if (h.getFreeSuits(false,  this.heartsBroken) > 0) {
						
						return h.playCard(h.getBestCardToLoseOwnership(cardsLeft, false,  this.heartsBroken));
					
					}
					
					SuitEvaluation suit = h.getBestSuitToDitch(cardsLeft, false, this.heartsBroken);		//check which is the best suit to ditch so that I can get maybe rid of my QoS. Exclude Spades and only include hearts if broken.
					System.out.println("Value of ditching a suit: " + suit.getValue() + " for suit " + Constants.SUIT_NAME[suit.getSuit()]);
					return h.playCard(h.getHighestCardInSuit(suit.getSuit()));
					
				} else {			//doesn't have QoS...
					
					if (h.hasSuit(Constants.SPADES)) {		// does he have spades at all?
						
						float estimate = h.getPlayOutSpadeSafety(cardsLeft);
						System.out.println("Estimate to safely play spades: "+estimate);
						
						if (estimate > 0.9f) {
							
							//we either don't have K or A, or we have so many spades that it's cool anyways
							System.out.println("WohAA!");
							return h.playCard(h.getHighestCardBelow(Constants.SPADES,  Constants.QUEEN));
						
						} else {		//check if the chance that my best card won't go through is higher than the chance that I can safely play spades
							
							float safestCard = h.calculateLossOfTrick(h.getBestCardToLoseOwnership(cardsLeft, false, this.heartsBroken), cardsLeft);
							System.out.println("Chance of best card to lose ownership:" + safestCard);
							
							if (estimate > safestCard) {	//should play Spades
								
								System.out.println("WohAB!");
								
								Card returnCard = h.getHighestCardBelow(Constants.SPADES,  Constants.QUEEN);
								
								if (returnCard == null) {
									//I only have King or Ace but all other cards are worse
									returnCard = h.getLowestCardInSuit(Constants.SPADES);
								}
								
								return h.playCard(returnCard);
								
							} else {		//play card that will least likely result in getting trick
								
								System.out.println("WohAC!");
								return h.playCard(h.getBestCardToLoseOwnership(cardsLeft, false, this.heartsBroken));
							}
							
						} 
					} else {
						
						//if I don't have spades, I need to play the card that is most likely to go through
						//TODO: strategy for optimizing cards when QoS still out there and player doesn't have spades
						return h.playCard(h.getBestCardToLoseOwnership(cardsLeft, false, this.heartsBroken));
						
					}
				}
				
			}
			//play the card that is most likely to move ownership or play a "painful" card
			//TODO: I should also calculate in if I know that someone doesn't have this suit
			//how do we run through the test to see if a card is painful?
			//let's just see what the values are
			
			System.out.print("Values for suits: ");
			
			for (int i=0; i<4; i++) {
				float a = h.getAverageHasSuit(i, cardsLeft);
				float b = h.getChanceForAllCardsComingIntoPlay(i,  cardsLeft);
				System.out.println(Constants.SUIT_NAME[i]+": "+h.getAverageHasSuit(i, cardsLeft)+", "+h.getChanceForAllCardsComingIntoPlay(i, cardsLeft));
			}
			System.out.println("WohB!");
			return h.playCard(h.getBestCardToLoseOwnership(cardsLeft, true, this.heartsBroken));
			
		} else {			//not first to play card
			
			if (h.hasSuit(this.suitPlayed)) {									//must play suit
				
				if (this.cardsInTheTrick.size()==3) {								//if I'm the last player...
					
					if (h.getCardsAbove(this.highestRankInSuit)==0) { 				//...and none of my cards can go over
						if ((this.suitPlayed == Constants.SPADES) && (h.getQoS()!=null)) {
							return h.playCard(h.getQoS());							//if Spades is played and I have QoS, play it
						} else {
							return h.playCard(h.getHighestCardInSuit(this.suitPlayed));	//...play highest card
						}
					}
					
					if (h.getCardsBelow(this.highestRankInSuit)==0) {				//...but if ALL my cards are above
						return h.playCard(h.getHighestNoCostCardInSuit(this.suitPlayed)); //...then return the highest card other than the QoS
					}
					
					//we can go under OR go over
					
					if (this.suitPlayed == Constants.SPADES) {						//if Spades, always play the highest no cost card
						return h.playCard(h.getHighestNoCostCardInSuit(this.suitPlayed));
					}
					
					//else we need to check the average points made for each card
					Hand suit = h.getCardsOfSuit(this.suitPlayed);
					float maxValue = 0;
					Card returnCard = null;
					
					for (int i=0; i < suit.size(); i++) {
						
						Card thisCard = suit.getCard(i);
						float tempValue = suit.getCardValue(thisCard, cardsLeft);
						System.out.println("Card value of " + thisCard.getName() + ": " + tempValue);
						
						if (tempValue > maxValue) {
							maxValue = tempValue;
							returnCard = thisCard;
						}
					}
						
					if (maxValue > this.trickValue) { //if the average expected cost of the worst card is higher than the trick is worth
						return h.playCard(returnCard);
					} else {
						return h.playCard(h.getHighestCardBelow(this.highestRankInSuit)); //should never be NULL as we've already checked that we've got a card below
					}
				}
				
				//I am NOT the last player, so what is the logic now?
				
				Card playCard=h.getHighestCardBelow(this.highestRankInSuit);
				
				if (playCard == null) {
					//if the hand holds no card below the highest card played, play highest card of the suit
						//here, we would also need to add additional logic
					//should check what the highest number of points is that I could get with this trick
					//should then check the different likelihood of winning the trick per card
					if (!this.queenSpadesGone)  {
						//here, I would first need to check if I have the QoS and spades is played. Yes indeed!
						System.out.println("WohCB!");
						return h.playCard(h.getLowestCardInSuit(this.suitPlayed)); 
					}
					else {
						System.out.println("WohDD!");
						return h.playCard(h.getHighestCardInSuit(this.suitPlayed));
					}
				}  else {			//otherwise play the highest card in the hand that's below the highest card played
					System.out.println("WohEE!");
					return h.playCard(playCard);
				}
			} else {						//doesn't have the suit played
				
				System.out.println("WohFF!");
				return h.playCard(h.getBestCardToLose(true, cardsLeft, null));
			}
		}
		
	}
	
	
	public void humanMove(int suit, int rank) {
		
		//we need to wait until not only a card has been played, but also until this card is deemed valid
		boolean chosenCardValid = false;
		Card clickedCard = null;
		Hand h = this.nextToMove.getHand();
		
		System.out.println("humanMove called.");
		System.out.println("We're playing a card now!");
			
		//get card from hand
		clickedCard = h.getCard(suit, rank);
			
		//if the card doesn't exist, we've got a problem
			if (clickedCard == null) {
				System.err.println("Oh my god - card was chosen that doesn't exist! Suit: " + suit + ", Rank: " + rank);
				Platform.exit();
			}
			
		//various checks if card is legal to play
			chosenCardValid = true;
			String errorMessage = "";
			
			//if first round and first player, player can only play 2 of clubs
			if (this.cardsPlayed.size()==0) {
				if ((clickedCard.getRank()!=0)||(clickedCard.getSuit()!=0)) {
					chosenCardValid = false;
					errorMessage = Constants.ERROR_2_OF_CLUBS;
				}
			}
			
			//if first player and hearts are not broken, player can't play hearts
			if ((this.cardsInTheTrick.isEmpty())&&(clickedCard.getSuit()==Constants.HEARTS)&&(!this.heartsBroken)) {
				chosenCardValid = false;
				errorMessage = Constants.ERROR_HEARTS_NOT_BROKEN;
			}
			//if not first player, player must play suit if he has it
			if (!(this.cardsInTheTrick.isEmpty())&&(this.suitPlayed!=clickedCard.getSuit())&&(h.hasSuit(this.suitPlayed))) {
				chosenCardValid = false;
				errorMessage = Constants.ERROR_PLAY_SUIT + Constants.SUIT_NAME[this.suitPlayed] + "!"; 
			}
			
			//if choice was not legal, we need to reset lock and try again
			if (chosenCardValid == false) {
				
				//send out error message
				System.out.println("dude, the " + clickedCard.getName() + " is not a valid card!");
				System.out.println("Because: " + errorMessage);
				final String msg = errorMessage;
				Platform.runLater(() -> content.showMessage(AlertType.ERROR, Constants.ERROR_TITLE, msg, Constants.WAIT));
				
			} else {
				
				//move is legal, finalize it
				this.gameStatus = Constants.STATUS_NO_INPUT_ALLOWED;
				this.finalizeMove(h.playCard(clickedCard), this.nextToMove);
				
			}
	}

	
	
	public void finalizeMove(Card card, Player p) {
		
			System.out.println(p.getName()+" plays the "+card.getName());
			//now we need to animate the card going into the trick
			//and tell the card that it's in the trick
			int cardPosition = card.getOriginalPosition();
			card.setPosition(-1*(this.cardsInTheTrick.size()+1));
			
			//trigger animation
			System.out.println("Triggering a new onPlayCard event");
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				try {
					content.playCard(card, cardPosition);
				} finally {
					latch.countDown();
				}
			});
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(!content.isAnimationComplete()) {}
			
			//if the first card: determine suit played
			if(this.cardsInTheTrick.isEmpty()) {
				this.suitPlayed=card.getSuit();
				this.highestRankInSuit=card;
			}
			//if suit is suit played then check if current card is highest card
			if (card.getSuit() == this.suitPlayed) {
				if (card.getRank() > this.highestRankInSuit.getRank()) {
					this.highestRankInSuit=card;
					this.trickWinner=p;
				}
			}
			
			//update trick value
			this.trickValue=this.trickValue+card.getCost();
			System.out.println("Trick value now is "+this.trickValue);
			this.cardsInTheTrick.add(card);
			this.cardsPlayed.add(card);
			this.owners.add(p);
			
			//next player
			this.nextToMove=Game.this.getNextPlayer(this.nextToMove);
			
			//if not the last card in the trick, play next card
			if (this.cardsInTheTrick.size() < 4) {
				
				if (!this.nextToMove.isHuman()) this.playCard();
				else this.gameStatus = Constants.STATUS_AWAITING_PLAYER_MOVE;
				
			} else {
				
				this.finalizeTrick();
				
			}
		}

	public void finalizeTrick() {
		
		//else finalize the trick and return
		//TODO: add a confirmation screen

		//update score
		this.trickWinner.increaseRoundScore(this.trickValue);
		if (this.trickValue > 4) this.queenSpadesGone = true;
		if ((this.trickValue > 0) && (this.trickValue!=13)) this.heartsBroken = true;
		
		//set starter for next trick
		this.nextToStart=this.trickWinner;
		
		//Game.this.showDialog(this.trickWinner.getName()+" has won the trick and has now " + this.trickWinner.getRoundScore() + " points.", this.trickWinner.getName() + " won the trick");
		this.trickComplete=true;
		
		try {
			Thread.sleep(2000);
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				try {
					content.clearTableCenter(this.trickWinner.getPlayerNumber());
				} finally {
					latch.countDown();
				}
			});
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//remove cards from game - not sure if needed
		for (int i=0; i < this.cardsInTheTrick.size(); i++) {
			cardsInTheTrick.get(i).setOwner(-1);
		}
		
		this.mouseClicked=false;
		
		if (this.cardsPlayed.size() < this.deck.getNumberOfCards()) {
			
			//not all cards played yet
			
			System.out.println("Currently played cards: " + this.cardsPlayed.size());
			System.out.println("Overall cards in the deck: " + this.deck.getNumberOfCards());
			this.startNewTrick();
			
		} else {
			
			this.finalizeRound();
			
		}
		
	}
	
	public void finalizeRound() {
		
		//display scores
		this.roundComplete = true;
		this.updateRoundResults();
		
		if (!this.gameIsFinished) {
			
			this.startNewRound();
			
		} else {
			
			this.finalizeGame();
		}
				
	}
	
	public void finalizeGame() {
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	
	
	
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
	
	*/
	
	public void updateRoundResults() {
		
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

		System.out.println("Round finished!");
		
	}
	
	
	
	//do we need this?
	/*
	public void setPlayCardEventListener(PlayCardEventListener theListener) {
		this.listener = theListener;
	}
	*/
	
	//this is the action called when a user clicks on a card
	public void selectCard(int player, int suit, int rank) {
		
		System.out.println("user clicked on card " + suit + ", " + rank);
		
		//we should only consider clicks that are performed while it's the person's turn (isHuman())
		//and also ignore other actions when one valid action has already been given (humanMoved)
		
		switch (this.gameStatus) {
		
		case Constants.STATUS_AWAITING_PLAYER_MOVE: 
			this.humanMove(suit, rank);
			break;
		
		case Constants.STATUS_SELECT_TRADE_CARDS:
			this.humanTradeCard(player, suit, rank);
			break;
			
		default:
			break;
		
		}
		
	}
	
	
	
	/*
	 ********************************
	 * Class ROUND
	 ********************************
	 
	
	class Round {
		
		private Hand cardsPlayed;
		private Player nextToStart;
		private boolean queenSpadesGone;
		private boolean heartsBroken;
		private Trick trick;
		private boolean roundComplete;
		
		public Round() {
			deck.shuffle();
			Game.this.roundCounter++;
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
		 
		
		
		
		class Trick {
			
			private int suitPlayed;
			private int trickValue;
			private Card highestRankInSuit;
			private int rankOfHumanCardPlayed;
			private int suitOfHumanCardPlayed;
			private Player nextToMove;
			private Player trickWinner;
			private boolean humanMoved;
			private ArrayList<Card> cardsInTheTrick = new ArrayList<Card>(4);
			private ArrayList<Player> owners = new ArrayList<Player>(4);
			private boolean trickComplete;
			
			public Trick(Player startingPlayer) {
				this.suitPlayed = 0;
				this.trickValue = 0;
				this.highestRankInSuit = null;
				this.trickWinner = startingPlayer;
				this.nextToMove = startingPlayer;
				this.cardsInTheTrick.clear();
				this.owners.clear();
				this.rankOfHumanCardPlayed = 0;
				this.suitOfHumanCardPlayed = 0;
				this.trickComplete = false;
				this.humanMoved = false;
				Game.this.mouseClicked = false;
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
				
				this.playCard(this.nextToMove);
					
			}
			
			public void playCard(Player p) {
				
				Card playedCard;
				//play
				//if (p.isHuman()) playedCard=this.humanMove(p.getHand()); else playedCard=this.computerMove(p.getHand());
				if (!p.isHuman()) {
					playedCard = this.computerMove(p.getHand());
					this.continueToPlayCard(p, playedCard);
				}
				
				
			}
			
			public void continueToPlayCard(Player p, Card card) {
				System.out.println(p.getName()+" plays the "+card.getName());
				//now we need to animate the card going into the trick
				//and tell the card that it's in the trick
				int cardPosition = card.getOriginalPosition();
				card.setPosition(-1*(this.cardsInTheTrick()+1));
				//trigger animation
				System.out.println("Triggering a new onPlayCard event");
				CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					content.playCard(card, cardPosition);
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
					this.suitPlayed=card.getSuit();
					this.highestRankInSuit=card;
				}
				//if suit is suit played then check if current card is highest card
				if (card.getSuit()==this.suitPlayed) {
					if (card.getRank()>this.highestRankInSuit.getRank()) {
						this.highestRankInSuit=card;
						this.trickWinner=p;
					}
				}
				//update trick value
				this.trickValue=this.trickValue+card.getCost();
				System.out.println("Trick value now is "+this.trickValue);
				this.cardsInTheTrick.add(card);
				Round.this.cardsPlayed.add(card);
				this.owners.add(p);
				
				//next player
				this.nextToMove=Game.this.getNextPlayer(this.nextToMove);
				
				//now we can also release the "human player moved" lock to prevent illegal updates to the chosen card
				this.humanMoved = false;
				
				//if not the last card in the trick, play next card
				if (cardsInTheTrick()<4) {
					
					this.playCard(this.nextToMove);
					
				} else {
					//else finalize the trick and return
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
						if (h.hasCard(Constants.SPADES,  Constants.QUEEN)) {
							//go for best card to lose
							//TODO: include code for what to do if hearts are broken
							//TODO: if player is blank on one suit, he better try to lose ownership, especially late in the game
							//TODO: No good if the suit with the least cards is spades!
							System.out.println("WohA!");
							return h.playCard(h.getHighestCardInSuit(h.getSuitWithLeastCards(false)));
							
						} else {
							//if I don't have spades, I need to play the card that is most likely to go through
							if (h.hasSuit(Constants.SPADES)) {
								float estimate = h.getPlayOutSpadeSafety(cardsLeft);
								System.out.println("Estimate to safely play spades: "+estimate);
								if (estimate>0.9f) {
									//we either don't have K or A, or we have so many spades that it's cool anyways
									System.out.println("WohAA!");
									return h.playCard(h.getHighestCardBelow(Constants.SPADES,  Constants.QUEEN));
								} else {
									//check if the chance that my best card will go through is higher than the chance that I can safely play spades
									float safestCard = h.calculateLossOfTrick(h.getBestCardToLoseOwnership(cardsLeft), cardsLeft);
									if (estimate>safestCard) {
										//play spades
										System.out.println("WohAB!");
										return h.playCard(h.getHighestCardBelow(Constants.SPADES,  Constants.QUEEN));
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
			
			public void humanMove(Hand h) {
				
				//we need to wait until not only a card has been played, but also until this card is deemed valid
				boolean chosenCardValid = false;
				Card clickedCard = null;
				System.out.println("humanMove called.");
				while (chosenCardValid == false) {
					//wait until manual card clicked
					while(!humanMoved) {
					}
					
					System.out.println("We're playing a card now!");
					//get card from hand
					clickedCard = h.getCard(this.suitOfHumanCardPlayed, this.rankOfHumanCardPlayed);
					
					//if the card doesn't exist, we've got a problem
					if (clickedCard == null) {
						System.err.println("Oh my god - card was chosen that doesn't exist! Suit: " + this.suitOfHumanCardPlayed + ", Rank: " + this.rankOfHumanCardPlayed);
						Platform.exit();
					}
					
				//various checks if card is legal to play
					chosenCardValid = true;
					String errorMessage = "";
					
					//if first round and first player, player can only play 2 of clubs
					if (Round.this.cardsPlayed.size()==0) {
						if ((clickedCard.getRank()!=0)||(clickedCard.getSuit()!=0)) {
							chosenCardValid = false;
							errorMessage = Constants.ERROR_2_OF_CLUBS;
						}
					}
					
					//if first player and hearts are not broken, player can't play hearts
					if ((this.cardsInTheTrick.isEmpty())&&(clickedCard.getSuit()==Constants.HEARTS)&&(!Round.this.heartsBroken)) {
						chosenCardValid = false;
						errorMessage = Constants.ERROR_HEARTS_NOT_BROKEN;
					}
					//if not first player, player must play suit if he has it
					if (!(this.cardsInTheTrick.isEmpty())&&(this.suitPlayed!=clickedCard.getSuit())&&(h.hasSuit(this.suitPlayed))) {
						chosenCardValid = false;
						errorMessage = Constants.ERROR_PLAY_SUIT + Constants.SUIT_NAME[this.suitPlayed] + "!"; 
					}
					
					//if choice was not legal, we need to reset lock and try again
					if (chosenCardValid == false) {
						//send out error message
						System.out.println("dude, the " + clickedCard.getName() + " is not a valid card!");
						System.out.println("Because: " + errorMessage);
						final String msg = errorMessage;
						Platform.runLater(() -> content.showErrorMessage(msg));
						//unlock lock
						humanMoved = false;
					}
				}
			
				
				//move is legal
				this.continueToPlayCard(Game.this.getPlayer(3), h.playCard(clickedCard));
				
			}
			
		}
		
		
	}
	
*/

}
