import java.io.*;

public class testState {

    public static void main(String[] args){
	
	StateGenerator x = new StateGenerator(13, "RL_file", "stateTraceFile");
	x.convertToStateTrace();

    }

}
	
