package view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper extends View {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHelper.class);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private String input;

    private SkillView skillView = new SkillView();
    private ProjectView projectView = new ProjectView();
    private DeveloperView developerView = new DeveloperView();
    private CustomerView customerView = new CustomerView();
    private CompanyView companyView = new CompanyView();

    public void displayStartMenu() {
        int choice = 0;
        printLine();
        System.out.println("What data you want to operate with?");
        System.out.println("1 - Developers");
        System.out.println("2 - Skills");
        System.out.println("3 - Companies");
        System.out.println("4 - Projects");
        System.out.println("5 - Customers");
        printExitMessage();
        printLine();
        System.out.print("Please enter your choice:");

        try {
            input = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
            displayStartMenu();
        }
        isQuitInput(input, LOGGER);

        try {
            choice = Integer.valueOf(input);
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect value. Please try again.");
            displayStartMenu();
        }

        if (choice == 1) {
            developerView.displayDevMenu();
        } else if (choice == 2) {
            skillView.displaySkillMenu();
        } else if (choice == 3) {
            companyView.displayCompanyMenu();
        } else if (choice == 4) {
            projectView.displayProjectsMenu();
        } else if (choice == 5) {
            customerView.displayCustomersMenu();
        } else {
            System.out.println("An incorrect value. Please try again.");
            displayStartMenu();
        }

    }


}
