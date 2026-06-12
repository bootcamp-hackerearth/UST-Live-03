"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { isValidEmail } from "../../lib/validators";

function validate({ username, password }) {
  if (!username.trim()) return "Email is required.";
  if (!isValidEmail(username.trim())) return "Enter a valid email address.";
  if (!password) return "Password is required.";
  return null;
}

export default function Login() {
  const router = useRouter();
  const [form, setForm] = useState({ username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [touched, setTouched] = useState({});

  const handleChange = (e) => {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));
    setError("");
  };
  const handleBlur = (e) =>
    setTouched((t) => ({ ...t, [e.target.name]: true }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched({ username: true, password: true });
    const err = validate(form);
    if (err) {
      setError(err);
      return;
    }
    setLoading(true);
    setError("");
    try {
      const res = await fetch("http://localhost:8080/api/authenticate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: form.username.trim(),
          password: form.password,
        }),
      });
      const data = await res.json();
      if (!res.ok) {
        const msg = data?.message || data;
        setError(
          typeof msg === "string" && msg
            ? msg
            : "Invalid credentials. Please try again.",
        );
        return;
      }
      const token = data?.token;
      if (
        !token ||
        typeof token !== "string" ||
        token.trim() === "" ||
        token === "Error"
      ) {
        setError("Invalid email or password.");
        return;
      }

      localStorage.setItem("token", token);
      localStorage.setItem("username", form.username.trim());

      router.replace("/home");
    } catch {
      setError("Unable to connect. Please check your connection.");
    } finally {
      setLoading(false);
      setForm((f) => ({ ...f, password: "" }));
    }
  };

  const emailErr = touched.username && !form.username.trim();
  const passErr = touched.password && !form.password;

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
        *,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
        body{font-family:'Barlow',sans-serif}
        .pg{min-height:100vh;display:flex;background:#f0f2f5}
        .pg-left{width:380px;flex-shrink:0;background:#005dab;display:flex;flex-direction:column;justify-content:space-between;padding:48px 40px;position:relative;overflow:hidden}
        .pg-left::before{content:'';position:absolute;top:-60px;right:-60px;width:200px;height:200px;border-radius:50%;background:rgba(255,255,255,0.05);pointer-events:none}
        .pg-left::after{content:'';position:absolute;bottom:-80px;left:-80px;width:280px;height:280px;border-radius:50%;background:rgba(255,255,255,0.05);pointer-events:none}
        .logo{display:flex;align-items:center;gap:10px}
        .logo-mark{width:36px;height:36px;background:#e31837;border-radius:4px;display:flex;align-items:center;justify-content:center;font-size:16px;font-weight:900;color:#fff}
        .logo-name{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:0.06em}
        .pg-tagline{font-size:28px;font-weight:900;color:#fff;line-height:1.2;letter-spacing:-0.02em}
        .pg-tagline span{color:#ffb800}
        .pg-sub{font-size:13px;color:rgba(255,255,255,0.55);line-height:1.75;margin-top:10px;font-weight:500}
        .pg-foot{font-size:11px;color:rgba(255,255,255,0.25);text-transform:uppercase;letter-spacing:0.06em;font-weight:600}
        .pg-right{flex:1;display:flex;align-items:center;justify-content:center;padding:40px 32px}
        .card{width:100%;max-width:360px}
        .card-title{font-size:24px;font-weight:900;color:#111;letter-spacing:-0.03em;margin-bottom:4px}
        .card-sub{font-size:13px;color:#888;margin-bottom:28px;font-weight:500}
        .alert{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:16px;font-weight:600;display:flex;align-items:center;gap:8px}
        .alert::before{content:'!';width:18px;height:18px;border-radius:50%;background:#e31837;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:11px;font-weight:900;flex-shrink:0}
        .fg{margin-bottom:14px}
        .lbl{display:block;font-size:11px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:5px}
        .inp{width:100%;padding:10px 12px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
        .inp:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,0.1)}
        .inp.err{border-color:#e31837;background:#fffafa}
        .ferr{font-size:11px;color:#e31837;margin-top:3px;font-weight:600}
        .btn{width:100%;padding:12px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:14px;font-weight:800;text-transform:uppercase;letter-spacing:0.04em;cursor:pointer;transition:background .15s,transform .1s;margin-top:4px}
        .btn:hover:not(:disabled){background:#c0152a}
        .btn:active:not(:disabled){transform:scale(.99)}
        .btn:disabled{opacity:.5;cursor:not-allowed}
        .link-row{text-align:center;font-size:13px;color:#999;margin-top:20px;font-weight:500}
        .link-row a{color:#005dab;font-weight:700;text-decoration:none}
        .link-row a:hover{text-decoration:underline}
        @media(max-width:640px){.pg-left{display:none}.pg-right{padding:28px 20px}}
      `}</style>
      <div className="pg">
        <div className="pg-left">
          <div className="logo">
            <div className="logo-mark">P</div>
            <span className="logo-name">POS Enterprise</span>
          </div>
          <div>
            <h1 className="pg-tagline">
              Run your business <span>smarter.</span>
            </h1>
            <p className="pg-sub">
              Sign in to manage products, users, pricing, and operations in one
              place.
            </p>
          </div>
          <div className="pg-foot">© 2026 POS Enterprise</div>
        </div>
        <div className="pg-right">
          <div className="card">
            <h2 className="card-title">Welcome back</h2>
            <p className="card-sub">Sign in to your account to continue</p>
            {error && (
              <div className="alert" role="alert">
                {error}
              </div>
            )}
            <form onSubmit={handleSubmit} noValidate>
              <div className="fg">
                <label className="lbl" htmlFor="username">
                  Email
                </label>
                <input
                  id="username"
                  className={`inp${emailErr ? " err" : ""}`}
                  type="email"
                  name="username"
                  value={form.username}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  placeholder="you@company.com"
                  autoComplete="email"
                />
                {emailErr && <p className="ferr">Email is required.</p>}
              </div>
              <div className="fg">
                <label className="lbl" htmlFor="password">
                  Password
                </label>
                <input
                  id="password"
                  className={`inp${passErr ? " err" : ""}`}
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                />
                {passErr && <p className="ferr">Password is required.</p>}
              </div>
              <button className="btn" type="submit" disabled={loading}>
                {loading ? "Signing in…" : "Sign In"}
              </button>
            </form>
            <div className="link-row">
              New here? <Link href="/register">Create an account</Link>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
