"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useForm } from "react-hook-form";
import { useRouter, useParams } from "next/navigation";
import Link from "next/link";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import Spinner from "./Spinner";
import FormFields from "./FormFields";
import DropDownService from "./DropDownService";
import { useFetchEntity } from "@/hooks/useFetchEntity";

const FORM_MODE = {
  ADD: "add",
  UPDATE: "update"
}

const AddEditForm = ({ title, fields, apiRoute, dropdownApis, method }) => {

  const { register, reset, handleSubmit, formState: { errors } } = useForm();
  const router = useRouter();
  const params = useParams();
  const identifier = params?.identifier ? decodeURIComponent(params.identifier) : null;
  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const [dropdownData, setDropdownData] = useState({});
  const [errorMessage, setErrorMessage] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const { data, loading } = useFetchEntity({
    baseUrl,
    apiRoute,
    identifier,
    method
  });

  useEffect(() => {

    if (!dropdownApis || Object.keys(dropdownApis).length === 0) return;

    DropDownService(dropdownApis)
      .then((data) => setDropdownData(data))
      .catch((err) => {
        console.log(err);
      });
  }, [dropdownApis]);

  useEffect(() => {

    const hasDropdownApis = dropdownApis && Object.keys(dropdownApis).length > 0;

    const dropdownReady =
      !hasDropdownApis || Object.keys(dropdownData).length > 0;

    if (data && dropdownReady) {
      reset(data)
    }
  }, [data, dropdownData, dropdownApis, reset]);

  const onSubmit = async (formData) => {    
    setSubmitting(true)
    try {
      if (method === FORM_MODE.ADD) {
        const response = await FetchEntity(
          `${baseUrl}/${apiRoute}/add`,
          "POST",
          formData,
          "application/json"
        );

        if (response.message != null || response.error) {
          setErrorMessage(response?.message || "Error adding data");

          setTimeout(() => {
            setErrorMessage("");
          }, 4000);

          return;
        } else {
          router.push(`/${apiRoute}`);
        }

      } else {
        await FetchEntity(
          `${baseUrl}/${apiRoute}/update`,
          "PUT",
          formData,
          "application/json"
        );
        router.push(`/${apiRoute}`);
      }
    } catch (err) {
      console.log(err);
      setErrorMessage("Something went wrong");
    } finally {
      setSubmitting(false)
    }
  };

  let submitButtonText = "Update";
  if (submitting) {
    submitButtonText = "Submitting...";
  } else if (method === FORM_MODE.ADD) {
    submitButtonText = "Create";
  }

  if (loading && method === FORM_MODE.UPDATE) {
    return (
      <Spinner size={40} />
    );
  }

  return (

    <div className="min-h-screen bg-[#F4F5FB] flex justify-center px-4">
      <div className="w-full max-w-xl mt-6">

        <h1 className="text-xl font-semibold text-slate-700 text-center mb-3">
          {method.charAt(0).toUpperCase() + method.slice(1)} {title}
        </h1>

        {method === FORM_MODE.UPDATE && data && (
          <div className="mb-3 px-2 py-2.5 bg-violet-50 border border-violet-200 rounded-xl">
            <div className="flex justify-between text-xs">
              <div>
                <p className="font-semibold text-violet-700">Created</p>
                <p className="text-gray-600">
                  <span className="font-medium">By:</span> {data.createdBy || "-"}
                </p>
                <p className="text-gray-600">
                  <span className="font-medium">On:</span>{" "}
                  {data.createdOn
                    ? new Date(data.createdOn).toLocaleDateString()
                    : "-"}
                </p>
              </div>

              <div className="text-right">
                <p className="font-semibold text-violet-700">Modified</p>
                <p className="text-gray-600">
                  <span className="font-medium">By:</span> {data.modifiedBy || "-"}
                </p>
                <p className="text-gray-600">
                  <span className="font-medium">On:</span>{" "}
                  {data.modifiedOn
                    ? new Date(data.modifiedOn).toLocaleDateString()
                    : "-"}
                </p>
              </div>
            </div>
          </div>
        )}

        <div className="bg-white mt-5 p-5 border border-slate-300 rounded-xl shadow">

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <input type="hidden" {...register("id")} />

            {fields.map((field) => {
              if (field.type === "section") {
                return (
                  <div
                    key={field.title}
                    className="md:col-span-2 mt-2 border-b border-violet-200 pb-2"
                  >
                    <h2 className="text-sm font-semibold text-violet-700">
                      {field.title}
                    </h2>
                  </div>
                );
              }

              return (
                <FormFields
                  key={field.identifier ?? field.name}
                  field={field}
                  register={register}
                  errors={errors}
                  dropdownData={dropdownData}/>
              );
            })}

            <div className="md:col-span-2 flex gap-3 pt-2">
              <Link
                href={`/${apiRoute}`}
                className="flex-1 py-2 rounded-xl border border-violet-300 text-violet-600 text-center font-medium hover:bg-violet-50 transition"
              >
                Cancel
              </Link>

              <button
                type="submit"
                disabled={submitting}
                className="flex-1 py-2 rounded-xl bg-violet-500 text-white font-medium hover:bg-violet-600 transition disabled:bg-violet-300"
              >
                {submitButtonText}
              </button>
            </div>
          </form>
        </div>

        {errorMessage && (
          <div className="text-center mt-4 text-red-500">
            {errorMessage}
          </div>
        )}
      </div>
    </div>
  );
};

AddEditForm.propTypes = {
  title: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(PropTypes.object).isRequired,
  apiRoute: PropTypes.string.isRequired,
  dropdownApis: PropTypes.object,
  method: PropTypes.string.isRequired,
};

export default AddEditForm;