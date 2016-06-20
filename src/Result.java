
public class Result implements Comparable<Result>{
	
	public final String msg;
	public final int score;
	
	public Result(String input_msg, int input_score) 
	{
		msg = input_msg;
		score = input_score;
	}

	public int compareTo(Result another_result) 
	{
		return another_result.score - score;
	}

}
