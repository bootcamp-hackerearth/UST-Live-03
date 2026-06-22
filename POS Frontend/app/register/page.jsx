"use client";

import { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { BASE } from "@/lib/api";
import { validators } from "@/lib/security";
import { PATHS, ERROR_MESSAGES } from "@/config/constants";

function validate(user, confirmPassword) {
  if (!user.name.trim()) return "Full name is required.";
  if (!user.username.trim()) return "Email is required.";
  if (!validators.email(user.username.trim()))
    return "Enter a valid email address.";
  if (!user.phoneNo.trim()) return "Phone number is required.";
  if (!validators.phone(user.phoneNo.trim()))
    return "Phone must be exactly 10 digits.";
  if (!user.password) return "Password is required.";
  if (!confirmPassword) return "Password confirmation is required.";
  if (user.password !== confirmPassword) return "Passwords do not match.";
  return null;
}

export default function Register() {
  const router = useRouter();
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState("");
  const [user, setUser] = useState({
    name: "",
    username: "",
    roles: [],
    phoneNo: "",
    password: "",
  });

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const res = await fetch(`${BASE}/api/role/list`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ page: 0, sizePerPage: 100 }),
        });
        
        if (!res.ok) {
          return;
        }
        
        const data = await res.json();
        const rolesList = Array.isArray(data) ? data : (data?.dtoList ?? []);
        setRoles(rolesList);
      } catch {
      }
    };
    
    fetchRoles();
  }, []);

  const handleChange = useCallback((e) => {
    setUser((u) => ({ ...u, [e.target.name]: e.target.value }));
    setError("");
  }, []);

  const handlePhone = useCallback((e) => {
    const val = e.target.value.replaceAll(/\D/g, "").slice(0, 10);
    setUser((u) => ({ ...u, phoneNo: val }));
    setError("");
  }, []);

  const toggleRole = useCallback((id) => {
    setUser((u) => ({
      ...u,
      roles: u.roles.includes(id)
        ? u.roles.filter((r) => r !== id)
        : [...u.roles, id],
    }));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitted(true);
    const err = validate(user, confirmPassword);
    if (err) {
      setError(err);
      return;
    }
    setLoading(true);
    setError("");
    try {
      const res = await fetch(`${BASE}/api/security/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user),
      });
      
      const data = await res.json().catch(() => ({}));
      
      if (!res.ok) {
        const msg = data?.message || data;
        const errorMsg = typeof msg === "string" && msg
          ? msg
          : ERROR_MESSAGES.VALIDATION_ERROR;
        setError(errorMsg);
        return;
      }
      
      router.push(PATHS.LOGIN);
    } catch (err) {
      const errorMsg = err instanceof TypeError ? 
        ERROR_MESSAGES.NETWORK_ERROR : 
        ERROR_MESSAGES.SERVER_ERROR;
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const isErr = (field) => submitted && !user[field]?.toString().trim();

  const roleLabel =
    user.roles.length > 0 ? `Roles (${user.roles.length} selected)` : "Roles";

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
        *,*::before,*::after{box-sizing:border-box;margin:0;padding:0}
        body{font-family:'Barlow',sans-serif}
        .pg{min-height:100vh;display:flex;background:#f0f2f5}
        .pg-left{width:380px;flex-shrink:0;background:#005dab;display:flex;flex-direction:column;justify-content:space-between;padding:48px 40px;position:relative;overflow:hidden}
        .pg-left::before{content:'';position:absolute;top:-60px;right:-60px;width:200px;height:200px;border-radius:50%;background:rgba(255,255,255,0.05);pointer-events:none}
        .pg-left::after{content:'';position:absolute;bottom:-80px;left:-80px;width:260px;height:260px;border-radius:50%;background:rgba(255,255,255,0.05);pointer-events:none}
        .logo{display:flex;align-items:center;gap:10px}
        .logo-mark{width:36px;height:36px;background:#e31837;border-radius:4px;display:flex;align-items:center;justify-content:center;font-size:16px;font-weight:900;color:#fff}
        .logo-name{font-size:13px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:0.06em}
        .pg-tagline{font-size:26px;font-weight:900;color:#fff;line-height:1.2;letter-spacing:-0.02em}
        .pg-tagline span{color:#ffb800}
        .pg-sub{font-size:13px;color:rgba(255,255,255,0.55);line-height:1.75;margin-top:10px;font-weight:500}
        .pg-foot{font-size:11px;color:rgba(255,255,255,0.25);text-transform:uppercase;letter-spacing:0.06em;font-weight:600}
        .pg-right{flex:1;display:flex;align-items:center;justify-content:center;padding:40px 32px;overflow-y:auto}
        .card{width:100%;max-width:400px;padding:4px 0}
        .card-title{font-size:24px;font-weight:900;color:#111;letter-spacing:-0.03em;margin-bottom:4px}
        .card-sub{font-size:13px;color:#888;margin-bottom:24px;font-weight:500}
        .two-col{display:grid;grid-template-columns:1fr 1fr;gap:12px}
        .alert{padding:10px 14px;background:#fff0f3;border:1px solid #fbbcca;border-radius:6px;font-size:13px;color:#c0152a;margin-bottom:16px;font-weight:600;display:flex;align-items:center;gap:8px}
        .alert::before{content:'!';width:18px;height:18px;border-radius:50%;background:#e31837;color:#fff;display:inline-flex;align-items:center;justify-content:center;font-size:11px;font-weight:900;flex-shrink:0}
        .fg{margin-bottom:12px}
        .lbl{display:block;font-size:11px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:5px}
        .inp{width:100%;padding:10px 12px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:6px;outline:none;transition:border-color .15s,box-shadow .15s}
        .inp:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,0.1)}
        .inp.err{border-color:#e31837;background:#fffafa}
        .roles-wrap{display:flex;flex-wrap:wrap;gap:6px;padding:10px;background:#fff;border:1.5px solid #ddd;border-radius:6px;min-height:46px}
        .chip{padding:4px 12px;border-radius:20px;font-size:11px;font-weight:700;cursor:pointer;border:1.5px solid #ddd;background:#fff;color:#555;transition:all .13s;user-select:none;font-family:'Barlow',sans-serif;text-transform:uppercase;letter-spacing:0.04em}
        .chip:hover{border-color:#005dab;color:#005dab}
        .chip.sel{background:#005dab;border-color:#005dab;color:#fff}
        .empty-roles{font-size:12px;color:#bbb;font-weight:500;align-self:center}
        .btn{width:100%;padding:12px;background:#e31837;border:none;border-radius:6px;color:#fff;font-family:'Barlow',sans-serif;font-size:14px;font-weight:800;text-transform:uppercase;letter-spacing:0.04em;cursor:pointer;transition:background .15s,transform .1s;margin-top:6px}
        .btn:hover:not(:disabled){background:#c0152a}
        .btn:active:not(:disabled){transform:scale(.99)}
        .btn:disabled{opacity:.5;cursor:not-allowed}
        .link-row{text-align:center;font-size:13px;color:#999;margin-top:18px;font-weight:500}
        .link-row a{color:#005dab;font-weight:700;text-decoration:none}
        .link-row a:hover{text-decoration:underline}
        @media(max-width:640px){.pg-left{display:none}.two-col{grid-template-columns:1fr}.pg-right{padding:28px 20px}}
      `}</style>
      <div className="pg">
        <div className="pg-left">
          <div className="logo">
            <div className="logo-mark">P</div>
            <span className="logo-name">POS Enterprise</span>
          </div>
          <div>
            <h1 className="pg-tagline">
              Set up <span>team access</span> for your store.
            </h1>
            <p className="pg-sub">
              Register users and assign role-based permissions to control access
              across your system.
            </p>
          </div>
          <div className="pg-foot">© 2026 POS Enterprise</div>
        </div>
        <div className="pg-right">
          <div className="card">
            <h2 className="card-title">Create account</h2>
            <p className="card-sub">Fill in the details below to register</p>
            {error && (
              <div className="alert" role="alert">
                {error}
              </div>
            )}
            <form onSubmit={handleSubmit} noValidate>
              <div className="two-col">
                <div className="fg">
                  <label className="lbl" htmlFor="name">
                    Full Name
                  </label>
                  <input
                    id="name"
                    className={`inp${isErr("name") ? " err" : ""}`}
                    type="text"
                    name="name"
                    value={user.name}
                    onChange={handleChange}
                    placeholder="John Doe"
                  />
                </div>
                <div className="fg">
                  <label className="lbl" htmlFor="phoneNo">
                    Phone
                  </label>
                  <input
                    id="phoneNo"
                    className={`inp${isErr("phoneNo") ? " err" : ""}`}
                    type="tel"
                    name="phoneNo"
                    value={user.phoneNo}
                    onChange={handlePhone}
                    placeholder="10 digits"
                    maxLength={10}
                    inputMode="numeric"
                  />
                </div>
              </div>
              <div className="fg">
                <label className="lbl" htmlFor="username">
                  Email
                </label>
                <input
                  id="username"
                  className={`inp${isErr("username") ? " err" : ""}`}
                  type="email"
                  name="username"
                  value={user.username}
                  onChange={handleChange}
                  placeholder="you@company.com"
                  autoComplete="username"
                />
              </div>
              <div className="fg">
                <label className="lbl" htmlFor="password">
                  Password
                </label>
                <input
                  id="password"
                  className={`inp${isErr("password") ? " err" : ""}`}
                  type="password"
                  name="password"
                  value={user.password}
                  onChange={handleChange}
                  autoComplete="new-password"
                  placeholder="Enter a password"
                />
              </div>
              <div className="fg">
                <label className="lbl" htmlFor="confirmPassword">
                  Confirm Password
                </label>
                <input
                  id="confirmPassword"
                  className={`inp${submitted && confirmPassword !== user.password ? " err" : ""}`}
                  type="password"
                  name="confirmPassword"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  autoComplete="new-password"
                  placeholder="Re-enter password"
                />
              </div>
              <div className="fg">
                <label className="lbl">{roleLabel}</label>
                <div className="roles-wrap">
                  {roles.length === 0 ? (
                    <span className="empty-roles">Loading roles…</span>
                  ) : (
                    roles.map((role) => (
                      <button
                        key={role.id}
                        type="button"
                        className={`chip${user.roles.includes(role.identifier) ? " sel" : ""}`}
                        onClick={() => toggleRole(role.identifier)}
                      >
                        {role.identifier}
                      </button>
                    ))
                  )}
                </div>
              </div>
              <button className="btn" type="submit" disabled={loading}>
                {loading ? "Creating account…" : "Create Account"}
              </button>
            </form>
            <div className="link-row">
              Already have an account? <Link href="/login">Sign in</Link>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
