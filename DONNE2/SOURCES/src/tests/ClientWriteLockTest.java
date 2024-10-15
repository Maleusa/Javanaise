package tests;

import irc.Irc;
import irc.Sentence;
import jvn.JvnObject;
import jvn.JvnServerImpl;

import java.io.Serializable;

public class ClientWriteLockTest {
    public static void main(String[] args) {
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

            int writeCounter = 0;
            for (int i = 0; i <= randomNum; i++) {
                // get the value to be written from the buffer
                writeCounter++;
                String s = randomNum + "_attempt_" + writeCounter;
                System.out.println("Write: " + s);

                // lock the object in write mode
                client.getSentence().jvnLockWrite();

                // invoke the method
                ((Sentence) (client.getSentence().jvnGetSharedObject())).write(s);

                // unlock the object
                client.getSentence().jvnUnLock();
                int threadRandomMillis = (int)(Math.random() * (10000 - 200 + 1)) + 200;
                Thread.sleep(threadRandomMillis);
            }
        } catch (Exception e) {
            System.out.println("ClientWriteLockTest crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
