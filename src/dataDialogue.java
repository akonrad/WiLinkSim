import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class dataDialogue {

    public static String previous;

    public dataDialogue (String prev) {
	previous = prev;
    }

    public void getDataFileName(){
	final JFrame frame = new JFrame("Data File Segment");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
	panel.setLayout(new GridLayout(0, 1));
	
	final JTextField dataName = new JTextField();
	
	JLabel label = new JLabel("Please enter the name of the data file to be run");
	
	JButton button = new JButton("Accept");
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String fileName = dataName.getText();
		    if (fileName.equals(""))
			mustTypeName();
		    frame.dispose();
		    doSomethingWithFile(fileName);
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    if (previous.equals("saveState")) {
			saveState temp = new saveState(saveState.saveFile);
			temp.save();
		    }
		    else if (previous.equals("savedExp")) {
			savedExp temp = new savedExp();
			temp.listSavedConfigs();
		    }
		}
			
	    });
	
	JButton cancel = cancelButton();
	
	panel.add(label);
	panel.add(dataName);
	panel.add(button);
	panel.add(back);
	panel.add(cancel);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }
    
    public void mustTypeName(){
	final JFrame frame = new JFrame("Must type some name");
	
	JPanel panel = new JPanel();
	JLabel exitLabel = new JLabel("Must type some file name");
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
    
    public void doSomethingWithFile(String file_name){
	/** 
	 * 1) Start the the sender thread which will start transferring the data file
	 */
	
	startup x = new startup();
	x.go(file_name);

    }

	
}	









