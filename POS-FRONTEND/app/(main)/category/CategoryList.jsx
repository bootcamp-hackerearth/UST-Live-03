"use client";

import { useState, useEffect } from "react";
import { FiTag } from "react-icons/fi";

import CommonList from "@/component/CommonList";

import CategoryRegistration from "./CategoryRegistration";
import CategoryEdit from "./CategoryEdit";

import api from "../api/axios";

const CategoryList = () => {
  const [categories, setCategories] = useState([]);

  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState("");

  const [page, setPage] = useState(0);
  const [sizePerPage] = useState(5);
  const [totalPage, setTotalPage] = useState(0);

  const categoryColumns = [
    {
      header: "Category Name",
      key: "identifier",
    },
    {
      header: "Super Category",
      key: "superCategory",
    },
  ];

  const fetchCategories = async () => {
    setLoading(true);

    try {
      const response = await api.post("/api/category/list", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      const data = response.data.dtoList || [];

      setCategories(data);

      setTotalPage(response.data.totalPage || 0);
    } catch (err) {
      console.error(err);

      setCategories([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [page, search]);

  return (
    <CommonList
      title="Categories"
      icon={FiTag}
      columns={categoryColumns}
      data={categories}
      loading={loading}
      searchTerm={search}
      setSearchTerm={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={CategoryRegistration}
      EditComponent={CategoryEdit}
      editPropName="category"
      deleteApi={(identifier) =>
        api.delete(`/api/category/delete?identifier=${identifier}`)
      }
      deleteIdentifierField="identifier"
      refreshData={fetchCategories}
      pagination={{
        page,
        totalPage,
        setPage,
      }}
    />
  );
};

export default CategoryList;
