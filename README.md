# CodeQuiz — Adaptive Online Quiz Portal

A full-stack enterprise-grade adaptive examination platform built with Java EE (Servlet/JSP/JDBC), MySQL, and Apache Tomcat 9.

## Features

### Student
- Register / Login / Logout with BCrypt password hashing
- Adaptive quiz engine (Easy → Medium → Hard based on performance)
- 10-minute countdown timer with auto-submit
- Anti-cheat: tab-switch detection (3-warning limit)
- Auto-save every 15 seconds — resume after accidental close
- Detailed post-quiz report with per-question review & explanations
- Topic-wise performance breakdown with mastery levels
- Leaderboard rankings
- Quiz history with accuracy charts

### Admin
- Dashboard with live platform KPIs
- Add / Edit / Delete questions with difficulty, tags, explanation
- Manage topic tags
- Analytics dashboard: score distributions, top performers, hardest/easiest questions
- Full leaderboard view

### Architecture
```
HTTP Request → AuthFilter → Servlet (thin controller)
                                ↓
                          Service Layer (business logic)
                                ↓
                           DAO Layer (JDBC)
                                ↓
                             MySQL
```

## Technology Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 21                           |
| Web         | Servlet 4.0, JSP 2.3, JSTL 1.2   |
| Database    | MySQL 8.x (JDBC)                  |
| Build       | Apache Maven 3.8+                 |
| Server      | Apache Tomcat 9                   |
| Security    | BCrypt (jBCrypt 0.4)              |
| JSON        | Gson 2.10.1                       |
| Frontend    | Bootstrap 5.3, Chart.js 4.4, Bootstrap Icons |

## Project Structure

```
quiz-portal/
├── database/
│   └── schema.sql              ← Run this first
├── src/main/java/com/quizportal/
│   ├── config/                 ← AppConstants (all magic strings)
│   ├── dao/                    ← Database access layer
│   ├── dto/                    ← Data transfer objects
│   ├── exception/              ← Custom exception hierarchy
│   ├── filter/                 ← Auth + encoding filters
│   ├── model/                  ← Domain models
│   ├── service/                ← Business logic
│   ├── servlet/                ← Thin HTTP controllers
│   ├── util/                   ← DB, session, password utils
│   └── validator/              ← Centralised input validation
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── views/              ← JSP pages
├── pom.xml
└── README.md
```

## Setup Instructions

### 1. Database Setup
```sql
mysql -u root -p < database/schema.sql
```
Edit `DBConnection.java` to set your MySQL credentials:
```java
private static final String USER     = "root";
private static final String PASSWORD = "your_password";
```

### 2. IntelliJ IDEA Setup
1. File → Open → select the `quiz-portal/` folder
2. Maven auto-import (trust the project)
3. File → Project Structure → SDK → Java 21
4. Add Tomcat 9 server: Run → Edit Configurations → + → Tomcat Local
5. Deployment tab → + → Artifact → `quiz-portal:war exploded`
6. Application context: `/quiz-portal`

### 3. Tomcat Setup
- Tomcat 9.x required (NOT Tomcat 10 — that uses jakarta.*)
- Set VM Options: `-Djava.util.logging.config.file=logging.properties`

### 4. Build & Run

```bash

mvn clean package

# Deploy quiz-portal.war to Tomcat webapps/
```

### 5. Default Admin Account
- **Email:** kukbhavesh@gmail.com  
- **Password:** 123456

## Demo Pages (for presentation)

1. `/login` — Login page
2. `/register` — Student registration  
3. `/admin/dashboard` — Admin overview
4. `/admin/analytics` — Charts and analytics
5. `/admin/questions` — Question management
6. `/quiz/tags` — Topic selection
7. `/quiz/resume` — Active quiz with timer
8. `/report?sessionId=...` — Detailed result report
9. `/leaderboard` — Global rankings
10. `/student/dashboard` — Student home with topic analytics
