# ClassPulse

ClassPulse is a lightweight teacher feedback dashboard for logging student notes, reviewing sentiment, and tracking classroom engagement through a responsive analytics dashboard.

## Features

- Student CRUD with add and delete flows
- Feedback CRUD with create, view, edit, and delete
- Student-specific feedback tracking
- Sentiment classification using rule-based scoring with optional Groq fallback
- Real-time dashboard cards for totals and sentiment counts
- Sentiment distribution donut chart
- Feedback-per-student bar chart
- Responsive React UI
- H2 in-memory database

## Tech Stack

Backend:
- Java 17
- Spring Boot 3.3.2
- Spring Data JPA
- H2 database
- Jakarta Validation

Frontend:
- React 18
- Vite
- Recharts
- CSS

Sentiment:
- Rule-based sentiment classifier
- Optional Groq API integration

## Architecture

React frontend
↓
REST API
↓
Spring Boot controllers and services
↓
JPA repositories
↓
H2 database

Feedback flow:
Feedback note
↓
SentimentService
↓
Positive / Neutral / Negative

## Dashboard Features

- Summary cards for Students, Total Feedback, Positive, Neutral, and Negative counts
- Donut chart for sentiment distribution
- Bar chart showing feedback count per student
- Feedback review cards with sentiment badges and edit/delete actions
- Student panel with note counts

## Screenshot Gallery

The application UI includes the following dashboard views:

- Dashboard summary and forms
- Sentiment distribution chart
- Feedback-per-student chart
- Feedback review cards and student list

Place exported screenshots in the folder below and then update the markdown with the final image names:

```text
docs/screenshots/
  dashboard-overview.png
  sentiment-chart.png
  feedback-per-student-chart.png
  feedback-review.png
```

Example placeholder format:

```md
![Dashboard overview](docs/screenshots/dashboard-overview.png)
![Sentiment distribution](docs/screenshots/sentiment-chart.png)
![Feedback per student](docs/screenshots/feedback-per-student-chart.png)
![Feedback review](docs/screenshots/feedback-review.png)
```

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
- PUT /api/feedback/{id}
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
- http://localhost:8081

H2 console is enabled at:
- http://localhost:8081/h2-console

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
- Backend: http://localhost:8081
- H2 console: http://localhost:8081/h2-console

## Sentiment Approach

This implementation uses a lightweight deterministic rule-based sentiment classifier by default to keep the app self-contained and cost-free. If a Groq API key is provided through the `GROQ_API_KEY` environment variable, the app can optionally use Groq for sentiment classification before falling back to the built-in rule-based logic.

Why this approach:
- no external API dependency by default
- no API key required for local use
- cost-free default path
- deterministic and easy to test
- replaceable with LLM or model-backed scoring later

Optional Groq configuration:
```powershell
$env:GROQ_API_KEY="your_key_here"
$env:GROQ_MODEL="llama-3.1-8b-instant"
```

## Design Decisions

- Layered backend architecture with controllers, services, repositories, and entities
- DTOs used for request/response payloads instead of JPA entities
- Jakarta Bean Validation for request validation
- One-to-many Student/Feedback relationship with a simple H2 in-memory database
- Responsive React dashboard for desktop, tablet, and mobile workflows
- Sentiment logic abstracted behind SentimentService so it can be replaced later
- Recharts used for sentiment and student analytics visualizations

## Trade-offs

This is intentionally a small, self-contained implementation. A production system could add:
- pretrained sentiment models
- LLM-powered classification
- authentication and roles
- PostgreSQL
- pagination and filtering
- audit logging
- richer analytics and retention features

## Future Improvements

- Add pagination and filtering for larger classrooms
- Add teacher-friendly search and sorting
- Add student edit in place
- Add a richer NLP or ML-backed sentiment engine
- Add stronger audit and retention features for teacher notes

## Notes

- Student deletion removes associated feedback to keep the dataset consistent.
- Feedback sentiment is generated server-side and is never taken from the frontend.
- Feedback edit actions re-run sentiment classification after the note is updated.
- The H2 database is in-memory, so data resets when the backend restarts.

## ScreenShots

<img width="1835" height="808" alt="image" src="https://github.com/user-attachments/assets/45941860-7f7c-45c4-a12c-ea0f544d039f" />
<img width="1588" height="812" alt="image" src="https://github.com/user-attachments/assets/22b510ee-a38f-4d9b-a5b1-5fa871158b77" />
<img width="1635" height="538" alt="image" src="https://github.com/user-attachments/assets/6135df9b-c65b-49db-a8ac-a3a16e5923e8" />
<img width="1705" height="757" alt="image" src="https://github.com/user-attachments/assets/44943c19-324a-48d8-95ea-180ab282121f" />



