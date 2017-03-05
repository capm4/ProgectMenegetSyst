package view;

import dao.jdbc.CustomerDAOImpl;
import dao.jdbc.ProjectCustomerDAOImpl;
import dao.jdbc.ProjectDAOImpl;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Customer;
import model.Project;
import model.ProjectCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProjectView extends View {
    public void displayProjectsMenu() {



    }

    private static Logger logger = LoggerFactory.getLogger(Project.class);
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        boolean con = true;
        ProjectDAOImpl dao = new ProjectDAOImpl();
        List<Project> items = dao.getAll();

        System.out.println();
        while (con) {
            System.out.println("1 - delete\n2 - update\n3 - select by id\n4 - insert\n5 - select all\n6 - add customer\n7 - delete customer\nInput number of operation: ");


            int numberOfOperation = readInt(1, 7);
            if (    numberOfOperation == 1 ||
                    numberOfOperation == 2 ||
                    numberOfOperation == 6 ||
                    numberOfOperation == 7) {

                printProjects(items);
                System.out.println("Input number of project: ");
                int elementNumber = readInt(1, items.size()) - 1;
                Project element = items.get(elementNumber);

                if (numberOfOperation == 1) {
                    deleteProject(element);
                }

                if (numberOfOperation == 2) {
                    updateProject(element);
                }

                if (numberOfOperation == 6) {
                    addCustomerToProject(element);
                }

                if (numberOfOperation == 7) {
                    deleteCustomerFromProject(element);
                }
            }

            if (numberOfOperation == 3) {
                getProjectById();
            }

            if (numberOfOperation == 4) {
                createNewProject();
            }

            if (numberOfOperation == 5) {
                System.out.println("Existent projects:");
                printProjects(items);
            }

            System.out.println("Do you want to continue?  (Y - yes/N - no) ");
            con = readBoolean();
        }
    }

    private static void createNewProject() {
        ProjectDAOImpl dao = new ProjectDAOImpl();
        Project element = new Project();
        System.out.println("Input new name:");
        element.setName(readStr());
        System.out.println("Input new description:");
        element.setDescription(readStr());
        try {
            dao.save(element);
        } catch (ItemExistException e) {
            e.printStackTrace();
        }
    }

    private static void getProjectById() {
        ProjectDAOImpl dao = new ProjectDAOImpl();
        System.out.println("Input id:");
        int id = readInt();
        System.out.print("Project with id = " + id + ": ");
        System.out.println(dao.getById(id));
    }

    private static void deleteCustomerFromProject(Project element) {
        ProjectCustomerDAOImpl dao2 = new ProjectCustomerDAOImpl();
        List<Customer> customers = dao2.getByProject(element);
        System.out.println("Project customers:");
        printCustomers(customers);
        System.out.println("Input number of customer:");
        Integer customerNumber = readInt(1,customers.size())-1;
        try {
            dao2.delete(new ProjectCustomer(element.getId(),customers.get(customerNumber).getId()));
        } catch (DeleteException e) {
            e.printStackTrace();
        }
    }

    private static void addCustomerToProject(Project element) {
        CustomerDAOImpl dao2 = new CustomerDAOImpl();
        List<Customer> customers = dao2.getAll();
        System.out.println("Existent customers:");
        printCustomers(customers);
        System.out.println((customers.size()+1)+". Create new customer");
        System.out.println("Input number of customer:");
        Integer customerNumber = readInt(1,customers.size()+1)-1;
        Customer customer;
        if (customerNumber==customers.size()){
            customer = createCustomer();
        }
        else{
            customer = customers.get(customerNumber);
        }
        ProjectCustomerDAOImpl dao3 = new ProjectCustomerDAOImpl();
        try {
            dao3.save(new ProjectCustomer(element.getId(),customer.getId()));
        } catch (ItemExistException e) {
            e.printStackTrace();
        }
    }

    private static void updateProject(Project element) {
        ProjectDAOImpl dao = new ProjectDAOImpl();
        System.out.println("Input new name:");
        element.setName(readStr());
        System.out.println("Input new description:");
        element.setDescription(readStr());
        try {
            dao.update(element);
        } catch (NoItemToUpdateException e) {
            e.printStackTrace();
        }
    }

    private static void deleteProject(Project element) {
        ProjectDAOImpl dao = new ProjectDAOImpl();
        try {
            dao.delete(element);
        } catch (DeleteException e) {
            System.out.println("Cannot delete a project");
        }
    }

    //TODO Move to CustomerView
    static private Customer createCustomer(){
        Customer c = new Customer();
        System.out.println("Input name:");
        c.setName(readStr());
        System.out.println("Input inn:");
        c.setInn(readInt());
        System.out.println("Input edrpou:");
        c.setEdrpou(readInt());
        CustomerDAOImpl dao  = new CustomerDAOImpl();
        try {
            dao.save(c);
        } catch (ItemExistException e) {
            e.printStackTrace();
        }
        ProjectCustomerDAOImpl dao2 = new ProjectCustomerDAOImpl();
        c.setId(dao2.selectCustomerId(c));
        return c;
    }

    private static boolean readBoolean() {
        try {
            String s = reader.readLine();
            if (!s.isEmpty() && (s.toLowerCase().equals("y"))){
                return true;
            }
            if (!s.isEmpty() && (s.toLowerCase().equals("n"))){
                return false;
            }
        } catch (IOException e){
            System.out.println("Input failed");
        }
        System.out.print("You should input either \"y\" or \"n\". Try input again.\n");
        return readBoolean();
    }

    private static int readInt() {
        String str = readStr();
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (IllegalArgumentException e) {
            System.out.println("Value should an integer. Try input again: ");
            return readInt();
        }
    }

    private static int readInt(int min, int max) {
        int i = readInt();
        if (i <= max && i >= min) {
            return i;
        } else {
            System.out.println("Value should be between " + min + " and " + max + ". Try input again: ");
            return readInt(max, min);
        }
    }

    private static String readStr(){
        String str = null;
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Input failed");
            throw new RuntimeException(e);
        }
    }

    private static void printProjects(List<Project> list){
        for (int i=0; i<list.size(); i++){
            System.out.println((i+1)+". "+list.get(i).getName());
        }
    }

    private static void printCustomers(List<Customer> list){
        for (int i=0; i<list.size(); i++){
            System.out.println((i+1)+". "+list.get(i).getName());
        }
    }

}