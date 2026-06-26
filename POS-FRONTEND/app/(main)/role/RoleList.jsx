"use client";

import { useState, useEffect } from "react";
import { FiShield } from "react-icons/fi";

import CommonList from "@/component/CommonList";

import RoleRegistration from "./RoleRegistration";
import RoleEdit from "./RoleEdit";

import api from "../api/axios";

const RoleList = () => {
  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [sizePerPage] = useState(5);
  const [totalPage, setTotalPage] = useState(0);

  const [search, setSearch] = useState("");

  const roleColumns = [
    {
      header: "Role Name",
      key: "identifier",
    },
    {
      header: "Description",
      key: "description",
    },
  ];

  const fetchRoles = async () => {
    setLoading(true);

    try {
      const response = await api.post("/api/role/list", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      const data = response.data.dtoList || [];

      setRoles(data);

      setTotalPage(response.data.totalPage || 0);
    } catch (err) {
      console.error(err);

      setRoles([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, [page, search]);

  const renderCustomCell = (key, item) => item[key];

  return (
      <CommonList
        title="Roles"
        icon={FiShield}
        columns={roleColumns}
        data={roles}
        loading={loading}
        searchTerm={search}
        setSearchTerm={setSearch}
        onSearchChange={(value) => {
          setPage(0);
          setSearch(value);
        }}
        renderCustomCell={renderCustomCell}
        AddComponent={RoleRegistration}
        EditComponent={RoleEdit}
        editPropName="role"
        deleteApi={(identifier) =>
          api.delete("/api/role/delete", {
            params: { identifier },
          })
        }
        deleteIdentifierField="identifier"
        filterFunction={(role, searchTerm) =>
          role.identifier?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          role.description?.toLowerCase().includes(searchTerm.toLowerCase())
        }
        pagination={{
          page,
          totalPage,
          setPage,
        }}
        refreshData={fetchRoles}
      />
  );
};

export default RoleList;