"use client";

import {getCurrentUser} from "@/services/api";
import UserEditor from "@/components/user/UserEditor";
import { useEffect, useState } from "react";

export default function EditProfile() {

  const [auditData, setAuditData] = useState({});

  useEffect(() => {

    loadUserData();

  }, []);

  const loadUserData = async () => {
    try {
      const user = await getCurrentUser();
      setAuditData({
        createdBy: user.createdBy,
        createdOn: user.createdOn,
        modifiedBy: user.modifiedBy,
        modifiedOn: user.modifiedOn,
      });
    } catch (error) {
      console.error("Error loading user data:", error);
    }
  };

  return (

    <UserEditor
      title="Edit Profile"
      subtitle="Update your profile"
      cancelPath="/profile"
      afterSaveRedirect="/profile"
      refreshCurrentUser={true}
      loadUser={getCurrentUser}
      auditData={auditData}
    />

  );

}