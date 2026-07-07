"use client";

import { useState, useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";

import { loginUser } from "@/services/api";
import { saveToken } from "@/utils/auth";
import { validateEmail, validatePassword } from "@/utils/validation";
import { AUTH_MESSAGES } from "@/constants/messages";

import AuthLayout from "@/components/auth/AuthLayout";
import AuthCard from "@/components/auth/AuthCard";
import AuthInput from "@/components/auth/AuthInput";
import AuthButton from "@/components/auth/AuthButton";
import Alert from "@/components/common/Alert";

function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState(
    searchParams.get("message") || "",
  );

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => {
        setSuccessMessage("");
        router.replace("/login");
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, []);

  const validateForm = () => {
    if (username.trim() === "" || password.trim() === "") {
      return AUTH_MESSAGES.ALL_FIELDS_REQUIRED;
    }
    if (!validateEmail(username)) {
      return AUTH_MESSAGES.INVALID_EMAIL;
    }
    if (!validatePassword(password)) {
      return AUTH_MESSAGES.PASSWORD_MIN;
    }
    return null;
  };

  const handleLogin = async (event) => {
    event.preventDefault();
    setError("");

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      setLoading(true);
      const response = await loginUser(username, password);

      if (response.token === "Error") {
        setError(AUTH_MESSAGES.INVALID_CREDENTIALS);
        return;
      }

      saveToken(response.token);
      router.push("/dashboard");
    } catch (error) {
      console.log(error);
      console.log(error.response?.status, error.response?.data);
      setError(error.response?.data || AUTH_MESSAGES.LOGIN_FAILED);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout
      leftContent={
        <>
          <div>
            <div className="mb-20">
              <h1 className="text-5xl font-bold leading-tight">
                Welcome back to your POS platform.
              </h1>
              <p className="mt-8 text-2xl text-gray-300 leading-relaxed">
                Manage inventory, billing, customers, and sales from one
                powerful dashboard.
              </p>
            </div>
          </div>
          <div className="border-t border-gray-800 pt-10">
            <h2 className="text-4xl font-bold mb-6">Need assistance?</h2>
            <div className="text-xl text-gray-300 space-y-3">
              <p>+91 9876543210</p>
              <p>support@yourpos.com</p>
            </div>
          </div>
        </>
      }
    >
      <AuthCard title="Sign In" subtitle="Access your POS dashboard">
        <Alert type="error" message={error} />
        <Alert type="success" message={successMessage} />

        <form onSubmit={handleLogin} className="space-y-7">
          <AuthInput
            label="Email Address"
            type="text"
            value={username}
            onChange={(event) => {
              setUsername(event.target.value);
              if (error) setError("");
            }}
            placeholder="Enter your email"
            disabled={loading}
          />

          <AuthInput
            label="Password"
            type="password"
            value={password}
            onChange={(event) => {
              setPassword(event.target.value);
              if (error) setError("");
            }}
            placeholder="Enter password"
            disabled={loading}
          />

          <AuthButton
            text="Sign In"
            loadingText="Signing In..."
            loading={loading}
          />
        </form>

        <div className="mt-8 text-center">
          <p className="text-gray-600">Are you a new user?</p>
          <button
            type="button"
            disabled={loading}
            onClick={() => router.push("/register")}
            className="mt-2 text-[#0066ff] font-semibold hover:underline disabled:opacity-50"
          >
            Click Here
          </button>
        </div>
      </AuthCard>
    </AuthLayout>
  );
}

export default function LoginPage() {
  return (
    <Suspense fallback={null}>
      <LoginForm />
    </Suspense>
  );
}
