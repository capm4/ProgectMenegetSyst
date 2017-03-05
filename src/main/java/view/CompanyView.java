package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.jdbc.CompanyDAOImpl;
import dao.jdbc.ProjectDAOImpl;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Company;

public class CompanyView extends View {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyView.class);

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private String input;
    private CompanyDAOImpl companyDAO = CompanyDAOImpl.getInstance();
    private ProjectDAOImpl projectDAO = new ProjectDAOImpl();

    public void displayCompanyMenu() {
        int choice = 0;
        printLine();
        System.out.println("Please select option:");
        System.out.println("0 - Display find by name");
        System.out.println("1 - Display all available companys");
        System.out.println("2 - Insert new company");
        System.out.println("3 - Update company by ID");
        System.out.println("4 - Delete company by ID");
        System.out.println("5 - Display all available projects");
        System.out.println("6 - Insert new  projects");
        System.out.println("7 - Delete projects");
        System.out.println("8 - Return to main menu");
        printExitMessage();
        printLine();
        System.out.print("Please enter your choice:");

        try {
            input = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
            displayCompanyMenu();
        }
        isQuitInput(input, LOGGER);

        try {
            choice = Integer.valueOf(input);
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect value. Please try again.");
            displayCompanyMenu();
        }

        if (choice == 0) {
            displayFindByName();
        } else if(choice == 1) {
            displayAll();
        } else if (choice == 2) {
            insertCompany();
        } else if (choice == 3) {
            updateCompany();
        } else if (choice == 4) {
            deleteCompany();
        } else if (choice == 5) {
            displayProjectsAll();
        } else if (choice == 6) {
            insertProject();
        } else if (choice == 7) {
            deleteProject();
        } else if (choice == 8) {
            new ConsoleHelper().displayStartMenu();
        } else {
            System.out.println("An incorrect value. Please try again.");
            displayCompanyMenu();
        }
    }

    private void deleteProject() {
        printLine();
        try {
            System.out.print("Please enter ID of company: ");
            int companyId=Integer.valueOf(reader.readLine());
            System.out.print("Please enter ID of project: ");
            int projectId=Integer.valueOf(reader.readLine());

            companyDAO.deletePrpject(companyDAO.getById(companyId), projectDAO.getById(projectId));

        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect company ID value. Please try again.");
        } catch (DeleteException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCompanyMenu();
    }

    private void insertProject() {
        printLine();
        try {
            System.out.print("Please enter ID of company: ");
            int companyId=Integer.valueOf(reader.readLine());
            System.out.print("Please enter ID of project: ");
            int projectId=Integer.valueOf(reader.readLine());

            companyDAO.addProject(companyDAO.getById(companyId), projectDAO.getById(projectId));

        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect company ID value. Please try again.");
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCompanyMenu();
    }

    private void displayProjectsAll() {
        try {
            System.out.print("Please enter ID of company: ");
            companyDAO.getById(Integer.valueOf(reader.readLine())).getProjects().stream().forEach(System.out::println);
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCompanyMenu();
    }

    private void displayAll() {
        companyDAO.getAll().stream().sorted((d1, d2) -> d1.getId() - d2.getId()).forEach(System.out::println);
        displayCompanyMenu();
    }

    private void displayFindByName() {
        try {
            System.out.print("Please enter Name: ");
            companyDAO.getList(reader.readLine()).stream().sorted((d1, d2) -> d1.getId() - d2.getId()).forEach(System.out::println);
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCompanyMenu();
    }

    private void insertCompany() {
        printLine();
        Company company = new Company();
        try {
            System.out.print("Please enter ID of new company: ");
            company.setId(Integer.valueOf(reader.readLine()));
            if (companyDAO.isExistCompany(company.getId())) {
                System.out.print("There is already company with id: " + company.getId());
                displayCompanyMenu();
            }
            System.out.print("Please enter Name of new company: ");
            company.setName(reader.readLine());
            System.out.print("Please enter Address of new company: ");
            company.setAddress(reader.readLine());
            System.out.print("Please enter Country of new company: ");
            company.setCountry(reader.readLine());
            System.out.print("Please enter City of new company: ");
            company.setCity(reader.readLine());

            try {
                companyDAO.save(company);
            } catch (ItemExistException e) {
                System.out.print("Cannot add " + company + ". There is already company with id:" + company.getId());
            }
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
        displayCompanyMenu();
    }

    private void getInput() {
        try {
            input = reader.readLine();
        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
        }
    }

    private void deleteCompany() {
        printLine();
        System.out.print("Please enter company ID to delete: ");
        getInput();
        try {
            companyDAO.delete(companyDAO.getById(Integer.valueOf(input)));
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect company ID value. Please try again.");
        } catch (DeleteException e) {
            LOGGER.error(e.getMessage());
        }
        displayCompanyMenu();
    }

    private void updateCompany() {
        Company newCompany = new Company();
        Company companyToUpdate;
        printLine();
        System.out.print("Please enter company ID to update: ");
        try {
            input = reader.readLine();
            newCompany.setId(Integer.valueOf(input));
            if (!companyDAO.isExistCompany(newCompany.getId())) {
                System.out.println("There is no company to update with requested id:" + newCompany.getId());
                displayCompanyMenu();
            }
            companyToUpdate = companyDAO.getById(newCompany.getId());

            System.out.print("Current company name: \"" + companyToUpdate.getName()
                    + "\". New company name (enter \"-\" to leave current name): ");
            getInput();
            if (input.equals("-")){
                newCompany.setName(companyToUpdate.getName());
            } else {
                newCompany.setName(input);
            }

            System.out.print("Current company address: \"" + companyToUpdate.getAddress()
                    + "\". New company age (enter \"-\" to leave current address): ");
            getInput();
            if (input.equals("-")){
                newCompany.setAddress(companyToUpdate.getAddress());
            } else {
                newCompany.setAddress(input);
            }

            System.out.print("Current company country: \"" + companyToUpdate.getCountry()
                    + "\". New company country (enter \"-\" to leave current country): ");
            getInput();
            if (input.equals("-")){
                newCompany.setCountry(companyToUpdate.getCountry());
            } else {
                newCompany.setCountry(input);
            }

            System.out.print("Current company city: \"" + companyToUpdate.getCity()
                    + "\". New company city (enter \"-\" to leave current city): ");
            getInput();
            if (input.equals("-")){
                newCompany.setCity(companyToUpdate.getCity());
            } else {
                newCompany.setCity(input);
            }
            getInput();

            companyDAO.update(newCompany);

        } catch (IOException e) {
            LOGGER.error("IOException occurred:" + e.getMessage());
            new ConsoleHelper().displayStartMenu();
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException occurred:" + e.getMessage());
            System.out.println("An incorrect value. Please try again.");
        } catch (NoItemToUpdateException e) {
            LOGGER.error(e.getMessage());
        }
        displayCompanyMenu();
    }

}

