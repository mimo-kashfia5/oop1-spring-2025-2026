package gui;

import entity.BankAccount;
import fileIO.BankAccountFileIO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class BankGUI extends JFrame {

    private final BankAccountFileIO fileIO = new BankAccountFileIO();

    private JTextField tfId, tfName, tfEmail, tfPhone, tfBalance, tfSearch;
    private JComboBox<String> cbType;
    private JTable table;
    private DefaultTableModel tableModel;
    private String editingId = null;

    public BankGUI() {
        setTitle("Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 680);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(13, 27, 42));

        //centre on desktop
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);

        buildHeader();
        buildForm();
        buildTablePanel();

        setVisible(true);
        refreshTable(fileIO.readAll());
    }

    
    //  UI CONSTRUCTION
    

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(27, 38, 59));
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("Bank Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(65, 188, 170));

        JLabel sub = new JLabel("Secure · Reliable · Simple");
        sub.setFont(new Font("SansSerif", Font.ITALIC, 13));
        sub.setForeground(new Color(140, 165, 190));

        header.add(title, BorderLayout.WEST);
        header.add(sub,   BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void buildForm() {
        JPanel formOuter = new JPanel(new BorderLayout());
        formOuter.setBackground(new Color(13, 27, 42));
        formOuter.setPreferredSize(new Dimension(285, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(13, 27, 42));
        form.setBorder(BorderFactory.createEmptyBorder(20, 16, 10, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(5, 2, 5, 2);
        gbc.weightx   = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        form.add(makeSection("ACCOUNT DETAILS"), gbc);

        form.add(makeLabel("Account ID (8 digits)"), gbc);
        tfId = makeField(); form.add(tfId, gbc);

        form.add(makeLabel("Account Holder Name"), gbc);
        tfName = makeField(); form.add(tfName, gbc);

        form.add(makeLabel("Email Address"), gbc);
        tfEmail = makeField(); form.add(tfEmail, gbc);

        form.add(makeLabel("Phone Number"), gbc);
        tfPhone = makeField(); form.add(tfPhone, gbc);

        form.add(makeLabel("Account Type"), gbc);
        cbType = new JComboBox<>(new String[]{"Savings", "Current"});
        cbType.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cbType.setBackground(new Color(27, 38, 59));
        cbType.setForeground(new Color(230, 241, 255));
        form.add(cbType, gbc);

        form.add(makeLabel("Initial Balance (BDT)"), gbc);
        tfBalance = makeField(); form.add(tfBalance, gbc);

        form.add(makeSection("ACTIONS"), gbc);

        JButton btnSave   = makeButton("Save / Update",   new Color(65, 188, 170), new Color(13, 27, 42));
        JButton btnClear  = makeButton("Clear Form",      new Color(27, 38, 59),   new Color(230, 241, 255));
        JButton btnDelete = makeButton("Delete Selected", new Color(200, 60, 60),  new Color(230, 241, 255));

        form.add(btnSave,  gbc);
        form.add(Box.createVerticalStrut(4), gbc);
        form.add(btnClear, gbc);
        form.add(Box.createVerticalStrut(4), gbc);
        form.add(btnDelete, gbc);

        btnSave  .addActionListener(e -> onSave());
        btnClear .addActionListener(e -> clearForm());
        btnDelete.addActionListener(e -> onDelete());

        formOuter.add(form, BorderLayout.NORTH);
        add(formOuter, BorderLayout.WEST);
    }

    private void buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(new Color(13, 27, 42));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        tfSearch = makeField();

        JLabel lbSearch = new JLabel("Search:");
        lbSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbSearch.setForeground(new Color(255, 180, 50));

        JButton btnSearch  = makeButton("Search",   new Color(255, 180, 50), new Color(13, 27, 42));
        JButton btnShowAll = makeButton("Show All", new Color(27, 38, 59),   new Color(230, 241, 255));

        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(new Color(13, 27, 42));
        searchBar.add(lbSearch,  BorderLayout.WEST);
        searchBar.add(tfSearch,  BorderLayout.CENTER);
        searchBar.add(btnSearch, BorderLayout.EAST);

        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(new Color(13, 27, 42));
        searchRow.add(searchBar,  BorderLayout.CENTER);
        searchRow.add(btnShowAll, BorderLayout.EAST);
        tablePanel.add(searchRow, BorderLayout.NORTH);

        btnSearch .addActionListener(e -> onSearch());
        btnShowAll.addActionListener(e -> refreshTable(fileIO.readAll()));
        tfSearch  .addActionListener(e -> onSearch());

        
        String[] cols = {"Account ID", "Holder Name", "Email", "Phone", "Type", "Balance (BDT)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(new Color(230, 241, 255));
        table.setBackground(new Color(13, 27, 42));
        table.setGridColor(new Color(27, 38, 59));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(65, 188, 170).darker());
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(new Font("SansSerif", Font.BOLD, 13));
        hdr.setBackground(new Color(27, 38, 59));
        hdr.setForeground(new Color(255, 180, 50));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? new Color(13, 27, 42) : new Color(20, 35, 55));
                    setForeground(new Color(230, 241, 255));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onRowSelected(); }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(13, 27, 42));
        scroll.setBackground(new Color(13, 27, 42));
        tablePanel.add(scroll, BorderLayout.CENTER);

        JLabel statusLabel = new JLabel("  Tip: Click a row to select it for editing.");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(140, 165, 190));
        tablePanel.add(statusLabel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
    }

    //  ACTION HANDLERS

    void onSave() {
        String id     = tfId.getText().trim();
        String name   = tfName.getText().trim();
        String email  = tfEmail.getText().trim();
        String phone  = tfPhone.getText().trim();
        String type   = (String) cbType.getSelectedItem();
        String balStr = tfBalance.getText().trim();

        //  all fields required
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || balStr.isEmpty()) {
            showError("All fields are required. Please fill in every field.");
            return;
        }
        // exactly 8 digits
        if (!id.matches("\\d{8}")) {
            showError("Account ID must be exactly 8 digits (numbers only).");
            return;
        }
        double balance;
        try {
            balance = Double.parseDouble(balStr);
            if (balance < 0) { showError("Balance must be a non-negative number."); return; }
        } catch (NumberFormatException ex) {
            showError("Balance must be a valid number.");
            return;
        }

        BankAccount acc = new BankAccount(id, name, type, balance, email, phone);

        if (editingId == null) {
            // CREATE
            BankAccountFileIO.createFileIfNotExists();
            // No duplicate IDs
            if (BankAccountFileIO.idExists(id)) {
                showError("Account ID " + id + " already exists! Duplicate IDs are not allowed.");
                return;
            }
            BankAccountFileIO.addAccount(acc);
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            // UPDATE
            boolean ok = BankAccountFileIO.updateAccount(acc);
            if (!ok) { showError("Update failed. Record not found."); return; }
            JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        clearForm();
        refreshTable(fileIO.readAll());
    }

    void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a row from the table to delete.");
            return;
        }
        String id   = tableModel.getValueAt(row, 0).toString();
        String name = tableModel.getValueAt(row, 1).toString();

        //Confirmation dialog before deleting
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this account?\n\nID: " + id + "\nName: " + name,
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            BankAccountFileIO.deleteAccount(id);
            JOptionPane.showMessageDialog(this, "Account deleted successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable(fileIO.readAll());
        }
    }

    void onSearch() {
        String kw = tfSearch.getText().trim();
        if (kw.isEmpty()) {
            showError("Please enter an Account ID or Name to search.");
            return;
        }
        // Search by ID or Name
        Object[][] results = BankAccountFileIO.searchAccounts(kw);
        if (results.length == 0) {
            JOptionPane.showMessageDialog(this, "No records found for: " + kw,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            refreshTable(results);
        }
    }

    void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        // Populate all fields for editing
        // Columns: 0=ID, 1=Name, 2=Email, 3=Phone, 4=Type, 5=Balance
        tfId     .setText(tableModel.getValueAt(row, 0).toString());
        tfName   .setText(tableModel.getValueAt(row, 1).toString());
        tfEmail  .setText(tableModel.getValueAt(row, 2).toString());
        tfPhone  .setText(tableModel.getValueAt(row, 3).toString());
        cbType   .setSelectedItem(tableModel.getValueAt(row, 4).toString());
        tfBalance.setText(tableModel.getValueAt(row, 5).toString());
        tfId.setEditable(false);
        editingId = tfId.getText().trim();
    }

    void clearForm() {
        tfId.setText(""); tfId.setEditable(true);
        tfName.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfBalance.setText("");
        cbType.setSelectedIndex(0);
        editingId = null;
        table.clearSelection();
    }


    void refreshTable(Object[][] data) {
        tableModel.setRowCount(0);
        for (int i = 0; i < data.length; i++) {
            // toRow() order: [0]ID [1]Name [2]Email [3]Phone [4]Type [5]Balance
            tableModel.addRow(new Object[]{
                data[i][0],
                data[i][1],
                data[i][2],
                data[i][3],
                data[i][4],
                String.format("%.2f", data[i][5])
            });
        }
    }


    //  UI HELPERS
    

    private JLabel makeSection(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(255, 180, 50));
        return l;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(new Color(230, 241, 255));
        return l;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(new Color(27, 38, 59));
        f.setForeground(new Color(230, 241, 255));
        f.setCaretColor(new Color(65, 188, 170));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(65, 188, 170).darker(), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return f;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 38));
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}