import { NextResponse } from "next/server";
 
export default function middleware(request) {
 
  const token = request.cookies.get("token")?.value;
 
  const publicPaths = ["/login", "/register", "/auth"];
 
  const isPublic = publicPaths.some(path =>
    request.nextUrl.pathname.startsWith(path)
  );
 
  if (!token && !isPublic) {
    return NextResponse.redirect(new URL("/login", request.url));
  }
  return NextResponse.next();
}
 
export const config = {
  matcher: [
    "/((?!_next/static|_next/image|public|favicon.ico|api).*)",
  ]
};
 