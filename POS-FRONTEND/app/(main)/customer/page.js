"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import api,{
  listItems,
  addItem,
  updateItem,
  getItem,
} from "@/services/api";

export default function CustomerList() {
  const [customers, setCustomers] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewCustomer, setViewCustomer] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  const emptyCustomer = {
  identifier: "",
  email: "",
  phoneno: "",
  address: "",
  partytype: "",

  billing: {
    addressLine: "",
    city: "",
    state: "",
    pincode: "",
    country: "",
  },

  shipping: {
    addressLine: "",
    city: "",
    state: "",
    pincode: "",
    country: "",
  },
};

  const [newCustomer, setNewCustomer] =
    useState(emptyCustomer);

  const [editCustomer, setEditCustomer] =
    useState(null);

  // ================= FETCH =================

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems(
        "customer",
        {
          page,
          sizePerPage,
          sortField: "identifier",
          search: searchTerm,
        }
      );

      console.log(
        "CUSTOMER RESPONSE:",
        res
      );

      let data = [];
      let pages = 1;

      // Backend returns List<CustomerDto>
      if (Array.isArray(res)) {
        data = res;
      }

      // Backend returns WsDto
      else if (
        res &&
        Array.isArray(res.content)
      ) {
        data = res.content;
        pages = res.totalPages || 1;
      }

      console.log(
        "CUSTOMER DATA:",
        data
      );

      setCustomers(data);
      setTotalPages(pages);
    } catch (err) {
      console.error(
        "Customer fetch failed:",
        err
      );

      setError(
        err?.response?.data?.message ||
          "Failed to load customers"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [page, searchTerm]);

  // ================= ADD =================

  const handleAddCustomer =
    async () => {
      try {
        const payload = {
          ...newCustomer,

          billing: {
            ...newCustomer.billing,
            identifier:
              newCustomer.identifier,
          },

          shipping: {
            ...newCustomer.shipping,
            identifier:
              newCustomer.identifier,
          },
        };

        const res = await addItem(
          "customer",
          payload
        );

        if (
          res?.success === false
        ) {
          setMessage(
            res.message
          );
          return false;
        }

        setMessage(
          "Customer Added Successfully"
        );

        setNewCustomer(
          emptyCustomer
        );

        await fetchCustomers();

        return true;
      } catch (err) {
        console.error(err);

        setMessage(
          err?.response?.data
            ?.message ||
            err?.message ||
            "Add failed"
        );

        return false;
      }
    };

  // ================= UPDATE =================

 const handleUpdate = async () => {
  try {
    const payload = {
      ...editCustomer,

      billing: {
        ...editCustomer.billing,
        identifier: editCustomer.identifier,
      },

      shipping: {
        ...editCustomer.shipping,
        identifier: editCustomer.identifier,
      },
    };

    console.log("UPDATE PAYLOAD", payload);

    const res = await updateItem(
      "customer",
      payload
    );

    if (res?.success === false) {
      setMessage(res.message);
      return;
    }

    setMessage(
      "Customer Updated Successfully"
    );

    setEditCustomer(null);

    await fetchCustomers();
  } catch (err) {
    console.error(err);

    setMessage(
      err?.response?.data?.message ||
        err?.message ||
        "Update failed"
    );
  }
};

  // ================= DELETE =================

 const handleDelete = async (email) => {
  if (!confirm(`Delete ${email}?`)) {
    return;
  }

  try {
    await api.delete("/api/customer/delete", {
      params: {
        email,
      },
    });

    setMessage("Customer Deleted Successfully");

    await fetchCustomers();
  } catch (err) {
    console.error(err);

    setMessage(
      err?.response?.data?.message ||
      err?.message ||
      "Delete failed"
    );
  }
};

  // ================= COLUMNS =================

  const columns = [
    {
      label: "ID",
      render: (
        row,
        index
      ) =>
        page *
          sizePerPage +
        index +
        1,
    },

    {
      label: "Identifier",
      key: "identifier",
    },

    {
      label: "Phone",
      key: "phoneno",
    },

    {
      label: "Email",
      key: "email",
    },

    {
      label: "Type",
      key: "partytype",
    },

    {
      label: "Address",
      key: "address",
    },
  ];

  // ================= ACTIONS =================

  const actions = [
  {
    label: "✏️",
    onClick: async (row) => {
      try {
        const customer =
          await getItem(
            "customer",
            row.identifier
          );

        setEditCustomer(customer);
        setMessage("");
      } catch (err) {
        console.error(err);
      }
    },
  },

  {
    label: "🗑",
    onClick: (row) =>
      handleDelete(row.email),
  },
  {
      label: "👁️",
      onClick: (row) => setViewCustomer(row),
    },
];

  // ================= FORMS =================

  const addFields = [
  {
    name: "identifier",
    label: "Customer Name",
    required: true,
  },

  {
    name: "phoneno",
    label: "Phone Number",
  },

  {
    name: "email",
    label: "Email",
    type: "email",
  },

  {
    name: "address",
    label: "Address",
  },

  {
    name: "partytype",
    label: "Party Type",
    type: "select",
    options: [
      {
        label: "Customer",
        value: "Customer",
      },
      {
        label: "Dealer",
        value: "Dealer",
      },
      {
        label: "Supplier",
        value: "Supplier",
      },
    ],
  },

  {
    name: "billing.addressLine",
    label: "Billing Address Line",
  },

  {
    name: "billing.city",
    label: "Billing City",
  },

  {
    name: "billing.state",
    label: "Billing State",
  },

  {
    name: "billing.pincode",
    label: "Billing Pincode",
  },

  {
    name: "billing.country",
    label: "Billing Country",
  },

  {
    name: "shipping.addressLine",
    label: "Shipping Address Line",
  },

  {
    name: "shipping.city",
    label: "Shipping City",
  },

  {
    name: "shipping.state",
    label: "Shipping State",
  },

  {
    name: "shipping.pincode",
    label: "Shipping Pincode",
  },

  {
    name: "shipping.country",
    label: "Shipping Country",
  },
];

  const editFields = [
  {
    name: "identifier",
    label: "Customer Name",
    disabled: true,
  },

  {
    name: "phoneno",
    label: "Phone Number",
  },

  {
    name: "email",
    label: "Email",
  },

  {
    name: "address",
    label: "Address",
  },

  {
    name: "partytype",
    label: "Party Type",
    type: "select",
    options: [
      {
        label: "Customer",
        value: "Customer",
      },
      {
        label: "Dealer",
        value: "Dealer",
      },
      {
        label: "Supplier",
        value: "Supplier",
      },
    ],
  },

  {
    name: "billing.addressLine",
    label: "Billing Address Line",
  },

  {
    name: "billing.city",
    label: "Billing City",
  },

  {
    name: "billing.state",
    label: "Billing State",
  },

  {
    name: "billing.pincode",
    label: "Billing Pincode",
  },

  {
    name: "billing.country",
    label: "Billing Country",
  },

  {
    name: "shipping.addressLine",
    label: "Shipping Address Line",
  },

  {
    name: "shipping.city",
    label: "Shipping City",
  },

  {
    name: "shipping.state",
    label: "Shipping State",
  },

  {
    name: "shipping.pincode",
    label: "Shipping Pincode",
  },

  {
    name: "shipping.country",
    label: "Shipping Country",
  },
];

  return (
    <>
    <CommonList
      title="Customers"
      data={customers}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={
        setSearchTerm
      }
      message={message}
      setMessage={setMessage}
      onAdd={() => {}}
      addButtonText="+ Add Customer"
      addFields={addFields}
      newItem={newCustomer}
      setNewItem={
        setNewCustomer
      }
      handleAdd={
        handleAddCustomer
      }
      editItem={editCustomer}
      setEditItem={
        setEditCustomer
      }
      handleUpdate={
        handleUpdate
      }
      editFields={editFields}
      actions={actions}
      emptyMessage="No customers found"
    />
    {viewCustomer && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Customer Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setViewCustomer(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewCustomer.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewCustomer.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewCustomer.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewCustomer.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewCustomer.modifiedOn ||
                  "-"}
              </strong>
            </div>
          </div>
        </div>
      </div>
    )}
    </>
  );
}