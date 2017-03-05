package dao.jdbc;

import dao.AbstractDao;
import dao.DAO;
import exceptions.DeleteException;
import exceptions.ItemExistException;
import exceptions.NoItemToUpdateException;
import model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAOImpl extends AbstractDao<Skill, Integer> implements DAO<Skill, Integer>   {

    private static SkillDAOImpl instance;

    private final static Logger LOGGER = LoggerFactory.getLogger(SkillDAOImpl.class);

    private final static String UPDATE_SQL_QUERY = "UPDATE skills SET skill_description=? WHERE skill_id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM skills WHERE skill_id = ? AND skill_description=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO skills(skill_id, skill_description) VALUES (?, ?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM skills";
    private static final String GET_BY_ID_SQL_QUERY = "SELECT * FROM skills WHERE skill_id = ?";

    public static SkillDAOImpl getInstance() {
        if (instance == null) {
            instance = new SkillDAOImpl();
        }
        return instance;
    }

    @Override
    public void save(Skill item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement =
                    connection.prepareStatement(INSERT_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.setString(2, item.getDescription());
            if (getById(item.getId()) == null) {
                try {
                    statement.execute();
                    connection.commit();
                    LOGGER.info("\"" + item + "\" added");
                } catch (SQLException e) {
                    LOGGER.error(e.getMessage());
                    connection.rollback();
                    LOGGER.error("Exception occurred while inserting data to Skills table");
                } finally {
                    connection.setAutoCommit(true);
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already skill with id:" + item.getId());
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Skill item) throws DeleteException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement =
                    connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getId());
            statement.setString(2, item.getDescription());
            try {
                statement.execute();
                connection.commit();
                LOGGER.info("Skill with id:" + item.getId() + "; description:"
                        + item.getDescription() + " deleted");
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.error("Can not delete skill ith id:" + item.getId() + "; description:"
                        + item.getDescription() + e.getMessage());
                throw new DeleteException();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Skill item) throws NoItemToUpdateException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement statement =
                    connection.prepareStatement(UPDATE_SQL_QUERY);
            statement.setString(1, item.getDescription());
            statement.setInt(2, item.getId());
            try {
                if (getById(item.getId()) != null) {
                    statement.execute();
                    connection.commit();
                    LOGGER.info("Skill description with ID=" + item.getId() + " updated to \"" + item + "\"");
                } else {
                    LOGGER.error("There is no skill to update with requested id:" + item.getId());
                    connection.rollback();
                    throw new NoItemToUpdateException();
                }
            } catch (SQLException e) {
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
    public Skill getById(Integer id) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_ID_SQL_QUERY);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createSkill(resultSet);
            } else {
                LOGGER.error("NoSuchElementException occurred. Cannot find skill with id " + id);
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Skill> getAll() {
        List<Skill> skillList = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_SQL_QUERY);

            while (resultSet.next()) {
                skillList.add(createSkill(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return skillList;
    }

    private Skill createSkill(ResultSet resultSet) throws SQLException {
        return new Skill(resultSet.getInt("skill_id"), resultSet.getString("skill_description"));
    }

    @Override
    public List<Skill> findByName(String name) {
        return getList(name);
    }


}

