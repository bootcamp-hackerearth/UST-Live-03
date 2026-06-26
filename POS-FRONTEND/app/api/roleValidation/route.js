import { cookies } from "next/headers";

export async function POST(req) {
    try {

        const cookieStore = await cookies();
        const token = cookieStore.get("token")
        
        const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
        const body = await req.json();

        const res = await fetch(`${baseUrl}/roleValidation`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Cookie: `token=${token?.value}`
            },
            body: JSON.stringify({ ...body, token: token?.value }),
        });

        let data;
        try {
            data = await res.json();
            return new Response(JSON.stringify(data), {
                status: 200,
                headers: {
                    "Content-Type": "application/json",
                },
            });

        } catch (err) {
            console.error("failed", err);
            return new Response("Invalid backend response", { status: 500 });
        }

    } catch (err) {
        console.error("SERVER ERROR:", err);
        return new Response("Server error", { status: 500 });
    }
}


