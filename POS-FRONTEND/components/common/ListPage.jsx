'use client';

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "../../services/api";
import PropTypes from "prop-types";
import { validateForm } from "../../components/common/validation";
import Modal from "../../components/common/Modal";

const ListPage = ({ keys, fields, modelName, showToggle = true }) => {
  const router = useRouter(); 

  const [refresh, setRefresh] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});

  const [listData, setListData] = useState([]);
  const [page, setPage] = useState(0);
  const [sizePerPage] = useState(2);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);

  const [searchQuery, setSearchQuery] = useState("");

  const fetchData = async () => {
  try {
    const isSearchEmpty = searchQuery.trim() === "";

    if (isSearchEmpty) {
      const res = await api.post(`/${modelName}/list`, {
        page,
        sizePerPage,
      });

      const data = res.data;

      setListData(data.dtoList || []);
      setTotalPages(data.totalPages || 0);
      setTotalRecords(data.totalRecords || 0);
    } else {
      const res = await api.post(`/${modelName}/list`, {
        page: 0,
        sizePerPage: 1000,
      });

      const data = res.data;
      const fullData = data.dtoList || [];

      const filtered = fullData.filter((item) =>
        Object.values(item).some((value) =>
          String(value).toLowerCase().includes(searchQuery.toLowerCase())
        )
      );

      setListData(filtered);
      setTotalPages(1);
      setTotalRecords(filtered.length);
    }
  } catch (err) {
    console.error(err);
    alert("Failed to fetch data");
  }
};

  useEffect(() => {
    fetchData();
  }, [page, refresh, searchQuery]);

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
    setPage(0);
  };

 const handleChange = (e) => {
  const { name, value } = e.target;

  setFormData((prev) => ({
    ...prev,
    [name]: value,
  }));

  setErrors((prev) => ({
    ...prev,
    [name]: "",
  }));
};

 const handleEdit = (item) => {
  const updatedData = { ...item };

  fields.forEach((field) => {
    if (field.multiple) {
      if (Array.isArray(item[field.name])) {
        updatedData[field.name] = item[field.name];
      } else if (item[field.name]) {
        updatedData[field.name] = item[field.name].split(",");
      } else {
        updatedData[field.name] = [];
      }
    }
  });
  setFormData(updatedData);
  setErrors({});
  setShowModal(true);
};

