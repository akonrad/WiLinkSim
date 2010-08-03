import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Creates the GUI to find out if error or delay is chosen for the chosen
 * model. Stores preference in the OPTIONS file
 */ 

public class ipErrorOrDelay {

    private boolean errorChoice = true;
     private boolean choice = false;

    public void errorOrDelay() {
	
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
	
	// create the radio buttons for the two choices

	// Button for choosing error modelling at the IP level
	JRadioButton error = new JRadioButton(" Error Modelling (Corruption of packets) ");
	error.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    errorChoice = true;
		    choice = true;
		}
	    });
	
	JRadioButton delay = new JRadioButton(" Delay Modelling (Dropping of late packets)");
	delay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    errorChoice = false;
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information
	
	JLabel label = new JLabel("Select the desired type of modelling for IP level");
	
	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storeModelChoice(errorChoice);
			if (errorChoice)
			    runErrorExperiment();
			else runDelayExperiment();
		    }
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    modelChoice temp = new modelChoice();
		    temp.showModels();
		}
	    });
	
	JButton cancel = cancelButton();
	
	
	bgroup.add(error);
	bgroup.add(delay);
	panel.add(label);
	panel.add(error);
	panel.add(delay);	
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
	    
	    String model;
	    if (error)
		model = "ERROR";
	    else model = "DELAY";
	    String data = "<IP_Mode> " + model + " </IP_Mode>\n";
	    options.write(data, 0, data.length());
	    options.flush();

	    // If we are choosing the delay model, then there cannot be any use of feedback
	    if (!error)
		{
		    String data2 = "<IP_Feedback> FALSE </IP_Feedback>\n";
		    options.write(data2, 0, data2.length());
		    options.flush();
		}
	    options.close();

	} catch (Exception e){
	    System.out.println("Exception while writing options in ipErrorOrDelay.java");
	}
	
    }
    
    public void runDelayExperiment(){
	ipModelsMenu temp = new ipModelsMenu("ipErrorOrDelay");
	temp.makeModelMenu();
    }

    public void runErrorExperiment(){
	ipFeedback temp = new ipFeedback();
	temp.showMenu();
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
