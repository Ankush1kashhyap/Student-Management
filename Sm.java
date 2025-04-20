import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Student {
    private String id;
    private String name;
    private int age;
    private String course;
    private String email;
    private String phone;

    public Student(String id, String name, int age, String course, String email, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return id + "," + name + "," + age + "," + course + "," + email + "," + phone;
    }
}

public class StudentManagementSystem extends JFrame {
    private List<Student> students;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JTextField idField, nameField, ageField, courseField, emailField, phoneField, searchField;
    private JButton addButton, updateButton, deleteButton, clearButton, searchButton;
    private final String DATA_FILE = "students.txt";

    public StudentManagementSystem() {
        students = new ArrayList<>();
        loadStudentsFromFile();

        // Set up the main frame
        setTitle("Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        createComponents();

        // Add components to the frame
        addComponents();

        // Display all students
        displayAllStudents();

        setVisible(true);
    }

    private void createComponents() {
        // Input fields
        idField = new JTextField(15);
        nameField = new JTextField(15);
        ageField = new JTextField(15);
        courseField = new JTextField(15);
        emailField = new JTextField(15);
        phoneField = new JTextField(15);
        searchField = new JTextField(15);

        // Buttons
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        searchButton = new JButton("Search");

        // Table
        String[] columnNames = {"ID", "Name", "Age", "Course", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add action listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchStudent());

        // Table selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    ageField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    courseField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    emailField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    phoneField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                }
            }
        });
    }

    private void addComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(new JLabel("Search by ID or Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Combine form and button panels
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.add(formPanel, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Add all components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addStudent() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String course = courseField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || course.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if student ID already exists
            for (Student student : students) {
                if (student.getId().equals(id)) {
                    JOptionPane.showMessageDialog(this, "Student ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Student student = new Student(id, name, age, course, email, phone);
            students.add(student);
            saveStudentsToFile();
            displayAllStudents();
            clearFields();
            JOptionPane.showMessageDialog(this, "Student added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String course = courseField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || course.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Find the student to update
            for (Student student : students) {
                if (student.getId().equals(id)) {
                    student.setName(name);
                    student.setAge(age);
                    student.setCourse(course);
                    student.setEmail(email);
                    student.setPhone(phone);
                    break;
                }
            }

            saveStudentsToFile();
            displayAllStudents();
            clearFields();
            JOptionPane.showMessageDialog(this, "Student updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete student with ID: " + id + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            students.removeIf(student -> student.getId().equals(id));
            saveStudentsToFile();
            displayAllStudents();
            clearFields();
            JOptionPane.showMessageDialog(this, "Student deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchStudent() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            displayAllStudents();
            return;
        }

        tableModel.setRowCount(0); // Clear the table

        for (Student student : students) {
            if (student.getId().toLowerCase().contains(searchText) || 
                student.getName().toLowerCase().contains(searchText)) {
                Object[] rowData = {
                    student.getId(),
                    student.getName(),
                    student.getAge(),
                    student.getCourse(),
                    student.getEmail(),
                    student.getPhone()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void displayAllStudents() {
        tableModel.setRowCount(0); // Clear the table

        for (Student student : students) {
            Object[] rowData = {
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getCourse(),
                student.getEmail(),
                student.getPhone()
            };
            tableModel.addRow(rowData);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        courseField.setText("");
        emailField.setText("");
        phoneField.setText("");
        studentTable.clearSelection();
    }

    private void saveStudentsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Student student : students) {
                writer.println(student.toString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String id = parts[0];
                    String name = parts[1];
                    int age = Integer.parseInt(parts[2]);
                    String course = parts[3];
                    String email = parts[4];
                    String phone = parts[5];
                    students.add(new Student(id, name, age, course, email, phone));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading student data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementSystem());
    }
}