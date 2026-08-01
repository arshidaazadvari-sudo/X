# XClone – Twitter/X Clone

A desktop social media application inspired by Twitter/X, built with **Java**, **JavaFX**, and **PostgreSQL**.

---
## 📖 Table of Contents

- [Features](#-features)
- [Project Architecture](#-project-architecture)
- [Technologies](#-technologies)
- [Installation & Setup](#-installation--setup)
- [Database Schema](#-database-schema)
- [API Protocol](#-api-protocol)
- [Team Members](#-team-members)
- [Contributing](#-contributing)
- [Changelog](#-changelog)
- [Contact](#-contact)
- [License](#-license)
-
## 📌 Features

### ✅ Implemented

- **Authentication:** Register, login
- **Tweets:** Create, view, delete, and edit tweets
- **Personalized Feed:** Display tweets from user and followed users
- **Follow System:** Follow and unfollow users
- **Likes:** Like and unlike tweets
- **Replies:** Reply to tweets
- **Hashtags:** Auto-detection and search by hashtag
- **Media:** Upload and display images

---

## 🧱 Project Architecture

```

XClone/
├── shared/          # Shared code (models, protocol, utilities)
├── server/          # Server (database, DAO, handlers, services)
├── client/          # Client (JavaFX, controllers, views)
├── docs/            # Documentation (ERD, schema, API)
└── run/             # Execution scripts

```

---

### Communication Flow

- **Client ↔ Server:** JSON messages over TCP sockets
- **Server ↔ Database:** JDBC with PreparedStatement
- **Concurrency:** Thread pool for handling multiple clients

---

## 🛠️ Technologies

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| GUI | JavaFX 21 |
| Database | PostgreSQL 16 |
| Communication | Sockets + JSON (Jackson) |
| Build Tool | Maven |

---

## 🚀 Installation & Setup

### 1. Prerequisites

- Java 17 or higher
- PostgreSQL 16 or higher
- Maven 3.8 or higher (optional)

### 2. Clone the Repository

```bash
git clone https://github.com/arshidaazadvari-sudo/X.git
cd X
```

3. Database Setup

```bash
# Create database
psql -U postgres -c "CREATE DATABASE x_clone;"
psql -U postgres -c "CREATE USER ap_user WITH PASSWORD 'your password';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE x_clone TO ap_user;"

# Run schema script
psql -U postgres -d x_clone -f docs/schema.sql
```

4. Configure Database Connection

Open DatabaseConnection.java and update:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/x_clone";
private static final String USER = "ap_user";
private static final String PASSWORD = "your_password";
```

5. Run the Application

```bash
# Server
mvn exec:java -pl server -Dexec.mainClass="server.ServerLauncher"

# Client
mvn exec:java -pl client -Dexec.mainClass="ClientLauncher"
```

---

📂 Database Schema

docs/ERD.png

For detailed table descriptions and relationships, see docs/database-schema.md.

---
## 📝 API Protocol

Communication between client and server uses **JSON** messages.

### Request Format

```json
{
  "type": "LOGIN",
  "payload": {
    "username": "john_doe",
    "password": "123456"
  }
}
```
👥 Team Members

| Name | Responsibilities |
|------|------------------|
| Zahra | Database, DAO, Models, password hashing |
| Kimia | Server, Network, Handlers, Services |
| Arshida | Client, GUI, User Experience, controller |

---

🤝 Contributing

1. Create a new branch from develop
2. Apply your changes
3. Submit a Pull Request

---

📄 License

This project is developed for educational purposes.

---

Course: Advanced Programming – Summer 2026
Instructor: Dr. Saeed Reza Kheradpisheh

```
