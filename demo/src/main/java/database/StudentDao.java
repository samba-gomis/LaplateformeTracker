package database;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// StudentDAO (Data Access Object) - Contains all SQL queries to interact with the "student" table in the database
// Uses PreparedStatements to prevent SQL injection attacks

public class StudentDao {

    // Connection retrieved via the Singleton
    private final Connection connection;

    public StudentDao() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // ADD : Insert a new student
    // Inserts a new student into the database
    // return true if the insertion succeeded, false otherwise

    public boolean addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, age, grade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setInt(3, student.getAge());
            stmt.setDouble(4, student.getGrade());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error while adding student: " + e.getMessage());
            return false;
        }
    }

    // READ : Retrieve all students
    // Retrieves all students stored in the database
    // return List of all students, ordered by last name then first name

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY last_name, first_name";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error while retrieving students: " + e.getMessage());
        }

        return students;
    }

    // READ : Find a student by ID
    // Searches for a student by their unique identifier
    // param id The student's ID
    // return The matching Student object, or null if not found

    public Student getStudentById(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error while searching for student (id=" + id + "): " + e.getMessage());
        }

        return null; // Student not found
    }

    // UPDATE : Update a student
    // Updates an existing student's information in the database
    // param student The student with updated information (must have a valid id)
    // return true if the update succeeded, false otherwise

    public boolean updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, age = ?, grade = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setInt(3, student.getAge());
            stmt.setDouble(4, student.getGrade());
            stmt.setInt(5, student.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.err.println("No student found with id: " + student.getId());
                return false;
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error while updating student: " + e.getMessage());
            return false;
        }
    }

    // DELETE : Remove a student
    // Permanently deletes a student from the database
    // param id The ID of the student to delete
    // return true if the deletion succeeded, false otherwise

    // FIX : removed 'static' keyword — a static method cannot access
    // the non-static field 'connection', which caused a compilation error
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.err.println("No student found with id: " + id);
                return false;
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error while deleting student (id=" + id + "): " + e.getMessage());
            return false;
        }
    }

    // HELPER : Map a ResultSet row to a Student object
    // Private utility method: converts a SQL row into a Student object
    // Centralised here to avoid code duplication across methods

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getInt("age"),
            rs.getDouble("grade")
        );
    }
}