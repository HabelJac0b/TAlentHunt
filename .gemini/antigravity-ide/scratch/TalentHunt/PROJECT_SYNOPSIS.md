# Initial Project Synopsis

## 1. Title of the Project
**Online Talent Hunt and Competition Evaluation System with Java, Servlets, JSP, and Web Technologies**

*(Short Title: **TalentHunt System**)*

---

## 2. Introduction

### Purpose and Significance of the Project
The **TalentHunt System** is an interactive web-based platform engineered to discover, showcase, evaluate, and celebrate creative talent across diverse creative domains (vocal music, dance, visual arts, technology innovation, comedy, and dramatic performance). In traditional offline talent competitions, participant registration, submission tracking, judge scoring, and audience voting are fragmented, paper-heavy, and lack real-time visibility. 

This project bridges that gap by providing a unified digital portal where:
- **Contestants** can seamlessly register, view active competitions, submit their performances with media links, and track their competition standing.
- **Audience Members** can explore curated entries, search/filter talents by category, and cast verified votes.
- **Judges** can assess performances across standardized scoring metrics (Technical Execution, Creativity, Presentation) with qualitative feedback.
- **Administrators** can moderate submissions, oversee competition schedules, and access system telemetry.

### Problem Statement
Existing talent management and competition workflows face critical bottlenecks:
1. **Inefficient Participant Management:** Manual and unverified registration leads to duplicate entries, data inconsistencies, and communication delays.
2. **Lack of Transparent Scoring:** Without standardized criteria and secure role-based access, judging scores and audience voting can lack auditability and real-time computation.
3. **Absence of Unified Access Control:** Platforms often lack granular session management to differentiate contestant dashboards from administrative moderation panels.
4. **Disjointed User Experience:** Existing academic and legacy systems often fail to offer responsive, modern user interfaces that pair robust server-side data integrity with responsive client-side interactions.

### Main Objectives of the Project
- Design and implement a robust, encapsulated **User Entity** with multi-tier validation (string handling in Java and client-side JavaScript regex checks).
- Follow **Structured Programming Practices** with clean package segregation, service interfaces (`UserOperations`), and custom exception hierarchies.
- Deliver an interactive, modern **Web Interface** for user onboarding, submission management, and live leaderboard tracking.
- Implement server-side processing using **Java Servlets (GET/POST)**, session tracking with **`HttpSession`**, and dynamic **JSP Dashboards**.
- Enforce strict **Role-Based Access Control (RBAC)** to restrict unauthorized dashboard access and provide session invalidation on logout.

---

## 3. Objectives (Aligned with Course Learning Outcomes)

| No. | Core Objective | Course Learning Outcome (CLO) |
|---|---|---|
| **OBJ-1** | **User Entity & Data Modeling:** Design encapsulated `User` class with attributes (username/email, password, role, profile metadata), constructors, and Java validation logic. | **CLO1** (Core Application Setup and Design) |
| **OBJ-2** | **Structured Programming & Reliability:** Organize code into clean packages (`model`, `service`, `dao`, `servlet`, `exception`, `util`), define an interface for user operations (`UserOperations`), and handle exceptions robustly. | **CLO2** (Structured Programming & Reliability) |
| **OBJ-3** | **Interactive Web Client:** Build responsive HTML5/CSS3 registration, login, and submission forms with real-time JavaScript validation and JSON data representation. | **CLO2** (Web Interface & Client Interaction) |
| **OBJ-4** | **Server-Side Integration with Servlets:** Process registration and authentication via HTTP Servlets (`doGet`/`doPost`), performing server-side validation against a persistent database. | **CLO1, CLO2, CLO3** (Server-Side Integration) |
| **OBJ-5** | **Session Tracking & Access Restriction:** Manage user sessions via `HttpSession`, restrict unauthorized dashboard access via filters and session checks, and invalidate sessions upon logout. | **CLO3** (Security & Session Tracking) |
| **OBJ-6** | **Dynamic JSP Dashboards:** Render tailored contestant and administrator dashboards using JavaServer Pages (JSP) and standard Java EE deployment descriptors (`web.xml`). | **CLO1, CLO3** (Dynamic Views & Deployment) |

---

## 4. Tools and Technologies

| Layer / Component | Technology Selected | Rationale & Usage |
|---|---|---|
| **Programming Language** | **Java (JDK 8 / 17 / 21)** | Object-Oriented programming, packages, classes, interfaces, exception handling, and JDBC. |
| **Web Presentation** | **HTML5 & Vanilla CSS3** | Modern semantic markup, accessible forms, glassmorphism design, and responsive layouts. |
| **Client-Side Scripting** | **JavaScript (ES6+)** | Dynamic DOM updates, regex input validation, form submission flow, and asynchronous fetch APIs. |
| **Data Representation** | **JSON** | Clean data exchange format between client interface and server endpoints. |
| **Server-Side Processing** | **Java Servlets (JSP / Servlet API 3.1+)** | Request/response handling (`doGet`, `doPost`), credential verification, and business logic execution. |
| **Dynamic View Rendering** | **JavaServer Pages (JSP)** | Server-rendered user and administrator dashboards (`dashboard.jsp`, `admin-dashboard.jsp`). |
| **Session Management** | **`javax.servlet.http.HttpSession`** | Cookie-based session tracking, user state persistence, and session invalidation. |
| **Persistence / Database** | **MySQL Server / Embedded H2 (MySQL Mode)** | Relational database schema with tables for `users`, `categories`, `competitions`, `talent_submissions`, `evaluations`, and `votes`. |
| **Web Server / Container** | **Apache Tomcat 9/10 / Embedded Java HTTP Server** | Deployment via standard `web.xml` and WAR structure, plus standalone runner (`run.bat`). |

