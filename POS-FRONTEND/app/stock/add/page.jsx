"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AddPage from "@/components/common/AddPage";
import api from "@/services/api";

const StockAddPage = () => {
  const router = useRouter();
  const [options, setOptions] = useState({
    productIdentifier: [],
    warehouseIdentifier: [],
  });
  const [loadingOptions, setLoadingOptions] = useState(true);

  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [productsRes, warehousesRes] = await Promise.all([
          api.get("/product/active"),
          api.get("/warehouse/active"),
        ]);

        setOptions({
          productIdentifier: productsRes.data.map((p) => ({
            identifier: p.identifier,
            label: p.name,
          })),
          warehouseIdentifier: warehousesRes.data.map((w) => ({
            identifier: w.identifier,
            label: w.name,
          })),
        });
      } catch (err) {
        console.error("Failed to load dropdown options", err);
      } finally {
        setLoadingOptions(false);
      }
    };

    loadOptions();
  }, []);

  if (loadingOptions) return null;

  return (
    <AddPage
      title="Add Stock"
      modelName="stock"
      options={options}
      initialForm={{
        productIdentifier: "",
        warehouseIdentifier: "",
        availableQuantity: 0,
        reorderLevel: 0,
        status: true,
      }}
      fields={[
        {
          name: "productIdentifier",
          type: "select",
          label: "Product",
          required: true,
        },
        {
          name: "warehouseIdentifier",
          type: "select",
          label: "Warehouse",
          required: true,
        },
        {
          name: "availableQuantity",
          type: "number",
          label: "Available Quantity",
          required: true,
        },
        {
          name: "reorderLevel",
          type: "number",
          label: "Reorder Level",
        },
        {
          name: "status",
          type: "status",
          label: "Active",
        },
      ]}
      validate={(form) => {
        const errors = {};

        if (!form.productIdentifier)
          errors.productIdentifier = "Product is required";

        if (!form.warehouseIdentifier)
          errors.warehouseIdentifier = "Warehouse is required";

        if (form.availableQuantity < 0)
          errors.availableQuantity = "Quantity cannot be negative";

        if (form.reorderLevel < 0)
          errors.reorderLevel = "Reorder level cannot be negative";

        return errors;
      }}
      onSuccess={() => router.push("/stock/list")}
      onCancel={() => router.push("/stock/list")}
    />
  );
};

export default StockAddPage;