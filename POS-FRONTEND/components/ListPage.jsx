'use client';

import { useEffect, useState, useCallback } from 'react';
import api from '@/app/services/api';
import Layout from '@/components/Layout';
import ActionButtons from '@/components/ActionButtons';
import { useRouter } from 'next/navigation';
import PropTypes from 'prop-types';

const CommonList = ({
  title,
  apiUrl,
  deleteUrl,
  modelName,
  columns,
  AddComponent,
  UpdateComponent,
  customRender,
  customViewHandler
}) => {

  const [data, setData] = useState([]);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const [selectedItem, setSelectedItem] = useState(null);

  const [showViewModal, setShowViewModal] = useState(false);
  const [viewData, setViewData] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [sizePerPage] = useState(2);
  const router = useRouter();

  useEffect(() => {
    const t = setTimeout(() => {
      setDebouncedSearch(search.trim());
    }, 400);
    return () => clearTimeout(t);
  }, [search]);

  const fetchData = useCallback(
    async (pageNo) => {
      try {

        setLoading(true);

        const response = await api.post(
          apiUrl,
          {
            page: pageNo,
            sizePerPage,
            sortDirection: 'ASC',
            sortField: 'id',
            keyword: debouncedSearch || undefined
          }
        );

        setData(response.data.dtoList || []);
        setTotalPages(response.data.totalPages || 0);

      } catch (err) {

        console.log(err);
        setError(`Failed to load ${modelName}`);

      } finally {

        setLoading(false);

      }
    },
    [apiUrl, sizePerPage, debouncedSearch, modelName]
  );

  useEffect(() => {
    fetchData(currentPage);
  }, [currentPage, fetchData]);

  useEffect(() => {
    setCurrentPage(0);
  }, [debouncedSearch]);

  const deleteItem = async (identifier) => {

    const confirmDelete = globalThis.confirm(
      `Delete this ${modelName}?`
    );

    if (!confirmDelete) return;

    try {

      await api.delete(
        `${deleteUrl}?identifier=${identifier}`
      );

      const currentUsername = localStorage.getItem("username");

      console.log("Deleted:", identifier);
      console.log("Logged in user:", currentUsername);

      if (identifier === currentUsername) {

        localStorage.clear();

        alert("Your account was deleted. Redirecting to login...");

        router.replace("/login");

        return;
      }

      fetchData(currentPage);

    } catch (err) {
      console.log(err);
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

      setViewData(response.data);
      setShowViewModal(true);

    } catch (err) {

      console.error(err);
      alert("Failed to load details");

    }
  };

  const getCellValue = (row, column, index) => {

  if (column === 'S.No') {
    return currentPage * sizePerPage + index + 1;
  }

  if (customRender?.[column]) {
    return customRender[column](row, fetchData);
  }

  if (Array.isArray(row[column])) {
    return row[column].join(', ');
  }

  return row[column];
};

  const filteredData = data;

  const renderTableBody = () => {

  if (loading) {
    return (
      <tr>
        <td
          colSpan={columns.length + 1}
          className="text-center p-6 text-gray-500"
        >
          Loading...
        </td>
      </tr>
    );
  }

  if (filteredData.length === 0) {
    return (
      <tr>
        <td
          colSpan={columns.length + 1}
          className="text-center p-6 text-gray-500"
        >
          No data found
        </td>
      </tr>
    );
  }

  return filteredData.map((row, i) => (

    <tr
      key={row.identifier}
      className="border-b hover:bg-gray-50 transition"
    >

      {columns.map((c) => (

        <td
          key={`${row.identifier}-${c}`}
          className="p-3 text-gray-700"
        >
          {getCellValue(row, c, i)}
        </td>

      ))}

      <td className="p-3">

        <ActionButtons
          onView={() => {

            if (customViewHandler) {

              customViewHandler(
                row,
                setViewData,
                setShowViewModal
              );

            } else {

              handleView(row);

            }

          }}
          onEdit={() => handleEdit(row)}
          onDelete={() => deleteItem(row.identifier)}
        />

      </td>

    </tr>

  ));
};

  return (

    <Layout>

      <div className="max-w-7xl mx-auto mt-6 px-4">

        <div className="bg-white shadow-xl rounded-2xl p-6">

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
                  focus:ring-slate-500
                "
              />

              {AddComponent && (
                <button
                  onClick={() => setShowAddModal(true)}
                  className="
                    bg-slate-800
                    hover:bg-slate-700
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

          {error && (
            <div className="bg-red-100 border border-red-300 text-red-600 p-3 mb-4 rounded-lg text-center">
              {error}
            </div>
          )}

          <div className="overflow-x-auto rounded-xl border">

            <table className="w-full text-sm">

              <thead>

                <tr className="bg-slate-800 text-white text-left">

                  {columns.map((c) => (
                    <th
                      key={c}
                      className="p-3"
                    >
                      {c}
                    </th>
                  ))}

                  <th className="p-3 text-center">
                    Actions
                  </th>

                </tr>

              </thead>

              <tbody>
                {renderTableBody()}
              </tbody>

            </table>

          </div>

          <div className="flex justify-between items-center mt-6">

            <button
              disabled={currentPage === 0}
              onClick={() => setCurrentPage(currentPage - 1)}
              className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg disabled:opacity-40"
            >
              Prev
            </button>

            <span className="text-gray-700 font-medium">
              Page {currentPage + 1} of {totalPages}
            </span>

            <button
              disabled={currentPage + 1 >= totalPages}
              onClick={() => setCurrentPage(currentPage + 1)}
              className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg disabled:opacity-40"
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
                  fetchData(currentPage);
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
                  fetchData(currentPage);
                }}
              />

            </div>

          </div>

        )}

        {showViewModal && viewData && (

          <AuditViewModal
            data={viewData}
            onClose={() => {
              setShowViewModal(false);
              setViewData(null);
            }}
          />

        )}

      </div>

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
  customRender: PropTypes.objectOf(PropTypes.func),
  customViewHandler: PropTypes.func,
};

