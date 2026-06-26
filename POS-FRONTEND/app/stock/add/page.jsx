'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddStock = ({ closeModal, refreshData }) => {

    const [products, setProducts] = useState([]);
    const [warehouses, setWarehouses] = useState([]);

    const [stock, setStock] = useState({
        product: '',
        warehouse: '',
        quantity: '',
        expiryDate: '',
        stockStatus: true,
        status: true
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        fetchProducts();
        fetchWarehouses();
    }, []);

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

        setErrors({
            ...errors,
            [name]: ''
        });
    };

    const validate = () => {

        const newErrors = {};

        if (!stock.product) {
            newErrors.product = 'Product is required';
        }

        if (!stock.warehouse) {
            newErrors.warehouse = 'Warehouse is required';
        }

        if (!stock.quantity) {
            newErrors.quantity = 'Quantity is required';
        }

        if (!stock.expiryDate) {
            newErrors.expiryDate = 'Expiry Date is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        try {

            const payload = {
                identifier: `${stock.product}_${stock.warehouse}`,
                product: stock.product,
                warehouse: stock.warehouse,
                quantity: Number(stock.quantity),
                expiryDate: `${stock.expiryDate}T00:00:00`,
                stockStatus: true,
                status: true
            };

            await api.post('/stock/add', payload);

            alert('Stock Added');

            refreshData();
            closeModal();

        } catch (err) {

            console.error(err);

            alert(
                err.response?.data?.message ||
                'Failed to add stock'
            );
        }
    };

    return (
        <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden">

            <div className="px-8 py-6 border-b bg-slate-50">
                <h2 className="text-2xl font-bold text-slate-800">
                    Add Stock
                </h2>
            </div>

            <form
                onSubmit={handleSubmit}
                className="p-8 space-y-5"
            >

                <div>
                    <select
                        name="product"
                        value={stock.product}
                        onChange={handleChange}
                        className="w-full h-12 px-4 rounded-xl border"
                    >
                        <option value="">
                            Select Product
                        </option>

                        {products.map((product) => (
                            <option
                                key={product.identifier}
                                value={product.identifier}
                            >
                                {product.identifier} - {product.name}
                            </option>
                        ))}
                    </select>

                    {errors.product &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.product}
                        </p>
                    }
                </div>

                <div>
                    <select
                        name="warehouse"
                        value={stock.warehouse}
                        onChange={handleChange}
                        className="w-full h-12 px-4 rounded-xl border"
                    >
                        <option value="">
                            Select Warehouse
                        </option>

                        {warehouses.map((warehouse) => (
                            <option
                                key={warehouse.identifier}
                                value={warehouse.identifier}
                            >
                                {warehouse.identifier}
                            </option>
                        ))}
                    </select>

                    {errors.warehouse &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.warehouse}
                        </p>
                    }
                </div>

                <div>
                    <input
                        type="number"
                        name="quantity"
                        value={stock.quantity}
                        onChange={handleChange}
                        placeholder="Quantity"
                        className="w-full h-12 px-4 rounded-xl border"
                    />

                    {errors.quantity &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.quantity}
                        </p>
                    }
                </div>

                <div>
                    <input
                        type="date"
                        name="expiryDate"
                        value={stock.expiryDate}
                        onChange={handleChange}
                        className="w-full h-12 px-4 rounded-xl border"
                    />

                    {errors.expiryDate &&
                        <p className="text-red-500 text-xs mt-1">
                            {errors.expiryDate}
                        </p>
                    }
                </div>

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
                        Save Stock
                    </button>

                </div>

            </form>

        </div>
    );
};

AddStock.propTypes = {
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default AddStock;