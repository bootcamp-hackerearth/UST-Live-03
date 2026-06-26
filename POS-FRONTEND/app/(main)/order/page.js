"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import api ,{
  listItems,
  getItem,
  updateItem,
} from "@/services/api";

export default function OrderPage() {

  const [orders, setOrders] = useState([]);

  const [searchTerm, setSearchTerm] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(0);

  const [message, setMessage] =
    useState("");

  const [editItem, setEditItem] =
    useState(null);
  const [selectedOrder, setSelectedOrder] =
  useState(null);
  const [orderItems, setOrderItems] = useState([]);

const [showOrderModal, setShowOrderModal] =
  useState(false);

  const handleOrderClick = async (row) => {
  try {
    const data = await getItem(
      "order",
      row.identifier
    );

    const itemsRes = await api.get(
      "/api/orderitem/getByOrder",
      {
        params: {
          orderIdentifier: row.identifier,
        },
      }
    );

    setSelectedOrder(data);
    setOrderItems(itemsRes.data || []);
    setShowOrderModal(true);
  } catch (err) {
    console.error(err);
  }
};
  const loadOrders = async () => {

    try {

      const response =
        await listItems(
          "order",
          {
            page,
            sizePerPage: 5,
            sortField: "identifier",
            search: searchTerm,
          }
        );

      setOrders(
        response?.content || []
      );

      setTotalPages(
        response?.totalPages || 0
      );

    } catch (err) {

      console.error(err);

    }
  };

  useEffect(() => {

    loadOrders();

  }, [page, searchTerm]);

  
  const handleUpdate = async () => {

    try {

      const res =
        await updateItem(
          "order",
          editItem
        );

      if (res?.success === false) {

        setMessage(
          res.message
        );

        return;
      }

      setEditItem(null);

      setMessage(
        "Order updated successfully"
      );

      loadOrders();

    } catch (err) {

      console.error(err);

    }
  };

  const columns = [
  {
    key: "identifier",
    label: "Order No",
  },
  {
    key: "customerId",
    label: "Customer",
  },
  {
    key: "paymentMethod",
    label: "Payment",
  },
  {
    key: "totalPrice",
    label: "Total",
    render: (row) =>
      `₹${row.totalPrice || 0}`,
  },
  {
    key: "orderStatus",
    label: "Status",
  },
];

  const editFields = [
    {
      name: "identifier",
      label: "Order No",
      disabled: true,
    },
    {
      name: "customerId",
      label: "Customer",
      disabled: true,
    },
    {
      name: "paymentMethod",
      label: "Payment Method",
      disabled: true,
    },
    {
  name: "orderStatus",
  label: "Status",
  type: "select",
  options: [
    { value: "PENDING", label: "PENDING" },
    { value: "CONFIRMED", label: "CONFIRMED" },
    { value: "PACKED", label: "PACKED" },
    { value: "SHIPPED", label: "SHIPPED" },
    { value: "DELIVERED", label: "DELIVERED" },
    { value: "CANCELLED", label: "CANCELLED" },
  ],
},
  ];

  const actions = [
    {
      label: "Edit",
      onClick: async (row) => {

        const data =
          await getItem(
            "order",
            row.identifier
          );

        setEditItem(data);
      },
    },
  ];

  return (
    <>
    <CommonList
      title="Orders"
      data={orders}
      columns={columns}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      message={message}
      setMessage={setMessage}
      editItem={editItem}
      setEditItem={setEditItem}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No Orders Found"
      onRowClick={handleOrderClick}
    />
    {
  showOrderModal &&
  selectedOrder && (
    <div className="modalOverlay">
      <div className="billModal">

        <div className="modalHeader">
          <h3>
            Order #{selectedOrder.identifier}
          </h3>

          <button
            className="closeBtn"
            onClick={() =>
              setShowOrderModal(false)
            }
          >
            ✕
          </button>
        </div>

        <div className="billItemsSection">
          <h3>Ordered Items</h3>

          {orderItems.length === 0 ? (
            <div className="emptyCart">
              No Items Found
            </div>
          ) : (
            orderItems.map((item) => (
              <div
                key={item.identifier}
                className="billItemRow"
              >
                <div>
                  <strong>
                    {item.product}
                  </strong>

                  <div>
                    Qty: {item.quantity}
                  </div>

                  <div>
                    Unit Price: ₹
                    {item.unitPrice || 0}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        <div className="billTotals">

          <div>
            <span>Discount</span>
            <span>
              ₹
              {selectedOrder.discount || 0}
            </span>
          </div>

          <div className="grandTotal">
            <span>Total Price</span>
            <span>
              ₹
              {selectedOrder.totalPrice || 0}
            </span>
          </div>

        </div>

      </div>
    </div>
  )
}
</>
  );
} 