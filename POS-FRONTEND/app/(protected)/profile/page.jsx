"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function ProfilePage() {

  const router = useRouter();

  const { user, loading } = useAuth();

  if (loading) {

    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-xl font-semibold">
          Loading Profile...
        </div>
      </div>
    );

  }

  return (
    <div className="max-w-5xl mx-auto">

      <div className="bg-white rounded-4xl border border-gray-200 shadow-sm overflow-hidden">

        <div className="h-40 bg-white"></div>

        <div className="px-10 pb-10">

          <div className="-mt-16 flex items-end justify-between flex-wrap gap-6">

            <div className="flex items-center gap-6">

              <div className="h-32 w-32 rounded-full border-8 border-white bg-blue-600 flex items-center justify-center text-5xl font-bold text-white shadow-xl">
                {user?.name?.charAt(0)?.toUpperCase()}
              </div>

              <div className="pb-3">

                <h1 className="text-4xl font-bold text-black">
                  {user?.name}
                </h1>

                <p className="text-gray-500 mt-2 text-lg">
                  {user?.username}
                </p>

              </div>

            </div>

            <button
              onClick={() =>
                router.push("/profile/edit")
              }
              className="bg-blue-600 hover:bg-blue-500 text-white px-7 py-4 rounded-2xl font-semibold transition-all shadow-lg"
            >
              Edit Profile
            </button>

          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-12">

            <div className="bg-[#f8fafc] border border-gray-200 rounded-3xl p-7">

              <p className="text-sm text-gray-500 font-medium">
                Phone Number
              </p>

              <h2 className="text-2xl font-bold text-gray-900 mt-3">
                {user?.phoneNo}
              </h2>

            </div>

            <div className="bg-[#f8fafc] border border-gray-200 rounded-3xl p-7">

              <p className="text-sm text-gray-500 font-medium">
                Email Address
              </p>

              <h2 className="text-2xl font-bold text-gray-900 mt-3 break-all">
                {user?.username}
              </h2>

            </div>

          </div>

          <div className="mt-10">

            <p className="text-sm text-gray-500 font-medium mb-4">
              Roles
            </p>

            <div className="flex flex-wrap gap-3">

              {
                user?.roles?.map(
                  (role) => (

                    <div
                      key={role}
                      className="px-5 py-3 rounded-2xl bg-blue-100 text-blue-700 font-semibold"
                    >
                      {role}
                    </div>

                  )
                )
              }

            </div>

          </div>

        </div>

      </div>

    </div>
  );

}