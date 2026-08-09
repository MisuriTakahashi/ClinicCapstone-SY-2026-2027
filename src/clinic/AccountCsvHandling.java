/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class AccountCsvHandling {
    
      private final File csvFile;

    public AccountCsvHandling (String path) {
        this.csvFile = new File(path);
    }

    public ArrayList<AccountSystem> loadAll() throws IOException {

        ArrayList<AccountSystem> accounts = new ArrayList<>();

        if (!csvFile.exists()) {
            return accounts;
        }

        try (BufferedReader br =
                new BufferedReader(new FileReader(csvFile))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data =
                        line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length >= 3) {

                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].replace("\"", "").trim();
                    }

                    accounts.add(
                            new AccountSystem(
                                    data[0],
                                    data[1],
                                    data[2]
                            )
                    );
                }
            }
        }

        return accounts;
    }

    public boolean nameExists(String name) throws IOException {

        for (AccountSystem account : loadAll()) {

            if (account.GetName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
    //create an account
    public void createAccount(
            String name,
            String password,
            String role) throws IOException {

        AccountSystem account =
                new AccountSystem(name, password, role);

        try (BufferedWriter bw =
                new BufferedWriter(new FileWriter(csvFile, true))) {

            bw.write(account.toCsvLine());
            bw.newLine();
        }
    }

    // LOGIN
    public AccountSystem authenticate(
            String name,
            String password) throws IOException {

        for (AccountSystem account : loadAll()) {

            if (account.GetName().equalsIgnoreCase(name)
                    && account.GetPassword().equals(password)) {

                return account;
            }
        }

        return null;
    }
    //Delete an accounts
    public boolean deleteAccount(String name) throws IOException {

        ArrayList<AccountSystem> accounts = loadAll();

        boolean removed =
                accounts.removeIf(
                        account ->
                                account.GetName()
                                        .equalsIgnoreCase(name)
                );

        if (!removed) {
            return false;
        }

        try (BufferedWriter bw =
                new BufferedWriter(new FileWriter(csvFile))) {

            for (AccountSystem account : accounts) {

                bw.write(account.toCsvLine());
                bw.newLine();
            }
        }

        return true;
    }
}
