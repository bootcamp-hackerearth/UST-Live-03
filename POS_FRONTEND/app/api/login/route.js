import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function POST(req) {
    try {
        const body = await req.json();

        if (!body.token) {
            return NextResponse.json({ error: "Token missing" }, { status: 400 });
        }

        const cookieStore = await cookies();
        cookieStore.set("token", body.token, {
            httpOnly: true,
            secure: process.env.NODE_ENV === "production",
            sameSite: "lax",
            path: "/",
            maxAge: 60 * 60 * 24,
        });

        return NextResponse.json({ ok: true });
    } catch (err) {
        console.error("SERVER ERROR:", err);
        return NextResponse.json({ error: "Server error" }, { status: 500 });
    }
}
