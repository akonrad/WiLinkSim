import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Responsible for choosing whether user wants packets dropped at
 * in the IP layer, if yes, then another option is presented for 
 * choosing the error model for the ip layer
 */

public class endToEndError {

    private static String selection;
    private static boolean choice = false;
    
    public void makeEndToEndMenu(){
	final JFrame frame = new JFrame("Model selection");
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
	
	// create the button for signifying no error
	JRadioButton noError = new JRadioButton(" No models required for ip packet loss ");
	noError.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "FALSE";
		    choice = true;
		}
	    });
	
	// create button for signifying that an error model is required for the ip layer also. next menu will be a list of error models
	JRadioButton yesError = new JRadioButton(" Show me models for ip packet loss ");
	yesError.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selection = "TRUE";
		    choice = true;
		}
	    });

	// label to be inserted in the Panel for information
	JLabel label = new JLabel("Do you want packet errors at the IP level?");
	
	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storeIpPreference(selection);
			// if packet error is required, then show error model menu
			if (selection.equals("TRUE")){
			    ipModelsMenu x = new ipModelsMenu("endToEndError");
			    x.makeModelMenu();
			}
			// no packet error is required, show dialogue for the wireless level
			else {
			    traceDialogue tracer = new traceDialogue("endToEndEror");
			    tracer.getTraceFile();
			    
			}
		    }
		}
	    });
	
	JButton cancel = cancelButton();


	bgroup.add(noError);
	bgroup.add(yesError);
	panel.add(noError);
	panel.add(yesError);	
	panel.add(label);
	panel.add(proceed);
	panel.add(cancel);
		
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }

    /** 
     * Writes the error model into the options file so that they can be read 
     * by the generator at a later time
     */
    public void storeIpPreference(String ip) {
	try {
	    FileWriter x = new FileWriter("options", true);
	    BufferedWriter optionWriter = new BufferedWriter(x);
	    String data = "<IpError> " + ip + " </IpError>\n";
	    optionWriter.write(data, 0, data.length());
	    optionWriter.flush();
	    optionWriter.close();
	} catch (Exception e) {
	    System.out.println("Error while writing to options file in endToEndError.java");
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

