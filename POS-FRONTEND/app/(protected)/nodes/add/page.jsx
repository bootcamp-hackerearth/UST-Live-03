"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import CommonAdd from "@/components/common/CommonAdd";
import { addItem } from "@/services/api";
import { useAuth } from "@/context/AuthContext";
import NodeFields from "@/components/node/NodeFields";

export default function AddNode() {

  const router = useRouter();

  const [identifier, setIdentifier] = useState("");
  const [description, setDescription] = useState("");
  const [path, setPath] = useState("");
  const [roles, setRoles] = useState([]);
  const { nodes,setNodes } = useAuth();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      setLoading(true);
      setError("");
      setSuccessMessage("");

      if (!description.trim()) {

        setError(
          "Description cannot be empty"
        );

        return;

      }

      if (roles.length === 0) {

        setError(
          "Please select at least one role"
        );

        return;

      }

      if (!path.startsWith("/")) {

        setError(
          "Path must start with /"
        );

        return;

      }

      const response = await addItem("node", {
        identifier,
        description,
        path,
        roles,
        status: true,
      });

      setNodes([
      ...nodes,
      response
    ]);

      setSuccessMessage(
        "Node created successfully"
      );

      setTimeout(() => {

        router.push("/nodes");

      }, 1000);

    } catch (error) {

      console.log(error);

      setError(
        error?.response?.data?.message ||
        "Failed to create node"
      );

    } finally {

      setLoading(false);

    }

  };

  return (

    <CommonAdd
      title="Add Node"
      subtitle="Create a new navigation node"
      identifier={identifier}
      setIdentifier={setIdentifier}
      description={description}
      setDescription={setDescription}
      loading={loading}
      error={error}
      successMessage={successMessage}
      onSubmit={handleSubmit}
      cancelPath="/nodes"
    >

      <NodeFields
        path={path}
        setPath={setPath}
        roles={roles}
        setRoles={setRoles}
       />

    </CommonAdd>

  );

}