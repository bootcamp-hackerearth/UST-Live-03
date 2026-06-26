"use client";
import {useEffect,useState} from "react";
import {useRouter,useParams} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import {getItem,updateItem} from "@/services/api";
import MultiCheckBox from "@/components/common/MultiCheckBox";

export default function EditRack() {
  
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const [description,setDescription] = useState("");
  const [shelves,setShelves] = useState([]);
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});
  useEffect(() => {
    loadRack();
  },[]);
  const loadRack = async () => {
    try {
      setPageLoading(true);
      setError("");
      const response = await getItem("rack",identifier);
      setDescription(response.description || "");
      setShelves(response.shelves || []);
      setAuditData({
        createdBy: response.createdBy,
        createdOn: response.createdOn,
        modifiedBy: response.modifiedBy,
        modifiedOn: response.modifiedOn,
      });
    } catch (error) {
      console.log(error);
      setError("Failed to load rack");
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
      if (shelves.length === 0) {
        setError("Please select at least one shelf");
        return;
      }
      await updateItem("rack",{identifier,description,shelves});
      setSuccessMessage("Rack updated successfully");
      setTimeout(() => {
        router.push("/racks");
      },1000);
    } catch (error) {
      console.log(error);
      setError(error?.response?.data?.message || "Failed to update rack");
    } finally {
      setLoading(false);
    }
  };
  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading rack...</p>
      </div>
    );
  }
  return (
    <CommonEdit
      title="Edit Rack"
      subtitle="Update rack details"
      identifier={identifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/racks"
      auditData={auditData}
    >
      <MultiCheckBox
        label="Shelves"
        model="shelf"
        values={shelves}
        onChange={setShelves}
        required={true}
      />
    </CommonEdit>
  );
}