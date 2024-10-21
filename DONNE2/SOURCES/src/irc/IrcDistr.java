/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;
import jvn.*;
import java.io.*;


public class IrcDistr {
    public TextArea text;
    public TextField data;
    Frame frame;
    JvnObject sentence;

    /**
     * main method
     * create a JVN object named IRC for representing the Chat application
     **/
    public static void main(String[] argv) {
        try {
            // initialize JVN
        	System.out.println(argv[0]);
        	int arg =Integer.parseInt(argv[0]);
        	
            JvnNode server = JvnNode.jvnGetServer(arg);
            JvnObject object=null;
            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            if(arg!=0) {
             object = server.jvnLookupObject("IRC", server.getJvnClient());
            }
            if (object == null) {
                object = server.getJvnClient().jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                object.jvnUnLock();
                server.getJvnClient().jvnRegisterObject("IRC", object);
            }

            // create the graphical part of the Chat application
            new Irc(object);
        } catch (Exception e) {
            System.out.println("IRC problem : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * IRC Constructor
     *
     * @param jo the JVN object representing the Chat
     **/
    public IrcDistr(JvnObject jo) {
        sentence = jo;
        frame = new Frame();
        frame.setLayout(new GridLayout(1, 1));
        text = new TextArea(10, 60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);
        data = new TextField(40);
        frame.add(data);
        Button read_button = new Button("read");
        read_button.addActionListener(new readListenerD(this));
        frame.add(read_button);
        Button write_button = new Button("write");
        write_button.addActionListener(new writeListenerD(this));
        frame.add(write_button);
        frame.setSize(545, 201);
        text.setBackground(Color.black);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    JvnServerImpl s = JvnServerImpl.jvnGetServer();
                    s.jvnTerminate();
                    frame.dispose();
                    System.exit(0);
                } catch (Exception ex) {
                    System.out.println("Cannot execute jvnTerminate");
                }
            }
        });
    }
}


/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListenerD implements ActionListener {
    IrcDistr irc;

    public readListenerD(IrcDistr i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            // lock the object in read mode
            irc.sentence.jvnLockRead();

            // invoke the method
            String s = ((Sentence) (irc.sentence.jvnGetSharedObject())).read();

            // unlock the object
            irc.sentence.jvnUnLock();

            // display the read value
            irc.data.setText(s);
            irc.text.append(s + "\n");
        } catch (JvnException je) {
            System.out.println("IRC problem : " + je.getMessage());
        }
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListenerD implements ActionListener {
    IrcDistr irc;

    public writeListenerD(IrcDistr i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            // get the value to be written from the buffer
            String s = irc.data.getText();

            // lock the object in write mode
            irc.sentence.jvnLockWrite();

            // invoke the method
            ((Sentence) (irc.sentence.jvnGetSharedObject())).write(s);

            // unlock the object
            irc.sentence.jvnUnLock();
        } catch (JvnException je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }
    }
}
