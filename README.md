# Notes API

A RESTful API for managing personal notes, built with **Spring Boot 3.4** and **Java 17+**.

## Features

- Create, read, update, and delete notes
- User ownership for each note
- Input validation with detailed error messages
- H2 in-memory database for development
- Health monitoring with Spring Boot Actuator

## Tech Stack

- Java 17+
- Spring Boot 3.4.12
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven Wrapper)

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/laylarodas/notes-api-springboot.git
cd notes-api-springboot
```

2. **Run the application**

```bash
./mvnw spring-boot:run
```

On Windows:
```bash
mvnw.cmd spring-boot:run
```

3. **Verify the application is running**

Open your browser and navigate to:
```
http://localhost:8080/actuator/health
```

You should see:
```json
{
  "status": "UP"
}
```

## API Endpoints

### Notes

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/notes` | Create a new note |
| `GET` | `/notes` | Get all notes |
| `GET` | `/notes/{id}` | Get a note by ID |
| `PUT` | `/notes/{id}` | Update a note |
| `DELETE` | `/notes/{id}` | Delete a note |

### Request & Response Examples

#### Create a Note

**Request:**
```http
POST /notes
Content-Type: application/json

{
  "title": "My First Note",
  "content": "This is the content of my note.",
  "userId": 1
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "My First Note",
  "content": "This is the content of my note.",
  "createdAt": "2024-12-24T10:30:00",
  "archived": false
}
```

#### Get All Notes

**Request:**
```http
GET /notes
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "My First Note",
    "content": "This is the content of my note.",
    "createdAt": "2024-12-24T10:30:00",
    "archived": false
  }
]
```

#### Get a Note by ID

**Request:**
```http
GET /notes/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "My First Note",
  "content": "This is the content of my note.",
  "createdAt": "2024-12-24T10:30:00",
  "archived": false
}
```

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Note with id 99 was not found",
  "timestamp": "2024-12-24T10:35:00"
}
```

#### Update a Note

**Request:**
```http
PUT /notes/1
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content.",
  "archived": true
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Updated Title",
  "content": "Updated content.",
  "createdAt": "2024-12-24T10:30:00",
  "archived": true
}
```

#### Delete a Note

**Request:**
```http
DELETE /notes/1
```

**Response:** `200 OK`
```json
{
  "message": "Note deleted successfully"
}
```

### Validation Errors

If you send an invalid request, you will receive a `400 Bad Request` response:

```json
{
  "timestamp": "2024-12-24T10:40:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/notes"
}
```

**Validation Rules:**
- `title`: Required, max 200 characters
- `userId`: Required (must reference an existing user)
- `content`: Optional

## H2 Database Console

The H2 Console allows you to view and manage the in-memory database directly from your browser.

### Access the Console

1. Open your browser and navigate to:
   ```
   http://localhost:8080/h2-console
   ```

2. Use the following connection settings:

   | Field | Value |
   |-------|-------|
   | JDBC URL | `jdbc:h2:mem:notesdb` |
   | Username | `sa` |
   | Password | *(leave empty)* |

3. Click **Connect**

### Create a Test User

Before creating notes, you need to create a user. Run this SQL in the H2 Console:

```sql
INSERT INTO users (name, email, created_at) 
VALUES ('Test User', 'test@example.com', NOW());
```

## Actuator Endpoints

Spring Boot Actuator provides health and monitoring endpoints.

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | `http://localhost:8080/actuator/health` | Application health status |
| Info | `http://localhost:8080/actuator/info` | Application information |
| Metrics | `http://localhost:8080/actuator/metrics` | Application metrics |

### Health Endpoint

**Request:**
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Info Endpoint

**Request:**
```http
GET /actuator/info
```

**Response:**
```json
{
  "app": {
    "name": "Notes API",
    "version": "1.0.0",
    "description": "API REST para gestionar notas"
  }
}
```

## Project Structure

```
src/main/java/dev/layla/notesapi/
├── NotesApiApplication.java
├── common/
│   └── GlobalExceptionHandler.java
├── health/
│   └── HealthController.java
├── note/
│   ├── Note.java
│   ├── NoteController.java
│   ├── NoteRepository.java
│   ├── NoteService.java
│   ├── dto/
│   │   ├── CreateNoteRequest.java
│   │   ├── NoteResponse.java
│   │   └── UpdateNoteRequest.java
│   ├── exception/
│   │   └── NoteNotFoundException.java
│   └── mapper/
│       └── NoteMapper.java
└── user/
    ├── User.java
    ├── UserRepository.java
    └── exception/
        └── UserNotFoundException.java
```

## License

This project is open source and available under the [MIT License](LICENSE).

## Author

**Layla Rodas**

- GitHub: [@laylarodas](https://github.com/laylarodas)

