/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import javax.swing.SwingWorker;

public class LS {

    public static void run(LOADINGSCREEN screen) {
        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
                protected Void doInBackground() throws Exception {
                publish(new Object[]{5, "Starting up..."});

                publish(new Object[]{20, "Applying theme..."});
                try {
                    com.formdev.flatlaf.FlatLightLaf.setup();
                } catch (Exception ex) {
                    System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
                }

                publish(new Object[]{45, "Connecting to database..."});
                DatabaseManager.initializeDatabase();

                publish(new Object[]{70, "Migrating accounts..."});
                try {
                    int migrated = new AccountData().migrateFromCsv("accounts.csv");
                    System.out.println("Migrated " + migrated + " account(s) from accounts.csv.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                publish(new Object[]{90, "Restoring session..."});

                publish(new Object[]{100, "Ready."});
                return null;
            }
            @Override
            protected void process(java.util.List<Object[]> chunks) {
                Object[] latest = chunks.get(chunks.size() - 1);
                int progress = (int) latest[0];
                String status = (String) latest[1];
                screen.getProgressBar().setValue(progress);
                screen.getStatusLabel().setText(status);
            }

            @Override
            protected void done() {
                screen.dispose();
                AccountSystem restoredAccount = SessionManager.loadSession();
                if (restoredAccount != null) {
                    new Dashboard(restoredAccount).setVisible(true);
                } else {
                    new LoginUi().setVisible(true);
                }
            }
        };
        worker.execute();
    }
}