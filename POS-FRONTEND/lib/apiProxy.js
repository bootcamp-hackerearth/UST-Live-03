import { cookies } from "next/headers";

export async function proxyRoute(req, endpoint) {
  try {
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const body = await req.json();
    const token = (await cookies()).get("token")?.value;

    console.log(`[${endpoint}] Request body:`, JSON.stringify(body));
    console.log(`[${endpoint}] Token found:`, !!token);

    const headers = {
      "Content-Type": "application/json",
    };

    if (token) {
      headers.Cookie = `token=${token}`;
      console.log(`[${endpoint}] Adding Cookie header with token`);
    }

    console.log(`[${endpoint}] Headers being sent:`, headers);

    const res = await fetch(`${baseUrl}/${endpoint}`, {
      method: "POST",
      headers,
      body: JSON.stringify(body),
    });

    console.log(`[${endpoint}] Response status:`, res.status);
    const text = await res.text();
    console.log(`[${endpoint}] Response text (first 200 chars):`, text.substring(0, 200));

    return new Response(text || "null", {
      status: res.status,
      headers: {
        "Content-Type": res.headers.get("Content-Type") || "application/json",
      },
    });
  } catch (error) {
    console.error(`[${endpoint}] Proxy failed:`, error);
    return new Response("Proxy error", { status: 500 });
  }
}
