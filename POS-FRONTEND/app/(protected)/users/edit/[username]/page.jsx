"use client";

import {useParams} from "next/navigation";
import {getItem} from "@/services/api";
import UserEditor from "@/components/user/UserEditor";

export default function EditUser() {

  const params = useParams();

  const routeUsername =
    decodeURIComponent(
      params.username
    );

  return (

    <UserEditor
      title="Edit User"
      subtitle="Update user details"
      cancelPath="/users"
      afterSaveRedirect="/users"
      refreshCurrentUserIfEditingSelf={true}
      loadUser={() =>
        getItem(
          "user",
          routeUsername
        )
      }
    />

  );

}