import { NextResponse } from "next/server";

export const middleware = (request) => {
  const { pathname } = request.nextUrl;

  if (
    pathname.startsWith("/api") ||
    pathname.startsWith("/_next") ||
    pathname === "/favicon.ico"
  ) {
    return NextResponse.next();
  }

  if (pathname === "/login" || pathname === "/register") {
    return NextResponse.next();
  }

 const token = request.cookies.get("token")?.value;
console.log("Middleware token:", token);

  if (!token) {
    console.log("[Middleware] No token found, redirecting to login");
    return NextResponse.redirect(new URL("/login", request.url));
  }

  console.log(
    "[Middleware] Token found, allowing access to",
    pathname
  );

  return NextResponse.next();
};

export const config = {
  matcher: [
    "/((?!_next/static|_next/image|favicon.ico).*)",
  ],
};
