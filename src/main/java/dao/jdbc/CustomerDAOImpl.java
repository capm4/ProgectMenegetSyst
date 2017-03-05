package dao.jdbc;

import dao.AbstractDao;
import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAOImpl extends AbstractDao<Customer, Integer> implements DAO<Customer, Integer> {

    private static CustomerDAOImpl instance;
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerDAOImpl.class);

    private static final String UPDATE_SQL_QUERY = "UPDATE customers SET name=?, inn=?,edrpou=? WHERE id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM customers WHERE id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO customers(name, inn, edrpou) VALUES (?, ?, ?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM customers";
    private static final String GET_BY_ID_SQL_QUERY = "SELECT * FROM customers WHERE id = ?";

    public static CustomerDAOImpl getInstance() {
        if (instance == null) {
            instance = new CustomerDAOImpl();
        }
        return instance;
    }

    @Override
    public void save(Customer item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, item.getName());
            statement.setInt(2, item.getInn());
            statement.setInt(3, item.getEdrpou());

            if (getById(item.getId()) == null) {
                try {
                    statement.execute();
                    LOGGER.info("\"" + item + "\" added");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error("Exception occurred while inserting data to customers table");
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already customer with id:" + item.getId());
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Customer item) throws DeleteException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.execute();
            LOGGER.info("Customer with id: " + item.getId() + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Customer item) throws NoItemToUpdateException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL_QUERY);

            try {
                statement.setString(1, item.getName());
                statement.setInt(2, item.getInn());
                statement.setInt(3, item.getEdrpou());
                statement.setInt(4, item.getId());

                statement.execute();
                LOGGER.info("Customer with ID: " + item.getId() + " was updated.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer getById(Integer id) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL_QUERY);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createCustomer(resultSet);
            } else {
                LOGGER.error("NoSuchElementException occurred. Cannot find project with id " + id);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> custList = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL_QUERY);

            while (resultSet.next()) {
                custList.add(createCustomer(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return custList;
    }

    private Customer createCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt("ID"));
        customer.setName(resultSet.getString("NAME"));
        customer.setInn(resultSet.getInt("INN"));
        customer.setEdrpou(resultSet.getInt("EDRPOU"));
        return customer;
    }

    public boolean isExistCustomer(int id) {
        for (Customer customer : getAll()
                ) {
            if (customer.getId() == id)
                return true;
        }
        return false;
    }

    @Override
    public List<Customer> findByName(String name) {
        return getList(name);
    }
}


