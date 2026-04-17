package controller;

import database.StudentDao;
import model.Student;

public class EditStudentController {

    private StudentDao StudentDao;

    public EditStudentController() {
        this.StudentDao = new StudentDao();
    }

    public void updateStudent(int id, String firstName, String lastName, int age, double grade) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("Le prénom est obligatoire");
        }

        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        if (age < 0) {
            throw new IllegalArgumentException("Âge invalide");
        }

        if (grade < 0 || grade > 20) {
            throw new IllegalArgumentException("La note doit être entre 0 et 20");
        }

        try {
            Student student = new Student(id, firstName, lastName, age, grade);
            StudentDao.updateStudent(student);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la modification");
        }
    }

    public void deleteStudent(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        try {
            StudentDao.deleteStudent(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression");
        }
    }
}
