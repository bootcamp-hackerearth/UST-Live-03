"use client";

import { useState, useEffect } from "react";
import { FiGrid } from "react-icons/fi";

import CommonList from "@/component/CommonList";

import NodeRegistration from "./NodeRegistration";
import NodeEdit from "./NodeEdit";

import api from "../api/axios";

const NodeList = () => {

    const [nodes, setNodes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");

    const [page, setPage] = useState(0);
    const [sizePerPage] = useState(5);
    const [totalPage, setTotalPage] = useState(0);

    
    const nodeColumns = [
        {
            header: "Identifier",
            key: "identifier"
        },
        {
            header: "Path",
            key: "path"
        },
        {
            header: "Roles",
            key: "roles"
        }
    ];

    const fetchNodes = async () => {

        setLoading(true);

        try {

            const response = await api.post(
                "/api/node/list",
                {
                    page,
                    sizePerPage,
                    sortField: "identifier",
                    search,
                }
            );
            console.log(response.data);

            const dat = response.data.dtoList || [];
            setNodes(dat);
            setTotalPage(response.data.totalPage || 0);

        }
        catch (err) {

            console.error(err);

            setNodes([]);

        }
        finally {

            setLoading(false);

        }
    };

    useEffect(() => {
        fetchNodes();
    }, [page, search]);


    const renderCustomCell = (key, item) => {
      if (key === "roles") {
        return (
          <span className="px-2 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-semibold">
            {item.roles?.join(", ")}
          </span>
        );
      }

      return item[key];
    };

    return (
        <CommonList
          title="Nodes"
          icon={FiGrid}
          columns={nodeColumns}
          data={nodes}
          loading={loading}
          searchTerm={search}
          setSearchTerm={setSearch}
          onSearchChange={(value) => {
            setPage(0);
            setSearch(value);
          }}
          renderCustomCell={renderCustomCell}
          AddComponent={NodeRegistration}
          EditComponent={NodeEdit}
          editPropName="node"
          deleteApi={async (identifier) =>{
            const response = await api.delete("/api/node/delete", {
                params: { identifier },
            });
              globalThis.dispatchEvent(new Event("refreshSidebar"));
              return response;}
          }
          deleteIdentifierField="identifier"
          filterFunction={(node, searchTerm) =>
            node.identifier?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            node.path?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            node.roles
              ?.join(", ")
              .toLowerCase()
              .includes(searchTerm.toLowerCase())
          }
          pagination={{
            page,
            totalPage,
            setPage,
          }}
          refreshData={fetchNodes}
        />
    );
};

export default NodeList;