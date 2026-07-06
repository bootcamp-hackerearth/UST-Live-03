"use client"
import React, { useEffect, useState } from 'react'
import { UserCircle, LogOut, Store } from 'lucide-react'
import { useRouter } from 'next/navigation'

const Navbar = () => {
  const router = useRouter()
  const [operator, setOperator] = useState("Operator")

  useEffect(() => {
    const savedUser = localStorage.getItem("username")
    if (savedUser) setOperator(savedUser)
  }, [])

  const handleLogout = async () => {
    await fetch("/auth/logout",{method:"POST"})
    localStorage.removeItem("username")
    router.push("/login")
  }

  return (
    <header className="fixed top-0 right-0 left-0 h-12 bg-slate-900 border-b border-slate-800 text-white flex items-center justify-between px-4 z-1000 shadow-sm pl-3">
      
      <button
        type="button"
        aria-label="Go to home"
        className="flex items-center gap-2 cursor-pointer group select-none"
        onClick={() => router.push("/")}
      >
        <div className="p-1.5 bg-blue-600 rounded group-hover:bg-blue-500 transition-colors">
          <Store className="size-4 text-white" />
        </div>
        <div>
          <span className="font-bold text-xs tracking-wider uppercase block">POS Application</span>
        </div>
      </button>

      <div className="flex items-center gap-3">
        
        <button
          type="button"
          aria-label="Open profile"
          onClick={() => router.push("/profile")}
          className="flex items-center gap-2 px-2.5 py-1 rounded border border-slate-800 bg-slate-950/40 hover:bg-slate-800/60 cursor-pointer transition-all"
        >
          <UserCircle className="size-4 text-slate-400" />
          <div className="hidden sm:block text-left">
            <p className="text-[11px] font-semibold text-slate-200 line-clamp-1">{operator}</p>
          </div>
          <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" />
        </button>

        <button 
          onClick={handleLogout}
          className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-red-500/10 rounded transition-all"
          title="Logout"
        >
          <LogOut className="size-4" />
        </button>
      </div>

    </header>
  )
}

export default Navbar