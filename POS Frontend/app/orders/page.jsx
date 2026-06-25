"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import OrderPage from "@/components/OrderPage";
import { fetchWithAuth } from "@/lib/api";

const PB = JSON.stringify({
  page: 0,
  sizePerPage: 50,
  sortDirection: "DESC",
  sortField: "id",
});

export default function OrderRoute() {
  const router = useRouter();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [pageErr, setPageErr] = useState("");
  const [pageOk, setPageOk] = useState("");
  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [viewOrder, setViewOrder] = useState(null);

  const [carts, setCarts] = useState([]);
  const [showPlaceOrder, setShowPlaceOrder] = useState(false);
  const [poCartId, setPoCartId] = useState("");
  const [poPayment, setPoPayment] = useState("CASH");
  const [poErr, setPoErr] = useState("");
  const [poSaving, setPoSaving] = useState(false);

  const [confirmDelete, setConfirmDelete] = useState(null);

  const receiptRef = useRef(null);

  const loadOrders = useCallback(async (pageNum = 0) => {
    setLoading(true);
    try {
      const body = JSON.stringify({
        page: pageNum,
        sizePerPage: 50,
        sortDirection: "DESC",
        sortField: "id",
      });
      const data = await fetchWithAuth("/api/orders/list", { method: "POST", body });
      const ordersList = Array.isArray(data) ? data : data?.dtoList ?? [];
      setOrders(ordersList);
      setCurrentPage(pageNum);
      if (data && typeof data === "object" && data.totalRecords !== undefined) {
        setTotalPages(Math.ceil(data.totalRecords / 50));
      } else if (ordersList.length < 50) {
        setTotalPages(pageNum + 1);
      }
    } catch (e) {
      setPageErr(e.message || "Failed to load orders.");
    } finally {
      setLoading(false);
    }
  }, []);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      loadOrders(newPage);
    }
  };

  const loadCarts = useCallback(async () => {
    try {
      const data = await fetchWithAuth("/api/cart/list", { method: "POST", body: PB });
      setCarts(Array.isArray(data) ? data : data?.dtoList ?? []);
    } catch {
      setCarts([]);
    }
  }, []);

  useEffect(() => {
    loadOrders();
    loadCarts();
  }, [loadOrders, loadCarts]);

  const handlePlaceOrder = async () => {
    if (!poCartId.trim()) { setPoErr("Please select a cart."); return; }
    setPoSaving(true); setPoErr("");
    try {
      await fetchWithAuth("/api/orders/place", {
        method: "POST",
        body: JSON.stringify({ cartIdentifier: poCartId, paymentMode: poPayment }),
      });
      setShowPlaceOrder(false);
      setPoCartId("");
      setPoPayment("CASH");
      setPageOk("Order placed successfully.");
      await loadOrders();
      await loadCarts();
    } catch (e) {
      setPoErr(e.message || "Failed to place order.");
    } finally {
      setPoSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    setBusy(true); setPageErr("");
    try {
      await fetchWithAuth(`/api/orders/delete/${confirmDelete.identifier}`, {
        method: "DELETE",
        body: JSON.stringify({}),
      });
      setConfirmDelete(null);
      setPageOk("Order cancelled.");
      await loadOrders();
    } catch (e) {
      setPageErr(e.message || "Failed to cancel order.");
    } finally {
      setBusy(false);
    }
  };

  const handlePrintReceipt = () => {
    if (!receiptRef.current) return;
    const receiptHTML = receiptRef.current.innerHTML;

    const htmlContent = `
      <!DOCTYPE html>
      <html>
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Receipt</title>
          <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { 
              font-family: 'Barlow', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
              background: #fff;
              padding: 20px;
            }
            .no-print { display: none !important; }
            @media print {
              body { padding: 0; }
            }
            p { margin: 0; }
          </style>
        </head>
        <body>
          <div style="max-width: 500px; margin: 0 auto; font-size: 14px;">
            ${receiptHTML}
          </div>
          <script>
            window.onload = function() {
              window.print();
            };
          </script>
        </body>
      </html>
    `;
    const blob = new Blob([htmlContent], { type: 'text/html;charset=UTF-8' });
    const url = URL.createObjectURL(blob);
    const newWindow = window.open(url, '_blank');
    if (newWindow) {
      newWindow.addEventListener('load', () => {
        newWindow.print();
      }, { once: true });
    }
  };

  const handleGoToCart = () => router.push("/cart");

  const q = search.trim().toLowerCase();
  const filteredOrders = q
    ? orders.filter(
        (o) =>
          o.orderId?.toLowerCase().includes(q) ||
          o.customer?.customerName?.toLowerCase().includes(q) ||
          o.customerIdentifier?.toLowerCase().includes(q) ||
          o.paymentMode?.toLowerCase().includes(q)
      )
    : orders;

  return (
    <OrderPage
      orders={filteredOrders}
      allOrders={orders}
      loading={loading}
      busy={busy}
      pageErr={pageErr}
      pageOk={pageOk}
      search={search}
      setSearch={setSearch}
      viewOrder={viewOrder}
      setViewOrder={setViewOrder}
      receiptRef={receiptRef}
      onPrintReceipt={handlePrintReceipt}
      carts={carts}
      showPlaceOrder={showPlaceOrder}
      setShowPlaceOrder={setShowPlaceOrder}
      poCartId={poCartId}
      setPoCartId={setPoCartId}
      poPayment={poPayment}
      setPoPayment={setPoPayment}
      poErr={poErr}
      poSaving={poSaving}
      onPlaceOrder={handlePlaceOrder}
      confirmDelete={confirmDelete}
      setConfirmDelete={setConfirmDelete}
      onDelete={handleDelete}
      onGoToCart={handleGoToCart}
      currentPage={currentPage}
      totalPages={totalPages}
      onPageChange={handlePageChange}
    />
  );
}