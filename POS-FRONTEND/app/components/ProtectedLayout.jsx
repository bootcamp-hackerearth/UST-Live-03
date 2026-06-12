import PropTypes from "prop-types";
import MainLayout from "@/app/components/MainLayout";
import ProtectedRoute from "@/app/components/ProtectedRoute";
 
/**
 * ProtectedLayout Component
 * @param {React.ReactNode} children - Child components to render
 */
export default function ProtectedLayout({
    children,
})  {
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