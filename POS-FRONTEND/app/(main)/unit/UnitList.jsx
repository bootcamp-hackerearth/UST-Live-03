"use client";

import { useEffect, useState } from "react";
import { FiPackage } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import UnitRegistration from "./UnitRegistration";
import UnitEdit from "./UnitEdit";

const UnitList = () => {
  const [units, setUnits] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadUnits = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/unit/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setUnits(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setUnits([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUnits();
  }, [page, search]);

  const columns = [
    {
      header: "Unit Name",
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
      title="Units"
      icon={FiPackage}
      columns={columns}
      data={units}
      loading={loading}
      searchTerm={search}
      setSearchTerm={setSearch}
      AddComponent={UnitRegistration}
      EditComponent={UnitEdit}
      editPropName="unit"
      onSearchChange={(value) => {
        setPage(0);
        setSearch(value);
      }}
      enableStatusToggle={true}
      toggleStatusApi={(unit) =>
        api.get("/api/unit/toggleStatus", {
          params: {
            identifier: unit.identifier,
          },
        })
      }
      setData={setUnits}
      deleteApi={(identifier) =>
        api.delete("/api/unit/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadUnits}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      filterFunction={(item, term) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase())
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

export default UnitList;
