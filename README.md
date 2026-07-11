# XClone – Twitter/X Clone

A desktop social media application inspired by Twitter/X, built with **Java**, **JavaFX**, and **PostgreSQL**.

---

## 📌 Features

### ✅ Implemented

- **Authentication:** Register, login, logout with BCrypt password hashing
- **Tweets:** Create, view, delete, and edit tweets
- **Personalized Feed:** Display tweets from user and followed users
- **Follow System:** Follow and unfollow users
- **Likes:** Like and unlike tweets
- **Replies:** Reply to tweets
- **Hashtags:** Auto-detection and search by hashtag
- **Media:** Upload and display images

### ⏳ In Progress

- Advanced search
- Notifications
- Dark mode

---

## 🧱 Project Architecture

```

XClone/
├── shared/          # Shared code (models, protocol, utilities)
├── server/          # Server (database, DAO, handlers)
├── client/          # Client (JavaFX, controllers, views)
├── docs/            # Documentation (ERD, schema, API)
└── run/             # Execution scripts

```

---

## 🛠️ Technologies

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| GUI | JavaFX 21 |
| Database | PostgreSQL 16 |
| Communication | Sockets + JSON (Gson) |
| Password Hashing | BCrypt |
| Build Tool | Maven |

---

## 🚀 Installation & Setup

### 1. Prerequisites

- Java 17 or higher
- PostgreSQL 16 or higher
- Maven 3.8 or higher (optional)

### 2. Clone the Repository

```bash
git clone https://github.com/your-team/xclone.git
cd xclone
```

3. Database Setup

```bash
# Create database
psql -U postgres -c "CREATE DATABASE x_clone;"

# Run schema script
psql -U postgres -d x_clone -f docs/database-schema.sql
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
mvn exec:java -pl server -Dexec.mainClass="com.twitter.server.Server"

# Client
mvn exec:java -pl client -Dexec.mainClass="com.twitter.client.ClientApp"
```

---

📂 Database Schema

docs/ERD.png

For detailed table descriptions and relationships, see docs/database-schema.md.

---

👥 Team Members

Role Name
Person Zahra Database, DAO, Models, Services
Person Kimia Server, Network, Handlers
Person Arshida Client, GUI, User Experience

---

📝 API Documentation

Request and response formats are described in docs/api-protocol.md.

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