"use client";
import {useState} from "react";
import {useRouter} from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import WarehouseFields from "@/components/warehouse/WarehouseFields";
import {addItem} from "@/services/api";
import {validatePhone} from "@/utils/validation";

export default function AddWarehouse() {
  const router = useRouter();
  const [identifier,setIdentifier] = useState("");
  const [description,setDescription] = useState("");
  const [country,setCountry] = useState("");
  const [region,setRegion] = useState("");
  const [address,setAddress] = useState("");
  const [phoneNumber,setPhoneNumber] = useState("");
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");

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
      await addItem("warehouse",{identifier,description,country,region,address,phoneNumber});
      setSuccessMessage("Warehouse added successfully");
      setTimeout(() => router.push("/warehouses"),1000);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to add warehouse");
    } finally {
      setLoading(false);
    }
  };

  return (
    <CommonAdd
      title="Add Warehouse"
      subtitle="Create a new warehouse"
      identifier={identifier}
      setIdentifier={setIdentifier}
      description={description}
      setDescription={setDescription}
      showDescription={false}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/warehouses"
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
    </CommonAdd>
  );
}