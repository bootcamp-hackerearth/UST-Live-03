import RegisterForm from "./RegisterForm";

export default async function RegisterPage() {

  const roles = await getRoles();

  return (
    <RegisterForm roles={roles} />
  )
}
async function getRoles() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

  try {
    const res = await fetch(`${baseUrl}/role/list`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        page: 0,
        sizePerPage: 100,
      }),
      cache: "no-store",
    });

    const text = await res.text();

    if (!text) {
      console.log("Empty response from backend");
      return [];
    }

    const data = JSON.parse(text);

    return data?.content || [];
  } catch (err) {
    console.log("Error", err);
    return [];
  }
}