package dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.jdbc.DBConnectionPool;
import exceptions.ItemExistException;
import model.Company;
import model.Developer;
import model.Project;
import model.Skill;

import java.sql.*;

public abstract class AbstractDao<T, ID> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDao.class);

    protected String getGenericName()
    {
        return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
    }

    public String getNameTable() {
        switch(getGenericName()) {
            case "Company":
                return "companies";
            case "Developer":
                return "developers";
            case "Skill":
                return "skills";
            default:
                return "";
        }
    }

    public List<T> getList(String name) {
        List<T> list = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM "+getNameTable()+" WHERE name like('%"+name+"%')");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(create(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<T> getList() {
        List<T> list = new ArrayList<>();

        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM "+getNameTable());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                list.add(create(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
        return list;
    }

    public void fillList(Company item) {

        try (Connection connection = DBConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_company WHERE company_id=?");
            statement.setInt(1, item.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                item.addProjects((Project)create(resultSet));
            }

        } catch (SQLException e) {
            LOGGER.error("Exception occurred while connecting to DB");
            throw new RuntimeException(e);
        }
    }

    public T create(ResultSet resultSet) throws SQLException {
        switch(getGenericName()) {
            case "Company":
                return (T) createCompany(resultSet);
            case "Developer":
                return (T) createDeveloper(resultSet);
            case "Skill":
                return (T) createSkill(resultSet);
            case "Project":
                return (T) createProject(resultSet);
            default:
                return null;

        }

    }

    private Company createCompany(ResultSet resultSet) throws SQLException {
        Company company = new Company( resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("address"),
                resultSet.getString("country"),
                resultSet.getString("city"));
        fillList(company);
        return company;
    }

    private Developer createDeveloper(ResultSet resultSet) throws SQLException {
        Developer developer = new Developer();
        developer.setId(resultSet.getInt("id"));
        developer.setName(resultSet.getString("name"));
        developer.setAge(resultSet.getInt("age"));
        developer.setCountry(resultSet.getString("country"));
        developer.setCity(resultSet.getString("city"));
        developer.setJoinDate(resultSet.getDate("join_date"));
        //developer.setSkills(getDeveloperSkills(developer.getId()));
        return developer;
    }

    private Skill createSkill(ResultSet resultSet) throws SQLException {
        return new Skill(resultSet.getInt("skill_id"), resultSet.getString("skill_description"));
    }

    private Project createProject(ResultSet resultSet) throws SQLException {
        return new Project( resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"));
    }
}

