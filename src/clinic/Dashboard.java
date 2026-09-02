package clinic;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Main dashboard shell. Legacy controller code that referenced controls removed
 * from the NetBeans form has been removed; the generated form remains intact.
 */
public class Dashboard extends javax.swing.JFrame {
    private final AccountSystem loggedInAccount;
    private javax.swing.JPanel currentPanel;
    private StudentDetails selectedStudent;
    private int studentSearchGeneration;
    private final javax.swing.JButton CancelCheckinBTN = new javax.swing.JButton("Cancel");

    public Dashboard(AccountSystem account) {
        loggedInAccount = account;
        FlatLightLaf.setup();
        initComponents();
        UserSession.start(account);
        configureNavigation();
        configureClinicWorkflow();
        installSessionPersistence();
        setIconImage(AppIcon.getIcon());
        setLocationRelativeTo(null);
    }

    public Dashboard() {
        this(null);
    }

    /** Refreshes the remembered session without storing a password. */
    private void installSessionPersistence() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                SessionManager.saveSession(loggedInAccount);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent event) {
                SessionManager.saveSession(loggedInAccount);
            }
        });
    }

    /** Keeps the generated AbsoluteLayout untouched while ensuring that only
     * one of its overlapping content panels can ever be painted. */
    private void configureNavigation() {
        HomeBTN.addActionListener(e -> showPanel(MainPanel));
        CheckinBTN.addActionListener(e -> showPanel(CheckInPanel1));
        StatisticBTN.addActionListener(e -> showPanel(StatisticPanel));

        for (Component panel : new Component[]{MainPanel, CheckInPanel1,
                CheckInPopup, StatisticPanel}) {
            panel.setVisible(false);
        }
        showPanel(MainPanel);
    }

    private void showPanel(javax.swing.JPanel target) {
        if (target == null || currentPanel == target) {
            return;
        }

        // Swing actions run on the EDT. Hiding every peer first makes rapid
        // clicks idempotent and prevents absolute-layout panels from stacking.
        for (javax.swing.JPanel panel : new javax.swing.JPanel[]{MainPanel,
                CheckInPanel1, CheckInPopup, StatisticPanel}) {
            panel.setVisible(panel == target);
        }
        currentPanel = target;
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private boolean requireAdminPanelAccess() {
        if (loggedInAccount != null && loggedInAccount.canAccessAdminPanel()) {
            return true;
        }
        JOptionPane.showMessageDialog(this,
                "Inventory and management are available to Admin accounts only.",
                "Access Denied", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    /** Wires the existing Dashboard controls to the existing visit/student DAOs. */
    private void configureClinicWorkflow() {
        jButton2.addActionListener(e -> findExpressStudent());
        LrnField.addActionListener(e -> findExpressStudent());
        ECheckin.addActionListener(e -> submitExpressCheckin());
        jButton1.addActionListener(e -> submitPopupCheckin());
        ReasonTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ReasonTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectStudentFromChooser();
        });
        SearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
        });
        ReasonField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
            @Override public void removeUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
            @Override public void changedUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
        });
        configureCheckinPopupCard();
        loadStudentChooser("");
        loadMedicineChoices();
        refreshRecentVisits();
    }

    /** Replaces the generated fixed-width popup content with a readable card. */
    private void configureCheckinPopupCard() {
        CheckInPopup.removeAll();
        CheckInPopup.setBackground(new java.awt.Color(255, 255, 255));
        CheckInPopup.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(203, 213, 225)),
                javax.swing.BorderFactory.createEmptyBorder(18, 22, 18, 22)));
        CheckInPopup.setLayout(new java.awt.GridBagLayout());

        StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 19));
        StudentCheckinLabel.setForeground(new java.awt.Color(15, 23, 42));
        StudentCheckinLabel.setText("Student Check-in");
        for (javax.swing.JLabel label : new javax.swing.JLabel[]{jLabel7, jLabel8,
                LRNLabel, jLabel9, jLabel29, jLabel11}) {
            label.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 13));
            label.setForeground(new java.awt.Color(51, 65, 85));
        }
        jLabel10.setText("Reason");
        jLabel12.setText("Medicine");
        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 12));
        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 12));
        jButton1.setText("Complete Check-in");
        CancelCheckinBTN.setText("Cancel");
        CancelCheckinBTN.addActionListener(e -> closeCheckinPopup());

        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL; c.weightx = 1; c.insets = new java.awt.Insets(0, 0, 12, 0);
        CheckInPopup.add(StudentCheckinLabel, c);
        int row = 1;
        for (javax.swing.JLabel label : new javax.swing.JLabel[]{jLabel7, jLabel8,
                LRNLabel, jLabel9, jLabel29, jLabel11}) {
            c.gridy = row++; c.insets = new java.awt.Insets(2, 0, 2, 0);
            CheckInPopup.add(label, c);
        }
        c.gridwidth = 1; c.weightx = 0; c.gridy = row; c.gridx = 0;
        c.insets = new java.awt.Insets(12, 0, 4, 10); CheckInPopup.add(jLabel10, c);
        c.gridx = 1; c.weightx = 1; c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.insets = new java.awt.Insets(12, 0, 4, 0); CheckInPopup.add(jTextField1, c);
        c.gridy = ++row; c.gridx = 0; c.weightx = 0; c.insets = new java.awt.Insets(4, 0, 10, 10);
        CheckInPopup.add(jLabel12, c);
        c.gridx = 1; c.weightx = 1; c.insets = new java.awt.Insets(4, 0, 10, 0);
        CheckInPopup.add(jComboBox1, c);
        c.gridy = ++row; c.gridx = 0; c.gridwidth = 1; c.weightx = 0.5;
        c.insets = new java.awt.Insets(4, 0, 0, 5); CheckInPopup.add(CancelCheckinBTN, c);
        c.gridx = 1; c.weightx = 0.5; c.insets = new java.awt.Insets(4, 5, 0, 0);
        CheckInPopup.add(jButton1, c);

        // The generated AbsoluteLayout constraint gives this dialog too
        // little space for full student details; replace it at runtime only.
        getContentPane().add(CheckInPopup,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 190, 520, 445));
    }

    /** Positions the panel in the center of the check-in workspace. */
    private void centerCheckinPopup() {
        int width = 520, height = 445;
        int panelWidth = CheckInPanel1.getWidth() > 0 ? CheckInPanel1.getWidth()
                : getContentPane().getWidth();
        int panelHeight = CheckInPanel1.getHeight() > 0 ? CheckInPanel1.getHeight()
                : getContentPane().getHeight();
        int panelX = CheckInPanel1.getWidth() > 0 ? CheckInPanel1.getX() : 0;
        int panelY = CheckInPanel1.getHeight() > 0 ? CheckInPanel1.getY() : 0;
        int x = panelX + Math.max(0, (panelWidth - width) / 2);
        int y = panelY + Math.max(0, (panelHeight - height) / 2);
        getContentPane().add(CheckInPopup,
                new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width, height));
    }

    private void closeCheckinPopup() {
        CheckInPopup.setVisible(false);
        jTextField1.setText("");
        if (jComboBox1.getItemCount() > 0) jComboBox1.setSelectedIndex(0);
        selectedStudent = null;
        ReasonTable.clearSelection();
        CheckInPopup.revalidate();
        CheckInPopup.repaint();
    }

    /** Suggests an in-stock medicine for a recognized express-check-in symptom. */
    private void recommendMedicineFromReason() {
        if (!MedicineField.getText().trim().isEmpty()) return;
        final String reason = ReasonField.getText();
        final String recommendation = recommendationFor(reason);
        if (recommendation == null) return;

        DatabaseExecutor.run(() -> new MedicineData().loadAll(), medicines -> {
            // Do not overwrite a nurse's manual entry or a newer reason.
            if (!MedicineField.getText().trim().isEmpty()
                    || !reason.equals(ReasonField.getText())) return;
            String suggested = inStockRecommendation(recommendation, medicines);
            MedicineField.setText(suggested);
        }, ex -> {
            // A failed stock lookup must not interrupt check-in or erase a
            // manual value. The generic recommendation remains optional.
        });
    }

    private static String recommendationFor(String reason) {
        String symptom = reason == null ? "" : reason.toLowerCase(Locale.ROOT);
        if (containsAny(symptom, "fever", "headache", "body pain", "temp"))
            return "Paracetamol / Biogesic";
        if (containsAny(symptom, "cough", "colds", "flu", "runny nose"))
            return "Solmux / Neozep / Decolgen";
        if (containsAny(symptom, "stomach ache", "hyperacidity", "hyperacid", "indigestion"))
            return "Kremil-S / Antacid";
        if (containsAny(symptom, "allergy", "itch", "rashes")) return "Cetirizine";
        if (containsAny(symptom, "wound", "cut", "scratch")) return "Betadine / Bandage";
        if (containsAny(symptom, "dizziness", "fatigue"))
            return "Rest / Oral Rehydration Solution";
        return null;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) if (text.contains(keyword)) return true;
        return false;
    }

    private static String inStockRecommendation(String recommendation,
            java.util.List<Medicine> medicines) {
        String[] options = recommendation.split(" / ");
        for (String option : options) {
            for (Medicine medicine : medicines) {
                if (medicine.getname().equalsIgnoreCase(option)
                        && medicine.getquantity() > 0 && !medicine.isExpired()) {
                    return medicine.getname();
                }
            }
        }
        return recommendation + " (Out of Stock)";
    }

    private void loadMedicineChoices() {
        DatabaseExecutor.run(() -> new MedicineData().loadAll(), medicines -> {
            javax.swing.DefaultComboBoxModel<String> model =
                    new javax.swing.DefaultComboBoxModel<>();
            model.addElement("None");
            for (Medicine medicine : medicines) model.addElement(medicine.getname());
            jComboBox1.setModel(model);
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load medicines: " + ex.getMessage(), "Medicine Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void selectStudentFromChooser() {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) return;
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        String lrn = String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
        findStudent(lrn, student -> {
            if (!lrn.equals(currentSelectedChooserLrn())) return;
            selectedStudent = student;
            if (student != null) populatePopupStudent(student);
        });
    }

    private String currentSelectedChooserLrn() {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) return null;
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        return String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
    }

    private void openCheckinForSelection() {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        String lrn = String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
        findStudent(lrn, student -> {
            if (!lrn.equals(currentSelectedChooserLrn())) return;
            if (student == null) {
                JOptionPane.showMessageDialog(this, "The selected student is no longer active.",
                        "Student Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedStudent = student;
            populatePopupStudent(student);
            centerCheckinPopup();
            CheckInPopup.setVisible(true);
            CheckInPopup.revalidate();
            CheckInPopup.repaint();
        });
    }

    private void populatePopupStudent(StudentDetails student) {
        jLabel7.setText(detail("Name", student.name()));
        jLabel8.setText(detail("Grade & Section", student.gradeSection()));
        LRNLabel.setText(detail("LRN", student.lrn()));
        jLabel9.setText(detail("Allergies", blankAsNone(student.allergy())));
        jLabel29.setText(detail("Health Conditions", blankAsNone(student.healthConditions())));
        jLabel11.setText(detail("Guardian / Contact", blankAsNone(student.parentName())
                + " · " + blankAsNone(student.phoneNumber())));
    }

    private static String detail(String label, String value) {
        return "<html><b>" + escapeHtml(label) + ":</b> " + escapeHtml(value) + "</html>";
    }

    private static String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String blankAsNone(String value) {
        return value == null || value.isBlank() ? "None" : value;
    }

    private void findExpressStudent() {
        String lrn = LrnField.getText().trim();
        if (lrn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a student LRN first.",
                    "LRN Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        findStudent(lrn, student -> {
            if (student == null) {
                selectedStudent = null;
                jLabel15.setText("Name:");
                JOptionPane.showMessageDialog(this, "No active student was found for that LRN.",
                        "Student Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedStudent = student;
            jLabel15.setText("Name: " + student.name());
        });
    }

    private void submitExpressCheckin() {
        if (selectedStudent == null || !selectedStudent.lrn().equals(LrnField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Search and select a valid student before checking in.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        submitCheckin(selectedStudent, ReasonField.getText().trim(), MedicineField.getText().trim());
    }

    private void submitPopupCheckin() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Select a student before proceeding.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object item = jComboBox1.getSelectedItem();
        String medicine = item == null ? "None" : item.toString();
        submitCheckin(selectedStudent, jTextField1.getText().trim(), medicine);
    }

    private void submitCheckin(StudentDetails student, String reason, String medicine) {
        if (reason == null || reason.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a reason for the clinic visit.",
                    "Reason Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String medicineName = medicine == null || medicine.isBlank() ? "None" : medicine;
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        ECheckin.setEnabled(false);
        jButton1.setEnabled(false);
        DatabaseExecutor.run(() -> new VisitData().checkInWithMedicine(
                student.name(), student.gradeSection(), student.lrn(), reason, medicineName,
                "None".equalsIgnoreCase(medicineName) ? 0 : 1,
                student.parentName(), student.phoneNumber(), new MedicineData(), actor), result -> {
            ECheckin.setEnabled(true);
            jButton1.setEnabled(true);
            refreshRecentVisits();
            closeCheckinPopup();
            ReasonField.setText("");
            MedicineField.setText("");
            JOptionPane.showMessageDialog(this,
                    result.medicineDeducted() || "None".equalsIgnoreCase(medicineName)
                            ? "Student checked in successfully."
                            : "Student checked in, but the selected medicine was unavailable.",
                    "Check-in Complete", JOptionPane.INFORMATION_MESSAGE);
        }, ex -> {
            ECheckin.setEnabled(true);
            jButton1.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to save the check-in: " + ex.getMessage(),
                    "Check-in Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void loadStudentChooser(String search) {
        final String requestedSearch = search == null ? "" : search.trim();
        final int requestId = ++studentSearchGeneration;
        DatabaseExecutor.run(() -> findStudents(requestedSearch), students -> {
            if (requestId != studentSearchGeneration
                    || !requestedSearch.equals(SearchField.getText().trim())) return;
            DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
            model.setRowCount(0);
            for (StudentDetails student : students) {
                model.addRow(new Object[]{student.name(), student.gradeSection(), student.lrn(),
                        student.parentName(), student.phoneNumber()});
            }
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load students: " + ex.getMessage(), "Student Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void refreshRecentVisits() {
        DatabaseExecutor.run(() -> new VisitData().loadActive(), visits -> {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            for (CheckinSystem visit : visits) {
                model.addRow(new Object[]{visit.getCheckInTime(), visit.getName(),
                        visit.getGradeSection(), "", visit.getReason(), visit.getMedUsed(),
                        visit.getStatus()});
            }
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load recent clinic visits: " + ex.getMessage(), "Visit Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void findStudent(String lrn, java.util.function.Consumer<StudentDetails> onSuccess) {
        DatabaseExecutor.run(() -> findStudentByLrn(lrn), onSuccess, ex ->
                JOptionPane.showMessageDialog(this, "Unable to find student: " + ex.getMessage(),
                        "Student Search Error", JOptionPane.ERROR_MESSAGE));
    }

    private static StudentDetails findStudentByLrn(String lrn) throws java.sql.SQLException {
        String sql = "SELECT name, grade_section, lrn, allergy, health_conditions, parent_name, phone_number "
                + "FROM STUDENTS WHERE lrn = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? studentFromResult(rs) : null;
            }
        }
    }

    private static java.util.ArrayList<StudentDetails> findStudents(String search) throws java.sql.SQLException {
        java.util.ArrayList<StudentDetails> students = new java.util.ArrayList<>();
        String sql = "SELECT name, grade_section, lrn, allergy, health_conditions, parent_name, phone_number "
                + "FROM STUDENTS WHERE status = 'ACTIVE' AND (UPPER(name) LIKE ? OR UPPER(grade_section) LIKE ? OR UPPER(lrn) LIKE ?) "
                + "ORDER BY name, lrn";
        String filter = "%" + (search == null ? "" : search.trim().toUpperCase()) + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filter);
            ps.setString(2, filter);
            ps.setString(3, filter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(studentFromResult(rs));
            }
        }
        return students;
    }

    private static StudentDetails studentFromResult(ResultSet rs) throws java.sql.SQLException {
        return new StudentDetails(rs.getString("name"), rs.getString("grade_section"),
                rs.getString("lrn"), rs.getString("allergy"), rs.getString("health_conditions"),
                rs.getString("parent_name"), rs.getString("phone_number"));
    }

    private record StudentDetails(String name, String gradeSection, String lrn, String allergy,
            String healthConditions, String parentName, String phoneNumber) {
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do 
NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SidePanel = new javax.swing.JPanel();
        HomeBTN = new javax.swing.JButton();
        CheckinBTN = new javax.swing.JButton();
        StatisticBTN = new javax.swing.JButton();
        InventoryBTN = new javax.swing.JButton();
        Logout = new javax.swing.JButton();
        ThemeToggle = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        ReasonField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        LrnField = new javax.swing.JTextField();
        ECheckin = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        MedicineField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        TempField = new javax.swing.JTextField();
        NameField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        DateTimeLabel = new javax.swing.JLabel();
        CheckInPopup = new javax.swing.JPanel();
        StudentCheckinLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        LRNLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        CheckInPanel1 = new javax.swing.JPanel();
        CheckInBTN = new javax.swing.JButton();
        SentHomeBTN = new javax.swing.JButton();
        SearchField = new javax.swing.JTextField();
        SearchLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        StatisticPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SidePanel.setBackground(new java.awt.Color(51, 153, 255));

        HomeBTN.setText("Home");

        CheckinBTN.setText("Check in");

        StatisticBTN.setText("Statistic");
        StatisticBTN.addActionListener(this::StatisticBTNActionPerformed);

        InventoryBTN.setText("Inventory and Management");
        InventoryBTN.addActionListener(this::InventoryBTNActionPerformed);

        Logout.setBackground(new java.awt.Color(255, 255, 255));
        Logout.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        Logout.setForeground(new java.awt.Color(0, 0, 0));
        Logout.setText("Logout");
        Logout.addActionListener(this::LogoutActionPerformed);

        ThemeToggle.setBackground(new java.awt.Color(255, 255, 255));
        ThemeToggle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThemeToggle.setForeground(new java.awt.Color(0, 0, 0));
        ThemeToggle.setText("Mode");
        ThemeToggle.addActionListener(this::ThemeToggleActionPerformed);

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Clinic Dashboard");

        javax.swing.GroupLayout SidePanelLayout = new javax.swing.GroupLayout(SidePanel);
        SidePanel.setLayout(SidePanelLayout);
        SidePanelLayout.setHorizontalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CheckinBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addComponent(StatisticBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addComponent(InventoryBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidePanelLayout.createSequentialGroup()
                        .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel3)
                .addGap(138, 138, 138)
                .addComponent(HomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(CheckinBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StatisticBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InventoryBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(SidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -3, 270, 840));

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Name", "Section", "Temp", "Symptoms", "Medicine", "Status"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
        );

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("SYMPTOM DISTRIBUTION (THIS WEEK)");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(jLabel13)
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(151, 151, 151))
        );

        jPanel8.setBackground(new java.awt.Color(248, 247, 247));

        jButton2.setText("Search");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Student LRN:");

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Name:");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Reason:");

        ECheckin.setText("Check in");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Medicine:");

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Temperature:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ReasonField, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                    .addComponent(MedicineField)
                                    .addComponent(TempField)
                                    .addComponent(NameField, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(LrnField)))
                            .addComponent(jLabel18))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(ECheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jButton2)))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(LrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(ReasonField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(TempField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(MedicineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ECheckin))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Express Check in");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Recent Clinic Visit and Action");

        jLabel2.setText("High Temps");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(74, 74, 74))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel5.setText("Low Stock");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel5)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jLabel1.setText("Active Visits");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel4.setText("Total Today");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jLabel4)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        DateTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        DateTimeLabel.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel20))
                    .addComponent(jLabel19)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(22, 22, 22)
                        .addComponent(jLabel20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        getContentPane().add(MainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, -4, 1490, 840));

        CheckInPopup.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPopup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        StudentCheckinLabel.setForeground(new java.awt.Color(0, 0, 0));
        StudentCheckinLabel.setText("Student Check-in");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Name:");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Grade/Section:");

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        LRNLabel.setForeground(new java.awt.Color(0, 0, 0));
        LRNLabel.setText("LRN:");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Allergy:");

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Reason");

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Medicine");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(0, 153, 204));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Proceed");

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Status:");

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setText("Health Condition:");

        javax.swing.GroupLayout CheckInPopupLayout = new javax.swing.GroupLayout(CheckInPopup);
        CheckInPopup.setLayout(CheckInPopupLayout);
        CheckInPopupLayout.setHorizontalGroup(
            CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPopupLayout.createSequentialGroup()
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CheckInPopupLayout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(StudentCheckinLabel))
                    .addGroup(CheckInPopupLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CheckInPopupLayout.createSequentialGroup()
                                .addGap(127, 127, 127)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7)
                            .addGroup(CheckInPopupLayout.createSequentialGroup()
                                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, 274, Short.MAX_VALUE)
                                    .addComponent(jTextField1)))
                            .addComponent(jLabel8)
                            .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel29)
                            .addComponent(jLabel11))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        CheckInPopupLayout.setVerticalGroup(
            CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPopupLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(27, 27, 27)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        getContentPane().add(CheckInPopup, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 290, 420, 360));

        CheckInPanel1.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        CheckInBTN.setBackground(new java.awt.Color(0, 102, 204));
        CheckInBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        CheckInBTN.setForeground(new java.awt.Color(255, 255, 255));
        CheckInBTN.setText("Check In");
        CheckInBTN.addActionListener(this::CheckInBTNActionPerformed);

        SentHomeBTN.setBackground(new java.awt.Color(0, 102, 204));
        SentHomeBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        SentHomeBTN.setForeground(new java.awt.Color(255, 255, 255));
        SentHomeBTN.setText("Sent Home/Back");
        SentHomeBTN.addActionListener(this::SentHomeBTNActionPerformed);

        SearchLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        SearchLabel.setForeground(new java.awt.Color(0, 0, 0));
        SearchLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchLabel.setText("Search:");

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Grade & Section", "LRN", "Parent/Guardian Name", "Phone number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
        jScrollPane2.setViewportView(ReasonTable);
        if (ReasonTable.getColumnModel().getColumnCount() > 0) {
            ReasonTable.getColumnModel().getColumn(0).setResizable(false);
            ReasonTable.getColumnModel().getColumn(1).setResizable(false);
            ReasonTable.getColumnModel().getColumn(2).setResizable(false);
            ReasonTable.getColumnModel().getColumn(3).setResizable(false);
            ReasonTable.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout CheckInPanel1Layout = new javax.swing.GroupLayout(CheckInPanel1);
        CheckInPanel1.setLayout(CheckInPanel1Layout);
        CheckInPanel1Layout.setHorizontalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CheckInPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addGap(456, 456, 456)
                .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(653, Short.MAX_VALUE))
        );
        CheckInPanel1Layout.setVerticalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CheckInBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(SentHomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(CheckInPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1490, 840));

        StatisticPanel.setBackground(new java.awt.Color(255, 255, 255));
        StatisticPanel.setForeground(new java.awt.Color(0, 0, 0));

        jLabel22.setText("Weekly Bar Graph");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(319, 319, 319)
                .addComponent(jLabel22)
                .addContainerGap(387, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jLabel22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Number of Student with Special Needs ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Number of Student with Allergy");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Total Medicine Used this Month");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Total of Student Sent home/ Referred to Hospital");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel28.setText("Pie Chart");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(322, 322, 322)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jLabel28)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("STATISTICS & HEALTH MONITORS");

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("CHRONIC HEALTH CONDITIONS");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Data", "Name", "Symptom", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 642, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
        );

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("EMERGENCY & REFERRAL INCIDENT LOG");

        javax.swing.GroupLayout StatisticPanelLayout = new javax.swing.GroupLayout(StatisticPanel);
        StatisticPanel.setLayout(StatisticPanelLayout);
        StatisticPanelLayout.setHorizontalGroup(
            StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addGroup(StatisticPanelLayout.createSequentialGroup()
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel26))
                        .addGap(18, 18, 18)
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(StatisticPanelLayout.createSequentialGroup()
                                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        StatisticPanelLayout.setVerticalGroup(
            StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addGap(34, 34, 34)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(StatisticPanelLayout.createSequentialGroup()
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(27, 27, 27)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42))
        );

        getContentPane().add(StatisticPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 1490, 800));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InventoryBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InventoryBTNActionPerformed
        if (!requireAdminPanelAccess()) return;
        SessionManager.saveSession(loggedInAccount);
        new AdminPanel(loggedInAccount).setVisible(true);
        dispose();
    }//GEN-LAST:event_InventoryBTNActionPerformed
    private void CheckInBTNActionPerformed(java.awt.event.ActionEvent evt) {
        openCheckinForSelection();
    }

    private void LogoutActionPerformed(java.awt.event.ActionEvent evt) {
        if (loggedInAccount != null) {
            try {
                ActivityLogData.log("LOGOUT", "User signed out.", loggedInAccount.GetName());
            } catch (java.sql.SQLException ignored) {
                // Logging failure must not trap a user in the application.
            }
        }
        SessionManager.clearSession();
        UserSession.clear();
        new LoginUi().setVisible(true);
        dispose();
    }

    private void SentHomeBTNActionPerformed(java.awt.event.ActionEvent evt) {
        // Legacy action removed: its original controls no longer exist in the generated form.
    }

    private void StatisticBTNActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(StatisticPanel);
    }

    private void ThemeToggleActionPerformed(java.awt.event.ActionEvent evt) {
        // Legacy action removed: its original controls no longer exist in the generated form.
    }

    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {
        selectStudentFromChooser();
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CheckInBTN;
    private javax.swing.JPanel CheckInPanel1;
    private javax.swing.JPanel CheckInPopup;
    private javax.swing.JButton CheckinBTN;
    private javax.swing.JLabel DateTimeLabel;
    private javax.swing.JButton ECheckin;
    private javax.swing.JButton HomeBTN;
    private javax.swing.JButton InventoryBTN;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JButton Logout;
    private javax.swing.JTextField LrnField;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JTextField MedicineField;
    private javax.swing.JTextField NameField;
    private javax.swing.JTextField ReasonField;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel SearchLabel;
    private javax.swing.JButton SentHomeBTN;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JButton StatisticBTN;
    private javax.swing.JPanel StatisticPanel;
    private javax.swing.JLabel StudentCheckinLabel;
    private javax.swing.JTextField TempField;
    private javax.swing.JToggleButton ThemeToggle;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
