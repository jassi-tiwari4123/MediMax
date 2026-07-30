-- ================================================================
-- OHMS — Online Healthcare Management System
-- Database Schema  (MySQL 8.x)
-- ================================================================
-- Run this script once to create the full normalized schema.
-- All tables use InnoDB for foreign key support.
-- ================================================================

CREATE DATABASE IF NOT EXISTS ohms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ohms_db;

-- ----------------------------------------------------------------
-- 1. ROLES  — lookup table, seeded below
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id          TINYINT      UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(20)  NOT NULL UNIQUE,   -- ADMIN | DOCTOR | PATIENT
    description VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 2. DEPARTMENTS
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS departments (
    id          INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_dept_active (is_active)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 3. USERS  — central auth table for all roles
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(15)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- BCrypt hash
    role_id       TINYINT      UNSIGNED NOT NULL,
    gender        ENUM('MALE','FEMALE','OTHER') NOT NULL,
    date_of_birth DATE,
    profile_image VARCHAR(255),
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id)
        REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_users_email  (email),
    INDEX idx_users_phone  (phone),
    INDEX idx_users_role   (role_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 4. DOCTORS  — extends users (one-to-one)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS doctors (
    id              INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         INT          UNSIGNED NOT NULL UNIQUE,
    department_id   INT          UNSIGNED NOT NULL,
    specialization  VARCHAR(150) NOT NULL,
    qualification   VARCHAR(200) NOT NULL,
    experience_yrs  TINYINT      UNSIGNED NOT NULL DEFAULT 0,
    consultation_fee DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    available_days  VARCHAR(100),                 -- e.g. "MON,WED,FRI"
    available_from  TIME,
    available_to    TIME,
    bio             TEXT,
    status          ENUM('PENDING','APPROVED','REJECTED','INACTIVE')
                    NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_doctor_user   FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_doctor_dept   FOREIGN KEY (department_id)
        REFERENCES departments(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_doctor_dept   (department_id),
    INDEX idx_doctor_status (status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 5. PATIENTS  — extends users (one-to-one)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    id             INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id        INT          UNSIGNED NOT NULL UNIQUE,
    blood_group    VARCHAR(5),                    -- A+, B-, O+, etc.
    address        TEXT,
    emergency_contact_name  VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    medical_history TEXT,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_patient_user  FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 6. APPOINTMENTS
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    id              INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    patient_id      INT          UNSIGNED NOT NULL,
    doctor_id       INT          UNSIGNED NOT NULL,
    appointment_date DATE         NOT NULL,
    appointment_time TIME         NOT NULL,
    status          ENUM('PENDING','CONFIRMED','COMPLETED',
                         'CANCELLED','RESCHEDULED')
                    NOT NULL DEFAULT 'PENDING',
    reason          TEXT,                          -- patient's reason for visit
    notes           TEXT,                          -- doctor's notes
    diagnosis       TEXT,
    cancelled_by    ENUM('PATIENT','DOCTOR','ADMIN'),
    cancel_reason   TEXT,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_appt_patient  FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_appt_doctor   FOREIGN KEY (doctor_id)
        REFERENCES doctors(id) ON DELETE CASCADE ON UPDATE CASCADE,

    -- Prevent double-booking: same doctor cannot have two appointments
    -- at the same date+time while they are PENDING or CONFIRMED
    UNIQUE KEY uq_doctor_slot (doctor_id, appointment_date, appointment_time),

    INDEX idx_appt_patient (patient_id),
    INDEX idx_appt_doctor  (doctor_id),
    INDEX idx_appt_date    (appointment_date),
    INDEX idx_appt_status  (status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 7. PRESCRIPTIONS
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prescriptions (
    id              INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    appointment_id  INT          UNSIGNED NOT NULL UNIQUE,
    doctor_id       INT          UNSIGNED NOT NULL,
    patient_id      INT          UNSIGNED NOT NULL,
    diagnosis       TEXT         NOT NULL,
    instructions    TEXT,
    follow_up_date  DATE,
    pdf_path        VARCHAR(500),
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_presc_appt    FOREIGN KEY (appointment_id)
        REFERENCES appointments(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_presc_doctor  FOREIGN KEY (doctor_id)
        REFERENCES doctors(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_presc_patient FOREIGN KEY (patient_id)
        REFERENCES patients(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_presc_patient (patient_id),
    INDEX idx_presc_doctor  (doctor_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 8. PRESCRIPTION_ITEMS  — one row per medicine in a prescription
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prescription_items (
    id                INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    prescription_id   INT          UNSIGNED NOT NULL,
    medicine_name     VARCHAR(200) NOT NULL,
    dosage            VARCHAR(100) NOT NULL,      -- e.g. "500mg"
    morning           TINYINT(1)   NOT NULL DEFAULT 0,
    afternoon         TINYINT(1)   NOT NULL DEFAULT 0,
    night             TINYINT(1)   NOT NULL DEFAULT 0,
    duration_days     TINYINT      UNSIGNED,
    instructions      TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_presc    FOREIGN KEY (prescription_id)
        REFERENCES prescriptions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_item_presc (prescription_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------
-- 9. EMAIL_OTP  — stores OTP for password reset
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS email_otp (
    id          INT          UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     INT          UNSIGNED NOT NULL,
    otp_code    VARCHAR(10)  NOT NULL,
    purpose     ENUM('PASSWORD_RESET','EMAIL_VERIFY') NOT NULL,
    is_used     TINYINT(1)   NOT NULL DEFAULT 0,
    expires_at  DATETIME     NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_otp_user  FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_otp_user    (user_id),
    INDEX idx_otp_expires (expires_at)
) ENGINE=InnoDB;

-- ================================================================
-- SEED DATA
-- ================================================================

-- Roles
INSERT IGNORE INTO roles (id, name, description) VALUES
    (1, 'ADMIN',   'System administrator with full access'),
    (2, 'DOCTOR',  'Healthcare professional'),
    (3, 'PATIENT', 'Patient seeking healthcare services');

-- Departments
INSERT IGNORE INTO departments (name, description) VALUES
    ('Cardiology',       'Heart and cardiovascular system'),
    ('Neurology',        'Brain and nervous system'),
    ('Orthopedics',      'Bones and joints'),
    ('Pediatrics',       'Children healthcare'),
    ('Dermatology',      'Skin conditions'),
    ('General Medicine', 'General health checkups'),
    ('ENT',              'Ear, Nose and Throat'),
    ('Ophthalmology',    'Eye care'),
    ('Gynecology',       'Women health'),
    ('Psychiatry',       'Mental health');

-- Default Admin user
-- Password: Admin@1234  (BCrypt hash — update via app if needed)
INSERT IGNORE INTO users
    (full_name, email, phone, password_hash, role_id, gender, is_active)
VALUES
    ('System Admin',
     'admin@ohms.com',
     '9999999999',
     '$2a$12$hashed_placeholder_change_me',   -- replace via PasswordUtil
     1,
     'MALE',
     1);

-- ================================================================
-- END OF SCHEMA
-- ================================================================
