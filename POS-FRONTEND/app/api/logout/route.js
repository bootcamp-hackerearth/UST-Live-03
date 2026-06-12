import { cookies } from "next/headers";
 
export async function POST() {
 
  const cookieStore = await cookies();
 
  cookieStore.set("token", "", {
    httpOnly: true,
    secure: false,
    sameSite: "strict",
    path: "/",
    expires: new Date(0),
  });
 
  return new Response("Logged out", { status: 200 });
}