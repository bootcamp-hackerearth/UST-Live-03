"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import OrderPage from "@/components/OrderPage";
import { fetchWithAuth } from "@/lib/api";

const PAGE_SIZE = 50;
const SEARCH_DEBOUNCE_MS = 400;

export default function OrderRoute() {
  const router = useRouter();
  const [orders, setOrders] = useState([]);
  const [totalRecords, setTotalRecords] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [pageErr, setPageErr] = useState("");
  const [pageOk, setPageOk] = useState("");

  const [searchInput, setSearchInput] = useState("");
  const [searchQuery, setSearchQuery] = useState("");

  const [viewOrder, setViewOrder] = useState(null);

  const [carts, setCarts] = useState([]);
  const [showPlaceOrder, setShowPlaceOrder] = useState(false);
  const [poCartId, setPoCartId] = useState("");
  const [poPayment, setPoPayment] = useState("CASH");
  const [poErr, setPoErr] = useState("");
  const [poSaving, setPoSaving] = useState(false);

  const [confirmDelete, setConfirmDelete] = useState(null);

  const receiptRef = useRef(null);
  const fetchSeqRef = useRef(0);

  const loadOrders = useCallback(async (pageNum, keyword) => {
    const seq = ++fetchSeqRef.current;
    setLoading(true);
    setPageErr("");
    try {
      const data = await fetchWithAuth("/api/orders/list", {
        method: "POST",
        body: JSON.stringify({
          page: pageNum,
          sizePerPage: PAGE_SIZE,
          sortDirection: "DESC",
          sortField: "id",
          keyword: keyword || "",
        }),
      });
      if (seq !== fetchSeqRef.current) return;
      const ordersList = Array.isArray(data) ? data : (data?.dtoList ?? []);
      setOrders(ordersList);
      setCurrentPage(pageNum);
      setTotalRecords(data?.totalRecords ?? ordersList.length);
      setTotalPages(Math.max(1, data?.totalPages ?? 1));
    } catch (e) {
      if (seq !== fetchSeqRef.current) return;
      if (e.message !== "Unauthorized") setPageErr(e.message || "Failed to load orders.");
    } finally {
      if (seq === fetchSeqRef.current) setLoading(false);
    }
  }, []);

  useEffect(() => {
    const t = setTimeout(() => setSearchQuery(searchInput.trim()), SEARCH_DEBOUNCE_MS);
    return () => clearTimeout(t);
  }, [searchInput]);

  useEffect(() => {
    setCurrentPage(0);
  }, [searchQuery]);

  useEffect(() => {
    loadOrders(currentPage, searchQuery);
  }, [loadOrders, currentPage, searchQuery]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) setCurrentPage(newPage);
  };

  const loadCarts = useCallback(async () => {
    try {
      const data = await fetchWithAuth("/api/cart/list", {
        method: "POST",
        body: JSON.stringify({ page: 0, sizePerPage: 200 }),
      });
      setCarts(Array.isArray(data) ? data : (data?.dtoList ?? []));
    } catch {
      setCarts([]);
    }
  }, []);

  useEffect(() => {
    loadCarts();
  }, [loadCarts]);

  const handlePlaceOrder = async () => {
    if (!poCartId.trim()) {
      setPoErr("Please select a cart.");
      return;
    }
    setPoSaving(true);
    setPoErr("");
    try {
      await fetchWithAuth("/api/orders/place", {
        method: "POST",
        body: JSON.stringify({
          cartIdentifier: poCartId,
          paymentMode: poPayment,
        }),
      });
      setShowPlaceOrder(false);
      setPoCartId("");
      setPoPayment("CASH");
      setPageOk("Order placed successfully.");
      if (currentPage === 0) {
        loadOrders(0, searchQuery);
      } else {
        setCurrentPage(0);
      }
      await loadCarts();
    } catch (e) {
      setPoErr(e.message || "Failed to place order.");
    } finally {
      setPoSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirmDelete) return;
    setBusy(true);
    setPageErr("");
    try {
      await fetchWithAuth(`/api/orders/delete/${confirmDelete.identifier}`, {
        method: "DELETE",
        body: JSON.stringify({}),
      });
      setConfirmDelete(null);
      setPageOk("Order cancelled.");
      if (orders.length === 1 && currentPage > 0) {
        setCurrentPage(currentPage - 1);
      } else {
        loadOrders(currentPage, searchQuery);
      }
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
          <title>Receipt</title>
          <style>
            *{margin:0;padding:0;box-sizing:border-box}
            body{font-family:'Barlow',-apple-system,sans-serif;background:#fff;padding:20px}
            .no-print{display:none!important}
            @media print{body{padding:0}}
            p{margin:0}
          </style>
        </head>
        <body>
          <div style="max-width:500px;margin:0 auto;font-size:14px">${receiptHTML}</div>
          <script>window.onload=function(){window.print()}</script>
        </body>
      </html>
    `;
    const blob = new Blob([htmlContent], { type: "text/html;charset=UTF-8" });
    const url = URL.createObjectURL(blob);
    const newWindow = globalThis.window?.open(url, "_blank");
    if (newWindow) {
      newWindow.addEventListener("load", () => newWindow.print(), { once: true });
    }
  };

  const handleGoToCart = () => router.push("/cart");

  return (
    <OrderPage
      orders={orders}
      totalRecords={totalRecords}
      pageSize={PAGE_SIZE}
      loading={loading}
      busy={busy}
      pageErr={pageErr}
      pageOk={pageOk}
      search={searchInput}
      setSearch={setSearchInput}
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