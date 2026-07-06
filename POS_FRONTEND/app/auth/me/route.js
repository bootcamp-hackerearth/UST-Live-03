import { cookies } from "next/headers";
import { NextResponse } from "next/server";

export async function GET() {
    try {
        const cookieStore = await cookies();
        const token = cookieStore.get("token")?.value;

        return NextResponse.json({
            authenticated: Boolean(token),
            token: token || null,
        });
    } catch (err) {
        console.error("ME ERROR:", err);
        return NextResponse.json({ authenticated: false }, { status: 500 });
    }
}
