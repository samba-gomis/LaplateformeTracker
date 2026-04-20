package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controller.MainController;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileManagement {

    //Exports

    public static void exportCSV(File file, List<Student> students) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("ID;FirstName;LastName;Age;Grade");
            for (Student s : students) {
                writer.println(s.getId() + ";" + s.getFirstName() + ";" +
                        s.getLastName() + ";" + s.getAge() + ";" + s.getGrade());
            }
        }
    }

    public static void exportJSON(File file, List<Student> students) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(students, writer);
        }
    }

    public static void exportXML(File file, List<Student> students) throws Exception {
        JAXBContext context = JAXBContext.newInstance(MainController.StudentListWrapper.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        MainController.StudentListWrapper wrapper = new MainController.StudentListWrapper();
        wrapper.setStudents(new ArrayList<>(students));
        marshaller.marshal(wrapper, file);
    }

    public static void exportHTML(File file, List<Student> students) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("<html><head><meta charset='UTF-8'><title>Students List</title></head><body>");
            writer.println("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            writer.println("<tr style='background-color: #eee;'><th>ID</th><th>Name</th><th>Nom</th><th>Age</th><th>Grade</th></tr>");
            for (Student s : students) {
                writer.println("<tr><td>" + s.getId() + "</td><td>" + s.getFirstName() +
                        "</td><td>" + s.getLastName() + "</td><td>" + s.getAge() +
                        "</td><td>" + s.getGrade() + "</td></tr>");
            }
            writer.println("</table></body></html>");
        }
    }

    //Imports

    public static List<Student> importCSV(File file) throws IOException {
        List<Student> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                list.add(new Student(data[1], data[2], Integer.parseInt(data[3]), Double.parseDouble(data[4])));
            }
        }
        return list;
    }

    public static List<Student> importJSON(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Student>>(){}.getType();
            return new Gson().fromJson(reader, listType);
        }
    }

    public static List<Student> importXML(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(MainController.StudentListWrapper.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        MainController.StudentListWrapper wrapper = (MainController.StudentListWrapper) unmarshaller.unmarshal(file);
        return wrapper.getStudents();
    }
}