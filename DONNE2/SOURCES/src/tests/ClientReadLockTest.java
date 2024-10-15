package tests;

import irc.Irc;
import irc.Sentence;
import jvn.JvnObject;
import jvn.JvnServerImpl;

import java.io.Serializable;
import java.util.Random;

public class ClientReadLockTest {
    public static void main(String[] args) {
        Random randomNumbers = new Random();
        int randomNum = (int)(Math.random() * (100 - 1 + 1)) + 1;

        try {
            // initialize JVN
            JvnServerImpl server = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            JvnObject object = server.jvnLookupObject("IRC");

            if (object == null) {
                System.out.println("Create IRC object");
                object = server.jvnCreateObject((Serializable) new Sentence());
                // after creation, I have a write lock on the object
                object.jvnUnLock();
                server.jvnRegisterObject("IRC", object);
            }

            // create the graphical part of the Chat application
            Irc client = new Irc(object);

            for (int i = 0; i <= randomNum; i++) {

                // Random read
                if (randomNumbers.nextBoolean()) {
                    // lock the object in read mode
                    client.getSentence().jvnLockRead();

                    // invoke the method
                    String s = ((Sentence) (client.getSentence().jvnGetSharedObject())).read();
                    System.out.println("Read: " + s);

                    // unlock the object
                    client.getSentence().jvnUnLock();

                    // display the read value
                    client.data.setText(s);
                    client.text.append(s + "\n");

                    int threadRandomMillis = (int)(Math.random() * (10000 - 200 + 1)) + 200;
                    Thread.sleep(threadRandomMillis);
                }
            }
        } catch (Exception e) {
            System.out.println("ClientReadLock crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
