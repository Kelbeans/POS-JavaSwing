# POS-JavaSwing  

A **mock Point of Sale (POS)** system with a **Java Swing** frontend and a **Spring Boot** backend.  
This project demonstrates how a desktop-based POS client can communicate with a backend service for managing sales, inventory, and customer data.  

---

## ✨ Features  
- 🛒 Manage products, customers, and sales  
- 💵 Handle transactions and mock payment flow  
- 📊 Generate simple sales reports  
- 🖥️ Java Swing desktop UI for cashier operations  
- 🌐 Spring Boot backend (REST API) for business logic and data management  

---

## 🛠️ Tech Stack  
**Frontend (POS Client):**  
- Java (Core OOP)  
- Java Swing (Desktop GUI)  

**Backend (Mock Server):**  
- Spring Boot (Java)  
- REST APIs  
- In-memory database (H2) / or mock data  

---

## 🚀 Getting Started  

### Prerequisites  
- Java JDK 11+  
- Maven or Gradle  
- (Optional) MySQL / PostgreSQL if switching from H2  

### Installation  

1. **Clone the repository:**  
   ```bash
   git clone https://github.com/<your-username>/POS-JavaSwing.git
   cd POS-JavaSwing

2. **Backend (Spring Boot)**
   - From the project root (or the backend folder, if split), run:
     ```bash
     # Maven
     mvn spring-boot:run

     # or Gradle
     ./gradlew bootRun
     ```
   - The server will start at `http://localhost:8080`.
   - (Optional) If using H2, access the console at `http://localhost:8080/h2-console`.

3. **Frontend (Java Swing)**
   - Open the project in your IDE (IntelliJ/Eclipse/NetBeans).
   - Run the `Main` class to launch the desktop POS client.
   - The client communicates with the backend at `http://localhost:8080` (update the base URL in code or config if needed).

