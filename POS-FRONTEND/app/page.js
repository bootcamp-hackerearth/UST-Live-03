import { cookies } from "next/headers";
import { redirect } from "next/navigation";
 
const RootPage = async () => {
  const cookieStore = await cookies();
 
  const token = cookieStore.get("token")?.value;
  const username = cookieStore.get("username")?.value;
 
  if (token && username) {
    redirect("/Dashboard");
  } else {
    redirect("/Login");
  }
};
 
export default RootPage;