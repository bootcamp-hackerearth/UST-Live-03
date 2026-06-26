import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import SalesPage from "./SalesPage";

export default async function Sales() {
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;

  if (!token) {
    redirect("/login");
  }

  return (
    <div>
      <SalesPage />
    </div>
  );
}
