package dao.jdbc;

import exceptions.DeleteException;
import exceptions.ItemExistException;
import model.SkillsDeveloper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 03.03.2017.
 */
public class SkillsDeveloperDAOImpl {

    private static SkillsDeveloperDAOImpl instance;
    private final static Logger LOGGER = LoggerFactory.getLogger(SkillsDeveloperDAOImpl.class);

    public static SkillsDeveloperDAOImpl getInstance() {
        if (instance == null) {
            instance = new SkillsDeveloperDAOImpl();
        }
        return instance;
    }

    private static final String DELETE_SQL_QUERY = "DELETE FROM dev_skills WHERE developer_id=? AND skills_id =?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO dev_skills (developer_id,skills_id) VALUES (?,?)";
    private static final String GET_ALL_SQL_QUERY = "SELECT * FROM dev_skills";
    private static final String GET_BY_DEVELOPER_SQL_QUERY = "SELECT developers.* FROM dev_skills join developers on developers.id = dev_skills.developers.id WHERE skills_id = ?";
    private static final String GET_BY_SKILLS_SQL_QUERY = "SELECT skills.* FROM dev_skills join skills on skills.skill_id = dev_skills.skill.id WHERE skill_id = ?";
    private static final String GET_BY_DEVELOPERS_AND_SKILLS_SQL_QUERY = "SELECT 1 FROM dev_skills WHERE developer_id = ? AND skill_id = ?";

    public boolean isExistSkillsDeveloper(SkillsDeveloper item) {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_BY_DEVELOPERS_AND_SKILLS_SQL_QUERY);
            statement.setInt(1, item.getDeveloperId());
            statement.setInt(2, item.getSkillsId());
            try {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getFetchSize() > 0;
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                LOGGER.error("Exception occurred while reading data to dev_skills table");
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public void save(SkillsDeveloper item) throws ItemExistException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            if (!isExistSkillsDeveloper(item)) {
                try {
                    PreparedStatement statement = connection.prepareStatement(INSERT_SQL_QUERY);
                    statement.setInt(1, item.getDeveloperId());
                    statement.setInt(2, item.getSkillsId());
                    statement.execute();
                    LOGGER.info("Developer: " + item + ". Was successfully added to DB.");
                } catch (SQLException e) {
                    connection.rollback();
                    LOGGER.error("Exception occurred inserting dev_skills table \"" + item + "\" to DB");
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("Cannot add " + item + ". There is already exist skills in that developer ");
                throw new ItemExistException();
            }
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public void delete(SkillsDeveloper item) throws DeleteException {
        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getDeveloperId());
            statement.setInt(2, item.getSkillsId());
            statement.execute();
            LOGGER.info("skills_developer item " + item + " was deleted.");
        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    private SkillsDeveloper createDeveloperProject(ResultSet resultSet) throws SQLException {
        SkillsDeveloper skillsDeveloper = new SkillsDeveloper();
        skillsDeveloper.setDeveloperId(resultSet.getInt("developerId"));
        skillsDeveloper.setSkillsId(resultSet.getInt("skillsId"));

        return skillsDeveloper;
    }

}

