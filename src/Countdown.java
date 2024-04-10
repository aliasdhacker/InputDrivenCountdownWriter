import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Countdown {

    private static final String USAGE = "Usage: java Countdown <hour> <min> <message> \n\t* requires at least first two arguments.";
    ExecutorService threadManager = Executors.newFixedThreadPool(1);

    public void initializeCountdownTimer(String args[]) throws InterruptedException, IOException {
        int imin = 0;
        int ihour = 0;
        LocalDateTime now = LocalDateTime.now();

        System.out.println(USAGE);
        System.out.println("NOW HR: " + now.getHour() + " NOW MIN: " + now.getMinute());

        UserSpecifiedDeadline userSpecifiedDeadline = getUserSpecifiedDeadline(args);


        Runnable timer = generateTimerThread(userSpecifiedDeadline);

        threadManager.execute(timer);
        threadManager.shutdown();
        threadManager.awaitTermination(100, TimeUnit.DAYS);

        System.out.println("Done!");
        FileWriter f = new FileWriter("countdown.txt", false);
        f.write("Done!");
        f.close();
        System.exit(0);
    }

    private UserSpecifiedDeadline getUserSpecifiedDeadline(String[] args) {
        UserSpecifiedDeadline d = new UserSpecifiedDeadline();
        String hour = null;
        String min = null;
        String deadlineMessage = null;

        if (args.length < 2) {
            System.out.println("Deadline Hour? ");
            Scanner in = new Scanner(System.in);
            hour = in.next().trim();
            System.out.println("Deadline min? ");
            min = in.next().trim();
            in.nextLine();
            System.out.println("Message for deadline? ");
            deadlineMessage = in.nextLine();
        } else {
            hour = (args[0] != null) ? args[0] : "";
            min = (args[1] != null) ? args[1] : "";
            deadlineMessage = (args[2] != null) ? args[2] : "";
        }

        try {
            d.minutes = Integer.parseInt(min);
            d.hours = Integer.parseInt(hour);
            d.userSpecifiedMessage = deadlineMessage;
        } catch (Exception e) {
            System.err.println("Only numbers for hours / mins");

            System.exit(-1);
        }

        return d;
    }

    private Runnable generateTimerThread(UserSpecifiedDeadline deadline) {

        return new Runnable() {
            private LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, deadline.getLocalDateTimeForDeadline());
            int moderator = 0;
            boolean firstRun = true;

            @Override
            public void run() {
                while (duration.getSeconds() > 0) {
                    moderator++;

                    long seconds = duration.getSeconds();

                    int hours = (int) (seconds / 60 / 60);
                    int minutes = (int) (seconds / 60) - (hours * 60);
                    int modseconds = (int) (seconds % 60);

                    String sminutes = "00";
                    String shours = "00";
                    String smodseconds = "00";

                    if (hours < 10) {
                        shours = "0" + hours;
                    } else {
                        shours = String.valueOf(hours);
                    }
                    if (minutes < 10) {
                        sminutes = "0" + minutes;
                    } else {
                        sminutes = String.valueOf(minutes);
                    }
                    if (modseconds < 10) {
                        smodseconds = "0" + modseconds;
                    } else {
                        smodseconds = String.valueOf(modseconds);
                    }
                    duration = Duration.between(now, deadline.getLocalDateTimeForDeadline());

                    String generated = deadline.userSpecifiedMessage + shours + ":" + sminutes + ":" + smodseconds;
                    if (moderator % 60 == 0 || firstRun) {
                        moderator = 0;
                        firstRun = false;
                        System.out.println("tick-tock " + generated);
                    }
                    FileWriter f = null;
                    try {
                        f = new FileWriter("countdown.txt", false);

                        f.write(generated, 0, generated.length());
                        f.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    now = LocalDateTime.now();
                }

            }
        };

    }

    private class UserSpecifiedDeadline {
        public int days;
        public int hours;
        public int minutes;
        public int seconds;

        public LocalDateTime now;

        public String userSpecifiedMessage;

        UserSpecifiedDeadline() {
            now = LocalDateTime.now();
        }

        public LocalDateTime getLocalDateTimeForDeadline() {
            int year = now.getYear();
            int month = now.getMonthValue();
            int day = now.getDayOfMonth();
            return LocalDateTime.of(year, month, day, this.hours, this.minutes);
        }
    }

}
