

"use client";
import { useRouter } from "next/navigation";
export default function Header() {
  const router = useRouter();
  return (
    <header
      className="
        sticky
        top-0
        z-40
        h-20
        bg-white
        border-b
        border-slate-200
        px-8
        flex
        items-center
        justify-between
      "
    >
      {/* LEFT */}

      <div>
        <h1 className="text-3xl font-bold text-slate-800">
          POS Dashboard
        </h1>

        <p className="text-sm text-slate-500">
          Inventory & Sales Management
        </p>
      </div>

      {/* RIGHT */}

      <div className="flex items-center gap-4">


        {/* PROFILE */}

        <button
          className="
    flex
    items-center
    gap-3
    pl-2
    cursor-pointer
  "
          onClick={() => router.push("/user/profile")}
        >
          <div className="text-right">

            <h4 className="font-semibold text-slate-800">
              Admin
            </h4>

            <p className="text-sm text-slate-500">
              Administrator
            </p>

          </div>

          <div
            className="
              h-12
              w-12
              rounded-full
              bg-gradient-to-r
              from-teal-500
              to-cyan-600
              text-white
              flex
              items-center
              justify-center
              font-bold
              text-lg
              shadow-md
            "
          >
            A
          </div>

        </button>

      </div>
    </header>
  );
}