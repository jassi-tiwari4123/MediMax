# OHMS — Complete Setup & Run Guide

## Step 1 — Prerequisites

Install these if not already present:

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 11+ | https://adoptium.net |
| Apache Tomcat | 9.x | https://tomcat.apache.org |
| MySQL Server | 8.x | https://dev.mysql.com/downloads |
| Maven | 3.8+ | https://maven.apache.org/download.cgi (or use IDE's built-in) |

---

## Step 2 — Database Setup

Open MySQL Workbench or terminal and run:

```sql
mysql -u root -p < docs/sql/schema.sql
```

This creates the `ohms_db` database, all 9 tables, seeds roles and departments,
and adds a default admin account.

**Then update the admin password hash:**
```sql
-- In MySQL, replace the placeholder hash with a real BCrypt hash
-- Use PasswordUtil.hash("Admin@1234") to generate the real hash
-- OR just register via the app UI after deployment
```

---

## Step 3 — Configure application.properties

```bash
# Copy the template
copy src\main\resources\application.properties.example src\main\resources\application.properties
```

Edit `src/main/resources/application.properties`:

```properties
# Your MySQL credentials
db.username=root
db.password=your_mysql_password

# A long random JWT secret (32+ characters)
jwt.secret=MyLongSecretKeyForOHMS2024@GalgotiasProject

# Gmail App Password (NOT your Gmail password)
# Go to: myaccount.google.com → Security → 2-Step Verification → App passwords
mail.from.address=youremail@gmail.com
mail.from.password=xxxx xxxx xxxx xxxx

# PDF output directory (must exist and be writable)
pdf.output.dir=C:/ohms/prescriptions
```

Create the PDF output directory:
```cmd
mkdir C:\ohms\prescriptions
```

---

## Step 4 — Build the WAR

If Maven is on your PATH:
```bash
mvn clean package
```

If using IntelliJ IDEA:
1. Open project → File → Project Structure → Artifacts → + → Web Application: Archive
2. Build → Build Artifacts → Build
3. Or use the Maven panel on the right → Lifecycle → package

If using Eclipse:
1. Right-click project → Run As → Maven Build → Goals: `clean package`

This generates: `target/OnlineHealthCareSystem.war`

---

## Step 5 — Deploy to Tomcat

### Option A — Manual WAR deployment
1. Copy `target/OnlineHealthCareSystem.war` to `<TOMCAT_HOME>/webapps/`
2. Start Tomcat:
   - Windows: `<TOMCAT_HOME>/bin/startup.bat`
   - Linux: `<TOMCAT_HOME>/bin/startup.sh`
3. Open: http://localhost:8080/OnlineHealthCareSystem

### Option B — IDE integrated Tomcat (recommended for development)

**IntelliJ IDEA:**
1. Run → Edit Configurations → + → Tomcat Server → Local
2. Server tab: set Tomcat home directory
3. Deployment tab: + → Artifact → select `OnlineHealthCareSystem:war`
4. Application context: `/OnlineHealthCareSystem`
5. Click Run

**Eclipse (with WTP):**
1. Window → Show View → Servers
2. Right-click → New → Server → Apache Tomcat 9
3. Add project → Finish
4. Right-click server → Start

---

## Step 6 — Access the Application

| URL | Description |
|-----|-------------|
| http://localhost:8080/OnlineHealthCareSystem | Home (redirects to login) |
| http://localhost:8080/OnlineHealthCareSystem/login | Login page |
| http://localhost:8080/OnlineHealthCareSystem/register | Patient / Doctor registration |

### Default Credentials

After running the schema and updating the admin password hash:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@ohms.com | Admin@1234 |

---

## Step 7 — Generate a valid Admin password hash

The schema seeds a placeholder hash. Generate a real one using:

```java
// Run this once as a simple Java main class
import com.ohms.utility.PasswordUtil;
public class GenerateHash {
    public static void main(String[] args) {
        System.out.println(PasswordUtil.hash("Admin@1234"));
    }
}
```

Then update the DB:
```sql
UPDATE users SET password_hash = '$2a$12$<your_generated_hash>' 
WHERE email = 'admin@ohms.com';
```

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | MySQL connector JAR not in WEB-INF/lib. Run `mvn package` again. |
| `Could not connect to database` | Check db.url, db.username, db.password in application.properties |
| `JWT expired / invalid` | Check jwt.secret is the same value across restarts |
| Email not sending | Enable Gmail 2FA, generate App Password, use it in properties |
| PDF not generating | Create the `pdf.output.dir` directory and ensure write permissions |
| 403 Forbidden on dashboard | JWT cookie role doesn't match the URL prefix — check filter configuration |

---

## Project Structure Quick Reference

```
src/main/java/com/ohms/
├── controller/          Servlets (HTTP layer)
│   ├── auth/            Login, Register, Logout, ForgotPwd, ResetPwd
│   ├── admin/           Dashboard, Doctors, Patients, Appointments, Departments
│   ├── doctor/          Dashboard, Appointments, Prescription
│   └── patient/         Dashboard, Search, BookAppointment, Download
├── service/             Business logic (Auth, Appointment, Prescription, etc.)
├── dao/                 DAO interfaces + JDBC implementations
├── model/               POJOs (User, Doctor, Patient, Appointment, Prescription)
├── utility/             DBConnection, JwtUtil, PasswordUtil, AppConfig, ValidationUtil
├── filter/              AuthFilter (JWT), RoleFilter (RBAC)
├── exception/           OhmsException hierarchy
└── enums/               Role, AppointmentStatus, DoctorStatus, Gender

src/main/webapp/
├── WEB-INF/web.xml      Servlet mappings, filter config
├── jsp/admin/           Admin views
├── jsp/doctor/          Doctor views
├── jsp/patient/         Patient views
├── jsp/common/          Login, Register, Forgot/Reset password, Sidebars
├── jsp/error/           403, 404, 500 error pages
├── css/style.css        Main stylesheet
└── js/                  dashboard.js, validation.js
```
