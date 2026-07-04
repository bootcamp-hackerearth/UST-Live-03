import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const { username } = await req.json();

    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;

    console.log("Token exists:", !!token);

    const baseUrl =
      process.env.NEXT_PUBLIC_BASE_URL ||
      "http://localhost:8080/api";

    const headers = {};

    if (token) {
      headers.Cookie = `token=${token}`;
    }

    const res = await fetch(`${baseUrl}/user/${username}`, {
      headers,
    });

    const text = await res.text();

    return new NextResponse(text, {
      status: res.status,
      headers: {
        "Content-Type": "application/json",
      },
    });

  } catch (e) {
    console.error(e);

    return NextResponse.json(
      { error: e.message },
      { status: 500 }
    );
  }
}