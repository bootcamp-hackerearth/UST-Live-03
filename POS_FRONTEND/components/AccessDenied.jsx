"use client";

import { useRouter } from "next/navigation";

export default function AccessDenied() {
    const router = useRouter();

    return (
        <div className="w-full h-[60vh] flex flex-col justify-center items-center">
            <h1 className="text-4xl font-bold">403</h1>

            <p className="text-gray-500 mt-2">
                You don’t have permission to access this
            </p>

            <button
                onClick={() => router.back()}
                className="mt-4 px-4 py-2 bg-black text-white rounded"
            >
                Go Back
            </button>
        </div>
    );
}
