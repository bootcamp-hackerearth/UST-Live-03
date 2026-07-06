import { NextResponse } from "next/server";

export async function POST(req) {
  try {
    const body = await req.json();
    const backendResponse = await fetch("http://localhost:8080/api/authenticate",
      {method: "POST", headers: {"Content-Type": "application/json"},body: JSON.stringify(body),});

    if (!backendResponse.ok) {
      return NextResponse.json(
        {message: "Invalid credentials"},
        {status: 401});}

    const data = await backendResponse.json();

    if (!data.token || data.token === "Error") {
      return NextResponse.json(
        {message: "Invalid credentials",},
        {status: 401,}
      );
    }

    const response = NextResponse.json({success: true});

    response.cookies.set("token", data.token, {
      httpOnly: true,
      secure: false,
      path: "/",
      maxAge: 60 * 60 * 24});

    response.cookies.set("username", body.username, {
      httpOnly: false,
      secure: false,
      path: "/",
      maxAge: 60 * 60 * 24});

    return response;

  } catch (error) {
    console.error("LOGIN ERROR:", error);

    return NextResponse.json(
      {message: "Login failed",},
      {status: 500,});
  }
}