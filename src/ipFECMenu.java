import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ipFECMenu {
    

    public void displayFEC() {

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
	
	final JTextField nValue = new JTextField();
	final JTextField mValue = new JTextField();

	JLabel nLabel = new JLabel("number of data packets (n):");
	JLabel mLabel = new JLabel("number of redundant packets (m):");
	
	JLabel label = new JLabel("Please enter the n and m values for FEC. We will send m redundant packets for every n data packets.");
	
	JButton button = new JButton("Accept");
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String n = nValue.getText();
		    String m = mValue.getText();
		    if (m.equals("") || n.equals(""))
			mustEnterValues();
		    frame.dispose();
		    saveValues(n, m);
		    ipModelsMenu temp = new ipModelsMenu("ipFECMenu");
		    temp.makeModelMenu();
		}
	    });
	
	JButton cancel = cancelButton();
	
	JButton noFEC = new JButton("No FEC Required");
	noFEC.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    saveNoFEC();
		    ipModelsMenu temp = new ipModelsMenu("ipFECMenu");
		    temp.makeModelMenu();
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    ipFeedback temp = new ipFeedback();
		    temp.showMenu();
		}

	    });

	panel.add(label);

	panel.add(nLabel);
	panel.add(nValue);
	panel.add(mLabel);
	panel.add(mValue);
	panel.add(new JLabel());
	panel.add(button);
	panel.add(noFEC);	
	panel.add(back);
	panel.add(cancel);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }
    
    public void saveValues(String n, String m)
    {
	try {
	    FileWriter x = new FileWriter("options", true);
	    final BufferedWriter options = new BufferedWriter(x);

	    String yesFEC = "<IP_FEC> TRUE </IP_FEC>\n";
	    String mValue = "<IP_FEC_MVALUE> " + m + " </IP_FEC_MVALUE>\n";
	    String nValue = "<IP_FEC_NVALUE> " + n + " </IP_FEC_NVALUE>\n";
	    
	    options.write(yesFEC, 0, yesFEC.length());
	    options.write(mValue, 0, mValue.length());
	    options.write(nValue, 0, nValue.length());
	    options.flush();
	    options.close();
	} catch (Exception e) {
	    System.out.println("Exception while writing values in ipFECMenu.java");
	}
    }

    public void saveNoFEC() 
    {
	try {
	    FileWriter x = new FileWriter("options", true);
	    final BufferedWriter options = new BufferedWriter(x);

	    String noFEC = "<IP_FEC> FALSE </IP_FEC>\n";
	    
	    options.write(noFEC, 0, noFEC.length());
	    options.flush();
	    options.close();
	} catch (Exception e) {
	    System.out.println("Exception while writing no FEC in ipFECMenu.java");
	}
    }
		
    public void mustEnterValues(){
	final JFrame frame = new JFrame("Must enter some numbers");
	
	JPanel panel = new JPanel();
	JLabel exitLabel = new JLabel("Must enter some values");
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


