import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import PropTypes from "prop-types";
import Layout from "@/app/components/Layout";

const Home = async () => {
  const cookieStore = await cookies();

  const token = cookieStore.get("token")?.value;
  const username = cookieStore.get("username")?.value;

  if (!token || !username) {
    redirect("/login");
  }

  return (
    <Layout username={username}>
      <WelcomeDashboard username={username} />
    </Layout>
  );
};

const WelcomeDashboard = ({username}) => {
  const displayName = username ? String(username).split("@")[0] : "Operator";
  const greeting = getGreeting();

  const stats = [
    {
      label: "System Status",
      value: "Online",
      sub: "All services running",
      color: "emerald",
      icon: (
        <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
      ),
    },
    {
      label: "Session",
      value: "Active",
      sub: "Authenticated & secured",
      color: "blue",
      icon: (
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
      ),
    },
    {
      label: "Today",
      value: new Date().toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" }),
      sub: new Date().toLocaleDateString("en-GB", { weekday: "long" }),
      color: "violet",
      icon: (
        <path strokeLinecap="round" strokeLinejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      ),
    },
    {
      label: "Version",
      value: "v1.0.0",
      sub: "RetailPOS Console",
      color: "slate",
      icon: (
        <path strokeLinecap="round" strokeLinejoin="round" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
      ),
    },
  ];

  return (
    <div className="w-full space-y-6">

      <div className="relative overflow-hidden rounded-2xl bg-slate-900 p-7 text-white shadow-lg shadow-slate-900/20">
      
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff06_1px,transparent_1px),linear-gradient(to_bottom,#ffffff06_1px,transparent_1px)] bg-[size:28px_28px]" />
        <div className="absolute -top-10 -right-10 w-48 h-48 rounded-full bg-white/5 blur-2xl" />
        <div className="absolute -bottom-8 -left-8 w-36 h-36 rounded-full bg-white/5 blur-2xl" />

        <div className="relative z-10">
          <p className="text-slate-400 text-sm font-medium mb-1">{greeting}</p>
          <h1 className="text-3xl font-bold tracking-tight" style={{ color: "#ffffff" }}>
            Welcome back, <span className="capitalize" style={{ color: "#ffffff" }}>{displayName}</span>
          </h1>
          <p className="text-slate-400 text-sm mt-2 max-w-md">
            You're now logged into the RetailPOS Management Console. Use the sidebar to navigate your assigned modules.
          </p>
          <div className="mt-5 pt-5 border-t border-white/10 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
            <span className="text-xs font-medium text-slate-400">System operational · All nodes reachable</span>
          </div>
        </div>
      </div>
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {stats.map((stat) => (
          <StatCard key={stat.label} {...stat} />
        ))}
      </div>
      <div className="bg-white rounded-2xl border border-slate-200 p-6">
        <h2 className="text-sm font-bold text-slate-800 mb-1">Quick Start</h2>
        <p className="text-xs text-slate-400 mb-4">Select a module from the sidebar to begin. Here's a reminder of what each area covers.</p>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {[
            { icon: "🛒", title: "Sales / POS", desc: "Process transactions, apply discounts, print receipts." },
            { icon: "📦", title: "Inventory", desc: "Track stock levels, manage products and categories." },
            { icon: "👥", title: "Customers", desc: "View customer profiles, purchase history and loyalty." },
            { icon: "📊", title: "Reports", desc: "Analyse revenue, trends and generate summaries." },
          ].map((item) => (
            <div key={item.title} className="flex items-start gap-3 p-3 rounded-xl bg-slate-50 border border-slate-100 hover:border-blue-100 hover:bg-blue-50/30 transition-colors">
              <span className="text-xl leading-none mt-0.5">{item.icon}</span>
              <div>
                <p className="text-xs font-semibold text-slate-800">{item.title}</p>
                <p className="text-[11px] text-slate-400 mt-0.5 leading-relaxed">{item.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

    </div>
  );
};

WelcomeDashboard.propTypes = {
  username: PropTypes.string,
};

WelcomeDashboard.defaultProps = {
  username: "",
};



const colorMap = {
  emerald: { bg: "bg-emerald-50", border: "border-emerald-100", icon: "text-emerald-500", value: "text-emerald-600" },
  blue: { bg: "bg-blue-50", border: "border-blue-100", icon: "text-blue-500", value: "text-blue-600" },
  violet: { bg: "bg-violet-50", border: "border-violet-100", icon: "text-violet-500", value: "text-violet-600" },
  slate: { bg: "bg-slate-50", border: "border-slate-200", icon: "text-slate-400", value: "text-slate-700" },
};

const StatCard = ({ label, value, sub, color, icon }) => {
  const c = colorMap[color] || colorMap.slate;
  return (
    <div className={`rounded-xl border p-4 flex flex-col gap-2 ${c.bg} ${c.border}`}>
      <div className={`w-8 h-8 rounded-lg bg-white border ${c.border} flex items-center justify-center`}>
        <svg className={`w-4 h-4 ${c.icon}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.8}>
          {icon}
        </svg>
      </div>
      <div>
        <p className={`text-sm font-bold leading-tight ${c.value}`}>{value}</p>
        <p className="text-[10px] font-semibold uppercase tracking-wide text-slate-400 mt-0.5">{label}</p>
        <p className="text-[10px] text-slate-400 mt-0.5">{sub}</p>
      </div>
    </div>
  );
};

StatCard.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.node]).isRequired,
  sub: PropTypes.string,
  color: PropTypes.string,
  icon: PropTypes.node,
};

StatCard.defaultProps = {
  sub: "",
  color: "slate",
  icon: null,
};



function getGreeting() {
  const h = new Date().getHours();
  if (h < 12) return "Good morning 👋";
  if (h < 17) return "Good afternoon 👋";
  return "Good evening 👋";
}

export default Home;