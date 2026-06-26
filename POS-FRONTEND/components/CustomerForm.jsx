"use client"

import { useForm } from "react-hook-form"
import { emailValidation, nameValidation, phoneValidation, requiredValidation } from "@/validation/validation"
import axios from "@/components/axiosConfig"
import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { ArrowLeft, CheckCircle2, AlertCircle } from 'lucide-react'
import PropTypes from 'prop-types'
import AuditField from "./AuditField"

const CustomerForm = ({ mode = "add", identifier = null }) => {

    CustomerForm.propTypes = {
        mode: PropTypes.oneOf(["add", "update"]),
        identifier: PropTypes.string,
    }
    const router = useRouter()
    const [message, setMessage] = useState("")
    const [auditField, setAuditField] = useState({});

    const { register, handleSubmit, reset, formState: { errors } } = useForm()

    useEffect(() => {
        async function init() {
            if (mode === "update" && identifier) {
                const res = await axios.get(`/customer/get?identifier=${identifier}`)
                reset(res.data);
               setAuditField(res.data);
            }
        }
        init();
    }, [mode, identifier])

    const onSubmit = async (formData) => {
        try {
            const res = mode === "add"
                ? await axios.post("/customer/add", formData)
                : await axios.put("/customer/update", formData)

            if (res.data.success) {
                mode === "add" ? setMessage("Add success") : setMessage("Update success");
                setAuditField(res.data)
                setTimeout(() => { setMessage(""); router.back() }, 2000)
            } else {
                setMessage(res.data.message)
            }
        } catch (error) {
            console.error(error)
            setMessage("An error occurred. Please try again.")
        } finally {
            reset()
            setTimeout(() => setMessage(""), 3000)
        }
    }

    const renderFieldError = (error) => error && (
        <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
            <span>⚠</span> {error.message}
        </p>
    )

    const inputClass = (hasError) =>
        `h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white ${hasError ? "border-rose-400 bg-rose-50/10 focus:border-rose-500" : "border-slate-200 focus:border-slate-900"
        }`

    const renderCoreDetails = () => (
        <div>
            <h3 className="text-sm font-bold uppercase tracking-wider text-black mb-4 pb-1 border-b border-slate-100">
                Core Details
            </h3>
            <div className="flex flex-wrap gap-x-6 gap-y-5">
                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="name" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Customer Name</label>
                    <input type="text" placeholder="Enter customer name..." {...register("name", nameValidation)} className={inputClass(errors.name)} />
                    {renderFieldError(errors.name)}
                </div>

                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="PhoneNumber" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Phone Number</label>
                    <input type="text" placeholder="Enter phone number..." {...register("identifier", phoneValidation)} className={inputClass(errors.identifier)} />
                    {renderFieldError(errors.identifier)}
                </div>

                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="email" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Email</label>
                    <input type="text" placeholder="Enter email address..." {...register("email", emailValidation)} className={inputClass(errors.email)} />
                    {renderFieldError(errors.email)}
                </div>

                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="partyType" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Party Type</label>
                    <div className="relative flex items-center">
                        <select
                            {...register("partyType", requiredValidation)}
                            className={`text-sm h-10 w-full pl-3.5 pr-8 py-2 border rounded-lg bg-white font-medium text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-colors appearance-none cursor-pointer ${errors.partyType ? "border-rose-400 focus:border-rose-500" : "border-slate-200 focus:border-slate-900"
                                }`}
                        >
                            <option value="Customer">Customer</option>
                            <option value="Wholesaler">Wholesaler</option>
                            <option value="Retailer">Retailer</option>
                        </select>
                        <div className="pointer-events-none absolute right-3 text-zinc-400 text-[10px]">▼</div>
                    </div>
                    {renderFieldError(errors.partyType)}
                </div>

                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="balance" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Balance</label>
                    <input type="number" placeholder="0.00" {...register("balance", requiredValidation)} className={inputClass(errors.balance)} />
                    {renderFieldError(errors.balance)}
                </div>

                <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                    <label htmlFor="creditLimit" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Credit Limit</label>
                    <input type="number" placeholder="0.00" {...register("creditLimit", requiredValidation)} className={inputClass(errors.creditLimit)} />
                    {renderFieldError(errors.creditLimit)}
                </div>
            </div>
        </div>
    )

    const renderAddressFields = (type, title) => {
        const addressErrors = errors[type]
        return (
            <div>
                <h3 className="text-sm font-bold uppercase tracking-wider text-black mb-4 pb-1 border-b border-slate-100">
                    {title}
                </h3>
                <div className="flex flex-wrap gap-x-6 gap-y-5">
                    <div className="flex flex-col w-full">
                        <label htmlFor="addressLine" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Address Line</label>
                        <input
                            type="text"
                            placeholder="Street name, suite, unit..."
                            {...register(`${type}.addressLine`, requiredValidation)}
                            className={inputClass(addressErrors?.addressLine)}
                        />
                        {renderFieldError(addressErrors?.addressLine)}
                    </div>
                    <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                        <label htmlFor="city" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">City</label>
                        <input
                            type="text" placeholder="City name..."
                            {...register(`${type}.city`, requiredValidation)}
                            className={inputClass(addressErrors?.city)} />

                        {renderFieldError(addressErrors?.city)}
                    </div>
                    <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                        <label htmlFor="state" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">State</label>

                        <input
                            type="text"
                            placeholder="State/Province..."
                            {...register(`${type}.state`, requiredValidation)}
                            className={inputClass(addressErrors?.state)}
                        />

                        {renderFieldError(addressErrors?.state)}
                    </div>
                    <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                        <label htmlFor="country" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Country</label>

                        <input
                            type="text"
                            placeholder="Country..."
                            {...register(`${type}.country`, requiredValidation)}
                            className={inputClass(addressErrors?.country)}
                        />

                        {renderFieldError(addressErrors?.country)}

                    </div>
                    <div className="flex flex-col w-full md:w-[calc(50%-12px)]">
                        <label htmlFor="number" className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">Zipcode</label>

                        <input
                            type="number"
                            placeholder="Zip/Postal code..."
                            {...register(`${type}.zipcode`, requiredValidation)}
                            className={inputClass(addressErrors?.zipcode)}
                        />
                        {renderFieldError(addressErrors?.zipcode)}

                    </div>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-slate-50 p-6 md:p-12 flex justify-center items-start">
            <div className="w-full max-w-4xl bg-white shadow-sm border border-slate-200/80 rounded-2xl overflow-hidden">

                <div className="border-b border-slate-100 p-6 bg-white flex items-center justify-between">
                    <div>
                        <button type="button" onClick={() => router.back()}
                            className="inline-flex items-center gap-1 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors mb-2 cursor-pointer">
                            <ArrowLeft className="w-3.5 h-3.5" /> Back to list
                        </button>
                        <h2 className="text-xl font-bold text-slate-900 tracking-tight uppercase">
                            Customer {mode === "add" ? "Add" : "Update"}
                        </h2>
                    </div>
                </div>

                <div className="p-6 md:p-8">

                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
                        {renderCoreDetails()}
                        {renderAddressFields("shippingAddress", "Shipping Address")}
                        {renderAddressFields("billingAddress", "Billing Address")}

                        <div className="w-full flex justify-end gap-3 mt-8 pt-6 border-t border-slate-100">
                            <button type="button" onClick={() => router.back()}
                                className="px-5 py-2 border border-slate-200 text-slate-600 rounded-lg text-sm font-semibold hover:bg-slate-50 hover:text-slate-900 transition-colors cursor-pointer">
                                Cancel
                            </button>
                            <button type="submit"
                                className="px-6 py-2 bg-slate-900 text-white font-semibold rounded-lg text-sm hover:bg-slate-800 shadow-sm transition-colors cursor-pointer">
                                {mode === "add" ? "Add Customer" : "Update Customer"}
                            </button>
                        </div>
                    </form>

                    {message && (
                        <div className={`flex items-center gap-2 p-3 mt-6 rounded-lg text-sm font-medium border transition-all ${message.includes("success") ? "bg-emerald-50 border-emerald-200 text-emerald-800" : "bg-rose-50 border-rose-200 text-rose-800"
                            }`}>
                            {message.includes("success")
                                ? <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
                                : <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />}
                            <p>{message}</p>
                        </div>
                    )}

                    {mode == "update" ?
                        <AuditField auditField = {auditField} /> : ""}

                </div>
            </div>
        </div>
    )
}

export default CustomerForm