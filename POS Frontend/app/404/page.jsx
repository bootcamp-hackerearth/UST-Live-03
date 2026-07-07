import StatusPage from "@/components/StatusPage";

export default function NotFoundRoutePage() {
  return (
    <StatusPage
      statusCode="404"
      title="404 – Page Not Found"
      message="The page or resource you are looking for does not exist."
    />
  );
}
