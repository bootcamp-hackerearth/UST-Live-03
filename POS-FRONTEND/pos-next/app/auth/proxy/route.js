import { cookies } from "next/headers";
import { NextResponse } from "next/server";

const BACKEND_BASE_URL = "https://localhost:8080";

const parseBackendResponse = async (backendResponse) => {
  const responseText = await backendResponse.text();

  if (!responseText) {
    return { parsed: null, raw: "" };
  }

  try {
    return { parsed: JSON.parse(responseText), raw: responseText };
  } catch {
    return { parsed: null, raw: responseText };
  }
};

const createNextResponse = (backendResponse, parsed, raw) => {
  const contentType = backendResponse.headers.get("content-type") || "application/json";

  if (parsed !== null) {
    return NextResponse.json(parsed, { status: backendResponse.status });
  }

  return new NextResponse(raw, {
    status: backendResponse.status,
    headers: { "Content-Type": contentType },
  });
};

const getTokenFromCookies = async () => {
  const cookieStore = await cookies();
  return cookieStore.get("token")?.value;
};

export async function POST(req) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;
    const body = await req.json();
    const endpoint = body?.endpoint;
    const method = (body?.method || "POST").toUpperCase();

    if (!endpoint) {
      return NextResponse.json({ message: "Missing endpoint in proxy request" }, { status: 400 });
    }

    const response = await fetch(`${BACKEND_BASE_URL}${endpoint}`, {
      method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: method === "GET" || method === "HEAD" ? undefined : JSON.stringify(body?.payload ?? {}),
    });

    const { parsed, raw } = await parseBackendResponse(response);
    return createNextResponse(response, parsed, raw);
  } catch (error) {
    console.error("PROXY ERROR:", error);
    return NextResponse.json({ message: error.message || "Proxy request failed" }, { status: 500 });
  }
}

export async function GET(req) {
  try {
    const token = await getTokenFromCookies();
    const url = new URL(req.url);
    const endpoint = url.searchParams.get("endpoint");

    if (!endpoint) {
      return NextResponse.json({ message: "Missing endpoint query parameter" }, { status: 400 });
    }

    const response = await fetch(`${BACKEND_BASE_URL}${endpoint}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const { parsed, raw } = await parseBackendResponse(response);
    return createNextResponse(response, parsed, raw);
  } catch (error) {
    console.error("PROXY GET ERROR:", error);
    return NextResponse.json({ message: error.message || "Proxy GET failed" }, { status: 500 });
  }
}
