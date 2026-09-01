import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";


// ==========================================
// Register Page
// ==========================================

function Register() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState("");

    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();


    // ======================================
    // Frontend Validation
    // ======================================

    const validate = () => {

        if (username.length < 3) {
            setMessage("Username must be at least 3 characters.");
            setMessageType("error");
            return false;
        }

        if (username.length > 50) {
            setMessage("Username must be at most 50 characters.");
            setMessageType("error");
            return false;
        }

        if (password.length < 6) {
            setMessage("Password must be at least 6 characters.");
            setMessageType("error");
            return false;
        }

        if (password.length > 100) {
            setMessage("Password must be at most 100 characters.");
            setMessageType("error");
            return false;
        }

        return true;
    };


    // ======================================
    // Handle Register
    // ======================================

    const handleSubmit = async (e) => {

        e.preventDefault();

        setMessage("");
        setMessageType("");

        if (!validate()) {
            return;
        }

        setLoading(true);

        try {

            const response = await fetch(
                "http://localhost:8080/auth/register",
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

            setMessage(
                "Registration successful! Redirecting to login..."
            );
            setMessageType("success");

            setTimeout(() => {
                navigate("/login");
            }, 1500);

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
                        Create Account
                    </h1>

                    <p className="auth-subtitle">
                        Sign up to start managing your API keys
                        with intelligent rate limiting.
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
                                id="register-username"
                                type="text"
                                placeholder="Username"
                                value={username}
                                onChange={(e) =>
                                    setUsername(e.target.value)
                                }
                                minLength={3}
                                maxLength={50}
                                required
                            />

                            <label htmlFor="register-username">
                                Username
                            </label>

                        </div>


                        <div className="auth-input-group">

                            <input
                                id="register-password"
                                type="password"
                                placeholder="Password"
                                value={password}
                                onChange={(e) =>
                                    setPassword(e.target.value)
                                }
                                minLength={6}
                                maxLength={100}
                                required
                            />

                            <label htmlFor="register-password">
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
                                ? "Creating account..."
                                : "Create Account"
                            }

                        </button>

                    </form>


                    {/* Divider */}

                    <div className="auth-divider">
                        or
                    </div>


                    {/* Switch to login */}

                    <p className="auth-switch">
                        Already have an account?{" "}
                        <Link to="/login">
                            Sign in
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


export default Register;