function AuditViewModal({
  data,
  onClose
}) {

  const formatDate = (value) => {

    if (!value) return "N/A";

    return new Date(value).toLocaleString(
      "en-IN",
      {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      }
    );
  };

  return (

    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

      <div className="bg-white rounded-2xl w-full max-w-2xl shadow-xl">

        <div className="flex justify-between items-center p-5 border-b">

          <h2 className="text-xl font-semibold">
            Audit Details
          </h2>

          <button
            onClick={onClose}
            className="text-red-500"
          >
            ✕
          </button>

        </div>

        <div className="p-6">

          <div className="grid grid-cols-2 gap-4">

            <div>
              <span className="text-gray-500 text-sm block">
                Identifier
              </span>

              <p className="font-medium">
                {data.identifier}
              </p>
            </div>

            {data.description && (
              <div>
                <span className="text-gray-500 text-sm block">
                  Description
                </span>

                <p className="font-medium">
                  {data.description}
                </p>
              </div>
            )}

            <div>
              <span className="text-gray-500 text-sm block">
                Created By
              </span>

              <p className="font-medium">
                {data.createdBy || "N/A"}
              </p>
            </div>

            <div>
              <span className="text-gray-500 text-sm block">
                Created On
              </span>

              <p className="font-medium">
                {formatDate(data.createdOn)}
              </p>
            </div>

            <div>
              <span className="text-gray-500 text-sm block">
                Modified By
              </span>

              <p className="font-medium">
                {data.modifiedBy || "N/A"}
              </p>
            </div>

            <div>
              <span className="text-gray-500 text-sm block">
                Modified On
              </span>

              <p className="font-medium">
                {formatDate(data.modifiedOn)}
              </p>
            </div>

          </div>

        </div>

      </div>

    </div>
  );
}
AuditViewModal.propTypes = {
  data: PropTypes.shape({
    identifier: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
    ]),
    description: PropTypes.string,
    createdBy: PropTypes.string,
    createdOn: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
      PropTypes.instanceOf(Date),
    ]),
    modifiedBy: PropTypes.string,
    modifiedOn: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
      PropTypes.instanceOf(Date),
    ]),
  }).isRequired,
  onClose: PropTypes.func.isRequired,
};

export default CommonList;