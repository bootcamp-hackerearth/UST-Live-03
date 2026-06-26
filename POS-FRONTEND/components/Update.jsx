"use client";
import React, { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import PropTypes from 'prop-types';
import CommonDropDown from "@/components/CommonDropDown";
import axios from "./axiosConfig";
import { useRouter } from "next/navigation";
import AuditField from "./AuditField";
import AccessDenied from "./AccessDenied";
import ResourceNotFound from "./ResourceNotFound";

const Update = (props) => {
    const router = useRouter();
    const identifier = props.identifier
    const [message, setMessage] = useState("");
    const urlName = props.urlName;
    const formFields = props.formFelids;
    const dropDowns = props.dropDowns;
    const hardCodedDropDowns = props.hardCodedDropDowns;
    const [auditField, setAuditField] = useState({});
    const [accessDenied, setAccessDenied] = useState(false)
    const [resourceNotFound, setResourceNotFound] = useState(false)

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        watch,
        formState: { errors },
    } = useForm();

    const getByIdentifier = async (identifier) => {
        try {
            const queryParam = urlName === "user" ? "username" : "identifier";
            const res = await axios.get(`${urlName}/get?${queryParam}=${identifier}`);
            setAuditField(res.data);
            reset(res.data);
        } catch (error) {
            if (error.response?.status === 403) {
                setAccessDenied(true)
            }
            else
            if(error.response?.status === 404){
                setResourceNotFound(true);
            }
            else {
                alert("Something went wrong");
            }
        }
    }

    useEffect(() => {
        getByIdentifier(identifier)
    }, [])


    const onSubmit = async (formData) => {
        const res = await axios.put(`/${urlName}/update`, formData);

        if (res.data.success) {
            setMessage("Update success");
            setTimeout(() => {
                setMessage("");
                router.push(`/${urlName}`)
            }, 2000);
        } else {
            setMessage(res.data.message);
        }

        setTimeout(() => {
            setMessage("");
        }, 3000);
    };

    return (
        <>
        {accessDenied ? <AccessDenied/> : ""}
        {resourceNotFound ? <ResourceNotFound/> : ""}
        <div className="min-h-screen bg-gray-50 flex justify-center items-start pt-16">
            <div className="w-full max-w-4xl bg-white shadow-lg rounded-2xl p-8">

                <h2 className="text-2xl font-semibold text-center mb-6">
                    update {urlName}
                </h2>

                {message && (
                    <p
                        className={`text-center mb-4 font-medium ${message.includes("success")
                            ? "text-green-600"
                            : "text-red-600"
                            }`}
                    >
                        {message}
                    </p>
                )}

                <form
                    onSubmit={handleSubmit(onSubmit)}
                    className="flex flex-wrap gap-4"
                >

                    {formFields.map((field) => {
                        if (["text", "number", "tel", "password", "email"].includes(field.type)) {
                            return (
                                <div key={field.name} className="flex flex-col w-full md:w-[48%]">
                                    <label className="mb-1 text-sm font-medium text-gray-700">
                                        {"Enter " + field.name}
                                    </label>

                                    <input
                                        type={field.type}
                                        {...register(field.name, field.validation)}
                                        placeholder={`${field.name}`}
                                        className={`h-10 px-3 border rounded-lg focus:outline-none focus:ring-2 transition text-sm
                                            ${errors[field.name]
                                                ? "border-red-500 ring-red-300"
                                                : "border-gray-300 focus:ring-black"
                                            }`}
                                        disabled={field.isDisabled ?? false}
                                    />

                                    {errors[field.name] && (
                                        <p className="text-red-500 text-sm mt-1">
                                            {errors[field.name].message}
                                        </p>
                                    )}
                                </div>
                            );
                        }
                    })}

                    {dropDowns.map((dropDown) => (
                        <div key={dropDown.name} className="flex flex-col w-full md:w-[48%]">
                            <label className="mb-1 text-sm font-medium text-gray-700">
                                {"Select " + dropDown.name}
                            </label>

                            <CommonDropDown
                                register={register}
                                name={dropDown.name}
                                urlName={dropDown.urlName}
                                httpMethod={dropDown.httpMethod}
                                ismultiple={dropDown.ismultiple}
                                validation={dropDown.validation}
                                errors={errors}
                                setValue={setValue}
                                currentValue={watch(dropDown.name)}
                                isDisabled={dropDown.isDisabled}
                            />
                        </div>
                    ))}

                    {hardCodedDropDowns.map((dropDown, index) => (
                        <div key={dropDown.name} className="flex flex-col w-full md:w-[48%]">
                            <label className="mb-1 text-sm font-medium text-gray-700">
                                {"Select " + dropDown.name}
                            </label>
                            <select
                                {...register(dropDown.name, dropDown.validation)}
                                className={`text-sm px-3 py-2 border border-gray-300 rounded-lg bg-white 
                                focus:outline-none focus:ring-2 focus:ring-black transition`}
                                disabled={dropDown.isDisabled ?? false}
                            >
                                <option value="">Select {dropDown.name}</option>
                                {dropDown.values.map((value) => (
                                    <option key={value} value={value}>{value}</option>
                                ))}
                            </select>
                            {errors[dropDown.name] && (
                                <p className="text-red-500 text-sm mt-1">
                                    {errors[dropDown.name].message}
                                </p>
                            )}

                        </div>
                    ))}

                    <div className="w-full flex justify-center gap-4 mt-6">
                        <button
                            type="button"
                            onClick={() => router.push(`/${urlName}`)}
                            className="px-5 py-2 border border-gray-400 rounded-lg hover:bg-gray-100 transition"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="px-6 py-2 bg-black text-white rounded-lg hover:bg-gray-800 transition"
                        >
                            Submit
                        </button>
                    </div>

                    <AuditField auditField={auditField} />
                </form>
            </div>
        </div>
    </>
    );
};

Update.propTypes = {
    identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    urlName: PropTypes.string.isRequired,
    formFelids: PropTypes.arrayOf(
        PropTypes.shape({
            name: PropTypes.string.isRequired,
            type: PropTypes.string.isRequired,
            validation: PropTypes.object,
            isDisabled: PropTypes.bool,
        })
    ).isRequired,
    dropDowns: PropTypes.array,
    hardCodedDropDowns: PropTypes.array,
};

Update.defaultProps = {
    dropDowns: [],
    hardCodedDropDowns: [],
};
export default Update;