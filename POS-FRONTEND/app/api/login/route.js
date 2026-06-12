import { cookies } from "next/headers";
 
export async function POST(req) {
  try {
 
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const body = await req.json();
    const res = await fetch(`${baseUrl}/authenticate`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });
 
    let data;
    try {
      data = await res.json();
    } catch (err) {
      console.error("failed", err);
      return new Response("Invalid backend response", { status: 500 });
    }
 
    if (!res.ok || data.token === "Error") {
      return new Response("Invalid login", { status: 401 });
    }
 
    const cookieStore = await cookies();
 
    cookieStore.set("token", data.token, {
      httpOnly: true,
      secure: false,
      sameSite: "strict",
      path: "/",
    });
    return new Response("OK", { status: 200 });
 
  } catch (err) {
    console.error("SERVER ERROR:", err);
    return new Response("Server error", { status: 500 });
  }
}
 
 
 