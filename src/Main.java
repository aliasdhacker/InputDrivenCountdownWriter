import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("v0.1");
        Countdown countdown = new Countdown();

        try {
            countdown.initializeCountdownTimer(args);
        } catch (InterruptedException e) {
            System.err.println("Error with Thread management:");
            System.err.println(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error with IO:");
            System.err.println(e);
            throw new RuntimeException(e);
        }

    }
}