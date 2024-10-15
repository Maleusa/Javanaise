package tests;

import irc.IrcA;
import irc.Sentence;
import irc.SentenceAnnotation;
import jvn.JvnInvocationHandler;

import java.util.Random;

public class ClientWriteLockATest {
    public static void main(String[] args) {
        Random randomNumbers = new Random();
        int randomNum = (int)(Math.random() * (100 - 1 + 1)) + 1;

        try {
            // Create (or get if it already exists) a shared object named IRC
            SentenceAnnotation s = (SentenceAnnotation) JvnInvocationHandler.newInstance(new Sentence(), "IRC");

            // create the graphical part of the Chat application
            IrcA client = new IrcA(s);

            int writeCounter = 0;
            for (int i = 0; i <= randomNum; i++) {

                // Random write
                if (randomNumbers.nextBoolean()) {
                    writeCounter++;
                    String string = randomNum + "_attempt_" + writeCounter;
                    System.out.println("Write: " + string);
                    client.sentence.write(string);

                    int threadRandomMillis = (int)(Math.random() * (10000 - 200 + 1)) + 200;
                    Thread.sleep(threadRandomMillis);
                }
            }
        } catch (Exception e) {
            System.out.println("ClientReadLockA crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
