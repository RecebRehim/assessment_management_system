# Assessment Management System

Spring Boot REST backend: istifadəçi qiymətləndirmə (assessment) yaradır, nəticəni saxlayır və istəyə bağlı LLM ilə qısa mətn xülasəsi alır.

LLM **score-u dəyişmir** və **business qərarı vermir**. `FAIL` / `PASS` / `EXCELLENT` yalnız serverdə, `0–100` bal əsasında hesablanır.

## Texnologiyalar

- Java 21
- Spring Boot 4.1
- Spring Web MVC, Spring Data JPA, Spring Validation, Spring Security
- PostgreSQL 16
- JUnit 5, Mockito, MockMvc, H2 (yalnız test)
- Docker / Docker Compose
- GitHub Actions (build + tests)
- Ollama (istəyə bağlı AI xülasə)

## Arxitektura

```
Client
  → Controller
    → Service (business logic)
      → Repository
        → PostgreSQL
```

- Controller yalnız HTTP qəbul edir və DTO qaytarır.
- Service: unikal email, bir assessment üçün bir nəticə, score-dan `resultStatus`.
- Repository yalnız data access-dir.
- Security: HTTP Basic, `ADMIN` və `ANALYST`.

### Rollar

| Əməliyyat | ADMIN | ANALYST |
| --- | --- | --- |
| `POST /users` | bəli | xeyr |
| `POST /assessments`, `PUT /assessments/{id}` | bəli | xeyr |
| `POST /assessments/{id}/result` | bəli | xeyr |
| `GET /assessments`, `GET /assessments/{id}` | bəli | bəli |
| `GET /assessments/{id}/result` | bəli | bəli |
| `POST /assessments/{id}/ai-summary` | bəli | bəli |

Nəticə statusu:

- `0–49` → `FAIL`
- `50–79` → `PASS`
- `80–100` → `EXCELLENT`

## Necə işə salınır

API: `http://localhost:8081`

Default demo istifadəçilər:

- ADMIN: `admin@assessment.local` / `Admin123!`
- ANALYST: `analyst@assessment.local` / `Analyst123!`

### Docker (tövsiyə olunur)

Windows-da `docker compose` susursa `docker.exe` yazın:

```powershell
docker.exe compose up --build
```

Linux/macOS:

```bash
docker compose up --build
```

Host **8081** → konteyner **8080**. Lokal Maven run üçün Postgres host-da **5433**-dür (`localhost:5432` deyil).

### Local (Maven + Postgres)

1. Postgres işləsin (`docker.exe start task-postgres` və ya yalnız `postgres` servisi).
2. Tətbiqi işə salın:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Default datasource: `jdbc:postgresql://localhost:5433/assessment` (user/password: `assessment`).

## API endpoint-lər

Bütün endpoint-lər HTTP Basic tələb edir. PowerShell-də `curl` alias-dır — `curl.exe` yazın. JSON-u birbaşa `-d` ilə verməyin; fayldan göndərin.

### Users

`POST /users`

```json
{
  "name": "Leyla",
  "email": "leyla@example.com",
  "password": "password1",
  "role": "ANALYST"
}
```

### Assessments

`POST /assessments`

```json
{
  "title": "Access review",
  "description": "Quarterly access review"
}
```

`GET /assessments`

`GET /assessments/{id}`

`PUT /assessments/{id}`

```json
{
  "title": "Access review",
  "description": "Updated description",
  "status": "ACTIVE"
}
```

`POST /assessments/{id}/result`

```json
{
  "score": 81
}
```

`GET /assessments/{id}/result`

`POST /assessments/{id}/ai-summary`

AI üçün lokal Ollama (`http://localhost:11434`, model: `llama3`). Ollama yoxdursa endpoint **503** qaytarır; mövcud score və `resultStatus` dəyişmir.

Nümunə (PowerShell):

```powershell
Set-Content -Path body.json -Value '{"title":"Access review","description":"Q1"}' -Encoding ascii -NoNewline
curl.exe -u admin@assessment.local:Admin123! -H "Content-Type: application/json" -X POST --data-binary "@body.json" http://localhost:8081/assessments
```

Yanlış input zamanı API `application/problem+json` qaytarır (`400`, `401`, `403`, `404`, `409`, `503`).

## Testlər

Postgres lazım deyil (H2):

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

- Service testləri (JUnit + Mockito)
- Bean Validation
- WebMvc security (`ADMIN` / `ANALYST`)
- API integration (`@SpringBootTest` + MockMvc)

CI hər `push` və `pull request` zamanı `./mvnw -B verify` işlədir.
