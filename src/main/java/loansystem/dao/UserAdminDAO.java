package loansystem.dao;

import loansystem.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAdminDAO {
    private Connection connection;

    public UserAdminDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY created_at DESC";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to get all users", e);
        }
        return users;
    }

    public boolean updateUserStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE Users SET account_status = ? WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Failed to update user status", e);
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new SQLException("Failed to delete user", e);
        }
    }

    public User getUserDetails(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to get user details", e);
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setRole(resultSet.getString("role"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhoneNumber(resultSet.getString("phone_number"));
        user.setAddress(resultSet.getString("address"));
        user.setCreditScore(resultSet.getInt("credit_score"));
        
        // Handle potential null values for salary
        double salary = resultSet.getDouble("salary");
        user.setSalary(resultSet.wasNull() ? null : salary);
        
        user.setAccountStatus(resultSet.getString("account_status"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setLastLogin(resultSet.getTimestamp("last_login"));
        return user;
    }
}