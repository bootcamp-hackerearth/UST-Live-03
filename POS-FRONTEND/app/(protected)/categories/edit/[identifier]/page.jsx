"use client";
import {useEffect,useState} from "react";
import {useParams,useRouter} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import SingleDropDown from "@/components/common/SingleDropDown";
import {getItem,updateItem} from "@/services/api";

export default function EditCategory() {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const [superCategory,setSuperCategory] = useState("");
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});

  useEffect(() => {
    const loadCategory = async () => {
      try {
        setPageLoading(true);
        const response = await getItem("category", identifier);
        setSuperCategory(response.superCategory || "");
        setAuditData({
          createdBy: response.createdBy,
          createdOn: response.createdOn,
          modifiedBy: response.modifiedBy,
          modifiedOn: response.modifiedOn,
        });
      } catch (err) {
        console.log(err);
        setError("Failed to load category");
      } finally {
        setPageLoading(false);
      }
    };
    loadCategory();
  },[identifier]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");
      const response = await updateItem("category",{identifier,superCategory});
      if (!response.success) {
        setError(response.message || "Failed to update category");
        return;
      }
      setSuccessMessage("Category updated successfully");
      setTimeout(() => router.push("/categories"),1000);
    } catch (err) {
      console.log(err);
      setError(err?.response?.data?.message || "Failed to update category");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading category...</p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Category"
      subtitle="Update category details"
      identifier={identifier}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/categories"
      auditData={auditData}
    >
      <SingleDropDown
        label="Super Category"
        model="category"
        value={superCategory}
        onChange={setSuperCategory}
        placeholder="Select Super Category"
      />
    </CommonEdit>
  );
}