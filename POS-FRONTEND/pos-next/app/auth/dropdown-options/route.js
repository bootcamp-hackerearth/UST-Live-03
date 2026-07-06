import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();

    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;

    console.log("TOKEN EXISTS:", !!token);

    const response = await fetch(`http://localhost:8080${body.endpoint}`,
  {method: body.method || "GET",
    headers: {"Content-Type": "application/json",
    Authorization: `Bearer ${token}`},
    body:
      body.method === "POST"
        ? JSON.stringify(body.payload || {})
        : undefined,
  }
);
    const text = await response.text();

    let data = {};

    try {
      data = JSON.parse(text);
    } catch {
      console.log( "NOT JSON RESPONSE");
    }
    let options = [];

if (Array.isArray(data)) {
  options = data;
}
else if (data.dtoList) {
  options = data.dtoList;
}
else {
  options = Object.values(data).find(
      (value) => Array.isArray(value)) || [];
}

    return NextResponse.json({options});
  } catch (error) {
    console.error("DROPDOWN ERROR:", error);

    return NextResponse.json(
      {message: error.message},
      {status: 500});
  }
}