'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const UpdateCustomer = ({ data, closeModal, refreshData }) => {

    const [customer, setCustomer] = useState(data);

    useEffect(() => {

        const fetchCustomer = async () => {

            try {

                const response = await api.get(
                    `/customer/get?identifier=${data.identifier}`
                );

                console.log('Customer Data:', response.data);

                setCustomer(response.data);

            } catch (err) {

                console.error(err);

            }
        };

        if (data?.identifier) {
            fetchCustomer();
        }

    }, [data]);

    const handleChange = (e) => {
        const { name, value } = e.target;

        setCustomer({
            ...customer,
            [name]: value
        });
    };

    const handleAddressChange = (type, field, value) => {
        setCustomer({
            ...customer,
            [type]: {
                ...customer[type],
                [field]: value
            }
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            await api.put('/customer/update', customer);

            alert('Customer Updated');
            refreshData();
            closeModal();

        } catch (err) {
            console.error(err);
            alert('Update Failed');
        }
    };

    if (!customer) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">

            <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden flex flex-col w-full max-w-2xl mx-4">

                <div className="px-8 py-6 bg-slate-50 border-b border-slate-200">
                    <h2 className="text-2xl font-bold text-slate-800">
                        Update Customer
                    </h2>

                    <p className="text-xs text-slate-500 mt-1">
                        Edit customer details and billing and shipping addresses
                    </p>
                </div>

    
                <form
                    onSubmit={handleSubmit}
                    className="flex-1 overflow-y-auto px-8 py-6 space-y-6"
                    style={{ maxHeight: '65vh' }}
                >

                    <div className="grid grid-cols-2 gap-3">

                        <div>
                            <label 
                                htmlFor="identifier"
                                className="block text-xs text-slate-500 mb-1.5">Customer ID</label>
                            <input
                                value={customer.identifier || ''}
                                readOnly
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-100 text-sm text-slate-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="phoneNo"
                                className="block text-xs text-slate-500 mb-1.5">Phone Number</label>
                            <input
                                value={customer.phoneNo || ''}
                                readOnly
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-100 text-sm text-slate-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="name"
                                className="block text-xs text-slate-500 mb-1.5">Customer Name</label>
                            <input
                                name="name"
                                value={customer.name || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="email"
                                className="block text-xs text-slate-500 mb-1.5">Email</label>
                            <input
                                name="email"
                                value={customer.email || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="partyType"
                                className="block text-xs text-slate-500 mb-1.5">Party Type</label>
                            <select
                                name="partyType"
                                value={customer.partyType || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            >
                                <option value="Customer">Customer</option>
                                <option value="Dealer">Dealer</option>
                                <option value="Wholesaler">Wholesaler</option>
                            </select>
                        </div>

                        <div>
                            <label 
                                htmlFor="balance"
                                className="block text-xs text-slate-500 mb-1.5">Balance</label>
                            <input
                                name="balance"
                                value={customer.balance || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="balanceType"
                                className="block text-xs text-slate-500 mb-1.5">Balance Type</label>
                            <select
                                name="balanceType"
                                value={customer.balanceType || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            >
                                <option value="Due">Due</option>
                                <option value="Advance">Advance</option>
                            </select>
                        </div>

                        <div>
                            <label 
                                htmlFor="creditlimit"
                                className="block text-xs text-slate-500 mb-1.5">Credit Limit</label>
                            <input
                                name="creditLimit"
                                value={customer.creditLimit || ''}
                                onChange={handleChange}
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            />
                        </div>

                        <div>
                            <label 
                                htmlFor="status"
                                className="block text-xs text-slate-500 mb-1.5">Status</label>
                            <select
                                name="status"
                                value={customer.status}
                                onChange={(e) =>
                                    setCustomer({
                                        ...customer,
                                        status: e.target.value === 'true'
                                    })
                                }
                                className="w-full h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                            >
                                <option value="true">Active</option>
                                <option value="false">Inactive</option>
                            </select>
                        </div>

                    </div>

                 
                    <div className="border border-slate-200 rounded-2xl p-5 bg-slate-50">
                        <p className="text-base font-semibold text-slate-800 mb-4">
                            Billing Address
                        </p>

                        <div className="grid grid-cols-2 gap-2">
                            {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
                                <input
                                    key={field}
                                    value={customer.billingAddress?.[field] || ''}
                                    onChange={(e) =>
                                        handleAddressChange('billingAddress', field, e.target.value)
                                    }
                                    placeholder={field}
                                    className="h-12 px-4 rounded-xl border border-slate-300 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                                />
                            ))}
                        </div>
                    </div>

       
                    <div className="border border-slate-200 rounded-2xl p-5 bg-slate-50">
                        <p className="text-base font-semibold text-slate-800 mb-4">
                            Shipping Address
                        </p>

                        <div className="grid grid-cols-2 gap-2">
                            {['addressLine', 'city', 'state', 'zip', 'country'].map((field) => (
                                <input
                                    key={field}
                                    value={customer.shippingAddress?.[field] || ''}
                                    onChange={(e) =>
                                        handleAddressChange('shippingAddress', field, e.target.value)
                                    }
                                    placeholder={field}
                                    className="h-12 px-4 rounded-xl border border-slate-300 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500"
                                />
                            ))}
                        </div>
                    </div>

                </form>

     
                <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3">
                    <button
                        type="button"
                        onClick={closeModal}
                        className="px-6 py-2.5 rounded-xl border text-slate-700 font-medium hover:bg-slate-100 transition"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        onClick={handleSubmit}
                        className="px-4 py-2 text-sm rounded-lg bg-cyan-500 text-white hover:bg-cyan-600 font-medium transition"
                    >
                        Update Customer
                    </button>
                </div>

            </div>
        </div>
    );
};

UpdateCustomer.propTypes = {
    data: PropTypes.shape({
        identifier: PropTypes.string,
        name: PropTypes.string,
        phoneNo: PropTypes.string,
        email: PropTypes.string,
        partyType: PropTypes.string,
        balance: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        balanceType: PropTypes.string,
        creditLimit: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),
        status: PropTypes.bool,
        billingAddress: PropTypes.object,
        shippingAddress: PropTypes.object
    }),
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired
};

export default UpdateCustomer;