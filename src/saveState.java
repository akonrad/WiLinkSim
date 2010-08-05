import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class saveState {

    private boolean save = false;
    public static String saveFile;

    public saveState(String file_name){
	saveFile = file_name;	
	//System.out.println("The trace file is = " + file_name);
    }
    
    
    /**
     * Save the settings of the options file into a new file specified by the 
     * user - then add the name of the file into SavedConfigs
     */
    public void saveSettings(String name){
	try {
	    FileWriter x = new FileWriter(name);
	    BufferedWriter settingSaver = new BufferedWriter(x);
	    
	    BufferedReader optionReader = new BufferedReader(new FileReader("options"));
	    String line;
	    
	    while ((line = optionReader.readLine()) != null) {
		
		settingSaver.write(line, 0, line.length());
		settingSaver.newLine();
		
	    }
	    
	    BufferedWriter saveConfig = new BufferedWriter(new FileWriter("SavedConfigs", true));
	    saveConfig.write(name, 0, name.length());
	    saveConfig.newLine();
	    
	    settingSaver.close();
	    saveConfig.close();
	    optionReader.close();
	    
	} catch (Exception e) {
	    System.out.println("Error in saveSettings of saveState.java");
	}
    }


    public void save(){
	try {
	    FileReader x = new FileReader(saveFile);
	    BufferedReader traceReader = new BufferedReader(x);
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(new JFrame(), "This file is not found.");
	    e.printStackTrace();
	}
			
	/****************************************************************/
	/** Here we start the experiment and save the transition matrices 
	 *  inside the options file itself - which can then be copied to the
	 * file in which the user wants to save his settings
	 */
	/****************************************************************/
	
	try {
	    FileWriter dummy = new FileWriter("options", true);
	    BufferedWriter optionWriter = new BufferedWriter(dummy);
	    String data;

	    // get the change of state variable from the RL_file
	    int changeOfState;
	    double FER;
	    getStats statFinder = new getStats(saveFile);
	    statFinder.getStatistics();
	    changeOfState = statFinder.getC();
	    FER = statFinder.getFER();
	    data = "<ChangeOfState> " + changeOfState + " </ChangeOfState>\n";
	    
	    optionWriter.write(data, 0 , data.length());
	    optionWriter.flush();
	    
	    data = "<FER> " + FER + " </FER>\n";
	    optionWriter.write(data, 0 , data.length());
	    optionWriter.flush();
	    

	    // first convert the trace file into the lossy subtrace
	    LossyTrace x = new LossyTrace(changeOfState, saveFile, "lossyTraceFile");
	    x.convertToLossy();
	    
	    // produce the probability Matrix for this lossy trace
	    probabilityMatrix lossyMatrix = new probabilityMatrix("lossyTraceFile", "pattern4", 4);
	    double[][] lossy = lossyMatrix.generateMatrix();
	    data = "<LossyMatrix>\n";
	   
	    for (int i = 0; i < 16; i ++){
		data = data + lossy[i][0] + "\t" + lossy[i][1] + "\n";
	    }

	    data = data + "</LossyMatrix>\n";
	    
	    optionWriter.write(data, 0, data.length());
	    optionWriter.flush();
	

	    // convert the trace file into the state file and then convert it into a binary file
	    StateGenerator x1 = new StateGenerator(changeOfState, saveFile, "stateTraceFile");
	    x1.convertToStateTrace();
	    binaryTraceGenerator x2 = new binaryTraceGenerator("stateTraceFile", "binaryStateTraceFile");
	    x2.convertToBinary();
	    
	    // make a probability matrix out of the state trace
	    probabilityMatrix stateMatrix = new probabilityMatrix("binaryStateTraceFile", "pattern4", 4);
	    double[][] stateMat = stateMatrix.generateMatrix();
	    
	    data = "<StateMatrix>\n";

	    for (int i = 0; i < 16; i ++){
		data = data +  stateMat[i][0] + "\t" + stateMat[i][1] + " \n";
	    }
	    
	    data = data + "</StateMatrix>\n";

	    optionWriter.write(data, 0 , data.length());
	    optionWriter.flush();
	    optionWriter.close();

	} catch (Exception e) {
	    //e.printStackTrace();
	}

	
	final JFrame frame = new JFrame("Save state?");

	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
	panel.setLayout(new GridLayout(0, 1));

	JLabel label1 = new JLabel("The models you chose have been generated. Would you like to save your settings before you proceed to load the data file?");

	final JTextField fileName = new JTextField(20);
	
	JLabel label2 = new JLabel("Please click on SAVE to save after typing in desired name for saving. Click on NO SAVE to carry on with the experiment");

	JButton save = new JButton("SAVE");
	save.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveSettings(fileName.getText());
		    frame.dispose();
		    dataDialogue datax = new dataDialogue("saveState");
		    datax.getDataFileName();
		}
	    });

	JButton conti = new JButton("NO SAVE");
	conti.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    dataDialogue datax = new dataDialogue("saveState");
		    datax.getDataFileName();
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    traceDialogue temp = new traceDialogue(traceDialogue.previous);
		    temp.getTraceFile();
		}
			
	    });

	panel.add(label1);
	panel.add(fileName);
	panel.add(label2);
	panel.add(save);
	panel.add(conti);
	panel.add(back);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
	
    }
}









