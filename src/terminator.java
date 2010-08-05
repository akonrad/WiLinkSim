import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class terminator {

    public void show() {
	//startup x = new startup();
     	//startup.stop(); // stop all the old threads
	final JFrame frame = new JFrame("simulation has ended");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
        panel.setLayout(new GridLayout(0, 1));
	
	JButton restart = new JButton("Restart WSim");
	restart.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			FileWriter optionFile = new FileWriter("options");
			BufferedWriter optionWriter = new BufferedWriter(optionFile);
		    } catch (Exception e2) {
			System.out.println("Error in opening the optionFile: WSim.java");
		    }
		    
		    /*
		     * 1) Create the frame in which all the components will be shown
		     */

		    final JFrame frame2 = new JFrame();
		    frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    final WSim userInterface = new WSim();
		    
		    /*
		     * 2) Create the menu for choosing between new experiment or old one 
		     */

		    JPanel panel2 = userInterface.createNewOrSaved();
		    
		    //button for finalizing choice in this menu. Its actionlistener will write the choice into the options file
		    JButton newOrSavedButton = new JButton("Proceed");
		    newOrSavedButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				if (!WSim.choiceMade)
				    userInterface.mustMakeChoice();
				else {
				    WSim.choiceMade = false;
				    frame2.dispose();
				    if (WSim.newExperiment){
					userInterface.runNewExp();
				    }
				    else userInterface.runSavedExp();
				}
			    }
			});
		    panel2.add(newOrSavedButton);
		    
		    // button for cancelling and exiting
		    JButton cancelButton = new JButton("Exit");
		    cancelButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				userInterface.exit();
			    }
			});
		    panel2.add(cancelButton);
		    
		    frame2.getContentPane().add(panel2);
		    frame2.pack();
		    frame2.setVisible(true);
		    
		}
		
	    });
	
	JButton exit = new JButton("Exit");
	exit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });

	JLabel label = new JLabel("WSim has finished. Please select from the following options:");

	panel.add(label);
	panel.add(restart);
	panel.add(exit);

	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }
}







