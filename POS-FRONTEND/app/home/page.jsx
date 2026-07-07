"use client";

import { useEffect, useState } from "react";

export default function Home() {
  const [user, setUser] = useState({
    name: "",
    email: "",
  });

  useEffect(() => {
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");

    if (!token) return;

    fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/api/user/list`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        page: 0,
        sizePerPage: 50,
      }),
    })
      .then((res) => res.json())
      .then((data) => {
        const users = data.dtoList || data || [];

        const currentUser = users.find(
          (u) => u.username === username
        );

        if (currentUser) {
          setUser({
            name: currentUser.name,
            email: currentUser.username,
          });
        }
      })
      .catch(() => {
        console.log("Error fetching user");
      });
  }, []);

  return (
    <div className="w-full h-full flex items-center justify-center p-5">

      <div className="relative w-full max-w-5xl h-[540px] bg-[#ECECEC] rounded-[24px] overflow-hidden">

        <div className="absolute left-0 top-0 w-[50%] h-full bg-black rounded-r-[60px]"></div>

        <div className="absolute bottom-[-70px] left-[10%] w-[190px] h-[190px] bg-white rounded-full"></div>

        <div className="relative z-10 h-full grid lg:grid-cols-2 items-center">

          <div className="px-12">

            <h1 className="text-5xl font-bold text-white leading-[1.1] tracking-[-2px]">
              Smart POS
              <br />
              Management
            </h1>

            <p className="text-gray-300 text-base mt-6 leading-8 max-w-sm">
              Billing, inventory, sales and retail operations made simple.
            </p>

          </div>

          <div className="flex justify-center">

            <div className="w-[340px] bg-white rounded-[24px] p-7 shadow-[0_15px_60px_rgba(0,0,0,0.10)]">

              <div className="w-16 h-16 rounded-full bg-black text-white flex items-center justify-center text-2xl font-bold mx-auto">
                {user.name ? user.name.charAt(0) : "U"}
              </div>

              <div className="text-center mt-5">

                <h2 className="text-3xl font-bold text-black">
                  Welcome
                </h2>

                <p className="text-gray-500 text-sm mt-2">
                  {user.name}
                </p>

                <p className="text-gray-400 text-xs mt-1">
                  {user.email}
                </p>

              </div>

            </div>

          </div>

        </div>

      </div>

    </div>
  );
}