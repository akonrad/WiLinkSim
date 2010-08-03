import java.io.*;

public class testMatrix {

    /**
     * Testing the probabilityMatrix file to see if it works
     */

    public static void main(String[] argv){
	probabilityMatrix dawn = new probabilityMatrix("tracer", "pattern4", 4);
	System.out.println("Got past the opening");
	double[][] ret = dawn.generateMatrix();
	System.out.println("Got past the generation");

	for (int i = 0; i < 16; i ++){
	    System.out.println("[ " + ret[i][0] + "    " + ret[i][1] + " ] ");
	}
    }
	
	    
}
