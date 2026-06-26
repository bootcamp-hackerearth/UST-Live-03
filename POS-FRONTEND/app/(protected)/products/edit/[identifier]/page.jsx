"use client";
import {useEffect,useState} from "react";
import {useRouter,useParams} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import {getItem,updateItem} from "@/services/api";
import ProductFields from "@/components/product/ProductFields";

export default function EditProduct() {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const [description,setDescription] = useState("");
  const [brand,setBrand] = useState("");
  const [model,setModel] = useState("");
  const [unit,setUnit] = useState("");
  const [category,setCategory] = useState([]);
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});
  useEffect(() => {
    loadProduct();
  }, []);
  const loadProduct = async () => {
    try {
      setPageLoading(true);
      setError("");
      const response = await getItem("product",identifier);
      setDescription(response.description || "");
      setBrand(response.brand || "");
      setModel(response.model || "");
      setUnit(response.unit || "");
      setCategory(response.category || []);
      setAuditData({
        createdBy: response.createdBy,
        createdOn: response.createdOn,
        modifiedBy: response.modifiedBy,
        modifiedOn: response.modifiedOn,
      });
    } catch (error) {
      console.log(error);
      setError("Failed to load product");
    } finally {
      setPageLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");
      if (category.length === 0) {
        setError("Please select at least one category");
        return;
      }
      await updateItem("product",{identifier,description,brand,model,unit,category});
      setSuccessMessage("Product updated successfully");
      setTimeout(() => {
        router.push("/products");
      },1000);
    } catch (error) {
      console.log(error);
      setError(error?.response?.data?.message || "Failed to update product");
    } finally {
      setLoading(false);
    }
  };
  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading product...</p>
      </div>
    );
  }
  return (
    <CommonEdit
      title="Edit Product"
      subtitle="Update product details"
      identifier={identifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/products"
      auditData={auditData}
    >
      <ProductFields
        brand={brand}
        setBrand={setBrand}
        model={model}
        setModel={setModel}
        unit={unit}
        setUnit={setUnit}
        category={category}
        setCategory={setCategory}
      />
    </CommonEdit>
  );
}