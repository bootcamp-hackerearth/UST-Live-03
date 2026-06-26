import { cookies } from "next/headers";

export async function POST(req) {
  try {
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const identifier = await req.text();
    const token = (await cookies()).get("token")?.value;

    const headers = {
      "Content-Type": "text/plain",
    };

    if (token) {
      headers.Cookie = `token=${token}`;
      headers.Authorization = `Bearer ${token}`;
    }

    const res = await fetch(`${baseUrl}/cart/get`, {
      method: "POST",
      headers,
      body: identifier,
    });

    const text = await res.text();
    return new Response(text || "null", {
      status: res.status,
      headers: {
        "Content-Type": res.headers.get("Content-Type") || "application/json",
      },
    });
  } catch (error) {
    console.error("cart/get proxy failed", error);
    return new Response("Proxy error", { status: 500 });
  }
}