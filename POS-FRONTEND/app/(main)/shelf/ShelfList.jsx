"use client";

import { useEffect, useState } from "react";
import { FiArchive } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import ShelfRegistration from "./ShelfRegistration";
import ShelfEdit from "./ShelfEdit";

const ShelfList = () => {
  const [shelves, setShelves] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadShelves = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/shelf/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setShelves(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setShelves([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadShelves();
  }, [page, search]);

  const columns = [
    {
      header: "Shelf Name",
      key: "identifier",
    },
    {
      header: "Racks",
      key: "racks",
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
      title="Shelves"
      icon={FiArchive}
      columns={columns}
      data={shelves}
      loading={loading}
      searchTerm={search}
      setSearchTerm={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={ShelfRegistration}
      EditComponent={ShelfEdit}
      editPropName="shelf"
      enableStatusToggle={true}
      toggleStatusApi={(shelf) =>
        api.get("/api/shelf/toggleStatus", {
          params: {
            identifier: shelf.identifier,
          },
        })
      }
      setData={setShelves}
      deleteApi={(identifier) =>
        api.delete("/api/shelf/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadShelves}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      renderCustomCell={(key, item) => {
        if (key === "racks") {
          return Array.isArray(item.racks) ? item.racks.join(", ") : "";
        }
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

export default ShelfList;
