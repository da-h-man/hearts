package heart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class Probabilities {
	
	/*
	public static final HashMap<Integer, Float> probMap;
	public static final HashMap<Integer, Float> probMap2;
	public static final HashMap<Integer, Float> probMap3;
	*/
	public static float[] probMap = new float[67108864];
	public static float[] probMap2 = new float[67108864];
	public static float[] probMap3 = new float[67108864];
	
	static {
		
		//fill HashMap
		FileInputStream fileIn;
		FileInputStream fileIn2;
		FileInputStream fileIn3;
		//HashMap<Integer, Float> tempMap = new HashMap<Integer, Float>();
		//HashMap<Integer, Float> tempMap2 = new HashMap<Integer, Float>();
		//HashMap<Integer, Float> tempMap3 = new HashMap<Integer, Float>();
		try {
			System.out.println("Loading files");
			fileIn = new FileInputStream(Constants.AVG_TRICKS_FILE);
			fileIn2 = new FileInputStream(Constants.ALL_CARDS_FILE);
			fileIn3 = new FileInputStream(Constants.HAS_SUIT_FILE);
			ObjectInputStream in = new ObjectInputStream(fileIn);
		    probMap = (float[])in.readObject();
		    in.close();
		    fileIn.close();
		    ObjectInputStream in2 = new ObjectInputStream(fileIn2);
		    probMap2 = (float[])in2.readObject();
		    in2.close();
		    fileIn2.close();
		    ObjectInputStream in3 = new ObjectInputStream(fileIn3);
		    probMap3 = (float[])in3.readObject();
		    in3.close();
		    fileIn3.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//probMap = tempMap;
		//probMap2 = tempMap2;
		//probMap3 = tempMap3;
		//if ((probMap == null)||(probMap2 == null)||(probMap3 == null)) System.out.println("OMG! One of the maps is NULL!!");
		
	}
	
	
	public static float averageTricks(int key) {
		System.out.println("Getting Probabilities.averageTricks");
		float returnValue = probMap[key];
		
		return returnValue;
	}
	
	public static float averageAllCardsPlayed(int key) {
		System.out.println("Getting Probabilities.averageAllCardsPlayed");
		float returnValue = probMap2[key];
		return returnValue;
	}
	
	public static float hasSuit(int key) {
		System.out.println("Getting Probabilities.hasSuit");
		float returnValue = probMap3[key];
		return returnValue;
	}
	
	/*
	public static float averageTricks(Integer key) {
		System.out.println("Getting Probabilities.averageTricks");
		Float returnValue = probMap.get(key);
		if (returnValue == null) {
			System.out.println("Oops! Key for averageTricks did not return a result: " + key);
			System.out.println(Thread.currentThread().getStackTrace());
			return 0f;
		}
		return returnValue;
	}
	
	public static float averageAllCardsPlayed(Integer key) {
		System.out.println("ProbMap2 key: "+key);
		Float returnValue = probMap2.get(key);
		if (returnValue == null) {
			System.out.println("Oops! Key for averageAllCardsPlayed did not return a result: " + key);
			System.out.println(Thread.currentThread().getStackTrace());
			return 0f;
		}
		return returnValue;
	}
	
	public static float hasSuit(Integer key) {
		System.out.println("Getting Probabilities.hasSuit");
		Float returnValue = probMap3.get(key);
		if (returnValue == null) {
			System.out.println("Oops! Key for hasSuit did not return a result: " + key);
			System.out.println(Thread.currentThread().getStackTrace());
			return 0f;
		}
		return returnValue;
	}
	
	*/

}
