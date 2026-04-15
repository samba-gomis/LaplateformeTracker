public class AddStudentController {

    private StudentDAO studentDAO;

    public AddStudentController() {
        this.studentDAO = new StudentDAO();
    }

    public void addStudent(String firstName, String lastName, int age, double grade) {

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
            Student student = new Student(firstName, lastName, age, grade);
            studentDAO.addStudent(student);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'étudiant");
        }
    }
}