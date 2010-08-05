import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/***************************************** 
 * Responsible for listing the different error or delay models and storing
 * the chosen option in the OPTIONS file
 ***************************************
 */

public class ipModelsMenu {
    
    private static String selection;
    private static boolean choice = false;
    public static String previous;
    
    public ipModelsMenu(String prev) {
	previous = prev;
    }
            
    public void makeModelMenu(){
	final JFrame frame = new JFrame("Model selection for ip level packet losses");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
        panel.setLayout(new GridLayout(0, 1));
	
	// create the button group for the four radioButtons
	ButtonGroup bgroup = new ButtonGroup();
	
	// create the radio buttons for the two choices
	JRadioButton mta = new JRadioButton("MTA");
	mta.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "MTA";
		    choice = true;
		}
	    });

	JRadioButton hmm = new JRadioButton("HMM");
	hmm.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "HMM";
		    choice = true;
		}
	    });

	JRadioButton gilbert = new JRadioButton("GILBERT");
	gilbert.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "GILBERT";
		    choice = true;
		}
	    });

	JRadioButton m3 = new JRadioButton("M3");
	m3.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "M3";
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information
	JLabel label = new JLabel(" Choose type of error/delay model for IP level modelling ");

	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storeModelPreference(selection);
			traceDialogue tracer = new traceDialogue("ipModelsMenu");
			tracer.getTraceFile();
		    }
		}
	    });
    
	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    if (previous.equals("ipErrorOrDelay")) {
			ipErrorOrDelay temp = new ipErrorOrDelay();
			temp.errorOrDelay();
		    }
		    else if (previous.equals("ipFeedback")){
			ipFeedback temp = new ipFeedback();
			temp.showMenu();
		    }
		    else if (previous.equals("ipFECMenu")) {
			ipFECMenu temp = new ipFECMenu();
			temp.displayFEC();
		    }
		}

	    });
	JButton cancel = cancelButton();


	bgroup.add(hmm);
	bgroup.add(mta);
	bgroup.add(gilbert);
	bgroup.add(m3);
	panel.add(hmm);
	panel.add(mta);
	panel.add(gilbert);	
	panel.add(m3);
	panel.add(proceed);
	panel.add(back);
	panel.add(cancel);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }

    /** 
     * Writes the error model into the options file so that they can be read 
     * by the generator at a later time
     */
    public void storeModelPreference(String model) {
	try {
	    FileWriter x = new FileWriter("options", true);
	    BufferedWriter optionWriter = new BufferedWriter(x);
	    String data = "<IpErrorModel> " + model + " </IpErrorModel>\n";
	    optionWriter.write(data, 0, data.length());
	    optionWriter.flush();
	    optionWriter.close();
	} catch (Exception e) {
	    System.out.println("Error while writing to options file in ipModelsMenu.java");
	}
    }


   public void mustMakeChoice(){
	final JFrame frame = new JFrame("Must specify one option");

	JPanel panel = new JPanel();
	JLabel exitLabel = new JLabel("Must specify one option");
	JButton okButton = new JButton("Continue");
	okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		}
	    });

	panel.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );
        panel.setLayout(new GridLayout(0, 1));

	panel.add(exitLabel);
	panel.add(okButton);
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }
    
     public JButton cancelButton(){
	JButton cancel = new JButton("Exit");
	cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });

	return cancel;
    }
}
