import { Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";


// ==========================================
// App
// ==========================================

function App() {

    const token = localStorage.getItem("token");

    return (

        <Routes>

            {/* Login */}
            <Route
                path="/login"
                element={
                    token
                        ? <Navigate to="/dashboard" replace />
                        : <Login />
                }
            />

            {/* Register */}
            <Route
                path="/register"
                element={
                    token
                        ? <Navigate to="/dashboard" replace />
                        : <Register />
                }
            />

            {/* Dashboard (protected) */}
            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <Dashboard />
                    </ProtectedRoute>
                }
            />

            {/* Default: redirect to /dashboard */}
            <Route
                path="/"
                element={
                    <Navigate to="/dashboard" replace />
                }
            />

            {/* Unknown routes: redirect to /dashboard */}
            <Route
                path="*"
                element={
                    <Navigate to="/dashboard" replace />
                }
            />

        </Routes>
    );
}


export default App;