import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Creates the GUI to find out if error or delay is chosen for the chosen
 * model. Stores preference in the OPTIONS file
 */ 

public class errorOrDelayMenu {

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
	JRadioButton error = new JRadioButton(" Error Modelling (Corruption of packets) ");
	error.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    errorChoice = true;
		    choice = true;
		}
	    });
	
	JRadioButton delay = new JRadioButton(" Delay Modelling (Dropping of late packets");
	delay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    errorChoice = false;
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information
	
	JLabel label = new JLabel("Select the desired type of modelling for the wireless level ");
	
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
	    String data = "<Wireless_Mode> " + model + " </Wireless_Mode>\n";
	    options.write(data, 0, data.length());

	    if (!error)
		{
		    String data2 = "<Wireless_Feedback> FALSE </Wireless_Feedback>\n";
		    options.write(data2, 0, data2.length());
		}

	    options.flush();
	    options.close();
	} catch (Exception e){
	    System.out.println("Exception while reading the options file in errorOrDelayMenu");
	}
	
    }
    
    public void runErrorExperiment(){
	wirelessFeedback temp = new wirelessFeedback();
	temp.showMenu();
    }

    public void runDelayExperiment(){
	modelsMenu temp = new modelsMenu("errorOrDelayMenu");
	temp.makeModelMenu();
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
