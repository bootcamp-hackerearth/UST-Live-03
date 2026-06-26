"use client";
import {useEffect,useState} from "react";
import {useRouter,useParams} from "next/navigation";
import CommonEdit from "@/components/common/CommonEdit";
import {getItem,updateItem} from "@/services/api";
import {useAuth} from "@/context/AuthContext";
import NodeFields from "@/components/node/NodeFields";

export default function EditNode() {
  const router = useRouter();
  const params = useParams();
  const identifier = decodeURIComponent(params.identifier);
  const {loadUserData} = useAuth();
  const [description,setDescription] = useState("");
  const [path,setPath] = useState("");
  const [roles,setRoles] = useState([]);
  const [loading,setLoading] = useState(false);
  const [pageLoading,setPageLoading] = useState(true);
  const [error,setError] = useState("");
  const [successMessage,setSuccessMessage] = useState("");
  const [auditData,setAuditData] = useState({});

  useEffect(() => {
    const loadNode = async () => {
      try {
        setPageLoading(true);
        setError("");
        const response = await getItem("node",identifier);
        setDescription(response.description || "");
        setPath(response.path || "");
        setRoles(response.roles || []);
        setAuditData({
          createdBy: response.createdBy,
          createdOn: response.createdOn,
          modifiedBy: response.modifiedBy,
          modifiedOn: response.modifiedOn,
        });
      } catch (err) {
        console.log(err);
        setError("Failed to load node");
      } finally {
        setPageLoading(false);
      }
    };
    loadNode();
  },[identifier]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccessMessage("");
      if (!description.trim()) {
        setError("Description cannot be empty");
        return;
      }
      if (roles.length === 0) {
        setError("Please select at least one role");
        return;
      }
      if (!path.startsWith("/")) {
        setError("Path must start with /");
        return;
      }
      await updateItem("node",{identifier,description,path,roles});
      await loadUserData();
      setSuccessMessage("Node updated successfully");
      setTimeout(() => router.push("/nodes"),1000);
    } catch (err) {
      console.log(err);
      setError(err?.response?.data?.message || "Failed to update node");
    } finally {
      setLoading(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <p className="text-[#667085]">Loading node...</p>
      </div>
    );
  }

  return (
    <CommonEdit
      title="Edit Node"
      subtitle="Update navigation node"
      identifier={identifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/nodes"
      auditData={auditData}
    >
      <NodeFields
        path={path}
        setPath={setPath}
        roles={roles}
        setRoles={setRoles}
      />
    </CommonEdit>
  );
}