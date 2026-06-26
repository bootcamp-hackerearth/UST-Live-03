"use client";

import { useRouter } from "next/navigation";

export default function Dashboard1() {
  const router = useRouter();

  const cards = [
    {
      title: "New Sale",
      description: "Create and manage cart items",
      icon: "🛒",
      path: "/cart",
    },
    {
      title: "Orders",
      description: "View and track orders",
      icon: "📦",
      path: "/order",
    },
    {
      title: "Stock",
      description: "Check inventory levels",
      icon: "📋",
      path: "/stock",
    },
    {
      title: "Customers",
      description: "Manage customers",
      icon: "👥",
      path: "/customer",
    },
  ];

  return (
    <div className="space-y-6">
      {/* WELCOME CARD */}
      <div
        className="
          rounded-3xl
          bg-gradient-to-r
          from-teal-700
          to-cyan-700
          text-white
          p-10
          shadow-lg
        "
      >
        <h1 className="text-4xl font-bold">
          Welcome Back 👋
        </h1>

        <p className="mt-3 text-lg text-teal-100">
          Monitor sales, inventory and customers from one place.
        </p>
      </div>

      {/* DASHBOARD CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
        {cards.map((card) => (
          <button
            key={card.title}
            onClick={() => router.push(card.path)}
            className="
              bg-white
              rounded-3xl
              p-8
              shadow-sm
              border
              border-slate-200
              hover:shadow-lg
              hover:-translate-y-1
              transition-all
              text-left
            "
          >
            <div className="flex items-center gap-6">
              <div className="text-5xl">
                {card.icon}
              </div>

              <div>
                <h2 className="text-2xl font-bold text-slate-900">
                  {card.title}
                </h2>

                <p className="mt-2 text-slate-500">
                  {card.description}
                </p>
              </div>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}