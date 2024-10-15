package tests;

import irc.IrcA;
import irc.Sentence;
import irc.SentenceAnnotation;
import jvn.JvnInvocationHandler;

import java.util.Random;

public class ClientReadLockATest {
    public static void main(String[] args) {
        Random randomNumbers = new Random();
        int randomNum = (int)(Math.random() * (100 - 1 + 1)) + 1;

        try {
            // Create (or get if it already exists) a shared object named IRC
            SentenceAnnotation s = (SentenceAnnotation) JvnInvocationHandler.newInstance(new Sentence(), "IRC");

            // create the graphical part of the Chat application
            IrcA client = new IrcA(s);

            for (int i = 0; i <= randomNum; i++) {

                // Random read
                if (randomNumbers.nextBoolean()) {
                    // invoke the method
                    String sentence = client.sentence.read();
                    System.out.println("Read: " + sentence);

                    // display the read value
                    client.data.setText(sentence);
                    client.text.append(sentence + "\n");

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
