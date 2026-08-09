# Distributed File Storage System (DFSS)

A full-stack MVP for storing, listing, downloading, and deleting files using Local and AWS S3 storage.

## Live Deployment

- Frontend: https://distributed-file-storage-system-ten.vercel.app/
- Backend: https://distributed-file-storage-system-production.up.railway.app/
- Health API: https://distributed-file-storage-system-production.up.railway.app/api/health

## Project Status

**MVP COMPLETE — 2026-08-09**

### Backend
- Spring Boot REST API ✅
- Java 21 ✅
- H2 metadata database ✅
- Local filesystem storage ✅
- AWS S3 storage ✅
- StorageService abstraction ✅
- StorageServiceManager provider routing ✅
- Upload/List/Metadata/Download/Delete ✅
- Railway deployment ✅

### Frontend
- React + Vite ✅
- Axios API layer ✅
- Dashboard ✅
- File upload/listing ✅
- LOCAL/S3 provider badges ✅
- Download/Delete/Refresh ✅
- Loading/error/success states ✅
- Backend health indicator ✅
- Vercel deployment ✅

### Production Integration
- Vercel → Railway ✅
- Railway → H2 ✅
- Railway → AWS S3 ✅
- Production upload/download/delete ✅
- Full browser flow ✅

## Architecture

```text
React + Vite (Vercel)
        |
        | /api/*
        v
Spring Boot REST API (Railway)
        |
        +--------------------+
        |                    |
        v                    v
H2 Metadata DB        StorageServiceManager
                             |
                      +------+------+
                      |             |
                      v             v
                 LOCAL Storage    AWS S3
```

New uploads use the configured active provider. Existing files use the `storageProvider` stored with their metadata, so LOCAL and S3 records can coexist.

## Tech Stack

### Backend
- Java 21
- Spring Boot 4.1.x
- Spring Web MVC
- Spring Data JPA
- Spring Security
- H2
- AWS SDK for Java 2.x
- AWS S3
- Maven

### Frontend
- React
- Vite
- Axios
- CSS

### Deployment
- Frontend: Vercel
- Backend: Railway
- Object storage: AWS S3

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Backend health check |
| POST | `/api/files/upload` | Upload file |
| GET | `/api/files` | List metadata |
| GET | `/api/files/metadata/{fileId}` | Get metadata |
| GET | `/api/files/download/{fileId}` | Download file |
| DELETE | `/api/files/{fileId}` | Delete file and metadata |

## Main Backend Structure

```text
backend/src/main/java/com/dfss/backend/
├── config/
│   ├── AwsS3Config.java
│   └── SecurityConfig.java
├── controller/
│   ├── FileController.java
│   └── HealthController.java
├── dto/
├── model/
│   └── FileMetadata.java
├── repository/
│   └── FileMetadataRepository.java
└── service/
    ├── StorageService.java
    ├── StorageServiceManager.java
    ├── LocalStorageService.java
    ├── S3StorageService.java
    └── LocalFileStorageService.java
```

## Frontend Structure

```text
frontend/src/
├── api.js
├── App.jsx
├── App.css
├── index.css
└── main.jsx
```

## Local Development

Backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173` and backend at `http://localhost:8080`.

## Deployment Configuration

Example backend properties:

```properties
server.port=${PORT:8080}
server.address=0.0.0.0

spring.datasource.url=jdbc:h2:file:${DFSS_DATA_PATH:./data/dfss}

storage.provider=${STORAGE_PROVIDER:s3}
aws.s3.region=${AWS_REGION:ap-southeast-2}
aws.s3.bucket=${AWS_S3_BUCKET:dfss-omm-prakash-2026-001}
```

Railway production variables include:

```text
STORAGE_PROVIDER=s3
AWS_REGION=ap-southeast-2
AWS_S3_BUCKET=dfss-omm-prakash-2026-001
AWS_ACCESS_KEY_ID=<secret>
AWS_SECRET_ACCESS_KEY=<secret>
```

Never commit AWS secrets.

Vercel rewrite:

```json
{
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "https://distributed-file-storage-system-production.up.railway.app/api/:path*"
    }
  ]
}
```

## Verified Tests

### Local
- Health ✅
- Upload ✅
- List ✅
- Metadata ✅
- Download ✅
- Delete ✅
- H2 metadata persistence ✅
- LOCAL storage ✅
- S3 storage ✅
- Provider-aware routing ✅

### Production
- Railway health endpoint ✅
- Railway files endpoint ✅
- Railway → S3 upload ✅
- Railway → S3 download ✅
- Railway → S3 delete ✅
- Vercel frontend ✅
- Vercel → Railway integration ✅
- Browser upload/list/download/delete ✅

## Build Verification

Frontend:

```powershell
cd frontend
npm run lint
npm run build
```

Backend:

```powershell
cd backend
.\mvnw.cmd clean compile
```

## MVP Scope

Included:
- Upload
- List
- Metadata
- Download
- Delete
- LOCAL storage
- AWS S3
- H2 metadata
- React frontend
- Spring Boot backend
- Deployment
- Basic error handling

Deferred:
- Login/signup
- Role-based access
- File sharing
- Chunked uploads
- Replication
- PostgreSQL
- Redis
- GCS
- Kubernetes
- Advanced distributed algorithms

## Security Notes

- Never commit AWS credentials.
- Do not use root-account access keys.
- Use least-privilege IAM credentials for the DFSS bucket.
- Current Spring Security configuration is development-oriented.
- H2 is suitable for the MVP but not ideal for a larger production system.

## Submission Checklist

- [x] Backend source
- [x] Frontend source
- [x] Local storage
- [x] AWS S3 storage
- [x] Storage abstraction
- [x] H2 metadata
- [x] REST API
- [x] React dashboard
- [x] Production frontend
- [x] Production backend
- [x] Production S3 integration
- [x] Build verification
- [x] README
- [ ] Final screenshots
- [ ] Demo video (optional)
- [ ] Final Git cleanliness check

## Final Git Check

```powershell
git status
git log --oneline -5
```

Expected:

```text
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```

## Final Result

The DFSS MVP provides a working storage abstraction over local and AWS S3 storage, persists metadata in H2, exposes REST APIs with Spring Boot, and provides a deployed React interface for the complete file lifecycle.
