# Distributed File Storage System (DFSS)

A learning-focused Distributed File Storage System built with a simple Spring Boot backend and a React/Vite frontend.

The project is being developed incrementally: first with local file storage and an H2 metadata database, then moving toward cloud/distributed storage such as AWS S3.

## Current Status

**Last updated:** 2026-08-09

Current backend foundation is working with:

- Spring Boot REST API
- Local file upload storage
- H2 file metadata database
- File listing from database metadata
- File metadata lookup by `fileId`
- File download by `fileId`
- File deletion logic being standardized around `fileId`
- Development security configuration
- H2 web console
- Multipart file upload limits
- Git ignore rules for local database/build/upload artifacts

## Tech Stack

### Backend
- Java
- Spring Boot 4.1.x
- Spring Web
- Spring Security
- Spring Data JPA
- H2 Database
- Maven / Maven Wrapper

### Frontend
- React
- Vite

### Current Storage
- Local filesystem

### Planned Storage
- AWS S3
- Storage abstraction for multiple providers
- Additional distributed/cloud storage support later

## Backend Package Structure

```text
backend/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── dfss/
│       │           └── backend/
│       │               ├── BackendApplication.java
│       │               ├── config/
│       │               │   └── SecurityConfig.java
│       │               ├── controller/
│       │               │   ├── HealthController.java
│       │               │   └── FileController.java
│       │               ├── dto/
│       │               │   ├── FileUploadResponse.java
│       │               │   ├── FileMetadataResponse.java
│       │               │   └── StoredFileResponse.java
│       │               ├── model/
│       │               │   └── FileMetadata.java
│       │               ├── repository/
│       │               │   └── FileMetadataRepository.java
│       │               └── service/
│       │                   └── LocalFileStorageService.java
│       └── resources/
│           └── application.properties
├── data/                 # ignored by Git
├── uploads/              # ignored by Git
└── target/               # ignored by Git
```

## Current File Metadata Model

Each newly uploaded file stores metadata similar to:

```json
{
  "fileId": "4b98a639-9504-4630-ad68-fd217a13ea53",
  "originalFileName": "metadata-test-2.txt",
  "storedFileName": "4b98a639-9504-4630-ad68-fd217a13ea53.txt",
  "contentType": "text/plain",
  "size": 22,
  "storageProvider": "LOCAL",
  "storagePath": ".../uploads/4b98a639-9504-4630-ad68-fd217a13ea53.txt",
  "uploadedAt": "2026-08-09T00:44:10.040776"
}
```

## API Endpoints

### Health
```http
GET /api/health
```

### Upload File
```http
POST /api/files/upload
```

Example:
```powershell
curl.exe -X POST -F "file=@C:\path\to\file.txt" http://localhost:8080/api/files/upload
```

### List Files
```http
GET /api/files
```

Example:
```powershell
curl.exe http://localhost:8080/api/files
```

### Get File Metadata by ID
```http
GET /api/files/metadata/{fileId}
```

### Download File by ID
```http
GET /api/files/download/{fileId}
```

### Delete File
Target canonical endpoint:
```http
DELETE /api/files/{fileId}
```

Delete should remove:
1. The physical file.
2. The corresponding H2 metadata row.

## H2 Database

```properties
spring.datasource.url=jdbc:h2:file:./data/dfss
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

H2 Console:
```text
http://localhost:8080/h2-console
```

Login:
```text
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:file:./data/dfss
User Name: sa
Password: blank
```

Useful query:
```sql
SELECT * FROM FILE_METADATA;
```

## Local File Configuration

```properties
file.upload-dir=uploads

spring.servlet.multipart.max-file-size=25MB
spring.servlet.multipart.max-request-size=25MB

server.servlet.session.persistent=false
```

## Running the Backend

```powershell
cd "E:\Project\Distributed-File-Storage-System\backend"
.\mvnw.cmd spring-boot:run
```

Successful startup should include:

```text
Tomcat started on port 8080
Started BackendApplication
```

`BUILD SUCCESS` alone does not guarantee that the server is running.

## Compile Check

```powershell
cd "E:\Project\Distributed-File-Storage-System\backend"
.\mvnw.cmd clean compile
```

Expected:

```text
BUILD SUCCESS
```

## Git Ignore Rules

Recommended local/generated ignores:

```gitignore
.vscode/

backend/target/

backend/data/
*.mv.db
*.trace.db

backend/uploads/

metadata-test.txt
metadata-test-2.txt
download-test.txt
```

Do not ignore the Maven wrapper or source:

```text
backend/.mvn/
backend/mvnw
backend/mvnw.cmd
backend/pom.xml
```

## Work Completed So Far

### Spring Boot Setup
- Backend starts successfully.
- Health endpoint created.
- Spring Security development configuration added.
- HTTP Basic/form login disabled for current local development routes.

### File Storage
- Multipart upload endpoint created.
- Files receive generated UUID-based stored names.
- Files are written to the local `uploads` directory.
- File size and content type are captured.
- File listing initially worked directly from the local filesystem.

### Database Metadata
- Spring Data JPA added.
- H2 persistent database added.
- `FileMetadata` entity created.
- `FileMetadataRepository` created.
- Metadata is saved when a new file is uploaded.
- `GET /api/files` converted to database-backed listing.
- `fileId` / `originalFileName` response mapping bug fixed and verified.

### Retrieval
- Metadata lookup by `fileId` added/planned in the current implementation.
- Download by `fileId` added/planned in the current implementation.
- Original filenames are preserved for downloads.

### Delete Workflow
- Earlier delete endpoint worked using `storedFileName`.
- Current direction is to use `fileId` as the canonical identifier.
- Physical file deletion and database metadata deletion must stay synchronized.

### Git
- Local H2 database caused a Git indexing permission error while the app held the DB file open.
- Database files and uploaded files were added to `.gitignore`.
- Maven wrapper and project source remain tracked.

## Important Development Notes

Use plain URLs in PowerShell. Do not paste Markdown links like:

```text
[http://localhost:8080/api/files](http://localhost:8080/api/files)
```

Use:

```powershell
curl.exe http://localhost:8080/api/files
```

Files uploaded before metadata persistence was implemented can exist physically without a matching H2 row.

## Next Session Starting Point

1. Verify canonical `fileId`-based delete.
2. Introduce a `StorageService` abstraction.
3. Refactor local storage behind that abstraction.
4. Add AWS S3 implementation.
5. Keep metadata independent of the storage provider.
6. Add provider-selection/fallback logic only after the abstraction works.
7. Connect the React/Vite frontend after the backend storage contract is stable.

Planned structure:

```text
StorageService
├── LocalStorageService
└── S3StorageService
```

The controller should not need to know whether a file is stored locally or in S3.

## Daily Development Rule

At the end of every work session:

- Update this README.
- Record completed work.
- Record tests that passed.
- Record unresolved issues.
- Record the exact next starting point.
- Commit the source changes.
- Push to the remote `main` branch.

## Git Push Workflow

From the repository root:

```powershell
cd "E:\Project\Distributed-File-Storage-System"

git status
git add .
git commit -m "Update DFSS backend progress"
git push origin main
```

A more descriptive commit for this session:

```powershell
git commit -m "Add database backed file metadata and retrieval"
git push origin main
```

## Current Milestone

**Local Storage + Metadata Foundation**

The project now has the core foundation needed to move from a simple local file server toward a proper distributed/cloud file storage architecture.
