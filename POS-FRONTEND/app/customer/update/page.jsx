"use client";

import { useEffect, useState } from "react";
import {
  useRouter,
  useSearchParams,
} from "next/navigation";

import api from "../../../services/api";
import Sidebar from "../../../components/layout/Sidebar";

import CustomerForm, {
  customerInitialData,
} from "../../../components/customer/CustomerForm";
import { validateForm }
from "../../../components/customer/CustomerValidation";

export default function CustomerUpdatePage() {

  const router = useRouter();
  const searchParams = useSearchParams();
  const identifier =searchParams.get("identifier") || "";
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState({});
  const [formData, setFormData] = useState(customerInitialData);

  useEffect(() => {
    if (identifier) {
      loadCustomer();
    }
  }, [identifier]);

  const loadCustomer =
    async () => {
      try {
        setLoading(true);

        const response =
          await api.get(
            `/customer/get?identifier=${identifier}`
          );

        setFormData({
          ...customerInitialData,
          ...response.data,
        });

          } catch (error) {
      const message =
        error?.response?.data?.message ||
        "Unable to load customer";

      alert(message);

      router.push("/customer");
    } finally {
        setLoading(false);
      }
    };

  const handleChange =
    (e) => {
      const {
        name,
        value,
      } = e.target;

      setFormData(
        (prev) => ({
          ...prev,
          [name]:
            value,
        })
      );
    };

  const updateCustomer =
    async (e) => {
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
        setSaving(
          true
        );
        await api.put(
          "/customer/update",
          {
            ...formData,
            identifier,
            creditLimit:
              Number(
                formData.creditLimit
              ) || 0,
            balance:
              Number(
                formData.balance
              ) || 0,
          }
        );

        alert(
          "Customer updated successfully"
        );

        router.push(
          "/customer"
        );

      } catch (error) {
        console.log(error);

        alert(
          "Update failed"
        );
      } finally {
        setSaving(
          false
        );
      }
    };

  return (
    <Sidebar>
      <div className="min-h-screen bg-[#f4f6fb] py-10 px-4 flex justify-center items-start">
        <div className="w-full max-w-[800px] bg-white rounded-xl shadow-md border border-slate-100 p-8">
          <h1 className="text-2xl font-bold mb-8 text-blue-600 tracking-wide">
            Update Customer
          </h1>
          {loading && (
  <div className="text-center text-blue-600 font-medium mb-4">
    Loading customer data...
  </div>
)}
          <form
            onSubmit={
              updateCustomer
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
                  saving
                }
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium px-6 py-2.5 rounded-md shadow-sm transition-colors text-sm text-center disabled:opacity-70"
              >
                {saving
                  ? "Updating..."
                  : "Update Customer"}
              </button>

              <button
                type="button"
                onClick={() =>
                  router.push(
                    "/customer"
                  )
                }
                className="flex-1 border border-slate-200 hover:bg-slate-50 text-slate-600 font-medium px-6 py-2.5 rounded-md transition-colors text-sm text-center"
              >
                Back
              </button>
            </div>
          </form>
        </div>
      </div>
    </Sidebar>
  );
}