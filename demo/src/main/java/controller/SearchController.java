package controller;

import dao.StudentDAO;
import model.Student;

import java.util.List;

public class SearchController {

    private StudentDAO studentDAO;

    public SearchController() {
        this.studentDAO = new StudentDAO();
    }

    public Student searchById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        Student student = studentDAO.getStudentById(id);

        if (student == null) {
            throw new RuntimeException("Étudiant introuvable");
        }

        return student;
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }
}
