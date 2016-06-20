//code sourced from https://code.google.com/p/himmele/source/browse/trunk/Bioinformatics/NeedlemanWunsch/src/NeedlemanWunsch.java
	
public class Globaldistance 
{
	char[] word1;//mSeqA;
    char[] word2;//mSeqB;
    int[][] mD;
    int mScore;
    String mAlignmentSeqA = "";
    String mAlignmentSeqB = "";
    
    void init(char[] word1, char[] word2) {
            this.word1 = word1;
            this.word2 = word2;
            mD = new int[word1.length + 1][word2.length + 1];
            for (int i = 0; i <= word1.length; i++) {
                    for (int j = 0; j <= word2.length; j++) {
                            if (i == 0) {
                                    mD[i][j] = j;
                            } else if (j == 0) {
                                    mD[i][j] = i;
                            } else {
                                    mD[i][j] = 0;
                            }
                    }
            }
    }
    
    void process() {
            for (int i = 1; i <= word1.length; i++) {
                    for (int j = 1; j <= word2.length; j++) {
                            int scoreDiag = mD[i-1][j-1] + weight(i, j);
                            int scoreLeft = mD[i][j-1] + 1;
                            int scoreUp = mD[i-1][j] + 1;
                            mD[i][j] = Math.min(Math.min(scoreDiag, scoreLeft), scoreUp);
                    }
            }
    }
    
    void backtrack() {
            int i = word1.length;
            int j = word2.length;
            mScore = mD[i][j];
            while (i > 0 && j > 0) {                        
                    if (mD[i][j] == mD[i-1][j-1] + weight(i, j)) {                          
                            mAlignmentSeqA += word1[i-1];
                            mAlignmentSeqB += word2[j-1];
                            i--;
                            j--;                            
                            continue;
                    } else if (mD[i][j] == mD[i][j-1] - 1) {
                            mAlignmentSeqA += "-";
                            mAlignmentSeqB += word2[j-1];
                            j--;
                            continue;
                    } else {
                            mAlignmentSeqA += word1[i-1];
                            mAlignmentSeqB += "-";
                            i--;
                            continue;
                    }
            }
            mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
            mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
    }
    
    private int weight(int i, int j) 
    {
            if (word1[i - 1] == word2[j - 1]) 
            {
                    return 0;
            } 
            else 
            {
                    return 1;
            }
    }
    
    void printMatrix() {
            System.out.println("D =");
            for (int i = 0; i < word1.length + 1; i++) {
                    for (int j = 0; j < word2.length + 1; j++) {
                            System.out.print(String.format("%4d ", mD[i][j]));
                    }
                    System.out.println();
            }
            System.out.println();
    }
    
    void printScoreAndAlignments() {
            System.out.println("Score: " + mScore);
            System.out.println("Sequence A: " + mAlignmentSeqA);
            System.out.println("Sequence B: " + mAlignmentSeqB);
            System.out.println();
    }
    
   public int getScore(char[] word1, char[] word2) {
    	init(word1, word2);
    	process();
    	backtrack();
   	return mScore;
    }
   
   public static void main(String [] args) 
   {               

           char[] inputword1 = { 'C', 'h', 'u', 'r', 'c','h',' ','F','a','i','t','h','w','a','y'};
           char[] inputword2 = { 'F','a','i','t','h','w','a','y',' ','C','h','u','r','c','h'};
           Globaldistance sw = new Globaldistance();
           sw.init(inputword1, inputword2);            
           sw.process();
           sw.backtrack();        
           sw.printMatrix();
           sw.printScoreAndAlignments();
           System.out.println(sw.getScore(inputword1,inputword2));

   }

}
