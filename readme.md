# Quiz-Portal — Adaptive Online Quiz Portal

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Servlet Version](https://img.shields.io/badge/Servlet-4.0-blue.svg?style=flat-square&logo=eclipseche)](https://jakarta.ee/)
[![Server](https://img.shields.io/badge/Tomcat-9.x-green.svg?style=flat-square&logo=apachetomcat)](https://tomcat.apache.org/)
[![Database](https://img.shields.io/badge/MySQL-8.x-blue.svg?style=flat-square&logo=mysql)](https://www.mysql.com/)

An enterprise-grade, full-stack adaptive examination platform engineered using **Java EE (Servlet/JSP/JDBC)**, **MySQL**, and **Apache Tomcat 9**. **Quiz Portal** leverages an intelligent item response algorithm that dynamically scales assessment difficulty based on live student performance metrics, backed by real-time proctoring features.

---

##  Key Features

###  Student Subsystem
* **Secure Authentication:** Registration, Login, and Session management fortified with **BCrypt password hashing** via `jBCrypt`.
* **Adaptive Testing Engine:** Dynamically transitions question difficulty levels (Easy → Medium → Hard) in real-time based on the trailing performance matrix of the examinee.
* **State Persistence Engine:** Periodic **auto-save intervals every 15 seconds** using asynchronous AJAX operations. Sessions safely resume uninterrupted in the event of hardware failures or accidental window termination.
* **Anti-Cheat Proctoring:** Automated browser window/tab-switch interception using the Page Visibility API. Tracks occurrences and enforces an immutable **3-warning hard threshold** before performing an immediate auto-submission.
* **Precision Countdown Engine:** Integrated 10-minute timer that triggers automated data collection and submission operations upon expiration.
* **Granular Performance Analysis:**
    * Post-quiz breakdowns with full per-question review logs and descriptive resolution explanations.
    * Topic-based subject mastery tracking visualized through real-time responsive analytics charts.
    * Global historical tracking logs with comprehensive accuracy trend graphs.
* **Global Standings:** Dynamic, real-time leaderboard calculations ranking platform peers.

###  Administrative Control Center
* **Executive Dashboard:** Centralized operation hub highlighting core platform KPIs (Active Sessions, Average Mastery Scores, Test Completion Rates).
* **Item Bank Management:** Full CRUD mechanisms for questions, allowing specific mappings for difficulty weights, custom explanation parameters, and contextual metadata.
* **Tag Architecture Management:** Standardized taxonomy controls for managing topic nodes and categorization paths.
* **Business Intelligence & Analytics:** Extensive analytical reporting showcasing raw score distribution curves, top student performances, and structural question vulnerability maps (identifying anomalous hardest/easiest questions).
* **Global User Audit:** High-level leaderboard views mapping cross-platform operational outcomes.

---

##  Architecture & Core Patterns

The platform adheres to an enterprise multi-tier decoupling strategy, enforcing clean separation of concerns and safeguarding data isolation.

### Flow Mechanics
1. **Request Interception:** `AuthFilter` intercepts inbound actions to validate state constraints, ensure role-based authorization, and resolve character encoding properties.
2. **Controller Routing:** Thin Servlets process requests, delegate input verification parameters to a centralized validation engine, and route variables down the stack.
3. **Business Processing:** The Service layer executes transactional workflows, updates state trees, and evaluates adaptive scoring paths.
4. **Data Isolation:** DAOs isolation-wrap target ANSI SQL executions via explicit Data Sources, utilizing `PreparedStatement` to comprehensively nullify SQL injection profiles.

---

##  Technology Stack

| Layer | Component Technology | Version / Specification | Description |
| :--- | :--- | :--- | :--- |
| **Language Runtime** | Java OpenJDK | 21 | Leveraging modern enterprise-grade LTS framework features. |
| **Web Container** | Jakarta EE (Java EE) | Servlet 4.0 / JSP 2.3 / JSTL 1.2 | Core servlet handling with structured presentation layers. |
| **Application Server**| Apache Tomcat | 9.0.x | Thread-pooled HTTP application execution runtime. |
| **Database** | MySQL Server | 8.x | Relational engine storing persistent normalized data maps. |
| **Build Automation** | Apache Maven | 3.8+ | Dependency lifecycle management and structural build packaging. |
| **Cryptographic Unit**| jBCrypt | 0.4 | Blowfish key-stretching cipher implementation for credential security. |
| **Data Parsing** | Google Gson | 2.10.1 | High-throughput reflection-based JSON serialization engine. |
| **UI Presentation** | Bootstrap | 5.3.x | Fully responsive utility-first CSS styling architecture. |
| **Data Visualization**| Chart.js | 4.4.x | Canvas-driven, vector-mapped frontend analytics rendering. |
| **Asset Packages** | Bootstrap Icons | 1.11.x | Vector icon distribution packages for user interface components. |

---

##  Project Directory Taxonomy

```
quiz-portal/
├── database/
│   └── schema.sql              # Relational database definition mappings (Execute First)
├── src/main/java/com/quizportal/
│   ├── config/                 # AppConstants and immutables mapping strings/keys
│   ├── dao/                    # Data Access Objects (Data layer communication)
│   ├── dto/                    # Data Transfer Objects for cross-tier data isolation
│   ├── exception/              # Custom functional exception class definitions
│   ├── filter/                 # Stateful Security Filters & Encoding Sanitizers
│   ├── model/                  # Concrete Plain Old Java Objects (POJOs) matching database schemas
│   ├── service/                # Business logic, state calculation, and adaptive metrics
│   ├── servlet/                # High-throughput Controller routing handlers
│   ├── util/                   # Connection Pooling, Session helpers, and Hash engines
│   └── validator/              # Centralized payload and request sanitization engine
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml             # Deployment descriptor mapping core system declarations
│   │   └── views/              # Secure encapsulated JSP presentation layouts
│   └── assets/                 # Client assets (CSS stylesheets, modular JS, images)
├── pom.xml                     # Core Maven project object model dependency definitions
└── README.md                   # System documentation

```
---

##  Engineering & Setup Guidelines

### 1. Relational Layer Initialization
Provision your target database environment by compiling the schema layer directly inside your operational instance:

```bash
mysql -u root -p < database/schema.sql


Once provisioned, configure the system's explicit credentials by accessing the runtime connection utility located at src/main/java/com/quizportal/util/DBConnection.java:

private static final String USER     = "YOUR_DATABASE_USER";
private static final String PASSWORD = "YOUR_DATABASE_PASSWORD";
```


### 2. Integrated Development Environment Configuration (IntelliJ IDEA)
Launch IntelliJ IDEA, choose Open, and select the root directory path containing the target quiz-portal/ deployment node.

Accept the prompt context to Trust Project and let Maven complete its automated dependency indexing routines.

Access File → Project Structure → Project, then explicitly declare the operational SDK to Java 21.

Configure the Tomcat Application Server:

Navigate to Run → Edit Configurations.

Click the + (Add New Configuration) symbol and select Tomcat Server → Local.

Define your platform's local installation home path for Apache Tomcat 9.

Map the Output Artifact:

Shift focus to the Deployment configuration tab.

Click + → Artifact and choose quiz-portal:war exploded.

Define the target Application context value as: /quiz-portal.

3. Application Server Tuning Notes
Server Variant Requirements: This software artifact demands Tomcat 9.x or earlier. Deployment inside Tomcat 10+ will fail due to namespace changes from the standard Java EE specification (javax.servlet.*) to the Jakarta EE specification (jakarta.servlet.*).

Logging Output Properties: Attach the following instruction inside your active Tomcat instance configuration under VM Options to ensure consistent log output streaming:



### 4. Compilation & Deployment Execution
Execute standard Maven pipeline tasks to cleanly compile, run structural validations, and build your package file:

```bash
mvn clean package
```

## Seeding Parameters
For deployment testing, presentation reviews, and live validations, use these default administrative root credentials:

Administrative Account Identifier: kukbhavesh@gmail.com

Administrative Access Secret: 123456

## Core Endpoint Route Map
The application layer handles routing through these explicit target paths during live execution profiles:

```
Client / Student Endpoints
/login — Gateway interface authentication checkpoint.

/register — Profile onboarding endpoint for new student registrations.

/quiz/tags — Metadata filtering engine for topic and track selection.

/quiz/resume — Live state exam terminal featuring runtime proctor trackers and countdown engine.

/report?sessionId=[ID] — Detailed analytical post-exam summary and question validation portal.

/student/dashboard — Main landing interface displaying individualized tracking matrices.

/leaderboard — Unified ranking breakdown displaying active platform leaders.

Administrative Endpoints
/admin/dashboard — Platform executive operations tower containing platform health KPIs.

/admin/analytics — Dynamic analytics reporting page visualizing question hardness maps and performance data.

/admin/questions — Control hub for executing item-bank maintenance and full CRUD adjustments.

```

## Developed By : Bhavesh Kukreja and Chandan Kawadker

<img width="1919" height="963" alt="Screenshot 2026-05-27 184827" src="https://github.com/user-attachments/assets/7946e50a-cb58-4d39-accd-e75aeec4cf9a" />
<br><br>
<img width="1919" height="972" alt="Screenshot 2026-05-27 184843" src="https://github.com/user-attachments/assets/706d85bb-513c-4b9b-a40c-d92a97a7a338" />
<br><br>
<img width="1919" height="961" alt="Screenshot 2026-05-27 184904" src="https://github.com/user-attachments/assets/f9fb10fa-b7d6-452d-916e-4ab05eadd372" />
<br><br>
<img width="1919" height="963" alt="Screenshot 2026-05-27 184919" src="https://github.com/user-attachments/assets/5fd13e48-ea21-4e7c-9998-0eeede2cd155" />
<br><br>
<img width="1919" height="950" alt="Screenshot 2026-05-27 184937" src="https://github.com/user-attachments/assets/e06bee89-4386-45e3-ab0f-57af5a5514e0" />
<br><br>
<img width="1919" height="965" alt="Screenshot 2026-05-27 184953" src="https://github.com/user-attachments/assets/a20d0f93-0d2b-486b-b5e9-e08506e57048" />
<br><br>
<img width="1885" height="967" alt="Screenshot 2026-05-27 185017" src="https://github.com/user-attachments/assets/b4121c4e-e2b8-42a3-918c-d033c0743ffd" />
<br><br>
<img width="1919" height="971" alt="Screenshot 2026-05-27 185042" src="https://github.com/user-attachments/assets/b076f41a-2aa0-412d-bf47-0c60a8e1760e" />
<br><br>
<img width="1886" height="937" alt="Screenshot 2026-05-27 185109" src="https://github.com/user-attachments/assets/24eb2991-9d14-4abc-8386-74fa65cbb699" />
<br><br>
<img width="1853" height="948" alt="Screenshot 2026-05-27 185125" src="https://github.com/user-attachments/assets/d188e084-c715-44de-aca4-39857b2f3381" />
<br><br>
<img width="1887" height="957" alt="Screenshot 2026-05-27 185141" src="https://github.com/user-attachments/assets/de8f7c46-3cc2-4f84-a868-7199f5ed4474" />
<br><br>
<img width="1894" height="942" alt="Screenshot 2026-05-27 185154" src="https://github.com/user-attachments/assets/95647daa-88b3-4533-ba3d-0c2923bf4699" />
