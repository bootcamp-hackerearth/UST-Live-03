import { cookies } from "next/headers";

export async function POST(req) {
  try {
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const body = await req.json();
    const token = (await cookies()).get("token")?.value;

    const normalizedBody = {
      ...body,
      orginalPrice: Number(body?.orginalPrice ?? body?.totalPrice ?? 0),
      totalPrice: Number(body?.totalPrice ?? body?.orginalPrice ?? 0),
    };

    const headers = {
      "Content-Type": "application/json",
    };

    if (token) {
      headers.Cookie = `token=${token}`;
      headers.Authorization = `Bearer ${token}`;
    }

    const res = await fetch(`${baseUrl}/cartEntry/add`, {
      method: "POST",
      headers,
      body: JSON.stringify(normalizedBody),
    });

    const text = await res.text();
    return new Response(text || "null", {
      status: res.status,
      headers: {
        "Content-Type": res.headers.get("Content-Type") || "application/json",
      },
    });
  } catch (error) {
    console.error("cartEntry/add proxy failed", error);
    return new Response("Proxy error", { status: 500 });
  }
}
