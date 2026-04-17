package controller;

import database.StudentDao;
import model.Student;

import java.util.List;

public class SearchController {

    private StudentDao StudentDao;

    public SearchController() {
        this.StudentDao = new StudentDao();
    }

    public Student searchById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        Student student = StudentDao.getStudentById(id);

        if (student == null) {
            throw new RuntimeException("Étudiant introuvable");
        }

        return student;
    }

    public List<Student> getAllStudents() {
        return StudentDao.getAllStudents();
    }
}
