import PropTypes from "prop-types";
import MainLayout from "@/app/components/MainLayout";
import ProtectedRoute from "@/app/components/ProtectedRoute";

export default function ProtectedLayout({ children }) {
  return (
    <ProtectedRoute>
      <MainLayout>
        {children}
      </MainLayout>
    </ProtectedRoute>
  );
}

ProtectedLayout.propTypes = {
  children: PropTypes.node.isRequired,
};