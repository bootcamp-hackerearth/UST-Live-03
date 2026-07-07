'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import api from '@/app/services/api';
import Layout from '@/components/Layout';
import ActionButtons from '@/components/ActionButtons';
import { useRouter } from 'next/navigation';
import AuditViewModal from '@/components/AuditViewModal';

const CommonList = ({
  title,
  apiUrl,
  deleteUrl,
  modelName,
  columns,
  AddComponent,
  UpdateComponent,
  customRender
}) => {

  const [data, setData] = useState([]);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  const [loading, setLoading] = useState(true);       
  const [tableLoading, setTableLoading] = useState(false); 

  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const [selectedItem, setSelectedItem] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [viewData, setViewData] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [sizePerPage] = useState(2);
  const router = useRouter();

  const [showAccessDeniedModal, setShowAccessDeniedModal] = useState(false);
  const [accessDeniedMessage, setAccessDeniedMessage] = useState('');

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(search);
    }, 400);

    return () => clearTimeout(handler);
  }, [search]);

  useEffect(() => {
    setCurrentPage(0);
  }, [debouncedSearch]);

  useEffect(() => {
    fetchData();
  }, [currentPage, debouncedSearch]);

  useEffect(() => {
    const handleAccessDenied = (e) => {
      setAccessDeniedMessage(e.detail?.message || "Access Denied");
      setShowAccessDeniedModal(true);
    };

    globalThis.addEventListener("access-denied", handleAccessDenied);
    return () => globalThis.removeEventListener("access-denied", handleAccessDenied);
  }, []);

  const fetchData = async () => {
    try {
      if (data.length === 0 && !debouncedSearch) {
        setLoading(true);
      } else {
        setTableLoading(true);
      }

      const response = await api.post(apiUrl, {
        page: currentPage,
        sizePerPage,
        sortDirection: 'ASC',
        sortField: 'id',
        keyword: debouncedSearch
      });

      if (!response?.data) {
        setData([]);
        setTotalPages(0);
        return;
      }

      setData(response.data.dtoList || []);
      setTotalPages(response.data.totalPages || 0);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
      setTableLoading(false);
    }
  };

  const deleteItem = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete this ${modelName}?`);
    if (!confirmDelete) return;
    try {
      await api.delete(`${deleteUrl}?identifier=${identifier}`);

      const currentUsername = localStorage.getItem("username");

      console.log("Deleted:", identifier);
      console.log("Logged in user:", currentUsername);

      if (identifier === currentUsername) {
        localStorage.clear();
        alert("Your account was deleted. Redirecting to login...");
        router.replace("/login");
        return;
      }

      fetchData();

    } catch (err) {
      console.error(err);
      alert(`Failed to delete ${modelName}`);
    }
  };

  const handleEdit = (item) => {
    setSelectedItem(item);
    setShowUpdateModal(true);
  };

  const handleView = async (item) => {
    try {
      const response = await api.get(
        `/${modelName}/get?identifier=${item.identifier}`
      );
      console.log("Audit Response:", response.data);
      setViewData(response.data);
      setShowViewModal(true);
    } catch (err) {
      console.error(err);
      alert("Failed to load details");
    }
  };

  const filteredData = data;

  const getCellValue = (column, row, index) => {
    if (column === 'S.No') {
      return debouncedSearch
        ? index + 1
        : currentPage * sizePerPage + index + 1;
    }

    if (customRender?.[column]) {
      return customRender[column](row, fetchData);
    }

    if (Array.isArray(row[column])) {
      return row[column].join(', ');
    }

    return row[column];
  };

  
  if (loading && data.length === 0 && !debouncedSearch) {
    return (
      <Layout>
        <div className="flex justify-center items-center h-[70vh]">
          Loading...
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-7xl mx-auto mt-6 px-4">

        <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-cyan-500">

          <div className="flex justify-between items-center mb-6">

            <h2 className="text-2xl font-semibold text-gray-800">
              {title}
            </h2>

            <div className="flex items-center gap-3">

              <input
                type="text"
                placeholder={`Search ${modelName}...`}
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="
                  border
                  border-gray-300
                  rounded-xl
                  px-4
                  py-2
                  w-72
                  focus:outline-none
                  focus:ring-2
                  focus:ring-cyan-500
                "
              />

              {AddComponent && (
                <button
                  onClick={() => setShowAddModal(true)}
                  className="
                    bg-cyan-500
                    hover:bg-cyan-600
                    text-white
                    px-5
                    py-2
                    rounded-xl
                    shadow-md
                    transition
                  "
                >
                  + Add {modelName}
                </button>
              )}
            </div>
          </div>

          <div className="overflow-hidden rounded-xl border relative">
            {tableLoading && (
              <div className="absolute inset-0 bg-white flex items-center justify-center z-10">
                Loading...
              </div>
            )}
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-cyan-500 text-white text-left">
                  {columns.map((c) => (
                    <th key={c} className="p-3">{c}</th>
                  ))}
                  <th className="p-3 text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredData.length > 0 ? (
                  filteredData.map((row, i) => (
                    <tr
                      key={row.identifier}
                      className="border-b hover:bg-gray-50 transition"
                    >
                      {columns.map((c) => (
                        <td
                          key={`${row.identifier}-${c}`}
                          className="p-3 text-gray-700"
                        >
                          {getCellValue(c, row, i)}
                        </td>
                      ))}
                      <td className="p-3">
                        <ActionButtons
                          onEdit={() => handleEdit(row)}
                          onDelete={() => deleteItem(row.identifier)}
                          onView={() => handleView(row)}
                        />
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan={columns.length + 1}
                      className="text-center p-6 text-gray-500"
                    >
                      No data found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="flex justify-between items-center mt-6">
            <button
              disabled={currentPage === 0}
              onClick={() => setCurrentPage(currentPage - 1)}
              className="px-4 py-2 bg-cyan-100 hover:bg-cyan-200 text-cyan-700 rounded-lg disabled:opacity-40"
            >
              Prev
            </button>

            <span className="text-gray-700 font-medium">
              Page {currentPage + 1} of {totalPages}
            </span>

            <button
              disabled={currentPage + 1 >= totalPages}
              onClick={() => setCurrentPage(currentPage + 1)}
              className="px-4 py-2 bg-cyan-100 hover:bg-cyan-200 text-cyan-700 rounded-lg disabled:opacity-40"
            >
              Next
            </button>
          </div>

        </div>

        {showAddModal && AddComponent && (
          <div className="fixed inset-0 bg-slate-100/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="w-full max-w-4xl bg-white rounded-2xl border border-[#163D4A] shadow-[0_8px_30px_rgba(22,61,74,0.15)] overflow-hidden">
              <AddComponent
                closeModal={() => setShowAddModal(false)}
                refreshData={() => {
                  fetchData();
                }}
              />
            </div>
          </div>
        )}

        {showUpdateModal && UpdateComponent && (
          <div className="fixed inset-0 bg-slate-100/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="w-full max-w-3xl bg-white rounded-2xl p-8 border border-[#163D4A] shadow-[0_8px_30px_rgba(22,61,74,0.15)]">
              <UpdateComponent
                data={selectedItem}
                closeModal={() => setShowUpdateModal(false)}
                refreshData={() => {
                  fetchData();
                }}
              />
            </div>
          </div>
        )}

        {showViewModal && viewData && (
          <AuditViewModal
            data={viewData}
            onClose={() => setShowViewModal(false)}
          />
        )}

      </div>

      {showAccessDeniedModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl p-8 max-w-sm w-full text-center border-t-4 border-red-500">
            <div className="text-red-500 text-5xl mb-4">🚫</div>
            <h2 className="text-xl font-bold text-gray-800 mb-2">Access Denied</h2>
            <p className="text-gray-500 mb-6">{accessDeniedMessage}</p>
            <button
              onClick={() => {
                setShowAccessDeniedModal(false);
                router.replace('/home');
              }}
              className="bg-red-500 hover:bg-red-600 text-white px-6 py-2 rounded-xl transition"
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      )}

    </Layout>
  );
};

CommonList.propTypes = {
  title: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  deleteUrl: PropTypes.string.isRequired,
  modelName: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(PropTypes.string).isRequired,
  AddComponent: PropTypes.elementType,
  UpdateComponent: PropTypes.elementType,
  customRender: PropTypes.objectOf(PropTypes.func)
};

export default CommonList;