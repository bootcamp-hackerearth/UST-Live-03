import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function DELETE(req) {
  try {
    const body = await req.json();

    const {
      endpoint,
      identifier,
      paramName = "identifier",
    } = body;

    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;
    const url = `http://localhost:8080${endpoint}?${paramName}=${encodeURIComponent(identifier)}`;
    console.log("Forwarding Request to Backend URL:", url);

    const response = await fetch(url, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const text = await response.text();
    const data = text === "true";

    return NextResponse.json({
      success: response.ok && data, 
    });

  } catch (error) {
    console.error("DELETE API ERROR:", error);

    return NextResponse.json(
      { success: false, message: error.message },
      { status: 500 }
    );
  }
}