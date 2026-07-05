export async function POST(req) {

  try {
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const body = await req.json();
    const res = await fetch(`${baseUrl}/user/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    let data;
  
    try {
      data = await res.json();
    } catch (err) {
      console.log("Failed to parse backend response:", err)
      return new Response("Invalid backend response", { status: 500 });
    }

    if (!res.ok || data?.status === "Error") {
      return new Response("Registration failed", { status: 400 });
    }

    return new Response(JSON.stringify(data), {
      status: 200,
      headers: {
        "Content-Type": "application/json",
      },
    });

  } catch (err) {
    console.log(err);
    return new Response("Error", { status: 500 });
  }
}