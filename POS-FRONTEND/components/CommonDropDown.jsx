"use client";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";

const CommonDropDown = ({
    register,
    name,
    ismultiple,
    urlName,
    httpMethod,
    validation,
    errors,
    reset,
    setValue, 
    currentValue,
    isDisabled
}) => {
    const [listData, setListData] = useState([]);

    useEffect(() => {
        fetchList();
    }, []);

    useEffect(() => {
        if (currentValue !== undefined && currentValue !== null) {
            setValue(name, currentValue);
        }
    }, [currentValue]);


    async function fetchList() {
        const options = {
            method: httpMethod,
            headers: {
                "Content-Type": "application/json",
            },
            credentials:"include"
        };

        if (httpMethod.toLowerCase() === "post") {
            options.body = JSON.stringify({
                page: 0,
                sizePerPage: 100,
            });
        }

        const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/${urlName}`, options);
        const response = await res.json();

        setListData(response.dtoList || response);
    }

    return (
        <div className="flex flex-col w-full">
            <select
                {...register(name, validation)}
                multiple={ismultiple}
                className={`text-sm px-3.5 py-2 border rounded-lg bg-white font-medium text-slate-800
                focus:outline-none focus:ring-2 focus:ring-slate-900/10 transition-all cursor-pointer
                ${ismultiple ? "h-24 p-2" : "h-10"}
                ${errors[name] 
                    ? "border-rose-400 focus:border-rose-500 bg-rose-50/10" 
                    : "border-slate-200 focus:border-slate-900"
                } ${isDisabled ? "bg-slate-100 text-slate-400 cursor-not-allowed" : ""}`}
                disabled={isDisabled ?? false}
            >
                {!ismultiple && (
                    <option value="" className="text-slate-400">
                        Select {name.toLowerCase()}...
                    </option>
                )}

                {listData?.map((data) => (
                    <option
                        key={data.identifier} 
                        value={data.identifier + (data.name ? "-"+data.name : "")}
                        className="text-sm py-1 px-1 text-slate-700"
                    >
                        {data.identifier + (data.name ? "-"+data.name : "")}
                    </option>
                ))}
            </select>

            {errors[name] && (
                <p className="text-rose-600 text-xs mt-1.5 font-medium flex items-center gap-1">
                    <span>⚠</span> {errors[name].message}
                </p>
            )}
        </div>
    );
};

export default CommonDropDown;

CommonDropDown.propTypes = {
    register: PropTypes.func.isRequired,
    name: PropTypes.string.isRequired,
    ismultiple: PropTypes.bool,
    urlName: PropTypes.string.isRequired,
    httpMethod: PropTypes.string,
    validation: PropTypes.object,
    errors: PropTypes.object,
    reset: PropTypes.func,
    setValue: PropTypes.func,
    currentValue: PropTypes.any,
    isDisabled: PropTypes.bool,
};

CommonDropDown.defaultProps = {
    ismultiple: false,
    httpMethod: 'get',
    validation: {},
    errors: {},
    isDisabled: false,
};