# Online Healthcare Management System (OHMS)

**A full-stack Java EE web application for healthcare operations management.**

> **Built as a final-year major project demonstrating core Java, OOP, Servlets, JDBC, JWT authentication, and modern web development practices.**

---

## Project Overview

OHMS enables three user roles (Admin, Doctor, Patient) to perform their respective workflows:
- **Patients** search doctors, book appointments, download prescriptions.
- **Doctors** manage appointments, add diagnoses, generate prescriptions.
- **Admins** manage doctors, patients, departments, and view analytics.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | HTML5, CSS3, Bootstrap 5, JavaScript, JSP |
| **Backend** | Java 11, Servlets, JDBC |
| **Security** | JWT (JJWT), BCrypt password hashing |
| **Database** | MySQL 8.x |
| **Build** | Maven |
| **Server** | Apache Tomcat 9.x |
| **Email** | JavaMail API (SMTP) |
| **PDF** | iText 5 |

---

## Architecture

Follows **MVC + Layered Architecture**:

```
┌─────────────┐
│   Browser   │
└─────┬───────┘
      │  HTTP
┌─────▼─────────┐
│  JSP + HTML   │  ← View Layer
└─────┬─────────┘
      │
┌─────▼─────────┐
│  Servlets     │  ← Controller Layer
└─────┬─────────┘
      │
┌─────▼─────────┐
│  Service      │  ← Business Logic
└─────┬─────────┘
      │
┌─────▼─────────┐
│  DAO          │  ← Data Access Layer
└─────┬─────────┘
      │ JDBC
┌─────▼─────────┐
│  MySQL DB     │
└───────────────┘
```

**Packages:**
- `com.ohms.controller` — Servlets (auth, admin, doctor, patient)
- `com.ohms.service` — Business logic, transaction coordination
- `com.ohms.dao` — JDBC operations, PreparedStatements
- `com.ohms.model` — POJOs (User, Doctor, Patient, Appointment, etc.)
- `com.ohms.utility` — DBConnection, JwtUtil, PasswordUtil, ValidationUtil
- `com.ohms.filter` — AuthFilter, RoleFilter (JWT validation)
- `com.ohms.exception` — Custom exceptions (AuthException, ValidationException)
- `com.ohms.enums` — Role, AppointmentStatus, DoctorStatus, Gender

---

## Java Concepts Demonstrated

| Concept | Implementation |
|---------|---------------|
| **Encapsulation** | All model fields private; accessed via getters/setters |
| **Inheritance** | `OhmsException → AuthException / ValidationException / etc.` |
| **Polymorphism** | Method overloading (constructors, validators) |
| **Abstraction** | DAO interfaces (`UserDAO`, `DoctorDAO`), service contracts |
| **Interfaces** | `UserDAO`, `Comparable<Doctor>`, `Comparable<Appointment>` |
| **Enums** | `Role`, `AppointmentStatus`, `DoctorStatus`, `Gender` |
| **Collections** | `List<User>`, `List<Appointment>`, `ArrayList`, sorting |
| **Generics** | `Optional<T>`, `List<T>`, DAO return types |
| **Comparable** | `Doctor` sorts by experience, `Appointment` sorts by date |
| **Exception Handling** | Try-with-resources, custom exceptions, transaction rollback |
| **JDBC** | PreparedStatement (SQL injection prevention), transactions |
| **Constructor Overloading** | Multiple constructors for different use cases |

---

## Database Schema

Normalized schema with **9 tables**:
1. **roles** — ADMIN, DOCTOR, PATIENT
2. **users** — central auth table for all users
3. **departments** — hospital departments
4. **doctors** — doctor profiles (1-to-1 with users)
5. **patients** — patient profiles (1-to-1 with users)
6. **appointments** — patient ↔ doctor appointments
7. **prescriptions** — one per completed appointment
8. **prescription_items** — medicines in a prescription
9. **email_otp** — OTP for password reset

**Key Constraints:**
- Foreign keys enforce referential integrity.
- Unique constraint on `(doctor_id, appointment_date, appointment_time)` prevents double-booking.
- Indexes on email, phone, appointment date, status.

---

## Features

### Authentication & Authorization
- ✅ JWT-based stateless authentication (stored in HTTP-only cookie)
- ✅ BCrypt password hashing (cost factor 12)
- ✅ Email OTP for password reset
- ✅ Role-based authorization via Filters (ADMIN / DOCTOR / PATIENT)
- ✅ Session timeout (30 minutes)

### Admin Module
- Manage doctors (approve/reject/disable)
- Manage patients (view, deactivate)
- View all appointments
- Manage departments
- Dashboard with analytics

### Doctor Module
- View appointments (filter by date, status)
- Confirm/complete appointments
- Add diagnosis
- Generate prescriptions (with multiple medicines)
- Download prescription as PDF

### Patient Module
- Search doctors (by name, department, specialization)
- Book appointments (with slot availability check)
- Cancel appointments
- View appointment history
- Download prescriptions as PDF

### Email Notifications
- Registration success
- OTP for password reset
- Appointment confirmation
- Appointment cancellation

### PDF Generation
- Prescription PDF with hospital header
- Patient & doctor details
- Medicines with dosage schedule (morning/afternoon/night)

---

## Security Features

