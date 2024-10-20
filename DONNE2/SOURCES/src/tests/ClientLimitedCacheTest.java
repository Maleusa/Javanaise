package tests;

import irc.Irc;
import irc.Sentence;
import jvn.JvnObject;
import jvn.JvnServerImpl;

import java.io.Serializable;
import java.util.Random;

public class ClientLimitedCacheTest {
    public static void main(String[] args) {
        int attemptsNumber = 7;

        try {
            // initialize JVN
            JvnServerImpl server = JvnServerImpl.jvnGetServer();

            // This case is testing cache saturation with unique objects
            for (int attemptNumber = 1; attemptNumber <= attemptsNumber; attemptNumber++) {
                // look up the IRC object in the JVN server
                // if not found, create it, and register it in the JVN server
                JvnObject object = server.jvnLookupObject("IRC_" + attemptNumber);

                if (object == null) {
                    System.out.println("Create IRC_" + attemptNumber + " object");
                    object = server.jvnCreateObject((Serializable) new Sentence());
                    // after creation, I have a write lock on the object
                    object.jvnUnLock();
                    server.jvnRegisterObject("IRC_" + attemptNumber, object);
                }

                // create the graphical part of the Chat application
                Irc client = new Irc(object);

                // ------------------------------------
                // Write
                // ------------------------------------
                String s = "IRC_" + attemptNumber;
                System.out.println("Write: " + s);

                // lock the object in write mode
                client.getSentence().jvnLockWrite();

                // invoke the method
                ((Sentence) (client.getSentence().jvnGetSharedObject())).write(s);

                // unlock the object
                client.getSentence().jvnUnLock();

                // ------------------------------------
                // Read
                // ------------------------------------
                // lock the object in read mode
                client.getSentence().jvnLockRead();

                // invoke the method
                s = ((Sentence) (client.getSentence().jvnGetSharedObject())).read();
                System.out.println("Read: " + s);

                // unlock the object
                client.getSentence().jvnUnLock();

                // display the read value
                client.data.setText(s);
                client.text.append(s + "\n");

                int threadRandomMillis = (int)(Math.random() * (2000 - 200 + 1)) + 200;
                Thread.sleep(threadRandomMillis);
            }
        } catch (Exception e) {
            System.out.println("ClientLimitedCacheTest crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
