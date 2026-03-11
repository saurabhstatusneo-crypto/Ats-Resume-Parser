# 🚀 TPMS Backend

A Spring Boot based backend service that provides APIs for resume parsing and data processing.
The project is containerized using Docker for easy deployment.

---

# ⚙️ Setup Instructions

## 1️⃣ Configure Groq API Key

Open the `application.properties` file and update your Groq API key:

```
groq.api.key=ENTER_YOUR_GROQ_API_HERE
```

---

# 📚 API Documentation

This project uses **Swagger UI** for API documentation and testing.

Open the following URL in your browser:

```
http://localhost:8080/swagger-ui/index.html#/
```

You can test all available APIs directly from this interface.

---

# 🗄 Database Console

The project uses **H2 Database**.

To access the H2 console, open:

```
http://localhost:8080/h2-console
```

From here you can:

* View database tables
* Run SQL queries
* Inspect schema and stored data

---

# 🐳 Run Using Docker

You can run the application directly from Docker Hub.

### Pull the Image

```
docker pull hoshiyar9351/tmps-backend:latest
```

### Run the Container

```
docker run -p 8080:8080 hoshiyar9351/tmps-backend:latest
```

After running the container, open:

```
http://localhost:8080
```

---

# 🔗 Docker Repository

Docker Image available here:

https://hub.docker.com/r/hoshiyar9351/tmps-backend

---

# 🛠 Tech Stack

* Java
* Spring Boot
* H2 Database
* Swagger (OpenAPI)
* Docker

---

