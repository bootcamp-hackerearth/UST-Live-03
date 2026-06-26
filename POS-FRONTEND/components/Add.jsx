"use client";
import React, { useState } from "react";
import PropTypes from "prop-types";
import { useForm } from "react-hook-form";
import CommonDropDown from "@/components/CommonDropDown";
import axios from "../components/axiosConfig";
import { useRouter } from "next/navigation";
import { ArrowLeft, CheckCircle2, AlertCircle } from 'lucide-react';
import AccessDenied from "./AccessDenied";

const Add = (props) => {
    const router = useRouter();

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm();

    const [message, setMessage] = useState("");
    const urlName = props.urlName;
    const [accessDenied, setAccessDenied] = useState(false)

    const onSubmit = async (formData) => {
        const endpoint = urlName === "user" ? "/register" : `/${urlName}/add`;

        try{
            const res = await axios.post(endpoint, formData);
            if (res.data.success) {
            setMessage("Add success");
            setTimeout(() => {
                setMessage("");
                router.back()
            }, 2000);
            console.log("Data added successfully");
        } else {
            setMessage(res.data.message);
        }

        reset();

        setTimeout(() => {
            setMessage("");
        }, 3000);

        }catch (error) {
            if (error.response?.status === 403) {
                setAccessDenied(true)
            }
            else {
                alert("Something went wrong");
            }
        }
    };

    const formFields = props.formFelids;
    const dropDowns = props.dropDowns;
    const hardCodedDropDowns = props.hardCodedDropDowns;

    return (
        <>
        {accessDenied ? <AccessDenied/> : ""}
        <div className="min-h-screen bg-slate-50 p-6 md:p-12 flex justify-center items-start">
            <div className="w-full max-w-4xl bg-white shadow-sm border border-slate-200/80 rounded-2xl overflow-hidden">
                
                <div className="border-b border-slate-100 p-6 bg-white flex items-center justify-between">
                    <div>
                        <button 
                            type="button"
                            onClick={() => router.back()}
                            className="inline-flex items-center gap-1 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors mb-2 cursor-pointer"
                        >
                            <ArrowLeft className="w-3.5 h-3.5" /> Back to list
                        </button>
                        <h2 className="text-xl font-bold text-slate-900 tracking-tight uppercase">
                            Create New {urlName}
                        </h2>
                    </div>
                </div>

                <div className="p-6 md:p-8">
                    {message && (
                        <div className={`flex items-center gap-2 p-3 mb-6 rounded-lg text-sm font-medium border transition-all ${
                            message.includes("success")
                                ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                                : "bg-rose-50 border-rose-200 text-rose-800"
                        }`}>
                            {message.includes("success") ? (
                                <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-600" />
                            ) : (
                                <AlertCircle className="w-4 h-4 shrink-0 text-rose-600" />
                            )}
                            <p>{message}</p>
                        </div>
                    )}

                    <form
                        onSubmit={handleSubmit(onSubmit)}
                        className="flex flex-wrap gap-x-6 gap-y-5"
                    >
                        {formFields.map((field) => {
                            if (["text", "number", "tel", "password", "email"].includes(field.type)) {
                                return (
                                    <div key={field.name} className="flex flex-col w-full md:w-[calc(50%-12px)]">
                                        <label className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                            {field.name.replaceAll(/([A-Z])/g, ' $1')}
                                        </label>

                                        <input
                                            type={field.type}
                                            {...register(field.name, field.validation)}
                                            placeholder={`Enter ${field.name.toLowerCase()}...`}
                                            className={`h-10 px-3.5 border rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all text-sm font-medium text-slate-800 placeholder:text-slate-400 bg-white
                                                ${errors[field.name]
                                                    ? "border-rose-400 bg-rose-50/10 focus:border-rose-500"
                                                    : "border-slate-200 focus:border-slate-900"
                                                } ${field.isDisabled ? "bg-slate-100 text-slate-400 cursor-not-allowed" : ""}`}
                                            disabled={field.isDisabled ?? false}
                                        />

                                        {errors[field.name] && (
                                            <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                                                <span>⚠</span> {errors[field.name].message}
                                            </p>
                                        )}
                                    </div>
                                );
                            }
                            return null;
                        })}

                        {dropDowns.map((dropDown) => (
                            <div key={dropDown.name} className="flex flex-col w-full md:w-[calc(50%-12px)]">
                                <label className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                    {dropDown.name.replaceAll(/([A-Z])/g, ' $1')}
                                </label>

                                <CommonDropDown
                                    register={register}
                                    name={dropDown.name}
                                    urlName={dropDown.urlName}
                                    httpMethod={dropDown.httpMethod}
                                    ismultiple={dropDown.ismultiple}
                                    validation={dropDown.validation}
                                    errors={errors}
                                />
                            </div>
                        ))}

                        {hardCodedDropDowns.map((dropDown) => (
                            <div key={dropDown.name} className="flex flex-col w-full md:w-[calc(50%-12px)]">
                                <label className="mb-1.5 text-xs font-semibold uppercase tracking-wider text-slate-600">
                                    {dropDown.name.replaceAll(/([A-Z])/g, ' $1')}
                                </label>
                                <select
                                    {...register(dropDown.name, dropDown.validation)}
                                    className={`text-sm h-10 px-3 py-2 border rounded-lg bg-white font-medium text-slate-800
                                     focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all cursor-pointer
                                     ${errors[dropDown.name]
                                        ? "border-rose-400 focus:border-rose-500"
                                        : "border-slate-200 focus:border-slate-900"
                                    }`}
                                >
                                    <option value="" className="text-slate-400">Select {dropDown.name.toLowerCase()}...</option>
                                    {dropDown.values.map((value) => (
                                        <option key={value} value={value}>{value}</option>
                                    ))}
                                </select>
                                {errors[dropDown.name] && (
                                    <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                                        <span>⚠</span> {errors[dropDown.name].message}
                                    </p>
                                )}
                            </div>
                        ))}

                        <div className="w-full flex justify-end gap-3 mt-8 pt-6 border-t border-slate-100">
                            <button
                                type="button"
                                onClick={() => router.back()}
                                className="px-5 py-2 border border-slate-200 text-slate-600 rounded-lg text-sm font-semibold hover:bg-slate-50 hover:text-slate-900 transition-colors cursor-pointer"
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="px-6 py-2 bg-slate-900 text-white font-semibold rounded-lg text-sm hover:bg-slate-800 shadow-sm transition-colors cursor-pointer"
                            >
                                Save Entries
                            </button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
        </>
    );
};

Add.propTypes = {
    urlName: PropTypes.string.isRequired,
    formFelids: PropTypes.array.isRequired,
    dropDowns: PropTypes.array.isRequired,
    hardCodedDropDowns: PropTypes.array.isRequired,
};

export default Add;