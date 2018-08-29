package heart;

import java.lang.reflect.InvocationTargetException;

import javafx.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class Screen extends BorderPane {
	
	private Game game;
	private Deck deck;
	private GridPane gridPane;
	private Image backside;
	private static final String BACKSIDE_IMG_FILE_NAME = "../resources/back_of_card.jpg";

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
	    this.backside = new Image(this.getClass().getResourceAsStream(this.BACKSIDE_IMG_FILE_NAME));
		initializeTable();
		//this.revalidate();
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
		System.out.println("Updated racks. New dimensions: ");
		System.out.println("Size of Screen: "+ this.getWidth() + ", " + this.getHeight());
		System.out.println("Size of GridPane center bound parents: " + this.getCenter().getBoundsInParent().getWidth() + ", " + this.getCenter().getBoundsInParent().getHeight());
		System.out.println("Size of GridPane center bound local: " + this.getCenter().getBoundsInLocal().getWidth() + ", " + this.getCenter().getBoundsInLocal().getHeight());
		System.out.println("Size of borderCenterPane: " + borderPaneCenter.getWidth() + ", " + borderPaneCenter.getHeight());
		System.out.println("Size of middleRowCenter: " + middleRowCenter.getWidth() + ", " + middleRowCenter.getHeight());
		//aaa System.out.println("Size of trick: " + trick.getWidth() + ", " + trick.getHeight());
	}

	
	public AnchorPane getDialogRack() {
		//used to not need to display a dialog box directly on top of the trick
		return rack[1];
	}
	
	
	public void playCard(Card theCard, int oldPosition) {
		
		//use this method to do the full trick animation incl setting image and final position...
		
		//aaa System.out.println("Size of trick: " + trick.getWidth() + ", " + trick.getHeight());
		
		
		System.out.println(Thread.currentThread().getName());
		double windowWidth = borderPaneCenter.getWidth();
		double windowHeight = borderPaneCenter.getHeight();

		System.out.println("Trick width and height: "+ windowWidth + ", " + windowHeight);
		
		int owner = theCard.getOwner();
		rack[owner].card[oldPosition].shouldDraw = false;
		CardImage card = new CardImage();
		
		//aaa trick.card[owner].setVisible(false);
		//aaa trick.card[owner].setGraphic(new ImageView(theCard.getImage()));
		card.setGraphic(new ImageView(theCard.getImage()));
		borderPaneCenter.getChildren().add(card);
		borderPaneCenter.applyCss();
		borderPaneCenter.layout();
		double cardWidth = card.getBoundsInParent().getWidth();
		double cardHeight = card.getBoundsInParent().getHeight();
		System.out.println("Card dimensions: "+cardWidth + ", " + cardHeight);
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
		
		TranslateTransition animation = new TranslateTransition(Duration.seconds(1.5), card);
	    animation.setToX(newX);
	    animation.setToY(newY);
	    animation.setOnFinished(event -> {
	    	//remove node - why not leave it and ignore the table center?
	    	//aaa borderPaneCenter.getChildren().remove(card);
	    });
	    
	    Screen.this.transitionQueue.stop();
	    Screen.this.transitionQueue.getChildren().add(animation);

		if (renderFinished) Screen.this.transitionQueue.play(); else System.out.println("Render not finished yet - will not play transition queue");
		
		//aaa trick.card[owner].setVisible(true);
		//aaa StackPane.setAlignment(trick.card[owner], position);
		
		rack[owner].updateCards();

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

			location = loc;

			for (int i=0;i<13;i++) {
					card[i] = new CardImage();
					card[i].setAlignment(Pos.CENTER);
					card[i].shouldDraw = true;
					//this.getChildren().add(card[i]);
			}
			
		}
		
		public void updateCards() {
			double panelWidth = this.getWidth();
			double panelHeight =this.getHeight();
			
			int locationCounter = 0;
			
			this.getChildren().clear();
			
			for (int i=0;i<Constants.suitsInTheDeck;i++) {		
				for (int j=0; j < Constants.CARDS_IN_A_SUIT; j++) {
					Card theCard = Screen.this.deck.getSpecificCard(i, j);
					if ((theCard.getOwner()==this.location)&&(theCard.getPosition()>=0)) {
						//the card belongs to this rack
						System.out.println("The " + theCard.getName() + " belongs to " + theCard.getOwner() + " at position " + theCard.getPosition());
						this.card[locationCounter].setImage(theCard.getImage());
						this.card[locationCounter].shouldDraw(true);
						locationCounter++;
					}
				}
			}
				
			for (int k=12;k>=locationCounter;k--) {
				card[k].shouldDraw(false);
			}
			
			for (int i=0;i<13;i++) {
				if (card[i].shouldDraw) {
					card[i].setLayoutX(startX+(offsetX*i));
					card[i].setLayoutY(startY+(offsetY*i));
					this.getChildren().add(card[i]);
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
