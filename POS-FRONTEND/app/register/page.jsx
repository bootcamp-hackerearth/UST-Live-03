"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "../api";

export default function Register() {
  const router = useRouter();

  const [user, setUser] = useState({
    name: "",
    username: "",
    roles: [],
    phoneNo: "",
    password: "",
  });

  const [rolesList, setRolesList] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api
      .post("/role/list", {
        page: 0,
        sizePerPage: 10,
        sortDirection: "ASC",
        sortField: "identifier",
      })
      .then((res) => {
        const roles =
          res.data.dtoList || res.data.content || res.data;
        setRolesList(roles);
      })
      .catch(() => {
        setMessage("Failed to load roles");
      });
  }, []);

  const handleChange = (e) => {
    const { name, value, options } = e.target;

    if (name === "roles") {
      const selected = Array.from(options)
        .filter((o) => o.selected)
        .map((o) => o.value);

      setUser({
        ...user,
        roles: selected,
      });
    } else {
      setUser({
        ...user,
        [name]: value,
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    setMessage("");

    if (
      !/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(
        user.username.trim()
      )
    ) {
      setMessage("Email must be like example@gmail.com");
      setLoading(false);
      return;
    }

    if (!/^\d{10}$/.test(user.phoneNo)) {
      setMessage("Phone number must be exactly 10 digits");
      setLoading(false);
      return;
    }

    if (user.password.length < 6) {
      setMessage("Password must be at least 6 characters");
      setLoading(false);
      return;
    }

    try {
      const res = await api.post("/user/register", user);

      const data = res.data;

      if (
        (typeof data === "string" &&
          data.toLowerCase().includes("exist")) ||
        data?.message?.toLowerCase().includes("exist")
      ) {
        setMessage("Email already exists");
        setLoading(false);
        return;
      }

      setMessage("Registration successful!");

      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } catch (err) {
      console.error(err);
      setMessage("Server error");
    }

    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-6 rounded-xl shadow w-96">
        <h2 className="text-xl font-bold text-center mb-4">
          Register
        </h2>

        {message && (
          <p
            className={`text-sm text-center mb-3 ${
              message.includes("")
                ? "text-green-600"
                : "text-red-500"
            }`}
          >
            {message}
          </p>
        )}

        {rolesList.length === 0 && (
          <p className="text-sm text-center text-red-500 mb-2">
            Roles not loaded
          </p>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-3"
        >
          <input
            type="text"
            name="name"
            placeholder="Name"
            className="w-full p-2 border rounded"
            value={user.name}
            onChange={handleChange}
            required
          />

          <input
            type="email"
            name="username"
            placeholder="Email"
            className="w-full p-2 border rounded"
            value={user.username}
            onChange={handleChange}
            required
          />

          <select
            name="roles"
            multiple
            className="w-full p-2 border rounded"
            onChange={handleChange}
            required
          >
            {rolesList.map((role) => (
              <option
                key={role.id}
                value={role.identifier}
              >
                {role.identifier}
              </option>
            ))}
          </select>

          <input
            type="tel"
            name="phoneNo"
            placeholder="Phone Number"
            maxLength={10}
            className="w-full p-2 border rounded"
            value={user.phoneNo}
            onChange={(e) =>
              setUser({
                ...user,
                phoneNo: e.target.value
                  .replaceAll(/\D/g, "")
                  .slice(0, 10),
              })
            }
            required
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            className="w-full p-2 border rounded"
            value={user.password}
            onChange={handleChange}
            required
          />

          <button
            type="submit"
            disabled={loading}
            className={`w-full p-2 text-white rounded ${
              loading
                ? "bg-gray-400"
                : "bg-blue-600 hover:bg-blue-700"
            }`}
          >
            {loading ? "Registering..." : "Register"}
          </button>
        </form>

        <p className="text-center mt-3 text-sm">
          Already have an account?{" "}
          <button
            type="button"
            className="text-blue-500 cursor-pointer underline"
            onClick={() => router.push("/login")}
          >
            Login
          </button>
        </p>
      </div>
    </div>
  );
}