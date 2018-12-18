package heart;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javafx.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.event.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class Screen extends BorderPane {
	
	private Game game;
	private Deck deck;
	private Thread backgroundThread;
	private GridPane gridPane;
	private Image backside;
	private TableView<RoundScores> scoresTable;
	private final ObservableList<RoundScores> tableData =  FXCollections.observableArrayList();

	private CardRack[] rack = new CardRack[4];
	//aaa private TableCenter trick;
	private StackPane topRow = new StackPane();
	private StackPane middleRowLeft = new StackPane();
	private Pane borderPaneCenter = new Pane();
	private StackPane middleRowCenter = new StackPane();
	private StackPane middleRowRight = new StackPane();
	private StackPane bottomRow = new StackPane();
	private Background green = new Background(new BackgroundFill(Color.rgb(0,128,6), null, null));
	private SequentialTransition transitionQueue = new SequentialTransition();
	private boolean renderFinished;
	private volatile boolean animationComplete;
	private Alert waitForMe;
	//offsets for cards in the table center
	double[] offsetX = new double[] {0.0,0.5,1.0,0.5};
	double[] offsetY = new double[] {0.5,0.0,0.5,1.0};
	
	public Screen() {
		this(new Deck(), new Game("Player A", "Player B", "Player C", "Player D"), new Thread());
	}
	
	public Screen(Deck theDeck, Game theGame, Thread backgroundThread) {
		this.deck = theDeck;
		this.game = theGame;
		this.backgroundThread = backgroundThread;
		this.renderFinished = false;
		this.animationComplete = true;
		//UPDATE this.game.setPlayCardEventListener(this);
	    this.backside = new Image(this.getClass().getResourceAsStream(Constants.BACKSIDE_IMG_FILE));
	    
	    //setting up score table
	  	TableColumn roundColumn = new TableColumn("Round");
	  	roundColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
	  	roundColumn.setCellValueFactory(new PropertyValueFactory<>("roundNumber"));
	  	TableColumn player1Column = new TableColumn(this.game.getPlayer(0).getName());
	  	player1Column.setStyle("-fx-alignment: CENTER-RIGHT;");
	  	player1Column.setCellValueFactory(new PropertyValueFactory<>("player1Score"));
	  	TableColumn player2Column = new TableColumn(this.game.getPlayer(1).getName());
	  	player2Column.setStyle("-fx-alignment: CENTER-RIGHT;");
	  	player2Column.setCellValueFactory(new PropertyValueFactory<>("player2Score"));
	  	TableColumn player3Column = new TableColumn(this.game.getPlayer(2).getName());
	  	player3Column.setStyle("-fx-alignment: CENTER-RIGHT;");
	  	player3Column.setCellValueFactory(new PropertyValueFactory<>("player3Score"));
	  	TableColumn player4Column = new TableColumn(this.game.getPlayer(3).getName());
	  	player4Column.setStyle("-fx-alignment: CENTER-RIGHT;");
	  	player4Column.setCellValueFactory(new PropertyValueFactory<>("player4Score"));
	  	this.scoresTable = new TableView();
	  	scoresTable.setItems(tableData);
	  	scoresTable.getColumns().addAll(roundColumn, player1Column, player2Column, player3Column, player4Column);
	  			  		
		initializeTable();

	}
	
	public void initializeTable() {
		this.setPrefSize(700, 700);		 
		this.setPadding(new Insets(30,20,30,20));
		topRow.setBackground(green);
		topRow.setAlignment(Pos.CENTER);
		this.setTop(topRow);
		bottomRow.setBackground(green);
		bottomRow.setAlignment(Pos.CENTER);
		this.setBottom(bottomRow);
		middleRowLeft.setBackground(green);
		middleRowLeft.setPrefSize(100, 433);
		middleRowLeft.setAlignment(Pos.CENTER);
		this.setLeft(middleRowLeft);
		middleRowCenter.setBackground(green);
		borderPaneCenter.getChildren().add(middleRowCenter);
		this.setCenter(borderPaneCenter);
		middleRowRight.setBackground(green);
		middleRowRight.setPrefSize(100, 433);
		middleRowRight.setAlignment(Pos.CENTER);
		this.setRight(middleRowRight);
		this.setBackground(green);
		
		//set clipping for middleRowCenter
		final Rectangle centerClip = new Rectangle();
	    borderPaneCenter.setClip(centerClip);

	    borderPaneCenter.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
	        centerClip.setWidth(newValue.getWidth());
	        centerClip.setHeight(newValue.getHeight());
	    }); 
	    
	    borderPaneCenter.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
	        middleRowCenter.setPrefSize(newValue.getWidth(), newValue.getHeight());
	    });

 		for (int i=0;i<4;i++) {
 			rack[i] = new CardRack(i);
 			switch (i) {
 			case 0:
 				middleRowLeft.getChildren().add(rack[i]);
 				StackPane.setAlignment(rack[i], Pos.CENTER);
 				break;
 			case 1:
 				topRow.getChildren().add(rack[i]);
 				StackPane.setAlignment(rack[i], Pos.CENTER);
 				break;
 			case 2:
 				middleRowRight.getChildren().add(rack[i]);
 				StackPane.setAlignment(rack[i], Pos.CENTER);
 				break;
 			case 3:
 				bottomRow.getChildren().add(rack[i]);
 				StackPane.setAlignment(rack[i], Pos.CENTER);
 				break;
 			}
 			rack[i].setVisible(true);
 		}
 		
 		//don't try to animate things before the whole table is drawn
 		renderFinished = true;
	}
	
	public void clearTableCenter(int winner) {
		
		double windowWidth = borderPaneCenter.getWidth();
		double windowHeight = borderPaneCenter.getHeight();
		Double[] x = new Double[] {0.0,0.5,1.0,0.5};
		Double[] y = new Double[] {0.5,0.0,0.5,1.0};
		
		int numberOfCards = borderPaneCenter.getChildren().size();
		ArrayList<CardImage> cards = new ArrayList();
		
		for(int i=1; i < numberOfCards; i++) {
			cards.add((CardImage)borderPaneCenter.getChildren().get(i));
		}
		
		for (int i=0; i < cards.size(); i++) {
			CardImage card = cards.get(i);
			double cardWidth = card.getBoundsInParent().getWidth();
			double cardHeight = card.getBoundsInParent().getHeight();
			//System.out.println("Card dimensions: "+cardWidth + ", " + cardHeight);

			double newX = x[winner]*windowWidth + ((2.0*x[winner])-1)*cardWidth;
			double newY = y[winner]*windowWidth + ((2.0*y[winner])-1)*cardHeight;

			TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), card);
		    animation.setToX(newX);
		    animation.setToY(newY);
		    animation.setOnFinished(event -> borderPaneCenter.getChildren().remove(card));
		    animation.play();
		}
	}

	
	public void updateRacks() {
		for (int i=0; i<4; i++) {
			rack[i].updateCards();
		}
		//aaa System.out.println("Size of trick: " + trick.getWidth() + ", " + trick.getHeight());
	}
	
	
	public void clickCard(int location, int suit, int rank) {	
		
		//check status of current backgroundThread
		
		System.out.println("State of background thread: " + backgroundThread.getState());
		
		Runnable task = new Runnable() {
			public void run() {
				game.selectCard(location, suit, rank);
			}
		};
        // Run the task in a background thread
		this.backgroundThread = new Thread(task);
		// should be user thread
		backgroundThread.setDaemon(false);
		// Start the thread
		 backgroundThread.start();
	}
	

	
	public AnchorPane getDialogRack() {
		//used to not need to display a dialog box directly on top of the trick
		return rack[1];
	}
	
	
	public void playCard(Card theCard, int originalPosition) {
		
		//use this method to do the full trick animation incl setting image and final position...

		double windowWidth = borderPaneCenter.getWidth();
		double windowHeight = borderPaneCenter.getHeight();

		System.out.println("Trick width and height: "+ windowWidth + ", " + windowHeight);
		
		//remove card from rack
		int owner = theCard.getOwner();
		rack[owner].card[originalPosition].shouldDraw = false;
		rack[owner].updateCards();
		
		//create card for trick and calculate positions
		CardImage card = new CardImage();
		card.setGraphic(new ImageView(theCard.getImage()));
		borderPaneCenter.getChildren().add(card);
		borderPaneCenter.applyCss();
		borderPaneCenter.layout();
		double cardWidth = card.getBoundsInParent().getWidth();
		double cardHeight = card.getBoundsInParent().getHeight();
		//System.out.println("Card dimensions: "+cardWidth + ", " + cardHeight);
		double oldX = 0;
		double oldY = 0;
		double newX = 0;
		double newY = 0;
		
		oldX = (offsetX[owner]*windowWidth)-((1.0-offsetX[owner])*cardWidth);
		oldY = (offsetY[owner]*windowHeight)-((1.0-offsetY[owner])*cardHeight);
		newX = Math.round(windowWidth*0.5 - cardWidth + (offsetX[owner]*2*cardWidth*0.6));
		newY = Math.round(windowHeight*0.5 - cardHeight + (offsetY[owner]*2*cardHeight*0.6));
		
		card.setTranslateX(oldX);
		card.setTranslateY(oldY);
		
		System.out.println("animating card for owner " + owner + " from x="+oldX+", y="+oldY+" to x="+newX+", y="+newY);
		
		TranslateTransition animation = new TranslateTransition(Duration.seconds(0.75), card);
	    animation.setToX(newX);
	    animation.setToY(newY);
	    
	    Screen.this.transitionQueue.stop();
	    Screen.this.transitionQueue.getChildren().add(animation);

		if (renderFinished) {
			this.animationComplete = false;
			Screen.this.transitionQueue.setOnFinished(event -> {
				this.animationComplete = true;
				Screen.this.transitionQueue.getChildren().clear();
			});
			System.out.println("Kicking off TransitionQueue, current size: " + Screen.this.transitionQueue.getChildren().size());
			Screen.this.transitionQueue.play();
		} else {
			System.out.println("Render not finished yet - will not play transition queue");
		}
		
	}
	
	public boolean isAnimationComplete() {
		return this.animationComplete;
	}
	
	
	public void playSelectionAnimation(int player, int position, boolean activate) {
		
		CardImage card = this.rack[player].card[position];
		
		if (activate) {
			ColorAdjust tone = new ColorAdjust(0, 0.5, 0, 0);
			card.setEffect(tone);
		} else {
			card.setEffect(null);
		}

	}
	
	public void removeEffects(int player) {
		
		for (int i = 0; i < this.rack[player].card.length; i++) {
			this.rack[player].card[i].setEffect(null);
		}
	}
	
	
	public void playFadeInAnimation(Card[] theCards) {
		
		for (int i=0; i < theCards.length; i++) {
			
			int owner = theCards[i].getOwner();
			int pos = theCards[i].getOriginalPosition();
			CardImage cardImage = rack[owner].card[pos];
			
			FadeTransition fadeIn = new FadeTransition(Duration.millis(5000), cardImage);
		    fadeIn.setFromValue(0.0);
		    fadeIn.setToValue(1.0);
		    fadeIn.play();
			
		}
		
	}
	
	
	
	public void setCards() {
		int rackNumber = 0;
		
		for (int i=0;i<Constants.SUITS_IN_THE_DECK;i++) {		
			for (int j=0; j < Constants.CARDS_IN_A_SUIT; j++) {
				Card theCard = Screen.this.deck.getSpecificCard(i, j);
				int k = theCard.getOriginalPosition();
				rackNumber = theCard.getOwner();
				//System.out.println("Setting " + theCard.getName() + " to player " + rackNumber + " and card " + k);
				rack[rackNumber].card[k].shouldDraw = true;
				rack[rackNumber].card[k].isSelected = false;
				rack[rackNumber].card[k].setImage(theCard.getImage());
				rack[rackNumber].card[k].cardRank = j;
				rack[rackNumber].card[k].cardSuit = i;
			}
		}
		
		this.updateRacks();
	}
	
	public void showRoundResults(int round, Integer[] scores) {

		//adding row of results to table
		 tableData.add(new RoundScores(round, scores[0], scores[1], scores[2], scores[3]));
        
        //TODO: add totals row
		 
		boolean alertConfirmed = false;
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Scoreboard");
		alert.setHeaderText("Current Standings:");
		alert.getDialogPane().setContent(this.scoresTable);
		alert.showAndWait();
	}
	
	public void showMessage(AlertType alertType, String title, String errorMessage, boolean wait) {
		
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(errorMessage);
		if (wait) alert.showAndWait();
		else alert.show();
		
	}
	
	public void showWaitForMe(boolean show) {
		
		if (show) {
			this.waitForMe = new Alert(AlertType.CONFIRMATION);
			this.waitForMe.setTitle("Please wait");
			this.waitForMe.setHeaderText("Please wait for the other players choose their trade cards.");
			this.waitForMe.show();
		} else {
			this.waitForMe.close();
		}
		
	}
	
	
	
