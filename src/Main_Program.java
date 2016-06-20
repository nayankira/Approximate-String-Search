import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * The main class to do approximate matching for each query in the query file to the Tweet dataset
 * 
 * @author Yan NA(Ryan NA)
 *
 */

public class Main_Program 
{
	
	public static void main(String[] args) throws IOException 
	{
		String queryPath = args[0];
		String tweetPath = args[1];		
		String gramResultPath1 = args[2];
		String gramResultPath2 = args[3];
		String globalResultPath = args[4];
		String localResultPath = args[5];
		String NWglobalResultPath1 = args[6];

		System.out.println("Processing");
		
		ArrayList<String> loaded_location_file = FileReader (queryPath);//read location
		ArrayList<String> loaded_tweets_file = FileReader (tweetPath);//read tweet

		MatchingViaLocaldistance(loaded_location_file, loaded_tweets_file, localResultPath,0.5); //(path of location file, path of tweets file, path of output result file, threshold)
		MatchingViangram(loaded_location_file, loaded_tweets_file, gramResultPath1, 2, 0.5); //(path of location file, path of tweets file, path of output result file, gram, threshold)
		MatchingViangram(loaded_location_file, loaded_tweets_file, gramResultPath2, 3, 0.5);
		MatchingViaGlobaldistance(loaded_location_file, loaded_tweets_file, globalResultPath, 0.5); //(path of location file, path of tweets file, path of output result file, threshold)
		MatchingViaNWGlobaldistance(loaded_location_file, loaded_tweets_file, NWglobalResultPath1, 0.5); //(path of location file, path of tweets file, path of output result file, threshold)

		
		System.out.println("Done");
	}
	
	public static ArrayList<String> FileReader (String locationPath) throws IOException 
	{
		ArrayList<String> loaded_file = new ArrayList<String>();
		File locationFile = new File(locationPath);
		
		if (locationFile.isFile() && locationFile.exists()) 
		{  
				InputStreamReader reader = new InputStreamReader(new FileInputStream(locationFile));
				BufferedReader bufferedReader = new BufferedReader(reader);
				String Locationline;
				while ((Locationline = bufferedReader.readLine()) != null) 
				{
					loaded_file.add(Locationline);
				}				
			reader.close();			
		}
		return loaded_file;
	}
	
	public static void MatchingViaGlobaldistance(ArrayList<String> Locationlist, ArrayList<String> Tweetlist, String resultPath, double threshold) throws IOException 
	{
		
		FileWriter fileWriter= new FileWriter(resultPath);
			
		if (Locationlist.size() > 0 && Tweetlist.size() > 0) 
		{
			String lineLocation = null;
			String lineTweet = null;		
			Iterator<String> ItOfLocation = Locationlist.iterator();

			String[] locations, tweets;
			
			while (ItOfLocation.hasNext()) 
			{
				lineLocation = ItOfLocation.next();//read each line of location
				
				StringBuffer sb = new StringBuffer();
				sb.append("********************************************\n"); //can be replaced 
				sb.append(lineLocation + "\n");
				
				boolean isMatch = false;
				ArrayList<Result> al = new ArrayList<Result>();
				Iterator<String> ItOfTweet = Tweetlist.iterator();
				while (ItOfTweet.hasNext())
				{
					lineTweet = ItOfTweet.next();//read each line of tweet
					locations = lineLocation.split("\\s");
					tweets = lineTweet.split("\\s");
					if (tweets.length < locations.length) continue;
					int l = tweets.length - locations.length + 1;
					String[] aggStr = new String[l];
					for (int i = 0; i < l; i++) 
					{
						aggStr[i] = tweets[i];
						for (int j = 1; j < locations.length; j++) 
						{
							aggStr[i] += " " + tweets[i + j];
						}
					}
					
					int score = Integer.MAX_VALUE;
					String matchResult = "";
					for (int i = 0; i < l; i++) 
					{
						Globaldistance rw = new Globaldistance();
						int tmp = rw.getScore(lineLocation.toCharArray(), aggStr[i].toCharArray());
						if (score > tmp) 
						{
							score = tmp;
							matchResult = aggStr[i];
						}
					}
					if (score <= (double)(lineLocation.length()) * threshold) // threshold setting
					{   
						isMatch = true;
						al.add(new Result(tweets[0] + " " + matchResult, score));
					}

				}
				if (isMatch)
				{
					Collections.sort(al);
					for (Result result : al)
					{
						sb.append(result.msg + " " + result.score + "\n");
					}			
						fileWriter.append(sb.toString());					
				}
			}
				
		}
		fileWriter.close();
	}
	
