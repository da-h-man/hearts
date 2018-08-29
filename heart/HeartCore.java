package heart;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class HeartCore extends Application {
	
	Game game;
	Screen screen;
	
	public static void main(String[] args) {
		
		//HeartCore app = new HeartCore();
		//app.runApplication();
		Application.launch(args);
	}
	
	@Override
	public void start(final Stage primaryStage) {
		
		this.game = new Game("Joe", "Billy", "Suzanne", "You");
		//check for probabilities file
		
        File f1 = new File(Constants.AVG_TRICKS_FILE);
        File f2 = new File(Constants.ALL_CARDS_FILE);
        File f3 = new File(Constants.HAS_SUIT_FILE);
        if ((!f1.exists()||(f1.exists() && f1.isDirectory()))||(!f2.exists()||(f2.exists() && f2.isDirectory()))||(!f3.exists()||(f3.exists()&& f3.isDirectory()))) { 
            //create file
        	FileCreator newFile = new FileCreator(this.game);
        	newFile.createFiles();
        	//runTest(game);
        }
		
        System.out.println("Let's get ready.");
        this.screen = new Screen(game.getDeck(), game);
		Scene window = new Scene(screen, 700, 700);
        primaryStage.setTitle("Hearts by The Henque");
        primaryStage.setScene(window);
        System.out.println("Showing primary Stage.");
        
        primaryStage.show();
		
		this.game.setScreen(this.screen);
		//this.game.play();
		startGame(game);
		//screen.setupScreen();

	}
	
	private void startGame(Game theGame) {
		Runnable task = new Runnable() {
			public void run() {
				theGame.play();
			}
		};
        // Run the task in a background thread
		Thread backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		 backgroundThread.start();
	}
	
	private static void runTest(Game game) {
		Hand playersHand=new Hand();
        Hand remainingCards= new Hand();
        playersHand.add(game.getDeck().getSpecificCard(0, 5));
        remainingCards.add(game.getDeck().getSpecificCard(0, 2));
        remainingCards.add(game.getDeck().getSpecificCard(0, 10));
        remainingCards.add(game.getDeck().getSpecificCard(0, 12));
        float prob = playersHand.getAverageTricksPerSuit(0, remainingCards);
	}
}
	
class FileCreator {
		
		Game game;
		float[] map = new float[67108864];
		float[] map2 = new float[67108864];
		float[] map3 = new float[67108864];
		
		public FileCreator(Game theGame) {
			this.game = theGame;
		}
		
		public int createKey(Hand myCards, Hand otherCards) {
			int result = 0;
			for (int i = 0; i < Constants.CARDS_IN_A_SUIT; i++) {
				if (myCards.hasCard(Constants.CLUBS, i)) {
					result+=Math.pow(2,(25-i));
				} else {
					if (otherCards.hasCard(Constants.CLUBS, i)) result+=Math.pow(2,(12-i));
				}
			}
			return result;
		}
		