const handleUpdate = async () => {
  const newErrors = validateForm(fields, formData);
  setErrors(newErrors);
  if (Object.keys(newErrors).length > 0) {
    return;
  }
  try {
    await api.post(`/${modelName}/update`, formData);
    setShowModal(false);
    setRefresh((prev) => prev + 1);

  } catch (err) {
    console.error(err);
    alert("Update failed");
  }
};

  const start = page * sizePerPage + 1;
  const end = start + listData.length - 1;

  return (
    <div className="bg-[#f4f6fb] min-h-screen py-10 px-5">
      <div className="max-w-6xl mx-auto">
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-4 rounded-lg mb-4">
          <h2 className="text-xl font-semibold">
            {modelName.charAt(0).toUpperCase() + modelName.slice(1)} List
          </h2>
          <p className="text-sm">
            View and manage {modelName}
          </p>
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
              onClick={() => router.push(`/${modelName}/add`)} 
              className="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded text-sm"
            >
              + Add {modelName}
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
              <span className="absolute right-2 top-2 text-gray-400 text-sm">
              </span>
            </div>
          </div>
          {listData.length === 0 && (
            <div className="text-center text-blue-600">
              No Data Available
            </div>
          )}
          {listData.length > 0 && (
            <div className="overflow-x-auto">
              <table className="w-full border border-gray-200">
                <thead>
                  <tr className="bg-gray-100 text-sm">
                    {keys.map((key) => (
                      <th key={key} className="text-left py-2 px-3">
                        {key}
                      </th>
                    ))}
                    {showToggle && (
                      <th className="text-center py-2 px-3">Status</th>
                    )}
                    <th className="text-center py-2 px-3">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {listData.map((item, rowIndex) => (
                    <tr key={item.identifier ?? rowIndex} className="border-t hover:bg-gray-50">
                      {keys.map((key) => (
                        <td key={key} className="py-2 px-3">
                          {Array.isArray(item[key]) ? (
                            <div className="flex flex-wrap gap-1">
                              {item[key].map((value, index) => (
                                <span
                                  key={`${key}-${rowIndex}-${index}`}
                                  className="bg-blue-100 text-blue-700 px-2 py-1 rounded text-xs"
                                >
                                  {value}
                                </span>
                              ))}
                            </div>
                          ) : (
                            item[key]
                          )}
                        </td>
                      ))}

                      {showToggle && (
                        <td className="py-2 px-3 text-center">
                          <div className="flex flex-col items-center">
                            <label className="relative inline-flex items-center cursor-pointer" aria-label="Toggle status">
                              <input
                                type="checkbox"
                                checked={item.status}
                                onChange={async () => {
                                  try {
                                    await api.post(`/${modelName}/toggleStatus`, {
                                      identifier: item.identifier,
                                      status: !item.status,
                                    });
                                    setRefresh(prev => prev + 1);
                                  } catch {
                                    alert("Toggle failed");
                                  }
                                }}
                                className="sr-only peer"
                              />

                              <div className="w-12 h-6 bg-gray-300 rounded-full peer-checked:bg-blue-600 transition-all" />
                              <span className="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all peer-checked:translate-x-6" />
                            </label>

                            <span className={`text-xs mt-1 font-semibold ${item.status ? "text-blue-600" : "text-red-500"}`}>
                              {item.status ? "Active" : "Deactivated"}
                            </span>
                          </div>
                        </td>
                      )}

                      <td className="py-2 px-3 text-center">
                        <div className="flex gap-2 justify-center">

                          <button
                            onClick={() => handleEdit(item)}
                            className="bg-green-500 text-white px-2 py-1 rounded text-sm"
                          >
                            Update
                          </button>
                          <button
                            onClick={async () => {
                              if (!globalThis.confirm("Delete?")) return;

                              try {
                                await api.post(`/${modelName}/delete`, {
                                  identifier: item.identifier,
                                });

                                setRefresh((prev) => prev + 1);
                              } catch {
                                alert("Delete failed");
                              }
                            }}
                            className="bg-red-500 text-white px-2 py-1 rounded text-sm"
                          >
                            Delete
                          </button>

                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          <div className="flex justify-center items-center gap-3 mt-4">
            <button
              disabled={page === 0 || searchQuery !== ""}
              onClick={() => setPage(prev => prev - 1)}
              className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50"
            >
              Prev
            </button>
            <span className="text-sm">
              Page {page + 1} of {totalPages}
            </span>
            <button
              disabled={page + 1 === totalPages || searchQuery !== ""}
              onClick={() => setPage(prev => prev + 1)}
              className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50"
            >
              Next
            </button>
          </div>
          <div className="text-center text-sm mt-3 text-gray-600">
            {listData.length > 0
              ? `Showing ${start}-${end} of ${totalRecords} entries`
              : "Showing 0 entries"}
          </div>
        </div>
      </div>
      {showModal && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/20">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-lg">
            <h2 className="text-xl font-semibold mb-4">
              Update {modelName}
            </h2>
            <Modal
              formData={formData}
              handleChange={handleChange}
              fields={fields}
              errors={errors}
            />
            <div className="flex justify-end gap-3 mt-4">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 border rounded"
              >
                Cancel
              </button>
              <button
                onClick={handleUpdate}
                className="px-4 py-2 bg-blue-500 text-white rounded"
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ListPage;
ListPage.propTypes = {
  keys: PropTypes.arrayOf(PropTypes.string).isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.string,
      multiple: PropTypes.bool,
    })
  ).isRequired,
  modelName: PropTypes.string.isRequired,
  showToggle: PropTypes.bool,
};