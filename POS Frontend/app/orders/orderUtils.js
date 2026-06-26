import api from "@/api/axios";

export async function fetchOrders({ setOrders, setOrdersLoading, setOrdersError }) {
    setOrdersLoading(true);
    setOrdersError("");
    try {
        const res = await api.post("/order/list", {
            page: 0,
            sizePerPage: 1000,
            sortDirection: "DESC",
            sortField: "id",
        });
        const data = res.data;
        if (Array.isArray(data)) {
            setOrders(data);
        } else if (data?.dtoList) {
            setOrders(data.dtoList);
        } else {
            setOrders([]);
        }
    } catch (err) {
        const status = err.response?.status;
        if (status === 401) {
            setOrdersError("Session expired. Please log in again.");
            localStorage.removeItem("token");
            globalThis.location.href = "/login";
        } else if (status === 403) {
            setOrdersError("Access denied. Your account cannot access order data.");
        } else {
            setOrdersError("Failed to load orders. Please check your connection and try again.");
        }
    } finally {
        setOrdersLoading(false);
    }
}
