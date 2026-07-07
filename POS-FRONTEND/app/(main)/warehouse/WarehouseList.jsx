"use client";

import { useEffect, useState } from "react";
import { FiHome } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import WarehouseRegistration from "./WarehouseRegistration";
import WarehouseEdit from "./WarehouseEdit";

const WarehouseList = () => {
  const [warehouses, setWarehouses] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadWarehouses = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/warehouse/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setWarehouses(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setWarehouses([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadWarehouses();
  }, [page, search]);

  const columns = [
    {
      header: "Warehouse",
      key: "identifier",
    },
    {
      header: "Address",
      key: "address",
    },
    {
      header: "Country",
      key: "country",
    },
    {
      header: "Pincode",
      key: "pincode",
    },
    {
      header: "Shelves",
      key: "shelves",
    },
    {
      header: "Status",
      key: "status",
    },
  ];

  return (
    <CommonList
      title="Warehouses"
      icon={FiHome}
      columns={columns}
      data={warehouses}
      loading={loading}
      searchTerm={search}
      setSearchTerm={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={WarehouseRegistration}
      EditComponent={WarehouseEdit}
      editPropName="warehouse"
      enableStatusToggle={true}
      toggleStatusApi={(warehouse) =>
        api.get("/api/warehouse/toggleStatus", {
          params: {
            identifier: warehouse.identifier,
          },
        })
      }
      setData={setWarehouses}
      deleteApi={(identifier) =>
        api.delete("/api/warehouse/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadWarehouses}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      renderCustomCell={(key, item) => {
        switch (key) {
          case "status":
            return item.status ? (
              <span className="text-green-600 font-semibold">Active</span>
            ) : (
              <span className="text-red-600 font-semibold">Inactive</span>
            );
          case "shelves":
            return (
              <div className="flex flex-wrap gap-1">
                {item.shelves?.map((shelf) => (
                  <span
                    key={shelf}
                    className="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs"
                  >
                    {shelf}
                  </span>
                ))}
              </div>
            );
          default:
            return item[key];
        }
      }}
    />
  );
};

export default WarehouseList;