1. **SQL Injection Prevention** — All queries use PreparedStatement.
2. **XSS Prevention** — Input sanitization via `ValidationUtil.sanitize()`.
3. **Password Security** — BCrypt hashing, never stored in plain text.
4. **JWT Signing** — HMAC-SHA256 with secret key.
5. **Authorization Filters** — AuthFilter validates token; RoleFilter checks role.
6. **HTTP-only Cookies** — JWT stored in HTTP-only cookie (not localStorage).

---

## Project Structure

```
OnlineHealthCareSystem/
├── pom.xml
├── README.md
├── src/main/
│   ├── java/com/ohms/
│   │   ├── controller/       ← Servlets (auth, admin, doctor, patient)
│   │   ├── service/          ← Business logic (AuthService, AppointmentService, etc.)
│   │   ├── dao/              ← DAO interfaces + JDBC implementations
│   │   ├── model/            ← POJOs (User, Doctor, Patient, Appointment, etc.)
│   │   ├── utility/          ← DBConnection, JwtUtil, PasswordUtil, etc.
│   │   ├── filter/           ← AuthFilter, RoleFilter
│   │   ├── exception/        ← Custom exceptions
│   │   └── enums/            ← Role, AppointmentStatus, DoctorStatus, Gender
│   ├── resources/
│   │   └── application.properties
│   └── webapp/
│       ├── WEB-INF/web.xml
│       ├── index.jsp
│       ├── css/
│       ├── js/
│       ├── images/
│       └── jsp/
│           ├── admin/
│           ├── doctor/
│           ├── patient/
│           ├── common/
│           └── error/
├── docs/
│   ├── diagrams/             ← ER Diagram, Class Diagram, Use Case
│   └── sql/
│       └── schema.sql        ← Full MySQL schema with seed data
```

---

## Setup Instructions

### Prerequisites
- **Java 11** or higher
- **MySQL 8.x**
- **Apache Tomcat 9.x**
- **Maven** (optional if IDE has embedded Maven)

### Steps

1. **Clone / extract the project**
   ```bash
   cd OnlineHealthCareSystem
   ```

2. **Create the database**
   ```bash
   mysql -u root -p < docs/sql/schema.sql
   ```

3. **Update `application.properties`**
   ```properties
   db.username=root
   db.password=yourpassword
   jwt.secret=YourLongRandomSecretKey
   mail.from.address=your.email@gmail.com
   mail.from.password=your_app_password
   ```

4. **Build the WAR file**
   ```bash
   mvn clean package
   ```
   Generates `target/OnlineHealthCareSystem.war`

5. **Deploy to Tomcat**
   - Copy `OnlineHealthCareSystem.war` to `<TOMCAT_HOME>/webapps/`
   - Start Tomcat: `./bin/startup.sh` (Linux) or `bin\startup.bat` (Windows)

6. **Access the application**
   ```
   http://localhost:8080/OnlineHealthCareSystem
   ```

---

## Default Credentials

After running the schema script, a default admin account is available:
- **Email:** `admin@ohms.com`
- **Password:** `Admin@1234` (update the BCrypt hash in schema.sql before deploying)

---

## How to Explain in Interview

**"Why did you choose this architecture?"**
> We followed MVC + Layered Architecture to separate concerns:
> - **Controller** handles HTTP requests (Servlets).
> - **Service** contains all business logic (e.g., booking rules, validations).
> - **DAO** handles database access via JDBC, using PreparedStatements.
> - **Model** represents our entities. This separation makes the code maintainable and testable.

**"How do you prevent SQL injection?"**
> Every query uses `PreparedStatement` with parameterized queries. User input is never concatenated into SQL strings.

**"How does JWT authentication work here?"**
> On login, we generate a JWT containing `userId`, `email`, and `role`, signed with HMAC-SHA256. The token is stored in an HTTP-only cookie. Every subsequent request passes through `AuthFilter`, which validates the token and extracts user details before forwarding to the servlet.

**"What design patterns did you use?"**
> - **DAO Pattern** — abstracts data access behind interfaces.
> - **Singleton** — `AppConfig` loads properties once.
> - **Service Layer Pattern** — business logic centralized, reusable.
> - **MVC** — separation of view (JSP), controller (Servlet), model (POJO).

**"How do you handle transactions?"**
> For operations spanning multiple tables (e.g., saving Prescription + PrescriptionItems), we manually set `autoCommit=false`, execute all queries, then `commit()`. On any exception, we `rollback()` to ensure atomicity.

---

## Development Phases

✅ **Phase 1** — Requirement Analysis
✅ **Phase 2** — Folder Structure + Maven Setup
✅ **Phase 3** — Database Schema + ER Diagram
✅ **Phase 4** — Java Models (POJOs with OOP)
✅ **Phase 5** — Enums + Custom Exceptions
✅ **Phase 6** — Utility Layer (JwtUtil, PasswordUtil, DBConnection)
✅ **Phase 7** — DAO Layer (UserDAO, DoctorDAO, AppointmentDAO, etc.)
🟡 **Phase 8** — Service Layer (AuthService, AppointmentService, etc.) *[in progress]*
⬜ **Phase 9** — Servlets (Controllers)
⬜ **Phase 10** — JSP Pages (Views)
⬜ **Phase 11** — Email Integration
⬜ **Phase 12** — PDF Generation
⬜ **Phase 13** — Testing
⬜ **Phase 14** — Deployment on Tomcat

---

## License

This project is for educational purposes. Feel free to use it as a reference for your own final-year project.

---

## Contact

**Developer:** [Your Name]
**Email:** [your.email@example.com]
**GitHub:** [github.com/yourprofile]

---

**Built with ❤️ for Galgotias University Major Project**
