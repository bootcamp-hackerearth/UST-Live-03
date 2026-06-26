import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;
    const response = await fetch(`http://localhost:8080${body.endpoint}?identifier=${encodeURIComponent(
        body.identifier)}`,
      {
        method: "PATCH",
        headers: {Authorization: `Bearer ${token}`},
      }
    );

    const text = await response.text();

    return NextResponse.json(
      {success: response.ok, response: text},
      {status: response.status});
    } catch (error) {

    return NextResponse.json(
      {message: error.message},
      {status: 500});
  }
}