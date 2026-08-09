# Distributed File Storage System (DFSS)

A submission-focused Distributed File Storage System built with Spring Boot and React/Vite.

## Current Status

**Last updated:** 2026-08-09  
**Backend MVP status:** COMPLETE / FROZEN FOR FRONTEND WORK

### Completed backend capabilities
- Spring Boot REST API
- UUID-based file IDs
- Multipart file upload
- H2 metadata persistence
- Database-backed file listing
- Metadata lookup by `fileId`
- Download by `fileId`
- Delete by `fileId`
- Physical file + metadata deletion synchronization
- `StorageService` abstraction
- `StorageServiceManager`
- `LocalStorageService`
- `S3StorageService`
- AWS S3 upload/download/delete
- Provider-aware routing for LOCAL and S3 files
- H2 Console for development
- Development security configuration
- Multipart upload limits
- Git ignore rules for runtime/generated artifacts

## Tech Stack

### Backend
- Java 21
- Spring Boot 4.1.x
- Spring Web MVC
- Spring Security
- Spring Data JPA
- H2 Database
- AWS SDK for Java 2.x
- AWS S3
- Maven / Maven Wrapper

### Frontend
- React
- Vite

## Architecture

```text
React Frontend
      ↓
Spring Boot REST API
      ↓
FileController
      ↓
LocalFileStorageService
(metadata + orchestration)
      ↓
StorageServiceManager
      ↓
StorageService
   ↙             ↘
LOCAL             S3
   ↓               ↓
LocalStorage    AWS S3
      ↓
H2 Metadata Database
```

New uploads use the active provider in `storage.provider`. Existing files are downloaded/deleted using the provider stored in each metadata record.

## API Endpoints

```text
GET     /api/health
POST    /api/files/upload
GET     /api/files
GET     /api/files/metadata/{fileId}
GET     /api/files/download/{fileId}
DELETE  /api/files/{fileId}
```

## AWS S3

Bucket:

```text
dfss-omm-prakash-2026-001
```

Region:

```text
ap-southeast-2
Asia Pacific (Sydney)
```

Properties:

```properties
storage.provider=s3
aws.s3.region=ap-southeast-2
aws.s3.bucket=dfss-omm-prakash-2026-001
```

Do not commit AWS access keys or secret keys.

Useful commands:

```powershell
aws login
aws sts get-caller-identity
aws s3api head-bucket --bucket dfss-omm-prakash-2026-001 --region ap-southeast-2
aws s3 ls s3://dfss-omm-prakash-2026-001/
```

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
server.servlet.session.persistent=false
```

Console:

```text
http://localhost:8080/h2-console
```

Useful SQL:

```sql
SELECT * FROM FILE_METADATA;
```

## Tests Completed

### Local storage
- Backend startup ✅
- Health endpoint ✅
- Local upload ✅
- H2 metadata save ✅
- Database-backed listing ✅
- Metadata lookup by `fileId` ✅
- Download by `fileId` ✅
- Delete by `fileId` ✅
- Physical file removal ✅
- H2 metadata removal ✅
- `fileId` / `originalFileName` mapping bug fixed ✅

### Storage abstraction
- `StorageService` interface ✅
- `LocalStorageService` ✅
- `StorageServiceManager` ✅
- LOCAL file remains downloadable while active provider is S3 ✅

### AWS S3
Real test file ID:

```text
1f67607f-a3c4-45e5-bc92-cb17d419b1ab
```

Passed:
- S3 upload ✅
- `storageProvider = S3` metadata ✅
- `s3://...` storage path ✅
- Object visible in bucket ✅
- Metadata lookup ✅
- S3 download through API ✅
- Downloaded content matched original ✅
- S3 delete through API ✅
- H2 metadata removed after delete ✅
- S3 object removed after delete ✅

Verified downloaded content:

```text
S3 credentials fixed test
```

## Known / Deferred Items

Not part of current MVP:
- Production authentication / authorization
- PostgreSQL migration
- File sharing
- Chunked upload
- Replication
- Encryption management
- Google Cloud Storage
- Redis
- Kubernetes
- Advanced distributed algorithms

Current Spring Security configuration is development-only.

## Running the Backend

```powershell
cd "E:\Project\Distributed-File-Storage-System\backend"
.\mvnw.cmd spring-boot:run
```

Expected:

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

Make sure these are ignored:

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
local-after-s3-test.txt
s3-test.txt
s3-real-test.txt
s3-real-test-2.txt
s3-download-test.txt

README_DFSS_*.md
```

Do not ignore:
- `backend/.mvn/`
- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/pom.xml`
- `README.md`

## Next Session Starting Point

### React Frontend MVP

The backend should now remain frozen unless a real integration defect appears.

Next steps:
1. Inspect the existing React/Vite frontend.
2. Add backend API base configuration.
3. Build a simple dashboard.
4. Add file upload UI.
5. Add file list/table.
6. Show filename, type, size, storage provider and upload time.
7. Add Download action.
8. Add Delete action.
9. Add loading, success and error states.
10. Fix CORS only if frontend integration exposes an issue.
11. Run complete browser workflow.
12. Final UI cleanup and submission documentation.

## MVP Scope Lock

Included:

```text
Upload
List
Metadata
Download
Delete
LOCAL storage
AWS S3 storage
H2 metadata
React frontend
Spring Boot backend
Basic errors
README
Deployment
```

Deferred until after MVP:

```text
Login/signup
Roles
Sharing
Replication
Chunking
GCS
PostgreSQL
Redis
Docker/Kubernetes
Advanced distributed algorithms
```

## Current Milestone

**BACKEND MVP COMPLETE**

Next milestone:

**React Frontend MVP + Backend Integration**
