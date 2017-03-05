package dao.jdbc;

import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Customer;
import model.Project;
import model.ProjectCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProjectCustomerDAOImpl {

    private static ProjectDAOImpl instance;

    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectDAOImpl.class);

    private static final String DELETE_SQL_QUERY = "DELETE FROM projects_customers WHERE project_id = ? and customers_id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO projects_customers(project_id, customers_id) VALUES (?, ?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM projects_customers";
    private static final String GET_BY_PROJECT_SQL_QUERY = "SELECT customers.* FROM projects_customers join customers on customers.id = projects_customers.customers_id WHERE project_id = ?";
    private static final String GET_BY_CUSTOMER_SQL_QUERY = "SELECT projects.* FROM projects_customers join projects on project_id=projects.id WHERE customers_id = ?";
    private static final String GET_BY_PROJECT_AND_CUSTOMER_SQL_QUERY = "SELECT 1 FROM projects_customers WHERE project_id = ? and customers_id = ?";

    public static ProjectDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProjectDAOImpl();
        }
        return instance;
    }

    public void save(ProjectCustomer item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, item.getProjectId());
            statement.setInt(2, item.getCustomerId());

            if (! itemExists(item)) {
                try {
                    statement.execute();
                    LOGGER.info("\"" + item + "\" added");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error("Exception occurred while inserting data to projects_customers table");
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already such project_customer link:");
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    private boolean itemExists(ProjectCustomer item) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_PROJECT_AND_CUSTOMER_SQL_QUERY);
            statement.setInt(1, item.getProjectId());
            statement.setInt(2, item.getCustomerId());
            try {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getFetchSize() > 0;
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Exception occurred while reading data to projects_customers table");
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }


    public void delete(ProjectCustomer item) throws DeleteException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getProjectId());
            statement.setInt(2, item.getCustomerId());
            statement.execute();
            LOGGER.info("ProjectCustomer with : " + item + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public List<Customer> getByProject(Project item) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_PROJECT_SQL_QUERY);
            statement.setInt(1, item.getId());
            ResultSet resultSet = statement.executeQuery();
            return createCustomers(resultSet);
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public List<Project> getByCustomer(Customer item) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_CUSTOMER_SQL_QUERY);
            statement.setInt(1, item.getId());
            ResultSet resultSet = statement.executeQuery();
            return new ProjectDAOImpl().createProjects(resultSet);
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    //TODO move to CustomerDAOImpl
    private List<Customer> createCustomers(ResultSet resultSet) {
        List<Customer> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                list.add(new Customer(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("inn"),
                        resultSet.getInt("edrpou")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO move to CustomerDAOImpl
    public int selectCustomerId(Customer item){
        String SELECT_SQL_QUERY = "Select id from customers where name=? and inn=? and edrpou=?";
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_SQL_QUERY);
            statement.setString(1, item.getName());
            statement.setInt(2, item.getInn());
            statement.setInt(3, item.getEdrpou());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("ID");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }

    }
}

