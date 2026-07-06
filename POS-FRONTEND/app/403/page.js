"use client";
import React from "react";
import { useRouter } from "next/navigation";

export default function Forbidden() {
    const router = useRouter();

    const containerStyle = "fixed top-[60px] left-[220px] right-0 bottom-0 bg-gray-50 font-sans flex items-center justify-center p-5 md:p-6 overflow-hidden";
    const actionButtonStyle = "px-4.5 py-2.5 rounded-lg border-none cursor-pointer text-sm font-semibold transition-colors flex-1 text-center select-none";
    const outlineButtonStyle = "px-4 py-2 bg-transparent text-brand border-[1.5px] border-solid border-brand rounded-lg text-xs font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5 flex-1 text-center select-none";

    return (
        <div className={containerStyle}>
            <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] p-6 md:p-8 max-w-[400px] w-full text-center flex flex-col items-center">

                <div className="w-14 h-14 bg-[#fff5f5] rounded-xl border border-solid border-[#fca5a5] flex items-center justify-center mb-5">
                    <svg
                        className="w-7 h-7 text-[#d62828]"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 10-8 0v4h8z"
                        />
                    </svg>
                </div>

                <h1 className="text-5xl font-extrabold text-brand m-0 mb-1 tracking-tight">
                    403
                </h1>

                <h2 className="text-lg font-bold text-gray-800 m-0 mb-3 tracking-tight">
                    Access Forbidden
                </h2>

                <p className="m-0 mb-6 text-sm text-gray-500 leading-relaxed">
                    You do not have the required permissions to view this page or
                    perform this action. Please contact an administrator if you believe
                    this is a mistake.
                </p>

                <div className="flex gap-3 w-full justify-center">
                    <button
                        onClick={() => router.push("/home")}
                        className={`${actionButtonStyle} bg-brand text-white hover:bg-brand-hover`}
                    >
                        Go Home
                    </button>
                    <button
                        onClick={() => router.back()}
                        className={outlineButtonStyle}
                    >
                        Go Back
                    </button>
                </div>
            </div>
        </div>
    );
}