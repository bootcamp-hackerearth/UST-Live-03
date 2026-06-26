"use client";

import { useState, useEffect } from "react";
import CommonList from "@/app/components/CommonList";

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
  const [viewCustomer, setViewCustomer] = useState(null);

  const sizePerPage = 5;

  const fetchCustomers = async () => {
    setLoading(true);

    try {
      const res = await listItems("customer", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      console.log("CUSTOMER RESPONSE:", res);

      const customerData =
        res?.content ??
        res?.data ??
        (Array.isArray(res) ? res : []);

      setData(customerData);

      setTotalPages(
        res?.totalPages ||
        res?.total_page ||
        1
      );

    } catch (err) {
      console.error("FETCH ERROR:", err);
      setData([]); // prevent crash
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [page, searchTerm]);

  const handleAdd = async () => {

    if (!newItem.identifier?.trim()) {
      alert("Identifier is required");
      return;
    }

    const payload = {
      identifier: newItem.identifier,
      phoneno: newItem.phoneno,
      email: newItem.email,
      address: newItem.address,
      partytype: newItem.partytype,

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

  const handleUpdate = async () => {
    const payload = {
      id: editItem.id,
      identifier: editItem.identifier,
      phoneno: editItem.phoneno,
      email: editItem.email,
      address: editItem.address,
      partytype: editItem.partytype,

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

  const handleDelete = async (row) => {
    await deleteItem("customer", row.identifier);
    fetchCustomers();
  };

  const fields = [
    { name: "identifier", label: "Identifier", disabled: true },
    { name: "phoneno", label: "Phone No" },
    { name: "email", label: "Email" },
    { name: "address", label: "Address" },

    {
      name: "partytype",
      label: "Party Type",
      type: "select",
      options: [
        { label: "Customer", value: "Customer" },
        { label: "Dealer", value: "Dealer" },
        { label: "Supplier", value: "Supplier" },
      ],
    },

    { name: "billing.addressLine", label: "Billing Address Line" },
    { name: "billing.city", label: "Billing City" },
    { name: "billing.state", label: "Billing State" },
    { name: "billing.pincode", label: "Billing Pincode" },
    { name: "billing.country", label: "Billing Country" },

    { name: "shipping.addressLine", label: "Shipping Address Line" },
    { name: "shipping.city", label: "Shipping City" },
    { name: "shipping.state", label: "Shipping State" },
    { name: "shipping.pincode", label: "Shipping Pincode" },
    { name: "shipping.country", label: "Shipping Country" },
  ];

  const columns = [
    { key: "identifier", label: "Identifier" },
    { key: "phoneno", label: "Phone" },
    { key: "email", label: "Email" },
    { key: "partytype", label: "Type" },
    { key: "address", label: "Address" },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewCustomer(row),
    },
    {
      type: "edit",
      label: "✏️ Edit",
      onClick: (row) => {
        setEditItem({
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
      }
    },
    {
      type: "delete",
      label: "🗑 Delete",
      onClick: handleDelete,
    },
  ];

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
      viewItem={viewCustomer}
      setViewItem={setViewCustomer}
      actions={actions}
      emptyMessage="No customers found"
    />
  );
};

export default CustomerPage;