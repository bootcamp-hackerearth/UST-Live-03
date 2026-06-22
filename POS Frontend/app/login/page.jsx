"use client";

import { useState, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { BASE } from "@/lib/api";
import { isValidEmail } from "@/lib/validators";
import { loginRateLimiter, htmlEscape } from "@/lib/security";
import { STORAGE_KEYS, PATHS, ERROR_MESSAGES } from "@/config/constants";

const LOGIN_TIMEOUT_MS = 10000;

const setStorageItem = (key, value) => {
  try {
    if (globalThis.window?.localStorage) {
      globalThis.window.localStorage.setItem(key, value);
      return true;
    }
  } catch {
    return false;
  }
  return false;
};

function validate({ username, password }) {
  if (!username.trim()) return "Email is required.";
  if (!isValidEmail(username.trim())) return "Enter a valid email address.";
  if (!password) return "Password is required.";
  return null;
}

function getButtonLabel(loading, isRateLimited) {
  if (loading) return "Signing in…";
  if (isRateLimited) return "Account Locked";
  return "Sign In";
}

function getSubmitErrorMessage(err) {
  if (err instanceof TypeError) return ERROR_MESSAGES.NETWORK_ERROR;
  if (err?.name === "AbortError") return "Login request timed out. Please try again.";
  return ERROR_MESSAGES.SERVER_ERROR;
}

async function callAuthenticate(username, password) {
  const res = await fetch(`${BASE}/api/authenticate`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
    signal: AbortSignal.timeout(LOGIN_TIMEOUT_MS),
  });
  const data = await res.json();
  return { res, data };
}
function handleAuthFailure(res, data, username) {
  const msg = data?.message || data;
  const errorMsg = typeof msg === "string" && msg ? msg : ERROR_MESSAGES.INVALID_CREDENTIALS;

  const remaining = loginRateLimiter.getRemaining(username);
  return { errorMsg: htmlEscape(errorMsg), remaining };
}

function persistSession(token, username) {
  const stored = setStorageItem(STORAGE_KEYS.TOKEN, token) &&
    setStorageItem(STORAGE_KEYS.USERNAME, username);
  return stored;
}

export default function Login() {
  const router = useRouter();
  const [form, setForm] = useState({ username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [touched, setTouched] = useState({});
  const [remainingAttempts, setRemainingAttempts] = useState(5);
  const [isRateLimited, setIsRateLimited] = useState(false);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
    setError("");

    if (name === "username") {
      const remaining = loginRateLimiter.getRemaining(value.trim());
      setRemainingAttempts(remaining);
      setIsRateLimited(remaining === 0);
    }
  }, []);

  const handleBlur = useCallback((e) => {
    setTouched((t) => ({ ...t, [e.target.name]: true }));
  }, []);

  const processSuccessfulLogin = useCallback(async (data, username) => {
    const token = data?.token;
    if (!token || typeof token !== "string" || token.trim() === "" || token === "Error") {
      setError(ERROR_MESSAGES.INVALID_CREDENTIALS);
      return false;
    }

    loginRateLimiter.reset(username);

    if (!persistSession(token, username)) {
      setError(ERROR_MESSAGES.STORAGE_ERROR);
      return false;
    }

    setForm({ username: "", password: "" });
    router.replace(PATHS.HOME);
    return true;
  }, [router]);

  const handleSubmit = useCallback(async (e) => {
    e.preventDefault();
    setTouched({ username: true, password: true });

    const username = form.username.trim();

    if (!loginRateLimiter.isAllowed(username)) {
      setError("Too many login attempts. Please try again in 15 minutes.");
      setIsRateLimited(true);
      return;
    }

    const validationErr = validate(form);
    if (validationErr) {
      setError(validationErr);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const { res, data } = await callAuthenticate(username, form.password);

      if (!res.ok) {
        const { errorMsg, remaining } = handleAuthFailure(res, data, username);
        setError(errorMsg);
        setRemainingAttempts(remaining);
        if (remaining === 0) setIsRateLimited(true);
        return;
      }

      await processSuccessfulLogin(data, username);
    } catch (err) {
      setError(getSubmitErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [form, processSuccessfulLogin]);

  const emailErr = touched.username && !form.username.trim();
  const passErr = touched.password && !form.password;
  const buttonLabel = getButtonLabel(loading, isRateLimited);

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
            {!error && isRateLimited && remainingAttempts === 0 && (
              <div className="alert" role="alert">
                Account temporarily locked. Please try again in 15 minutes.
              </div>
            )}
            {!error && !isRateLimited && remainingAttempts > 0 && remainingAttempts < 5 && form.username && (
              <div className="alert" role="alert" style={{ background: "#fff8e1", borderColor: "#ffe082", color: "#b45309" }}>
                ⚠ {remainingAttempts} attempt{remainingAttempts === 1 ? "" : "s"} remaining
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
              <button className="btn" type="submit" disabled={loading || isRateLimited}>
                {buttonLabel}
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