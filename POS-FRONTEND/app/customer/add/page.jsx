"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Sidebar from "../../../components/layout/Sidebar";

import api from "../../../services/api";
import CustomerForm, {
  customerInitialData,
} from "../../../components/customer/CustomerForm";
import { validateForm }
from "../../../components/customer/CustomerValidation";

export default function CustomerAddPage() {
  const router = useRouter();

  const [formData, setFormData] = useState(customerInitialData);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (
    e
  ) => {
    e.preventDefault();
if (
  !validateForm(
    formData,
    setErrors
  )
) {
  return;
}

    try {
      setLoading(true);

      const payload = {
        ...formData,

        creditLimit:
          Number(
            formData.creditLimit
          ) || 0,

        balance:
          Number(
            formData.balance
          ) || 0,
      };

      const response =
        await api.post(
          "/customer/add",
          payload
        );

      if (
        response.data?.success ===
        false
      ) {
        alert(
          response.data.message
        );
        return;
      }

      alert(
        "Customer added successfully"
      );

      setFormData(
        customerInitialData
      );

      router.push(
        "/customer"
      );
    } catch (error) {
      console.error(error);

      alert(
        error?.response?.data
          ?.message ||
          "Unable to save customer"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Sidebar>
      <div className="min-h-screen bg-[#f4f6fb] py-10 px-4 flex justify-center items-start">
        <div className="w-full max-w-[800px] bg-white rounded-xl shadow-md border border-slate-100 p-8">

          <h1 className="text-2xl font-bold mb-8 text-blue-600 tracking-wide">
            Add Customer
          </h1>

          <form
            onSubmit={
              handleSubmit
            }
            className="space-y-6"
>
            <CustomerForm
              formData={
                formData
              }
              handleChange={
                handleChange
              }
              errors={
                errors
              }
            />
            <hr className="border-slate-100 my-4" />

            <div className="flex flex-col sm:flex-row gap-3 pt-2 max-w-md">

              <button
                type="submit"
                disabled={
                  loading
                }
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium px-6 py-2.5 rounded-md shadow-sm transition-colors text-sm disabled:opacity-70 text-center"
              >
                {loading
                  ? "Saving..."
                  : "Save Customer"}
              </button>

              <button
                type="button"
                onClick={() =>
                  router.back()
                }
                className="flex-1 border border-slate-200 hover:bg-slate-50 text-slate-600 font-medium px-6 py-2.5 rounded-md transition-colors text-sm text-center"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </Sidebar>
  );
}