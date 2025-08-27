package loansystem.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import loansystem.dao.UserDAO;
import loansystem.dao.DBConnection;
import loansystem.model.User;

public class UserController {
    private UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    // Authenticate user
    public User authenticateUser(String username, String password) {
    String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setUsername(rs.getString("username"));
            user.setRole(rs.getString("role"));
            user.setAccountStatus(rs.getString("account_status")); // Make sure this line exists
            return user;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    // Register new user
    public boolean registerUser(User user) {
        return userDAO.registerUser(user);
    }

    // Get user by ID
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    // In UserController.java
public boolean updateUserSalary(int userId, double salary) {
    String sql = "UPDATE Users SET salary = ? WHERE user_id = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setDouble(1, salary);
        stmt.setInt(2, userId);
        
        int affectedRows = stmt.executeUpdate();
        return affectedRows > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public boolean updateUser(User user) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    
    try {
        conn = DBConnection.getConnection();
        
        // SQL for updating all fields except password
        String sql = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, " +
                    "phone_number = ?, address = ?, salary = ?, credit_score = ? " +
                    "WHERE user_id = ?";
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, user.getFirstName());
        pstmt.setString(2, user.getLastName());
        pstmt.setString(3, user.getEmail());
        pstmt.setString(4, user.getPhoneNumber());
        pstmt.setString(5, user.getAddress());
        pstmt.setDouble(6, user.getSalary());
        pstmt.setInt(7, user.getCreditScore());
        pstmt.setInt(8, user.getUserId());
        
        int rowsAffected = pstmt.executeUpdate();
        
        // If password was changed, update it separately
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().equals("********")) {
            String passwordSql = "UPDATE Users SET password = ? WHERE user_id = ?";
            try (PreparedStatement pwdStmt = conn.prepareStatement(passwordSql)) {
                pwdStmt.setString(1, user.getPassword());
                pwdStmt.setInt(2, user.getUserId());
                pwdStmt.executeUpdate();
            }
        }
        
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("SQL Error updating user:");
        System.err.println("Message: " + e.getMessage());
        System.err.println("SQLState: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
}