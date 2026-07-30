# Online Healthcare Management System (OHMS)

**A full-stack Java EE web application for healthcare operations management.**

> **Built as a final-year major project at Galgotias University demonstrating core Java, OOP, Servlets, JDBC, JWT authentication, and modern web development practices.**

---

## Project Overview

OHMS enables three user roles (Admin, Doctor, Patient) to perform their respective workflows:
- **Patients** search doctors, book appointments, and download prescriptions.
- **Doctors** manage appointments, add diagnoses, and generate prescriptions.
- **Admins** manage doctors, patients, departments, and view analytics.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | HTML5, CSS3, Bootstrap 5, JavaScript, JSP |
| **Backend** | Java 17+, Servlets, JDBC |
| **Security** | JWT (JJWT), BCrypt password hashing |
| **Database** | MySQL 8.x |
| **Build** | Maven |
| **Server** | Apache Tomcat 9.x |
| **Email** | JavaMail API (SMTP / Gmail) |
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

Normalized schema with **10 tables**:
1. **roles** — ADMIN, DOCTOR, PATIENT
2. **users** — central auth table for all users
3. **departments** — hospital departments
4. **specializations** — specializations per department
5. **doctors** — doctor profiles (1-to-1 with users)
6. **patients** — patient profiles (1-to-1 with users)
7. **appointments** — patient ↔ doctor appointments
8. **prescriptions** — one per completed appointment
9. **prescription_items** — medicines in a prescription
10. **email_otp** — OTP for password reset

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
- View all appointments with status filter
- Manage departments + specializations
- Dashboard with analytics

### Doctor Module
- View and manage appointments
- Confirm / complete appointments
- Add diagnosis and notes
- Generate prescriptions (with multiple medicines, morning/afternoon/night dosage)
- Edit profile with photo upload

### Patient Module
- Search doctors (by name, department, specialization)
- Book appointments (with double-booking prevention)
- Cancel appointments
- View appointment history
- Download prescriptions as PDF
- Edit profile with photo upload

### Email Notifications
- Registration success
- OTP for password reset
- Appointment confirmation (when doctor confirms)
- Appointment cancellation
- Prescription ready notification

### PDF Generation
- Prescription PDF with hospital header
- Patient & doctor details
- Medicines table with dosage schedule (morning/afternoon/night)
- Follow-up date and doctor signature

---

## Security Features

1. **SQL Injection Prevention** — All queries use PreparedStatement.
2. **XSS Prevention** — Input sanitization via `ValidationUtil`.
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
│   │   ├── service/          ← Business logic
│   │   ├── dao/              ← DAO interfaces + JDBC implementations
│   │   ├── model/            ← POJOs
│   │   ├── utility/          ← DBConnection, JwtUtil, PasswordUtil, etc.
│   │   ├── filter/           ← AuthFilter, RoleFilter
│   │   ├── exception/        ← Custom exceptions
│   │   └── enums/            ← Role, AppointmentStatus, DoctorStatus, Gender
│   ├── resources/
│   │   └── application.properties.example
│   └── webapp/
│       ├── WEB-INF/web.xml
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
│   ├── SETUP_GUIDE.md
│   └── sql/
│       └── schema.sql
```

---

## Setup Instructions

### Prerequisites
- Java 17+
- MySQL 8.x
- Apache Tomcat 9.x
- IntelliJ IDEA (Community Edition — free)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/jassi-tiwari4123/OnlineHealthCareSystem.git
   ```

2. **Create the database**
   - Open MySQL Workbench
   - Run `docs/sql/schema.sql`

3. **Configure the application**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   Edit `application.properties`:
   ```properties
   db.password=your_mysql_password
   jwt.secret=YourLongRandomSecretKey
   mail.from.address=your.gmail@gmail.com
   mail.from.password=your_gmail_app_password
   pdf.output.dir=C:/ohms/prescriptions
   ```

4. **Open in IntelliJ IDEA**
   - File → Open → select project folder
   - Wait for Maven to sync dependencies

5. **Configure Tomcat**
   - Run → Edit Configurations → + → Tomcat Server → Local
   - Add `OnlineHealthCareSystem:war exploded` artifact
   - Set context path: `/OnlineHealthCareSystem`

6. **Run the project**
   - Click the green ▶ Run button
   - Browser opens at `http://localhost:8080/OnlineHealthCareSystem`

---

## Default Admin Credentials

| Field | Value |
|-------|-------|
| Email | `admin@ohms.com` |
| Password | `Admin@1234` |

> Generate the correct BCrypt hash using `src/test/java/com/ohms/HashGenerator.java` and update in MySQL.

---

## Development Phases

✅ Phase 1 — Requirement Analysis  
✅ Phase 2 — Folder Structure + Maven Setup  
✅ Phase 3 — Database Schema  
✅ Phase 4 — Java Models (POJOs with OOP)  
✅ Phase 5 — Enums + Custom Exceptions  
✅ Phase 6 — Utility Layer  
✅ Phase 7 — DAO Layer  
✅ Phase 8 — Service Layer  
✅ Phase 9 — Servlets (Controllers)  
✅ Phase 10 — JSP Pages (Views)  
✅ Phase 11 — Email Integration  
✅ Phase 12 — PDF Generation  
✅ Phase 13 — Profile Management  
✅ Phase 14 — Deployed on Apache Tomcat  

---

## License

This project is for educational purposes. Feel free to use it as a reference.

---

## Developer

**Himanshu**  
Galgotias University  
GitHub: [jassi-tiwari4123](https://github.com/jassi-tiwari4123)

---

**Built with ❤️ for Galgotias University Final Year Major Project**