	public static void MatchingViaNWGlobaldistance(ArrayList<String> locationlist, ArrayList<String> tweetlist,
			String resultPath, double threshold) throws IOException 
	{
		FileWriter fileWriter = new FileWriter(resultPath);
		
		if (locationlist.size() > 0 && tweetlist.size() > 0) 
		{
			String lineLocation = null;
			String lineTweet = null;		
			Iterator<String> ItOfLocation = locationlist.iterator();
			String[] locations, tweets;
			
			while (ItOfLocation.hasNext())
			{
				lineLocation = ItOfLocation.next();//read each line of location
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("********************************************\n");
				buffer.append(lineLocation + "\n");
				
				boolean isMatch = false;
				ArrayList<Result> al = new ArrayList<Result>();
				Iterator<String> ItOfTweet = tweetlist.iterator();
				while (ItOfTweet.hasNext()) 
				{
					lineTweet = ItOfTweet.next();//read each line of tweet
					locations = lineLocation.split("\\s");
					tweets = lineTweet.split("\\s");
					if (tweets.length < locations.length) continue;
					int len = tweets.length - locations.length + 1;
					String[] aggStr = new String[len];
					for (int i = 0; i < len; i++) {
						aggStr[i] = tweets[i];
						for (int j = 1; j < locations.length; j++) 
						{
							aggStr[i] += " " + tweets[i + j];
						}
					}					
					int score = 0;
					String matchResult = "";
					for (int i = 0; i < len; i++) 
					{
						NeedlemanWunsch rw = new NeedlemanWunsch();
						int tmp = rw.getScore (lineLocation.toCharArray(), aggStr[i].toCharArray());
						if (score < tmp) 
						{
							score = tmp;
							matchResult = aggStr[i];
						}
					}
					if (score >= lineLocation.length() * threshold) 
					{
						isMatch = true;
						al.add(new Result(tweets[0] + " " + matchResult, score));
					}

				}
				if (isMatch)
				{
					Collections.sort(al);
					for (Result result : al) 
					{
						buffer.append(result.msg + " " + result.score + "\n");
					}
					fileWriter.append(buffer.toString());
				}
				
			}

		}
		fileWriter.close();

	}
	
	public static void MatchingViaLocaldistance (ArrayList<String> LocationList, ArrayList<String> TweetList,
			String resultPath, double threshold) throws IOException 
	{		
		FileWriter ResultFile = new FileWriter(resultPath);
		
		if (LocationList.size() > 0 && TweetList.size() > 0) 
		{
			String LocationLine = null;
			String TweetLine = null;		
			Iterator<String> ItOfLocation = LocationList.iterator();
			String[]  TweetWords;
			
			while (ItOfLocation.hasNext())
			{
			     LocationLine = ItOfLocation.next();
				 StringBuffer buffer = new StringBuffer();
				 buffer.append("****************************************\n");
				 buffer.append(LocationLine + "\n");
				 Boolean matched = false; 
				 
				 ArrayList<Result> ResultList = new ArrayList<Result>();
				 Iterator<String> ItOfTweet = TweetList.iterator();
				 while (ItOfTweet.hasNext())
				 {
					 TweetLine = ItOfTweet.next();
					 TweetWords = TweetLine.split("\\s");
					 Localdistance  LocalAlign =new Localdistance();
					 int localmark = LocalAlign.getScore(LocationLine.toCharArray(), TweetLine.toCharArray());				
					 if (localmark >= (double)(LocationLine.length())*threshold)
					 {
						 matched = true;	
						 ResultList.add(new Result(TweetWords[0] + " " + TweetLine, localmark));
					 }
				 }
				if (matched) 
				{
					Collections.sort(ResultList);
					for (Result finalresult : ResultList)
					{
						buffer.append(finalresult.msg + " " + finalresult.score + "\n");						
					}
					ResultFile.append(buffer.toString());
				}
				
			}
			ResultFile.close();
		}
	}
	
	
	public static void MatchingViangram (ArrayList<String> locationlist,ArrayList<String> tweetlist,
			String resultPath,int n, double threshold) throws IOException 
	{
		
		FileWriter fileWriter = new FileWriter(resultPath);
		
		if (locationlist.size() > 0 && tweetlist.size() > 0) 
		{
			String lineLocation = null;
			String lineTweet = null;		
			Iterator<String> ItOfLocation = locationlist.iterator();

			String[] locations, tweets;
			
			while (ItOfLocation.hasNext()) 
			{
				lineLocation = ItOfLocation.next();//read each line of location
				
				StringBuffer sb = new StringBuffer();
				sb.append("************************************************\n");
				sb.append(lineLocation + "\n");
				
				boolean isMatch = false;
				ArrayList<Result> al = new ArrayList<Result>();
				Iterator<String> ItOfTweet = tweetlist.iterator();
				while (ItOfTweet.hasNext()) 
				{
					lineTweet = ItOfTweet.next();//read each line of tweet
					locations = lineLocation.split("\\s");
					tweets = lineTweet.split("\\s");
					if (tweets.length < locations.length) continue;
					int len = tweets.length - locations.length + 1;
					String[] aggStr = new String[len];
					for (int i = 0; i < len; i++) 
					{
						aggStr[i] = tweets[i];
						for (int j = 1; j < locations.length; j++) 
						{
							aggStr[i] += " " + tweets[i + j];
						}
					}
					
					int score = Integer.MAX_VALUE;
					String matchResult = "";
					for (int i = 0; i < len; i++) 
					{
						Ngram rw = new Ngram();
						int tmp = rw.getNgramDistance(lineLocation, aggStr[i], n);
						if (score > tmp) 
						{
							score = tmp;
							matchResult = aggStr[i];
						}
					}
					if (score <= (double)(lineLocation.length())*threshold) 
					{
						isMatch = true;
						al.add(new Result(tweets[0] + " " + matchResult, score));
					}

				}
				if (isMatch)
				{
					Collections.sort(al);
					for (Result result : al) 
					{
						sb.append(result.msg + " " + result.score + "\n");
					}
					fileWriter.append(sb.toString());
				}
				
			}

		}
		fileWriter.close();
	}
	
}
