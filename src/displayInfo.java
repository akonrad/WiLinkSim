import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class displayInfo{

    public void display(String config)
    {

	try {
	optionList opti = new optionList(config);
	
	final JFrame frame = new JFrame("information about a saved configuration");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel panel = new JPanel();
	panel.setBorder(BorderFactory.createEmptyBorder(
							30, //top
							30, //left
							10, //bottom
							30) //right
			);
    
	JTextPane ta = new JTextPane();
	String text = "";
    
	text += "\n Radio Packet Data Length = " + opti.dataLength;
	text += "\n Radio Packet Header Length = " + opti.headerLength;
	text += "\n Model for Radio = " + opti.wirelessErrorModel;
	text += "\n Mode for Radio Model = " + opti.wirelessMode;
	if (!opti.ipModel) 
	    {
		text += "\n No error model for IP level";
	    }
	else 
	    {
		text += "\n Error model for IP level = " + opti.ipErrorModel;
		text += "\n Mode for IP Model = " + opti.ipMode;
	    }
    
	JButton ok = new JButton("OK");
	ok.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    frame.dispose();
		}
	    });

	JLabel x = new JLabel(text);
	ta.setText(text);
	//JScrollPane pane = new JScrollPane(ta);
	panel.setLayout(new GridLayout(0, 1));
	


	panel.add(ta);
	panel.add(ok);
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
	
	} catch (Exception e){
	    e.printStackTrace();
	}
    }
}







