package dao.xml_dao;

import dao.dao_interface.CustomerDAO;
import domain.Customer;

public class XmlCustomerDAO implements CustomerDAO {
    @Override
    public boolean insertCustomer(Customer customer) {
        return false;
    }

    @Override
    public Customer findCustomer(long key) {
        return null;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return false;
    }

    @Override
    public boolean deleteCustomer(long key) {
        return false;
    }
}
