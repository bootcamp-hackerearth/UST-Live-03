"use client";

import { useState, useEffect } from "react";
import CommonList from "@/app/components/CommonList/CommonList";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const CustomerPage = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [searchTerm, setSearchTerm] = useState("");

  const [newItem, setNewItem] = useState({});
  const [editItem, setEditItem] = useState(null);
  const [viewItem, setViewItem] = useState(null);

  const sizePerPage = 5;

  // ================= FETCH =================
  const fetchCustomers = async () => {
    if (data.length === 0) {
      setLoading(true);
    }

    try {
      const res = await listItems("customer", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      console.log("CUSTOMER ROW:", res.content?.[0]);

      setData(res.content || []);
      setTotalPages(res.totalPages || 1);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  // ================= ADD =================
  const handleAdd = async () => {
    const payload = {
      identifier: newItem.identifier,
      phoneNo: newItem.phoneNo,
      email: newItem.email,
      address: newItem.address,
      partyType: newItem.partyType,

      billing: {
        addressLine: newItem["billing.addressLine"],
        city: newItem["billing.city"],
        state: newItem["billing.state"],
        pincode: newItem["billing.pincode"],
        country: newItem["billing.country"],
      },

      shipping: {
        addressLine: newItem["shipping.addressLine"],
        city: newItem["shipping.city"],
        state: newItem["shipping.state"],
        pincode: newItem["shipping.pincode"],
        country: newItem["shipping.country"],
      },
    };

    const res = await addItem("customer", payload);

    fetchCustomers();
    return res;
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    const payload = {
      id: editItem.id,
      identifier: editItem.identifier,
      phoneNo: editItem.phoneNo,
      email: editItem.email,
      address: editItem.address,
      partyType: editItem.partyType,

      billing: {
        id: editItem.billing?.id,
        addressLine: editItem["billing.addressLine"],
        city: editItem["billing.city"],
        state: editItem["billing.state"],
        pincode: editItem["billing.pincode"],
        country: editItem["billing.country"],
      },

      shipping: {
        id: editItem.shipping?.id,
        addressLine: editItem["shipping.addressLine"],
        city: editItem["shipping.city"],
        state: editItem["shipping.state"],
        pincode: editItem["shipping.pincode"],
        country: editItem["shipping.country"],
      },
    };

    const res = await updateItem("customer", payload);

    setEditItem(null);
    fetchCustomers();
    return res;
  };

  // ================= DELETE =================
  const handleDelete = async (row) => {
    if (!globalThis.confirm(`Are you sure you want to delete ${row.identifier}?`)) {
      return;
    }
    try {
      await deleteItem("customer", row.identifier);
      fetchCustomers();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= FIELDS =================
  const fields = [
    { name: "identifier", label: "Customer Name" },

    {
      name: "phoneNo",
      label: "Phone No",
      pattern: /^\d{10}$/,
      errorMessage: "Enter exactly 10 digits",
    },

    {
      name: "email",
      label: "Email",
      pattern: /^[^\s@]{1,64}@[^\s@]{1,255}\.[^\s@]{2,20}$/,
      errorMessage: "Enter a valid email address",
    },

    { name: "address", label: "Address" },

    {
      name: "partyType",
      label: "Party Type",
      type: "select",
      options: [
        { label: "Customer", value: "Customer" },
        { label: "Dealer", value: "Dealer" },
        { label: "Supplier", value: "Supplier" },
      ],
      required: false,
    },

    { name: "billing.addressLine", label: "Billing Address Line", required: false },
    { name: "billing.city", label: "Billing City", required: false },
    { name: "billing.state", label: "Billing State", required: false },
    {
      name: "billing.pincode",
      label: "Billing Pincode",
      required: false,
      pattern: /^\d{6}$/,
      errorMessage: "Enter a valid 6 digit pincode",
    },
    { name: "billing.country", label: "Billing Country", required: false },

    { name: "shipping.addressLine", label: "Shipping Address Line", required: false },
    { name: "shipping.city", label: "Shipping City", required: false },
    { name: "shipping.state", label: "Shipping State", required: false },
    {
      name: "shipping.pincode",
      label: "Shipping Pincode",
      required: false,
      pattern: /^\d{6}$/,
      errorMessage: "Enter a valid 6 digit pincode",
    },
    { name: "shipping.country", label: "Shipping Country", required: false },
  ];

  // ================= COLUMNS =================
  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    { key: "identifier", label: "Customer Name" },
    { key: "phoneNo", label: "Phone" },
    { key: "email", label: "Email" },
    { key: "partyType", label: "Party Type" },
    { key: "address", label: "Address" },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      type: "view",
      label: "View",
      onClick: (row) => {
        setViewItem(mapCustomerAddresses(row));
      }
    },

    {
      type: "edit",
      label: "Edit",
      onClick: (row) => {
        setEditItem(mapCustomerAddresses(row));
      }
    },
    {
      type: "delete",
      label: "Delete",
      onClick: handleDelete,
    },
  ];

  const mapCustomerAddresses = (row) => ({
    ...row,

    "billing.addressLine": row.billing?.addressLine || "",
    "billing.city": row.billing?.city || "",
    "billing.state": row.billing?.state || "",
    "billing.pincode": row.billing?.pincode || "",
    "billing.country": row.billing?.country || "",

    "shipping.addressLine": row.shipping?.addressLine || "",
    "shipping.city": row.shipping?.city || "",
    "shipping.state": row.shipping?.state || "",
    "shipping.pincode": row.shipping?.pincode || "",
    "shipping.country": row.shipping?.country || "",
  });

  return (
    <CommonList
      title="Customers"
      data={data}
      columns={columns}
      loading={loading}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => { }}
      addFields={fields}
      newItem={newItem}
      setNewItem={setNewItem}
      handleAdd={handleAdd}
      editItem={editItem}
      setEditItem={setEditItem}
      handleUpdate={handleUpdate}
      editFields={fields}
      viewItem={viewItem}
      setViewItem={setViewItem}
      viewFields={fields}
      actions={actions}
    />
  );
};

export default CustomerPage;