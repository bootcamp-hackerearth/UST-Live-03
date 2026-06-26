"use client"
import { useForm } from "react-hook-form";
import { useState } from "react";
import PropTypes from "prop-types";
import axios from "../components/axiosConfig";
import { emailValidation } from "@/validation/validation";

const CustomerAddModal = ({ setCurrentCustomer, onClose }) => {
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm({
        defaultValues: {
            partyType: "Customer"
        }
    });
   
    const onSubmit = async (data) => {
        setIsSubmitting(true);
        setSubmitError("");
        try {
            const res = await axios.post("/customer/add", data);
            console.log(res.data);
            setCurrentCustomer(res.data.identifier);
            reset();
            if (onClose) onClose(); 
        } catch (error) {
            console.error(error);
            setSubmitError(error.response?.data?.message || "Failed to save customer record.");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="w-80 bg-white flex flex-col">
           
            <div className="pb-3 mb-4 border-b border-zinc-100">
                <p className="text-[10px] font-semibold text-zinc-400 uppercase tracking-widest mb-0.5">
                    Profiles
                </p>
                <h2 className="text-base font-medium text-zinc-900">
                    Add new customer
                </h2>
            </div>

            {submitError && (
                <div className="mb-3 text-[11px] font-medium text-red-700 bg-red-50 border border-red-100 rounded-lg p-2">
                    {submitError}
                </div>
            )}

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3.5">
                
                <div className="flex flex-col gap-1">
                    <label htmlFor="customerName" className="text-[11px] text-zinc-500 font-medium">
                        Customer Name <span className="text-red-500">*</span>
                    </label>
                    <input 
                        id="customerName"
                        {...register("name", { required: "Name is required" })} 
                        type="text"
                        placeholder="e.g. John Doe"
                        autoFocus
                        className={`w-full text-xs border rounded-lg px-2.5 py-1.5 bg-white outline-none transition-colors ${
                            errors.name 
                                ? "border-red-300 focus:border-red-500 bg-red-50/10" 
                                : "border-zinc-200 focus:border-zinc-400"
                        }`}
                    />
                    {errors.name && (
                        <p className="text-[10px] font-medium text-red-600 mt-0.5">
                            {errors.name.message}
                        </p>
                    )}
                </div>

                
                <div className="flex flex-col gap-1">
                    <label htmlFor="phoneNo" className="text-[11px] text-zinc-500 font-medium">
                        Phone No / Identifier <span className="text-red-500">*</span>
                    </label>
                    <input 
                        id="phoneNo"
                        {...register("identifier", { 
                            required: "Phone no is required",
                            pattern: { value: /^\d+$/, message: "Please enter numbers only" }
                        })} 
                        type="tel"
                        placeholder="9876543210"
                        className={`w-full text-xs font-mono border rounded-lg px-2.5 py-1.5 bg-white outline-none transition-colors ${
                            errors.identifier 
                                ? "border-red-300 focus:border-red-500 bg-red-50/10" 
                                : "border-zinc-200 focus:border-zinc-400"
                        }`}
                    />
                    {errors.identifier && (
                        <p className="text-[10px] font-medium text-red-600 mt-0.5">
                            {errors.identifier.message}
                        </p>
                    )}
                </div>

                
                <div className="flex flex-col gap-1">
                    <label htmlFor="email" className="text-[11px] text-zinc-500 font-medium">
                        Email Address
                    </label>
                    <input 
                        id="email"
                        {...register("email",emailValidation)} 
                        type="email"
                        placeholder="name@domain.com"
                        className="w-full text-xs border border-zinc-200 rounded-lg px-2.5 py-1.5 bg-white outline-none focus:border-zinc-400 transition-colors"
                    />
                </div>

               
                <div className="flex flex-col gap-1">
                    <label htmlFor="partyType" className="text-[11px] text-zinc-500 font-medium">
                        Party Type
                    </label>
                    <div className="relative flex items-center">
                        <select 
                            id="partyType"
                            {...register("partyType")}
                            className="w-full text-xs border border-zinc-200 rounded-lg pl-2.5 pr-8 py-1.5 bg-white outline-none focus:border-zinc-400 transition-colors appearance-none cursor-pointer"
                        >
                            <option value="Customer">Customer</option>
                            <option value="Wholesaler">Wholesaler</option>
                            <option value="Retailer">Retailer</option>
                        </select>
                        <div className="pointer-events-none absolute right-2.5 flex items-center text-zinc-400 text-[10px]">
                            ▼
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-end gap-2 pt-3 mt-4 border-t border-zinc-100">
                    <button 
                        type="button" 
                        onClick={onClose}
                        className="px-3 py-1.5 text-xs font-medium border border-zinc-200 rounded-lg bg-white hover:bg-zinc-50 cursor-pointer transition-colors"
                    >
                        Cancel
                    </button>
                    <button 
                        type="submit" 
                        disabled={isSubmitting}
                        className="px-3 py-1.5 text-xs font-medium text-white bg-zinc-900 border border-transparent rounded-lg hover:bg-zinc-700 disabled:bg-zinc-200 disabled:text-zinc-400 cursor-pointer transition-colors flex items-center gap-1.5"
                    >
                        {isSubmitting ? "Saving..." : "Add & Select"}
                    </button>
                </div>
            </form>
        </div>
    );
};

CustomerAddModal.propTypes = {
    setCurrentCustomer: PropTypes.func.isRequired,
    onClose: PropTypes.func,
};

export default CustomerAddModal;