import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";


// ==========================================
// Login Page
// ==========================================

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState("");

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();


    // ======================================
    // Handle Login
    // ======================================

    const handleSubmit = async (e) => {

        e.preventDefault();

        setMessage("");
        setMessageType("");
        setLoading(true);

        try {

            const response = await fetch(
                "http://localhost:8080/auth/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                }
            );

            const data = await response.json();

            if (!response.ok) {

                setMessage(
                    data.message || "Something went wrong"
                );
                setMessageType("error");

                return;
            }

            localStorage.setItem(
                "token",
                data.token
            );

            navigate("/dashboard");

        } catch (error) {

            console.error(error);

            setMessage(
                "Could not connect to the server"
            );
            setMessageType("error");

        } finally {

            setLoading(false);
        }
    };


    // ======================================
    // Render
    // ======================================

    return (

        <div className="auth-page">


            {/* Floating particles */}

            <div className="auth-particles">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
                <span></span>
            </div>


            <div className="auth-wrapper">


                {/* Glass Card */}

                <div className="auth-card">


                    {/* Brand */}

                    <div className="auth-brand">

                        <div className="auth-brand-icon">
                            ⚡
                        </div>

                        <span>
                            RateGuard
                        </span>

                    </div>


                    {/* Title */}

                    <h1 className="auth-title">
                        Welcome back
                    </h1>

                    <p className="auth-subtitle">
                        Sign in to your account to manage
                        your API keys and monitor usage.
                    </p>


                    {/* Messages */}

                    {message && (

                        <div className={
                            `auth-message ${messageType}`
                        }>

                            {messageType === "error"
                                ? "✕"
                                : "✓"
                            }

                            {" "}{message}

                        </div>

                    )}


                    {/* Form */}

                    <form
                        className="auth-form"
                        onSubmit={handleSubmit}
                    >

                        <div className="auth-input-group">

                            <input
                                id="login-username"
                                type="text"
                                placeholder="Username"
                                value={username}
                                onChange={(e) =>
                                    setUsername(e.target.value)
                                }
                                required
                            />

                            <label htmlFor="login-username">
                                Username
                            </label>

                        </div>


                        <div className="auth-input-group">

                            <input
                                id="login-password"
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(e.target.value)
                                }
                                required
                            />

                            <label htmlFor="login-password">
                                Password
                            </label>

                        </div>


                        <button
                            className="auth-submit"
                            type="submit"
                            disabled={loading}
                        >

                            {loading && (
                                <span className="btn-spinner">
                                </span>
                            )}

                            {loading
                                ? "Signing in..."
                                : "Sign In"
                            }

                        </button>

                    </form>


                    {/* Divider */}

                    <div className="auth-divider">
                        or
                    </div>


                    {/* Switch to register */}

                    <p className="auth-switch">
                        Don't have an account?{" "}
                        <Link to="/register">
                            Create one
                        </Link>
                    </p>

                </div>


                {/* Features below card */}

                <div className="auth-features">

                    <div className="auth-feature">
                        <span className="auth-feature-dot">
                        </span>
                        Token Bucket Algorithm
                    </div>

                    <div className="auth-feature">
                        <span className="auth-feature-dot">
                        </span>
                        Real-time Monitoring
                    </div>

                    <div className="auth-feature">
                        <span className="auth-feature-dot">
                        </span>
                        Secure API Keys
                    </div>

                </div>

            </div>

        </div>
    );
}


export default Login;
