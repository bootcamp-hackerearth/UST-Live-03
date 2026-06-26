"use client";

import { useState, useEffect } from "react";
import { FiUsers } from "react-icons/fi";
import CommonList from "@/component/CommonList";
import UserRegistration from "./UserRegistration";
import UserEdit from "./UserEdit";
import api from "../api/axios";

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [search, setSearch] = useState("");
    const [sizePerPage] = useState(5);
    const [totalPage, setTotalPage] = useState(0);
    const userColumns =
    [
        {
            header: "Name",
            key: "name"
        },
        {
            header: "Phone Number",
            key: "phoneNo"
        },
        {
            header: "Role",
            key: "roles"
        },
        {
            header: "Username",
            key: "username"
        }
    ];

    const fetchUsers = async () => {
        setLoading(true);
        try{
            const response = await api.post("/api/user/list",
            {
                page,
                sizePerPage,
                sortField: "name",
                search,
            });
            const data = response.data.dtoList || [];
            setUsers(data);
            setTotalPage(response.data.totalPage || 0);
        }
        catch(err)
        {
            console.error(err);
            setUsers([]);
        }
        finally
        {
            setLoading(false);
        }
    };

    useEffect(() => { fetchUsers(); }, [page, search]);

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
          title="Users"
          icon={FiUsers}
          columns={userColumns}
          data={users}
          loading={loading}
          searchTerm={search}
          setSearchTerm={setSearch}
          onSearchChange={(value) => {
            setSearch(value);
            setPage(0);
          }}
          renderCustomCell={renderCustomCell}
          AddComponent={UserRegistration}
          EditComponent={UserEdit}
          editPropName="user"
          deleteApi={(username) =>
            api.delete("/api/user/delete", { params: { username } })
          }
          deleteIdentifierField="username"
          filterFunction={(user, searchTerm) =>
            user.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.roles
              ?.join(",")
              .toLowerCase()
              .includes(searchTerm.toLowerCase())
          }
          pagination={{
            page,
            totalPage,
            setPage,
          }}
          refreshData={fetchUsers}
          enableStatusToggle={true}
          setData={setUsers}
          toggleStatusApi={(payload) => api.post("/api/user/update", payload)}
        />
    );
};
export default UserList;