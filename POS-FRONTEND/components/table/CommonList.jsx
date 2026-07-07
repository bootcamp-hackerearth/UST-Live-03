"use client";

import PropTypes from "prop-types";
import { useEffect, useState, useRef } from "react";
import { useRouter } from "next/navigation";
import {
  listItems,
  toggleItem,
  deleteItem,
  DEFAULT_PAGINATION,
} from "@/services/api";
import { extractErrorInfo, isModalErrorStatus } from "@/utils/httpError";
import DataTable from "./DataTable";
import TablePagination from "./TablePagination";
import DeleteModal from "./DeleteModal";
import TableSkeleton from "./TableSkeleton";
import ErrorModal from "@/components/common/ErrorModal";
import { useAuth } from "@/context/AuthContext";
import Alert from "@/components/common/Alert";

export default function CommonList({
  title,
  subtitle,
  entity,
  addPath,
  editPath,
  columns,
  showToggle = true,
  identifierField = "identifier",
}) {
  const router = useRouter();
  const [items, setItems] = useState([]);
  const { loadUserData } = useAuth();
  const [alertMessage, setAlertMessage] = useState("");
  const [initialLoading, setInitialLoading] = useState(true);
  const [tableLoading, setTableLoading] = useState(false);
  const [error, setError] = useState("");
  const [modalError, setModalError] = useState(null);
  const [toast, setToast] = useState("");
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [selectedItem, setSelectedItem] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGINATION.sizePerPage);
  const [searchInput, setSearchInput] = useState("");
  const [keyword, setKeyword] = useState("");
  const debounceRef = useRef(null);

  const loadItems = async (isInitial = false) => {
    try {
      if (isInitial) {
        setInitialLoading(true);
      } else {
        setTableLoading(true);
      }
      setError("");
      const data = await listItems(entity, {
        page: currentPage,
        sizePerPage: pageSize,
        keyword: keyword,
      });
      setItems(data?.items || []);
      setTotalPages(Number(data?.totalPages) || 0);
      setTotalRecords(Number(data?.totalRecords) || 0);
      setPageSize(Number(data?.sizePerPage) || DEFAULT_PAGINATION.sizePerPage);
    } catch (err) {
      console.log(err);
      const { status, message } = extractErrorInfo(
        err,
        `Failed to load ${title}`,
      );
      if (isModalErrorStatus(status)) {
        setModalError({ status, message });
      } else {
        setError(message);
      }
    } finally {
      setInitialLoading(false);
      setTableLoading(false);
    }
  };

  useEffect(() => {
    loadItems(true);
  }, []);

  useEffect(() => {
    if (initialLoading) return;
    loadItems(false);
  }, [currentPage, keyword]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchInput(value);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setCurrentPage(0);
      setKeyword(value);
    }, 400);
  };

  const handleClearSearch = () => {
    setSearchInput("");
    setCurrentPage(0);
    setKeyword("");
    if (debounceRef.current) clearTimeout(debounceRef.current);
  };

  const handleToggleStatus = async (identifier, currentStatus) => {
    try {
      const updatedStatus = !currentStatus;
      await toggleItem(entity, identifier, updatedStatus);
      setItems((previousItems) =>
        previousItems.map((item) =>
          item[identifierField] === identifier
            ? { ...item, status: updatedStatus }
            : item,
        ),
      );
      await loadUserData();
      showToast("Status Updated Successfully");
    } catch (err) {
      console.log(err);
      const { status, message } = extractErrorInfo(
        err,
        "Failed To Update Status",
      );
      if (isModalErrorStatus(status)) {
        setModalError({ status, message });
      } else {
        showToast("Failed To Update Status");
      }
    }
  };

  const handleDeleteItem = async () => {
    try {
      const extraData =
        entity === "customer" && selectedItem?.phoneNo
          ? { phoneNo: selectedItem.phoneNo }
          : {};

      const result = await deleteItem(
        entity,
        selectedItem[identifierField],
        extraData,
      );

      if (result?.success === false) {
        setAlertMessage(result?.message || "Failed to delete");
        setTimeout(() => setAlertMessage(""), 4000);
        setShowDeleteModal(false);
        setSelectedItem(null);
        return;
      }

      setItems((previousItems) =>
        previousItems.filter(
          (item) => item[identifierField] !== selectedItem[identifierField],
        ),
      );

      showToast("Deleted Successfully");
      await loadItems(false);
      await loadUserData();
      setShowDeleteModal(false);
      setSelectedItem(null);
    } catch (err) {
      console.log(err);
      const { status, message } = extractErrorInfo(err, "Failed To Delete");
      setShowDeleteModal(false);
      setSelectedItem(null);
      if (isModalErrorStatus(status)) {
        setModalError({ status, message });
      } else {
        showToast("Failed To Delete");
      }
    }
  };

  const showToast = (message) => {
    setToast(message);
    setTimeout(() => setToast(""), 800);
  };

  if (initialLoading) return <TableSkeleton />;

  return (
    <div className="max-w-7xl mx-auto">
      <ErrorModal
        open={!!modalError}
        status={modalError?.status}
        message={modalError?.message}
        onClose={() => setModalError(null)}
      />

      {toast && (
        <div className="fixed top-6 right-6 z-50 bg-[#111827] text-white px-6 py-4 rounded-2xl shadow-2xl">
          {toast}
        </div>
      )}

      <Alert type="error" message={alertMessage} />

      <DeleteModal
        open={showDeleteModal}
        title={title}
        item={selectedItem}
        onClose={() => {
          setShowDeleteModal(false);
          setSelectedItem(null);
        }}
        onConfirm={handleDeleteItem}
      />

      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-4xl font-bold text-gray-900">{title}</h1>
          <p className="text-gray-500 mt-2 text-lg">{subtitle}</p>
        </div>

        <button
          type="button"
          onClick={() => router.push(addPath)}
          className="h-12 px-6 rounded-2xl bg-blue-600 hover:bg-blue-500 text-white font-semibold transition-all shadow-lg shadow-blue-500/20"
        >
          + Add
        </button>
      </div>

      {error ? (
        <div className="flex items-center justify-center h-125">
          <div className="bg-red-50 border border-red-200 text-red-600 px-6 py-4 rounded-2xl">
            {error}
          </div>
        </div>
      ) : (
        <>
          <div className="flex items-center gap-4 mb-5">
            <div className="relative w-96">
              <input
                type="text"
                value={searchInput}
                onChange={handleSearchChange}
                placeholder={`Search ${title}...`}
                className="w-full h-11 pl-4 pr-4 text-sm bg-white border border-gray-200 rounded-xl text-gray-800 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent shadow-sm transition-all"
              />
            </div>

            {keyword && (
              <div className="flex items-center gap-3">
                <span className="text-sm text-gray-500">
                  {totalRecords} result{totalRecords !== 1 ? "s" : ""} for{" "}
                  <span className="font-medium text-gray-700">"{keyword}"</span>
                </span>

                <button
                  type="button"
                  onClick={handleClearSearch}
                  className="text-sm font-medium text-red-500 hover:text-red-700 transition-colors"
                >
                  Clear Search
                </button>
              </div>
            )}
          </div>

          <div className="relative">
            {tableLoading && (
              <div className="absolute inset-0 bg-white/60 rounded-2xl z-10" />
            )}

            <DataTable
              items={items}
              columns={columns}
              showToggle={showToggle}
              editPath={editPath}
              identifierField={identifierField}
              onToggleStatus={handleToggleStatus}
              onDelete={(item) => {
                setSelectedItem(item);
                setShowDeleteModal(true);
              }}
            />
          </div>

          <TablePagination
            currentPage={currentPage}
            totalPages={totalPages}
            totalRecords={totalRecords}
            pageSize={pageSize}
            onPageChange={setCurrentPage}
          />
        </>
      )}
    </div>
  );
}

CommonList.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  entity: PropTypes.string.isRequired,
  addPath: PropTypes.string,
  editPath: PropTypes.string,
  columns: PropTypes.array.isRequired,
  showToggle: PropTypes.bool,
  identifierField: PropTypes.string,
};
