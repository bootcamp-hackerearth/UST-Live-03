import StatusPage from "@/components/StatusPage";

export default function ForbiddenPage() {
  return (
    <StatusPage
      statusCode="403"
      title="403 – Access Denied"
      message="You do not have permission to access this resource."
    />
  );
}
