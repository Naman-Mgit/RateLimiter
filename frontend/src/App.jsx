import { useState } from "react";

function App() {

    const [isLogin, setIsLogin] = useState(true);

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [message, setMessage] = useState("");

    const handleSubmit = async (e) => {

        e.preventDefault();

        setMessage("");

        const endpoint = isLogin
            ? "http://localhost:8080/auth/login"
            : "http://localhost:8080/auth/register";

        try {

            const response = await fetch(endpoint, {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            const data = await response.json();

            if (!response.ok) {

                setMessage(
                    data.message || "Something went wrong"
                );

                return;
            }

            if (isLogin) {

                localStorage.setItem(
                    "token",
                    data.token
                );

                setMessage("Login successful!");

                console.log("JWT:", data.token);

            } else {

                setMessage(
                    "Registration successful! You can now login."
                );

                setIsLogin(true);
            }

        } catch (error) {

            console.error(error);

            setMessage(
                "Could not connect to the server"
            );
        }
    };


    return (
        <div className="container">

            <div className="card">

                <h1>
                    Rate Limiter
                </h1>

                <h2>
                    {isLogin ? "Login" : "Create Account"}
                </h2>

                <form onSubmit={handleSubmit}>

                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) =>
                            setUsername(e.target.value)
                        }
                    />

                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) =>
                            setPassword(e.target.value)
                        }
                    />

                    <button type="submit">
                        {isLogin ? "Login" : "Register"}
                    </button>

                </form>

                {message && (
                    <p className="message">
                        {message}
                    </p>
                )}

                <button
                    className="switch-button"
                    onClick={() => {
                        setIsLogin(!isLogin);
                        setMessage("");
                    }}
                >
                    {isLogin
                        ? "Don't have an account? Register"
                        : "Already have an account? Login"
                    }
                </button>

            </div>

        </div>
    );
}

export default App;