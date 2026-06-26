import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;

    console.log("Endpoint:", body.endpoint);
    console.log("Payload:", body.payload);

    const response = await fetch(
      `http://localhost:8080${body.endpoint}`,
      {
        method: body.method || "POST",
        headers: { "Content-Type": "application/json",
        Authorization: `Bearer ${token}`},
        body: JSON.stringify(body.payload),
      }
    );

    const responseText = await response.text();
    console.log("Backend Status:", response.status);
    console.log("Backend Response:", responseText);

    let responseData;

    try {
      responseData = JSON.parse(responseText);
    } catch {
      responseData = {message: responseText,};}

    return NextResponse.json(responseData, {status: response.status,});

  } catch (error) {
    console.error( "ADD/UPDATE ERROR:", error);
    return NextResponse.json(
      {message: error.message || "Internal Server Error",},
      {status: 500,}
    );
  }
}