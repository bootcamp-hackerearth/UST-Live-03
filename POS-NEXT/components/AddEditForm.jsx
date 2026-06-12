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
          formData,
          "application/json"
        );
        if (!response || response.error) {
          setErrorMessage(response?.message || "Error adding data");
          return;
        }
      } else {
        await FetchEntity(
          `${baseUrl}/${apiRoute}/update`,
          formData,
          "application/json"
        );
      }
      router.push(`/${apiRoute}`);
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
    <div className="min-h-screen bg-[#F4F5FB] flex items-center justify-center px-4">
      <div className="w-full max-w-3xl mb-10 bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
        <h1 className="text-xl font-semibold text-gray-700 text-center mb-6">
          {method.charAt(0).toUpperCase() + method.slice(1)} {title}
        </h1>

        <form
          onSubmit={handleSubmit(onSubmit)}
          className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <input type="hidden" {...register("id")} />

          {fields.map((field) => (
            <FormFields
              key={field.identifier ?? field.name}
              field={field}
              register={register}
              errors={errors}
              dropdownData={dropdownData}
            />
          ))}

          <button className="md:col-span-2 mt-2 py-2 rounded-xl bg-indigo-500 text-white text-sm font-medium hover:bg-indigo-600 transition disabled:bg-indigo-400 disabled:hover:bg-indigo-500"
            disabled={submitting}>
            {submitButtonText}
          </button>
        </form>

        <div className="text-center mt-6">
          <Link
            href={`/${apiRoute}`}
            className="text-sm text-indigo-500 hover:underline"
          >
            Go back
          </Link>
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