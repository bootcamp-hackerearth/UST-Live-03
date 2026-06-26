"use client";

import { useEffect, useState } from "react";
import { FiBox } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import ModelProductRegistration from "./ModelProductRegistration";
import ModelProductEdit from "./ModelProductEdit";

const ModelProductList = () => {
  const [models, setModels] = useState([]);
  const [loading, setLoading] = useState(false);

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadModels = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/model/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setModels(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setModels([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadModels();
  }, [page, search]);

  const columns = [
    {
      header: "Model Name",
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
      title="Models"
      icon={FiBox}
      columns={columns}
      data={models}
      loading={loading}
      searchTerm={search}
      setSearchTerm={setSearch}
      onSearchChange={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={ModelProductRegistration}
      EditComponent={ModelProductEdit}
      editPropName="model"
      enableStatusToggle={true}
      toggleStatusApi={(model) =>
        api.get("/api/model/toggleStatus", {
          params: {
            identifier: model.identifier,
          },
        })
      }
      setData={setModels}
      deleteApi={(identifier) =>
        api.delete("/api/model/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadModels}
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

export default ModelProductList;
