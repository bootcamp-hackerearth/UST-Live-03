"use client";

import { useEffect, useState } from "react";
import { redirect, useRouter } from "next/navigation";
import { Pencil, Trash2Icon } from "lucide-react";
import PropTypes from "prop-types";
import { FetchList } from "@/apicalls/fetch/FetchList";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import Switch from "@mui/material/Switch";
import Link from "next/link"

const CommonList = ({ keys, routeName, title }) => {

  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const router = useRouter();
  const [search, setSearch] = useState("");

  useEffect(() => {
    const listFetch = async () => {
      const response = await FetchList(
        `${baseUrl}/${routeName}/list`,
        page,
        search
      );

      if (Array.isArray(response)) {
        setData(response);
      } else {
        setData(response?.content || []);
        setTotalPages(response?.totalPages || 0);
      }
    };

    listFetch();
  }, [routeName, page, search]);

  const handleDelete = async (item) => {

    const res = await FetchEntity(
      `${baseUrl}/${routeName}/delete`,
      "DELETE",
      item,
    );

    const currentIdentifier = localStorage.getItem("username");
    if (currentIdentifier === item.identifier) {
      localStorage.removeItem("username");
      redirect("/login");
    }

    if (res === true) {
      const response = await FetchList(
        `${baseUrl}/${routeName}/list`,
        page,
        search
      );

      if (Array.isArray(response)) {
        setData(response);
      } else {
        setData(response?.content || []);
        setTotalPages(response?.totalPages || 0);
      }
    }
  };

  const handleUpdate = (identifier) => {
    router.push(`/${routeName}/update/${identifier}`);
  };

  const handleToggle = async (item) => {

    await FetchEntity(
      `${baseUrl}/${routeName}/toggle`,
      "PATCH",
      item.identifier,
      "text/plain"
    );
  };

  const updateItemStatus = (itemId, newStatus) => {
    setData((prev) =>
      prev.map((p) =>
        p.id === itemId
          ? { ...p, status: newStatus }
          : p
      )
    );
  };

  const handleStatusChange = (e, item) => {
    const newStatus = e.target.checked;

    updateItemStatus(item.id, newStatus);

    handleToggle(item);
  };

  const handleNext = () => setPage((prev) => prev + 1);
  const handlePrev = () => {
    if (page > 0) setPage((prev) => prev - 1);
  };
  
  return (

    <div className="m-6">
      <div className="flex justify-between items-center mt-4 gap-4">

        <input
          type="text"
          placeholder="Search..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(0);
          }}
          className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400" />

        <Link
          href={`/${routeName}/add`}
          className="whitespace-nowrap px-6 py-2 rounded-xl bg-violet-500 text-white text-sm font-semibold shadow-sm hover:bg-violet-600 transition">
          Add {routeName}
        </Link>

      </div>

      <h1 className="text-xl font-semibold text-gray-700 text-center">
        {title} List
      </h1>

      <div className="mt-6 bg-white rounded-2xl shadow-sm border border-gray-200 overflow-hidden">

        <div className="overflow-x-auto">
          <table className="w-full text-sm">

            <thead className="bg-[#F4F5FB] text-gray-600">
              <tr>
                {keys.map((key) => (
                  <th key={key} className="px-4 py-3 text-left font-semibold">
                    {key}
                  </th>
                ))}
                <th className="px-4 py-3 text-center font-semibold">
                  Action
                </th>
              </tr>
            </thead>

            <tbody className="text-gray-600">
              {data.length === 0 ? (
                <tr>
                  <td
                    colSpan={keys.length + 1}
                    className="text-center py-6 text-gray-400">
                    No records found
                  </td>
                </tr>
              ) : (
                data.map((item) => (
                  <tr
                    key={item.identifier ?? item.username}
                    className="border-t hover:bg-gray-50 transition">
                    {keys.map((key) =>
                      key === "status" ? (
                        <td key={key} className="px-4 py-3">
                          <Switch
                            checked={item.status === true}
                            onChange={(e) => handleStatusChange(e, item)} />
                        </td>
                      ) : (
                        <td key={key} className="px-4 py-3">
                          {(() => {
                            const value = item[key];

                            if (Array.isArray(value)) {
                              return value.length ? value.join(", ") : "-";
                            }
                            return value || "-";
                          })()}
                        </td>
                      )
                    )}

                    <td className="px-4 py-3 flex justify-center gap-4">
                      <button
                        onClick={() =>
                          handleUpdate(item.identifier ?? item.username)
                        }
                        className="p-2 rounded-lg hover:bg-gray-100 transition">
                        <Pencil size={16} className="text-gray-600" />
                      </button>

                      <button
                        onClick={() =>
                          handleDelete(item)
                        }
                        className="p-2 rounded-lg hover:bg-red-50 transition">
                        <Trash2Icon size={16} className="text-red-400" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      <div className="flex justify-center items-center gap-4 mt-6 text-sm">
        <button
          onClick={handlePrev}
          disabled={page === 0}
          className="px-4 py-2 rounded-xl bg-gray-200 text-gray-600 disabled:opacity-50">
          Prev
        </button>

        <select
          value={page}
          onChange={(e) => setPage(Number(e.target.value))}
          className="px-3 py-2">
          {[...new Array(totalPages).keys()].map((p) => (
            <option key={p} value={p}>
              Page {p + 1}  of {totalPages}
            </option>
          ))}
        </select>

        <button
          onClick={handleNext}
          disabled={page >= totalPages - 1}
          className="px-4 py-2 rounded-xl bg-violet-500 text-white disabled:opacity-50 hover:bg-violet-600 transition">
          Next
        </button>
      </div>
    </div>
  );
};

CommonList.propTypes = {
  keys: PropTypes.array.isRequired,
  routeName: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
};

export default CommonList;
