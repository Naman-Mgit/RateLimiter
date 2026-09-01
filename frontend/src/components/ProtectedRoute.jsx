import { Navigate } from "react-router-dom";


// ==========================================
// Protected Route
// ==========================================

function ProtectedRoute({ children }) {

    const token = localStorage.getItem("token");

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    return children;
}


export default ProtectedRoute;
