import StatusPage from "@/components/StatusPage";

export default function ServerErrorPage() {
  return (
    <StatusPage
      statusCode="500"
      title="500 – Internal Server Error"
      message="Something went wrong on our side. Please try again later."
    />
  );
}
