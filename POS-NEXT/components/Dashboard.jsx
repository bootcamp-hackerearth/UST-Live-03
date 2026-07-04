import { Poppins, Orbitron } from "next/font/google";
import {
  Package,
  Users,
  ShoppingCart,
  AlertTriangle,
  Plus,
  Receipt,
} from "lucide-react";

const poppins = Poppins({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
});

const orbitron = Orbitron({
  subsets: ["latin"],
  weight: ["600", "700", "800"],
});

export default function DashboardPage() {
  const stats = [
    {
      title: "Products",
      value: "248",
      icon: Package,
    },
    {
      title: "Customers",
      value: "102",
      icon: Users,
    },
    {
      title: "Orders",
      value: "58",
      icon: ShoppingCart,
    },
    {
      title: "Low Stock",
      value: "7",
      icon: AlertTriangle,
    },
  ];

  return (
    <div
      className={`${poppins.className} min-h-screen bg-linear-to-br from-white via-violet-50 to-violet-100 p-8`}
    >
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-12">
          <h1
            className={`${orbitron.className} text-5xl font-extrabold bg-linear-to-r from-violet-600 to-indigo-500 bg-clip-text text-transparent`}
          >
            Welcome
          </h1>

          <p className="mt-4 text-gray-600 text-lg">
            Manage your products, customers, inventory and orders from one
            place.
          </p>

          <div className="w-20 h-1 bg-violet-500 rounded-full mx-auto mt-6" />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((item) => {
            const Icon = item.icon;

            return (
              <div
                key={item.title}
                className="bg-white rounded-2xl shadow-sm border border-violet-100 p-6 hover:shadow-md transition"
              >
                <div className="flex justify-between items-center">
                  <div>
                    <p className="text-gray-500 text-sm">{item.title}</p>

                    <h2 className="text-3xl font-bold text-gray-800 mt-2">
                      {item.value}
                    </h2>
                  </div>

                  <div className="bg-violet-100 p-3 rounded-xl">
                    <Icon className="w-6 h-6 text-violet-600" />
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        <div className="mt-12 bg-white rounded-2xl shadow-sm border border-violet-100 p-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-5">
            Quick Actions
          </h2>

          <div className="flex flex-wrap gap-4">
            <button className="flex items-center gap-2 bg-violet-600 hover:bg-violet-700 text-white px-5 py-3 rounded-xl transition">
              <Plus size={18} />
              New Sale
            </button>

            <button className="flex items-center gap-2 border border-violet-200 hover:bg-violet-50 px-5 py-3 rounded-xl transition">
              <Receipt size={18} />
              View Orders
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}