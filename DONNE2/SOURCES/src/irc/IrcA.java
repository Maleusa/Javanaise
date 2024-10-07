package irc;


import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.rmi.RemoteException;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnServerImpl;


public class IrcA {
	protected TextArea text;
	protected TextField data;
	Frame frame;
	SentenceAnnotation sentence;

	/**
	 * main method
	 * create a JVN object nammed IRC for representing the Chat application
	 **/
	public static void main(String[] argv) {
		try {

			// initialize JVN
			JvnServerImpl js = JvnServerImpl.jvnGetServer();

			// look up the IRC object in the JVN server
			// if not found, create it, and register it in the JVN server
			SentenceAnnotation jo = (SentenceAnnotation) js.jvnLookupObject("IRC");
			if (jo == null) {
                jo = (SentenceAnnotation) js.jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                
                js.jvnRegisterObject("IRC", (JvnObject) jo);
            }
			// create the graphical part of the Chat application
			new IrcA(jo);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IRC problem : " + e.getMessage());
		}
	}

	/**
	 * IRC Constructor
	 * 
	 * @param jo the JVN object representing the Chat
	 **/
	public IrcA(SentenceAnnotation jo) {
		sentence = jo;
		frame = new Frame();
		frame.setLayout(new GridLayout(1, 1));
		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data = new TextField(40);
		frame.add(data);
		Button readButton = new Button("read");
		readButton.addActionListener(new ReadListener(this));
		frame.add(readButton);
		Button writeButton = new Button("write");
		writeButton.addActionListener(new WriteListener(this));
		frame.add(writeButton);
		frame.setSize(545, 201);
		text.setBackground(Color.black);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					e.getWindow().dispose();
				}
			}
		});
	}
}

/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class ReadListener implements ActionListener {
	IrcA irc;

	public ReadListener(IrcA ircA) {
		irc = ircA;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		// invoke the method
		String s = irc.sentence.read();

		// unlock the object && display the read value
		irc.data.setText(s);

		irc.text.append(s + "\n");
	}
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class WriteListener implements ActionListener {
	IrcA irc;

	public WriteListener(IrcA ircA) {
		irc = ircA;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		// get the value to be written from the buffer
		String s = irc.data.getText();

		// lock the object in write mode && invoke the method
		irc.sentence.write(s);
	}
}
