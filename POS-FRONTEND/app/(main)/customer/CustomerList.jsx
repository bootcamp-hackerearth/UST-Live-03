"use client";

import { useState, useEffect } from "react";
import { FiUsers } from "react-icons/fi";
import api from "../api/axios";

import CommonList from "@/component/CommonList";
import CustomerRegistration from "./CustomerRegistration";
import CustomerEdit from "./CustomerEdit";

const CustomerList = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  const [search, setSearch] = useState("");

  const [page, setPage] = useState(0);

  const [pagination, setPagination] = useState({
    totalPage: 0,
  });

  const loadCustomers = async () => {
    try {
      setLoading(true);

      const response = await api.post("/api/customer/list", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        sortDirection: "ASC",
        search,
      });

      setCustomers(response.data.dtoList || []);

      setPagination({
        totalPage: response.data.totalPage || 0,
      });
    } catch (err) {
      console.error(err);
      setCustomers([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, [page, search]);

  const columns = [
    {
      header: "Customer Name",
      key: "identifier",
    },
    {
      header: "Email",
      key: "email",
    },
    {
      header: "Phone",
      key: "phoneno",
    },
    {
      header: "Status",
      key: "status",
    },
  ];

  return (
    <CommonList
      title="Customers"
      icon={FiUsers}
      columns={columns}
      data={customers}
      loading={loading}
      searchTerm={search}
      setSearchTerm={setSearch}
      onSearchChange={(value) => {
        setPage(0);
        setSearch(value);
      }}
      AddComponent={CustomerRegistration}
      EditComponent={CustomerEdit}
      editPropName="customer"
      deleteApi={(identifier) =>
        api.delete("/api/customer/delete", {
          params: { identifier },
        })
      }
      deleteIdentifierField="identifier"
      refreshData={loadCustomers}
      pagination={{
        page,
        setPage,
        totalPage: pagination.totalPage,
      }}
      filterFunction={(item, term) =>
        item.identifier?.toLowerCase().includes(term.toLowerCase()) ||
        item.email?.toLowerCase().includes(term.toLowerCase()) ||
        item.phone?.toLowerCase().includes(term.toLowerCase())
      }
      enableStatusToggle={true}
      toggleStatusApi={(customer) =>
        api.get("/api/customer/toggleStatus", {
          params: {
            email: customer.email,
          },
        })
      }
      setData={setCustomers}
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

export default CustomerList;
