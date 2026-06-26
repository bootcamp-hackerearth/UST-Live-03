'use client';

import { useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const AddCustomer = ({ closeModal, refreshData }) => {

    const [customer, setCustomer] = useState({
        name: '',
        phoneNo: '',
        email: '',
        partyType: '',
        balance: '',
        balanceType: 'Due',
        creditLimit: '',
        status: true,
        billingAddress: {
            addressLine: '',
            city: '',
            state: '',
            zip: '',
            country: ''
        },
        shippingAddress: {
            addressLine: '',
            city: '',
            state: '',
            zip: '',
            country: ''
        }
    });

    const [sameAsBilling, setSameAsBilling] = useState(false);
    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCustomer({ ...customer, [name]: value });
        setErrors((prev) => ({ ...prev, [name]: '' }));
    };

    const handleAddressChange = (type, field, value) => {

        if (field === 'zip') {
            if (value !== '' && !/^\d{0,6}$/.test(value)) return;
        }
        if (field === 'state' || field === 'city') {
            if (value !== '' && !/^[A-Za-z ]*$/.test(value)) return;
        }

        setCustomer((prev) => {
            const updated = {
                ...prev,
                [type]: { ...prev[type], [field]: value }
            };
            if (sameAsBilling && type === 'billingAddress') {
                updated.shippingAddress = { ...updated.billingAddress };
            }
            return updated;
        });

        setErrors((prev) => ({ ...prev, [`${type}_${field}`]: '' }));
    };

    const handleSameAsBilling = (checked) => {
        setSameAsBilling(checked);
        if (checked) {
            setCustomer((prev) => ({
                ...prev,
                shippingAddress: { ...prev.billingAddress }
            }));
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!customer.name.trim()) {
            newErrors.name = 'Customer name is required';
        } else if (!/^[A-Za-z ]+$/.test(customer.name.trim())) {
            newErrors.name = 'Only letters and spaces are allowed';
        }

        if (!customer.phoneNo.trim()) {
            newErrors.phoneNo = 'Mobile number is required';
        } else if (!/^[6-9]\d{9}$/.test(customer.phoneNo)) {
            newErrors.phoneNo = 'Must start with 6–9 and be 10 digits';
        }

        if (!customer.email.trim()) {
            newErrors.email = 'Email is required';
        } else if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(customer.email)) {
            newErrors.email = 'Enter a valid email address';
        }

        if (!customer.partyType) {
            newErrors.partyType = 'Party type is required';
        }

        if (customer.balance === '') {
            newErrors.balance = 'Balance is required';
        } else if (Number.isNaN(customer.balance)) {
            newErrors.balance = 'Balance must be a number';
        } else if (Number(customer.balance) < 0) {
            newErrors.balance = 'Balance cannot be negative';
        }

    
        if (customer.creditLimit === '') {
            newErrors.creditLimit = 'Credit limit is required';
        } else if (Number.isNaN(customer.creditLimit)) {
            newErrors.creditLimit = 'Credit limit must be a number';
        } else if (Number(customer.creditLimit) < 0) {
            newErrors.creditLimit = 'Credit limit cannot be negative';
        }

        ['billingAddress', 'shippingAddress'].forEach((addrType) => {
            const addr = customer[addrType];

            if (addr.zip && !/^\d{6}$/.test(addr.zip)) {
                newErrors[`${addrType}_zip`] = 'PIN code must be exactly 6 digits';
            }
            if (addr.state && !/^[A-Za-z ]+$/.test(addr.state.trim())) {
                newErrors[`${addrType}_state`] = 'Only letters allowed';
            }
            if (addr.city && !/^[A-Za-z ]+$/.test(addr.city.trim())) {
                newErrors[`${addrType}_city`] = 'Only letters allowed';
            }
        });

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validate()) return;

        try {
            await api.post('/customer/add', customer);
            alert('Customer Added');
            refreshData();
            closeModal();
        } catch (err) {
            console.error(err);
            alert(
                err.response?.data?.message ||
                err.response?.data ||
                'Failed to add customer'
            );
        }
    };

    const inputClass =
        'w-full h-11 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 transition';

    const errorClass = 'text-red-500 text-xs mt-1';

    const addressFields = [
        { key: 'addressLine', placeholder: 'Address Line', colSpan: true },
        { key: 'city', placeholder: 'City' },
        { key: 'state', placeholder: 'State' },
        { key: 'zip', placeholder: 'PIN Code (6 digits)' },
        { key: 'country', placeholder: 'Country' }
    ];

    const renderAddressSection = (type, label) => (
        <div className="border border-slate-200 rounded-2xl p-5 bg-slate-50">
            <p className="text-sm font-semibold text-slate-700 mb-4">{label}</p>
            <div className="grid grid-cols-2 gap-3">
                {addressFields.map(({ key, placeholder, colSpan }) => (
                    <div key={key} className={colSpan ? 'col-span-2' : 'col-span-1'}>
                        <input
                            value={customer[type][key]}
                            onChange={(e) => handleAddressChange(type, key, e.target.value)}
                            placeholder={placeholder}
                            disabled={type === 'shippingAddress' && sameAsBilling}
                            maxLength={key === 'zip' ? 6 : undefined}
                            className={`${inputClass} ${type === 'shippingAddress' && sameAsBilling
                                    ? 'bg-slate-100 cursor-not-allowed text-slate-400'
                                    : 'bg-white'
                                }`}
                        />
                        {errors[`${type}_${key}`] && (
                            <p className={errorClass}>{errors[`${type}_${key}`]}</p>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
            <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-xl flex flex-col w-full max-w-2xl mx-4">

                <div className="px-8 py-5 bg-slate-50 border-b border-slate-200 rounded-t-2xl">
                    <h2 className="text-xl font-bold text-slate-800">Add Customer</h2>
                    <p className="text-xs text-slate-500 mt-0.5">
                        Fill in the details below. Address fields are optional.
                    </p>
                </div>

                <form
                    onSubmit={handleSubmit}
                    className="flex-1 overflow-y-auto px-8 py-6 space-y-5"
                    style={{ maxHeight: '65vh' }}
                >
                 
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label 
                                htmlFor="customername"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Customer Name <span className="text-red-500">*</span>
                            </label>
                            <input
                                name="name"
                                value={customer.name}
                                onChange={(e) => {
                                    const val = e.target.value;
                                    if (/^[A-Za-z ]*$/.test(val)) handleChange(e);
                                }}
                                placeholder="e.g. Rahul Sharma"
                                className={inputClass}
                            />
                            {errors.name && <p className={errorClass}>{errors.name}</p>}
                        </div>

                        <div>
                            <label 
                                htmlFor="mobilenumber"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Mobile Number <span className="text-red-500">*</span>
                            </label>
                            <input
                                name="phoneNo"
                                value={customer.phoneNo}
                                onChange={(e) => {
                                    const val = e.target.value.replaceAll(/\D/g, '');
                                    if (val.length <= 10) {
                                        setCustomer({ ...customer, phoneNo: val });
                                        setErrors((prev) => ({ ...prev, phoneNo: '' }));
                                    }
                                }}
                                placeholder="10-digit mobile number"
                                maxLength={10}
                                className={inputClass}
                            />
                            {errors.phoneNo && <p className={errorClass}>{errors.phoneNo}</p>}
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label 
                                htmlFor="emailaddress"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Email Address <span className="text-red-500">*</span>
                            </label>
                            <input
                                name="email"
                                value={customer.email}
                                onChange={handleChange}
                                placeholder="e.g. rahul@example.com"
                                className={inputClass}
                            />
                            {errors.email && <p className={errorClass}>{errors.email}</p>}
                        </div>

                        <div>
                            <label 
                                htmlFor="partytype"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Party Type <span className="text-red-500">*</span>
                            </label>
                            <select
                                name="partyType"
                                value={customer.partyType}
                                onChange={handleChange}
                                className={`${inputClass} cursor-pointer`}
                            >
                                <option value="">Select Party Type</option>
                                <option value="Customer">Customer</option>
                                <option value="Dealer">Dealer</option>
                                <option value="Wholesaler">Wholesaler</option>
                            </select>
                            {errors.partyType && <p className={errorClass}>{errors.partyType}</p>}
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label 
                                htmlFor="openingbalance"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Opening Balance <span className="text-red-500">*</span>
                            </label>
                            <input
                                name="balance"
                                value={customer.balance}
                                onChange={(e) => {
                                    const val = e.target.value;

                                    if (
                                        val === '' ||
                                        !Number.isNaN(Number(val))
                                    ) {
                                        handleChange(e);
                                    }
                                }}
                                placeholder="0.00"
                                className={inputClass}
                            />
                            {errors.balance && <p className={errorClass}>{errors.balance}</p>}
                        </div>

                        <div>
                            <label 
                                htmlFor="balancetype"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Balance Type
                            </label>
                            <select
                                name="balanceType"
                                value={customer.balanceType}
                                onChange={handleChange}
                                className={`${inputClass} cursor-pointer`}
                            >
                                <option value="Due">Due</option>
                                <option value="Advance">Advance</option>
                            </select>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label 
                                htmlFor="creditlimit"
                                className="block text-xs font-medium text-slate-600 mb-1">
                                Credit Limit <span className="text-red-500">*</span>
                            </label>
                            <input
                                name="creditLimit"
                                value={customer.creditLimit}
                                onChange={(e) => {
                                    const val = e.target.value;

                                    if (
                                        val === '' ||
                                        !Number.isNaN(Number(val))
                                    ) {
                                        handleChange(e);
                                    }
                                }}
                                placeholder="0.00"
                                className={inputClass}
                            />
                            {errors.creditLimit && <p className={errorClass}>{errors.creditLimit}</p>}
                        </div>
                    </div>

                    <div className="border-t border-slate-200 pt-1">
                        <p className="text-xs text-slate-400 font-medium uppercase tracking-wide">
                            Address Details (Optional)
                        </p>
                    </div>

                    {renderAddressSection('billingAddress', 'Billing Address')}

                    <div className="flex items-center gap-2">
                        <input
                            type="checkbox"
                            id="sameAsBilling"
                            checked={sameAsBilling}
                            onChange={(e) => handleSameAsBilling(e.target.checked)}
                            className="w-4 h-4 accent-cyan-500 cursor-pointer"
                        />
                        <label
                            htmlFor="sameAsBilling"
                            className="text-sm text-slate-600 cursor-pointer select-none"
                        >
                            Shipping address same as billing address
                        </label>
                    </div>

                    {renderAddressSection('shippingAddress', 'Shipping Address')}
                </form>

                <div className="px-8 py-4 border-t border-slate-200 flex justify-end gap-3 rounded-b-2xl bg-white">
                    <button
                        type="button"
                        onClick={closeModal}
                        className="px-5 py-2.5 rounded-xl border border-slate-300 text-slate-700 text-sm font-medium hover:bg-slate-100 transition"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        onClick={handleSubmit}
                        className="px-5 py-2.5 rounded-xl bg-cyan-500 hover:bg-cyan-600 text-white text-sm font-semibold transition"
                    >
                        Save Customer
                    </button>
                </div>

            </div>
        </div>
    );
};

AddCustomer.propTypes = {
    closeModal: PropTypes.func.isRequired,
    refreshData: PropTypes.func.isRequired,
};

export default AddCustomer;