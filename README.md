# 📢 **Content Aggregator Web Application**

## 📝 **Table of Contents**

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Architecture](#architecture)
5. [Project Structure](#project-structure)
6. [API Endpoints](#api-endpoints)
7. [Security](#security)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Future Improvements](#future-improvements)
11. [License](#license)

---

## 🚀 **Overview**

Content Aggregator is a full-stack web application that allows users to **fetch and view top news articles and Reddit posts** based on their preferences. Users can **log in via AWS Cognito authentication** and access personalized content in a clean, responsive interface.

---

## 🌟 **Features**

✅ User authentication with AWS Cognito (OAuth2 support)  
✅ Fetches top headlines from News APIs with customizable parameters  
✅ Retrieves trending Reddit posts with dynamic image handling  
✅ Responsive frontend built with HTML, CSS, and **Vue.js** integration  
✅ JWT-based secure backend powered by **Spring Boot**  
✅ Dynamic content loading upon user login  
✅ Metrics and performance monitoring with Spring Boot Actuator  
✅ Optimized for fast response times and minimal load latency

---

## 🛠️ **Tech Stack**

### **Frontend:**

- 🌐 HTML5,
- 🎨 CSS3
- 📜 JavaScript (ES6+)
- 🖥️ Vue.js (for dynamic routing & components)

### **Backend:**

- ☕ Spring Boot 3.x (RESTful API)
- 🔒 Spring Security (OAuth2 + JWT)
- 🧩 AWS Cognito for user authentication
- 📊 Spring Boot Actuator for monitoring
- 🌎 Integration with external News and Reddit APIs

### **Database:**

- 🗄️ PostgreSQL (for user preferences & article tracking)

---

## 🏗️ **Architecture**

```plaintext
+---------------------+          +--------------------------+
|      Frontend       |  <--->   |         Backend          |
|  (HTML/CSS/JS/Vue)  |          |    (Spring Boot + APIs)  |
+---------------------+          +--------------------------+
           |                                |
           |      +---------------------+  |
           +----->|   External APIs      |<+
                  | (NewsAPI & Reddit)   |
                  +---------------------+
```

- **Frontend:** Handles user interactions, token storage, and displays news and Reddit posts.
- **Backend:** Manages authentication, API integrations, and business logic.
- **Database:** Stores user preferences and fetched articles.

## 📡 **API Endpoints**

### 📖 **News Articles**

| Method | Endpoint                               | Description                   |
| ------ | -------------------------------------- | ----------------------------- |
| GET    | `/api/news/articles`                   | Fetches top news articles.    |
| GET    | `/api/news/sources/{apiType}/articles` | Fetches articles by source.   |
| GET    | `/api/news/categories/{category}`      | Fetches articles by category. |

### 📢 **Reddit Posts**

| Method | Endpoint            | Description               |
| ------ | ------------------- | ------------------------- |
| GET    | `/api/reddit/posts` | Fetches top Reddit posts. |

---

## 🔒 **Security**

- Utilizes **AWS Cognito** for user authentication.
- **OAuth2** and **JWT tokens** secure API access.
- Tokens are stored in **localStorage** on the frontend.
- Unauthorized users are redirected to login automatically.

---

## 🧪 **Testing**

### ✅ **Run Tests:**

```bash
./mvnw test
```

### 📊 **API Metrics:**

Monitor performance with Spring Boot Actuator:

- View metrics: `http://localhost:8080/actuator/metrics`
- Check endpoint response times:
  ```bash
  curl "http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/news/articles"
  ```

---

## 🚀 **Deployment**

### 🛡️ **Deploy on AWS:**

- Host frontend on **AWS S3 + CloudFront**.
- Deploy backend using **AWS Elastic Beanstalk** or **EC2**.
- Secure backend APIs with **AWS Cognito** + **API Gateway**.

---

## 📅 **Future Improvements**

✅ Implement caching for faster response times.  
✅ Add user-specific preferences for content filtering.  
✅ Improve mobile responsiveness and accessibility.  
✅ Add pagination to article fetching endpoints.

---

## 📝 **License**

This project is licensed under the MIT License. See `LICENSE` for more details.

---

🌟 **Content Aggregator** - Bringing news and community stories to your fingertips! 🌍
