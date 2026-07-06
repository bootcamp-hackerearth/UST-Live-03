import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;
    console.log("=== PROXY DEBUG START ===");
    console.log("Token retrieved from cookies:", token ? "FOUND (Starts with " + token.substring(0, 10) + "...)" : "MISSING (UNDEFINED)");
    console.log("Outgoing Payload:", JSON.stringify(body.payload));

    if (!token) {
      console.warn("BLOCKED: Request intercepted in Next.js proxy due to missing token cookie.");
      return NextResponse.json(
        { message: "Authentication failed: No active session token found." },
        { status: 403 }
      );
    }

    const response = await fetch("http://localhost:8080/api/order/checkout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(body.payload),
    });

    console.log("Spring Boot Response Status:", response.status);
    console.log("=== PROXY DEBUG END ===");

    const responseText = await response.text();
    let data = responseText ? JSON.parse(responseText) : {};

    if (!response.ok) {
      return NextResponse.json(
        { message: data.message || `Checkout failed with status ${response.status}` },
        { status: response.status }
      );
    }

    return NextResponse.json(data, { status: 200 });
  } catch (error) {
    console.error("CHECKOUT PROXY ERROR:", error);
    return NextResponse.json(
      { message: error.message || "Internal Server Error" },
      { status: 500 }
    );
  }
}