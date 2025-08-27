package loansystem.controller;

import loansystem.model.User;
import loansystem.dao.UserAdminDAO;
import java.sql.SQLException;
import java.util.List;

public class AdminController {
    private UserAdminDAO userAdminDao;
    
    public AdminController(UserAdminDAO userAdminDao) {
        if (userAdminDao == null) {
            throw new IllegalArgumentException("UserAdminDAO cannot be null");
        }
        this.userAdminDao = userAdminDao;
    }
    
    public List<User> getAllUsers() throws SQLException {
        return userAdminDao.getAllUsers();
    }
    
    public boolean updateUserStatus(int userId, String status) throws SQLException {
        return userAdminDao.updateUserStatus(userId, status);
    }
    
    public User getUserDetails(int userId) throws SQLException {
        return userAdminDao.getUserDetails(userId);
    }

    public boolean deleteUser(int userId) throws SQLException {
        return userAdminDao.deleteUser(userId);
    }
}