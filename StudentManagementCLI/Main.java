import java.io.*;
import java.nio.file.*;
import java.util.*;

class Student {
    String id, name, branch;
    int semester;
    double cgpa;

    Student(String id, String name, String branch, int semester, double cgpa) {
        this.id = id;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.cgpa = cgpa;
    }

    String toCsv() {
        return String.join(",", id, name, branch, String.valueOf(semester), String.valueOf(cgpa));
    }

    static Student fromCsv(String line) {
        String[] a = line.split(",", -1);
        return new Student(a[0], a[1], a[2], Integer.parseInt(a[3]), Double.parseDouble(a[4]));
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | Sem %d | CGPA %.2f", id, name, branch, semester, cgpa);
    }
}

class StudentService {
    private final Path file = Paths.get("students.csv");
    private final Map<String, Student> map = new LinkedHashMap<>();

    StudentService() {
        load();
    }

    void add(Student s) {
        map.put(s.id, s);
        save();
    }

    boolean update(String id, Student s) {
        if (!map.containsKey(id)) return false;
        map.put(id, s);
        save();
        return true;
    }

    boolean delete(String id) {
        Student r = map.remove(id);
        if (r != null) {
            save();
            return true;
        }
        return false;
    }

    Student get(String id) {
        return map.get(id);
    }

    List<Student> list() {
        return new ArrayList<>(map.values());
    }

    List<Student> searchByName(String q) {
        q = q.toLowerCase();
        List<Student> out = new ArrayList<>();
        for (Student s : map.values())
            if (s.name.toLowerCase().contains(q))
                out.add(s);
        return out;
    }

    // Made private to avoid "overridable method call in constructor" warning
    private void load() {
        if (Files.notExists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                Student s = Student.fromCsv(line);
                map.put(s.id, s);
            }
        } catch (Exception e) {
            System.err.println("Load failed: " + e.getMessage());
        }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(file))) {
            for (Student s : map.values())
                pw.println(s.toCsv());
        } catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentService svc = new StudentService();

        while (true) {
            System.out.println("\n== Student Management ==");
            System.out.println("1. Add  2. Update  3. Delete  4. Get  5. List  6. SearchName  0. Exit");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();

            switch (ch) {
                case "1" -> {
                    Student s = read(sc);
                    svc.add(s);
                    System.out.println("Added.");
                }
                case "2" -> {
                    System.out.print("ID to update: ");
                    String id = sc.nextLine().trim();
                    Student s = read(sc);
                    s.id = id;
                    System.out.println(svc.update(id, s) ? "Updated." : "ID not found.");
                }
                case "3" -> {
                    System.out.print("ID to delete: ");
                    System.out.println(svc.delete(sc.nextLine().trim()) ? "Deleted." : "ID not found.");
                }
                case "4" -> {
                    System.out.print("ID: ");
                    Student s = svc.get(sc.nextLine().trim());
                    System.out.println(s == null ? "Not found" : s);
                }
                case "5" -> {
                    for (Student s : svc.list()) System.out.println(s);
                }
                case "6" -> {
                    System.out.print("Name contains: ");
                    for (Student s : svc.searchByName(sc.nextLine())) System.out.println(s);
                }
                case "0" -> {
                    System.out.println("Bye");
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    static Student read(Scanner sc) {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Branch: ");
        String branch = sc.nextLine();

        int sem;
        while (true) {
            System.out.print("Semester: ");
            String semInput = sc.nextLine();
            try {
                sem = Integer.parseInt(semInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for semester.");
            }
        }

        double cgpa;
        while (true) {
            System.out.print("CGPA: ");
            String cgpaInput = sc.nextLine();
            try {
                cgpa = Double.parseDouble(cgpaInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for CGPA.");
            }
        }
        return new Student(id, name, branch, sem, cgpa);
    }
}
