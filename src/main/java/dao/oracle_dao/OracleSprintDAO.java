package dao.oracle_dao;

import caching.SingletonCache;
import connections.OracleConnection;
import dao.dao_interface.SprintDAO;
import domain.Sprint;
import domain.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OracleSprintDAO implements SprintDAO{
    private OracleConnection oracleConnection = new OracleConnection();
    private OracleTaskDAO oracleTaskDAO = new OracleTaskDAO();

    private Sprint extractSprintFromResultSet (ResultSet resultSet) throws SQLException {
        Sprint sprint = new Sprint();
        sprint.setSprintID(resultSet.getLong(2));
        sprint.setName(resultSet.getString(3));
        return sprint;
    }
    @Override
    public boolean insertSprint(Sprint sprint) {
        Connection connection = oracleConnection.getConnection();

        try {
            PreparedStatement addObject = connection.prepareStatement("insert into OBJECTS (OBJECT_ID, NAME, TYPE_ID) values (?, ?, 6)");
            PreparedStatement addName = connection.prepareStatement("insert into PARAMS (text_value, object_id, attribute_id) values (?, ?, 113)");

            addObject.setLong(1, sprint.getSprintID());
            addObject.setString(2, sprint.getName());

            addName.setString(1, sprint.getName());
            addName.setLong(2, sprint.getSprintID());

            StringBuffer linkQuery = new StringBuffer();
            for(int i = 0; i < sprint.getTaskList().size(); i++){
                Task task = sprint.getTaskList().get(i);
                linkQuery.append("insert into LINKS (link_id, parent_id, child_id, link_type_id) values ("
                        + task.getTaskID()*sprint.getSprintID()%1273 +
                        "," + sprint.getSprintID() +
                        "," + task.getTaskID() + "154);");
            }

            PreparedStatement linksStatement = connection.prepareStatement(linkQuery.toString());

            int i = addObject.executeUpdate();
            int j = addName.executeUpdate();
            int k = linksStatement.executeUpdate();

            if(i==1 && j==1 && k==1)
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Sprint findSprint(long key) {
        Sprint sprint = (Sprint) SingletonCache.getInstance().get(key);

        if(sprint != null)
            return sprint;

        Connection connection = oracleConnection.getConnection();
        List<Task> tasks = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            Statement taskStat = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select attr.ATTRIBUTE_ID, o.object_id, p.text_value\n" +
                    "from objects o\n" +
                    "inner join attributes attr on attr.type_id = o.TYPE_ID\n" +
                    "left join params p on p.ATTRIBUTE_ID = attr.ATTRIBUTE_ID\n" +
                    "and p.object_id = o.OBJECT_ID\n" +
                    "where o.object_id = " + key);

            ResultSet taskSet = taskStat.executeQuery("SELECT T.OBJECT_ID, T.name\n" +
                    "   FROM Objects T\n" +
                    "    INNER JOIN LINKS L ON L.CHILD_ID = T.OBJECT_ID\n" +
                    "    INNER JOIN LINKTYPES LT ON L.LINK_TYPE_ID = LT.LINK_TYPE_ID\n" +
                    "    INNER JOIN OBJECTS S ON L.PARENT_ID = S.OBJECT_ID\n" +
                    "    INNER JOIN TYPES ST ON S.TYPE_ID = ST.TYPE_ID\n" +
                    "    WHERE ST.TYPE_ID = 6\n" +
                    "    AND LT.LINK_TYPE_ID = 154\n" +
                    "    AND S.OBJECT_ID = " + key);

            sprint = extractSprintFromResultSet(resultSet);

            while(taskSet.next())
                tasks.add(oracleTaskDAO.findTask(taskSet.getLong(1)));

            sprint.setTaskList(tasks);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sprint;
    }

    @Override
    public boolean updateSprint(Sprint sprint) {
        Connection connection = oracleConnection.getConnection();
        try {
            PreparedStatement updateName = connection.prepareStatement("update params set text_value = ? where object_id = ? and attribute_id = 113");

            updateName.setString(1, sprint.getName());
            updateName.setLong(2, sprint.getSprintID());

            int i = updateName.executeUpdate();

            if(i==1)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteSprint(long key) {
        Connection connection = oracleConnection.getConnection();

        try {
            Statement statement = connection.createStatement();
            int i = statement.executeUpdate("delete from params where object_id = " + key);
            int j = statement.executeUpdate("delete from objects where object_id = " + key);
            int k = statement.executeUpdate("delete from links where child_id = " + key);
            int s = statement.executeUpdate("delete from links where parent_id = " + key);

            if(i==1 && j==1 && k==1 && s==1)
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}