import { useEffect, useMemo, useState } from "react";

import {
  deleteFile,
  downloadFile,
  getFiles,
  getHealth,
  uploadFile,
} from "./api";

import "./App.css";

function App() {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [backendOnline, setBackendOnline] = useState(false);

  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [downloadingId, setDownloadingId] = useState(null);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadFiles = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getFiles();

      setFiles(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setError("Unable to load files from the backend.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let cancelled = false;

    Promise.allSettled([getFiles(), getHealth()]).then(
      ([filesResult, healthResult]) => {
        if (cancelled) {
          return;
        }

        if (filesResult.status === "fulfilled") {
          const data = filesResult.value;

          setFiles(Array.isArray(data) ? data : []);
          setError("");
        } else {
          console.error(filesResult.reason);
          setError("Unable to load files from the backend.");
        }

        setBackendOnline(healthResult.status === "fulfilled");

        setLoading(false);
      },
    );

    return () => {
      cancelled = true;
    };
  }, []);

  const totalSize = useMemo(() => {
    return files.reduce((total, file) => total + (file.size || 0), 0);
  }, [files]);

  const formatSize = (bytes = 0) => {
    if (bytes === 0) return "0 B";

    const units = ["B", "KB", "MB", "GB"];
    const index = Math.floor(Math.log(bytes) / Math.log(1024));

    const value = bytes / Math.pow(1024, index);

    return `${value.toFixed(index === 0 ? 0 : 2)} ${units[index]}`;
  };

  const formatDate = (date) => {
    if (!date) return "-";

    return new Date(date).toLocaleString();
  };

  const handleUpload = async (event) => {
    event.preventDefault();

    if (!selectedFile) {
      setError("Please choose a file first.");
      return;
    }

    try {
      setUploading(true);
      setError("");
      setMessage("");

      await uploadFile(selectedFile);

      setMessage("File uploaded successfully.");
      setSelectedFile(null);

      const input = document.getElementById("file-input");

      if (input) {
        input.value = "";
      }

      await loadFiles();
    } catch (err) {
      console.error(err);

      const backendMessage =
        err.response?.data?.message || "File upload failed.";

      setError(backendMessage);
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (file) => {
    try {
      setDownloadingId(file.fileId);
      setError("");
      setMessage("");

      const blob = await downloadFile(file.fileId);

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");

      link.href = url;
      link.download = file.originalFileName || "download";
      document.body.appendChild(link);

      link.click();
      link.remove();

      window.URL.revokeObjectURL(url);

      setMessage(`Downloaded ${file.originalFileName}`);
    } catch (err) {
      console.error(err);
      setError("Unable to download this file.");
    } finally {
      setDownloadingId(null);
    }
  };

  const handleDelete = async (file) => {
    const confirmed = window.confirm(`Delete "${file.originalFileName}"?`);

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(file.fileId);
      setError("");
      setMessage("");

      await deleteFile(file.fileId);

      setFiles((currentFiles) =>
        currentFiles.filter(
          (currentFile) => currentFile.fileId !== file.fileId,
        ),
      );

      setMessage(`${file.originalFileName} deleted successfully.`);
    } catch (err) {
      console.error(err);
      setError("Unable to delete this file.");
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="app">
      <header className="header">
        <div>
          <p className="eyebrow">DFSS</p>
          <h1>Distributed File Storage System</h1>
          <p className="subtitle">
            Store and manage files across Local and AWS S3 storage.
          </p>
        </div>

        <div
          className={`backend-status ${backendOnline ? "online" : "offline"}`}
        >
          <span className="status-dot" />

          {backendOnline ? "Backend Online" : "Backend Offline"}
        </div>
      </header>

      <main className="container">
        <section className="stats">
          <div className="stat-card">
            <span>Total files</span>
            <strong>{files.length}</strong>
          </div>

          <div className="stat-card">
            <span>Total storage</span>
            <strong>{formatSize(totalSize)}</strong>
          </div>

          <div className="stat-card">
            <span>Storage Providers</span>
            <strong>Local + S3</strong>
          </div>
        </section>

        <section className="panel upload-panel">
          <div className="section-heading">
            <div>
              <h2>Upload File</h2>
              <p>Maximum upload size: 25 MB</p>
            </div>
          </div>

          <form className="upload-form" onSubmit={handleUpload}>
            <input
              id="file-input"
              type="file"
              onChange={(event) =>
                setSelectedFile(event.target.files?.[0] || null)
              }
            />

            <button
              className="primary-button"
              type="submit"
              disabled={uploading}
            >
              {uploading ? "Uploading..." : "Upload"}
            </button>
          </form>

          {selectedFile && (
            <div className="selected-file">
              <strong>{selectedFile.name}</strong>
              <span>{formatSize(selectedFile.size)}</span>
            </div>
          )}
        </section>

        {message && <div className="message success">{message}</div>}

        {error && <div className="message error">{error}</div>}

        <section className="panel">
          <div className="section-heading">
            <div>
              <h2>Files</h2>
              <p>Metadata stored in H2 database.</p>
            </div>

            <button className="secondary-button" onClick={loadFiles}>
              Refresh
            </button>
          </div>

          {loading ? (
            <div className="empty-state">Loading files...</div>
          ) : files.length === 0 ? (
            <div className="empty-state">No files uploaded yet.</div>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>File</th>
                    <th>Type</th>
                    <th>Size</th>
                    <th>Storage</th>
                    <th>Uploaded</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {files.map((file) => (
                    <tr key={file.fileId}>
                      <td>
                        <div className="file-name">{file.originalFileName}</div>

                        <div className="file-id">{file.fileId}</div>
                      </td>

                      <td>{file.contentType || "Unknown"}</td>

                      <td>{formatSize(file.size)}</td>

                      <td>
                        <span
                          className={`provider ${
                            file.storageProvider === "S3" ? "s3" : "local"
                          }`}
                        >
                          {file.storageProvider}
                        </span>
                      </td>

                      <td>{formatDate(file.uploadedAt)}</td>

                      <td>
                        <div className="actions">
                          <button
                            className="download-button"
                            disabled={downloadingId === file.fileId}
                            onClick={() => handleDownload(file)}
                          >
                            {downloadingId === file.fileId
                              ? "Downloading..."
                              : "Download"}
                          </button>

                          <button
                            className="delete-button"
                            disabled={deletingId === file.fileId}
                            onClick={() => handleDelete(file)}
                          >
                            {deletingId === file.fileId
                              ? "Deleting..."
                              : "Delete"}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
