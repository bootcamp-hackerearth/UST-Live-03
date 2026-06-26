import PropTypes from 'prop-types';

export default function AddCustomerModal({
    onClose,
    customerForm,
    setCustomerForm,
    onSubmit,
}) {
    return (

        <div className="space-y-5">
            <div className="space-y-4">
                <div>
                    <label htmlFor="customer-name" className="mb-1 block text-xs font-medium text-slate-600">
                        Customer Name
                    </label>
                    <input
                        id="customer-name"
                        type="text"
                        placeholder="Enter customer name"
                        value={customerForm.name}
                        onChange={(e) =>
                            setCustomerForm({
                                ...customerForm,
                                name: e.target.value,
                            })
                        }
                        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 text-sm transition focus:border-blue-500 focus:bg-white focus:outline-none"
                    />
                </div>

                <div>
                    <label htmlFor="customer-email" className="mb-1 block text-xs font-medium text-slate-600">
                        Email Address
                    </label>
                    <input
                        id="customer-email"
                        type="email"
                        placeholder="customer@email.com"
                        value={customerForm.identifier}
                        onChange={(e) =>
                            setCustomerForm({
                                ...customerForm,
                                identifier: e.target.value,
                            })
                        }
                        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 text-sm transition focus:border-blue-500 focus:bg-white focus:outline-none"
                    />
                </div>

                <div>
                    <label htmlFor="customer-phone" className="mb-1 block text-xs font-medium text-slate-600">
                        Phone Number
                    </label>
                    <input
                        id="customer-phone"
                        type="text"
                        placeholder="1111111111"
                        value={customerForm.phoneNo}
                        onChange={(e) =>
                            setCustomerForm({
                                ...customerForm,
                                phoneNo: e.target.value,
                            })
                        }
                        className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 text-sm transition focus:border-blue-500 focus:bg-white focus:outline-none"
                    />
                </div>
            </div>

            <div className="flex justify-end gap-3 border-t border-slate-100 pt-4">
                <button
                    onClick={onClose}
                    className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100"
                >
                    Cancel
                </button>

                <button
                    onClick={onSubmit}
                    className="rounded-xl bg-violet-500 px-5 py-2 text-sm font-medium text-white transition hover:bg-violet-400"
                >
                    Create Customer
                </button>
            </div>
        </div>
    );
}

AddCustomerModal.propTypes = {
    onClose: PropTypes.func.isRequired,
    customerForm: PropTypes.shape({
        name: PropTypes.string,
        identifier: PropTypes.string,
        phoneNo: PropTypes.string,
    }).isRequired,
    setCustomerForm: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
};