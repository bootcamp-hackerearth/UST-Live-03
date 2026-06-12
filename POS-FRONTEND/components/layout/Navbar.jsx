"use client";

import { useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function Navbar() {

  const router = useRouter();

  const pathname = usePathname();

  const { user } = useAuth();

  const [showProfileMenu,setShowProfileMenu] =
    useState(false);

  const getPageTitle = () => {

    if(pathname === "/dashboard") {
      return "Dashboard";
    }

    if(pathname === "/profile") {
      return "Profile";
    }

    return "POS System";
  };

  return (
    <header className="h-24 bg-black border-b border-white/10 flex items-center justify-between px-10">

      <div>

        <h1 className="text-2xl font-semibold text-white">
          {getPageTitle()}
        </h1>
      </div>

      <div className="relative">

        <button
          onClick={() =>
            setShowProfileMenu(
              !showProfileMenu
            )
          }
          className="h-14 w-14 rounded-full bg-blue-600 flex items-center justify-center text-xl font-bold text-white hover:bg-blue-500 transition-all"
        >
          {user?.name?.charAt(0)?.toUpperCase()}
        </button>

        {
          showProfileMenu && (

            <div className="absolute right-0 top-16 w-72 bg-[#1b1b1b] border border-white/10 rounded-2xl shadow-2xl p-5 z-50">
              <div className="flex items-center gap-4 mb-5">
                <div className="h-14 w-14 rounded-full bg-blue-600 flex items-center justify-center text-xl font-bold text-white">

                  {user?.name
                    ?.charAt(0)
                    ?.toUpperCase()}
                </div>

                <div>
                  <p className="font-semibold text-white">
                    {user?.name}
                  </p>

                  <p className="text-sm text-gray-400">
                    {user?.username}
                  </p>
                </div>
              </div>

              <div className="space-y-2 text-sm text-gray-400">

                <p>
                  Phone: {user?.phoneNo}
                </p>

                <p>
                  Roles:{" "}
                  {user?.roles?.join(", ")}
                </p>

              </div>

              <button
                onClick={() => {

                  setShowProfileMenu(false);
                  router.push("/profile");

                }}
                className="w-full mt-5 bg-blue-600 hover:bg-blue-500 py-3 rounded-xl font-medium text-white transition-all"
              >

                View Profile

              </button>
            </div>
          )
        }
      </div>
    </header>
  );
}