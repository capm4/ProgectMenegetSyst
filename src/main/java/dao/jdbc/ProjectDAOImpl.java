package dao.jdbc;

import dao.AbstractDao;
import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProjectDAOImpl extends AbstractDao<Project, Integer> implements DAO<Project, Integer> {

    private static ProjectDAOImpl instance;

    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectDAOImpl.class);

    private static final String UPDATE_SQL_QUERY = "UPDATE projects SET name=?, description=? WHERE id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM projects WHERE id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO projects(name, description) VALUES (?, ?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM projects";
    private static final String GET_BY_ID_SQL_QUERY = "SELECT * FROM projects WHERE id = ?";

    public static ProjectDAOImpl getInstance() {
        if (instance == null) {
            instance = new ProjectDAOImpl();
        }
        return instance;
    }

    @Override
    public void save(Project item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());

            if (getById(item.getId()) == null) {
                try {
                    statement.execute();
                    LOGGER.info("\"" + item + "\" added");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error("Exception occurred while inserting data to Projects table");
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already project with id:" + item.getId());
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Project item) throws DeleteException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.execute();
            LOGGER.info("Project with id: " + item.getId() + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Project item) throws NoItemToUpdateException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL_QUERY);

            try {
                statement.setString(1, item.getName());
                statement.setString(2, item.getDescription());
                statement.setInt(3, item.getId());

                statement.execute();
                LOGGER.info("Project with ID: " + item.getId() + " was updated.");
            } catch (SQLException e){
                LOGGER.error(e.getMessage());
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Project getById(Integer id) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL_QUERY);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createProject(resultSet);
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
    public List<Project> getAll() {
        List<Project> projectList = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL_QUERY);

            while (resultSet.next()) {
                projectList.add(createProject(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return projectList;
    }

    private Project createProject(ResultSet resultSet) throws SQLException {
        return new Project( resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"));
    }


    public List<Project> createProjects(ResultSet resultSet) {
        List<Project> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                list.add(new Project(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Project> findByName(String name) {
        return getList(name);
    }
}
