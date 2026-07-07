"use client";
import { useState } from "react";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");
    if (!username.trim()) {
      setMessage("Email is required");
      return;
    }
    const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRegex.test(username)) {
      setMessage("Invalid email format");
      return;
    }

    try {
      const response = await fetch("/api/authenticate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      const text = await response.text();

      let data;
      try {
        data = JSON.parse(text);
      } catch {
        data = { message: text };
      }

      const token = data.token;
      const isValidToken =
        token &&
        token !== "Error" &&
        token !== "null" &&
        token.split(".").length === 3;

      if (response.ok && isValidToken) {
        localStorage.setItem("token", token);
        globalThis.location.href = "/";
        return;
      }

      setMessage("Invalid username or password");
    } catch (error) {
      console.error(error);
      setMessage("Server Error");
    }
  };

  return (
    <div
      style={{
        height: "100vh",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f5f5f5",
      }}
    >
      <form
        onSubmit={handleLogin}
        style={{
          width: "350px",
          padding: "25px",
          backgroundColor: "white",
          borderRadius: "8px",
          boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        }}
      >
        <h2
          style={{
            marginBottom: "20px",
            textAlign: "center",
            color: "#111827",
          }}
        >
          POS Login
        </h2>
        {message && (
          <div
            style={{
              backgroundColor: "#fee2e2",
              color: "#dc2626",
              padding: "10px",
              borderRadius: "6px",
              marginBottom: "15px",
              fontSize: "14px",
            }}
          >
            {message}
          </div>
        )}
        <input
          type="text"
          placeholder="Username"
          value={username}
          required
          onChange={(e) => setUsername(e.target.value)}
          style={{
            width: "100%",
            padding: "10px",
            marginBottom: "12px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            boxSizing: "border-box",
          }}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          required
          onChange={(e) => setPassword(e.target.value)}
          style={{
            width: "100%",
            padding: "10px",
            marginBottom: "12px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            boxSizing: "border-box",
          }}
        />

        <button
          type="submit"
          style={{
            width: "100%",
            padding: "10px",
            backgroundColor: "black",
            color: "white",
            border: "none",
            borderRadius: "6px",
            cursor: "pointer",
            marginBottom: "10px",
            fontWeight: "bold",
          }}
        >
          Login
        </button>

        <button
          type="button"
          onClick={() => (globalThis.location.href = "/register")}
          style={{
            width: "100%",
            padding: "10px",
            backgroundColor: "white",
            color: "black",
            border: "1px solid black",
            borderRadius: "6px",
            cursor: "pointer",
            fontWeight: "bold",
          }}
        >
          Register New User
        </button>
      </form>
    </div>
  );
}
export default Login;
