"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import {listItems, toggleItem, deleteItem, DEFAULT_PAGINATION} from "@/services/api";
import DataTable from "./DataTable";
import TablePagination from "./TablePagination";
import DeleteModal from "./DeleteModal";
import TableSkeleton from "./TableSkeleton";
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

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [toast, setToast] =
    useState("");

  const [totalPages, setTotalPages] =
    useState(0);

  const [totalRecords, setTotalRecords] =
    useState(0);

  const [currentPage, setCurrentPage] =
    useState(0);

  const [selectedItem, setSelectedItem] =
    useState(null);

  const [showDeleteModal,
    setShowDeleteModal] =
    useState(false);

  const [pageSize, setPageSize] = useState(DEFAULT_PAGINATION.sizePerPage);

  const loadItems = async () => {

    try {

      setLoading(true);
      setError("");

      const data = await listItems(
        entity,
        {
          page: currentPage,
          sizePerPage: pageSize,
        }
      );

      setItems(data?.items || []);

      setTotalPages(
        Number(data?.totalPages) || 0
      );

      setTotalRecords(
        Number(data?.totalRecords) || 0
      );

      setPageSize(
        Number(data?.sizePerPage) || DEFAULT_PAGINATION.sizePerPage
      );

    } catch (error) {
      console.log(error);

      setError(
        `Failed to load ${title}`
      );

    } finally {
      setLoading(false);
    }

  };

  useEffect(() => {

    loadItems();

  }, [currentPage]);

  const handleToggleStatus = async (
    identifier,
    currentStatus
  ) => {

    try {

      const updatedStatus =
        !currentStatus;

      await toggleItem(
        entity,
        identifier,
        updatedStatus
      );

      setItems((previousItems) =>
        previousItems.map((item) =>

          item[identifierField] === identifier
            ? {
                ...item,
                status: updatedStatus,
              }
            : item
        )
      );

      await(loadUserData());

      showToast(
        "Status Updated Successfully"
      );

    } catch (error) {
      console.log(error);

      showToast(
        "Failed To Update Status"
      );
    }
  };

  const handleDeleteItem = async () => {
    try {
      const success = await deleteItem(
        entity,
        selectedItem[identifierField]
      );

      if (entity === "category" && !success) {
        setAlertMessage(
          "Cannot delete category because it is used as a super category"
        );
        setTimeout(() => {
          setAlertMessage("");
        }, 3000);
        
        setShowDeleteModal(false);
        setSelectedItem(null);
        return;
      }

      setItems((previousItems) =>

        previousItems.filter(

          (item) =>

            item[identifierField] !==
            selectedItem[identifierField]
        )
      );

      showToast( "Deleted Successfully" );
      await loadItems();
      await loadUserData();
      setShowDeleteModal(false);
      setSelectedItem(null);

    } catch (error) {
      console.log(error);
      showToast("Failed To Delete");
    }
  };

  const showToast = (message) => {
    setToast(message);

    setTimeout(() => {
      setToast("");
    }, 800);
  };

  if (loading) {
    return <TableSkeleton />;
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-125">
        <div className="bg-red-50 border border-red-200 text-red-600 px-6 py-4 rounded-2xl">
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto">

      {
        toast && (
          <div className="fixed top-6 right-6 z-50 bg-[#111827] text-white px-6 py-4 rounded-2xl shadow-2xl">
            {toast}
          </div>
        )
      }

      <Alert
        type="error"
        message={alertMessage}
      />

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
          <h1 className="text-4xl font-bold text-gray-900">
            {title}
          </h1>

          <p className="text-gray-500 mt-2 text-lg">
            {subtitle}
          </p>

        </div>

        <button
          onClick={() =>
            router.push(addPath)
          }
          className="h-12 px-6 rounded-2xl bg-blue-600 hover:bg-blue-500 text-white font-semibold transition-all shadow-lg shadow-blue-500/20"
        >
          + Add
        </button>
      </div>

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

      <TablePagination
        currentPage={currentPage}
        totalPages={totalPages}
        totalRecords={totalRecords}
        pageSize={pageSize}
        onPageChange={setCurrentPage}
      />
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