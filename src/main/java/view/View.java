package view;

import org.slf4j.Logger;

public class View {
    void printExitMessage() {
        System.out.println("To close application please enter \"Quit\"");
    }

    void printLine() {
        System.out.println("\n===============================================\n");
    }


    void isQuitInput(String input, Logger logger) {
        if (input.toLowerCase().equals("quit")) {
            logger.info("Application shutdown");
            System.exit(0);
        }
    }
}
