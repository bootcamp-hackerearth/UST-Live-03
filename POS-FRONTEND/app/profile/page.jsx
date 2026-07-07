"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";

export default function Profile() {
  const router = useRouter();
  const [user, setUser] = useState(null);

  useEffect(() => {
  const username = localStorage.getItem("username");

  if (username) {
    axios
      .post(process.env.NEXT_PUBLIC_BASE_URL+"/user/get", username, {
        headers: {
          "Content-Type": "text/plain",
        },
        withCredentials: true,
      })
      .then((res) => {
        setUser(res.data);
      })
      .catch((err) => {
        console.error("Profile fetch failed:", err);

        if (
          err.response?.status === 401 ||
          err.response?.status === 403
        ) {
          localStorage.clear();
          router.push("/login");
        }
      });
  }
}, [router]);

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  };

  const handleUpdate = async () => {
    try {
      const response = await axios.put(
        process.env.NEXT_PUBLIC_BASE_URL+"/user/update",
        user,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
        }
      );

      setUser(response.data);
      alert("Profile Updated Successfully ");
      router.push("/home");
    } catch (error) {
      console.error("Profile update failed:", error);
      alert("Update Failed ");
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-gray-500">Loading Profile...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center items-start pt-20 px-4">
      <div className="w-full max-w-md bg-white shadow-md rounded-lg p-6">
        <h2 className="text-center text-lg font-semibold text-gray-800 mb-6 border-b pb-2">
          User Profile
        </h2>

        <div className="grid grid-cols-2 gap-y-4 text-sm text-gray-700">
          <div className="font-semibold">Name</div>
          <div>
            <input
              name="name"
              value={user.name || ""}
              onChange={handleChange}
              className="w-full border rounded px-2 py-1"
            />
          </div>

          <div className="font-semibold">Email</div>
          <div>
            <input
              name="username"
              value={user.username || ""}
              disabled
              className="w-full border rounded px-2 py-1 bg-gray-200"
            />
          </div>

          <div className="font-semibold">Roles</div>
          <div>
            <input
              value={user.roles?.join(", ") || ""}
              disabled
              className="w-full border rounded px-2 py-1 bg-gray-200"
            />
          </div>

          <div className="font-semibold">Phone Number</div>
          <div>
            <input
              name="phoneNo"
              value={user.phoneNo || ""}
              onChange={handleChange}
              className="w-full border rounded px-2 py-1"
            />
          </div>
        </div>

        <div className="mt-6 flex justify-center">
          <button
            onClick={handleUpdate}
            className="px-6 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            Update
          </button>
        </div>
      </div>
    </div>
  );
}