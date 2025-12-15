import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class GJMS extends JFrame {

    private static final String HOME = "HOME_SCREEN";

    private static final String CREATE_ACCOUNT = "CREATE_ACCOUNT_SCREEN";

    private static final String LOGIN = "LOGIN_SCREEN";

    private static final String DASHBOARD = "DASHBOARD_SCREEN";

    private static final String JOB_LIST = "JOB_LIST_SCREEN";

    private static final String APPLY_JOB = "APPLY_JOB_SCREEN";

    private static final String CREATE_JOB = "CREATE_JOB_SCREEN";

    private static final String VIEW_APPLIED = "VIEW_APPLIED_JOBS_SCREEN";

    private static final String VIEW_CREATED = "VIEW_CREATED_JOBS_SCREEN";

    private static final Color BG_COLOR = new Color(60, 60, 60);

    private static final Color TEXT_COLOR = new Color(120, 170, 255);

    private static final Color BOX_COLOR = new Color(50, 50, 50);
    private static final Color BUTTON_COLOR = new Color(0, 255, 0);

    private static final Color BUTTON_SURFACE_NORMAL = new Color(0, 200, 0);

    private static final Color BUTTON_SURFACE_HOVER = new Color(100, 240, 60);

    private static final Color BUTTON_SURFACE_PRESSED = new Color(0, 140, 0);

    private static final Font HEADER_FONT = new Font("Monospaced", Font.BOLD, 50);

    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 30);

    private static final Font BUTTON2_FONT = new Font("Monospaced", Font.BOLD, 25);
    private static final Font TEXT_FONT = new Font("Monospaced", Font.PLAIN, 20);

    private static final int BUTTON_CORNER_RADIUS = 10;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/gjms_db";

    private static final String DB_USER = "root";

    private static final String DB_PASS = "";

    private final CardLayout cardLayout = new CardLayout();

    private final JPanel mainPanel = new JPanel(cardLayout);

    private String loggedInUsername = null;

    private int selectedJobIdToApply;

    private JTextField loginUsernameField;

    private JPasswordField loginPasswordField;

    private JTextField createUsernameField;

    private JPasswordField createPasswordField;

    private JPasswordField createRePasswordField;

    private JTextField jobTitleField;

    private JTextField employerNameField;

    private JTextField jobLocationField;

    private JTextField contactNumberField;

    private JTextArea jobDescriptionArea;

    private JTextArea skillsRequiredArea;

    private JTextArea postingDeadlineArea;

    private JTextField applyFullNameField;

    private JTextField applyAgeField;

    private JTextField applyContactNumberField;

    private JTextField applyEmailField;

    private JTextArea applyEducationSkillsArea;

    private JTable applicantsTable;
    private JDialog applicantsDialog;
    private JTable jobListTable;

    private JTable appliedJobTable;

    private JTable createdJobTable;

    private JScrollPane jobListScrollPane;

    private JScrollPane appliedJobScrollPane;

    private JScrollPane createdJobScrollPane;

    // Hover tracking for tables (so table cell buttons can show hover)

    private int tableHoverRow = -1;

    private int tableHoverCol = -1;

    /**
     * 
     * Establishes a connection to the MySQL database.
     *
     * 
     * 
     * @return A Connection object or null if the connection fails.
     * 
     */

    public static Connection connect() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null,

                    "Database Connection Failed!\nEnsure MySQL/XAMPP is running and the 'gjms_db' database exists.",

                    "DB Error", JOptionPane.ERROR_MESSAGE);

            e.printStackTrace();

        }

        return conn;

    }

    public GJMS() {

        setTitle("GUI Window");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1000, 600);

        setLocationRelativeTo(null);

        initializeGUI();

    }

    private void initializeGUI() {

        mainPanel.add(createHomeScreen(), HOME);

        mainPanel.add(createCreateAccountScreen(), CREATE_ACCOUNT);

        mainPanel.add(createLoginScreen(), LOGIN);

        mainPanel.add(createDashboardScreen(), DASHBOARD);

        mainPanel.add(createJobListScreen(), JOB_LIST);

        mainPanel.add(createApplyJobScreen(), APPLY_JOB);

        mainPanel.add(createCreateJobScreen(), CREATE_JOB);

        mainPanel.add(createViewAppliedJobsScreen(), VIEW_APPLIED);

        mainPanel.add(createViewCreatedJobsScreen(), VIEW_CREATED);

        add(mainPanel);

        cardLayout.show(mainPanel, HOME);

    }

    private void addJobListMouseListener() {
        if (jobListTable == null)
            return;
        jobListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = jobListTable.rowAtPoint(e.getPoint());
                int column = jobListTable.columnAtPoint(e.getPoint());
                int lastCol = jobListTable.getColumnCount() - 1;

                if (column == lastCol) {
                    Object cellValue = jobListTable.getValueAt(row, column);

                    Object idValueObj = jobListTable.getModel().getValueAt(jobListTable.convertRowIndexToModel(row), 0);

                    if (!(idValueObj instanceof Integer) || (int) idValueObj <= 0) {
                        return;
                    }

                    // 🚨 CRITICAL FIX: Set the job ID before navigating
                    selectedJobIdToApply = (int) idValueObj;

                    if ("Apply".equals(cellValue)) {

                        clearApplyFields();
                        cardLayout.show(mainPanel, APPLY_JOB);
                    } else if ("APPLIED".equals(cellValue)) {

                        loadApplicationData(selectedJobIdToApply);
                        cardLayout.show(mainPanel, APPLY_JOB);
                    }
                }
            }
        });

    }

    private JPanel createHomeScreen() {

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(15, 15, 15, 15);

        gbc.gridx = 0;

        gbc.gridy = 0;

        JLabel title = new JLabel("GRADUATE JOB MANAGEMENT SYSTEM");

        title.setFont(HEADER_FONT);

        title.setForeground(TEXT_COLOR);

        panel.add(title, gbc);

        gbc.gridy++;

        JLabel subtitle = new JLabel("Find or Post Eco-Friendly Employment Opportunities");

        subtitle.setFont(TEXT_FONT.deriveFont(Font.ITALIC, 25f));

        subtitle.setForeground(TEXT_COLOR);

        panel.add(subtitle, gbc);

        gbc.gridy++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton loginButton = createStyledButton("LOG-IN", BUTTON_FONT);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> {

            clearLoginFields();

            cardLayout.show(mainPanel, LOGIN);

        });

        JButton createAccountButton = createStyledButton("CREATE ACCOUNT", BUTTON_FONT);
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.addActionListener(e -> {

            clearCreateFields();

            cardLayout.show(mainPanel, CREATE_ACCOUNT);

        });

        JButton exitButton = createStyledButton("EXIT", BUTTON_FONT);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> {

            System.exit(0);

        });

        buttonPanel.add(loginButton);

        buttonPanel.add(createAccountButton);

        buttonPanel.add(exitButton);

        panel.add(buttonPanel, gbc);

        return panel;

    }

    private JPanel createLoginScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        panel.add(createHeader("LOG IN"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = createLabel("Username:", TEXT_FONT);

        JLabel passLabel = createLabel("Password:", TEXT_FONT);

        loginUsernameField = createTextField(20);
        loginUsernameField.setForeground(Color.WHITE);

        loginPasswordField = createPasswordField(20);
        loginPasswordField.setForeground(Color.WHITE);

        gbc.gridx = 0;

        gbc.gridy = 0;

        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;

        formPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 1;

        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;

        formPanel.add(loginPasswordField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK", BUTTON_FONT);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {

            clearLoginFields();

            cardLayout.show(mainPanel, HOME);

        });

        JButton loginButton = createStyledButton("LOG-IN", BUTTON_FONT);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this::handleLogin);

        buttonPanel.add(backButton);

        buttonPanel.add(loginButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createCreateAccountScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        panel.add(createHeader("CREATE ACCOUNT"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = createLabel("New Username (Minimum At least 8 Characters):",
                TEXT_FONT);

        JLabel passLabel = createLabel("Password (Minimum At Least 8 Characters):", TEXT_FONT);

        JLabel rePassLabel = createLabel("Re-Enter Password:", TEXT_FONT);

        createUsernameField = createTextField(20);

        createPasswordField = createPasswordField(20);

        createRePasswordField = createPasswordField(20);

        gbc.gridx = 0;

        gbc.gridy = 0;

        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;

        formPanel.add(createUsernameField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 1;

        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;

        formPanel.add(createPasswordField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 2;

        formPanel.add(rePassLabel, gbc);

        gbc.gridx = 1;

        formPanel.add(createRePasswordField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK", BUTTON_FONT);

        backButton.addActionListener(e -> {

            clearCreateFields();

            cardLayout.show(mainPanel, HOME);

        });

        JButton createButton = createStyledButton("CREATE", BUTTON_FONT);

        createButton.addActionListener(this::handleCreateAccount);

        buttonPanel.add(backButton);

        buttonPanel.add(createButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createDashboardScreen() {

        JPanel panel = new JPanel(new BorderLayout(20, 20));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        panel.add(createHeader("MAIN DASHBOARD"), BorderLayout.NORTH);

        JPanel buttonGrid = new JPanel(new GridLayout(3, 2, 20, 20));

        buttonGrid.setBackground(BG_COLOR);

        JButton browseJobsButton = createStyledButton("FIND and APPLY JOBS", BUTTON2_FONT);
        browseJobsButton.setForeground(Color.WHITE);
        browseJobsButton.addActionListener(e -> refreshJobListAndShow());

        JButton viewAppliedButton = createStyledButton("VIEW APPLIED JOBS", BUTTON_FONT);
        viewAppliedButton.setForeground(Color.WHITE);
        viewAppliedButton.addActionListener(e -> refreshAppliedJobsAndShow());

        JButton createJobButton = createStyledButton("CREATE JOB POST", BUTTON_FONT);
        createJobButton.setForeground(Color.WHITE);
        createJobButton.addActionListener(e -> {

            clearCreateJobFields();

            cardLayout.show(mainPanel, CREATE_JOB);

        });

        JButton viewCreatedButton = createStyledButton("VIEW CREATED JOB POSTS", BUTTON2_FONT);
        viewCreatedButton.setForeground(Color.WHITE);
        viewCreatedButton.addActionListener(e -> refreshCreatedJobsAndShow());

        JButton logoutButton = createStyledButton("LOGOUT", BUTTON_FONT);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {

            loggedInUsername = null;

            cardLayout.show(mainPanel, HOME);

        });

        JButton deleteAccountButton = createStyledButton("DELETE ACCOUNT", BUTTON_FONT);

        deleteAccountButton.setBackground(Color.RED);

        deleteAccountButton.setForeground(Color.WHITE);

        deleteAccountButton.addActionListener(this::handleDeleteAccount);

        buttonGrid.add(browseJobsButton);

        buttonGrid.add(createJobButton);

        buttonGrid.add(viewAppliedButton);

        buttonGrid.add(viewCreatedButton);

        buttonGrid.add(logoutButton);

        buttonGrid.add(deleteAccountButton);

        panel.add(buttonGrid, BorderLayout.CENTER);

        return panel;

    }

    private JPanel createJobListScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(createHeader("AVAILABLE JOB POSTS"), BorderLayout.NORTH);

        jobListTable = new JTable();

        jobListTable.getTableHeader().setFont(TEXT_FONT.deriveFont(Font.BOLD));

        jobListTable.setFont(TEXT_FONT.deriveFont(12f));

        jobListTable.setRowHeight(50);

        jobListTable.setGridColor(TEXT_COLOR);

        jobListTable.setBackground(BOX_COLOR);

        jobListTable.setForeground(Color.WHITE);

        jobListTable.setSelectionBackground(new Color(0, 150, 0));

        jobListTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(0, 100, 0));

        headerRenderer.setForeground(Color.WHITE);

        jobListTable.getTableHeader().setDefaultRenderer(headerRenderer);

        jobListScrollPane = new JScrollPane(jobListTable);

        jobListScrollPane.getViewport().setBackground(BOX_COLOR);

        jobListScrollPane.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 2));

        panel.add(jobListScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK TO DASHBOARD", BUTTON_FONT);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));

        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        addJobListMouseListener();

        return panel;

    }

    private JPanel createApplyJobScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(createHeader("JOB APPLICATION FORM"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel fullNameLabel = createLabel("Full Name:", TEXT_FONT);

        JLabel ageLabel = createLabel("Age:", TEXT_FONT);

        JLabel contactLabel = createLabel("Contact Number:", TEXT_FONT);

        JLabel emailLabel = createLabel("Email:", TEXT_FONT);

        JLabel educationLabel = createLabel("Course and Skills:", TEXT_FONT);

        applyFullNameField = createTextField(30);
        applyFullNameField.setForeground(Color.WHITE);
        applyAgeField = createTextField(30);
        applyAgeField.setForeground(Color.WHITE);
        applyContactNumberField = createTextField(30);
        applyContactNumberField.setForeground(Color.WHITE);
        applyEmailField = createTextField(30);
        applyEmailField.setForeground(Color.WHITE);
        applyEducationSkillsArea = createTextArea(5, 30);
        applyEducationSkillsArea.setForeground(Color.WHITE);

        gbc.gridx = 0;

        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.WEST;

        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;

        gbc.weightx = 1.0;

        formPanel.add(applyFullNameField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 1;

        gbc.weightx = 0;

        formPanel.add(ageLabel, gbc);

        gbc.gridx = 1;

        gbc.weightx = 1.0;

        formPanel.add(applyAgeField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 2;

        gbc.weightx = 0;

        formPanel.add(contactLabel, gbc);

        gbc.gridx = 1;

        gbc.weightx = 1.0;

        formPanel.add(applyContactNumberField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 3;

        gbc.weightx = 0;

        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;

        gbc.weightx = 1.0;

        formPanel.add(applyEmailField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 4;

        gbc.weightx = 0;

        formPanel.add(educationLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 4;

        gbc.weighty = 1.0;

        gbc.fill = GridBagConstraints.BOTH;

        formPanel.add(new JScrollPane(applyEducationSkillsArea), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("CANCEL APPLY", BUTTON_FONT);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {

            clearApplyFields();

            refreshJobListAndShow();

        });

        JButton submitButton = createStyledButton("SUBMIT APPLICATION", BUTTON_FONT);
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(this::handleJobApplication);

        buttonPanel.add(backButton);

        buttonPanel.add(submitButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createCreateJobScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(createHeader("CREATE NEW JOB POST"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());

        formPanel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = createLabel("Job Title:", TEXT_FONT);

        JLabel descriptionLabel = createLabel("Job Description:", TEXT_FONT);

        JLabel employerLabel = createLabel("Employer Name:", TEXT_FONT);

        JLabel skillsLabel = createLabel("Course and Skills:", TEXT_FONT);

        JLabel locationLabel = createLabel("Job Location:", TEXT_FONT);

        JLabel contactLabel = createLabel("Contact Number:", TEXT_FONT);
        JLabel deadlineLabel = createLabel("Posting Date Deadline (YYYY-MM-DD):", TEXT_FONT);

        jobTitleField = createTextField(30);
        jobTitleField.setForeground(Color.WHITE);
        employerNameField = createTextField(30);
        employerNameField.setForeground(Color.WHITE);
        jobLocationField = createTextField(30);
        jobLocationField.setForeground(Color.WHITE);
        contactNumberField = createTextField(30);
        contactNumberField.setForeground(Color.WHITE);
        jobDescriptionArea = createTextArea(3, 30);
        jobDescriptionArea.setForeground(Color.WHITE);
        skillsRequiredArea = createTextArea(3, 30);
        skillsRequiredArea.setForeground(Color.WHITE);
        postingDeadlineArea = createTextArea(1, 30);
        postingDeadlineArea.setForeground(Color.WHITE);

        gbc.gridx = 0;

        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.WEST;

        formPanel.add(titleLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 0;

        gbc.weightx = 1.0;

        formPanel.add(jobTitleField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 1;

        formPanel.add(employerLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 1;

        formPanel.add(employerNameField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 2;

        formPanel.add(locationLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 2;

        formPanel.add(jobLocationField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 3;

        formPanel.add(contactLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 3;

        formPanel.add(contactNumberField, gbc);

        gbc.gridx = 0;

        gbc.gridy = 4;

        formPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 4;

        gbc.weighty = 0.5;

        formPanel.add(new JScrollPane(jobDescriptionArea), gbc);

        gbc.gridx = 0;

        gbc.gridy = 5;

        gbc.weighty = 0;

        formPanel.add(skillsLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 5;

        gbc.weighty = 0.5;

        formPanel.add(new JScrollPane(skillsRequiredArea), gbc);

        gbc.gridx = 0;

        gbc.gridy = 6;

        gbc.weighty = 0;

        formPanel.add(deadlineLabel, gbc);

        gbc.gridx = 1;

        gbc.gridy = 6;

        gbc.weighty = 0;

        formPanel.add(new JScrollPane(postingDeadlineArea), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        buttonPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK", BUTTON_FONT);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {

            clearCreateJobFields();

            cardLayout.show(mainPanel, DASHBOARD);

        });

        JButton createButton = createStyledButton("CREATE", BUTTON_FONT);
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(this::handleCreateJob);

        buttonPanel.add(backButton);

        buttonPanel.add(createButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createViewAppliedJobsScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(createHeader("VIEW JOB APPLIED"), BorderLayout.NORTH);

        appliedJobTable = new JTable();

        appliedJobTable.getTableHeader().setFont(TEXT_FONT.deriveFont(Font.BOLD));

        appliedJobTable.setFont(TEXT_FONT.deriveFont(12f));

        appliedJobTable.setRowHeight(30);

        appliedJobTable.setGridColor(BOX_COLOR);

        appliedJobTable.setBackground(BOX_COLOR);

        appliedJobTable.setForeground(TEXT_COLOR);

        appliedJobTable.setSelectionBackground(new Color(0, 100, 0));

        appliedJobTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();

        headerRenderer.setBackground(new Color(0, 70, 0));

        headerRenderer.setForeground(Color.WHITE);

        appliedJobTable.getTableHeader().setDefaultRenderer(headerRenderer);

        appliedJobScrollPane = new JScrollPane(appliedJobTable);

        appliedJobScrollPane.getViewport().setBackground(BOX_COLOR);

        appliedJobScrollPane.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 2));

        panel.add(appliedJobScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        southPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK", BUTTON_FONT);

        backButton.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));

        JButton deleteButton = createStyledButton("DELETE APPLICATION", TEXT_FONT);

        deleteButton.setBackground(Color.RED);

        deleteButton.setForeground(Color.WHITE);

        deleteButton.addActionListener(this::handleDeleteApplication);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setBackground(BG_COLOR);

        buttonPanel.add(deleteButton);

        buttonPanel.add(backButton);

        southPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createViewCreatedJobsScreen() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(BG_COLOR);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(createHeader("CREATED JOB POSTS"), BorderLayout.NORTH);

        createdJobTable = new JTable();

        createdJobTable.getTableHeader().setFont(TEXT_FONT.deriveFont(Font.BOLD));

        createdJobTable.setFont(TEXT_FONT.deriveFont(12f));

        createdJobTable.setRowHeight(30);

        createdJobTable.setGridColor(TEXT_COLOR);

        createdJobTable.setBackground(BOX_COLOR);

        createdJobTable.setForeground(TEXT_COLOR);

        createdJobTable.setSelectionBackground(new Color(0, 150, 0));

        createdJobTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer headerRenderer2 = new DefaultTableCellRenderer();

        headerRenderer2.setBackground(new Color(0, 100, 0));

        headerRenderer2.setForeground(Color.WHITE);

        createdJobTable.getTableHeader().setDefaultRenderer(headerRenderer2);

        createdJobScrollPane = new JScrollPane(createdJobTable);

        createdJobScrollPane.getViewport().setBackground(BOX_COLOR);

        createdJobScrollPane.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 2));

        panel.add(createdJobScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        southPanel.setBackground(BG_COLOR);

        JButton backButton = createStyledButton("BACK", BUTTON_FONT);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, DASHBOARD));

        JButton deleteButton = createStyledButton("DELETE JOB POST", TEXT_FONT);

        deleteButton.setBackground(Color.RED);

        deleteButton.setForeground(Color.WHITE);

        deleteButton.addActionListener(this::handleDeleteJobPost);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setBackground(BG_COLOR);

        buttonPanel.add(deleteButton);

        buttonPanel.add(backButton);

        southPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(southPanel, BorderLayout.SOUTH);

        createdJobTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = createdJobTable.getSelectedRow();
                    if (row != -1) {

                        Object idValue = createdJobTable.getValueAt(row, 0);
                        Object titleValue = createdJobTable.getValueAt(row, 1);

                        if (idValue instanceof Integer && (int) idValue > 0) {
                            int jobId = (int) idValue;
                            String jobTitle = (String) titleValue;

                            showApplicantsForJob(jobId, jobTitle);
                        }
                    }
                }
            }
        });
        //

        return panel;
    }

    private void handleCreateAccount(ActionEvent e) {

        String username = createUsernameField.getText().trim();

        String password = new String(createPasswordField.getPassword());

        String rePassword = new String(createRePasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);

            return;

        }

        if (!password.equals(rePassword)) {

            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);

            return;

        }

        if (password.length() < 8 || username.length() < 6) {

            JOptionPane.showMessageDialog(this,

                    "Password must be at least 8 Characters and Username at least 6 Characters.", "Error",

                    JOptionPane.ERROR_MESSAGE);

            return;

        }

        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hashedPassword = password;

            pstmt.setString(1, username);

            pstmt.setString(2, hashedPassword);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account Created Successfully! You can now Log In.", "Account Saved",

                    JOptionPane.INFORMATION_MESSAGE);

            clearCreateFields();

            cardLayout.show(mainPanel, HOME);

        } catch (SQLIntegrityConstraintViolationException ex) {

            JOptionPane.showMessageDialog(this,

                    "Username '" + username + "' Already Exists. Please choose a different one.", "Error",

                    JOptionPane.ERROR_MESSAGE);

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private void handleLogin(ActionEvent e) {
        String enteredUsername = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (enteredUsername.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Enter Username and Password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT username, password_hash FROM users WHERE username = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enteredUsername);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    String storedUsername = rs.getString("username");
                    String storedPasswordHash = rs.getString("password_hash");

                    if (!enteredUsername.equals(storedUsername)) {

                        JOptionPane.showMessageDialog(this,
                                "Wrong Username or Account Not Exist.", "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (password.equals(storedPasswordHash)) {

                        loggedInUsername = enteredUsername;
                        clearLoginFields();
                        cardLayout.show(mainPanel, DASHBOARD);
                    } else {

                        JOptionPane.showMessageDialog(this, "Incorrect Password.", "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {

                    JOptionPane.showMessageDialog(this,
                            "Wrong username or Account not exist.", "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error during Log-In: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJobListAndShow() {

        String sql = "SELECT id, job_title, job_description, employer_name, skills_required, job_location, contact_number, posting_deadline, posting_date FROM job_posts";

        Vector<Vector<Object>> data = new Vector<>();

        Vector<String> columnNames = new Vector<>();

        columnNames.addAll(java.util.Arrays.asList("ID", "JOB TITLE", "JOB DESCRIPTION", "EMPLOYER NAME",

                "COURSE and SKILLS", "JOB LOCATION", "CONTACT NUMBER", "POSTING DEADLINE", "POSTING DATE",

                "APPLY"));

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql);

                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                Vector<Object> row = new Vector<>();

                int jobId = rs.getInt("id");

                String applyStatus = hasUserApplied(loggedInUsername, jobId) ? "APPLIED" : "Apply";

                row.add(jobId);

                row.add(rs.getString("job_title"));

                row.add(rs.getString("job_description"));

                row.add(rs.getString("employer_name"));

                row.add(rs.getString("skills_required"));

                row.add(rs.getString("job_location"));

                row.add(rs.getString("contact_number"));

                row.add(rs.getString("posting_deadline"));

                row.add(rs.getDate("posting_date").toString());

                row.add(applyStatus);
                data.add(row);

            }

            if (data.isEmpty()) {

                data.add(createEmptyRow(columnNames.size(), "No Jobs Posted Yet."));

            }

            jobListTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {

                @Override

                public boolean isCellEditable(int row, int column) {

                    return column == columnNames.size() - 1;

                }

            });

            jobListTable.getColumnModel().getColumn(0).setMaxWidth(0);

            jobListTable.getColumnModel().getColumn(0).setMinWidth(0);

            jobListTable.getColumnModel().getColumn(0).setPreferredWidth(0);

            jobListTable.getColumnModel().getColumn(columnNames.size() - 1).setCellRenderer(new ButtonRenderer());

            jobListTable.getColumnModel().getColumn(columnNames.size() - 1)

                    .setCellEditor(new ButtonEditor(new JTextField()));

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Database error loading job posts: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

        cardLayout.show(mainPanel, JOB_LIST);

    }

    private boolean hasUserApplied(String username, int jobId) {

        String sql = "SELECT COUNT(*) FROM job_applications WHERE username = ? AND job_id = ?";

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            pstmt.setInt(2, jobId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1) > 0;

                }

            }

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return false;

    }

    private void loadApplicationData(int jobId) {

        String sql = "SELECT * FROM job_applications WHERE username = ? AND job_id = ?";

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loggedInUsername);

            pstmt.setInt(2, jobId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    applyFullNameField.setText(rs.getString("full_name"));

                    applyAgeField.setText(String.valueOf(rs.getInt("age")));

                    applyContactNumberField.setText(rs.getString("contact_number"));

                    applyEmailField.setText(rs.getString("email"));

                    applyEducationSkillsArea.setText(rs.getString("education_skills"));

                } else {

                    clearApplyFields();
                }

            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Error loading application data: " + ex.getMessage(), "DB Error",

                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private void handleJobApplication(ActionEvent e) {

        String fullName = applyFullNameField.getText().trim();
        String ageText = applyAgeField.getText().trim();
        String contactNumber = applyContactNumberField.getText().trim();
        String email = applyEmailField.getText().trim();
        String educationSkills = applyEducationSkillsArea.getText().trim();

        if (selectedJobIdToApply <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Application Error: Please go back to the Job List and click 'Apply' on a job.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Session Error: You must be logged in to apply for a job.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fullName.isEmpty() || ageText.isEmpty() || contactNumber.isEmpty() || email.isEmpty()
                || educationSkills.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Fill in All Required Fields.", "Validation Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 16 || age > 99) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age (16-99).", "Validation Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "REPLACE INTO job_applications (job_id, username, full_name, age, contact_number, email, education_skills) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedJobIdToApply);
            pstmt.setString(2, loggedInUsername);
            pstmt.setString(3, fullName);
            pstmt.setInt(4, age);
            pstmt.setString(5, contactNumber);
            pstmt.setString(6, email);
            pstmt.setString(7, educationSkills);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Application successfully submitted!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                clearApplyFields();
                refreshJobListAndShow();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit application. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this,
                    "Database error during application submission: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleCreateJob(ActionEvent e) {

        String jobTitle = jobTitleField.getText().trim();

        String jobDescription = jobDescriptionArea.getText().trim();

        String employerName = employerNameField.getText().trim();

        String skillsRequired = skillsRequiredArea.getText().trim();

        String jobLocation = jobLocationField.getText().trim();

        String contactNumber = contactNumberField.getText().trim();

        String deadline = postingDeadlineArea.getText().trim();

        if (jobTitle.isEmpty() || jobDescription.isEmpty() || employerName.isEmpty() || skillsRequired.isEmpty()

                || jobLocation.isEmpty() || contactNumber.isEmpty() || deadline.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all fields to create a job post.", "Error",

                    JOptionPane.ERROR_MESSAGE);

            return;

        }

        String sql = "INSERT INTO job_posts (job_title, job_description, employer_name, skills_required, job_location, contact_number, posting_deadline, creator_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, jobTitle);

            pstmt.setString(2, jobDescription);

            pstmt.setString(3, employerName);

            pstmt.setString(4, skillsRequired);

            pstmt.setString(5, jobLocation);

            pstmt.setString(6, contactNumber);

            pstmt.setString(7, deadline);
            pstmt.setString(8, loggedInUsername);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Job Created Successfully!", "Success",

                    JOptionPane.INFORMATION_MESSAGE);

            clearCreateJobFields();

            cardLayout.show(mainPanel, DASHBOARD);

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Database Error during Job Creation: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * * Displays a separate JFrame showing the list of applicants for a given Job
     * ID.
     * * @param jobId The ID of the job post to view applicants for.
     */
    private void showApplicantsForJob(int jobId, String jobTitle) {

        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Full Name");
        columnNames.add("Age");
        columnNames.add("Contact");
        columnNames.add("Email");
        columnNames.add("Education & Skills");
        columnNames.add("Applied Date");

        String sql = "SELECT full_name, age, contact_number, email, education_skills, application_date FROM job_applications WHERE job_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("full_name"));
                    row.add(rs.getInt("age"));
                    row.add(rs.getString("contact_number"));
                    row.add(rs.getString("email"));
                    row.add(rs.getString("education_skills"));
                    row.add(rs.getTimestamp("application_date").toString());
                    data.add(row);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading applicants: " + ex.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return;
        }

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Applicants have Applied for the Job: " + jobTitle, "No Applicants",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame applicantFrame = new JFrame("Applicants for: " + jobTitle);
        applicantFrame.setSize(800, 500);
        applicantFrame.setLocationRelativeTo(this);
        applicantFrame.getContentPane().setBackground(BG_COLOR);

        JTable applicantTable = new JTable(data, columnNames);
        applicantTable.getTableHeader().setFont(TEXT_FONT.deriveFont(Font.BOLD));
        applicantTable.setFont(TEXT_FONT.deriveFont(12f));
        applicantTable.setRowHeight(25);
        applicantTable.setGridColor(BOX_COLOR);
        applicantTable.setBackground(BOX_COLOR);
        applicantTable.setForeground(TEXT_COLOR);
        applicantTable.setSelectionBackground(new Color(0, 100, 0));
        applicantTable.setSelectionForeground(Color.WHITE);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(0, 70, 0));
        headerRenderer.setForeground(Color.WHITE);
        applicantTable.getTableHeader().setDefaultRenderer(headerRenderer);

        JScrollPane scrollPane = new JScrollPane(applicantTable);
        scrollPane.getViewport().setBackground(BOX_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(TEXT_COLOR, 2));

        JPanel titleLabel = createHeader("APPLICANTS FOR: " + jobTitle.toUpperCase());
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BG_COLOR);
        northPanel.add(titleLabel, BorderLayout.NORTH);

        applicantFrame.add(northPanel, BorderLayout.NORTH);
        applicantFrame.add(scrollPane, BorderLayout.CENTER);
        applicantFrame.setVisible(true);
    }

    private void refreshAppliedJobsAndShow() {

        String sql = "SELECT ja.job_id, jp.job_title, jp.employer_name, ja.application_date, ja.full_name, ja.age, ja.contact_number, ja.email, ja.education_skills FROM job_applications ja JOIN job_posts jp ON ja.job_id = jp.id WHERE ja.username = ?";

        Vector<Vector<Object>> data = new Vector<>();

        Vector<String> columnNames = new Vector<>();

        columnNames.addAll(java.util.Arrays.asList("JOB ID", "JOB TITLE", "EMPLOYER NAME", "APPLICATION DATE",

                "FULL NAME", "AGE", "CONTACT", "EMAIL", "EDUCATION/SKILLS"));

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loggedInUsername);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Vector<Object> row = new Vector<>();

                    row.add(rs.getInt("job_id"));

                    row.add(rs.getString("job_title"));

                    row.add(rs.getString("employer_name"));

                    row.add(rs.getTimestamp("application_date").toString());

                    row.add(rs.getString("full_name"));

                    row.add(rs.getInt("age"));

                    row.add(rs.getString("contact_number"));

                    row.add(rs.getString("email"));

                    row.add(rs.getString("education_skills"));

                    data.add(row);

                }

            }

            if (data.isEmpty()) {

                data.add(createEmptyRow(columnNames.size(), "You have not applied to any jobs."));

            }

            appliedJobTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {

                @Override

                public boolean isCellEditable(int row, int column) {

                    return false;

                }

            });

            appliedJobTable.getColumnModel().getColumn(0).setMaxWidth(0);

            appliedJobTable.getColumnModel().getColumn(0).setMinWidth(0);

            appliedJobTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Database error loading applied jobs: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

        cardLayout.show(mainPanel, VIEW_APPLIED);

    }

    private void refreshCreatedJobsAndShow() {

        String sql = "SELECT id, job_title, employer_name, posting_date FROM job_posts WHERE creator_username = ?";

        Vector<Vector<Object>> data = new Vector<>();

        Vector<String> columnNames = new Vector<>();

        columnNames.addAll(

                java.util.Arrays.asList("JOB ID", "JOB TITLE", "EMPLOYER NAME", "POSTING DATE", "APPLICATIONS"));

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loggedInUsername);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {

                    Vector<Object> row = new Vector<>();

                    int jobId = rs.getInt("id");

                    row.add(jobId);

                    row.add(rs.getString("job_title"));

                    row.add(rs.getString("employer_name"));

                    row.add(rs.getDate("posting_date").toString());

                    row.add(countApplications(jobId));
                    data.add(row);

                }

            }

            if (data.isEmpty()) {

                data.add(createEmptyRow(columnNames.size(), "You have not created any job posts."));

            }

            createdJobTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {

                @Override

                public boolean isCellEditable(int row, int column) {

                    return false;

                }

            });

            createdJobTable.getColumnModel().getColumn(0).setMaxWidth(0);

            createdJobTable.getColumnModel().getColumn(0).setMinWidth(0);

            createdJobTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "Database error loading created jobs: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

        cardLayout.show(mainPanel, VIEW_CREATED);

    }

    private int countApplications(int jobId) {

        String sql = "SELECT COUNT(*) FROM job_applications WHERE job_id = ?";

        try (Connection conn = connect();

                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jobId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);

                }

            }

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return 0;

    }

    private void handleDeleteAccount(ActionEvent e) {

        int confirm = JOptionPane.showConfirmDialog(this,

                "WARNING: This will permanently delete your account, all job posts, and all job applications.\nAre you sure you want to proceed?",

                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = connect()) {

                conn.setAutoCommit(false);

                try (PreparedStatement stmt = conn

                        .prepareStatement("DELETE FROM job_applications WHERE username = ?")) {

                    stmt.setString(1, loggedInUsername);

                    stmt.executeUpdate();

                }

                try (PreparedStatement stmt = conn

                        .prepareStatement("DELETE FROM job_posts WHERE creator_username = ?")) {

                    stmt.setString(1, loggedInUsername);

                    stmt.executeUpdate();

                }

                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {

                    stmt.setString(1, loggedInUsername);

                    stmt.executeUpdate();

                }

                conn.commit();

                JOptionPane.showMessageDialog(this, "Your account has been successfully deleted.", "Success",

                        JOptionPane.INFORMATION_MESSAGE);

                loggedInUsername = null;

                cardLayout.show(mainPanel, HOME);

            } catch (SQLException ex) {

                try (Connection conn = connect()) {

                    if (conn != null)

                        conn.rollback();

                } catch (SQLException ignored) {

                }

                JOptionPane.showMessageDialog(this,

                        "Error deleting account: " + ex.getMessage() + "\nDatabase operation failed.", "Error",

                        JOptionPane.ERROR_MESSAGE);

            }

        }

    }

    private void handleDeleteJobPost(ActionEvent e) {

        int selectedRow = createdJobTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(this, "Please select a job post to delete.", "Error",

                    JOptionPane.ERROR_MESSAGE);

            return;

        }

        try {

            int jobId = (int) createdJobTable.getValueAt(selectedRow, 0);

            String jobTitle = (String) createdJobTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,

                    "Are you sure you want to delete the job post: '" + jobTitle

                            + "'?\nThis will also delete all associated applications.",

                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {

                String sql = "DELETE FROM job_posts WHERE id = ? AND creator_username = ?";

                try (Connection conn = connect();

                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, jobId);

                    pstmt.setString(2, loggedInUsername);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {

                        JOptionPane.showMessageDialog(this, "Job Post Deleted Successfully.", "Success",

                                JOptionPane.INFORMATION_MESSAGE);

                        refreshCreatedJobsAndShow();
                    } else {

                        JOptionPane.showMessageDialog(this,

                                "Job post not found or you do not have permission to delete it.", "Error",

                                JOptionPane.ERROR_MESSAGE);

                    }

                }

            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Error processing delete: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private void handleDeleteApplication(ActionEvent e) {

        int selectedRow = appliedJobTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(this, "Please select an application to delete.", "Error",

                    JOptionPane.ERROR_MESSAGE);

            return;

        }

        try {

            int jobId = (int) appliedJobTable.getValueAt(selectedRow, 0);

            String jobTitle = (String) appliedJobTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,

                    "Are you sure you want to withdraw your application for: '" + jobTitle + "'?",

                    "Confirm Withdrawal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {

                String sql = "DELETE FROM job_applications WHERE job_id = ? AND username = ?";

                try (Connection conn = connect();

                        PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, jobId);

                    pstmt.setString(2, loggedInUsername);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {

                        JOptionPane.showMessageDialog(this, "Application withdrawn successfully.", "Success",

                                JOptionPane.INFORMATION_MESSAGE);

                        refreshAppliedJobsAndShow(); // Refresh the table

                    } else {

                        JOptionPane.showMessageDialog(this, "Application not found.", "Error",

                                JOptionPane.ERROR_MESSAGE);

                    }

                }

            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Error processing withdrawal: " + ex.getMessage(), "Error",

                    JOptionPane.ERROR_MESSAGE);

        }

    }

    private JPanel createHeader(String text) {

        JPanel headerPanel = new JPanel();

        headerPanel.setBackground(BG_COLOR);

        JLabel headerLabel = new JLabel(text);

        headerLabel.setFont(HEADER_FONT);

        headerLabel.setForeground(TEXT_COLOR);

        headerPanel.add(headerLabel);

        return headerPanel;
    }

    private JButton createStyledButton(String text, Font font) {

        StyledButton button = new StyledButton(text, font);

        return button;

    }

    private JLabel createLabel(String text, Font font) {

        JLabel label = new JLabel(text);

        label.setFont(font);

        label.setForeground(TEXT_COLOR);

        return label;

    }

    private JTextField createTextField(int columns) {

        JTextField field = new JTextField(columns);

        field.setFont(TEXT_FONT);

        field.setBackground(BOX_COLOR);

        field.setForeground(TEXT_COLOR);

        field.setCaretColor(TEXT_COLOR);

        field.setMargin(new Insets(5, 5, 5, 5));

        field.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(TEXT_COLOR, 2),

                BorderFactory.createEmptyBorder(5, 5, 5, 5)

        ));

        return field;

    }

    private JPasswordField createPasswordField(int columns) {

        JPasswordField field = new JPasswordField(columns);

        field.setFont(TEXT_FONT);

        field.setBackground(BOX_COLOR);

        field.setForeground(TEXT_COLOR);

        field.setCaretColor(TEXT_COLOR);

        field.setMargin(new Insets(5, 5, 5, 5));

        field.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(TEXT_COLOR, 2),

                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        return field;

    }

    private JTextArea createTextArea(int rows, int columns) {

        JTextArea area = new JTextArea(rows, columns);

        area.setFont(TEXT_FONT);

        area.setBackground(BOX_COLOR);

        area.setForeground(TEXT_COLOR);

        area.setCaretColor(TEXT_COLOR);

        area.setLineWrap(true);

        area.setWrapStyleWord(true);

        area.setMargin(new Insets(5, 5, 5, 5));

        area.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(TEXT_COLOR, 2),

                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        return area;

    }

    private void clearCreateFields() {

        createUsernameField.setText("");

        createPasswordField.setText("");

        createRePasswordField.setText("");

    }

    private void clearLoginFields() {

        loginUsernameField.setText("");

        loginPasswordField.setText("");

    }

    private void clearCreateJobFields() {

        jobTitleField.setText("");

        jobDescriptionArea.setText("");

        employerNameField.setText("");

        skillsRequiredArea.setText("");

        jobLocationField.setText("");

        contactNumberField.setText("");

        postingDeadlineArea.setText("");

    }

    private void clearApplyFields() {
        applyFullNameField.setText("");
        applyAgeField.setText("");
        applyContactNumberField.setText("");
        applyEmailField.setText("");
        applyEducationSkillsArea.setText("");
    }

    private Vector<Object> createEmptyRow(int size, String message) {

        Vector<Object> emptyRow = new Vector<>();

        emptyRow.add(-1); // dummy ID

        emptyRow.add(message);

        emptyRow.setSize(size);

        return emptyRow;

    }

    class StyledButton extends JButton {

        public StyledButton(String text, Font font) {

            super(text);

            setFont(font);

            setForeground(TEXT_COLOR);

            setFocusPainted(false);

            setContentAreaFilled(false);

            setBorderPainted(false);

            setOpaque(false);

            setCursor(new Cursor(Cursor.HAND_CURSOR));

            setMargin(new Insets(10, 20, 10, 20));

            setBackground(BUTTON_SURFACE_NORMAL);

            addMouseListener(new MouseAdapter() {

                @Override

                public void mouseEntered(MouseEvent e) {

                    repaint();

                }

                @Override

                public void mouseExited(MouseEvent e) {

                    repaint();

                }

                @Override

                public void mousePressed(MouseEvent e) {

                    repaint();

                }

                @Override

                public void mouseReleased(MouseEvent e) {

                    repaint();

                }

            });

        }

        @Override

        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            try {

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color surface = BUTTON_SURFACE_NORMAL;

                ButtonModel model = getModel();

                if (model.isPressed()) {

                    surface = BUTTON_SURFACE_PRESSED;

                } else if (model.isRollover()) {

                    surface = BUTTON_SURFACE_HOVER;

                } else {

                    surface = getBackground() != null ? getBackground() : BUTTON_SURFACE_NORMAL;

                }

                g2.setColor(surface);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

                if (getForeground().equals(TEXT_COLOR)) {

                    g2.setColor(TEXT_COLOR);

                } else {

                    g2.setColor(getForeground());

                }

                g2.setStroke(new BasicStroke(2));

                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

            } finally {

                g2.dispose();

            }

            super.paintComponent(g);

        }

        @Override

        public void setContentAreaFilled(boolean b) {

        }

    }

    class ButtonRenderer extends StyledButton implements javax.swing.table.TableCellRenderer {

        public ButtonRenderer() {

            super("", TEXT_FONT.deriveFont(Font.BOLD));

            setOpaque(false);

            setContentAreaFilled(false);

            setBorderPainted(false);

            setFocusPainted(false);

            setForeground(TEXT_COLOR);

            setFont(TEXT_FONT.deriveFont(Font.BOLD));

            setMargin(new Insets(0, 0, 0, 0));

        }

        @Override

        public Component getTableCellRendererComponent(JTable table, Object value,

                boolean isSelected, boolean hasFocus, int row, int column) {

            String label = (value == null) ? "" : value.toString();

            setText(label);

            if ("APPLIED".equals(label)) {

                setBackground(Color.DARK_GRAY.darker());

            } else {

                setBackground(BUTTON_SURFACE_NORMAL);

            }

            if (row == tableHoverRow && column == tableHoverCol) {

                if (!"APPLIED".equals(label)) {

                    setBackground(BUTTON_SURFACE_HOVER);

                } else {

                    setBackground(Color.DARK_GRAY.darker());

                }

            }

            return this;

        }

    }

    class ButtonEditor extends DefaultCellEditor {

        protected StyledButton button;

        private String label;

        private boolean isPushed;

        public ButtonEditor(JTextField tf) {

            super(tf);

            setClickCountToStart(1);

            button = new StyledButton("", TEXT_FONT.deriveFont(Font.BOLD));

            button.setOpaque(false);

            button.setBorderPainted(false);

            button.setFocusPainted(false);

            button.setForeground(TEXT_COLOR);

            button.addActionListener(e -> {

                fireEditingStopped();

            });

        }

        @Override

        public Component getTableCellEditorComponent(JTable table, Object value,

                boolean isSelected, int row, int column) {

            label = (value == null) ? "" : value.toString();

            button.setText(label);

            if ("APPLIED".equals(label)) {

                button.setBackground(Color.DARK_GRAY.darker());

                button.setForeground(Color.WHITE);

            } else {

                button.setBackground(BUTTON_SURFACE_NORMAL);

                button.setForeground(TEXT_COLOR);

            }

            isPushed = true;

            return button;

        }

        @Override

        public Object getCellEditorValue() {

            isPushed = false;

            return label;

        }

        @Override

        public boolean stopCellEditing() {

            isPushed = false;

            return super.stopCellEditing();

        }

    }

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {

        }

        SwingUtilities.invokeLater(() -> {

            GJMS frame = new GJMS();

            frame.setVisible(true);

        });

    }

}
