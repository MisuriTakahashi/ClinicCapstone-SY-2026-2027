/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author PC
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinic_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";     // Default MySQL username
    private static final String PASSWORD = "123456";     // Default XAMPP password is empty, or enter your MySQL root password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
}
}
