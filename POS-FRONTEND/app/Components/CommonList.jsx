"use client";
import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";
import { useRouter } from "next/navigation";

const CommonList = ({
  title,
  columns,
  urlName,
  showStatus = false,
}) => {

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sizePerPage] = useState(3);

const router = useRouter();
  const getPageBasePath = () => {
    if (urlName === "user") {
      return "/User";
    }

    return `/${urlName}`;
  };

  const getItemValue = (item) => {
    if (urlName === "user") {
      return item.username;
    }

    return item.identifier ?? item.id;
  };
  const filteredData = data.filter((item) => {
  const search = searchTerm.toLowerCase();

  return columns.some((col) => {
    const value = item[col.field];

    if (value === null || value === undefined) return false;

    return String(value).toLowerCase().includes(search);
  });
});

  const getDeleteParamName = () => (urlName === "user" ? "username" : "identifier");

  const getLoggedInUsername = () => {
    try {
      const user = JSON.parse(localStorage.getItem("user") || "{}");
      return user.username || "";
    } catch {
      return "";
    }
  };

  const computedTotalPages = (() => {
    if (totalPages > 0) return totalPages;
    if (data.length > 0) return currentPage + 1;
    return 0;
  })();

  const isLastPage =
    totalPages > 0 ? currentPage + 1 >= totalPages : data.length < sizePerPage;

  useEffect(() => {
    fetchData();
  }, [currentPage]);

  const fetchData = async () => {

    setLoading(true);
    setError("");

    try {

      const res = await axiosInstance.post(`/${urlName}/list`,

        {
          page: currentPage,
          sizePerPage: sizePerPage,
          sortDirection: "ASC",
          sortField: "identifier",
        }

      );

      console.log("LIST RESPONSE:", res.data);

      setData(res.data.dtoList || (Array.isArray(res.data) ? res.data : []));

      let responseTotalPages = 0;
      if (typeof res.data.totalPages === "number") {
        responseTotalPages = res.data.totalPages;
      } else if (typeof res.data.totalRecords === "number") {
        responseTotalPages = Math.ceil(res.data.totalRecords / sizePerPage);
      }

      setTotalPages(responseTotalPages);

    } catch (err) {

      console.error("Fetch Error:", err);
      setError(err?.response?.data?.message || err.message || "Unable to fetch data.");

    } finally {

      setLoading(false);

    }
  };

  const deleteItem = async (itemValue) => {
  if (
    urlName === "user" &&
    itemValue?.toLowerCase() === getLoggedInUsername().toLowerCase()
  ) {
    const message = "Logged-in user cannot be deleted.";
    if (globalThis.window !== undefined) globalThis.alert(message);
    setError(message);
    return;
  }

  const confirmDelete = globalThis.window !== undefined && globalThis.confirm(`Are you sure you want to delete this ${urlName}?`);
  if (!confirmDelete) return;

  try {
    const response = await axiosInstance.delete(
      `/${urlName}/delete?${getDeleteParamName()}=${encodeURIComponent(itemValue)}`
    );

    if (globalThis.window !== undefined) {
      if (response.data?.message) {
        globalThis.alert(response.data.message);
      } else {
        globalThis.alert(`${title} item has been successfully soft deleted!`);
      }
    }

    fetchData();

  } catch (err) {
    console.error("Delete Error:", err);
    
    const serverErrorMessage = err?.response?.data?.message || err.message || "Unable to delete item.";
    
    if (globalThis.window !== undefined) {
      globalThis.alert(`Error: ${serverErrorMessage}`);
    }
    setError(serverErrorMessage);
  }
};
  const toggleStatus = async (item) => {

    try {

      await axiosInstance.post(

        `/${urlName}/toggle`,

        urlName === "user"
          ? { id: item.id, status: !item.status }
          : { identifier: item.identifier, status: !item.status }

      );

      fetchData();

    } catch (err) {

      console.error("Toggle Error:", err);

    }
  };

  return (

  <div className="w-full min-h-screen bg-white p-6 rounded-3xl">

    <div className="mb-8 rounded-3xl border border-slate-200 bg-white p-8 shadow-lg">

      <div className="flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">

        <div>
  <h1 className="text-4xl font-extrabold text-indigo-600 tracking-wide">
    {title}
  </h1>

  <p className="mt-2 text-slate-500 text-sm">
    Manage all {urlName} records
  </p>


  <div className="mt-4">
    <input
      type="text"
      placeholder={`Search ${title}...`}
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      className="w-full max-w-md rounded-2xl border border-slate-300 px-4 py-3 text-sm text-black placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
    />
  </div>
</div>

        <div className="flex flex-wrap gap-4">

          <button
            onClick={() => router.push("/Dashboard")}
            className="rounded-2xl border border-slate-300 bg-slate-100 px-6 py-3 text-sm font-bold text-slate-700 shadow-sm transition duration-300 hover:bg-slate-200"
          >
            ← Back
          </button>

          <button
            onClick={() => {
              router.push(`${getPageBasePath()}/add`);
            }}
            className="rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg transition duration-300 hover:bg-indigo-700 hover:scale-105"
          >
            + Add New
          </button>

        </div>

      </div>

    </div>

    

      {error && (

      <div className="mb-5 rounded-2xl border border-rose-200 bg-rose-50 px-5 py-4 text-sm font-bold text-rose-600">
        {error}
      </div>

    )}

    {loading && (

      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-md">

        <p className="text-lg font-bold text-slate-600">
          Loading...
        </p>

      </div>

    )}

   
{!loading && filteredData.length === 0 && (
      <div className="rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-md">

        <p className="text-lg font-bold text-slate-600">
          No Data Found
        </p>

      </div>

    )}

  
{!loading && filteredData.length > 0 && (
      <>
        <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-xl">

          <table className="min-w-full">

            <thead className="bg-indigo-600">

              <tr>

                {columns.map((col) => {
                  const headerKey = col.field || col.label;
                  return (
                    <th
                      key={headerKey}
                      className="px-6 py-5 text-left text-xs font-bold uppercase tracking-widest text-white"
                    >
                      {col.label}
                    </th>
                  );
                })}

                <th className="px-6 py-5 text-center text-xs font-bold uppercase tracking-widest text-white">
                  Actions
                </th>

              </tr>

            </thead>

            <tbody>

{filteredData.map((item) => {
                const rowKey =
                  getItemValue(item) ||
                  item.id ||
                  item.identifier ||
                  item.username ||
                  JSON.stringify(item);

                return (
                  <tr
                    key={rowKey}
                    className="border-b border-slate-100 transition duration-300 hover:bg-slate-50"
                  >

                  {columns.map((col) => {
                    const cellKey = col.field || col.label;
                    let cellContent;

                    if (col.field === "status" && showStatus) {
                      cellContent = (
                        <button
                          onClick={() => toggleStatus(item)}
                          className={`rounded-full px-5 py-2 text-xs font-bold text-white shadow transition ${
                            item.status
                              ? "bg-emerald-500 hover:bg-emerald-600"
                              : "bg-rose-500 hover:bg-rose-600"
                          }`}
                        >
                          {item.status ? "Active" : "Inactive"}
                        </button>
                      );
                    } else if (col.render) {
                      cellContent = col.render(item);
                    } else {
                      cellContent = item[col.field] || "-";
                    }

                    return (
                      <td
                        key={cellKey}
                        className="px-6 py-4 text-sm font-medium text-slate-700"
                      >
                        {cellContent}
                      </td>
                    );
                  })}

                  <td className="px-6 py-4">

                    <div className="flex items-center justify-center gap-3">

                      <button
                        onClick={() =>router.push(`${getPageBasePath()}/edit?identifier=${encodeURIComponent(getItemValue(item))}`)} 
            className="rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg transition duration-300 hover:bg-indigo-700 hover:scale-105"
                      >
                        Edit
                      </button>

                      <button
                        onClick={() =>
                          deleteItem(getItemValue(item))
                        }
            className="rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg transition duration-300 hover:bg-indigo-700 hover:scale-105"
                      >
                        Delete
                      </button>

                    </div>

                  </td>

                </tr>
              );
            })}

            </tbody>

          </table>

        </div>

       <div className="mt-8 flex flex-col items-center justify-between gap-5 rounded-3xl border border-slate-200 bg-white p-6 shadow-lg md:flex-row">

          <div className="text-sm font-semibold text-slate-700">

            Page{" "}
            <span className="rounded-lg bg-indigo-100 px-3 py-1 text-indigo-700">
              {currentPage + 1}
            </span>{" "}
            of{" "}
            <span className="rounded-lg bg-slate-100 px-3 py-1 text-slate-700">
              {computedTotalPages}
            </span>

          </div>

          <div className="flex items-center gap-4">

            <button
              disabled={currentPage === 0}
              onClick={() =>
                setCurrentPage((prev) => prev - 1)
              }
              className="rounded-2xl bg-slate-200 px-6 py-3 text-sm font-bold text-slate-700 shadow-sm transition duration-300 hover:bg-slate-300 disabled:opacity-40"
            >
              ← Previous
            </button>

            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-indigo-600 text-sm font-bold text-white shadow-lg">
              {currentPage + 1}
            </div>

            <button
              disabled={isLastPage}
              onClick={() =>
                setCurrentPage((prev) => prev + 1)
              }
              className="rounded-2xl bg-indigo-600 px-6 py-3 text-sm font-bold text-white shadow-lg transition duration-300 hover:bg-indigo-700 disabled:opacity-40"
            >
              Next →
            </button>

          </div>

        </div>
      </>

    )}

  </div>
);
};

CommonList.propTypes = {
  title: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string.isRequired,
      field: PropTypes.string,
      render: PropTypes.func,
    })
  ).isRequired,
  urlName: PropTypes.string.isRequired,
  showStatus: PropTypes.bool,
};

export default CommonList;
