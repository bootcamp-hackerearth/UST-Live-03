import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    const paramName = body.paramName || "identifier";

    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;

    const response = await fetch(
      `http://localhost:8080${body.endpoint}?${paramName}=${encodeURIComponent(
        body.identifier
      )}`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    const text = await response.text();   
    const data = text === "true";         

    return NextResponse.json({
      success: response.ok,
      data: data,
    });

  } catch (error) {
    console.error("DELETE API ERROR:", error);

    return NextResponse.json(
      { message: error.message },
      { status: 500 }
    );
  }
}