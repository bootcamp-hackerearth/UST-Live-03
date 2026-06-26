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
      setSearchTerm={setSearch}
      onSearchChange={(value) => {
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
      filterFunction={(item, term) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase()) ||
        item.address?.toLowerCase().includes(term.toLowerCase()) ||
        item.country?.toLowerCase().includes(term.toLowerCase()) ||
        item.pincode?.toString().includes(term)
      }
      renderCustomCell={(key, item) => {
        switch (key) {
          case "status":
            return item.status ? (
              <span className="text-green-600 font-semibold">Active</span>
            ) : (
              <span className="text-red-600 font-semibold">Inactive</span>
            );

          default:
            return item[key];
        }
      }}
    />
  );
};

export default WarehouseList;
