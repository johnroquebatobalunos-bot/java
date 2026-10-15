# School Suggestion System

A Java 17 desktop application built with JavaFX and MySQL JDBC. The GUI is written entirely in Java without FXML.

## 1. Project Structure

```text
school-suggestion-system/
├── pom.xml
├── README.md
├── sql/
│   └── schema.sql
└── src/main/java/
    ├── Main.java
    ├── dao/
    │   ├── PasswordHasher.java
    │   ├── SuggestionDAO.java
    │   └── UserDAO.java
    ├── db/DatabaseConnection.java
    ├── model/
    │   ├── Suggestion.java
    │   └── User.java
    └── ui/
        ├── AdminDashboard.java
        ├── LoginView.java
        ├── RegisterView.java
        └── StudentDashboard.java
```

## 2. Technologies

- Java 17 (JDK)
- JavaFX 17 (GUI)
- Apache NetBeans (IDE)
- Maven (dependency management)
- MySQL Server and JDBC (database)

## 3. Database Setup

1. Start MySQL Server.
2. Open PowerShell in the project directory and run:

```bash
mysql -u root -p < sql/schema.sql
```

Alternatively, execute `sql/schema.sql` in MySQL Workbench.

This creates `school_suggestion_db`, the required tables, a cascading foreign key, and the default administrator account.

**Admin login:** `admin`  
**Password:** `admin123`

Default connection:

```text
URL: jdbc:mysql://localhost:3306/school_suggestion_db
Username: root
Password: (empty)
```

For a custom MySQL account, configure these PowerShell environment variables before running the application:

```powershell
$env:SCHOOL_DB_USER = "root"
$env:SCHOOL_DB_PASSWORD = "your-mysql-password"
$env:SCHOOL_DB_URL = "jdbc:mysql://localhost:3306/school_suggestion_db"
```

`SCHOOL_DB_URL` is optional. Restart NetBeans after setting variables if you run the project through the IDE.

## 4. NetBeans Setup and GUI Execution

Install JDK 17, Apache NetBeans, MySQL Server, and Maven. Open NetBeans, select **File → Open Project**, and choose the folder containing `pom.xml`. Allow Maven to download the dependencies.

To launch the GUI using PowerShell, run:

```powershell
cd "$HOME\Desktop\java"

$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'

$env:Path = "$env:JAVA_HOME\bin;$HOME\.maven\maven-3.10.0\bin;$env:Path"

mvn javafx:run
```

**Important:** The folder must contain `pom.xml`. If your project is inside `Desktop\java\school-suggestion-system`, navigate to that folder instead.

The `pom.xml` should declare JavaFX Controls 17, MySQL Connector/J, and the JavaFX Maven Plugin. Maven handles dependencies and JavaFX launch configuration. For a manually configured, non-Maven project, add the JavaFX SDK and MySQL connector JARs, then use:

```text
--module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls
```

## 5. System Features

**Student:**
- Register, log in, and log out.
- Submit suggestions with a subject and message.
- View only their own suggestion history.
- Cannot view other students' suggestions or delete suggestions through the application.

**Administrator:**
- Log in to the admin dashboard.
- View all student suggestions.
- Refresh the list.
- Delete selected suggestions after confirmation.

**Validation:** Required fields, matching passwords, and unique usernames are checked during registration.

## 6. Testing

1. Start MySQL and execute `sql/schema.sql`.
2. Run `mvn javafx:run`.
3. Log in as `admin` using `admin123`.
4. Test viewing, refreshing, and deleting suggestions.
5. Register a student and test blank fields, mismatched passwords, and duplicate usernames.
6. Submit a suggestion and verify it appears in the student's history and admin dashboard.
7. Log in as another student and verify that the first student's history remains private.

## 7. Security

- Student passwords use PBKDF2WithHmacSHA256 with a fresh random salt.
- SQL statements use `PreparedStatement`.
- Database resources use try-with-resources.
- Student and administrator permissions are separated.
- Suggestion deletion requires administrator confirmation.

## 8. Troubleshooting

If the GUI or database fails to start:

- Check that Java 17 is active using `java -version`, `javac -version`, and `mvn -version`.
- Confirm Maven is available in the configured path.
- Make sure you are in the folder containing `pom.xml`.
- Ensure MySQL is running and the database script executed successfully.
- Verify your database credentials and Maven dependencies.

**Project Summary:** A JavaFX desktop application that allows students to submit school suggestions and administrators to review and delete them using MySQL.
