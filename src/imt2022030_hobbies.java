import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

public class imt2022030_hobbies {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/hobbies";

    static final String USER = "root";
    static final String PASSWORD = "admin";

    public static void execLines(String pathx, Connection conn, Statement stmt) {
        System.out.println("Inserting data");
        String lines = "";
        Path path = Paths.get(pathx);
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lines = new String(bytes);
        String[] arrlines = lines.split(";");
        for (String line : arrlines) {
            if (!line.trim().isEmpty()) {
                try {
                    stmt.executeUpdate(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Helper method to hash passwords using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet exec = null;
        Scanner sc = new Scanner(System.in);
        String loggedInRole = null;
        String loggedInUser = null;
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            execLines("C:\\Users\\Hemant Gupta\\Downloads\\AK\\imt2022030_hobbies_JDBC_Project\\sql\\populate.sql",conn, stmt);

            // LOGIN/SIGNUP/RESET LOGIC
            while (true) {
                System.out.println("1. Login\n2. Signup\n3. Reset Password\n4. Exit");
                System.out.print("Choose option: ");
                int authOpt = sc.nextInt();
                sc.nextLine();
                if (authOpt == 1) {
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter password: ");
                    String password = sc.nextLine();
                    String hash = hashPassword(password);
                    String sql = "SELECT Role FROM Users WHERE Username='" + username + "' AND PasswordHash='" + hash + "'";
                    exec = stmt.executeQuery(sql);
                    if (exec.next()) {
                        loggedInRole = exec.getString("Role");
                        loggedInUser = username;
                        System.out.println("Login successful as " + loggedInRole);
                        break;
                    } else {
                        System.out.println("Invalid credentials. Try again.");
                    }
                } else if (authOpt == 2) {
                    System.out.print("Choose username: ");
                    String username = sc.nextLine();
                    System.out.print("Choose password: ");
                    String password = sc.nextLine();
                    System.out.print("Role (admin/user): ");
                    String role = sc.nextLine();
                    if (!role.equals("admin") && !role.equals("user")) {
                        System.out.println("Role must be 'admin' or 'user'.");
                        continue;
                    }
                    String hash = hashPassword(password);
                    String sql = "INSERT INTO Users (Username, PasswordHash, Role) VALUES ('" + username + "', '" + hash + "', '" + role + "')";
                    try {
                        stmt.executeUpdate(sql);
                        conn.commit();
                        System.out.println("Signup successful. You can now login.");
                    } catch (SQLException e) {
                        System.out.println("Signup failed. Username may already exist.");
                        conn.rollback();
                    }
                } else if (authOpt == 3) {
                    System.out.print("Enter your username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter your new password: ");
                    String newPassword = sc.nextLine();
                    String hash = hashPassword(newPassword);
                    String sql = "UPDATE Users SET PasswordHash='" + hash + "' WHERE Username='" + username + "'";
                    try {
                        int updated = stmt.executeUpdate(sql);
                        if (updated > 0) {
                            conn.commit();
                            System.out.println("Password reset successful. You can now login with your new password.");
                        } else {
                            System.out.println("Username not found. Password reset failed.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Password reset failed due to a database error.");
                        conn.rollback();
                    }
                } else {
                    System.out.println("Exiting.");
                    sc.close();
                    return;
                }
            }

            while (true) {
                System.out.println("""
                        1. List Hobbies
                        2. List people
                        3. Fetch PersonHobbies 
                        4. Insert into PersonHobbies 
                        5. List all Equipment and HobbyId
                        6. Update People table entry
                        7. Delete entry from HobbyEquipment 
                        8. Find hobbies of particular person
                        9. List all hobbies with the equipment
                        10. List all hobbies and the number of equipment items required for each
                        11. Count the number of people who have each hobby
                        12. Insert into HobbyEquipment and update hobby id
                        13. Exit
                        14. Search/Filter
                        """);
                System.out.print("Enter option: ");
                int option = sc.nextInt();
                
                if (option == 7) {
                    if (!"admin".equals(loggedInRole)) {
                        System.out.println("Only admin users can delete equipment.");
                        continue;
                    }
                }

                if (option == 1) {
                    String sql_query = "SELECT * FROM Hobbies;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                        System.out.println("Category: " + exec.getString("Category"));
                    }
                } else if (option == 2) {
                    String sql_query = "SELECT * FROM People;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("PersonID: " + exec.getInt("PersonID"));
                        System.out.println("Name: " + exec.getString("Name"));
                        System.out.println("Age: " + exec.getInt("Age"));
                        System.out.println("Gender: " + exec.getString("Gender"));
                    }

                } else if (option == 3) {
                    String sql_query = "SELECT * FROM PersonHobbies;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("PersonID: " + exec.getInt("PersonID"));
                    }
                } else if (option == 4) {
                    int person_id;
                    int hoby_id;
                    int temp;
                    System.out.println("Enter PersonID:");
                    person_id = sc.nextInt();
                    System.out.println("Enter HobbyID:");
                    hoby_id = sc.nextInt();
                    String sql_query = "INSERT INTO PersonHobbies (PersonID, HobbyID) VALUES (" + person_id + ","
                            + hoby_id
                            + ");";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                } else if (option == 5) {
                    String sql_query = "SELECT * FROM HobbyEquipment;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("EquipmentName: " + exec.getString("EquipmentName"));
                    }

                } else if (option == 6) {
                    int person_id;
                    int age;
                    int temp;
                    System.out.println("Enter PersonID:");
                    person_id = sc.nextInt();
                    System.out.println("Enter New Age:");
                    age = sc.nextInt();
                    String name, gender;
                    System.out.println("Enter New name:");
                    name = sc.next();
                    System.out.println("Enter Gender:");
                    gender = sc.next();

                    String sql_query = "UPDATE People SET Name = '" + name + "', Age = " + age + ", Gender = '"
                            + gender + "' WHERE PersonID = '" + person_id + "';";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                } else if (option == 7) {
                    int temp;
                    String name;
                    int hoby_id;

                    System.out.println("Enter HobbyID:");
                    hoby_id = sc.nextInt();

                    System.out.println("Enter EquipmentName:");
                    sc.nextLine();
                    name = sc.nextLine();
                    String sql_query = "DELETE FROM HobbyEquipment WHERE HobbyID = " + hoby_id
                            + " AND EquipmentName = '"
                            + name + "';";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Failed");
                    }
                }

                else if (option == 8) {
                    System.out.println("Enter Name");
                    String name = sc.next();
                    String sql_query = "SELECT p.Name, h.HobbyName FROM People p INNER JOIN PersonHobbies ph ON p.PersonID = ph.PersonID INNER JOIN Hobbies h ON ph.HobbyID = h.HobbyID WHERE p.Name = '"
                            + name + "';";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("Name: " + exec.getString("Name"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                    }
                }

                else if (option == 9) {

                    String sql_query = "SELECT h.HobbyName, he.EquipmentName FROM Hobbies h LEFT JOIN HobbyEquipment he ON h.HobbyID = he.HobbyID;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("EquipmentName: " + exec.getString("EquipmentName"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                    }
                } else if (option == 10) {
                    String sql_query = "SELECT h.HobbyName, COUNT(he.EquipmentName) AS NumberOfEquipment FROM Hobbies h LEFT JOIN HobbyEquipment he ON h.HobbyID = he.HobbyID GROUP BY h.HobbyName;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                        System.out.println("NumberOfEquipment: " + exec.getInt("NumberOfEquipment"));
                    }
                } else if (option == 11) {

                    String sql_query = "SELECT h.HobbyName, COUNT(*) AS NumberOfPeople FROM Hobbies h INNER JOIN PersonHobbies ph ON h.HobbyID = ph.HobbyID GROUP BY h.HobbyName;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                        System.out.println("NumberOfPeople: " + exec.getInt("NumberOfPeople"));
                    }
                } else if (option == 12) {
                    int temp;
                    System.out.println("Enter Hobby Id:");
                    int id = sc.nextInt();
                    System.out.println("Enter Equipment name:");
                    String equipment = sc.next();

                    String sql_query = "INSERT INTO HobbyEquipment (HobbyID, EquipmentName) VALUES (" + id + ", '"
                            + equipment + "');";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Failed");
                    }

                    System.out.println("Enter the new Hobby Id:");
                    id = sc.nextInt();

                    sql_query = "UPDATE HobbyEquipment SET HobbyID = " + id + " WHERE EquipmentName = '" + equipment
                            + "';";
                    temp = stmt.executeUpdate(sql_query);

                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                    conn.commit();
                } else if (option == 14) {
                    System.out.println("Search/Filter Menu:");
                    System.out.println("1. Search Hobbies by Name");
                    System.out.println("2. Filter Hobbies by Category");
                    System.out.println("3. Search People by Name");
                    System.out.println("4. Filter People by Age");
                    System.out.println("5. Filter People by Age Range");
                    System.out.println("6. Filter Hobbies by Multiple Categories");
                    System.out.println("7. Combined Advanced Search");
                    System.out.println("8. Back to Main Menu");
                    System.out.print("Choose option: ");
                    int searchOpt = sc.nextInt();
                    sc.nextLine();
                    if (searchOpt == 1) {
                        System.out.print("Enter hobby name to search: ");
                        String hobbyName = sc.nextLine();
                        String sql_query = "SELECT * FROM Hobbies WHERE HobbyName LIKE '%" + hobbyName + "%'";
                        exec = stmt.executeQuery(sql_query);
                        boolean found = false;
                        while (exec.next()) {
                            found = true;
                            System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                            System.out.println("HobbyName: " + exec.getString("HobbyName"));
                            System.out.println("Category: " + exec.getString("Category"));
                        }
                        if (!found) System.out.println("No hobbies found with that name.");
                    } else if (searchOpt == 2) {
                        System.out.print("Enter category to filter: ");
                        String category = sc.nextLine();
                        String sql_query = "SELECT * FROM Hobbies WHERE Category LIKE '%" + category + "%'";
                        exec = stmt.executeQuery(sql_query);
                        boolean found = false;
                        while (exec.next()) {
                            found = true;
                            System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                            System.out.println("HobbyName: " + exec.getString("HobbyName"));
                            System.out.println("Category: " + exec.getString("Category"));
                        }
                        if (!found) System.out.println("No hobbies found in that category.");
                    } else if (searchOpt == 3) {
                        System.out.print("Enter person name to search: ");
                        String personName = sc.nextLine();
                        String sql_query = "SELECT * FROM People WHERE Name LIKE '%" + personName + "%'";
                        exec = stmt.executeQuery(sql_query);
                        boolean found = false;
                        while (exec.next()) {
                            found = true;
                            System.out.println("PersonID: " + exec.getInt("PersonID"));
                            System.out.println("Name: " + exec.getString("Name"));
                            System.out.println("Age: " + exec.getInt("Age"));
                            System.out.println("Gender: " + exec.getString("Gender"));
                        }
                        if (!found) System.out.println("No people found with that name.");
                    } else if (searchOpt == 4) {
                        System.out.print("Enter age to filter: ");
                        int age = sc.nextInt();
                        String sql_query = "SELECT * FROM People WHERE Age = " + age;
                        exec = stmt.executeQuery(sql_query);
                        boolean found = false;
                        while (exec.next()) {
                            found = true;
                            System.out.println("PersonID: " + exec.getInt("PersonID"));
                            System.out.println("Name: " + exec.getString("Name"));
                            System.out.println("Age: " + exec.getInt("Age"));
                            System.out.println("Gender: " + exec.getString("Gender"));
                        }
                        if (!found) System.out.println("No people found with that age.");
                    } else if (searchOpt == 5) {
                        System.out.print("Enter minimum age: ");
                        int minAge = sc.nextInt();
                        System.out.print("Enter maximum age: ");
                        int maxAge = sc.nextInt();
                        String sql_query = "SELECT * FROM People WHERE Age BETWEEN " + minAge + " AND " + maxAge;
                        exec = stmt.executeQuery(sql_query);
                        boolean found = false;
                        while (exec.next()) {
                            found = true;
                            System.out.println("PersonID: " + exec.getInt("PersonID"));
                            System.out.println("Name: " + exec.getString("Name"));
                            System.out.println("Age: " + exec.getInt("Age"));
                            System.out.println("Gender: " + exec.getString("Gender"));
                        }
                        if (!found) System.out.println("No people found in that age range.");
                    } else if (searchOpt == 6) {
                        System.out.print("Enter categories (comma-separated): ");
                        String categoriesInput = sc.nextLine();
                        String[] categories = categoriesInput.split(",");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < categories.length; i++) {
                            String cat = categories[i].trim();
                            if (cat.isEmpty()) continue;
                            if (sb.length() > 0) sb.append(" OR ");
                            sb.append("Category LIKE '%" + cat + "%'");
                        }
                        if (sb.length() == 0) {
                            System.out.println("No valid categories entered.");
                        } else {
                            String sql_query = "SELECT * FROM Hobbies WHERE " + sb.toString();
                            exec = stmt.executeQuery(sql_query);
                            boolean found = false;
                            while (exec.next()) {
                                found = true;
                                System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                                System.out.println("HobbyName: " + exec.getString("HobbyName"));
                                System.out.println("Category: " + exec.getString("Category"));
                            }
                            if (!found) System.out.println("No hobbies found in those categories.");
                        }
                    } else if (searchOpt == 7) {
                        System.out.println("Combined Advanced Search:");
                        System.out.println("1. Hobbies");
                        System.out.println("2. People");
                        System.out.println("3. Combined Hobbies and People");
                        System.out.print("Choose (1 for Hobbies, 2 for People, 3 for Both): ");
                        int advType = sc.nextInt();
                        sc.nextLine();
                        if (advType == 1) {
                            // Hobbies advanced search
                            System.out.print("Enter hobby name (leave blank for any): ");
                            String hobbyName = sc.nextLine().trim();
                            System.out.print("Enter categories (comma-separated, leave blank for any): ");
                            String categoriesInput = sc.nextLine();
                            String[] categories = categoriesInput.split(",");
                            System.out.print("Sort by (1: HobbyName, 2: Category, 3: None): ");
                            int sortOpt = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Sort order (1: Ascending, 2: Descending): ");
                            int orderOpt = sc.nextInt();
                            sc.nextLine();
                            StringBuilder where = new StringBuilder();
                            if (!hobbyName.isEmpty()) {
                                where.append("HobbyName LIKE '%" + hobbyName + "%'");
                            }
                            StringBuilder catWhere = new StringBuilder();
                            for (String cat : categories) {
                                cat = cat.trim();
                                if (!cat.isEmpty()) {
                                    if (catWhere.length() > 0) catWhere.append(" OR ");
                                    catWhere.append("Category LIKE '%" + cat + "%'");
                                }
                            }
                            if (catWhere.length() > 0) {
                                if (where.length() > 0) where.append(" AND (");
                                else where.append("(");
                                where.append(catWhere.toString()).append(")");
                            }
                            String sql_query = "SELECT * FROM Hobbies";
                            if (where.length() > 0) sql_query += " WHERE " + where.toString();
                            if (sortOpt == 1) sql_query += " ORDER BY HobbyName";
                            else if (sortOpt == 2) sql_query += " ORDER BY Category";
                            if (sortOpt == 1 || sortOpt == 2) {
                                if (orderOpt == 2) sql_query += " DESC";
                                else sql_query += " ASC";
                            }
                            exec = stmt.executeQuery(sql_query);
                            boolean found = false;
                            while (exec.next()) {
                                found = true;
                                System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                                System.out.println("HobbyName: " + exec.getString("HobbyName"));
                                System.out.println("Category: " + exec.getString("Category"));
                            }
                            if (!found) System.out.println("No hobbies found with those filters.");
                        } else if (advType == 2) {
                            // People advanced search
                            System.out.print("Enter person name (leave blank for any): ");
                            String personName = sc.nextLine().trim();
                            System.out.print("Enter minimum age (or leave blank for any): ");
                            String minAgeStr = sc.nextLine().trim();
                            System.out.print("Enter maximum age (or leave blank for any): ");
                            String maxAgeStr = sc.nextLine().trim();
                            System.out.print("Sort by (1: Name, 2: Age, 3: None): ");
                            int sortOpt = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Sort order (1: Ascending, 2: Descending): ");
                            int orderOpt = sc.nextInt();
                            sc.nextLine();
                            StringBuilder where = new StringBuilder();
                            if (!personName.isEmpty()) {
                                where.append("Name LIKE '%" + personName + "%'");
                            }
                            if (!minAgeStr.isEmpty() && !maxAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("Age BETWEEN " + minAgeStr + " AND " + maxAgeStr);
                            } else if (!minAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("Age >= " + minAgeStr);
                            } else if (!maxAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("Age <= " + maxAgeStr);
                            }
                            String sql_query = "SELECT * FROM People";
                            if (where.length() > 0) sql_query += " WHERE " + where.toString();
                            if (sortOpt == 1) sql_query += " ORDER BY Name";
                            else if (sortOpt == 2) sql_query += " ORDER BY Age";
                            if (sortOpt == 1 || sortOpt == 2) {
                                if (orderOpt == 2) sql_query += " DESC";
                                else sql_query += " ASC";
                            }
                            exec = stmt.executeQuery(sql_query);
                            boolean found = false;
                            while (exec.next()) {
                                found = true;
                                System.out.println("PersonID: " + exec.getInt("PersonID"));
                                System.out.println("Name: " + exec.getString("Name"));
                                System.out.println("Age: " + exec.getInt("Age"));
                                System.out.println("Gender: " + exec.getString("Gender"));
                            }
                            if (!found) System.out.println("No people found with those filters.");
                        } else if (advType == 3) {
                            // Combined hobbies and people search
                            System.out.print("Enter hobby name (leave blank for any): ");
                            String hobbyName = sc.nextLine().trim();
                            System.out.print("Enter categories (comma-separated, leave blank for any): ");
                            String categoriesInput = sc.nextLine();
                            String[] categories = categoriesInput.split(",");
                            System.out.print("Enter person name (leave blank for any): ");
                            String personName = sc.nextLine().trim();
                            System.out.print("Enter minimum age (or leave blank for any): ");
                            String minAgeStr = sc.nextLine().trim();
                            System.out.print("Enter maximum age (or leave blank for any): ");
                            String maxAgeStr = sc.nextLine().trim();
                            System.out.print("Sort by (1: Person Name, 2: Hobby Name, 3: Age, 4: None): ");
                            int sortOpt = sc.nextInt();
                            sc.nextLine();
                            System.out.print("Sort order (1: Ascending, 2: Descending): ");
                            int orderOpt = sc.nextInt();
                            sc.nextLine();
                            StringBuilder where = new StringBuilder();
                            if (!hobbyName.isEmpty()) {
                                where.append("h.HobbyName LIKE '%" + hobbyName + "%'");
                            }
                            StringBuilder catWhere = new StringBuilder();
                            for (String cat : categories) {
                                cat = cat.trim();
                                if (!cat.isEmpty()) {
                                    if (catWhere.length() > 0) catWhere.append(" OR ");
                                    catWhere.append("h.Category LIKE '%" + cat + "%'");
                                }
                            }
                            if (catWhere.length() > 0) {
                                if (where.length() > 0) where.append(" AND (");
                                else where.append("(");
                                where.append(catWhere.toString()).append(")");
                            }
                            if (!personName.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("p.Name LIKE '%" + personName + "%'");
                            }
                            if (!minAgeStr.isEmpty() && !maxAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("p.Age BETWEEN " + minAgeStr + " AND " + maxAgeStr);
                            } else if (!minAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("p.Age >= " + minAgeStr);
                            } else if (!maxAgeStr.isEmpty()) {
                                if (where.length() > 0) where.append(" AND ");
                                where.append("p.Age <= " + maxAgeStr);
                            }
                            String sql_query = "SELECT p.PersonID, p.Name, p.Age, p.Gender, h.HobbyID, h.HobbyName, h.Category " +
                                    "FROM People p " +
                                    "JOIN PersonHobbies ph ON p.PersonID = ph.PersonID " +
                                    "JOIN Hobbies h ON ph.HobbyID = h.HobbyID";
                            if (where.length() > 0) sql_query += " WHERE " + where.toString();
                            if (sortOpt == 1) sql_query += " ORDER BY p.Name";
                            else if (sortOpt == 2) sql_query += " ORDER BY h.HobbyName";
                            else if (sortOpt == 3) sql_query += " ORDER BY p.Age";
                            if (sortOpt >= 1 && sortOpt <= 3) {
                                if (orderOpt == 2) sql_query += " DESC";
                                else sql_query += " ASC";
                            }
                            exec = stmt.executeQuery(sql_query);
                            boolean found = false;
                            while (exec.next()) {
                                found = true;
                                System.out.println("PersonID: " + exec.getInt("PersonID"));
                                System.out.println("Name: " + exec.getString("Name"));
                                System.out.println("Age: " + exec.getInt("Age"));
                                System.out.println("Gender: " + exec.getString("Gender"));
                                System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                                System.out.println("HobbyName: " + exec.getString("HobbyName"));
                                System.out.println("Category: " + exec.getString("Category"));
                                System.out.println("------------------------");
                            }
                            if (!found) System.out.println("No results found with those filters.");
                        } else {
                            System.out.println("Invalid option.");
                        }
                    } else {
                        // Back to main menu
                        continue;
                    }
                } else
                    break;
                conn.commit();
            }
            sc.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) { // Handle errors for JDBC
            se.printStackTrace();
            System.out.println("Rolling back data");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException se2) {
                System.out.println("Rollback failed");
                se2.printStackTrace();
            }
        } catch (Exception e) { // Handle errors for Class.forName
            e.printStackTrace();
        } finally { // finally block used to close resources regardless of whether an exception was
                    // thrown or not
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        System.out.println("End of Code");
    }

}