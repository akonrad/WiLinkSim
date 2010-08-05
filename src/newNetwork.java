import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class newNetwork {
    private boolean choice = false;
    
    public void runNewNetworks() {

	try {
	    FileWriter x = new FileWriter("options", true);
	    final BufferedWriter optionWriter = new BufferedWriter(x);
	

	final JFrame frame = new JFrame("Network selection");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel panel = new JPanel();
	
	panel.setBorder(BorderFactory.createEmptyBorder(
                                        60, //top
                                        60, //left
                                        20, //bottom
                                        60) //right
                                        );
        panel.setLayout(new GridLayout(0, 1));
	
	final JTextField dataLength = new JTextField(5);
	JLabel dataLabel = new JLabel("Enter the length of the data segment");
	JLabel headerLabel = new JLabel("Enter the length of the header segment");
	final JTextField headerLength = new JTextField(5);
	final JTextField time_wavelength = new JTextField(2);
	JLabel time_label = new JLabel("Enter the time it takes to transmit one packet");
	JButton button = new JButton("Accept");
	button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    headerLength.selectAll();
		    dataLength.selectAll();
		    time_wavelength.selectAll();
		    if (headerLength.getSelectedText() == null || dataLength.getSelectedText() == null || time_wavelength.getSelectedText() == null)
			mustMakeChoice();

		    else {
			int header = Integer.parseInt(headerLength.getText());
			int data = Integer.parseInt(dataLength.getText());
			int transmissionTime = Integer.parseInt(time_wavelength.getText());
			storeLength(header, data, transmissionTime, optionWriter);
			frame.dispose();
			modelsMenu x = new modelsMenu("newNetwork");
			x.makeModelMenu();
		    }
		}
	    });

	JButton back = new JButton("Go Back");
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		    WSim temp = new WSim();
		    temp.runNewExp();
		}

	    });

	JButton cancel = cancelButton();
	
	panel.add(dataLabel);
	panel.add(dataLength);
	panel.add(headerLabel);
	panel.add(headerLength);
	panel.add(time_label);
	panel.add(time_wavelength);	
	panel.add(button);
	panel.add(back);
	panel.add(cancel);
	
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);

	} catch (Exception e) {
	    System.out.println("Exception while reading the options file in newNetwork.java #1");
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
    
    public void storeLength(int headerLength, int dataLength, int transmissionTime, BufferedWriter options){
	try {
	    String data = "<Network_Data_Length> " + dataLength + " </Network_Data_Length>\n";
	    String header = "<Network_Header_Length> " + headerLength + " </Network_Header_Length>\n";
	    String transmission = "<Network_Transmission_Time> "  + transmissionTime + " <Network_Transmission_Time>\n";
 
	    options.write(data, 0, data.length());
	    options.write(header, 0, header.length());
	    options.write(transmission, 0, transmission.length());
	    options.flush();
	    options.close();
	} catch (Exception e){
	    System.out.println("Exception while reading the options file in newNetwork.java #1");
	}
	
    }
    public void mustMakeChoice(){
	final JFrame frame = new JFrame("Must specify all values");

	JPanel panel = new JPanel();
	JLabel exitLabel = new JLabel("Must specicy values for all fields");
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
}
