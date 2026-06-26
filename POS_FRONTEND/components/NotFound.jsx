"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

export default function NotFound({ message = "Data not found" }) {
    const router = useRouter();

    return (
        <div className="w-full h-[60vh] flex flex-col justify-center items-center">
            <h1 className="text-4xl font-bold">404</h1>

            <p className="text-gray-500 mt-2">{message}</p>

            <button
                onClick={() => router.push("/dashboard")}
                className="mt-4 px-4 py-2 bg-black text-white rounded"
            >
                Go Back
            </button>
        </div>
    );
}

NotFound.propTypes = {
    message: PropTypes.string
};