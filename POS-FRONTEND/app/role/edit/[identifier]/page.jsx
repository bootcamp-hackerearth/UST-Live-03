"use client";
import { useParams } from "next/navigation";
import Update from "../../../../components/edit";

export default function EditRole() {
  const params = useParams();
  return (
    <Update
      identifier={decodeURIComponent(params.identifier)}
      apiPath="role"
      title="Role"
      showDescription={true}
    />
  );
}
