# School Suggestion System

A Java 17 desktop application built with JavaFX and MySQL JDBC. The UI is written entirely in Java; there are no FXML files.

## Project Structure

```text
school-suggestion-system/
|-- pom.xml
|-- README.md
|-- sql/
|   `-- schema.sql
`-- src/main/java/
    |-- Main.java
    |-- dao/
    |   |-- PasswordHasher.java
    |   |-- SuggestionDAO.java
    |   `-- UserDAO.java
    |-- db/
    |   `-- DatabaseConnection.java
    |-- model/
    |   |-- Suggestion.java
    |   `-- User.java
    `-- ui/
        |-- AdminDashboard.java
        |-- LoginView.java
        |-- RegisterView.java
        `-- StudentDashboard.java
```

## Database Setup

1. Install and start MySQL Server.
2. Open `sql/schema.sql` in MySQL Workbench or run it from the MySQL command-line client:

   ```text
   mysql -u root -p < sql/schema.sql
   ```

3. The script creates `school_suggestion_db`, both tables, the cascading foreign key, and the administrator account. Seeded credentials are `admin` / `admin123`. The seed password is stored as a PBKDF2 hash.
4. The default database connection is `jdbc:mysql://localhost:3306/school_suggestion_db`, username `root`, and an empty password. For any other MySQL account, set these environment variables before starting NetBeans:
   - `SCHOOL_DB_URL` (optional; overrides the complete JDBC URL)
   - `SCHOOL_DB_USER`
   - `SCHOOL_DB_PASSWORD`

   Example PowerShell values for a local MySQL account:

   ```powershell
   $env:SCHOOL_DB_USER = "root"
   $env:SCHOOL_DB_PASSWORD = "your-mysql-password"
   ```

## NetBeans Setup (JDK 17)

1. Install a JDK 17 distribution, Apache NetBeans, and MySQL Server. In NetBeans, open **Tools > Java Platforms** and confirm that JDK 17 is registered.
2. Choose **File > Open Project** and select the project folder containing `pom.xml`. NetBeans recognizes it as a Maven project.
3. The `pom.xml` already declares JavaFX Controls 17 and MySQL Connector/J. Allow Maven to resolve dependencies. This is the preferred setup; the JavaFX SDK and connector JAR are not manually duplicated in a Maven project.
4. To configure the JavaFX SDK manually for a non-Maven NetBeans Java project, download and extract the JavaFX 17 SDK matching your operating system. In **Tools > Libraries > New Library**, create a JavaFX library and add the JAR files from the SDK's `lib` folder. Add that library to the project's compile-time libraries. Download `mysql-connector-j` from MySQL, then add its JAR through **Project Properties > Libraries > Compile > Add JAR/Folder**. With this manually configured project, use the VM options below.
5. For a manual JavaFX launch, open **Project Properties > Run** and enter the VM options below, replacing the SDK path with the actual extracted path:

   ```text
   --module-path "C:\javafx-sdk-17\lib" --add-modules javafx.controls
   ```

   For this Maven project, run the `javafx:run` goal instead; the JavaFX Maven plugin configures the module path from the declared dependencies. In NetBeans, use the project **Run** action or run `mvn javafx:run` from the project directory.
6. If your MySQL username or password differs from the defaults, set the `SCHOOL_DB_USER` and `SCHOOL_DB_PASSWORD` environment variables before opening NetBeans, then restart NetBeans so the variables are inherited.

## Run and Test

1. Confirm MySQL is running and `sql/schema.sql` completed successfully.
2. Run the project from NetBeans or execute `mvn javafx:run` in a terminal with JDK 17 and Maven available.
3. Sign in as the administrator with username `admin` and password `admin123`. Confirm the table lists all student suggestions, **Refresh** reloads the list, and **Delete selected** requires confirmation and then removes the selected row.
4. Sign out and choose **Create an account**. Register a student with a full name, unique username, and matching passwords. Try blank fields, mismatched passwords, and the same username again to verify validation.
5. Sign in with the student account. Submit a suggestion with both a subject and message; verify it appears in that student's history and in the administrator table. Sign out and sign in as another student to confirm the first student's history is not visible.

Student passwords are stored using PBKDF2WithHmacSHA256 with a fresh random salt. All application SQL statements use `PreparedStatement`; connections and statements are closed with try-with-resources.
