import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Creates the GUI to find out if feedback is required once wireless error modelling is chosen.
 * Note that this does not happen when delay modelling is used. 
 * Stores preference in the OPTIONS file
 */ 

public class wirelessFeedback {

    private boolean feedbackChoice = true;
     private boolean choice = false;

    public void showMenu() {
	
	final JFrame frame = new JFrame("Feeback selection for Wireless Modelling");
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
	
	// create the radio buttons for the two choices

	// Button for choosing error modelling at the IP level
	JRadioButton feedback = new JRadioButton(" Yes ");
	feedback.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    feedbackChoice = true;
		    choice = true;
		}
	    });
	
	JRadioButton noFeedback = new JRadioButton(" No ");
	noFeedback.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    feedbackChoice = false;
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information
	
	JLabel label = new JLabel("Choose whether you want feedback simulation for the Wireless level modelling");
	
	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storeModelChoice(feedbackChoice);
			runExperiment();
		    }
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    errorOrDelayMenu temp = new errorOrDelayMenu();
		    temp.errorOrDelay();
		}

	    });
	
	JButton cancel = cancelButton();
	
	
	bgroup.add(feedback);
	bgroup.add(noFeedback);
	panel.add(label);
	panel.add(feedback);
	panel.add(noFeedback);	
	panel.add(proceed);
	panel.add(back);
	panel.add(cancel);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
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
    
    public void storeModelChoice(boolean error){
	try {
	    FileWriter x = new FileWriter("options", true);
	    final BufferedWriter options = new BufferedWriter(x);
	    
	    String feedback;
	    if (error)
		feedback = "TRUE";
	    else feedback = "FALSE";
	    String data = "<Wireless_Feedback> " + feedback + " </Wireless_Feedback>\n";
	    options.write(data, 0, data.length());
	    options.flush();
	    options.close();
	} catch (Exception e){
	    System.out.println("Exception while writing options in wirelessFeedback.java");
	}
	
    }
    
    public void runExperiment(){
	if (!(feedbackChoice))
	    {
		modelsMenu temp = new modelsMenu("wirelessFeedback");
		temp.makeModelMenu();
	    }
	else
	    {
		wirelessFECMenu temp = new wirelessFECMenu();
		temp.displayFEC();
	    }
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

