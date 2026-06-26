"use client";
import {useState} from "react";
import {useRouter} from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import {addItem} from "@/services/api";
import MultiCheckBox from "@/components/common/MultiCheckBox";
export default function AddRack() {
  const router = useRouter();
  const [identifier,setIdentifier] = useState("");
  const [description,setDescription] = useState("");
  const [shelves,setShelves] = useState([]);
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
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
      await addItem("rack",{identifier,description,shelves});
      setSuccessMessage("Rack added successfully");
      setTimeout(() => {
        router.push("/racks");
      },1000);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to add rack");
    } finally {
      setLoading(false);
    }
  };
  return (
    <CommonAdd
      title="Add Rack"
      subtitle="Create a new rack"
      identifier={identifier}
      setIdentifier={setIdentifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/racks"
    >
      <MultiCheckBox
        label="Shelves"
        model="shelf"
        values={shelves}
        onChange={setShelves}
        required={true}
      />
    </CommonAdd>
  );
}