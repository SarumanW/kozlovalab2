package dao.json_dao;

import dao.dao_interface.ManagerDAO;
import domain.Manager;

public class JsonManagerDAO implements ManagerDAO {
    @Override
    public boolean insertManager(Manager manager) {
        return false;
    }

    @Override
    public Manager findManager(long key) {
        return null;
    }

    @Override
    public boolean updateManager(Manager manager) {
        return false;
    }

    @Override
    public boolean deleteManager(long key) {
        return false;
    }
}