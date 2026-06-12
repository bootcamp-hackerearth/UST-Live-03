import PropTypes from "prop-types";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import Footer from "./components/Footer";

export default async function DashboardLayout({ children }) {
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;
  const username = cookieStore.get("username")?.value || "User";

  if (!token) {redirect("/login");}

  let data = null;

  try {
    const response = await fetch("http://localhost:8080/api/home",
      {
        method: "GET",
        headers: {Authorization: `Bearer ${token}`},
        cache: "no-store",
      }
    );

    console.log("response status", response.status);

    if (response.status == 403) {
      redirect("/login");
    }
    data = await response.json();
  } catch (error) {
    console.error(error);
    redirect("/login");
  }

  const nodes = Array.isArray(data)
    ? data
    : data.nodes || [];

  return (
    <div className="min-h-screen bg-[#f4f6fb]">
      <Sidebar nodes={nodes} />
      <div className="ml-16 flex min-h-screen flex-col">
        <Header username={username} />
        <main className="flex-1 pt-16 px-8 pb-8">
          <div className="mx-auto w-full max-w-7xl">
            {children}
          </div>
        </main>
        <Footer />
      </div>
    </div>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node,
};