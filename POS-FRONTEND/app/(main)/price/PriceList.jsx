"use client";

import { useState, useEffect } from "react";
import { FiDollarSign } from "react-icons/fi";

import CommonList from "@/component/CommonList";

import PriceRegistration from "./PriceRegistration";
import PriceEdit from "./PriceEdit";

import api from "../api/axios";

const PriceList = () => {

    const [prices, setPrices] = useState([]);
    
    const [loading, setLoading] = useState(true);

    const [search, setSearch] = useState("");

    const [page, setPage] = useState(0);
    const [sizePerPage] = useState(5);
    const [totalPage, setTotalPage] = useState(0);

    const priceColumns = [
        {
            header: "Product",
            key: "identifier"
        },
        {
            header: "MRP",
            key: "costPrice"
        },
        {
            header: "Selling Price",
            key: "sellingPrice"
        },
        {
            header: "Discount",
            key: "difference"
        }
    ];

    const fetchPrices = async () => {

        setLoading(true);

        try {

            const response = await api.post(
                "/api/price/list",
                {
                    page,
                    sizePerPage,
                    sortField: "identifier",
                    search,
                }
            );

            const data =
                response.data.dtoList || [];

            setPrices(data);

            setTotalPage(
                response.data.totalPage || 0
            );

        } catch (err) {

            console.error(err);

            setPrices([]);

        } finally {

            setLoading(false);

        }
    };

    useEffect(() => {
        fetchPrices();
    }, [page, search]);

    const renderCustomCell = (
        key,
        item
    ) => {

        switch (key) {
            case "costPrice":
            case "sellingPrice":
            case "difference":

                return (
                    <span>
                        ₹ {item[key]}
                    </span>
                );

            default:
                return item[key];
        }
    };

    return (
      <CommonList
        title="Price"
        icon={FiDollarSign}
        columns={priceColumns}
        data={prices}
        loading={loading}
        searchTerm={search}
        setSearchTerm={(value) => {
          setPage(0);
          setSearch(value);
        }}
        renderCustomCell={renderCustomCell}
        AddComponent={PriceRegistration}
        EditComponent={PriceEdit}
        editPropName="price"
        deleteApi={(identifier) =>
          api.delete("/api/price/delete", { params: { identifier } })
        }
        deleteIdentifierField="identifier"
        pagination={{
          page,
          totalPage,
          setPage,
        }}
        refreshData={fetchPrices}
      />
    );
};

export default PriceList;