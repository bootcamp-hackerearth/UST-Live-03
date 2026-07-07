"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

const Dashboard1 = () => {
	const router = useRouter();

	const [user, setUser] = useState({
		name: "Admin"
	});

	const fetchUser = useCallback(async () => {
		try {
			const token = localStorage.getItem("token");
			const username = localStorage.getItem("username");

			if (!token || !username) {
				router.push("/login");
				return;
			}

			const res = await axios.get(
				`/api/user/get?username=${username}`,
				{
					headers: {
						Authorization: `Bearer ${token}`,
					},
				}
			);

			setUser({
				name: res.data?.name || username
			});
		} catch (err) {
			console.error("USER FETCH ERROR:", err);

			if (err.response?.status === 401 || err.response?.status === 403) {
				localStorage.removeItem("token");
				localStorage.removeItem("username");

				router.push("/login");
				return;
			}

			const username = localStorage.getItem("username");
			setUser({
				name: username
			});
		}
	}, [router]);

	useEffect(() => {
		fetchUser();
	}, [fetchUser]);

	return (
		<div style={styles.page}>

			{/* TOPBAR */}
			<div style={styles.topbar}>

				<div>
					<h1 style={styles.pageTitle}>
						Dashboard
					</h1>

					<p style={styles.pageSubtitle}>
						Welcome
					</p>
				</div>

				{/* USER PROFILE */}
				<button
					type="button"
					style={styles.profile}
					onClick={() =>
						router.push("/user/profile")
					}
				>
					<div style={styles.avatar}>
						{user.name?.charAt(0)?.toUpperCase() || "A"}
					</div>

					<div>
						<h4 style={styles.profileName}>
							{user.name}
						</h4>
					</div>
				</button>

			</div>

			{/* MAIN */}
			<div style={styles.mainCard}>
				<div>
					<h2 style={styles.mainTitle}>
						Manage your POS business
					</h2>

					<p style={styles.mainText}>
						Track sales, orders and users in one place.
					</p>
				</div>
			</div>
			{/* QUICK ACTIONS */}
			<div style={styles.quickGrid}>

				<button
					style={styles.quickCard}
					onClick={() => router.push("/cart")}
				>
					<div style={styles.icon}>
						🛒
					</div>

					<div>
						<h3 style={styles.cardTitle}>
							New Sale
						</h3>

						<p style={styles.cardText}>
							Create and manage cart items
						</p>
					</div>
				</button>


				<button
					style={styles.quickCard}
					onClick={() => router.push("/orders")}
				>
					<div style={styles.icon}>
						📦
					</div>

					<div>
						<h3 style={styles.cardTitle}>
							Orders
						</h3>

						<p style={styles.cardText}>
							View and track orders
						</p>
					</div>
				</button>

			</div>

		</div>
	);
};

export default Dashboard1;

const styles = {
	page: {
		minHeight: "100vh",
		padding: "30px",
		background: "#F2F7F8",
		fontFamily: "Inter, sans-serif",
		boxSizing: "border-box",
	},

	topbar: {
		display: "flex",
		justifyContent: "space-between",
		alignItems: "center",
		marginBottom: "28px",
		gap: "20px",
		flexWrap: "wrap",
	},

	pageTitle: {
		margin: 0,
		fontSize: "30px",
		color: "#111827",
		fontWeight: "700",
	},

	pageSubtitle: {
		marginTop: "6px",
		color: "#6b7280",
		fontSize: "14px",
	},

	profile: {
		display: "flex",
		alignItems: "center",
		gap: "12px",
		background: "#fff",
		padding: "10px 14px",
		borderRadius: "14px",
		boxShadow: "0 4px 18px rgba(0,0,0,0.05)",
		cursor: "pointer",
	},

	avatar: {
		width: "42px",
		height: "42px",
		borderRadius: "50%",
		background: "#006e74",
		display: "flex",
		alignItems: "center",
		justifyContent: "center",
		color: "#fff",
		fontWeight: "700",
		fontSize: "16px",
	},

	profileName: {
		margin: 0,
		fontSize: "14px",
		color: "#111827",
	},

	profileRole: {
		margin: 0,
		fontSize: "12px",
		color: "#6b7280",
	},

	mainCard: {
		background: "#006e74",
		borderRadius: "24px",
		padding: "35px",
		color: "#fff",
		boxShadow:
			"0 12px 30px rgba(99,102,241,0.25)",
	},

	mainTitle: {
		margin: 0,
		fontSize: "30px",
		fontWeight: "700",
		color: "#fff",
	},

	mainText: {
		marginTop: "12px",
		color: "rgba(255,255,255,0.9)",
		lineHeight: "1.6",
		maxWidth: "500px",
	},

	quickGrid: {
		display: "grid",
		gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
		gap: "20px",
		marginTop: "28px",
	},

	quickCard: {
		display: "flex",
		alignItems: "center",
		gap: "18px",
		background: "#ffffff",
		border: "none",
		borderRadius: "18px",
		padding: "22px",
		cursor: "pointer",
		textAlign: "left",
		boxShadow: "0 6px 20px rgba(0,0,0,0.06)",
		transition: "0.2s ease",
	},

	icon: {
		fontSize: "32px",
	},

	cardTitle: {
		margin: 0,
		fontSize: "18px",
		color: "#111827",
		fontWeight: "700",
	},

	cardText: {
		margin: "6px 0 0",
		fontSize: "13px",
		color: "#6b7280",
	},
};