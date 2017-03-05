package view;

import dao.jdbc.CustomerDAOImpl;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomerView extends View {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerView.class);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private String input;
    private CustomerDAOImpl customerDAO = CustomerDAOImpl.getInstance();

    public void displayCustomerMenu() {
        int choice = 0;
        printLine();
        System.out.println("Please select option:");
        System.out.println("1 - Display all available customers");
        System.out.println("2 - Insert new customer");
        System.out.println("3 - Update customer by ID");
        System.out.println("4 - Delete customer by ID");
        System.out.println("5 - Return to main menu");
        printExitMessage();
        printLine();
        System.out.print("Please enter your choice:");

        try {
            input = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
            displayCustomerMenu();
        }
        isQuitInput(input, LOGGER);

        try {
            choice = Integer.valueOf(input);
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect value. Please try again.");
            displayCustomerMenu();
        }

        if (choice == 1) {
            displayAll();
        } else if (choice == 2) {
            insertCustomer();
        } else if (choice == 3) {
            updateCustomer();
        } else if (choice == 4) {
            deleteCustomer();
        } else if (choice == 5) {
            new ConsoleHelper().displayStartMenu();
        } else {
            System.out.println("An incorrect value. Please try again.");
            displayCustomerMenu();
        }
    }

    private void displayAll() {
        customerDAO.getAll().stream().sorted((d1, d2) -> d1.getId() - d2.getId()).forEach(System.out::println);
        displayCustomerMenu();
    }

    private void insertCustomer() {
        printLine();
        Customer customer = new Customer();
        try {
            System.out.print("Please enter ID of new customer: ");
            customer.setId(Integer.valueOf(reader.readLine()));
            if (customerDAO.isExistCustomer(customer.getId())) {
                System.out.print("There is already exist customer with id: " + customer.getId());
                displayCustomerMenu();
            }
            System.out.print("Please, enter Name of new customer: ");
            customer.setName(reader.readLine());
            System.out.print("Please, enter inn of new customer: ");
            customer.setInn(Integer.valueOf(reader.readLine()));
            System.out.print("Please enter edrpou of new customer: ");
            customer.setEdrpou(Integer.valueOf(reader.readLine()));

            try {
                customerDAO.save(customer);
            } catch (ItemExistException e) {
                System.out.print("Cannot add " + customer + ". There is already customer with id:" + customer.getId());
            }
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCustomerMenu();
    }

    private void getInput() {
        try {
            input = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
    }

    private void deleteCustomer() {
        printLine();
        System.out.print("Please enter customer ID to delete: ");
        getInput();
        try {
            customerDAO.delete(customerDAO.getById(Integer.valueOf(input)));
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect customer ID value. Please try again.");
        } catch (DeleteException e) {
            LOGGER.error(e.getMessage());
        }
        displayCustomerMenu();
    }

    private void updateCustomer() {
        Customer customer = new Customer();
        Customer customerToUpdate;
        printLine();
        System.out.print("Please enter customer ID to update: ");
        try {
            input = reader.readLine();
            customer.setId(Integer.valueOf(input));
            if (!customerDAO.isExistCustomer(customer.getId())) {
                System.out.println("There is no customer to update with requested id:" + customer.getId());
                displayCustomerMenu();
            }
            customerToUpdate = customerDAO.getById(customer.getId());

            System.out.print("Current customer name: \"" + customerToUpdate.getName()
                    + "\". New customer name (enter \"-\" to leave current name): ");
            getInput();
            if (input.equals("-")) {
                customer.setName(customerToUpdate.getName());
            } else {
                customer.setName(input);
            }

            System.out.print("Current customer inn: \"" + customerToUpdate.getInn()
                    + "\". New customer inn (enter \"-\" to leave current inn): ");
            getInput();
            if (input.equals("-")) {
                customer.setInn(customerToUpdate.getInn());
            } else {
                customer.setInn(Integer.valueOf(input));
            }

            System.out.print("Current customer edrpou: \"" + customerToUpdate.getEdrpou()
                    + "\". New customer edrpou (enter \"-\" to leave current edrpou): ");
            getInput();
            if (input.equals("-")) {
                customer.setEdrpou(customerToUpdate.getEdrpou());
            } else {
                customer.setEdrpou(Integer.valueOf(input));
            }


        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
            new ConsoleHelper().displayStartMenu();
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect value. Please try again.");

            displayCustomerMenu();
        }
    }

    public void displayCustomersMenu() {
        // TODO Auto-generated method stub

    }
}