		public void createFiles() {
			
					Hand playersHand=new Hand();
			        Hand remainingCards= new Hand();
			        Card[] cards = new Card[13];
			        //fill remaining cards with all cards of the suit
			        for (int i=0;i<13;i++) {
			        	cards[i]=game.getDeck().getSpecificCard(Constants.CLUBS, i);
			        	remainingCards.add(cards[i]);
			        }
			        
			        int counter=0;
			        
			        for (int i1=0;i1<2;i1++) {
			        	if (i1==1) remainingCards.giveTo(Constants.CLUBS, 0, playersHand);
			        	for (int i2=0;i2<2;i2++) {
			        		if (i2==1) remainingCards.giveTo(Constants.CLUBS, 1, playersHand);
				        	for (int i3=0;i3<2;i3++) {
				        		if (i3==1) remainingCards.giveTo(Constants.CLUBS, 2, playersHand);
					        	for (int i4=0;i4<2;i4++) {
					        		if (i4==1) remainingCards.giveTo(Constants.CLUBS, 3, playersHand);
						        	for (int i5=0;i5<2;i5++) {
						        		if (i5==1) remainingCards.giveTo(Constants.CLUBS, 4, playersHand);
							        	for (int i6=0;i6<2;i6++) {
							        		if (i6==1) remainingCards.giveTo(Constants.CLUBS, 5, playersHand);
								        	for (int i7=0;i7<2;i7++) {
								        		if (i7==1) remainingCards.giveTo(Constants.CLUBS, 6, playersHand);
									        	for (int i8=0;i8<2;i8++) {
									        		if (i8==1) remainingCards.giveTo(Constants.CLUBS, 7, playersHand);
										        	for (int i9=0;i9<2;i9++) {
										        		if (i9==1) remainingCards.giveTo(Constants.CLUBS, 8, playersHand);
											        	for (int i10=0;i10<2;i10++) {
											        		if (i10==1) remainingCards.giveTo(Constants.CLUBS, 9, playersHand);
												        	for (int i11=0;i11<2;i11++) {
												        		if (i11==1) remainingCards.giveTo(Constants.CLUBS, 10, playersHand);
													        	for (int i12=0;i12<2;i12++) {
													        		if (i12==1) remainingCards.giveTo(Constants.CLUBS, 11, playersHand);
													        		for (int i13=0;i13<2;i13++) {
													        			if (i13==1) remainingCards.giveTo(Constants.CLUBS, 12, playersHand);
													        			//now that all thirteen cards are allocated, cycle through number of remaining cards
													        			Hand otherCards = new Hand(remainingCards);
													        			if ((remainingCards.size()>0)&&(playersHand.size()>0)) {
													        				for (int j1=0;j1<2;j1++) {
														        				if (j1==0) otherCards.remove(remainingCards.getCard(0));
														        				if (remainingCards.size()>1) {
															        				for (int j2=0;j2<2;j2++) {
																        				if (j2==0) otherCards.remove(remainingCards.getCard(1));
																        				if (remainingCards.size()>2) {
																	        				for (int j3=0;j3<2;j3++) {
																		        				if (j3==0) otherCards.remove(remainingCards.getCard(2));
																		        				if (remainingCards.size()>3) {
																			        				for (int j4=0;j4<2;j4++) {
																				        				if (j4==0) otherCards.remove(remainingCards.getCard(3));
																				        				if (remainingCards.size()>4) {
																					        				for (int j5=0;j5<2;j5++) {
																						        				if (j5==0) otherCards.remove(remainingCards.getCard(4));
																						        				if (remainingCards.size()>5) {
																							        				for (int j6=0;j6<2;j6++) {
																								        				if (j6==0) otherCards.remove(remainingCards.getCard(5));
																								        				if (remainingCards.size()>6) {
																									        				for (int j7=0;j7<2;j7++) {
																										        				if (j7==0) otherCards.remove(remainingCards.getCard(6));
																										        				if (remainingCards.size()>7) {
																											        				for (int j8=0;j8<2;j8++) {
																												        				if (j8==0) otherCards.remove(remainingCards.getCard(7));
																												        				if (remainingCards.size()>8) {
																													        				for (int j9=0;j9<2;j9++) {
																														        				if (j9==0) otherCards.remove(remainingCards.getCard(8));
																														        				if (remainingCards.size()>9) {
																															        				for (int j10=0;j10<2;j10++) {
																																        				if (j10==0) otherCards.remove(remainingCards.getCard(9));
																																        				if (remainingCards.size()>10) {
																																	        				for (int j11=0;j11<2;j11++) {
																																		        				if (j11==0) otherCards.remove(remainingCards.getCard(10));
																																		        				if (remainingCards.size()>11) {
																																			        				for (int j12=0;j12<2;j12++) {
																																				        				if (j12==0) otherCards.remove(remainingCards.getCard(11));
																																				        				if (remainingCards.size()>12) {
																																					        				for (int j13=0;j13<2;j13++) {
																																						        				if (j13==0) otherCards.remove(remainingCards.getCard(12));
																																						        				//now execute
																																						        				if (otherCards.size()>0) {
																																						        					System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																																							        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																																						        					System.out.print("  ");
																																						        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																																						        					System.out.print("  ");
																																							        				float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																																							        				float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																																							        				float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																																							        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																																							        				int key = createKey(playersHand, otherCards);
																																							        				map[key] = prob;
																																							        				map2[key] = prob2;
																																							        				map3[key] = prob3;
																																						        				} else {
																																						        					System.out.println("Ignoring combo as othercards is 0");
																																						        				}
																																						        				
																																						        				if (j13==0) otherCards.add(remainingCards.getCard(12));
																																					        				} 
																																				        				} else {
																																				        					//execute
																																				        					if (otherCards.size()>0) {
																																				        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																																						        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																																					        					System.out.print("  ");
																																					        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																																					        					System.out.print("  ");
																																						        				float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																																						        				float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																																						        				float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																																						        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																																						        				int key = createKey(playersHand, otherCards);
																																						        				map[key] = prob;
																																						        				map2[key] = prob2;
																																						        				map3[key] = prob3;
																																				        					}
																																					        				
																																				        				}
																																				        				if (j12==0) otherCards.add(remainingCards.getCard(11));
																																			        				}
																																		        				} else {
																																		        					//execute
																																		        					if (otherCards.size()>0) {
																																		        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																																				        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																																			        					System.out.print("  ");
																																			        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																																			        					System.out.print("  ");
																																				        				float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																																				        				float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																																				        				float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																																				        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																																				        				int key = createKey(playersHand, otherCards);
																																				        				map[key] = prob;
																																				        				map2[key] = prob2;
																																				        				map3[key] = prob3;
																																		        					}
																																			        				
																																		        				}
																																		        				if (j11==0) otherCards.add(remainingCards.getCard(10));
																																	        				}
																																        				} else {
																																        					//execute
																																        					if (otherCards.size()>0) {
																																        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																																		        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																																	        					System.out.print("  ");
																																	        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																																	        					System.out.print("  ");
																																	        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																																	        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																																	        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																																		        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																																		        				int key = createKey(playersHand, otherCards);
																																		        				map[key] = prob;
																																		        				map2[key] = prob2;
																																		        				map3[key] = prob3;
																																        					}
																																	        				
																																        				}
																																        				if (j10==0) otherCards.add(remainingCards.getCard(9));
																															        				}
																														        				} else {
																														        					//execute
																														        					if (otherCards.size()>0) {
																														        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																																        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																															        					System.out.print("  ");
																															        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																															        					System.out.print("  ");
																															        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																															        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																															        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																																        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																																        				int key = createKey(playersHand, otherCards);
																																        				map[key] = prob;
																																        				map2[key] = prob2;
																																        				map3[key] = prob3;
																														        					}
																															        				
																														        				}
																														        				if (j9==0) otherCards.add(remainingCards.getCard(8));
																														        					
																													        				}
																												        				} else {
																												        					//execute
																												        					if (otherCards.size()>0) {
																												        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																														        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																													        					System.out.print("  ");
																													        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																													        					System.out.print("  ");
																													        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																													        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																													        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																														        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																														        				int key = createKey(playersHand, otherCards);
																														        				map[key] = prob;
																														        				map2[key] = prob2;
																														        				map3[key] = prob3;
																												        					}
																													        				
																												        				}
																												        				if (j8==0) otherCards.add(remainingCards.getCard(7));
																												        					
																											        				}
																										        				} else {
																										        					//execute
																										        					if (otherCards.size()>0) {
																										        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																												        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																											        					System.out.print("  ");
																											        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																											        					System.out.print("  ");
																											        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																											        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																											        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																												        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																												        				int key = createKey(playersHand, otherCards);
																												        				map[key] = prob;
																												        				map2[key] = prob2;
																												        				map3[key] = prob3;
																										        					}
																											        				
																										        				}
																										        				if (j7==0) otherCards.add(remainingCards.getCard(6));
																										        					
																									        				}
																								        				} else {
																								        					//execute
																								        					if (otherCards.size()>0) {
																								        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																										        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																									        					System.out.print("  ");
																									        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																									        					System.out.print("  ");
																									        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																									        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																									        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																										        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																										        				int key = createKey(playersHand, otherCards);
																										        				map[key] = prob;
																										        				map2[key] = prob2;
																										        				map3[key] = prob3;
																								        					}
																									        				
																								        				}
																								        				if (j6==0) otherCards.add(remainingCards.getCard(5));
																								        					
																							        				}
																						        				} else {
																						        					//execute
																						        					if (otherCards.size()>0) {
																						        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																								        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																							        					System.out.print("  ");
																							        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																							        					System.out.print("  ");
																							        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																							        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																							        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																								        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																								        				int key = createKey(playersHand, otherCards);
																								        				map[key] = prob;
																								        				map2[key] = prob2;
																								        				map3[key] = prob3;
																						        					}
																							        				
																						        				}
																						        				if (j5==0) otherCards.add(remainingCards.getCard(4));
																						        					
																					        				}
																				        				} else {
																				        					//execute
																				        					if (otherCards.size()>0) {
																				        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																						        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																					        					System.out.print("  ");
																					        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																					        					System.out.print("  ");
																					        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																					        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																					        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																						        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																						        				int key = createKey(playersHand, otherCards);
																						        				map[key] = prob;
																						        				map2[key] = prob2;
																						        				map3[key] = prob3;
																				        					}
																				        				}
																				        				if (j4==0) otherCards.add(remainingCards.getCard(3));
																			        				}
																		        				} else {
																		        					//execute
																		        					if (otherCards.size()>0) {
																		        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																				        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																			        					System.out.print("  ");
																			        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																			        					System.out.print("  ");
																			        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																			        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																			        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																				        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																				        				int key = createKey(playersHand, otherCards);
																				        				map[key] = prob;
																				        				map2[key] = prob2;
																				        				map3[key] = prob3;
																		        					}
																			        				
																		        				}
																		        				if (j3==0) otherCards.add(remainingCards.getCard(2));
																		        					
																	        				}
																        				} else {
																        					//execute
																        					if (otherCards.size()>0) {
																        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																		        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
																	        					System.out.print("  ");
																	        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
																	        					System.out.print("  ");
																	        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
																	        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
																	        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																		        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																		        				int key = createKey(playersHand, otherCards);
																		        				map[key] = prob;
																		        				map2[key] = prob2;
																		        				map3[key] = prob3;
																        					}
																        				}
																        				if (j2==0) otherCards.add(remainingCards.getCard(1));
																        					
															        				}
														        				} else {
														        					if (j1==1) {
														        						System.out.println("Writing file for "+playersHand.size()+" cards and "+otherCards.size()+" other cards out of "+remainingCards.size()+".");
																        				for (int hey=0;hey<playersHand.size();hey++) System.out.print(playersHand.getCard(hey).getRank()+"-");
															        					System.out.print("  ");
															        					for (int hey=0;hey<otherCards.size();hey++) System.out.print(otherCards.getCard(hey).getRank()+"-");
															        					System.out.print("  ");
															        					float prob = playersHand.calculateAverageTricksPerSuit(0, otherCards);
															        					float prob2 = playersHand.calculateChanceForAllCardsComingIntoPlay(0, otherCards);
															        					float prob3 = playersHand.calculateAverageHasSuit(0, otherCards);
																        				System.out.println("Value is: "+prob+" and location:"+createKey(playersHand, otherCards));
																        				int key = createKey(playersHand, otherCards);
																        				map[key] = prob;
																        				map2[key] = prob2;
																        				map3[key] = prob3;
														        					}
														        				}	
														        				//hier müssen wir die 0 wieder reinlegen
														        				otherCards.add(remainingCards.getCard(0));
													        				}
													        			} else {
													        				System.out.println("No cards remaining - no further action required.");
													        			}
													        			if (i13==1) playersHand.giveTo(Constants.CLUBS, 12, remainingCards);
													        		}
													        		if (i12==1) playersHand.giveTo(Constants.CLUBS, 11, remainingCards);
													        	}
													        	if (i11==1) playersHand.giveTo(Constants.CLUBS, 10, remainingCards);
												        	}
												        	if (i10==1) playersHand.giveTo(Constants.CLUBS, 9, remainingCards);
											        	}
											        	if (i9==1) playersHand.giveTo(Constants.CLUBS, 8, remainingCards);
										        	}
										        	if (i8==1) playersHand.giveTo(Constants.CLUBS, 7, remainingCards);
									        	}
									        	if (i7==1) playersHand.giveTo(Constants.CLUBS, 6, remainingCards);
								        	}
								        	if (i6==1) playersHand.giveTo(Constants.CLUBS, 5, remainingCards);
							        	}
							        	if (i5==1) playersHand.giveTo(Constants.CLUBS, 4, remainingCards);
						        	}
						        	if (i4==1) playersHand.giveTo(Constants.CLUBS, 3, remainingCards);
					        	}
					        	if (i3==1) playersHand.giveTo(Constants.CLUBS, 2, remainingCards);
				        	}
				        	if (i2==1) playersHand.giveTo(Constants.CLUBS, 1, remainingCards);
			        	}
			        	if (i1==1) playersHand.giveTo(Constants.CLUBS, 0, remainingCards);
			        }			

				try {
					//RandomAccessFile f = new RandomAccessFile(Constants.datafile, "rw");
			        //f.setLength(6311764);
			        //f.seek(0);
			        FileOutputStream fileOut = new FileOutputStream(Constants.AVG_TRICKS_FILE);
			        ObjectOutputStream out = new ObjectOutputStream(fileOut);
			        out.writeObject(map);
			        out.close();
			        fileOut.close();
			        FileOutputStream fileOut2 = new FileOutputStream(Constants.ALL_CARDS_FILE);
			        ObjectOutputStream out2 = new ObjectOutputStream(fileOut2);
			        out2.writeObject(map2);
			        out2.close();
			        fileOut2.close();
			        FileOutputStream fileOut3 = new FileOutputStream(Constants.HAS_SUIT_FILE);
			        ObjectOutputStream out3 = new ObjectOutputStream(fileOut3);
			        out3.writeObject(map3);
			        out3.close();
			        fileOut3.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.exit(0);
					
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
		}
	}


