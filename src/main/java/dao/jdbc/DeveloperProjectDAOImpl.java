package dao.jdbc;

import exceptions.DeleteException;
import exceptions.ItemExistException;
import model.DeveloperProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeveloperProjectDAOImpl {
    private static DeveloperProjectDAOImpl instance;
    private final static Logger LOGGER = LoggerFactory.getLogger(DeveloperProjectDAOImpl.class);

    public static DeveloperProjectDAOImpl getInstance() {
        if (instance == null) {
            instance = new DeveloperProjectDAOImpl();
        }
        return instance;
    }

    private static final String DELETE_SQL_QUERY = "DELETE FROM dev_projects WHERE developer_id=? AND project_id=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO dev_projects (developer_id,project_id) VALUES (?,?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM dev_projects";
    private static final String GET_BY_PROJECT_AND_CUSTOMER_SQL_QUERY = "SELECT 1 FROM dev_projects WHERE developer_id = ? AND project_id = ?";


    public boolean isExistDeveloperProject(DeveloperProject item) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_PROJECT_AND_CUSTOMER_SQL_QUERY);
            statement.setInt(1, item.getDeveloperId());
            statement.setInt(2, item.getProjectId());
            try {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getFetchSize() > 0;
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Exception occurred while reading data to dev_projects table");
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }


    public void save(DeveloperProject item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            if (!isExistDeveloperProject(item)) {
                try {
                    PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
                    statement.setInt(1, item.getDeveloperId());
                    statement.setInt(2, item.getProjectId());
                    statement.execute();
                    LOGGER.info("Developer: " + item + ". Was successfully added to DB.");
                } catch (SQLException e) {
                    connection.rollback();
                    LOGGER.error("Exception occurred inserting dev_projects table \"" + item + "\" to DB");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already exist project with that developer ");
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }

    }


    public void delete(DeveloperProject item) throws DeleteException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getDeveloperId());
            statement.setInt(2, item.getProjectId());
            statement.execute();
            LOGGER.info("Developer-project item " + item + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }



    private DeveloperProject createDeveloperProject(ResultSet resultSet) throws SQLException {
        DeveloperProject developerProject = new DeveloperProject();
        developerProject.setDeveloperId(resultSet.getInt("developerId"));
        developerProject.setProjectId(resultSet.getInt("projectId"));

        return developerProject;
    }


}

