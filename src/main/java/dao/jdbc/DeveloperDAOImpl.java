package dao.jdbc;

import dao.AbstractDao;
import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Developer;
import model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeveloperDAOImpl extends AbstractDao<Developer, Integer> implements DAO<Developer, Integer>   {

    private static DeveloperDAOImpl instance;

    public static DeveloperDAOImpl getInstance() {
        if (instance == null) {
            instance = new DeveloperDAOImpl();
        }
        return instance;
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(DeveloperDAOImpl.class);

    private final static String UPDATE_SQL_QUERY =
            "UPDATE developers SET name = ?, age = ?, country = ?, city = ?, join_date = ? WHERE id=?";
    private static final String DELETE_SQL_QUERY =
            "DELETE FROM developers WHERE id = ? ";
    private static final String DELETE_DEV_SKILLS_QUERY =
            "DELETE FROM dev_skills WHERE developer_id = ?";
    private static final String DELETE_DEV_PROJECTS_QUERY =
            "DELETE FROM dev_projects WHERE developer_id = ?";
    private static final String INSERT_SQL_QUERY =
            "INSERT INTO developers(id, name, age, country, city, join_date) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_DEPENDENCY_DEV_SKILL =
            "INSERT INTO dev_skills (developer_id, skills_id) VALUES (?, ?)";
    private static final String GET_ALL_SQL_QUERY =
            "SELECT * FROM developers";
    private static final String GET_ALL_SKILLS_BY_DEV_ID =
            "SELECT skill_id, skill_description FROM skills AS sk " +
                    "INNER JOIN dev_skills AS dsk ON skill_id = dsk.skills_id " +
                    "WHERE dsk.developer_id = ?;";
    private static final String GET_BY_ID_SQL_QUERY =
            "SELECT * FROM developers WHERE id = ?";


    @Override
    public void save(Developer item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            if (!isExistDeveloper(item.getId())) {
                try {
                    PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
                    statement.setInt(1, item.getId());
                    statement.setString(2, item.getName());
                    statement.setInt(3, item.getAge());
                    statement.setString(4, item.getCountry());
                    statement.setString(5, item.getCity());
                    statement.setDate(6, new java.sql.Date(item.getJoinDate().getTime()));
                    statement.execute();
                    insertDeveloperSkills(item, connection);
                    connection.commit();
                    LOGGER.info("Developer: " + item + ". Was successfully added to DB.");
                } catch (SQLException e) {
                    connection.rollback();
                    LOGGER.error("Exception occurred inserting Developer \"" + item + "\" to DB");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already developer with id:" + item.getId());
                throw new ItemExistException();
            }
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }

    }

    private void insertDeveloperSkills(Developer item, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_DEPENDENCY_DEV_SKILL);
        if (item.getSkills() != null) {
            for (Skill skill : item.getSkills()) {
                statement.setInt(1, item.getId());
                statement.setInt(2, skill.getId());
                statement.execute();
            }
        }
        LOGGER.info("Skills for developer with ID: " + item.getId() + " were added.");
    }

    @Override
    public void delete(Developer item) throws DeleteException {
        if (isExistDeveloper(item.getId())) {
            try (Connection connection = DBConnectionPool.getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
                try {
                    deleteDevSkills(item.getId(), connection);
                    deleteDevFromProjects(item.getId(), connection);
                    statement.setInt(1, item.getId());
                    statement.execute();
                    connection.commit();
                } catch (SQLException e){
                    connection.rollback();
                    LOGGER.error("Can not delete skill with id: " + item.getId() +". " + e.getMessage());
                    throw new DeleteException();
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.error("Exception occurred while connecting to DB");
                throw new RuntimeException(e);
            }
        }
    }

    private void deleteDevFromProjects(int id, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_DEV_PROJECTS_QUERY);
        statement.setInt(1, id);
        statement.execute();
        connection.commit();
        LOGGER.info("Developer with ID: " + id + " deleted from all projects.");
    }

    private void deleteDevSkills(int id, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_DEV_SKILLS_QUERY);
        statement.setInt(1, id);
        statement.execute();
        connection.commit();
        LOGGER.info("All skills for developer with id: " + id + " were deleted.");
    }

    @Override
    public void update(Developer item) throws NoItemToUpdateException {
        try(Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL_QUERY);

            try {
                statement.setString(1, item.getName());
                statement.setInt(2, item.getAge());
                statement.setString(3, item.getCountry());
                statement.setString(4, item.getCity());
                statement.setDate(5, new java.sql.Date(item.getJoinDate().getTime()));
                statement.setInt(6, item.getId());
                deleteDevSkills(item.getId(), connection);
                insertDeveloperSkills(item, connection);
                statement.execute();
                connection.commit();
                LOGGER.info("Developer with ID: " + item.getId() + " was updated.");
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
    public Developer getById(Integer id) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL_QUERY);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                try {
                    return createDeveloper(resultSet);
                } catch (SQLException e) {
                    LOGGER.error("Exception occurred while getting developer data: " + e.getMessage());
                    return null;
                }
            } else {
                LOGGER.error("NoSuchElementException occurred. Cannot find developer with id " + id);
                return null;
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Developer> getAll() {
        List<Developer> developers = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL_QUERY);

            while (resultSet.next()) {
                developers.add(createDeveloper(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return developers;
    }

    private List<Skill> getDeveloperSkills(Integer devId) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_ALL_SKILLS_BY_DEV_ID);
            statement.setInt(1, devId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                skills.add(new Skill(resultSet.getInt("skill_id"), resultSet.getString("skill_description")));
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while getting skills for developer");
            throw e;
        }
        return skills;
    }

    private Developer createDeveloper(ResultSet resultSet) throws SQLException {
        Developer developer = new Developer();
        developer.setId(resultSet.getInt("id"));
        developer.setName(resultSet.getString("name"));
        developer.setAge(resultSet.getInt("age"));
        developer.setCountry(resultSet.getString("country"));
        developer.setCity(resultSet.getString("city"));
        developer.setJoinDate(resultSet.getDate("join_date"));
        developer.setSkills(getDeveloperSkills(developer.getId()));
        return developer;
    }

    public boolean isExistDeveloper(int developerID) {
        for (Developer developer : getAll()) {
            if (developer.getId() == developerID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Developer> findByName(String name) {
        return getList(name);
    }


}

