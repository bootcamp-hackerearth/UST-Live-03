"use client";

export default function ServerErrorPage() {
  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.code}>500</h1>
        <h2 style={styles.title}>Something went wrong</h2>

        <p style={styles.message}>
          We’re experiencing a technical issue on our side. Please try again
          in a few moments.
        </p>

        <button style={styles.button} onClick={() => globalThis.location.reload()}>
          Retry
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    height: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "#f5f7fb",
  },
  card: {
    textAlign: "center",
    background: "#fff",
    padding: "50px 40px",
    borderRadius: "12px",
    boxShadow: "0 10px 30px rgba(0,0,0,0.1)",
    maxWidth: "420px",
    width: "100%",
  },
  code: {
    fontSize: "64px",
    margin: 0,
    color: "#ff4d4f",
  },
  title: {
    fontSize: "22px",
    margin: "10px 0",
  },
  message: {
    fontSize: "14px",
    color: "#666",
    marginBottom: "20px",
  },
  button: {
    padding: "10px 16px",
    border: "none",
    borderRadius: "6px",
    background: "#1677ff",
    color: "#fff",
    cursor: "pointer",
  },
};