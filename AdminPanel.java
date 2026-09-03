package clinic;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ooxml.POIXMLException;

/** Administrator student-management screen. */
public class AdminPanel extends javax.swing.JFrame {
    private AccountSystem loggedInAccount;
    private String selectedStudentLrn;
    private TableRowSorter<DefaultTableModel> studentSorter;
    private javax.swing.JPanel activeAdminPanel;
    private final javax.swing.JButton DeleteInventoryBTN = new javax.swing.JButton("Delete");
    private final javax.swing.JButton AddMedicineBTN = new javax.swing.JButton("Add Medicine");

    public AdminPanel(AccountSystem account) {
        if (account == null || !account.canAccessAdminPanel()) throw new SecurityException("Access denied. Administrator role required.");
        FlatLightLaf.setup();
        initComponents();
        loggedInAccount = account;
        setIconImage(AppIcon.getIcon());
        configureStudentManagement();
        configureInventoryControls();
        configureRoleBasedAccess();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                SessionManager.saveSession(loggedInAccount);
            }
        });
        setLocationRelativeTo(null);
    }

    /* Existing NetBeans mapping: jTextField1=search; jTextField2=name;
       jTextField3=grade/section; jTextField4=LRN; jTextField5=allergy;
       jTextField6=health condition; jTextField7=parent; jTextField8=phone. */
    private void configureStudentManagement() {
        clearStudentForm();
        ReasonTable.setRowHeight(30);
        ReasonTable.setShowVerticalLines(false);
        // JTable does not support FlatLaf's "arc" style key. Applying it
        // here throws UnknownStyleException; the containing scroll pane does.
        jScrollPane4.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        restoreVisibleTextAndStyling();
        DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
        model.setRowCount(0);
        studentSorter = new TableRowSorter<>(model);
        ReasonTable.setRowSorter(studentSorter);
        jButton8.setText("Add Student");
        jButton8.addActionListener(e -> addStudent());
        jButton2.setText("Edit / Update");
        jButton2.addActionListener(e -> updateStudent());
        jButton3.setText("Import Excel");
        jButton3.addActionListener(e -> importStudents());
        jButton4.setText("Clear");
        jButton4.addActionListener(e -> clearStudentForm());
        jButton6.setText("Delete / Archive");
        jButton6.addActionListener(e -> archiveStudent());
        SearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterStudents(); }
            @Override public void removeUpdate(DocumentEvent e) { filterStudents(); }
            @Override public void changedUpdate(DocumentEvent e) { filterStudents(); }
        });
        loadStudents();
    }

    /** Restores explicit colours/text after FlatLaf has initialized the form. */
    private void restoreVisibleTextAndStyling() {
        Color formLabelColor = new Color(0x33, 0x33, 0x33);
        for (javax.swing.JLabel label : new javax.swing.JLabel[]{
                jLabel7, jLabel8, jLabel10, LRNLabel, jLabel9, jLabel29,
                jLabel30, jLabel31}) {
            label.setForeground(formLabelColor);
        }

        Color sidebarBackground = new Color(15, 23, 42);
        for (javax.swing.JButton button : new javax.swing.JButton[]{
                jButton5, StudentManagementBTN, AccManageBTN, ExportBTN, jButton7}) {
            button.setForeground(Color.WHITE);
            button.setBackground(sidebarBackground);
        }
        jButton5.setText("Inventory");
        StudentManagementBTN.setText("Student Management");
        AccManageBTN.setText("Account Management");
        ExportBTN.setText("Export Report");
        jButton7.setText("Return");
        jLabel1.setText("Activity Logs");
    }

    /** Applies the RBAC matrix without changing NetBeans-generated layout. */
    private void configureRoleBasedAccess() {
        boolean headAdmin = loggedInAccount != null && loggedInAccount.isHeadAdmin();
        boolean admin = loggedInAccount != null && loggedInAccount.isAdmin();

        // Bind navigation outside initComponents() so NetBeans layout remains
        // generated and untouched.
        StudentManagementBTN.addActionListener(e -> showStudentManagement());
        AccManageBTN.addActionListener(e -> showAccountManagement());

        // Inventory is the default administrative view.
        showInventory();

        // ADMIN may read students but only HEADADMIN can change/import them.
        jButton8.setEnabled(headAdmin);
        jButton2.setEnabled(headAdmin);
        jButton3.setEnabled(headAdmin);
        jButton6.setEnabled(headAdmin);
        if (!headAdmin) {
            jButton8.setToolTipText("Only Head Admin can manage student records.");
            jButton3.setToolTipText("Only Head Admin can import Excel data.");
        }

        CAdminBTN.setEnabled(headAdmin);
        ResetPassword.setEnabled(headAdmin);
        // Both administrative roles can access account management, but the
        // service layer limits an ADMIN to USER targets only.
        AccManageBTN.setEnabled(admin);
        ExportBTN.setEnabled(headAdmin);
        ExportBTN.setToolTipText(headAdmin ? null : "Only Head Admin can export reports.");
    }

    /** Adds the inventory delete control without editing generated form code. */
    private void configureInventoryControls() {
        // Inventory supports the same multi-row selection behaviour as the
        // reference application; all selected medicines are handled together.
        stockTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        stockTable.setModel(new DefaultTableModel(
                new Object[]{"Status", "Product", "Quantity", "Expiration Date"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        stockTable.getColumnModel().getColumn(0).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean selected,
                    boolean focused, int row, int column) {
                java.awt.Component component = super.getTableCellRendererComponent(
                        table, value, selected, focused, row, column);
                if (!selected) {
                    String status = value == null ? "" : value.toString();
                    component.setForeground(status.contains("Expired")
                            ? new Color(0xB9, 0x1C, 0x1C)
                            : status.contains("Low Stock")
                                    ? new Color(0xB4, 0x53, 0x09) : Color.DARK_GRAY);
                }
                return component;
            }
        });
        AddMedicineBTN.setForeground(Color.WHITE);
        AddMedicineBTN.setBackground(new Color(0x15, 0x65, 0xC0));
        AddMedicineBTN.setToolTipText("Add a new medicine to inventory");
        AddMedicineBTN.addActionListener(e -> addMedicine());
        DeleteInventoryBTN.setForeground(Color.WHITE);
        DeleteInventoryBTN.setBackground(new Color(0xB9, 0x1C, 0x1C));
        DeleteInventoryBTN.setToolTipText("Delete the selected inventory item");
        DeleteInventoryBTN.addActionListener(e -> deleteSelectedMedicine());
        jPanel1.add(DeleteInventoryBTN,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 590, 110, 50));
        jPanel1.add(AddMedicineBTN,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 590, 130, 50));
    }

    private void showStudentManagement() {
        switchAdminPanel(jPanel5);
    }

    private void showAccountManagement() {
        if (loggedInAccount == null || !loggedInAccount.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Account Management is available to Admin accounts only.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        switchAdminPanel(AccountManagementPanel);
        refreshAccountTable();
    }

    private void showInventory() {
        switchAdminPanel(jPanel4);
        refreshInventoryView();
    }

    /**
     * Makes the generated feature views mutually exclusive. Inventory spans
     * several generated components, with jPanel4 serving as its anchor;
     * jPanel3 is deliberately excluded because it is the permanent sidebar.
     */
    private void switchAdminPanel(javax.swing.JPanel activePanel) {
        if (activeAdminPanel == activePanel) {
            return;
        }

        jPanel5.setVisible(false);
        AccountManagementPanel.setVisible(false);
        jPanel4.setVisible(false);
        jPanel6.setVisible(false);
        jLabel1.setVisible(false);
        jLabel6.setVisible(false);
        RestockBTN.setVisible(false);
        RestockBTN1.setVisible(false);
        DeleteInventoryBTN.setVisible(false);
        AddMedicineBTN.setVisible(false);

        if (activePanel == jPanel4) {
            jPanel4.setVisible(true);
            jPanel6.setVisible(true);
            jLabel1.setVisible(true);
            jLabel6.setVisible(true);
            RestockBTN.setVisible(true);
            RestockBTN1.setVisible(true);
            DeleteInventoryBTN.setVisible(true);
            AddMedicineBTN.setVisible(true);
        } else if (activePanel != null) {
            activePanel.setVisible(true);
        }

        activeAdminPanel = activePanel;
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void refreshInventoryView() {
        DatabaseExecutor.run(() -> {
            MedicineData medicineData = new MedicineData();
            return new Object[]{medicineData.loadAll(), medicineData.loadActivityLog()};
        }, result -> {
            @SuppressWarnings("unchecked")
            ArrayList<Medicine> medicines = (ArrayList<Medicine>) result[0];
            @SuppressWarnings("unchecked")
            ArrayList<String> activities = (ArrayList<String>) result[1];
            DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
            model.setRowCount(0);
            for (Medicine medicine : medicines) {
                String status = medicine.getInventoryStatus();
                model.addRow(new Object[]{status, medicine.getname(), medicine.getquantity(),
                        medicine.getExpirationDate()});
            }
            // A blank separator makes individual audit entries readable in
            // the fixed-width activity panel without changing its layout.
            InventoryLogs.setText(String.join(
                    System.lineSeparator() + System.lineSeparator(), activities));
            InventoryLogs.setCaretPosition(0);
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load inventory: " + ex.getMessage(),
                "Inventory Error", JOptionPane.ERROR_MESSAGE));
    }

    private void deleteSelectedMedicine() {
        int[] selectedRows = stockTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one inventory item to delete.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<String> medicineNames = new ArrayList<>();
        StringBuilder namesPreview = new StringBuilder();
        for (int selectedRow : selectedRows) {
            int modelRow = stockTable.convertRowIndexToModel(selectedRow);
            if (modelRow < 0 || modelRow >= stockTable.getModel().getRowCount()) {
                JOptionPane.showMessageDialog(this, "Inventory changed. Please try again.",
                        "Delete Failed", JOptionPane.WARNING_MESSAGE);
                refreshInventoryView();
                return;
            }
            String medicineName = String.valueOf(stockTable.getModel().getValueAt(modelRow, 1));
            medicineNames.add(medicineName);
            namesPreview.append("- ").append(medicineName).append(System.lineSeparator());
        }
        if (JOptionPane.showConfirmDialog(this,
                "Delete the following medicine item(s)? This cannot be undone.\n\n" + namesPreview,
                "Confirm Delete", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }

        DeleteInventoryBTN.setEnabled(false);
        DatabaseExecutor.run(() -> {
            MedicineData medicineData = new MedicineData();
            int deleted = 0;
            for (String medicineName : medicineNames) {
                if (medicineData.deleteItem(medicineName, loggedInAccount.GetName())) deleted++;
            }
            return new DeleteResult(deleted, medicineNames.size() - deleted);
        }, result -> {
            DeleteInventoryBTN.setEnabled(true);
            if (result.deleted() > 0) {
                refreshInventoryView();
                JOptionPane.showMessageDialog(this, result.deleted() + " inventory item(s) deleted."
                        + (result.notFound() > 0 ? " " + result.notFound() + " item(s) no longer existed." : ""),
                        "Delete Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The selected items no longer exist.",
                        "Delete Failed", JOptionPane.WARNING_MESSAGE);
            }
        }, ex -> {
            DeleteInventoryBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this,
                    "Unable to delete the inventory item: " + ex.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    /** Prompts for a new inventory item without changing the generated form. */
    private void addMedicine() {
        String name = JOptionPane.showInputDialog(this, "Product / medicine name:",
                "Add Medicine", JOptionPane.QUESTION_MESSAGE);
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A medicine name is required.",
                    "Invalid Details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String purpose = JOptionPane.showInputDialog(this,
                "What is this medicine for? (Example: fever / lagnat):",
                "Add Medicine", JOptionPane.QUESTION_MESSAGE);
        if (purpose == null) return;
        purpose = purpose.trim();
        if (purpose.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A medicine purpose is required so the system can make suggestions.",
                    "Purpose Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rawQuantity = JOptionPane.showInputDialog(this, "Initial quantity:",
                "Add Medicine", JOptionPane.QUESTION_MESSAGE);
        if (rawQuantity == null) return;
        final int quantity;
        try {
            quantity = Integer.parseInt(rawQuantity.trim());
            if (quantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a non-negative whole quantity.",
                    "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String expirationDate = JOptionPane.showInputDialog(this,
                "Expiration date (YYYY-MM-DD):", "Add Medicine",
                JOptionPane.QUESTION_MESSAGE);
        if (expirationDate == null) return;
        expirationDate = expirationDate.trim();
        try {
            LocalDate.parse(expirationDate);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid expiration date in YYYY-MM-DD format.",
                    "Invalid Expiration Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String medicineName = name;
        final String storedExpirationDate = expirationDate;
        final String medicinePurpose = purpose;
        AddMedicineBTN.setEnabled(false);
        DatabaseExecutor.run(() -> {
            MedicineData medicineData = new MedicineData();
            if (medicineData.nameExists(medicineName)) return false;
            medicineData.addItem(medicineName, storedExpirationDate, quantity,
                    medicinePurpose, loggedInAccount.GetName());
            return true;
        }, added -> {
            AddMedicineBTN.setEnabled(true);
            if (added) {
                refreshInventoryView();
                JOptionPane.showMessageDialog(this, "Medicine added successfully.",
                        "Add Medicine", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "A medicine with that name already exists.",
                        "Duplicate Medicine", JOptionPane.WARNING_MESSAGE);
            }
        }, ex -> {
            AddMedicineBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to add medicine: " + ex.getMessage(),
                    "Add Medicine Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private String selectedInventoryMedicineName() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an inventory item first.",
                    "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = stockTable.convertRowIndexToModel(selectedRow);
        return String.valueOf(stockTable.getModel().getValueAt(modelRow, 1));
    }

    private void restockSelectedMedicine() {
        String medicineName = selectedInventoryMedicineName();
        if (medicineName == null) return;
        String rawQuantity = JOptionPane.showInputDialog(this,
                "Quantity to add for \"" + medicineName + "\":", "Restock Inventory",
                JOptionPane.QUESTION_MESSAGE);
        if (rawQuantity == null) return;
        final int quantity;
        try {
            quantity = Integer.parseInt(rawQuantity.trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a whole quantity greater than zero.",
                    "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }
        RestockBTN.setEnabled(false);
        DatabaseExecutor.run(() -> new MedicineData().restockInventory(medicineName,
                quantity, loggedInAccount.GetName()), restocked -> {
            RestockBTN.setEnabled(true);
            if (restocked) {
                refreshInventoryView();
                JOptionPane.showMessageDialog(this, "Inventory restocked successfully.",
                        "Restock Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The selected item no longer exists.",
                        "Restock Failed", JOptionPane.WARNING_MESSAGE);
            }
        }, ex -> {
            RestockBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to restock inventory: " + ex.getMessage(),
                    "Restock Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void editSelectedMedicine() {
        if (stockTable.getSelectedRowCount() > 1) {
            JOptionPane.showMessageDialog(this, "Select only one inventory item to edit.",
                    "Multiple Items Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String currentName = selectedInventoryMedicineName();
        if (currentName == null) return;
        DatabaseExecutor.run(() -> new MedicineData().findByName(currentName), medicine -> {
            if (medicine == null) {
                JOptionPane.showMessageDialog(this, "The selected item no longer exists.",
                        "Edit Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String newName = JOptionPane.showInputDialog(this, "Product name:", medicine.getname());
            if (newName == null) return;
            newName = newName.trim();
            String purpose = JOptionPane.showInputDialog(this,
                    "What is this medicine for? (Example: fever / lagnat):", medicine.getPurpose());
            if (purpose == null) return;
            purpose = purpose.trim();
            if (purpose.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A medicine purpose is required.",
                        "Purpose Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String expiration = JOptionPane.showInputDialog(this,
                    "Expiration date (YYYY-MM-DD):", medicine.getExpDate());
            if (expiration == null) return;
            String rawQuantity = JOptionPane.showInputDialog(this, "Current quantity:",
                    String.valueOf(medicine.getquantity()));
            if (rawQuantity == null) return;
            final int quantity;
            try {
                quantity = Integer.parseInt(rawQuantity.trim());
                if (quantity < 0 || newName.trim().isEmpty()) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Provide a product name and a non-negative whole quantity.",
                        "Invalid Details", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (currentName.equals(newName.trim())
                    && medicine.getExpDate().equals(expiration.trim())
                    && medicine.getquantity() == quantity
                    && medicine.getPurpose().equals(purpose)) {
                JOptionPane.showMessageDialog(this, "No changes were made.",
                        "Edit Cancelled", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try {
                LocalDate.parse(expiration.trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Enter a valid expiration date in YYYY-MM-DD format.",
                        "Invalid Expiration Date", JOptionPane.WARNING_MESSAGE);
                return;
            }
            final String finalNewName = newName.trim();
            final String finalExpiration = expiration.trim();
            final String finalPurpose = purpose;
            RestockBTN1.setEnabled(false);
            DatabaseExecutor.run(() -> new MedicineData().editItem(currentName,
                    finalNewName, finalExpiration, quantity, finalPurpose, loggedInAccount.GetName()), edited -> {
                RestockBTN1.setEnabled(true);
                if (edited) {
                    refreshInventoryView();
                    JOptionPane.showMessageDialog(this, "Inventory item updated.",
                            "Edit Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "The selected item no longer exists.",
                            "Edit Failed", JOptionPane.WARNING_MESSAGE);
                }
            }, ex -> {
                RestockBTN1.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Unable to update inventory: " + ex.getMessage(),
                        "Edit Error", JOptionPane.ERROR_MESSAGE);
            });
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load the selected inventory item: " + ex.getMessage(),
                "Edit Error", JOptionPane.ERROR_MESSAGE));
    }

    private boolean requireHeadAdmin(String action) {
        if (loggedInAccount != null && loggedInAccount.isHeadAdmin()) return true;
        JOptionPane.showMessageDialog(this, "Only Head Admin can " + action + ".",
                "Access Denied", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    private void filterStudents() {
        if (studentSorter == null) return;
        String query = SearchField.getText().trim();
        studentSorter.setRowFilter(query.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 0, 1, 2));
    }
    private StudentForm readStudentForm() {
        StudentForm form = new StudentForm(NameField.getText().trim(), GradeSectionField.getText().trim(), LrnField.getText().trim(), AdviserField.getText().trim(), AllergyField.getText().trim(), HealthConditionField.getText().trim(), ParentField.getText().trim(), PhoneField.getText().trim());
        if (form.name().isEmpty() || form.gradeSection().isEmpty() || form.lrn().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Grade & Section, and LRN are required.", "Validation Error", JOptionPane.WARNING_MESSAGE); return null;
        }
        return form;
    }
    private void addStudent() {
        if (!requireHeadAdmin("manage student records")) return;
        StudentForm form = readStudentForm(); if (form == null) return;
        String sql = "INSERT INTO STUDENTS (lrn, name, grade_section, teacher, allergy, health_conditions, parent_name, phone_number, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bindStudent(statement, form); statement.executeUpdate();
            ActivityLogData.log("CREATE_STUDENT", "Created student record: " + form.name() + " (LRN: " + form.lrn() + ")", loggedInAccount.GetName());
            JOptionPane.showMessageDialog(this, "Student added successfully.", "Student Added", JOptionPane.INFORMATION_MESSAGE); clearStudentForm(); loadStudents();
        } catch (SQLException ex) { if ("23505".equals(ex.getSQLState())) JOptionPane.showMessageDialog(this, "A student with that LRN already exists.", "Duplicate LRN", JOptionPane.WARNING_MESSAGE); else showDatabaseError("adding the student", ex); }
    }
    private void updateStudent() {
        if (!requireHeadAdmin("manage student records")) return;
        if (selectedStudentLrn == null) { JOptionPane.showMessageDialog(this, "Select a student to update first.", "No Student Selected", JOptionPane.WARNING_MESSAGE); return; }
        StudentForm form = readStudentForm(); if (form == null) return;
        if (JOptionPane.showConfirmDialog(this, "Save changes for " + form.name() + "?", "Confirm Update", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        String sql = "UPDATE STUDENTS SET lrn=?, name=?, grade_section=?, teacher=?, allergy=?, health_conditions=?, parent_name=?, phone_number=? WHERE lrn=? AND status='ACTIVE'";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            StudentForm stored = loadActiveStudent(connection, selectedStudentLrn);
            if (stored == null) { JOptionPane.showMessageDialog(this, "The selected student is no longer active.", "Update Failed", JOptionPane.WARNING_MESSAGE); return; }
            if (stored.equals(form)) { JOptionPane.showMessageDialog(this, "No changes were made.", "Edit Cancelled", JOptionPane.INFORMATION_MESSAGE); return; }
            bindStudent(statement, form); statement.setString(9, selectedStudentLrn);
            if (statement.executeUpdate() == 0) { JOptionPane.showMessageDialog(this, "The selected student is no longer active.", "Update Failed", JOptionPane.WARNING_MESSAGE); return; }
            ActivityLogData.log("UPDATE_STUDENT", "Updated student record: " + form.name() + " (LRN: " + form.lrn() + ")", loggedInAccount.GetName());
            JOptionPane.showMessageDialog(this, "Student updated successfully.", "Student Updated", JOptionPane.INFORMATION_MESSAGE); clearStudentForm(); loadStudents();
        } catch (SQLException ex) { if ("23505".equals(ex.getSQLState())) JOptionPane.showMessageDialog(this, "A student with that LRN already exists.", "Duplicate LRN", JOptionPane.WARNING_MESSAGE); else showDatabaseError("updating the student", ex); }
    }
    private StudentForm loadActiveStudent(Connection connection, String lrn) throws SQLException {
        String sql = "SELECT name, grade_section, lrn, teacher, allergy, health_conditions, parent_name, phone_number FROM STUDENTS WHERE lrn=? AND status='ACTIVE'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lrn);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return new StudentForm(result.getString(1), result.getString(2), result.getString(3),
                        result.getString(4), result.getString(5), result.getString(6),
                        result.getString(7), result.getString(8));
            }
        }
    }
    private void archiveStudent() {
        if (!requireHeadAdmin("manage student records")) return;
        if (selectedStudentLrn == null) { JOptionPane.showMessageDialog(this, "Select a student to archive first.", "No Student Selected", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Archive the selected student?", "Confirm Archive", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE STUDENTS SET status='INACTIVE' WHERE lrn=?")) {
            statement.setString(1, selectedStudentLrn); statement.executeUpdate();
            ActivityLogData.log("ARCHIVE_STUDENT", "Archived student record (LRN: " + selectedStudentLrn + ")", loggedInAccount.GetName());
            JOptionPane.showMessageDialog(this, "Student archived successfully.", "Student Archived", JOptionPane.INFORMATION_MESSAGE); clearStudentForm(); loadStudents();
        } catch (SQLException ex) { showDatabaseError("archiving the student", ex); }
    }
    private void loadStudents() {
        DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel(); model.setRowCount(0);
        String sql = "SELECT name, grade_section, lrn, teacher, parent_name, phone_number, allergy, health_conditions FROM STUDENTS WHERE status='ACTIVE' ORDER BY name, lrn";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet results = statement.executeQuery()) {
            while (results.next()) model.addRow(new Object[]{results.getString(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5), results.getString(6), results.getString(7), results.getString(8)});
            filterStudents();
        } catch (SQLException ex) { showDatabaseError("loading students", ex); }
    }
    private void bindStudent(PreparedStatement statement, StudentForm form) throws SQLException {
        statement.setString(1, form.lrn()); statement.setString(2, form.name()); statement.setString(3, form.gradeSection()); statement.setString(4, form.teacher()); statement.setString(5, form.allergy()); statement.setString(6, form.healthConditions()); statement.setString(7, form.parentName()); statement.setString(8, form.phoneNumber());
    }
    private void clearStudentForm() {
        NameField.setText(""); GradeSectionField.setText(""); AdviserField.setText(""); LrnField.setText(""); AllergyField.setText(""); HealthConditionField.setText(""); ParentField.setText(""); PhoneField.setText(""); selectedStudentLrn = null; if (ReasonTable != null) ReasonTable.clearSelection();
    }
    private void populateStudentForm() {
        int viewRow = ReasonTable.getSelectedRow(); if (viewRow < 0) return;
        int row = ReasonTable.convertRowIndexToModel(viewRow); DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
        NameField.setText(value(model, row, 0)); GradeSectionField.setText(value(model, row, 1)); LrnField.setText(value(model, row, 2)); AdviserField.setText(value(model, row, 3)); ParentField.setText(value(model, row, 4)); PhoneField.setText(value(model, row, 5)); AllergyField.setText(value(model, row, 6)); HealthConditionField.setText(value(model, row, 7)); selectedStudentLrn = LrnField.getText().trim();
    }
    private String value(DefaultTableModel model, int row, int column) { Object value = model.getValueAt(row, column); return value == null ? "" : value.toString(); }
    private void importStudents() {
        if (!requireHeadAdmin("import Excel student data")) return;
        JFileChooser chooser = new JFileChooser(); chooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx, *.xls)", "xlsx", "xls"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        int imported = 0, duplicates = 0, invalid = 0;
        try (FileInputStream input = new FileInputStream(chooser.getSelectedFile()); Workbook workbook = WorkbookFactory.create(input)) {
            if (workbook.getNumberOfSheets() == 0) throw new IOException("The workbook has no worksheets.");
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            StudentImportColumns columns = validateStudentImportHeader(sheet.getRow(0), formatter);
            if (columns == null) {
                showInvalidStudentImportFile();
                return;
            }

            String insert = "INSERT INTO STUDENTS (lrn, name, grade_section, teacher, allergy, health_conditions, parent_name, phone_number, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
            try (Connection connection = DatabaseManager.getConnection();
                    PreparedStatement statement = connection.prepareStatement(insert);
                    PreparedStatement duplicateCheck = connection.prepareStatement("SELECT 1 FROM STUDENTS WHERE lrn = ?")) {
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    StudentForm form = rowToStudent(row, formatter, columns);
                    if (form == null) { invalid++; continue; }
                    duplicateCheck.setString(1, form.lrn());
                    try (ResultSet result = duplicateCheck.executeQuery()) {
                        if (result.next()) { duplicates++; continue; }
                    }
                    try { bindStudent(statement, form); statement.executeUpdate(); imported++; }
                    catch (SQLException ex) { if ("23505".equals(ex.getSQLState())) duplicates++; else throw ex; }
                }
            }
            ActivityLogData.log("IMPORT_STUDENTS", "Imported " + imported + " student record(s) from Excel; " + duplicates + " duplicate(s) skipped and " + invalid + " incomplete row(s) skipped.", loggedInAccount.GetName());
            JOptionPane.showMessageDialog(this, "Import complete.\nValid student records imported: " + imported + "\nSkipped duplicates: " + duplicates + "\nSkipped invalid records: " + invalid, "Import Complete", JOptionPane.INFORMATION_MESSAGE); loadStudents();
        } catch (POIXMLException | IOException ex) { JOptionPane.showMessageDialog(this, "Unable to read the Excel file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE); }
        catch (SQLException ex) { showDatabaseError("importing students", ex); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Unable to import the Excel file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE); }
    }
    private void showInvalidStudentImportFile() {
        JOptionPane.showMessageDialog(this,
                "Invalid Excel File Format!\nPlease upload a valid Student Information Excel file with LRN, Name, Grade & Section headers.",
                "Import Error - Invalid File", JOptionPane.ERROR_MESSAGE);
    }
    private StudentImportColumns validateStudentImportHeader(Row header, DataFormatter formatter) {
        if (header == null) return null;
        Map<String, Integer> indexes = new HashMap<>();
        boolean hasVisitLogHeader = false;
        for (int column = 0; column < header.getLastCellNum(); column++) {
            String value = cell(header, column, formatter).toLowerCase(java.util.Locale.ROOT);
            if (value.isEmpty()) continue;
            if (value.equals("medicine used") || value.equals("reason") || value.equals("status") || value.equals("medicine quantity")) hasVisitLogHeader = true;
            indexes.putIfAbsent(value, column);
        }
        if (hasVisitLogHeader) return null;
        int lrn = headerIndex(indexes, "lrn", "student lrn");
        int name = headerIndex(indexes, "name", "student name");
        int grade = headerIndex(indexes, "grade", "grade & section", "section");
        if (lrn < 0 || name < 0 || grade < 0) return null;
        return new StudentImportColumns(name, grade, lrn,
                headerIndex(indexes, "teacher", "adviser", "teacher/adviser", "teacher / adviser"),
                headerIndex(indexes, "allergy", "allergies"),
                headerIndex(indexes, "health condition", "health conditions"),
                headerIndex(indexes, "parent/guardian name", "parent name", "guardian name"),
                headerIndex(indexes, "phone number", "phone", "contact number"));
    }
    private int headerIndex(Map<String, Integer> indexes, String... names) { for (String name : names) { Integer index = indexes.get(name); if (index != null) return index; } return -1; }
    private StudentForm rowToStudent(Row row, DataFormatter formatter, StudentImportColumns columns) {
        if (row == null) return null;
        String name = cell(row, columns.name(), formatter), grade = cell(row, columns.grade(), formatter), lrn = cell(row, columns.lrn(), formatter);
        if (name.isEmpty() && grade.isEmpty() && lrn.isEmpty()) return null;
        // LRN is mandatory; incomplete student rows are intentionally skipped.
        if (lrn.isEmpty() || name.isEmpty() || grade.isEmpty()) return null;
        return new StudentForm(name, grade, lrn, cell(row, columns.teacher(), formatter), cell(row, columns.allergy(), formatter), cell(row, columns.healthConditions(), formatter), cell(row, columns.parentName(), formatter), cell(row, columns.phoneNumber(), formatter));
    }
    private String cell(Row row, int index, DataFormatter formatter) { if (row == null || index < 0) return ""; Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL); return cell == null ? "" : formatter.formatCellValue(cell).trim(); }
    private void showDatabaseError(String action, SQLException ex) { JOptionPane.showMessageDialog(this, "Error " + action + ": " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); }
    private record StudentImportColumns(int name, int grade, int lrn, int teacher, int allergy, int healthConditions, int parentName, int phoneNumber) { }
    private record DeleteResult(int deleted, int notFound) { }
    private record StudentForm(String name, String gradeSection, String lrn, String teacher, String allergy, String healthConditions, String parentName, String phoneNumber) { }
    public static WeeklyStats computeWeeklyStats(ArrayList<CheckinSystem> visits, LocalDate anyDayInWeek) {
        int weekly = 0, inClinic = 0, sentBack = 0, sentHome = 0, monday = 0, tuesday = 0, wednesday = 0, thursday = 0, friday = 0;
        Map<String, Integer> reasons = new HashMap<>(), medicines = new HashMap<>();
        LocalDate weekStart = anyDayInWeek.with(DayOfWeek.MONDAY), weekEnd = anyDayInWeek.with(DayOfWeek.FRIDAY);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        for (CheckinSystem visit : visits) {
            try { LocalDate date = LocalDateTime.parse(visit.getCheckInTime(), format).toLocalDate(); if (!date.isBefore(weekStart) && !date.isAfter(weekEnd)) { weekly++; switch (date.getDayOfWeek()) { case MONDAY -> monday++; case TUESDAY -> tuesday++; case WEDNESDAY -> wednesday++; case THURSDAY -> thursday++; case FRIDAY -> friday++; default -> { } } } } catch (Exception ignored) { }
            if ("In Clinic".equalsIgnoreCase(visit.getStatus())) inClinic++; if ("Sent Back".equalsIgnoreCase(visit.getStatus())) sentBack++; if ("Sent Home".equalsIgnoreCase(visit.getStatus())) sentHome++;
            if (visit.getReason() != null && !visit.getReason().isBlank()) reasons.merge(visit.getReason(), 1, Integer::sum);
            if (visit.getMedUsed() != null && !visit.getMedUsed().isBlank()) medicines.merge(visit.getMedUsed(), 1, Integer::sum);
        }
        String reason = reasons.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("N/A");
        String medicine = medicines.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No medicine used");
        return new WeeklyStats(weekly, inClinic, sentBack, sentHome, monday, tuesday, wednesday, thursday, friday, reason, medicine, weekStart, weekEnd);
    }
    public record WeeklyStats(int weeklyCheckins, int inClinic, int sentBack, int sentHome, int monday, int tuesday, int wednesday, int thursday, int friday, String topReason, String topMedicine, LocalDate mondayOfWeek, LocalDate fridayOfWeek) { }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        LRNLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        NameField = new javax.swing.JTextField();
        GradeSectionField = new javax.swing.JTextField();
        LrnField = new javax.swing.JTextField();
        AllergyField = new javax.swing.JTextField();
        HealthConditionField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        ParentField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        PhoneField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        AdviserField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        SearchField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        RestockBTN = new javax.swing.JButton();
        RestockBTN1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        AccManageBTN = new javax.swing.JButton();
        ExportBTN = new javax.swing.JButton();
        StudentManagementBTN = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InventoryLogs = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        AccountManagementPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ACTTable = new javax.swing.JTable();
        AccNameField = new javax.swing.JTextField();
        AccPasswordField = new javax.swing.JPasswordField();
        ConfirmPasswordField = new javax.swing.JPasswordField();
        AccountNameLabel = new javax.swing.JLabel();
        AccountPasswordLabel = new javax.swing.JLabel();
        ConfirmPasswordLabel = new javax.swing.JLabel();
        CAdminBTN = new javax.swing.JButton();
        CUserBTN = new javax.swing.JButton();
        AccDeleteBTN = new javax.swing.JButton();
        ResetPassword = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Grade & Section", "LRN", "Adviser", "Parent/Guardian Name", "Phone number", "Allergy", "Health Condition"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ReasonTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ReasonTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(ReasonTable);
        if (ReasonTable.getColumnModel().getColumnCount() > 0) {
            ReasonTable.getColumnModel().getColumn(0).setResizable(false);
            ReasonTable.getColumnModel().getColumn(1).setResizable(false);
            ReasonTable.getColumnModel().getColumn(2).setResizable(false);
            ReasonTable.getColumnModel().getColumn(3).setResizable(false);
            ReasonTable.getColumnModel().getColumn(4).setResizable(false);
            ReasonTable.getColumnModel().getColumn(5).setResizable(false);
            ReasonTable.getColumnModel().getColumn(6).setResizable(false);
            ReasonTable.getColumnModel().getColumn(7).setResizable(false);
        }

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Name:");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Grade and Section");

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        LRNLabel.setForeground(new java.awt.Color(255, 255, 255));
        LRNLabel.setText("LRN:");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Allergy:");

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Health Condition:");

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Parent/Gurdian Name");

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Phone Number");

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Teacher/Adviser");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel29)
                    .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(GradeSectionField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LrnField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AllergyField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HealthConditionField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(ParentField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(PhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(AdviserField, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GradeSectionField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AdviserField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LrnField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(1, 1, 1)
                .addComponent(AllergyField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HealthConditionField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ParentField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jButton2.setText("Edit");

        jButton3.setText("Import Excel");

        jButton4.setText("Clear");

        jButton6.setText("Delete");

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Search:");

        jButton8.setText("Add Student");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(407, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 634, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(374, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton6)
                    .addComponent(jButton8))
                .addGap(34, 34, 34))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(60, 60, 60)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(83, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1020, 620));

        RestockBTN.setText("Restock");
        RestockBTN.addActionListener(this::RestockBTNActionPerformed);
        jPanel1.add(RestockBTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 590, 110, 50));

        RestockBTN1.setText("Edit");
        RestockBTN1.addActionListener(this::RestockBTN1ActionPerformed);
        jPanel1.add(RestockBTN1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 590, 100, 50));

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 30));

        jPanel3.setBackground(new java.awt.Color(15, 23, 42));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jButton5.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Inventory");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton7.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Return");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        AccManageBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        AccManageBTN.setForeground(new java.awt.Color(255, 255, 255));
        AccManageBTN.setText("Account Management");

        ExportBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        ExportBTN.setForeground(new java.awt.Color(255, 255, 255));
        ExportBTN.setText("Import Report");
        ExportBTN.addActionListener(this::ExportBTNActionPerformed);

        StudentManagementBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        StudentManagementBTN.setForeground(new java.awt.Color(255, 255, 255));
        StudentManagementBTN.setText("Student Mangement");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ExportBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AccManageBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(StudentManagementBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StudentManagementBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(AccManageBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ExportBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 180, 620));

        stockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Status", "Product", "Quantity", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stockTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(stockTable);
        if (stockTable.getColumnModel().getColumnCount() > 0) {
            stockTable.getColumnModel().getColumn(0).setResizable(false);
            stockTable.getColumnModel().getColumn(1).setResizable(false);
            stockTable.getColumnModel().getColumn(2).setResizable(false);
            stockTable.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 650, 470));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel1.setText("Inventory Logs");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 70, 230, 40));

        InventoryLogs.setEditable(false);
        InventoryLogs.setColumns(20);
        InventoryLogs.setRows(5);
        jScrollPane2.setViewportView(InventoryLogs);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 110, 320, 470));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel6.setText("Stock");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 210, 40));

        AccountManagementPanel.setBackground(new java.awt.Color(255, 255, 255));

        ACTTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Role"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(ACTTable);
        if (ACTTable.getColumnModel().getColumnCount() > 0) {
            ACTTable.getColumnModel().getColumn(0).setResizable(false);
            ACTTable.getColumnModel().getColumn(1).setResizable(false);
        }

        AccNameField.addActionListener(this::AccNameFieldActionPerformed);

        AccPasswordField.addActionListener(this::AccPasswordFieldActionPerformed);

        ConfirmPasswordField.addActionListener(this::ConfirmPasswordFieldActionPerformed);

        AccountNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountNameLabel.setText("Account Name");

        AccountPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountPasswordLabel.setText("Account Password");

        ConfirmPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        ConfirmPasswordLabel.setText("Confirm Password");

        CAdminBTN.setBackground(new java.awt.Color(0, 102, 204));
        CAdminBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        CAdminBTN.setForeground(new java.awt.Color(255, 255, 255));
        CAdminBTN.setText("Create Admin");
        CAdminBTN.addActionListener(this::CAdminBTNActionPerformed);

        CUserBTN.setBackground(new java.awt.Color(0, 102, 204));
        CUserBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        CUserBTN.setForeground(new java.awt.Color(255, 255, 255));
        CUserBTN.setText("Create User");
        CUserBTN.addActionListener(this::CUserBTNActionPerformed);

        AccDeleteBTN.setBackground(new java.awt.Color(0, 102, 204));
        AccDeleteBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        AccDeleteBTN.setForeground(new java.awt.Color(255, 255, 255));
        AccDeleteBTN.setText("Delete");
        AccDeleteBTN.addActionListener(this::AccDeleteBTNActionPerformed);

        ResetPassword.setBackground(new java.awt.Color(0, 102, 204));
        ResetPassword.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        ResetPassword.setForeground(new java.awt.Color(255, 255, 255));
        ResetPassword.setText("Reset Password");
        ResetPassword.addActionListener(this::ResetPasswordActionPerformed);

        javax.swing.GroupLayout AccountManagementPanelLayout = new javax.swing.GroupLayout(AccountManagementPanel);
        AccountManagementPanel.setLayout(AccountManagementPanelLayout);
        AccountManagementPanelLayout.setHorizontalGroup(
            AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                .addContainerGap(149, Short.MAX_VALUE)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AccNameField)
                    .addComponent(AccPasswordField)
                    .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(AccountNameLabel)
                    .addComponent(AccountPasswordLabel)
                    .addComponent(ConfirmPasswordLabel)
                    .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                        .addComponent(CUserBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CAdminBTN)))
                .addGap(109, 109, 109)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ResetPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))))
        );
        AccountManagementPanelLayout.setVerticalGroup(
            AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(AccountNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccountPasswordLabel)
                .addGap(4, 4, 4)
                .addComponent(AccPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConfirmPasswordLabel)
                .addGap(4, 4, 4)
                .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CUserBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CAdminBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResetPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );

        jPanel1.add(AccountManagementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1020, 620));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        SessionManager.saveSession(loggedInAccount);
        new Dashboard(loggedInAccount).setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void RestockBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestockBTNActionPerformed
        restockSelectedMedicine();
    }//GEN-LAST:event_RestockBTNActionPerformed

    private void stockTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockTableMouseClicked
    }//GEN-LAST:event_stockTableMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        showInventory();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void AccDeleteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccDeleteBTNActionPerformed
                int[] selectedRows = ACTTable.getSelectedRows();

        // Nothing selected
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an account to delete.",
                "No Account Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Build list of selected account names
        StringBuilder accountList = new StringBuilder();

        for (int row : selectedRows) {
            String name = ACTTable.getValueAt(row, 0).toString();

            accountList.append("- ")
                       .append(name)
                       .append("\n");
        }

        // Confirmation warning
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the following account(s)?\n\n"
            + accountList,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        // User clicked No
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            // Delete each selected account
            for (int row : selectedRows) {

                String name = ACTTable.getValueAt(row, 0).toString();

               accountService.deleteAccount(loggedInAccount, name);
            }

            JOptionPane.showMessageDialog(
                this,
                "Selected account(s) deleted successfully.",
                "Delete Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Refresh the table
            refreshAccountTable();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error deleting account: " + ex.getMessage(),
                "Delete Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_AccDeleteBTNActionPerformed

    private void CAdminBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CAdminBTNActionPerformed
          createAccountFromForm("ADMIN");
    }//GEN-LAST:event_CAdminBTNActionPerformed

    private void CUserBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CUserBTNActionPerformed
          createAccountFromForm("USER");
    }//GEN-LAST:event_CUserBTNActionPerformed
        
    private final AccountData accountService = new AccountData();
            private void createAccountFromForm(String role) {

            // =========================
            // BASIC ACCESS CHECK
            // =========================

            if (loggedInAccount == null
                    || !loggedInAccount.isAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to create accounts.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // ADMIN CREATION
            // =========================

            if ("ADMIN".equalsIgnoreCase(role)
                    && !loggedInAccount.isHeadAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Only the Head Admin can create an Admin account.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // USER CREATION
            // =========================

            if ("USER".equalsIgnoreCase(role)
                    && !loggedInAccount.isAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to create a User account.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // READ FORM
            // =========================

            String name =
                AccNameField.getText().trim();

            char[] pw1 =
                AccPasswordField.getPassword();

            char[] pw2 =
                ConfirmPasswordField.getPassword();

            String password =
                new String(pw1);

            String confirmPassword =
                new String(pw2);

            // =========================
            // VALIDATION
            // =========================

            if (name.isEmpty()
                    || password.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Name and password are required."
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
                );

                return;
            }

            // =========================
            // CREATE ACCOUNT
            // =========================

            try {

                if (accountService.nameExists(name)) {

                    JOptionPane.showMessageDialog(
                        this,
                        "An account with this name already exists."
                    );

                    return;
                }

                accountService.createAccount(
                    loggedInAccount,
                    name,
                    password,
                    role
                );

                JOptionPane.showMessageDialog(
                    this,
                    role + " account created for " + name + "."
                );

                AccNameField.setText("");
                AccPasswordField.setText("");
                ConfirmPasswordField.setText("");

                refreshAccountTable();

            } catch (SecurityException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error creating account: "
                        + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
            
private void refreshAccountTable() {
    DatabaseExecutor.run(
            () -> accountService.loadAll(),
            accounts -> {
                DefaultTableModel model = (DefaultTableModel) ACTTable.getModel();
                model.setRowCount(0);

                for (AccountSystem a : accounts) {
                    model.addRow(new Object[]{a.GetName(), a.getRole()});
                }
            },
            ex -> JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage())
    );
}
    
    private void AccPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccPasswordFieldActionPerformed
   
     /** Small holder so the background export task can report both the saved
     *  file and whether the audit-log write succeeded, in one callback. */
    private record ExportOutcome(java.io.File file, boolean auditLogged) {}
  
    
    private void ExportBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportBTNActionPerformed
        if (!requireHeadAdmin("export reports")) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save activity and clinic report");
        chooser.setSelectedFile(new java.io.File("clinic-report-"
                + LocalDate.now() + ".xlsx"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel workbook (*.xlsx)", "xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File destination = chooser.getSelectedFile();
        if (!destination.getName().toLowerCase().endsWith(".xlsx")) {
            destination = new java.io.File(destination.getAbsolutePath() + ".xlsx");
        }
        final java.io.File reportFile = destination;
        ExportBTN.setEnabled(false);
        DatabaseExecutor.run(() -> {
            ReportExporter exporter = new ReportExporter(new MedicineData());
            boolean auditLogged = exporter.writeDailyReport(LocalDate.now(), reportFile,
                    loggedInAccount.GetName());
            return new ExportOutcome(reportFile, auditLogged);
        }, outcome -> {
            ExportBTN.setEnabled(true);
            String message = "Report exported to:\n" + outcome.file().getAbsolutePath();
            if (!outcome.auditLogged()) {
                message += "\n\nThe report was saved, but its export audit entry could not be recorded.";
            }
            JOptionPane.showMessageDialog(this, message, "Export Complete",
                    outcome.auditLogged() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        }, ex -> {
            ExportBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to export the report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        });
    }//GEN-LAST:event_ExportBTNActionPerformed

    private void AccNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccNameFieldActionPerformed

    private void ConfirmPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConfirmPasswordFieldActionPerformed

    private void ResetPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetPasswordActionPerformed
       
        int viewRow = ACTTable.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an account to reset the password for.",
                "No Account Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String targetName = ACTTable.getValueAt(viewRow, 0).toString();

        if (loggedInAccount == null || !loggedInAccount.isHeadAdmin()) {
            JOptionPane.showMessageDialog(
                this,
                "Only Head Admin accounts can reset passwords.",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (loggedInAccount.GetName().equalsIgnoreCase(targetName)) {
            JOptionPane.showMessageDialog(
                this,
                "You cannot reset your own password using this function.",
                "Password Reset Not Available",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the password\nfor account \"" + targetName + "\"?",
            "Reset Password",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        char[] newPassword = null;
        char[] confirmPassword = null;

        try {
            while (true) {

                javax.swing.JPasswordField newPasswordField = new javax.swing.JPasswordField();
                int newResult = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Enter a new password:", newPasswordField},
                    "Set New Password",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (newResult != JOptionPane.OK_OPTION) {
                    return;
                }

                newPassword = newPasswordField.getPassword();

                if (newPassword.length == 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "The new password cannot be empty.",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                javax.swing.JPasswordField confirmPasswordField = new javax.swing.JPasswordField();
                int confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Re-enter the new password:", confirmPasswordField},
                    "Confirm New Password",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (confirmResult != JOptionPane.OK_OPTION) {
                    return;
                }

                confirmPassword = confirmPasswordField.getPassword();

                if (confirmPassword.length == 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please confirm the new password.",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                if (!java.util.Arrays.equals(newPassword, confirmPassword)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "The passwords do not match.\nPlease try again.",
                        "Password Mismatch",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                break;
            }

            boolean success = accountService.resetPassword(
                loggedInAccount, targetName, newPassword
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "The password for account \"" + targetName + "\" has been\nsuccessfully updated.",
                    "Password Updated",
                    JOptionPane.INFORMATION_MESSAGE
                );
                refreshAccountTable();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to update the password.\nPlease try again.",
                    "Password Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Access Denied",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to update the password.\nPlease try again.",
                "Password Update Failed",
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            if (newPassword != null) java.util.Arrays.fill(newPassword, ' ');
            if (confirmPassword != null) java.util.Arrays.fill(confirmPassword, ' ');
        }
    }//GEN-LAST:event_ResetPasswordActionPerformed

    private void RestockBTN1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestockBTN1ActionPerformed
        editSelectedMedicine();
    }//GEN-LAST:event_RestockBTN1ActionPerformed

    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReasonTableMouseClicked
        populateStudentForm();
    }//GEN-LAST:event_ReasonTableMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new AdminPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ACTTable;
    private javax.swing.JButton AccDeleteBTN;
    private javax.swing.JButton AccManageBTN;
    private javax.swing.JTextField AccNameField;
    private javax.swing.JPasswordField AccPasswordField;
    private javax.swing.JPanel AccountManagementPanel;
    private javax.swing.JLabel AccountNameLabel;
    private javax.swing.JLabel AccountPasswordLabel;
    private javax.swing.JTextField AdviserField;
    private javax.swing.JTextField AllergyField;
    private javax.swing.JButton CAdminBTN;
    private javax.swing.JButton CUserBTN;
    private javax.swing.JPasswordField ConfirmPasswordField;
    private javax.swing.JLabel ConfirmPasswordLabel;
    private javax.swing.JButton ExportBTN;
    private javax.swing.JTextField GradeSectionField;
    private javax.swing.JTextField HealthConditionField;
    private javax.swing.JTextArea InventoryLogs;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JTextField LrnField;
    private javax.swing.JTextField NameField;
    private javax.swing.JTextField ParentField;
    private javax.swing.JTextField PhoneField;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JButton ResetPassword;
    private javax.swing.JButton RestockBTN;
    private javax.swing.JButton RestockBTN1;
    private javax.swing.JTextField SearchField;
    private javax.swing.JButton StudentManagementBTN;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable stockTable;
    // End of variables declaration//GEN-END:variables
}
