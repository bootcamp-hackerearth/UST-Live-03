// app/pos/upcoming/page.jsx

"use client";

import { useRouter } from "next/navigation";
import { useState, useEffect } from "react";

export default function UpcomingPage() {
    const router  = useRouter();
    const [ready, setReady] = useState(false);

    useEffect(() => { setReady(true); }, []);

    return (
        <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f4f6f8] flex items-center justify-center p-6 font-sans">
            <div className="max-w-sm w-full bg-white border border-gray-100 rounded-3xl p-8 shadow-sm flex flex-col items-center text-center gap-5">

                <div className="w-16 h-16 rounded-2xl bg-[#006E74]/8 flex items-center justify-center">
                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                    </svg>
                </div>

                <div>
                    <span className="text-[10px] font-bold text-[#006E74] uppercase tracking-widest bg-[#006E74]/8 px-3 py-1 rounded-full">
                        Coming soon
                    </span>
                    <h1 className="text-lg font-black text-[#231F20] mt-3 mb-2">
                        Still in progress
                    </h1>
                    <p className="text-xs text-gray-400 leading-relaxed">
                        This section is being built out. Check back soon — it'll be ready before you know it.
                    </p>
                </div>

                <div className="w-full bg-gray-100 h-1.5 rounded-full overflow-hidden">
                    <div
                        className="bg-[#006E74] h-full rounded-full transition-all duration-1000 ease-out"
                        style={{ width: ready ? "60%" : "0%" }}
                    />
                </div>

                <div className="flex flex-col gap-2 w-full pt-1">
                    <button
                        type="button"
                        onClick={() => {
                            router.back();
                            setTimeout(() => globalThis.location?.reload(), 100);
                        }}
                        className="w-full py-3 bg-[#006E74] text-white font-bold rounded-2xl border-none cursor-pointer hover:bg-[#0097AC] transition-colors text-sm"
                    >
                        ← Go back
                    </button>
                    <button
                        type="button"
                        onClick={() => router.push("/pos/home")}
                        className="w-full py-2.5 bg-transparent text-gray-400 font-semibold rounded-2xl text-xs border border-gray-200 hover:bg-gray-50 hover:text-gray-600 transition-colors cursor-pointer"
                    >
                        Back to Home
                    </button>
                </div>
            </div>
        </div>
    );
}