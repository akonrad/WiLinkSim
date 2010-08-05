import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Responsible for choosing whether user wants modelling at
 * in the IP layer, or if the user wants modelling in the wireless layer. 
 * Only one of them can be chosen and this represents a logical fork point
 * in the GUI
 */

public class modelChoice {

    private static String selection;
    private static boolean choice = false;
    
    public void showModels(){
	final JFrame frame = new JFrame("IP or wireless level selection");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
        panel.setLayout(new GridLayout(0, 1));
	
	// create the button group for the two radioButtons
	ButtonGroup bgroup = new ButtonGroup();
	
	// create the button to choose modelling at Wireless level
	JRadioButton wirelessModelling = new JRadioButton(" Wireless Level Modelling ");
	wirelessModelling.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "FALSE";
		    choice = true;
		}
	    });
	
	// create button to choose modelling at the IP level
	JRadioButton ipModelling = new JRadioButton(" IP level Modelling ");
	ipModelling.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "TRUE";
		    choice = true;
		}
	    });

	// label to be inserted in the Panel for information
	JLabel label = new JLabel("Please choose whether you want modelling at the IP layer or at the wireless layer");
	
	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storePreference(selection);
			// if the modelling is selected for IP level, then show choices for IP
			if (selection.equals("TRUE")){
			    ipErrorOrDelay temp = new ipErrorOrDelay();
			    temp.errorOrDelay();
			}
			// If on the other hand, wireless modelling is chosen, then show the choices for wireless modelling
			else {
			    errorOrDelayMenu temp = new errorOrDelayMenu();
			    temp.errorOrDelay();			    
			}
		    }
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    WSim temp = new WSim();
		    temp.runMainNetworks();
		}
	    });
	
	JButton cancel = cancelButton();


	bgroup.add(wirelessModelling);
	bgroup.add(ipModelling);
	panel.add(label);
	panel.add(wirelessModelling);
	panel.add(ipModelling);	
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
    public void storePreference(String ip) {
	try {
	    FileWriter x = new FileWriter("options", true);
	    BufferedWriter optionWriter = new BufferedWriter(x);
	    String ipChoice = "<IP_Model> " + ip + " </IP_Model>\n";
	    optionWriter.write(ipChoice, 0, ipChoice.length());
	    optionWriter.flush();

	    String wireless = "TRUE";

	    if (ip.equals("TRUE"))
		wireless = "FALSE";
		    
	    String wirelessChoice = "<Wireless_Model> " + wireless + " </Wireless_Model>\n";
	    optionWriter.write(wirelessChoice, 0, wirelessChoice.length());
	    optionWriter.flush();

	    optionWriter.close();

	} catch (Exception e) {
	    System.out.println("Error while writing to options file in modelChoice.java");
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

