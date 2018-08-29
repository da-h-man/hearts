package heart;

public class Constants {
	
	public static final int spades				= 2;
	public static final int hearts				= 3;
	public static final  int CLUBS				= 0;
	public static final int queen				= 10;
	public static final int king				= 11;
	public static final int ace					= 12;
	public static final int TWO					= 0;
	public static final int CARDS_IN_A_SUIT 	= 13;
	public static final int cardsInTheDeck		= 52;
	public static final int suitsInTheDeck		= 4;
	public static final long[][] triangle 		= new long [53][53];
	public static final String[] suit 			= new String[] {"Clubs", "Diamonds", "Spades", "Hearts"};
	public static final String AVG_TRICKS_FILE 	= "datafile.bin";
	public static final String ALL_CARDS_FILE	= "datafile2.bin";
	public static final String HAS_SUIT_FILE	= "datafile3.bin";
	public static final int NUMBER_OF_PLAYERS	= 4;
	public static final int CARDS_IN_A_FULL_HAND= 13;
	public static final int IN_THE_TRICK		= -1;
	
	
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
