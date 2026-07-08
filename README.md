# 🎓 University ERP System

A desktop **Enterprise Resource Planning (ERP)** system for universities, built with **Java Swing** on the frontend and a **dual MySQL database** backend. The system supports three distinct roles — **Student**, **Instructor**, and **Admin** — each with a tailored dashboard and workflow, all wrapped in a clean, modern, hand-styled UI (no external UI libraries — just carefully themed Swing components).

> Built as a layered academic project demonstrating clean separation of concerns: UI → Service → DAO → Database, with real transactional integrity, password hashing, and role-based access control.

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
  - [🛠 Admin](#-admin)
  - [🎒 Student](#-student)
  - [👨‍🏫 Instructor](#-instructor)
  - [🔐 Shared / Auth](#-shared--auth)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Database Design](#-database-design)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Default Test Accounts](#-default-test-accounts)
- [Key Design Decisions](#-key-design-decisions)
- [Known Limitations / TODO](#-known-limitations--todo)
- [License](#-license)

---

## 🌐 Overview

The University ERP System digitizes core academic operations — course registration, gradebook management, and administrative oversight — into a single desktop application. It is organized around **three databases-worth of responsibility split into two physical databases**:

| Database    | Purpose                                                              |
|-------------|-----------------------------------------------------------------------|
| **AuthDB**    | Stores login credentials (`Users` table: email, bcrypt password hash, role) |
| **StudentDB** | Stores all academic data: students, instructors, courses, sections, enrollments, grades, notifications, and system settings |

Splitting authentication from academic data means the credential store can be hardened and audited independently of the much larger, more frequently-touched academic dataset.

---

## ✨ Features

### 🛠 Admin

- **User Management** — Create new Student, Instructor, or Admin accounts with a default password (`defaultPassword123`), auto-provisioned across both databases in a single atomic transaction.
- **Course Management** — Add new courses (code, title, credits) to the university catalog.
- **Section Management** — Schedule course sections, assign instructors, set time slots and capacity, and remove sections (blocked if students are currently enrolled).
- **System Maintenance Mode** — A single toggle that locks out Students and Instructors from making any state-changing actions (registration, drops, grade edits) system-wide — useful during data migrations or exam freezes.
- **Registration Deadline Control** — Set a hard cutoff date after which students can no longer register for or drop sections.
- **Live Notification Feed** — Every significant admin action (course created, section added/removed, maintenance toggled) is pushed to a shared notification bell visible to all users.

### 🎒 Student

- **Course Catalog** — Browse all available sections with live seat counts, color-coded availability (green → orange as seats run low → red "Full"), and one-click Register/Drop actions.
- **Deadline-Aware Registration** — Registration and drop actions are blocked automatically once the admin-set deadline has passed.
- **Concurrency-Safe Enrollment** — Uses row-level locking (`SELECT ... FOR UPDATE`) during registration to prevent overbooking a section when multiple students register simultaneously.
- **Timetable View** — A clean, auto-refreshing schedule of all currently enrolled sections.
- **Grades & Transcript** — View final letter grades per course and export a full transcript to CSV with one click.

### 👨‍🏫 Instructor

- **My Sections Dashboard** — Card-based view of every section assigned to the instructor, with enrollment counts at a glance.
- **Gradebook** — Enter Quiz, Midterm, and Final scores per student directly in an editable table.
- **Automatic Grade Calculation** — Final numeric score is computed from a weighted formula (**20% Quiz + 30% Midterm + 50% Final**) and mapped to a letter grade (A → F) on save.
- **CSV Export** — Export any section's gradebook to a CSV file for offline record-keeping.
- **Maintenance-Aware UI** — The gradebook automatically becomes read-only (with a visible warning) when the system is in Maintenance Mode.

### 🔐 Shared / Auth

- **Secure Login** — Passwords are hashed with **BCrypt** (`jBCrypt`); plaintext passwords are never stored or logged.
- **Brute-Force Protection** — The login dialog locks out further attempts after 5 consecutive failures within a session.
- **Change Password** — Any logged-in user can change their password after re-verifying their current one.
- **Role-Based Routing** — On login, the app looks up the user's role-specific profile ID (Student ID / Instructor ID) and routes them straight to the appropriate dashboard.
- **Notifications Bell** — A persistent menu-bar dropdown shows the 10 most recent system-wide notifications with timestamps.

---

## 🏗 Architecture

The application follows a strict **layered architecture**, keeping Swing UI code completely decoupled from SQL:

```
┌─────────────────────────────────────────────────────────┐
│                        UI Layer                          │
│   (JPanels, JDialogs — Swing components, zero SQL)        │
│   Main.java · LoginDialog · *DashboardPanel · *Panel       │
└───────────────────────────┬─────────────────────────────┘
                            │  calls
┌───────────────────────────▼─────────────────────────────┐
│                      Service Layer                       │
│   Business logic, validation, transactions, notifications  │
│   AuthService · AdminService · StudentService ·             │
│   InstructorService · TranscriptService                    │
└───────────────────────────┬─────────────────────────────┘
                            │  calls
┌───────────────────────────▼─────────────────────────────┐
│                        DAO Layer                          │
│         Raw JDBC, prepared statements, ResultSet mapping    │
│   UserDAO · AdminDAO · StudentDAO · InstructorDAO ·         │
│   SettingsDAO · NotificationDAO                             │
└───────────────────────────┬─────────────────────────────┘
                            │  queries
┌───────────────────────────▼─────────────────────────────┐
│                   MySQL Databases                          │
│              AuthDB           StudentDB                    │
└─────────────────────────────────────────────────────────┘
```

Data crossing layer boundaries is passed as immutable **Java `record`** types (e.g. `AuthResult`, `SectionView`, `GradebookEntry`, `UserView`) rather than raw `ResultSet`s or database entities — keeping the UI layer entirely persistence-agnostic.

### Cross-database transactions

Some operations (like creating a new user) must write to **both** `AuthDB` and `StudentDB` atomically. `AdminService.createNewUser()` handles this by:
1. Opening a connection to each database with `autoCommit(false)`.
2. Writing the auth record, then the corresponding profile record.
3. Committing both connections only if every step succeeds.
4. Rolling back both on any failure, ensuring no orphaned accounts.

---

## 🧰 Tech Stack

| Layer            | Technology                                   |
|-------------------|-----------------------------------------------|
| Language          | Java 17+ (uses `record`, text blocks, `var`)   |
| UI                | Java Swing (Nimbus Look & Feel)                |
| Database          | MySQL 8+ (two separate schemas)                |
| DB Driver         | MySQL Connector/J (`com.mysql.cj.jdbc.Driver`) |
| Password Hashing  | jBCrypt (`org.mindrot.jbcrypt`)                |
| CSV I/O           | OpenCSV (`com.opencsv`)                        |
| Logging           | `java.util.logging`                            |

---

## 🗄 Database Design

### `AuthDB`
| Table | Key Columns |
|-------|-------------|
| `Users` | `UserID` (PK), `Email`, `PasswordHash`, `Role` |

### `StudentDB`
| Table | Key Columns | Notes |
|-------|-------------|-------|
| `Students` | `StudentID` (PK), `UserID`, `FullName`, `Email` | Linked to `AuthDB.Users` by `UserID` |
| `Instructors` | `InstructorID` (PK), `UserID`, `FullName`, `Email` | Linked to `AuthDB.Users` by `UserID` |
| `Course` | `CourseID` (PK), `CourseCode`, `CourseTitle`, `Credits` | Master course catalog |
| `Sections` | `SectionID` (PK), `CourseID`, `InstructorID`, `SectionNumber`, `TimeSlot`, `Capacity`, `EnrolledCount` | One course → many sections |
| `Enrollments` | `StudentID`, `SectionID`, `EnrollmentDate` | Join table for registrations |
| `Grades` | `StudentID`, `SectionID`, `QuizScore`, `MidtermScore`, `FinalScore`, `FinalGrade` | Upserted via `ON DUPLICATE KEY UPDATE` |
| `Notifications` | `NotificationID` (PK), `Message`, `CreatedAt` | System-wide activity feed |
| `SystemSettings` | `SettingKey`, `SettingValue` | Stores `MaintenanceMode` and `Deadline` as key-value pairs |

> ⚠️ `AuthDB` and `StudentDB` are **separate schemas with no foreign key relationship between them** — they are only linked logically via the shared `UserID`, which is why cross-database writes are handled manually in the service layer rather than relying on DB-enforced referential integrity.

---

## 📁 Project Structure

```
edu/univ/erp/
├── auth/                    # Authentication domain — credentials only
│   ├── AuthResult.java
│   ├── AuthUserInfo.java
│   └── UserDAO.java
├── data/                    # DAOs for academic data (StudentDB)
│   ├── AdminDAO.java
│   ├── InstructorDAO.java
│   ├── NotificationDAO.java
│   ├── ProfileInfo.java
│   ├── SettingsDAO.java
│   └── StudentDAO.java
├── domain/                  # Shared immutable record types (DTOs)
│   ├── AdminSectionView.java
│   ├── Course.java
│   ├── EnrolledSection.java
│   ├── Grade.java
│   ├── GradebookEntry.java
│   ├── Instructor.java
│   ├── SectionView.java
│   └── UserView.java
├── service/                 # Business logic layer
│   ├── AdminService.java
│   ├── AuthService.java
│   ├── InstructorService.java
│   ├── LoginResult.java
│   ├── StudentService.java
│   └── TranscriptService.java
├── ui/
│   ├── Main.java             # App entry point, CardLayout, menu bar, navigation
│   ├── admin/
│   │   ├── AdminDashboardPanel.java
│   │   ├── CourseManagementPanel.java
│   │   ├── SectionManagementPanel.java
│   │   └── UserManagementPanel.java
│   ├── auth/
│   │   ├── ChangePasswordDialog.java
│   │   └── LoginDialog.java
│   ├── instructor/
│   │   ├── GradebookPanel.java
│   │   └── MySectionsPanel.java
│   └── student/
│       ├── CourseCatalogPanel.java
│       ├── GradesPanel.java
│       ├── StudentDashboardPanel.java
│       └── TimetablePanel.java
└── util/
    ├── DatabaseSeeder.java   # One-shot script to wipe & seed test data
    └── DatabaseUtil.java     # JDBC connection factory for both databases

resources/
└── icons/
    └── iiitd_logo.png

test/
└── java/
```

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17+**
- **MySQL 8+** running locally (or reachable) with two schemas: `AuthDB` and `StudentDB`
- A build tool (Maven/Gradle) with the following dependencies:
  - `mysql:mysql-connector-j`
  - `org.mindrot:jbcrypt`
  - `com.opencsv:opencsv`

### 1. Configure the database connection

Edit `edu/univ/erp/util/DatabaseUtil.java` and set your MySQL credentials:

```java
private static final String AuthDB_URL = "jdbc:mysql://localhost:3306/AuthDB";
private static final String AuthDB_User = "AuthUser";
private static final String AuthDB_PassWord = "AUTHPASSWORD";

private static final String StudentDB_URL = "jdbc:mysql://localhost:3306/StudentDB";
private static final String StudentDB_User = "StudentUser";
private static final String StudentDB_PassWord = "STUDENTPASSWORD";
```

> 🔒 For a production setup, externalize these into environment variables or a `.properties` file instead of hardcoding them.

### 2. Create the schema

Create the `AuthDB` and `StudentDB` schemas and the tables described in [Database Design](#-database-design) above.

### 3. Seed test data

Run `DatabaseSeeder.main()` to wipe all tables and populate 4 ready-to-use accounts:

```bash
java edu.univ.erp.util.DatabaseSeeder
```

### 4. Launch the application

Run `Main.main()`:

```bash
java edu.univ.erp.ui.Main
```

The Nimbus Look & Feel is applied automatically if available, falling back to the system default otherwise.

---

## 🔑 Default Test Accounts

After running `DatabaseSeeder`, the following accounts are available:

| Role       | Email                     | Password  |
|------------|---------------------------|-----------|
| Student    | `stu1@university.edu`      | `stu123`  |
| Student    | `stu2@university.edu`      | `stu123`  |
| Instructor | `inst1@university.edu`     | `inst123` |
| Admin      | `admin1@university.edu`    | `admin123`|

---

## 🧩 Key Design Decisions

- **Records over classes for DTOs** — All data passed between layers (`SectionView`, `Grade`, `UserView`, etc.) are Java `record`s: immutable, concise, and free of accidental business logic creeping into data holders.
- **No ORM** — Persistence is handled with raw JDBC and `PreparedStatement`s. This keeps SQL explicit and auditable at the cost of some boilerplate — a deliberate trade-off for a learning-oriented project.
- **Manual two-phase-ish commits** — Since `AuthDB` and `StudentDB` are physically separate databases, true distributed transactions aren't available; the service layer instead manages manual commit/rollback ordering to minimize the window for inconsistency.
- **Optimistic UI, pessimistic data** — The UI trusts the service layer's return values (booleans/strings) to update itself, while the data layer uses row locks (`FOR UPDATE`) for anything involving shared, mutable counters like `EnrolledCount`.
- **Maintenance Mode as a global circuit breaker** — Rather than disabling individual features, one setting flips read-only behavior across registration, drops, and grade entry simultaneously.

---

## 🧭 Known Limitations / TODO

- [ ] `InstructorService.getGradebook()` has a `TODO` to verify the requesting instructor actually owns the section before returning grades.
- [ ] Default passwords (`defaultPassword123`) are not force-changed on first login.
- [ ] No password complexity requirements are enforced on change/creation.
- [ ] Admin display names are hardcoded to `"Admin User"` since there's no dedicated Admin profile table.
- [ ] Login lockout (5 attempts) is session-based only and resets on app restart — not persisted per-account.
- [ ] Database credentials are hardcoded in `DatabaseUtil.java` rather than externalized.

---

## 📄 License

MIT License