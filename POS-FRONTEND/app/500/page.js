"use client";
import React from "react";
import { useRouter } from "next/navigation";

export default function InternalServerError() {
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
                                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                            />
                        </svg>
                    </div>

                    <h1 className="text-5xl font-extrabold text-brand m-0 mb-1 tracking-tight">
                        500
                    </h1>

                    <h2 className="text-lg font-bold text-gray-800 m-0 mb-3 tracking-tight">
                        Internal Server Error
                    </h2>

                    <p className="m-0 mb-6 text-sm text-gray-500 leading-relaxed">
                        The server encountered an unexpected error configuration and could
                        not complete your list view request. Please try your operation again
                        or contact an administrator.
                    </p>

                    <div className="flex gap-3 w-full justify-center">
                        <button
                            onClick={() => globalThis.location.reload()}
                            className={`${actionButtonStyle} bg-brand text-white hover:bg-brand-hover`}
                        >
                            Retry Request
                        </button>
                        <button
                            onClick={() => router.push("/")}
                            className={outlineButtonStyle}
                        >
                            Go Back
                        </button>
                    </div>
                </div>
            </div>
    );
}