'use client';

import { useEffect, useState } from 'react';
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
  customRender
}) => {

  const [data, setData] = useState([]);
  const [allData, setAllData] = useState([]);
  const [search, setSearch] = useState('');

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const [selectedItem, setSelectedItem] = useState(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [sizePerPage] = useState(2);
  const router = useRouter();

  useEffect(() => {
    fetchData();
  }, [currentPage]);

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchData = async () => {
    try {

      setLoading(true);

      const response = await api.post(
        apiUrl,
        {
          page: currentPage,
          sizePerPage,
          sortDirection: 'ASC',
          sortField: 'id'
        }
      );

      setData(response.data.dtoList || []);
      setTotalPages(response.data.totalPages || 0);

    } catch (err) {

      console.error(err);
      setError(`Failed to load ${modelName}`);

    } finally {

      setLoading(false);

    }
  };

  const fetchAllData = async () => {

    try {

      let page = 0;
      let pages = 1;
      let records = [];

      while (page < pages) {

        const response = await api.post(
          apiUrl,
          {
            page,
            sizePerPage: 1000,
            sortDirection: 'ASC',
            sortField: 'id'
          }
        );

        records = [
          ...records,
          ...(response.data.dtoList || [])
        ];

        pages = response.data.totalPages || 1;
        page++;

      }

      setAllData(records);

    } catch (err) {

      console.error(err);

    }

  };

  const deleteItem = async (identifier) => {

  const confirmDelete = globalThis.confirm(
    `Delete this ${modelName}?`
  );

  if (!confirmDelete) return;

  try {

    await api.get(
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

    fetchData();
    fetchAllData();

  } catch (err) {
    console.error(err);
    alert(`Failed to delete ${modelName}`);
  }
};

  const handleEdit = (item) => {

    setSelectedItem(item);
    setShowUpdateModal(true);

  };

  const filteredData = search
    ? allData.filter((row) =>
        Object.values(row)
          .join(' ')
          .toLowerCase()
          .includes(search.toLowerCase())
      )
    : data;

  if (loading) {

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

          <div className="overflow-hidden rounded-xl border">

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

                      {
                        (() => {

                          if (c === 'S.No') {

                            return search ? i + 1 : currentPage * sizePerPage + i + 1;
                          }
                          if (customRender?.[c]) {
                            return customRender[c](row, fetchData);
                          }
                          if (Array.isArray(row[c])) {
                            return row[c].join(', ');
                          }
                          return row[c];
                          })()
                          }
                          </td>
                        ))}

                      <td className="p-3">

                        <ActionButtons
                          onEdit={() => handleEdit(row)}
                          onDelete={() => deleteItem(row.identifier)}
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

          {!search && (
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
          )}

        </div>

        {showAddModal && AddComponent && (

          <div className="fixed inset-0 bg-slate-100/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="w-full max-w-4xl bg-white rounded-2xl border border-[#163D4A] shadow-[0_8px_30px_rgba(22,61,74,0.15)] overflow-hidden">

              <AddComponent
                closeModal={() => setShowAddModal(false)}
                refreshData={() => {
                  fetchData();
                  fetchAllData();
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
                  fetchAllData();
                }}
              />

            </div>

          </div>

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
};

export default CommonList;