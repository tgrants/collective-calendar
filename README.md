# Collective Calendar

## Docker

- `docker compose up`

## Windows

This is a Spring Boot MVC project.  
Follow these steps to set up the environment on Windows.

---

### 1. Java Setup

You need to download and install:

- JDK 25

Download here:
https://download.oracle.com/java/25/latest/jdk-25_windows-x64_bin.zip

After installation:
- Set `JAVA_HOME`
- Add Java to your system `PATH`

Verify installation:

java -version  
javac -version

---

#### Alternative (if you have admin rights)

If you have administrator rights, you can install Java using the official installer instead of the ZIP version.  
The installer will automatically configure environment variables for you.

Download here:
https://download.oracle.com/java/25/latest/jdk-25_windows-x64_bin.exe

---

### 2. Maven Setup

You need to download and install:

- Apache Maven 3.9.15

Download here:
https://dlcdn.apache.org/maven/maven-3/3.9.15/binaries/apache-maven-3.9.15-bin.zip

After installation:
- Set `MAVEN_HOME`
- Add Maven to your system `PATH`

Verify installation:

mvn -v

---

### 3. Run the Project

Navigate to the project folder and run:

mvn spring-boot:run

---

### 4. Open Application

Open in browser:

http://localhost:8080

---

### 5. Tech Stack

- Spring Boot MVC  
- Spring Security  
- Thymeleaf  
- Spring Data JPA  
- H2 Database  
- Lombok  

---

### 6. Useful Commands

Build project:
mvn clean install

Run project:
mvn spring-boot:run

---

### 7. Notes

- DevTools enables automatic restart on code changes  
- H2 database is in-memory (data resets on restart)  
- Templates are located in src/main/resources/templates