/*
 * 
 * ROUND SCORES
 * 
 */
	
	public class RoundScores {
		private final SimpleIntegerProperty roundNumber;
		private final SimpleIntegerProperty player1Score;
		private final SimpleIntegerProperty player2Score;
		private final SimpleIntegerProperty player3Score;
		private final SimpleIntegerProperty player4Score;
		
		private RoundScores(int roundNumber, int p1score, int p2score, int p3score, int p4score) {
			this.roundNumber = new SimpleIntegerProperty(roundNumber);
			this.player1Score = new SimpleIntegerProperty(p1score);
			this.player2Score = new SimpleIntegerProperty(p2score);
			this.player3Score = new SimpleIntegerProperty(p3score);
			this.player4Score = new SimpleIntegerProperty(p4score);
		}
		
		public int getRoundNumber() {
			return this.roundNumber.get();
		}
		
		public void setRoundNumber(int round) {
			this.roundNumber.set(round);
		}
		
		public int getPlayer1Score() {
			return this.player1Score.get();
		}
		
		public void setPlayer1Score(int score) {
			this.player1Score.set(score);
		}
		
		public int getPlayer2Score() {
			return this.player2Score.get();
		}
		
		public void setPlayer2Score(int score) {
			this.player2Score.set(score);
		}
		
		public int getPlayer3Score() {
			return this.player3Score.get();
		}
		
		public void setPlayer3Score(int score) {
			this.player3Score.set(score);
		}
		
		public int getPlayer4Score() {
			return this.player4Score.get();
		}
		
		public void setPlayer4Score(int score) {
			this.player4Score.set(score);
		}
		
	}
	
	
	
	/*
	 * 		
	 * CARD RACK
	 * 
	 */
	
	
	public class CardRack extends AnchorPane {
		
		private boolean isVertical;
		private int location; 
		private CardImage[] card = new CardImage[13];
		private double startX, startY;
		private double offsetX, offsetY;
		private double cardSelectedOffsetX, cardSelectedOffsetY;
		
		public CardRack(int loc) {
			System.out.println("CardRack constructor called.");
			isVertical = (loc % 2 == 0)? true:false;;
			location = loc;
			
			if (isVertical) {
				this.setMaxSize(100, 433);
				this.setPrefSize(100, 433);
				this.setMinSize(100, 433);
				this.setBackground(green);
				this.offsetX = 0;
				this.offsetY = 25;
				this.startX = 0;
				this.startY = 0;
				this.cardSelectedOffsetX = (location-1)*(-30);
				this.cardSelectedOffsetY = 0;
				
			} else {
				
				this.setMaxSize(460, 133);
				this.setPrefSize(460, 133);
				this.setMinSize(460, 133);
				this.setBackground(green);
				this.offsetX = 30;
				this.offsetY = 0;
				this.startX = 0;
				this.startY = 0;
				this.cardSelectedOffsetX = 0;
				this.cardSelectedOffsetY = (location-2)*(-25);
				
			}

			for (int i=0;i<13;i++) {
					card[i] = new CardImage();
					card[i].setAlignment(Pos.CENTER);
					card[i].shouldDraw = true;
					final int pointer = i;
					if (location==3) card[i].setOnMouseClicked(event -> Screen.this.clickCard(location, card[pointer].cardSuit, card[pointer].cardRank));
					
			}
			
		}
		

		
			
		public void updateCards() {
			double panelWidth = this.getWidth();
			double panelHeight =this.getHeight();
			
			int locationCounter = 0;
			
			this.getChildren().clear();
			
			int cardsShown = 0;
			for (int i=0;i<13;i++) {
				if (card[i].shouldDraw) {
					int selected = card[i].isSelected?1:0;
					card[i].setLayoutX(startX+(offsetX*cardsShown)+(selected*this.cardSelectedOffsetX));
					card[i].setLayoutY(startY+(offsetY*cardsShown)+(selected*this.cardSelectedOffsetY));
					card[i].setVisible(true);
					this.getChildren().add(card[i]);
					cardsShown++;
				}
			}			
		}
		
	}
	

	
	
	public class CardImage extends Label {
		
		boolean shouldDraw;
		boolean isSelected;
		int cardSuit;
		int cardRank;
		
		public CardImage() {
			this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3.5), BorderWidths.DEFAULT)));
			this.setMinSize(91, 133);
			this.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(3.5), null)));
			this.setGraphic(new ImageView(Screen.this.backside));
			this.shouldDraw = true;
			this.isSelected = false;
		}
		
		public void shouldDraw(boolean value) {
			this.shouldDraw = value;
		}		
		
		public void setImage(Image image) {  	
			this.setGraphic(new ImageView(image));
			//if (this.shouldDraw) this.setVisible(true); else this.setVisible(false);
		}
		
	}

}
