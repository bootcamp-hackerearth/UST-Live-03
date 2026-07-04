import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const { username } = await req.json();

    console.log("====== NAVBAR API ======");
    console.log("Username:", username);

    const baseUrl =
      process.env.NEXT_PUBLIC_BASE_URL ||
      "http://localhost:8080/api";

    console.log("Backend URL:", `${baseUrl}/user/${username}`);

    const res = await fetch(`${baseUrl}/user/${username}`);

    console.log("Backend Status:", res.status);

    const text = await res.text();

    console.log("Backend Response:", text);

    return new NextResponse(text, {
      status: res.status,
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (e) {
    console.error("NAVBAR API ERROR:", e);

    return NextResponse.json(
      {
        error: e.message,
      },
      {
        status: 500,
      }
    );
  }
}