import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    console.log("Authenticate request body:", body);

    const backendUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080"}/api/authenticate`;
    console.log("Calling backend at:", backendUrl);

    const backendResponse = await fetch(backendUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    console.log("Backend response status:", backendResponse.status);

    const data = await backendResponse.json();
    console.log("Backend response data:", data);

    if (!backendResponse.ok) {
      return NextResponse.json(
        { error: data.error || "Authentication failed" },
        { status: backendResponse.status }
      );
    }

    return NextResponse.json(data);
  } catch (err) {
    console.error("Authentication error:", err);
    return NextResponse.json(
      { error: err.message || "Server error" },
      { status: 500 }
    );
  }
}
