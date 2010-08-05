import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class savedExp{
    
    public void listSavedConfigs(){
	
	try {
	    FileReader x = new FileReader("SavedConfigs");
	    BufferedReader configFile = new BufferedReader(x);
	    LinkedList savedConfNames = new LinkedList();
	    String current;
	    while ((current = configFile.readLine()) != null)
		savedConfNames.add(current);
	    String[] confNames = new String[savedConfNames.size()];
	    int length = savedConfNames.size();
	    for (int i = 0; i < length; i++) {
		confNames[i] = (String) savedConfNames.removeFirst();
	    }
	    
	    final JFrame frame = new JFrame("Configuration choice");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    final String[] dummyNames = confNames;
	    JPanel panel = new JPanel();
	    
	    
	    panel.setBorder(BorderFactory.createEmptyBorder(
							    30, //top
							    30, //left
							    10, //bottom
							    30) //right
			    );
	    
	    //panel.setLayout(new GridLayout(0, 1));
	    
	    JLabel label = new JLabel("Choose from the following configurations");
	    
	    final JList list = new JList(confNames);
	    JScrollPane pane = new JScrollPane(list);
	    
	    JButton accept = new JButton("Accept");
	    accept.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			int selection = list.getSelectedIndex();
			if (selection == -1)
			    mustSelectSomething();
			else {
			    frame.dispose();
			    loadConfig(dummyNames[selection]);
			    dataDialogue dataa = new dataDialogue("savedExp");
			    dataa.getDataFileName();
			}
		    }
		});

	    JButton cancel = cancelButton();

	    JButton infoButton = new JButton("Information");
	    infoButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			int selecting = list.getSelectedIndex();
			if (selecting == -1)
			    mustSelectSomething();
			else 
			    {
				displayInfo(dummyNames[selecting]);
			    }
		    }
		});
				
	    JButton back = new JButton("Go Back");
	    back.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			frame.dispose();
			final JFrame frame2 = new JFrame();
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			final WSim userInterface = new WSim();
	
			/** 
	 * 2) Create the menu for choosing between new experiment or old one 
	 */
			JPanel panel = userInterface.createNewOrSaved();
	
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
			panel.add(newOrSavedButton);

	// button for cancelling and exiting
			JButton cancelButton = new JButton("Exit");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    userInterface.exit();
				}
			    
			    });
			panel.add(cancelButton);
	
			frame2.getContentPane().add(panel);
			frame2.pack();
			frame2.setVisible(true);
		    }
		});
	    
	    panel.add(label);
	    panel.add(pane);
	    panel.add(accept);
	    panel.add(cancel);
	    panel.add(back);
	    panel.add(infoButton);
	    frame.getContentPane().add(panel);
	    frame.pack();
	    frame.setVisible(true);
	    

	} catch (Exception e){
	    System.out.println("error while opening file in savedExp.java");
	}
    }

    public void mustSelectSomething(){
	final JFrame frame = new JFrame("Must select something from the list");
	
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
    
    /**
     * Writes the saved configurations into the option file, so that they can
     * be read by the error model generators
     */
    public void loadConfig(String configName) {
	try {
	    BufferedWriter optionWriter = new BufferedWriter(new FileWriter("options", false));
	    BufferedReader configReader = new BufferedReader(new FileReader(configName));

	    String line;
	    while ((line = configReader.readLine()) != null) {
		
		optionWriter.write(line, 0, line.length());
		optionWriter.newLine();
	    }

	    optionWriter.close();
	    configReader.close();

	} catch (Exception e) {
	    System.out.println("error while saving the config in savedExp.java:loadConfig");
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

    public void displayInfo(String config)
    {
	displayInfo x = new displayInfo();
	x.display(config);
    }

}
    




