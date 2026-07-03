"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../services/api";
import Sidebar from "../../components/layout/Sidebar";

export default function CustomerPage() {
  const router = useRouter();

  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: 3,
    sortDirection: "ASC",
    sortField: "identifier",
  });
  const [totalPages, setTotalPages] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");

const fetchCustomers = async () => {
  try {
    setLoading(true);

    const response = await api.post("/customer/list", {
      ...pagination,
      keyword: searchQuery,
    });

    setCustomers(response.data?.dtoList || []);
    setTotalPages(response.data?.totalPages || 0);

  } catch (error) {
    console.log(error);
    alert(
      error?.response?.data?.message ||
      "Unable to load customers"
    );
  } finally {
    setLoading(false);
  }
};
  useEffect(() => {
    fetchCustomers();
  }, [pagination, searchQuery]); 

const handleSearchChange = (e) => {
  setSearchQuery(e.target.value);
  setPagination((prev) => ({
    ...prev,
    page: 0,
  }));
};

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm("Delete customer?");
    if (!confirmDelete) return;

    try {
      const response = await api.delete("/customer/delete",
        {
          data :{
            identifier,
          },
    });

      if (!response.data) {
        throw new Error("Delete failed");
      }

      alert("Customer deleted");
      fetchCustomers();
    } catch (error) {
      console.log(error);
      alert(error?.response?.data?.message || "Delete failed");
    }
  };

  const handleToggle = async (identifier, status) => {
    try {
      const response = await api.post("/customer/toggleStatus", {
        identifier,
        status: !status,
      });

      if (!response.data) {
        throw new Error("Status update failed");
      }

      fetchCustomers();
    } catch (error) {
      console.log(error);
      alert(error?.response?.data?.message || "Unable to update status");
    }
  };

  return (
    <Sidebar>
    <div className="bg-[#f4f6fb] min-h-screen py-10 px-5">
      <div className="max-w-6xl mx-auto">
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-4 rounded-lg mb-4">
          <h2 className="text-xl font-semibold">Customer List</h2>
          <p className="text-sm">View and manage customer entries</p>
        </div>
        <div className="bg-white p-5 rounded-lg shadow">
          <div className="flex justify-between mb-4">
            <button
              onClick={() => router.push("/home")}
              className="bg-gray-500 hover:bg-gray-600 text-white px-3 py-1 rounded text-sm"
            >
              Home
            </button>
            <button
              onClick={() => router.push("/customer/add")}
              className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm"
            >
              + Add Customer
            </button>
          </div>
          <div className="mb-4 flex justify-end">
            <div className="relative">
              <input
                type="text"
                placeholder="Search..."
                value={searchQuery}
                onChange={handleSearchChange}
                className="w-64 border border-gray-300 rounded px-3 py-2 text-sm pr-8"
              />
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full border border-gray-200">
              <thead>
                <tr className="bg-gray-100 text-sm">
                  <th className="text-left py-2 px-3">Name</th>
                  <th className="text-left py-2 px-3">Phone</th>
                  <th className="text-left py-2 px-3">Email</th>
                  <th className="text-left py-2 px-3">Party</th>
                  <th className="text-left py-2 px-3">Balance</th>

                  <th className="text-left py-2 px-3">
                    Created
                  </th>

                  <th className="text-left py-2 px-3">
                    Modified
                  </th>

                  <th className="text-center py-2 px-3">
                    Status
                  </th>

                  <th className="text-center py-2 px-3">
                    Action
                  </th>
                </tr>
              </thead>
              <tbody>
               {loading && (
  <tr>
    <td colSpan="9" className="text-center p-5 text-blue-600">
      Loading...
    </td>
  </tr>
)}

{!loading && customers.length === 0 && (
  <tr>
    <td colSpan="9" className="text-center p-5 text-blue-600">
      No Customers Found
    </td>
  </tr>
)}

{!loading &&
  customers.length > 0 &&
  customers.map((customer) => (
                    <tr
                      key={customer.identifier}
                      className="border-t hover:bg-gray-50 text-sm"
                    >
                      <td className="py-2 px-3">{customer.name}</td>
                      <td className="py-2 px-3">{customer.phoneNo}</td>
                      <td className="py-2 px-3">{customer.email}</td>
                      <td className="py-2 px-3">{customer.partyType}</td>
                      <td className="py-2 px-3">
                        {customer.balance} ({customer.balanceType})
                      </td>

                      <td className="py-2 px-3 text-xs">
                        <div className="flex flex-col">
                          <span className="font-medium">
                            {customer.createdBy || "-"}
                          </span>

                          <span className="text-gray-500">
                            {customer.createdOn
                              ? new Date(
                                  customer.createdOn
                                ).toLocaleString(
                                  "en-IN"
                                )
                              : "-"}
                          </span>
                        </div>
                      </td>

                      <td className="py-2 px-3 text-xs">
                        <div className="flex flex-col">
                          <span className="font-medium">
                            {customer.modifiedBy || "-"}
                          </span>

                          <span className="text-gray-500">
                            {customer.modifiedOn
                              ? new Date(
                                  customer.modifiedOn
                                ).toLocaleString(
                                  "en-IN"
                                )
                              : "-"}
                          </span>
                        </div>
                      </td>
                      <td className="py-2 px-3 text-center">
                        <div className="flex flex-col items-center">
                          <label className="relative inline-flex items-center cursor-pointer" aria-label="Toggle status">
                            <input
                              type="checkbox"
                              checked={customer.status || false}
                              onChange={() => handleToggle(customer.identifier, customer.status)}
                              className="sr-only peer"
                            />
                            <div className="w-12 h-6 bg-gray-300 rounded-full peer-checked:bg-blue-600 transition-all" />
                            <span 
                              className={`absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all ${
                                customer.status ? "translate-x-6" : "translate-x-0"
                              }`} 
                            />
                          </label>
                          <span className={`text-xs mt-1 font-semibold ${customer.status ? "text-blue-600" : "text-red-500"}`}>
                            {customer.status ? "Active" : "Deactivated"}
                          </span>
                        </div>
                      </td>
                      <td className="py-2 px-3 text-center">
                        <div className="flex gap-3 justify-center items-center">
                          <button
onClick={() =>
  router.push(
    `/customer/update/${customer.identifier}`
  )
}
                            className="text-green-600 hover:text-green-800 p-1 transition-colors"
                            title="Update Customer"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                              <path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10" />
                            </svg>
                          </button>
                          <button
                            onClick={() => handleDelete(customer.identifier)}
                            className="text-red-600 hover:text-red-800 p-1 transition-colors"
                            title="Delete Customer"
                          >
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                              <path strokeLinecap="round" strokeLinejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                }
              </tbody>
            </table>
          </div>
          <div className="flex justify-center items-center gap-3 mt-4">
            <button
              disabled={pagination.page === 0}
              onClick={() =>
                setPagination((prev) => ({
                  ...prev,
                  page: prev.page - 1,
                }))
              }
              className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50 text-sm"
            >
              Prev
            </button>
            <span className="text-sm">
              Page {pagination.page + 1} of {totalPages}
            </span>
            <button
              disabled={pagination.page + 1 >= totalPages}
              onClick={() =>
                setPagination((prev) => ({
                  ...prev,
                  page: prev.page + 1,
                }))
              }
              className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50 text-sm"
            >
              Next
            </button>
          </div>

        </div>
      </div>
    </div>
    </Sidebar>
  );
}