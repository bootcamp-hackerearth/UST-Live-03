import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();

    const backendUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL || "/api"}/role/list`;
    console.log("Fetching roles from:", backendUrl);

    const backendResponse = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    const data = await backendResponse.json();

    if (!backendResponse.ok) {
      console.error("Backend error response:", data);
      return NextResponse.json(
        { content: [], error: data.error || "Failed to fetch roles" },
        { status: backendResponse.status }
      );
    }

    return NextResponse.json(data);
  } catch (err) {
    console.error("Role list error:", err.message || err);
    return NextResponse.json(
      { content: [], error: err.message || "Server error" },
      { status: 500 }
    );
  }
}