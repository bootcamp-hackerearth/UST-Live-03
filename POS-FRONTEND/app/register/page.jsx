"use client";

import { useState, useEffect } from "react";
import axios from "axios";
import { registerUser } from "@/services/api";
import { useRouter } from "next/navigation";
import Select from "react-select";
import reactSelectStyles from "@/app/components/reactSelectStyles";

function Register() {
	const router = useRouter();

	const [username, setUsername] = useState("");
	const [name, setName] = useState("");
	const [phoneNo, setPhoneNo] = useState("");
	const [selectedRoles, setSelectedRoles] = useState([]);
	const [password, setPassword] = useState("");

	const [roles, setRoles] = useState([]);  
	const [error, setError] = useState("");

  const [submitted, setSubmitted] = useState(false);

  const roleOptions = roles.map((role) => ({
    value: role.identifier,
    label: role.identifier,
  }));

	useEffect(() => {
		document.body.style.margin = "0";
		document.body.style.padding = "0";
		document.body.style.backgroundColor = "#f4f6f9";
		document.body.style.overflowX = "hidden";

		const fetchRoles = async () => {
			try {
				const response = await axios.get(
					"/api/role/all"
				);

				setRoles(response.data || []);
			} catch (err) {
				console.error("Error fetching roles:", err);
				setError("Failed to load roles");
			}
		};

		fetchRoles();
	}, []);

	const handleSubmit = async (e) => {
		e.preventDefault();
    setSubmitted(true);
    setError("");

    if (selectedRoles.length === 0) {
      return;
    }

		try {
      setError("");

      const response = await registerUser({
        username,
        name,
        phoneNo,
        roles: selectedRoles,
        password,
      });

      if (response?.message?.includes("already exists")) {
        setError(response.message);
        return;
      }

      alert("Registration Successful");
      router.push("/login");

  } catch (err) {
      console.error(err);

      setError(
          err?.response?.data?.message ||
          err?.response?.data ||
          err?.message ||
          "Registration Failed ❌"
      );
  }
	};

	return (
		<div style={styles.page}>
			<div style={styles.card}>
				<h2 style={styles.title}>Register</h2>

				{error && (
					<div style={styles.errorMessage}>
						{error}
					</div>
				)}

				<form onSubmit={handleSubmit} style={styles.form}>
					<div style={styles.formGroup}>
						<label style={styles.label} htmlFor="username">Username</label>
						<input
              id="username"
              required
							type="email"
							value={username}
							onChange={(e) => setUsername(e.target.value)}
							style={styles.input}
						/>
					</div>

					<div style={styles.formGroup}>
						<label style={styles.label} htmlFor="fullname">Full Name</label>
						<input
              id="fullname"
							type="text"
              required
							value={name}
							onChange={(e) => setName(e.target.value)}
							style={styles.input}
						/>
					</div>

					<div style={styles.formGroup}>
						<label style={styles.label} htmlFor="phonenumber">Phone Number</label>
						<input
              id="phonenumber"
              type="tel"
              value={phoneNo}
              onChange={(e) =>
                setPhoneNo(
                  e.target.value.replaceAll(/\D/g, "")
                )
              }
              style={styles.input}
              required
              pattern="[0-9]{10}"
              maxLength={10}
              title="Enter exactly 10 digits"
            />
					</div>

					<div style={styles.formGroup}>
					<label style={styles.label} htmlFor="roles-select">Roles</label>

						<Select
              instanceId="roles-select"
              styles={reactSelectStyles}
              isMulti
              isSearchable
              options={roleOptions}
              value={roleOptions.filter((role) =>
                selectedRoles.includes(role.value)
              )}
              onChange={(selected) =>
                setSelectedRoles(
                  selected?.map((item) => item.value) || []
                )
              }
              placeholder="Select Roles"
            />
            {submitted && selectedRoles.length === 0 && (
              <small style={{ color: "red" }}>
                Please select at least one role
              </small>
            )}
					</div>

					<div style={styles.formGroup}>
					<label htmlFor="password-input" style={styles.label}>Password</label>
					<input
            id="password-input"
            type="password"
            required
            minLength={8}
            pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$"
            title="Password must contain at least 8 characters, one uppercase letter, one number and one special character"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={styles.input}
          />
					</div>

					<button type="submit" style={styles.button}>
						Register
					</button>
				</form>

				<p style={styles.footerText}>
					Already have an account?{" "}
          <button
            type="button"
            style={styles.loginLink}
            onClick={() => router.push("/login")}
            onKeyDown={(e) => {
              if (e.key === 'Enter' || e.key === ' ') router.push('/login');
            }}
            aria-label="Go to login page"
          >
            Login
          </button>
				</p>
			</div>
		</div>
	);
}

export default Register;

const styles = {
	page: {
		position: "fixed",
		top: 0,
		left: 0,
		right: 0,
		bottom: 0,
		width: "100%",
		height: "100%",
		backgroundColor: "#f4f6f9",
		display: "flex",
		justifyContent: "center",
		alignItems: "center",
		fontFamily: "Arial, sans-serif",
		overflow: "hidden",
	},

	card: {
    width: "500px",
    backgroundColor: "#fff",
    padding: "30px",
    borderRadius: "10px",
    border: "1px solid #e5e7eb",
    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    boxSizing: "border-box",
  },

	form: {
		display: "flex",
		flexDirection: "column",
		gap: "10px",
	},

	title: {
    fontSize: "24px",
    fontWeight: "600",
    color: "#111827",
    textAlign: "center",
    marginBottom: "10px",
  },

	errorMessage: {
    backgroundColor: "#fee2e2",
    color: "#dc2626",
    border: "1px solid #fecaca",
    borderRadius: "8px",
    padding: "10px",
    fontSize: "14px",
    marginBottom: "10px",
  },

	formGroup: {
		display: "flex",
		flexDirection: "column",
		gap: "2px",
	},

	label: {
    fontSize: "14px",
    fontWeight: "500",
    color: "#374151",
    marginBottom: "6px",
    textAlign: "left",
  },

	input: {
    width: "100%",
    minHeight: "42px",
    padding: "0 12px",
    border: "1px solid #d1d5db",
    borderRadius: "8px",
    fontSize: "14px",
    color: "#111827",
    outline: "none",
    boxSizing: "border-box",
  },

	multiSelect: {
    width: "100%",
    height: "120px",
    padding: "8px",
    border: "1px solid #d1d5db",
    borderRadius: "8px",
    fontSize: "14px",
    boxSizing: "border-box",
  },

	helperText: {
		fontSize: "11px",
		color: "#666",
		marginTop: "2px",
    textAlign: "left",
	},

	button: {
    width: "100%",
    height: "42px",
    backgroundColor: "#4f46e5",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    fontSize: "14px",
    fontWeight: "500",
    marginTop: "8px",
    marginBottom: "8px",
    cursor: "pointer",
  },

	footerText: {
		textAlign: "center",
		fontSize: "13px",
		color: "#555",
		margin: 0,
		marginTop: "2px",
	},

	loginLink: {
		color: "#1976d2",
		fontWeight: "500",
		cursor: "pointer",
	},
};
