# Distributed File Storage System (DFSS)

A learning-focused Distributed File Storage System built with Spring Boot and React/Vite.

The project is being developed incrementally: local storage first, then a storage abstraction, then cloud/distributed storage such as AWS S3.

## Current Status

**Last updated:** 2026-08-09

### Current milestone
**Local Storage + H2 Metadata + Storage Abstraction: COMPLETE**

The backend now supports:
- Spring Boot REST API
- Local file upload
- UUID-based file IDs
- H2 metadata persistence
- Database-backed file listing
- Metadata lookup by `fileId`
- File download by `fileId`
- File delete by `fileId`
- Physical file + metadata deletion synchronization
- `StorageService` abstraction
- `LocalStorageService` implementation
- Storage provider metadata (`LOCAL`)
- H2 Console for development
- Development security configuration
- Multipart upload limits
- Git ignore rules for generated/local files

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

### Current Storage Provider
- Local filesystem

### Next Storage Provider
- AWS S3

## Storage Architecture

```text
FileController
      ↓
LocalFileStorageService
(metadata + orchestration)
      ↓
StorageService
      ↓
LocalStorageService
      ↓
Local filesystem
```

Planned:

```text
StorageService
├── LocalStorageService
└── S3StorageService
```

## API Endpoints

```text
GET     /api/health
POST    /api/files/upload
GET     /api/files
GET     /api/files/metadata/{fileId}
GET     /api/files/download/{fileId}
DELETE  /api/files/{fileId}
```

## Tests Completed

### Core
- Backend startup verified.
- `/api/health` verified.
- File upload verified.
- H2 connection verified.
- Metadata persistence verified.
- Database-backed file listing verified.
- `fileId` / `originalFileName` response mapping fixed.
- `fileId`-based delete verified.
- Physical file deletion verified.
- Metadata deletion verified.

### 2026-08-09 Storage Abstraction Verification

Test file:

```text
storage-test.txt
```

Generated file ID:

```text
f39b16f8-e060-4daa-ab69-68dd48ce4331
```

Passed:
- `POST /api/files/upload` ✅
- `GET /api/files` ✅
- `storageProvider = LOCAL` ✅
- `GET /api/files/metadata/{fileId}` ✅
- `GET /api/files/download/{fileId}` ✅
- Downloaded file content matched original ✅

The new `StorageService` abstraction did not break the existing local-storage flow.

## H2 Configuration

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

H2 console:

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

## Local Storage Configuration

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

## Compile Check

```powershell
cd "E:\Project\Distributed-File-Storage-System\backend"
.\mvnw.cmd clean compile
```

Expected:

```text
BUILD SUCCESS
```

## Git Ignore

Recommended:

```gitignore
.vscode/

backend/target/
backend/data/
*.mv.db
*.trace.db
backend/uploads/

metadata-test.txt
metadata-test-2.txt
storage-test.txt
storage-download-test.txt
download-test.txt
```

Do not ignore:
- `backend/.mvn/`
- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/pom.xml`

## Known Issues / Deferred Work

- Files created before metadata persistence may not have H2 metadata rows.
- Current Spring Security setup is development-only.
- H2 is development metadata storage only.
- Local filesystem storage is not distributed storage.
- AWS credentials/configuration have not been added yet.
- Frontend integration is deferred until the backend storage contract is stable.

## Next Session Starting Point

### AWS S3 Integration

Start here next:

1. Add AWS SDK S3 dependency.
2. Add S3 configuration properties.
3. Create an S3 client/configuration bean.
4. Implement `S3StorageService implements StorageService`.
5. Keep `LocalStorageService` unchanged.
6. Add configuration-based storage provider selection.
7. Test S3 upload.
8. Verify metadata shows `storageProvider = S3`.
9. Test download through the existing API.
10. Test delete from both S3 and H2.

Important design rule:

```text
FileController should not change for S3.
```

Only the active `StorageService` implementation should change.

## Daily Development Rule

At the end of each work session:
- Update this README.
- Record completed work.
- Record passed tests.
- Record unresolved issues.
- Record the exact next starting point.
- Provide exact Git stage/commit/push commands.

## Git Push for This Session

From repository root:

```powershell
cd "E:\Project\Distributed-File-Storage-System"

git status
git add .
git commit -m "Add storage service abstraction and verify local storage flow"
git push origin main
```

If test files appear in `git status`, add them to `.gitignore` before committing.

## Current Milestone

**Local Storage + Metadata + Storage Abstraction — COMPLETE**

Next milestone: **AWS S3 storage provider integration**.
