"use client";

import { useEffect, useState } from "react";
import { FiArchive } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import StockRegistration from "./StockRegistration";
import StockEdit from "./StockEdit";

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadStocks = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/stock/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setStocks(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setStocks([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStocks();
  }, [page, search]);

  const columns = [
    {
      header: "Stock Name",
      key: "identifier",
    },
    {
      header: "Warehouse",
      key: "warehouseName",
    },
    {
      header: "Quantity",
      key: "quantity",
    },
    {
      header: "Status",
      key: "status",
    },
  ];

  return (
    <CommonList
      title="Stock"
      icon={FiArchive}
      columns={columns}
      data={stocks}
      loading={loading}
      AddComponent={StockRegistration}
      EditComponent={StockEdit}
      editPropName="stock"
      searchTerm={search}
      setSearchTerm={setSearch}
      onSearchChange={(value) => {
        setPage(0);
        setSearch(value);
      }}
      enableStatusToggle={true}
      toggleStatusApi={(stock) =>
        api.get("/api/stock/toggleStatus", {
          params: {
            identifier: stock.identifier,
          },
        })
      }
      setData={setStocks}
      deleteApi={(identifier) =>
        api.delete("/api/stock/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadStocks}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      filterFunction={(item, term) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase()) ||
        item.warehouseName?.toLowerCase().includes(term.toLowerCase())
      }
      renderCustomCell={(key, item) => {
        if (key === "status") {
          return item.status ? (
            <span className="text-green-600 font-semibold">Active</span>
          ) : (
            <span className="text-red-600 font-semibold">Inactive</span>
          );
        }

        return item[key];
      }}
    />
  );
};

export default StockList;
