import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WSim {

    public static boolean newExperiment = false;
    public static boolean choiceMade = false;
    private static BufferedWriter optionWriter;
    private boolean mainNetwork = false;
    private boolean choice = false;
    private String networkChoice = "";

    public JPanel createNewOrSaved(){
	JPanel newOrSavedPanel = new JPanel();

	newOrSavedPanel.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );
        newOrSavedPanel.setLayout(new GridLayout(0, 1));
	
	// create the button group for the two radioButtons
	ButtonGroup bgroup = new ButtonGroup();
	
	// create the radio buttons for the two choices
	JRadioButton newExp = new JRadioButton("Run a new experiment");
	newExp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    newExperiment = true;
		    choiceMade = true;
		}
	    });

	JRadioButton savedExp = new JRadioButton("Run a saved experiment");
	savedExp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    newExperiment = false;
		    choiceMade = true;
		}
	    });
	
	// label to be inserted in the Panel for information

	JLabel label = new JLabel("Choose from following options");

	
	bgroup.add(newExp);
	bgroup.add(savedExp);
	newOrSavedPanel.add(label);
	newOrSavedPanel.add(newExp);
	newOrSavedPanel.add(savedExp);
	

	return newOrSavedPanel;
    }

    public void runNewExp(){
	final JFrame frame = new JFrame("Network selection");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel networkSelectionPanel = new JPanel();

	networkSelectionPanel.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );
        networkSelectionPanel.setLayout(new GridLayout(0, 1));
	
	// create the button group for the two radioButtons
	ButtonGroup bgroup = new ButtonGroup();
	
	// create the radio buttons for the two choices
	JRadioButton mainNetworks = new JRadioButton("Choose from a standard network");
	mainNetworks.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    mainNetwork = true;
		    choice = true;
		}
	    });

	JRadioButton newNetwork = new JRadioButton("Specify your own network parameters");
	newNetwork.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    mainNetwork = false;
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information

	JLabel label = new JLabel("Choose from following options");

	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			if (mainNetwork)
			    runMainNetworks();
			else {
			    
			    newNetwork newNet = new newNetwork();
			    newNet.runNewNetworks();
		    }
		}
		}
	    });
    
	JButton cancel = cancelButton();

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    choice = false;
		    frame.dispose();

		    final JFrame frame2 = new JFrame();
		    frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    
		    /** 
	 * 2) Create the menu for choosing between new experiment or old one 
	 */
		    JPanel panel = createNewOrSaved();
		    
		    //button for finalizing choice in this menu. Its actionlistener will write the choice into the options file
		    JButton newOrSavedButton = new JButton("Proceed");
		    newOrSavedButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				if (!choiceMade)
				    mustMakeChoice();
				else {
				    choiceMade = false;
				    frame2.dispose();
				    if (newExperiment){
					runNewExp();
				    }
				    else runSavedExp();
				}
			    }
			});
		    panel.add(newOrSavedButton);

		    // button for cancelling and exiting
		    JButton cancelButton = new JButton("Exit");
		    cancelButton.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				exit();
			    }
			});
		    panel.add(cancelButton);
		    
		    frame2.getContentPane().add(panel);
		    frame2.pack();
		    frame2.setVisible(true);
		}
		
	    });


	bgroup.add(mainNetworks);
	bgroup.add(newNetwork);
	networkSelectionPanel.add(label);
	networkSelectionPanel.add(mainNetworks);
	networkSelectionPanel.add(newNetwork);	
	networkSelectionPanel.add(proceed);
	networkSelectionPanel.add(back);
	networkSelectionPanel.add(cancel);
	
	frame.getContentPane().add(networkSelectionPanel);
	frame.pack();
	frame.setVisible(true);
    }

    public JButton cancelButton(){
	JButton cancel = new JButton("Exit");
	cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    exit();
		}
	    });

	return cancel;
    }

    public void runSavedExp(){
	savedExp something = new savedExp();
	something.listSavedConfigs();
    }


    /** Creates the windows for choosing one of the three network options - GSM
     *  WLAN or GPRS */
    public void runMainNetworks(){
	final JFrame frame = new JFrame("Network selection");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel networkSelectionPanel = new JPanel();

	networkSelectionPanel.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );
        networkSelectionPanel.setLayout(new GridLayout(0, 1));
	
	// create the button group for the two radioButtons
	ButtonGroup bgroup = new ButtonGroup();
	
	// create the radio buttons for the two choices
	JRadioButton GSM = new JRadioButton("GSM");
	GSM.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    networkChoice = "GSM";
		    choice = true;
		}
	    });

	JRadioButton GPRS = new JRadioButton("GPRS");
	GPRS.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    networkChoice = "GPRS";
		    choice = true;
		}
	    });

	JRadioButton WLAN = new JRadioButton("WLAN");
	WLAN.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    networkChoice = "WLAN";
		    choice = true;
		}
	    });
	
	// label to be inserted in the Panel for information
	JLabel label = new JLabel("Choose your network preference");

	JButton proceed = new JButton("Proceed");
	proceed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choice)
			mustMakeChoice();
		    else {
			choice = false;
			frame.dispose();
			storeNetworkPreference(networkChoice);
			modelChoice temp = new modelChoice();
			temp.showModels();
		    }
		}
	    });
    
	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    choice = false;
		    frame.dispose();
		    runNewExp();
		}
	    });

	JButton cancel = cancelButton();


	bgroup.add(GSM);
	bgroup.add(GPRS);
	bgroup.add(WLAN);
	networkSelectionPanel.add(label);
	networkSelectionPanel.add(GSM);
	networkSelectionPanel.add(GPRS);
	networkSelectionPanel.add(WLAN);
	networkSelectionPanel.add(proceed);
	networkSelectionPanel.add(back);
	networkSelectionPanel.add(cancel);
	
	frame.getContentPane().add(networkSelectionPanel);
	frame.pack();
	frame.setVisible(true);
    }


    /**
     * This writes into the option file - the parameters of the network that 
     * the user has selected. It stores the data length and the header length
     */
    public void storeNetworkPreference(String networkChoice) {
	try {
	    FileWriter optionFile = new FileWriter("options", true);
	    optionWriter = new BufferedWriter(optionFile);

	    String frontData = "<Network_Data_Length> ";
	    String tailData = "</Network_Data_Length>\n";
	    String frontHeader = "<Network_Header_Length> ";
	    String tailHeader = "</Network_Header_Length>\n";
	    String dataOption, headerOption;
	    //optionWriter.write(frontData, 0, frontData.length());
	    if (networkChoice.equals("GSM"))
		 dataOption = frontData + "20 " + tailData;
	    else if (networkChoice.equals("GPRS"))
		dataOption = frontData + "15 " + tailData;
	    else dataOption = frontData + "25 " + tailData;
	    
	    optionWriter.write(dataOption, 0, dataOption.length());
	    
	    if (networkChoice.equals("GSM"))
	        headerOption = frontHeader + "20 " + tailHeader;
	    else if (networkChoice.equals("GPRS"))
		headerOption = frontHeader + "15 " + tailHeader;
	    else headerOption = frontHeader + "25 " + tailHeader;
	    optionWriter.write(headerOption, 0, headerOption.length());
	    optionWriter.flush();
	    optionWriter.close();
	} catch (Exception e) {
	    System.out.println("Error while writing to options file in storeNetworkChoice");
	    e.printStackTrace();
	}
    }

    public void runNewNetworks(){
	
    }


    public void exit(){
	System.exit(0);
    }


    /** Displays dialogue box which indicates the user must specify an option
     * This window is displayed when the user fails to indicate and option 
     * and simply presses a button */

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


    public static void main(String[] args){
	
	try {
	    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    FileWriter optionFile = new FileWriter("options");
	    optionWriter = new BufferedWriter(optionFile);
	} catch (Exception e) {
	    System.out.println("Error in opening the optionFile: WSim.java");
	}
	    
	/** 
	 * 1) Create the frame in which all the components will be shown
	 */ 
	final JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	final WSim userInterface = new WSim();
	
	/** 
	 * 2) Create the menu for choosing between new experiment or old one 
	 */
	JPanel panel = userInterface.createNewOrSaved();

	//button for finalizing choice in this menu. Its actionlistener will write the choice into the options file
	JButton newOrSavedButton = new JButton("Proceed");
	newOrSavedButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!choiceMade)
			userInterface.mustMakeChoice();
		    else {
			choiceMade = false;
			frame.dispose();
			if (newExperiment){
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
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
    }

}