"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const CustomerList = () => {
  const [customers, setCustomers] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editCustomer, setEditCustomer] = useState(null);

  const [auditCustomer, setAuditCustomer] = useState(null);

  const sizePerPage = 5;

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("customer", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setCustomers(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);

      setError("Failed to load customers");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete customer ${identifier}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("customer", identifier, "identifier");

      fetchCustomers();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editCustomer,
        phoneno: Number(editCustomer.phoneno),
      };

      if (editCustomer.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("customer", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("customer", payload);
      }

      await fetchCustomers();

      setEditCustomer(null);
    } catch (err) {
      console.error(err);

      alert(
        editCustomer?.isNew
          ? "Failed to add customer"
          : "Failed to update customer",
      );
    }
  };

  const columns = [
    {
      label: "ID",
      key: "id",
    },
    {
      label: "Username",
      key: "identifier",
    },
    {
      label: "Phone No",
      key: "phoneno",
    },
    {
      label: "Email",
      key: "email",
    },
    {
      label: "Party Type",
      key: "partytype",
    },
  ];
  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditCustomer(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditCustomer({
          ...row,
          isNew: false,
          formTitle: "Edit Customer",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const editFields = [
    {
      name: "id",
      label: "ID",
      disabled: true,
    },
    {
      name: "identifier",
      label: "Username",
      disabled: !editCustomer?.isNew,
    },
    {
      name: "address",
      label: "Address",
    },
    {
      name: "email",
      label: "Email",
      type: "email",
    },
    {
      name: "phoneno",
      label: "Phone Number",
      type: "number",
    },
    {
      name: "partytype",
      label: "Party Type",
      type: "select",
      options: [
        {
          label: "Retail",
          value: "Retail",
        },
        {
          label: "Wholesale",
          value: "Wholesale",
        },
        {
          label: "Distributor",
          value: "Distributor",
        },
      ],
    },
  ];

  return (
    <>
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginBottom: "16px",
        }}
      >
        <button
          onClick={() =>
            setEditCustomer({
              id: "",
              identifier: "",
              address: "",
              email: "",
              phoneno: "",
              partytype: "Retail",
              isNew: true,
              formTitle: "Add Customer",
            })
          }
          style={{
            background: "#1976d2",
            color: "#fff",
            border: "none",
            borderRadius: "6px",
            padding: "10px 18px",
            cursor: "pointer",
            fontWeight: "600",
          }}
        >
          + Add Customer
        </button>
      </div>

      <CommonList
        title="Customers"
        data={customers}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editCustomer}
        setEditItem={setEditCustomer}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editCustomer?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No customers found"
      />

      {auditCustomer && (
        <div className="modalOverlay">
          <div
            className="modal"
            style={{
              width: "500px",
              padding: "20px",
            }}
          >
            <h2>Audit Details</h2>

            <div
              style={{
                display: "grid",
                gap: "12px",
                marginTop: "16px",
              }}
            >
              <div>
                <strong>Customer:</strong> {auditCustomer.identifier}
              </div>

              <div>
                <strong>Email:</strong> {auditCustomer.email || "N/A"}
              </div>

              <div>
                <strong>Phone:</strong> {auditCustomer.phoneno || "N/A"}
              </div>

              <div>
                <strong>Party Type:</strong> {auditCustomer.partytype || "N/A"}
              </div>

              <div>
                <strong>Created By:</strong> {auditCustomer.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditCustomer.createdOn
                  ? new Date(auditCustomer.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong>{" "}
                {auditCustomer.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditCustomer.modifiedOn
                  ? new Date(auditCustomer.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditCustomer.status ? "Active" : "Inactive"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditCustomer(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default CustomerList;
