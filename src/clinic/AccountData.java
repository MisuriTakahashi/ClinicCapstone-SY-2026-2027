/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class AccountData {
        public ArrayList<AccountSystem> loadAll() throws SQLException {
           ArrayList<AccountSystem> accounts = new ArrayList<>();
           String sql = "SELECT name, password, role FROM ACCOUNTS";

           try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

               while (rs.next()) {
                   accounts.add(new AccountSystem(
                           rs.getString("name"),
                           rs.getString("password"),
                           rs.getString("role")
                   ));
               }
           }
           return accounts;
       }

       public boolean nameExists(String name) throws SQLException {
           String sql = "SELECT 1 FROM ACCOUNTS WHERE name = ?";
           try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
               ps.setString(1, name);
               try (ResultSet rs = ps.executeQuery()) {
                   return rs.next();
               }
           }
       }

       public void createAccount(String name, String password, String role) throws SQLException {
           String sql = "INSERT INTO ACCOUNTS(name, password, role) VALUES(?, ?, ?)";
           try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
               ps.setString(1, name);
               ps.setString(2, password);
               ps.setString(3, role);
               ps.executeUpdate();
           }
       }

       public AccountSystem authenticate(String name, String password) throws SQLException {
           String sql = "SELECT name, password, role FROM ACCOUNTS WHERE name = ? AND password = ?";
           try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
               ps.setString(1, name);
               ps.setString(2, password);
               try (ResultSet rs = ps.executeQuery()) {
                   if (rs.next()) {
                       return new AccountSystem(
                               rs.getString("name"),
                               rs.getString("password"),
                               rs.getString("role")
                       );
                   }
                   return null;
               }
           }
       }

       public boolean deleteAccount(String name) throws SQLException {
           String sql = "DELETE FROM ACCOUNTS WHERE name = ?";
           try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
               ps.setString(1, name);
               return ps.executeUpdate() > 0;
           }
       }
}
