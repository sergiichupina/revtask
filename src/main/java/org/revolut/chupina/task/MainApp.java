package org.revolut.chupina.task;

public class MainApp extends Service {

    static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = 0;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port " + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        new MainApp().run(new ApplicationConfig(), port != 0 ? port : DEFAULT_PORT);
    }
}

