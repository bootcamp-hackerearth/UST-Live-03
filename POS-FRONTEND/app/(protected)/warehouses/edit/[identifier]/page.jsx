"use client";
import {useEffect,useState} from "react";
import {useRouter,useParams} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import WarehouseFields from "@/components/warehouse/WarehouseFields";
import {getItem,updateItem} from "@/services/api";
import {validatePhone} from "@/utils/validation";

export default function EditWarehouse() {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const [description,setDescription] = useState("");
  const [country,setCountry] = useState("");
  const [region,setRegion] = useState("");
  const [address,setAddress] = useState("");
  const [phoneNumber,setPhoneNumber] = useState("");
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});

  useEffect(() => {
    const loadWarehouse = async () => {
      try {
        setPageLoading(true);
        setError("");
        const response = await getItem("warehouse",identifier);
        setDescription(response.description || "");
        setCountry(response.country || "");
        setRegion(response.region || "");
        setAddress(response.address || "");
        setPhoneNumber(response.phoneNumber || "");
        setAuditData({
          createdBy: response.createdBy,
          createdOn: response.createdOn,
          modifiedBy: response.modifiedBy,
          modifiedOn: response.modifiedOn,
        });
      } catch (err) {
        console.log(err);
        setError("Failed to load warehouse");
      } finally {
        setPageLoading(false);
      }
    };
    loadWarehouse();
  },[identifier]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");
      if (!country.trim() || !region.trim() || !address.trim()) {
        setError("Country, region and address are required");
        return;
      }
      if (phoneNumber && !validatePhone(phoneNumber)) {
        setError("Phone number must be exactly 10 digits");
        return;
      }
      await updateItem("warehouse",{identifier,description,country,region,address,phoneNumber});
      setSuccessMessage("Warehouse updated successfully");
      setTimeout(() => router.push("/warehouses"),1000);
    } catch (err) {
      console.log(err);
      setError(err?.response?.data?.message || "Failed to update warehouse");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading warehouse...</p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Warehouse"
      subtitle="Update warehouse details"
      identifier={identifier}
      description={description}
      setDescription={setDescription}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/warehouses"
      auditData={auditData}
    >
      <WarehouseFields
        country={country}
        setCountry={setCountry}
        region={region}
        setRegion={setRegion}
        address={address}
        setAddress={setAddress}
        phoneNumber={phoneNumber}
        setPhoneNumber={setPhoneNumber}
      />
    </CommonEdit>
  );
}