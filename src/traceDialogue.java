import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class traceDialogue {
    
    public static String previous;

    public traceDialogue(String prev) {
	previous = prev;
    }

    public void getTraceFile() {
	final JFrame frame = new JFrame("Choose trace file");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
	panel.setLayout(new GridLayout(0, 1));
	
	final JTextField traceName = new JTextField();
	
	JLabel label = new JLabel("Please enter the name of the trace file for models");
	
	JButton button = new JButton("Accept");
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String fileName = traceName.getText();
		    if (fileName.equals(""))
			mustTypeName();
		    frame.dispose();
		    saveState state = new saveState(fileName);
		    state.save();
		}
	    });
	
	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    if (previous.equals("ipModelsMenu")) {
			ipModelsMenu temp = new ipModelsMenu(ipModelsMenu.previous);
			temp.makeModelMenu();
		    }
		    else if (previous.equals("modelsMenu")){
			modelsMenu temp = new modelsMenu(modelsMenu.previous);
			temp.makeModelMenu();
		    }
			
		}

	    });

	JButton cancel = cancelButton();
	
	panel.add(label);
	panel.add(traceName);
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
		    System.exit(0);
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



