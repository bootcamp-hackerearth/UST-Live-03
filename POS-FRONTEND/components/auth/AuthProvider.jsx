// components/auth/AuthProvider.jsx
"use client";

import React, { createContext, useEffect, useCallback } from "react";
import { useRouter, usePathname } from "next/navigation";
import PropTypes from "prop-types";

export const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
    const router = useRouter();
    const pathname = usePathname();

    const handleForceLogout = useCallback(() => {
        if (typeof globalThis !== "undefined" && globalThis.localStorage) {
            globalThis.localStorage.removeItem("token");
            globalThis.localStorage.removeItem("tokenLoginTime");
            globalThis.localStorage.removeItem("username");
        }
        router.push("/login");
    }, [router]);

    useEffect(() => {
        if (pathname === "/login" || pathname === "/register") return;

        if (typeof globalThis === "undefined" || !globalThis.window || !globalThis.localStorage) return;

        const token = globalThis.localStorage.getItem("token");
        const loginTime = globalThis.localStorage.getItem("tokenLoginTime");
        const FIVE_HOURS_MS = 5 * 60 * 60 * 1000;

        if (!token || !loginTime) {
            handleForceLogout();
            return;
        }

        const parsedLoginTime = Number.parseInt(loginTime, 10);
        if (Number.isNaN(parsedLoginTime)) {
            handleForceLogout();
            return;
        }

        const timeElapsed = Date.now() - parsedLoginTime;
        const timeLeft = FIVE_HOURS_MS - timeElapsed;

        if (timeLeft <= 0) {
            handleForceLogout();
            return;
        }

        const logoutTimer = setTimeout(() => {
            handleForceLogout();
        }, timeLeft);

        return () => clearTimeout(logoutTimer);
    }, [pathname, handleForceLogout]);

    return (
        <AuthContext.Provider value={null}>
            {children}
        </AuthContext.Provider>
    );
}

AuthProvider.propTypes = {
    children: PropTypes.node.isRequired,
};