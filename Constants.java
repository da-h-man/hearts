package heart;

public class Constants {
	
	public static final int SPADES					= 2;
	public static final int HEARTS					= 3;
	public static final int CLUBS					= 0;
	public static final int QUEEN					= 10;
	public static final int KING					= 11;
	public static final int ACE						= 12;
	public static final int TWO						= 0;
	public static final int CARDS_IN_A_SUIT 			= 13;
	public static final int CARDS_IN_THE_DECK			= 52;
	public static final int SUITS_IN_THE_DECK			= 4;
	public static final int NUMBER_OF_TRADES_ALLOWED	= 3;
	public static final int AVG_POINTS_PER_SUIT						= 1;
	public static final int AVG_TRICKS_PER_SUIT						= 2;
	public static final int AVG_CARDS_LEFT_AFTER_BLANK				= 3;
	public static final int CHANCE_OF_ALL_CARDS_COMING_INTO_PLAY	= 4;
	public static final int AVG_PLAYERS_HAVING_SUIT					= 5;
	public static final int STATUS_SELECT_TRADE_CARDS	= 1;
	public static final int STATUS_AWAITING_PLAYER_MOVE	= 2;
	public static final int STATUS_NO_INPUT_ALLOWED		= 3;
	
	public static final long[][] triangle 		= new long [53][53];
	public static final String[] SUIT_NAME 		= new String[] {"Clubs", "Diamonds", "Spades", "Hearts"};
	public static final String[] VALUE_NAME 	= new String[] {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
	public static final String[] IMG_NAME 		= new String[] {"4", "1", "3", "2"};
	public static final String AVG_TRICKS_FILE 	= "datafile.bin";
	public static final String ALL_CARDS_FILE	= "datafile2.bin";
	public static final String HAS_SUIT_FILE	= "datafile3.bin";
	public static final String BACKSIDE_IMG_FILE= "../resources/back_of_card.jpg";
	public static final int NUMBER_OF_PLAYERS	= 4;
	public static final int CARDS_IN_A_FULL_HAND= 13;
	public static final int IN_THE_TRICK		= -1;
	public static final String ERROR_2_OF_CLUBS	= "You must start the game with the 2 of Clubs!";
	public static final String ERROR_HEARTS_NOT_BROKEN = "Hearts not broken yet!";
	public static final String ERROR_PLAY_SUIT 	= "You must play ";
	public static final String ERROR_TITLE		= "Illegal card played";
	public static final boolean DONT_WAIT 		= false;
	public static final boolean WAIT			= true;
	
	
	static {
		//fill nCr table
	    int i, j;
	    triangle[0][0] = 1; // C(0, 0) = 1
	    for(i = 1; i < 53; i++) {
	        triangle[i][0] = 1; // C(i, 0) = 1
	        for(j = 1; j <= i; j++) {
	            triangle[i][j] = triangle[i - 1][j - 1] + triangle[i - 1][j];
	        }
	    }
	}
	
	public static long combinations(int n, int r) {
	    return triangle[n][r];
	}

}
