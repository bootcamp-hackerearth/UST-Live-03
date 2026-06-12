"use client"
import React, { useState, useEffect } from 'react'

export default function Home() {
    const [username, setUsername] = useState("User")

    useEffect(() => {
        if (globalThis.window !== undefined) {
            const savedUser = localStorage.getItem("username")
            if (savedUser) setUsername(savedUser.split('@')[0])
        }
    }, [])

    return (
        <div className="flex justify-center items-center min-h-[80vh] p-6 text-center text-slate-800">
            <div className="space-y-2">
                <h1 className="text-3xl font-bold tracking-tight text-slate-900 capitalize">
                    Welcome back, {username}!
                </h1>
                <p className="text-sm text-slate-500 max-w-sm mx-auto leading-relaxed">
                    You have successfully logged in. Use the side bar to navigate.
                </p>
            </div>
        </div>
    )
}
