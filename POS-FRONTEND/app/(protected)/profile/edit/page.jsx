"use client";

import {getCurrentUser} from "@/services/api";
import UserEditor from "@/components/user/UserEditor";

export default function EditProfile() {

  return (

    <UserEditor
      title="Edit Profile"
      subtitle="Update your profile"
      cancelPath="/profile"
      afterSaveRedirect="/profile"
      refreshCurrentUser={true}
      loadUser={getCurrentUser}
    />

  );

}