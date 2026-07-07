"use client";

import { useEffect, useState } from "react";
import { FiTag } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import BrandRegistration from "./BrandRegistration";
import BrandEdit from "./BrandEdit";

const BrandList = () => {
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadBrands = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/brand/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setBrands(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage,
      });
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBrands();
  }, [page, search]);

  const columns = [
    {
      header: "Identifier",
      key: "identifier",
    },
    {
      header: "Description",
      key: "description",
    },
    {
      header: "Status",
      key: "status",
    },
  ];

  return (
    <CommonList
      title="Brands"
      icon={FiTag}
      columns={columns}
      data={brands}
      loading={loading}
      searchTerm={search}
      setSearchTerm={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={BrandRegistration}
      EditComponent={BrandEdit}
      editPropName="brand"
      enableStatusToggle={true}
      toggleStatusApi={(brand) =>
        api.get("/api/brand/toggleStatus", {
          params: { identifier: brand.identifier },
        })
      }
      setData={setBrands}
      deleteApi={(identifier) =>
        api.delete("/api/brand/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadBrands}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
    />
  );
};

export default BrandList;
