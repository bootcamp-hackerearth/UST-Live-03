"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../../api/axiosInstance";

export default function UserAdd() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [roles, setRoles] = useState([]);
  const [selectedRoles, setSelectedRoles] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await axiosInstance.post("/role/list", {
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "identifier",
        });
        setRoles(response.data.dtoList ?? response.data.content ?? response.data ?? []);
      } catch (err) {
        setError(err?.response?.data?.message || err.message || "Unable to load roles.");
      }
    };
    fetchRoles();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (selectedRoles.length === 0) {
      setError("Please select at least one role.");
      return;
    }

    setLoading(true);
    try {
      const response = await axiosInstance.post("/user/add", {
        username, name, password, phoneNo, roles: selectedRoles,
      });
      if (response.data?.success === false) {
        setError(response.data.message || "Failed to add user");
        return;
      }
      router.push("/user");
    } catch (err) {
      setError(err?.response?.data?.message || err.message || "Unable to save user.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto">
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-slate-900">Add User</h2>
        <p className="mt-1 text-sm text-slate-500">Create a new POS user account.</p>
      </div>

      <form onSubmit={handleSubmit} className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm md:p-8">
        {error && <div className="mb-5 rounded-xl bg-rose-50 px-4 py-3 text-sm font-medium text-rose-600">{error}</div>}

        <div className="grid gap-5 md:grid-cols-2">
          <div className="flex flex-col gap-1">
            <label htmlFor="username" className="text-sm font-semibold text-gray-600">Username</label>
            <input id="username" type="email" placeholder="Enter username" value={username}
              onChange={(e) => setUsername(e.target.value)} required
              className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition" />
          </div>

          <div className="flex flex-col gap-1">
            <label htmlFor="name" className="text-sm font-semibold text-gray-600">Name</label>
            <input id="name" type="text" placeholder="Enter full name" value={name}
              onChange={(e) => setName(e.target.value)} required
              className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition" />
          </div>

          <div className="flex flex-col gap-1">
            <label htmlFor="password" className="text-sm font-semibold text-gray-600">Password</label>
            <input id="password" type="password" minLength={6} placeholder="Enter password" value={password}
              onChange={(e) => setPassword(e.target.value)} required
              className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition" />
          </div>

          <div className="flex flex-col gap-1">
            <label htmlFor="phoneNo" className="text-sm font-semibold text-gray-600">Phone Number</label>
            <input id="phoneNo" type="tel" pattern="\d{10}" maxLength={10} placeholder="Enter phone number" value={phoneNo}
              onKeyDown={(e) => { if (!/\d/.test(e.key) && e.key !== "Backspace" && e.key !== "Delete" && e.key !== "Tab") e.preventDefault(); }}
              onChange={(e) => setPhoneNo(e.target.value)}
              className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition" />
          </div>

          <div className="flex flex-col gap-1 md:col-span-2">
            <label htmlFor="roles" className="text-sm font-semibold text-gray-600">Roles</label>
            <select id="roles" multiple size={4} value={selectedRoles} required
              onChange={(e) => setSelectedRoles(Array.from(e.target.selectedOptions, (o) => o.value))}
              className="border border-gray-300 rounded-xl px-4 py-2 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition">
              {roles.map((role) => (
                <option key={role.identifier} value={role.identifier}>{role.identifier}</option>
              ))}
            </select>
            <p className="text-xs text-gray-400 mt-1">Hold Ctrl to select multiple roles</p>
          </div>
        </div>

        <div className="mt-8 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
          <button type="button" onClick={() => router.back()}
            className="inline-flex items-center justify-center rounded-xl bg-slate-100 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-200">
            Cancel
          </button>
          <button type="submit" disabled={loading}
            className="inline-flex items-center justify-center rounded-xl bg-emerald-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60">
            {loading ? "Saving..." : "Add User"}
          </button>
        </div>
      </form>
    </div>
  );
}