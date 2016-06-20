import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class Ngram {
	
	public  static void main (String[] args)  //For testing the the program
	{
		String word1 = "panorama";
		String word2 = "paranormal";
		Ngram ngramprocessing = new Ngram();
		System.out.println(ngramprocessing.getNgramDistance(word1,word2,3));
	}
	
	public String[] Granulation(String text,int n) //for generating grams group of each word
	{
		if (text.length() <= 2)
		{
			return new String[]{text};
		}
		String[] result=new String[text.length()-n+1];
		
		for(int i=0;i<=text.length()-n;i++){
			result[i]=text.substring(i, i+n);
		}			
		return result;
	}
	
	public int getNgramDistance(String inputword1, String inputword2, int n)
	{
		String[] grams1=Granulation(inputword1,n);
		String[] grams2=Granulation(inputword2,n);
		int ngramdistance=0;
		int count = 0;
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		
		for (int i = 0; i < grams1.length; i=i+1) 
		{
			if (!hm.containsKey(grams1)) 
			{
				hm.put(grams1[i], 1);
			} 
			else 
			{
				hm.put(grams1[i], hm.get(grams1[i]) + 1);
			}
		}
		for(int i=0;i<grams2.length;i=i+1)
		{
			if (hm.containsKey(grams2[i]))
			{
				int value = hm.get(grams2[i] ) - 1;
				count=count+1;
				if (value == 0) 
				{
					hm.remove(grams2[i]);
				} 
				else 
				{
					hm.put(grams2[i], value);
				}
			}
		}
			
		ngramdistance=grams1.length+grams2.length-2*count;
		return ngramdistance;
	}
	

}
