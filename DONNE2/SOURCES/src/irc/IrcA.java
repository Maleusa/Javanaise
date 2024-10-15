/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

import jvn.JvnException;
import jvn.JvnInvocationHandler;
import jvn.JvnServerImpl;


public class IrcA {
    public TextArea text;
    public TextField data;
    JFrame frame;
    public SentenceAnnotation sentence;

    /**
     * main method
     * create a JVN object named IRC for representing the Chat application
     **/
    public static void main(String[] argv) {
        SentenceAnnotation s;

        try {
            // Create (or get if it already exists) a shared object named IRC
            s = (SentenceAnnotation) JvnInvocationHandler.newInstance(new Sentence(), "IRC");

            // create the graphical part of the Chat application
            new IrcA(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the chat GUI
     *
     * @param s the sentence object
     */
    public IrcA(SentenceAnnotation s) {
        sentence = s;
        frame = new JFrame();
        frame.setLayout(new GridLayout(1, 1));
        text = new TextArea(10, 60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);
        data = new TextField(40);
        frame.add(data);
        Button read_button = new Button("read");
        read_button.addActionListener(new readListener(this));
        frame.add(read_button);
        Button write_button = new Button("write");
        write_button.addActionListener(new writeListener(this));
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
class readListener implements ActionListener {
    IrcA irc;

    public readListener(IrcA i) {
        irc = i;
    }

    /**
     * Management of user events
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        // invoke the method
        String s = irc.sentence.read();

        // display the read value
        irc.text.append(s + "\n");
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListener implements ActionListener {
    IrcA irc;

    public writeListener(IrcA i) {
        irc = i;
    }

    /**
     * Management of user events
     *
     * @param e the event to be processed
     */
    public void actionPerformed(ActionEvent e) {
        // get the value to be written from the buffer
        String s = irc.data.getText();

        // invoke the method
        irc.sentence.write(s);

        irc.data.setText("");
    }
}