package dao.xml_dao;

import dao.dao_interface.TaskDAO;
import domain.Task;

public class XmlTaskDAO implements TaskDAO {
    @Override
    public boolean insertTask(Task task) {
        return false;
    }

    @Override
    public Task findTask(long key) {
        return null;
    }

    @Override
    public boolean updateTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTask(long key) {
        return false;
    }
}
