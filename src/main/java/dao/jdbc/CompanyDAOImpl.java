package dao.jdbc;

import dao.AbstractDao;
import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Company;
import model.DeveloperProject;
import model.Project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAOImpl extends AbstractDao<Company, Integer> implements DAO<Company, Integer> {

    private static CompanyDAOImpl instance;

    private final static Logger LOGGER = LoggerFactory.getLogger(CompanyDAOImpl.class);

    private final static String UPDATE_SQL_QUERY = "UPDATE companies SET name=?, address=?, country=?, city=? WHERE id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM companies WHERE id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO companies(id, name, address, country, city) VALUES (?, ?, ?, ?, ?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM companies";
    private static final String GET_BY_ID_SQL_QUERY = "SELECT * FROM companies WHERE id = ?";

    public static CompanyDAOImpl getInstance() {
        if (instance == null) {
            instance = new CompanyDAOImpl();
        }
        return instance;
    }

    @Override
    public void save(Company item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getAddress());
            statement.setString(4, item.getCountry());
            statement.setString(5, item.getCity());

            if (getById(item.getId()) == null) {
                try {
                    statement.execute();
                    connection.commit();
                    LOGGER.info("\"" + item + "\" added");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    connection.rollback();
                    LOGGER.error("Exception occurred while inserting data to Companys table");
                } finally {
                    connection.setAutoCommit(true);
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already company with id:" + item.getId());
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Company item) throws DeleteException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.execute();
            connection.commit();
            LOGGER.info("All company with id: " + item.getId() + " were deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Company item) throws NoItemToUpdateException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL_QUERY);

            try {
                statement.setString(1, item.getName());
                statement.setString(2, item.getAddress());
                statement.setString(3, item.getCountry());
                statement.setString(4, item.getCity());
                statement.setInt(5, item.getId());

                statement.execute();
                connection.commit();
                LOGGER.info("Company with ID: " + item.getId() + " was updated.");
            } catch (SQLException e){
                connection.rollback();
                LOGGER.error(e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }


        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Company getById(Integer id) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL_QUERY);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createCompany(resultSet);
            } else {
                LOGGER.error("NoSuchElementException occurred. Cannot find company with id " + id);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Company> getAll() {
        List<Company> companyList = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL_QUERY);

            while (resultSet.next()) {
                companyList.add(createCompany(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return companyList;
    }

    private Company createCompany(ResultSet resultSet) throws SQLException {
        return create(resultSet);
    }

    public boolean isExistCompany(int id) {
        long count=getAll().stream().filter((c) -> c.getId()==id).count();
        return count==0?false:true;
    }

    @Override
    public List<Company> findByName(String name) {
        return getList(name);
    }

    public void addProject(Company company, Project project) {

        try (Connection connection = DBConnectionPool.getConnection()) {
            if (!isExistCompanyProject(company, project)) {
                try {
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO project_company (company_id, project_id) VALUES (?,?)");
                    statement.setInt(1, company.getId());
                    statement.setInt(2, project.getId());
                    statement.execute();
                    LOGGER.info("Developer: " + company + ". Was successfully added to DB.");
                } catch (SQLException e) {
                    connection.rollback();
                    LOGGER.error("Exception occurred inserting project_company table to DB");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("Cannot add " + project + ". There is already exist project with that company ");
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }

    }

    private boolean isExistCompanyProject(Company company, Project project) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM project_company WHERE company_id = ? AND project_id = ?");
            statement.setInt(1, company.getId());
            statement.setInt(2, project.getId());
            try {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getFetchSize() > 0;
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Exception occurred while reading data to project_company table");
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public void deletePrpject(Company company, Project project) throws DeleteException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, company.getId());
            statement.setInt(2, project.getId());
            statement.execute();
            LOGGER.info("project_company item " + project + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }
}

