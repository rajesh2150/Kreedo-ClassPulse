# ClassPulse

ClassPulse is a lightweight teacher feedback dashboard for logging quick notes about students and reviewing an at-a-glance sentiment summary.

## Features

- Student CRUD
- Feedback CRUD
- Student-specific feedback
- Sentiment classification
- Responsive React UI
- H2 database

## Tech Stack

Backend:
- Java
- Spring Boot
- Spring Data JPA
- H2
- Jakarta Validation

Frontend:
- React
- JavaScript
- CSS

Sentiment:
- Rule-based sentiment classifier

## Architecture

React
↓
REST API
↓
Spring Boot Service
↓
Repository
↓
H2

Feedback:
Feedback
↓
Student

Sentiment:
Feedback note
↓
SentimentService
↓
Positive / Neutral / Negative

## API Endpoints

### Students
- POST /api/students
- GET /api/students
- GET /api/students/{id}
- DELETE /api/students/{id}

### Feedback
- POST /api/feedback
- GET /api/feedback
- GET /api/feedback/{id}
- DELETE /api/feedback/{id}
- GET /api/students/{studentId}/feedback

## How To Run Backend

From the project root:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell, if you do not have a Maven wrapper:

```powershell
cd backend
mvn spring-boot:run
```

The backend runs on:
- http://localhost:8080

H2 console is enabled at:
- http://localhost:8080/h2-console

JDBC URL:
- jdbc:h2:mem:classpulse

## How To Run Frontend

From the project root:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on:
- http://localhost:5173

## URLs

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- H2 console: http://localhost:8080/h2-console

## Sentiment Approach

This implementation uses a lightweight deterministic rule-based sentiment classifier to keep the take-home application self-contained and cost-free. A production version could replace SentimentService with an LLM or pretrained model without changing the Feedback API.

Why this approach:
- no external API dependency
- no API key
- no cost
- deterministic
- easy to test
- replaceable with LLM or pretrained model later

## Design Decisions

- Layered backend architecture with controllers, services, repositories, and entities
- DTOs used for request/response payloads instead of JPA entities
- Jakarta Bean Validation for request validation
- Global exception handling for consistent 400/404/500 responses
- One-to-many Student/Feedback relationship with a simple H2 in-memory database
- Responsive React UI for desktop, tablet, and mobile workflows
- Sentiment logic abstracted behind SentimentService so it can be replaced later

## Trade-offs

This is intentionally a small, self-contained implementation. A production system could add:
- pretrained sentiment model
- LLM-based classification
- authentication
- PostgreSQL
- pagination
- audit logging
- better NLP coverage

## Future Improvements

- Add pagination and filtering for larger classrooms
- Add editing in place for students and feedback
- Add a richer sentiment model with NLP tuning or model-backed scoring
- Add stronger audit and retention features for teacher notes

## Notes

- Student deletion first removes associated feedback to keep the data model consistent.
- Feedback sentiment is generated server-side and is never taken from the frontend.
- The N+1 issue is avoided by using fetch-join repository queries for listing feedback and student-specific feedback.
