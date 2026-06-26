'use client';

import { useState, useEffect } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateStock = ({
    data,
    closeModal,
    refreshData
}) => {

    const [stock, setStock] = useState(null);
    const [products, setProducts] = useState([]);
    const [warehouses, setWarehouses] = useState([]);

    useEffect(() => {

        fetchStock();
        fetchProducts();
        fetchWarehouses();

    }, [data]);

    const fetchStock = async () => {

        try {

            const response = await api.get(
                `/stock/get?identifier=${data.identifier}`
            );

            const stockData = response.data;

            setStock({
                ...stockData,
                expiryDate: stockData.expiryDate
                    ? stockData.expiryDate.split('T')[0]
                    : ''
            });

        } catch (err) {

            console.error(err);
        }
    };

    const fetchProducts = async () => {

        try {

            const response = await api.post('/product/list', {
                page: 0,
                sizePerPage: 100,
                sortField: 'identifier',
                sortDirection: 'ASC'
            });

            setProducts(response.data.dtoList || []);

        } catch (err) {

            console.error(err);
        }
    };

    const fetchWarehouses = async () => {

        try {

            const response = await api.post('/warehouse/list', {
                page: 0,
                sizePerPage: 100,
                sortField: 'identifier',
                sortDirection: 'ASC'
            });

            setWarehouses(response.data.dtoList || []);

        } catch (err) {

            console.error(err);
        }
    };

    const handleChange = (e) => {

        const { name, value } = e.target;

        setStock({
            ...stock,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const payload = {
                ...stock,
                quantity: Number(stock.quantity),
                expiryDate: `${stock.expiryDate}T00:00:00`
            };

            await api.put(
                '/stock/update',
                payload
            );

            alert('Stock Updated');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Update Failed'
            );
        }
    };

    if (!stock) return null;

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Update Stock
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <input
                    value={stock.identifier || ''}
                    readOnly
                    className="w-full h-12 px-4 rounded-xl border bg-slate-100"
                />

                <select
                    name="product"
                    value={stock.product || ''}
                    onChange={handleChange}
                    className="w-full h-12 px-4 rounded-xl border"
                >
                    {products.map((product) => (
                        <option
                            key={product.identifier}
                            value={product.identifier}
                        >
                            {product.identifier} - {product.name}
                        </option>
                    ))}
                </select>

                <select
                    name="warehouse"
                    value={stock.warehouse || ''}
                    onChange={handleChange}
                    className="w-full h-12 px-4 rounded-xl border"
                >
                    {warehouses.map((warehouse) => (
                        <option
                            key={warehouse.identifier}
                            value={warehouse.identifier}
                        >
                            {warehouse.identifier}
                        </option>
                    ))}
                </select>

                <input
                    type="number"
                    name="quantity"
                    value={stock.quantity || ''}
                    onChange={handleChange}
                    placeholder="Quantity"
                    className="w-full h-12 px-4 rounded-xl border"
                />

                <input
                    type="date"
                    name="expiryDate"
                    value={stock.expiryDate || ''}
                    onChange={handleChange}
                    className="w-full h-12 px-4 rounded-xl border"
                />

                <select
                    name="stockStatus"
                    value={stock.stockStatus}
                    onChange={(e) =>
                        setStock({
                            ...stock,
                            stockStatus:
                                e.target.value === 'true'
                        })
                    }
                    className="w-full h-12 px-4 rounded-xl border"
                >
                    <option value="true">
                        In Stock
                    </option>

                    <option value="false">
                        Out Of Stock
                    </option>
                </select>

                <select
                    name="status"
                    value={stock.status}
                    onChange={(e) =>
                        setStock({
                            ...stock,
                            status:
                                e.target.value === 'true'
                        })
                    }
                    className="w-full h-12 px-4 rounded-xl border"
                >
                    <option value="true">
                        Active
                    </option>

                    <option value="false">
                        Inactive
                    </option>
                </select>

                <div className="flex justify-end gap-3">

                    <button
                        type="button"
                        onClick={closeModal}
                        className="px-5 py-2 border rounded-xl"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="px-5 py-2 rounded-xl bg-cyan-500 text-white"
                    >
                        Update Stock
                    </button>

                </div>

            </form>

        </div>
    );
};

UpdateStock.propTypes = {
    data: PropTypes.shape({
        identifier: PropTypes.string
    }).isRequired,
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default UpdateStock;