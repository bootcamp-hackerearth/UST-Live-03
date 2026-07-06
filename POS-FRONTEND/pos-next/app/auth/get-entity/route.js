import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;
    const paramName = body.paramName || "identifier";

    const response = await fetch(`http://localhost:8080${body.endpoint}?${paramName}=${encodeURIComponent(
          body.identifier)}`,
        {method: "GET", headers: {Authorization: `Bearer ${token}`}});

    const data = await response.json();

    return NextResponse.json(
      {success: response.ok, data,},
      {status: response.status});
  } catch (error) {console.error( "GET ENTITY ERROR:", error);

    return NextResponse.json(
      {message: error.message},
      {status: 500});
  }
}