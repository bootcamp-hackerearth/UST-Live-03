"use client";
import { useRouter } from "next/navigation";

const quickStats = [
  { label: "Today Sales", value: "₹124,560", note: "+12.4% vs yesterday" },
  { label: "Orders", value: "86", note: "14 pending checkout" },
  { label: "Low Stock", value: "9", note: "Needs re-order today" },
  { label: "Active Staff", value: "12", note: "3 on the sales floor" },
];

const modules = [
  { label: "Products", href: "/product", description: "Manage SKUs, pricing, and stock." },
  { label: "Categories", href: "/category", description: "Organize your catalog." },
  { label: "Price Lists", href: "/price", description: "Update selling prices quickly." },
  { label: "Users", href: "/user", description: "Staff, roles, and access." },
  { label: "Nodes", href: "/node", description: "Route permissions and menus." },
];

export default function Dashboard() {
  const router = useRouter();

  return (
    <div className="w-full space-y-6">
      <section className="overflow-hidden rounded-[2rem] border border-white/70 bg-white/85 shadow-[0_24px_80px_rgba(15,23,42,0.12)] backdrop-blur">
        <div className="grid gap-0 lg:grid-cols-[1.35fr_1fr]">
          <div className="relative overflow-hidden bg-gradient-to-br from-slate-950 via-slate-900 to-blue-950 px-8 py-10 text-white lg:px-10">
            <div className="absolute inset-0 opacity-25">
              <div className="absolute -left-20 top-10 h-52 w-52 rounded-full bg-blue-500 blur-3xl" />
              <div className="absolute bottom-0 right-4 h-40 w-40 rounded-full bg-amber-400 blur-3xl" />
            </div>

            <div className="relative space-y-6">
              <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-blue-100">
                Retail POS Control Center
              </div>

              <div className="max-w-xl space-y-4">
                <h2 className="text-3xl font-black tracking-tight sm:text-4xl">
                  Welcome to your POS dashboard
                </h2>
                <p className="text-sm leading-6 text-slate-200 sm:text-base">
                  Keep an eye on sales, stock movement, staff activity, and catalog management from one clean workspace.
                </p>
              </div>

              <div className="flex flex-wrap gap-3">
                <button
                  onClick={() => router.push("/product")}
                  className="rounded-full bg-amber-400 px-5 py-3 text-sm font-bold text-slate-950 transition hover:bg-amber-300"
                >
                  Open Product Catalog
                </button>
                <button
                  onClick={() => router.push("/user")}
                  className="rounded-full border border-white/20 bg-white/5 px-5 py-3 text-sm font-semibold text-white transition hover:bg-white/10"
                >
                  Manage Staff
                </button>
              </div>
            </div>
          </div>

          <div className="grid gap-4 bg-stone-50 p-8 lg:grid-cols-2">
            {quickStats.map((item) => (
              <div
                key={item.label}
                className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm"
              >
                <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                  {item.label}
                </p>
                <p className="mt-3 text-3xl font-black text-slate-900">{item.value}</p>
                <p className="mt-2 text-sm text-slate-500">{item.note}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-[1.25fr_0.75fr]">
        <div className="rounded-[1.75rem] border border-slate-200 bg-white p-6 shadow-sm">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">
                Quick Modules
              </p>
              <h3 className="mt-2 text-2xl font-bold text-slate-900">
                Jump straight into POS operations
              </h3>
            </div>
            <p className="max-w-md text-sm text-slate-500">
              These shortcuts take you to the most common admin areas for managing the store.
            </p>
          </div>

          <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {modules.map((module, index) => (
              <button
                key={module.href}
                onClick={() => router.push(module.href)}
                className="group rounded-2xl border border-slate-200 bg-slate-50 p-5 text-left transition hover:-translate-y-0.5 hover:border-blue-200 hover:bg-blue-50"
              >
                <div className="flex items-center justify-between gap-4">
                  <div>
                    <p className="text-lg font-bold text-slate-900">{module.label}</p>
                    <p className="mt-2 text-sm text-slate-500">{module.description}</p>
                  </div>
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-white text-sm font-black text-blue-700 shadow-sm ring-1 ring-slate-200 transition group-hover:bg-blue-600 group-hover:text-white">
                    0{index + 1}
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>

        <aside className="rounded-[1.75rem] border border-slate-200 bg-slate-950 p-6 text-white shadow-sm">
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-300">
            Today&apos;s Floor View
          </p>
          <h3 className="mt-2 text-2xl font-bold">Sales floor snapshot</h3>
          <div className="mt-6 space-y-4">
            {[
              { label: "Checkout queue", value: "4 customers" },
              { label: "Reorder alert", value: "3 items below threshold" },
              { label: "Top category", value: "Fresh foods" },
            ].map((item) => (
              <div
                key={item.label}
                className="rounded-2xl border border-white/10 bg-white/5 px-4 py-4"
              >
                <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
                  {item.label}
                </p>
                <p className="mt-2 text-lg font-semibold text-white">{item.value}</p>
              </div>
            ))}
          </div>

          <button
            onClick={() => router.push("/price")}
            className="mt-6 w-full rounded-2xl bg-amber-400 px-5 py-3 text-sm font-bold text-slate-950 transition hover:bg-amber-300"
          >
            Update Pricing
          </button>
        </aside>
      </section>
    </div>
  );
}