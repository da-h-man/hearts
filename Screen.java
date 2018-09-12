package heart;

import java.lang.reflect.InvocationTargetException;

import javafx.*;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class Screen extends BorderPane {
	
	private Game game;
	private Deck deck;
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
	//offsets for cards in the table center
	double[] offsetX = new double[] {0.0,0.5,1.0,0.5};
	double[] offsetY = new double[] {0.5,0.0,0.5,1.0};
	
	public Screen() {
		this(new Deck(), new Game("Player A", "Player B", "Player C", "Player D"));
	}
	
	public Screen(Deck theDeck, Game theGame) {
		this.deck = theDeck;
		this.game = theGame;
		this.renderFinished = false;
		//UPDATE this.game.setPlayCardEventListener(this);
	    this.backside = new Image(this.getClass().getResourceAsStream(Constants.BACKSIDE_IMG_FILE));
	    
	    //setting up score table
	  	TableColumn roundColumn = new TableColumn("Round");
	  	roundColumn.setCellValueFactory(new PropertyValueFactory<>("roundNumber"));
	  	TableColumn player1Column = new TableColumn(this.game.getPlayer(0).getName());
	  	player1Column.setCellValueFactory(new PropertyValueFactory<>("player1Score"));
	  	TableColumn player2Column = new TableColumn(this.game.getPlayer(1).getName());
	  	player2Column.setCellValueFactory(new PropertyValueFactory<>("player2Score"));
	  	TableColumn player3Column = new TableColumn(this.game.getPlayer(2).getName());
	  	player3Column.setCellValueFactory(new PropertyValueFactory<>("player3Score"));
	  	TableColumn player4Column = new TableColumn(this.game.getPlayer(3).getName());
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
		
		//add confirmation click listener
		//long eventMask = AWTEvent.MOUSE_EVENT_MASK;
      //  Toolkit.getDefaultToolkit().addAWTEventListener(new WaitForConfirmationClick(), eventMask);
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			 
            public void handle(MouseEvent event) {
                System.out.println("Mouse Event triggered in gridPane");
                game.mouseClicked=true;
                
             //I think this is called when a new trick starts?
                borderPaneCenter.getChildren().clear();
                transitionQueue.getChildren().clear();
                
				for (int i=0;i<4;i++) {
					//aaa trick.card[i].setVisible(false);
					//aaa trick.card[i].shouldDraw(false);
				}
            }
        });
 		
 		//aaa trick = new TableCenter();
 		/*
 		 * HBox box1 = new HBox();
 		 
 		VBox box2 = new VBox();
 		box1.setAlignment(Pos.CENTER);
 		box2.setAlignment(Pos.CENTER);
 		box2.getChildren().add(trick);
 		box1.getChildren().add(box2);
 		middleRowCenter.getChildren().add(box1);
 		*/
 		//aaa StackPane.setAlignment(trick,  Pos.CENTER);
 		//aaa middleRowCenter.getChildren().add(trick);

 		for (int i=0;i<4;i++) {
 			boolean vertical = ((i==0)||(i==2))? true:false;
 			rack[i] = new CardRack(vertical,i);
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
	
	
	public void updateRacks() {
		for (int i=0; i<4; i++) {
			rack[i].updateCards();
		}
		//aaa System.out.println("Size of trick: " + trick.getWidth() + ", " + trick.getHeight());
	}
	

	
	public AnchorPane getDialogRack() {
		//used to not need to display a dialog box directly on top of the trick
		return rack[1];
	}
	
	
	public void playCard(Card theCard, int originalPosition) {
		
		//use this method to do the full trick animation incl setting image and final position...
		
		//aaa System.out.println("Size of trick: " + trick.getWidth() + ", " + trick.getHeight());
		
		
		//System.out.println(Thread.currentThread().getName());
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
		Pos position = null;
		double oldX = 0;
		double oldY = 0;
		double newX = 0;
		double newY = 0;
		
		oldX = (offsetX[owner]*windowWidth)-((1.0-offsetX[owner])*cardWidth);
		oldY = (offsetY[owner]*windowHeight)-((1.0-offsetY[owner])*cardHeight);
		newX = windowWidth*0.5 - cardWidth + (offsetX[owner]*2*cardWidth*0.6);
		newY = windowHeight*0.5 - cardHeight + (offsetY[owner]*2*cardHeight*0.6);
		
		card.setTranslateX(oldX);
		card.setTranslateY(oldY);
		
		System.out.println("animating card for owner " + owner + " from x="+oldX+", y="+oldY+" to x="+newX+", y="+newY);
		
		TranslateTransition animation = new TranslateTransition(Duration.seconds(1.0), card);
	    animation.setToX(newX);
	    animation.setToY(newY);
	    animation.setOnFinished(event -> {
	    	//remove node - why not leave it and ignore the table center?
	    	//aaa borderPaneCenter.getChildren().remove(card);
	    });
	    
	    Screen.this.transitionQueue.stop();
	    Screen.this.transitionQueue.getChildren().add(animation);

		if (renderFinished) Screen.this.transitionQueue.play(); else System.out.println("Render not finished yet - will not play transition queue");
		
	}
	
	
	
	public void setCards() {
		int rackNumber = 0;
		
		for (int i=0;i<Constants.SUITS_IN_THE_DECK;i++) {		
			for (int j=0; j < Constants.CARDS_IN_A_SUIT; j++) {
				Card theCard = Screen.this.deck.getSpecificCard(i, j);
				int k = theCard.getOriginalPosition();
				rackNumber = theCard.getOwner();
				rack[rackNumber].card[k].setImage(theCard.getImage());
				rack[rackNumber].card[k].shouldDraw = true;
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
		private double startX;
		private double startY;
		private double offsetX;
		private double offsetY;
		
		public CardRack(boolean vertical, int loc) {
			System.out.println("CardRack constructor called.");
			isVertical = vertical;
			location = loc;
			
			if (isVertical) {
				this.setMaxSize(100, 433);
				//this.setPrefSize(150, 433);
				this.setBackground(green);
				offsetX = 0;
				offsetY = 25;
				startX = 0;
				startY = 0;
				System.out.println("Values for this cardrack, offset and start: "+offsetX+", "+offsetY+", "+startX+", "+startY);
			} else {
				this.setMaxSize(460, 133);
				//this.setPrefSize(660, 200);
				this.setBackground(green);
				offsetX = 30;
				offsetY = 0;
				startX = 0;
				startY = 0;
				//startY = (this.getHeight()-133)/2;
				System.out.println("Values for this cardrack, offset and start: "+offsetX+", "+offsetY+", "+startX+", "+startY);
				
			}

			for (int i=0;i<13;i++) {
					card[i] = new CardImage();
					card[i].setAlignment(Pos.CENTER);
					card[i].shouldDraw = true;
					final int pointer = i;
					if (loc==3) card[i].setOnMouseClicked(event -> game.playManualCard(card[pointer].cardSuit, card[pointer].cardRank));
			}
			
		}
			
		public void updateCards() {
			double panelWidth = this.getWidth();
			double panelHeight =this.getHeight();
			
			System.out.println("Rack size: " + panelWidth + ", " + panelHeight);
			
			int locationCounter = 0;
			
			this.getChildren().clear();
			
			int cardsShown = 0;
			for (int i=0;i<13;i++) {
				if (card[i].shouldDraw) {
					card[i].setLayoutX(startX+(offsetX*cardsShown));
					card[i].setLayoutY(startY+(offsetY*cardsShown));
					this.getChildren().add(card[i]);
					cardsShown++;
				}
			}			
		}
		
	}
	
	/*
	 * 
	 * class TableCenter
	 * 
	 */
	
	
	/*
	public class TableCenter extends StackPane {
		
		private CardImage[] card = new CardImage[4];
		
		public TableCenter() {
			this.setBackground(yellow);
			this.setVisible(true);
			//this.setMinSize(500, 433);
			this.setPrefSize(200, 200);
			this.setMaxSize(200, 200);
			Pos[] position = new Pos[] {Pos.CENTER_LEFT, Pos.TOP_CENTER, Pos.CENTER_RIGHT, Pos.BOTTOM_CENTER};
			for (int i=0;i<4;i++) {
				//XXX MAYBE WE DONT NEED TABLECENTER??
				//card[i] = new CardImage();
				//this.getChildren().add(card[i]);
				//card[i].setAlignment(position[i]);
			}
		}
	}
	
	*/
	public class CardImage extends Label {
		
		boolean shouldDraw;
		int cardSuit;
		int cardRank;
		
		public CardImage() {
			this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3.5), BorderWidths.DEFAULT)));
			this.setMinSize(91, 133);
			this.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(3.5), null)));
			this.setGraphic(new ImageView(Screen.this.backside));
			this.shouldDraw = true;
		}
		
		public void shouldDraw(boolean value) {
			this.shouldDraw = value;
		}		
		
		public void setImage(Image image) {  	
			this.setGraphic(new ImageView(image));
			if (this.shouldDraw) this.setVisible(true); else this.setVisible(false);
		}
		
	}

}
