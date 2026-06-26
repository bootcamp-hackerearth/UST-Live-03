"use client";

import { useEffect, useState } from "react";
import { FiArchive } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import RackRegistration from "./RackRegistration";
import RackEdit from "./RackEdit";

const RackList = () => {
  const [racks, setRacks] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadRacks = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/racks/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setRacks(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setRacks([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRacks();
  }, [page, search]);

  const columns = [
    {
      header: "Rack Name",
      key: "identifier",
    },
    {
      header: "Shelf",
      key: "shelfs",
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
      title="Racks"
      icon={FiArchive}
      columns={columns}
      data={racks}
      loading={loading}
      searchTerm={search}
      setSearchTerm={setSearch}
      onSearchChange={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={RackRegistration}
      EditComponent={RackEdit}
      editPropName="rack"
      enableStatusToggle={true}
      toggleStatusApi={(rack) =>
        api.get("/api/racks/toggleStatus", {
          params: {
            identifier: rack.identifier,
          },
        })
      }
      setData={setRacks}
      deleteApi={(identifier) =>
        api.delete("/api/racks/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadRacks}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      filterFunction={(item, term) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase()) ||
        item.shelfs?.toLowerCase().includes(term.toLowerCase())
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

export default RackList;