---

## 5. Methodology / Approach / Phases

### Phase 1: Core Application Setup and Design (CLO1)
- **Identify User Entity:** Defined the `User` class with attributes `id`, `name`, `email`, `password`, `role` (`CONTESTANT`, `JUDGE`, `AUDIENCE`, `ADMIN`), `avatarUrl`, `bio`, and `createdAt`.
- **Implement Class Structure:** Created default and parameterized constructors, full getter and setter methods, encapsulation, and helper role checks (`isAdmin()`, `isContestant()`).
- **Basic Validation Logic in Java:** Developed `ValidationUtil` using Java string handling (`isNullOrEmpty`, `isValidEmail`, `isValidPassword`, `isValidName`, and input sanitization).

### Phase 2: Structured Programming and Reliability (CLO2)
- **Package Organization:**
  - `com.talenthunt.model`: Domain entities (`User`, `Competition`, `TalentSubmission`, `Evaluation`, `Vote`).
  - `com.talenthunt.service`: Operations contract (`UserOperations`).
  - `com.talenthunt.service.impl`: Business logic implementation (`UserServiceImpl`).
  - `com.talenthunt.dao`: JDBC database access layer (`TalentDao`).
  - `com.talenthunt.db`: Database connection manager (`DatabaseManager`).
  - `com.talenthunt.exception`: Custom exception classes (`ValidationException`, `AuthenticationException`, `UserAlreadyExistsException`).
  - `com.talenthunt.util`: Reusable string validation utilities (`ValidationUtil`).
  - `com.talenthunt.servlet`: Java EE Servlets and Filters (`LoginServlet`, `RegisterServlet`, `LogoutServlet`, `DashboardServlet`, `AuthenticationFilter`).
- **User Operations Interface:** Created `UserOperations` declaring contracts for `authenticate()`, `register()`, `validateUser()`, `isEmailAvailable()`, and user lookups.
- **Exception Handling for Invalid Inputs:** Integrated explicit custom exceptions thrown during invalid inputs, duplicate registrations, and authentication failures with structured try-catch blocks.

### Phase 3: Web Interface and Client Interaction (CLO2)
- **Design Web Forms:** Created sleek registration, login, and submission forms with user/admin role toggle buttons, clear labeling, and accessible inputs.
- **Apply JavaScript Validation:** Implemented client-side format checks (regex email pattern, minimum password length of 6 characters, non-empty fields) with real-time visual red borders and alert banners before submission.
- **Basic Interaction Handling:** Engineered interactive form submission flows with animated progress spinners, modal confirmations for voting, live toast notifications, and smooth page transitions.
- **Represent User Data Using JSON:** Structured user requests and responses in standardized JSON formats for lightweight API interaction.

### Phase 4: Server-Side Integration and Execution (CLO1, CLO2, CLO3)
- **Process Registration and Login using Servlets:** Built `LoginServlet` and `RegisterServlet` handling both `GET` (view dispatching) and `POST` (credential validation and persistence).
- **Server-Side Validation:** Validated incoming credentials against database records via `UserServiceImpl` and `TalentDao`.
- **Session Tracking via `HttpSession`:** Leveraged `request.getSession(true)` to store authenticated user objects, role attributes, and timestamps with a 30-minute inactivity timeout.
- **JSP User & Admin Dashboards:** Built `dashboard.jsp` (contestant overview, submission history, open competitions) and `admin-dashboard.jsp` (moderation queue, registered users, platform metrics).
- **Restrict Dashboard Access without Login:** Implemented `AuthenticationFilter` and JSP session guards redirecting unauthenticated traffic to `login.jsp?error=unauthorized`.
- **Implement Logout & Session Invalidation:** Created `LogoutServlet` executing `session.invalidate()` and clearing client state.
- **Deploy and Execute on Web Server:** Packaged configuration into standard `WEB-INF/web.xml` ready for deployment on Apache Tomcat, paired with an integrated runner (`run.bat`).

---

## 6. Expected Outcomes

1. **Functional Registration and Login System:**
   - Error-free contestant registration with automated email uniqueness validation.
   - Dual-portal authentication supporting contestants and administrative users.
2. **Secure Session Management:**
   - Active `HttpSession` state persistence across page transitions.
   - Clean session invalidation upon explicit logout, preventing back-navigation session hijacking.
3. **User Dashboard Access Control:**
   - Contestants access personalized performance stats and competition submission tools.
   - Administrators access a dedicated control panel to approve/reject submissions and view system metrics.
   - Unauthorized access attempts to protected dashboard pages are automatically redirected.
4. **Easy Usability and Maintenance:**
   - Modular, decoupled architecture separating presentation (HTML/JSP), controller logic (Servlets), business logic (`UserOperations`), and persistence (`TalentDao`).
   - Clean dark-gold modern UI with responsive layouts for mobile and desktop screens.
