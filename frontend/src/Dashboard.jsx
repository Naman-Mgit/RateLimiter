import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";


// ==========================================
// Get username from JWT
// ==========================================

function getUsernameFromToken() {

    const token = localStorage.getItem("token");

    if (!token) {
        return "User";
    }

    try {

        const payload = token.split(".")[1];

        const decodedPayload = JSON.parse(
            atob(
                payload
                    .replace(/-/g, "+")
                    .replace(/_/g, "/")
            )
        );

        return decodedPayload.sub || "User";

    } catch (error) {

        console.error(
            "Could not decode JWT",
            error
        );

        return "User";
    }
}


// ==========================================
// Dashboard
// ==========================================

function Dashboard() {

    // ======================================
    // API Key states
    // ======================================

    const [apiKeys, setApiKeys] = useState([]);

    const [loading, setLoading] = useState(true);

    const [creating, setCreating] = useState(false);

    const [error, setError] = useState("");

    const [message, setMessage] = useState("");


    // ======================================
    // API Tester states
    // ======================================

    const [selectedKey, setSelectedKey] = useState("");

    const [remainingTokens, setRemainingTokens] =
        useState(null);

    // Actual value received from backend
    const [serverTokens, setServerTokens] =
        useState(null);

    // Time when backend gave us token count
    const [lastRequestTime, setLastRequestTime] =
        useState(null);

    const [testingApi, setTestingApi] =
        useState(false);

    const [apiResponse, setApiResponse] =
        useState("");


    // ======================================
    // JWT
    // ======================================

    const token = localStorage.getItem("token");

    const username = getUsernameFromToken();

    const navigate = useNavigate();


    // ======================================
    // Fetch API Keys
    // ======================================

    useEffect(() => {

        const fetchApiKeys = async () => {

            try {

                const response = await fetch(
                    "http://localhost:8080/api-keys",
                    {
                        method: "GET",

                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    }
                );

                const data = await response.json();

                if (!response.ok) {

                    setError(
                        data.message ||
                        "Failed to load API keys"
                    );

                    return;
                }

                setApiKeys(data);

            } catch (error) {

                console.error(error);

                setError(
                    "Could not connect to server"
                );

            } finally {

                setLoading(false);
            }
        };

        fetchApiKeys();

    }, [token]);


    // ======================================
    // Automatic Token Refill
    // ======================================

    useEffect(() => {

    if (
        serverTokens === null ||
        lastRequestTime === null ||
        serverTokens >= 5
    ) {
        return;
    }

    const interval = setInterval(() => {

        const elapsedSeconds =
            Math.floor(
                (Date.now() - lastRequestTime) / 1000
            );

        const newTokens = Math.min(
            5,
            serverTokens + elapsedSeconds
        );

        setRemainingTokens(newTokens);

        if (newTokens >= 5) {
            clearInterval(interval);
        }

    }, 250);

    return () => {
        clearInterval(interval);
    };

}, [serverTokens, lastRequestTime]);


    // ======================================
    // Test API
    // ======================================

    const testApi = async () => {

        if (!selectedKey) {

            setError(
                "Please select an active API key first."
            );

            return;
        }


        setTestingApi(true);

        setError("");

        setMessage("");

        setApiResponse("");


        try {

            const response = await fetch(
                "http://localhost:8080/api/resource",
                {
                    method: "GET",

                    headers: {
                        "X-API-KEY": selectedKey
                    }
                }
            );


            const data = await response.json();


            // Show backend message
            setApiResponse(data.message);


            // Store authoritative server value
            setServerTokens(
                data.remainingTokens
            );


            // Immediately display server value
            setRemainingTokens(
                data.remainingTokens
            );


            // Start refill timer from this moment
            setLastRequestTime(
                Date.now()
            );


            // Handle errors such as 429
            if (!response.ok) {

                setError(
                    data.message ||
                    "API request failed"
                );
            }

        } catch (error) {

            console.error(error);

            setError(
                "Could not connect to server"
            );

        } finally {

            setTestingApi(false);
        }
    };


    // ======================================
    // Create API Key
    // ======================================

    const createApiKey = async () => {

        setCreating(true);

        setError("");

        setMessage("");


        try {

            const response = await fetch(
                "http://localhost:8080/api-keys",
                {
                    method: "POST",

                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                }
            );


            const data = await response.json();


            if (!response.ok) {

                setError(
                    data.message ||
                    "Failed to create API key"
                );

                return;
            }


            // Add new key to current list
            setApiKeys((previousKeys) => [
                ...previousKeys,
                data
            ]);


            setMessage(
                "API key created successfully!"
            );

        } catch (error) {

            console.error(error);

            setError(
                "Could not connect to server"
            );

        } finally {

            setCreating(false);
        }
    };


    // ======================================
    // Revoke API Key
    // ======================================

    const revokeApiKey = async (id) => {

        const confirmed = window.confirm(
            "Are you sure you want to revoke this API key?"
        );


        if (!confirmed) {
            return;
        }


        setError("");

        setMessage("");


        try {

            const response = await fetch(
                `http://localhost:8080/api-keys/${id}`,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                }
            );


            if (!response.ok) {

                let data = {};

                try {
                    data = await response.json();
                } catch {
                    // Response may not contain JSON
                }


                setError(
                    data.message ||
                    "Failed to revoke API key"
                );

                return;
            }


            // Mark key as revoked
            setApiKeys((previousKeys) =>
                previousKeys.map((key) =>
                    key.id === id
                        ? {
                            ...key,
                            active: false
                        }
                        : key
                )
            );


            // If currently selected key was revoked
            const revokedKey =
                apiKeys.find(
                    (key) => key.id === id
                );


            if (
                revokedKey &&
                revokedKey.keyValue === selectedKey
            ) {

                setSelectedKey("");

                setRemainingTokens(null);

                setServerTokens(null);

                setLastRequestTime(null);

                setApiResponse("");
            }


            setMessage(
                "API key revoked successfully!"
            );

        } catch (error) {

            console.error(error);

            setError(
                "Could not connect to server"
            );
        }
    };


    // ======================================
    // Logout
    // ======================================

    const logout = () => {

        localStorage.removeItem("token");

        navigate("/login");
    };


    // ======================================
    // Loading
    // ======================================

    if (loading) {

        return (
            <div className="loading-screen">

                <div className="loader">
                </div>

                <p>
                    Loading your dashboard...
                </p>

            </div>
        );
    }


    // ======================================
    // Stats
    // ======================================

    const activeKeys = apiKeys.filter(
        (key) => key.active
    ).length;


    // ======================================
    // Dashboard UI
    // ======================================

    return (

        <div className="dashboard">


            {/* ==================================
                HEADER
            ================================== */}

            <header className="navbar">

                <div className="brand">

                    <div className="brand-icon">
                        ⚡
                    </div>

                    <span>
                        RateGuard
                    </span>

                </div>


                <div className="nav-right">

                    <span className="username">
                        {username}
                    </span>


                    <button
                        className="logout-button"
                        onClick={logout}
                    >
                        Logout
                    </button>

                </div>

            </header>


            {/* ==================================
                MAIN
            ================================== */}

            <main className="dashboard-content">


                {/* ==================================
                    WELCOME
                ================================== */}

                <section className="welcome-section">

                    <div>

                        <h1>
                            Welcome back 👋
                        </h1>

                        <p>
                            Manage your API keys and
                            monitor your API usage.
                        </p>

                    </div>

                </section>


                {/* ==================================
                    STATS
                ================================== */}

                <section className="stats">


                    <div className="stat-card">

                        <div className="stat-icon">
                            🔑
                        </div>

                        <div>

                            <p>
                                Total API Keys
                            </p>

                            <h2>
                                {apiKeys.length}
                            </h2>

                        </div>

                    </div>


                    <div className="stat-card">

                        <div className="stat-icon">
                            ✓
                        </div>

                        <div>

                            <p>
                                Active Keys
                            </p>

                            <h2>
                                {activeKeys}
                            </h2>

                        </div>

                    </div>


                    <div className="stat-card">

                        <div className="stat-icon">
                            ⚡
                        </div>

                        <div>

                            <p>
                                Rate Limit
                            </p>

                            <h2>
                                5 req/sec
                            </h2>

                        </div>

                    </div>

                </section>


                {/* ==================================
                    API TESTER
                ================================== */}

                <section className="api-tester">


                    <div className="section-header">

                        <div>

                            <h2>
                                API Rate Limit
                            </h2>

                            <p>
                                Test your API and monitor
                                your token bucket.
                            </p>

                        </div>

                    </div>


                    <div className="tester-card">


                        {/* API selector */}

                        <div className="tester-top">


                            <div>

                                <span className="tester-label">
                                    Select API Key
                                </span>


                                <select
                                    value={selectedKey}
                                    onChange={(e) => {

                                        const newKey =
                                            e.target.value;

                                        setSelectedKey(
                                            newKey
                                        );

                                        // Reset old key's
                                        // token information
                                        setRemainingTokens(
                                            null
                                        );

                                        setServerTokens(
                                            null
                                        );

                                        setLastRequestTime(
                                            null
                                        );

                                        setApiResponse("");

                                        setError("");

                                    }}
                                >

                                    <option value="">
                                        Choose an active API key
                                    </option>


                                    {apiKeys
                                        .filter(
                                            (key) =>
                                                key.active
                                        )
                                        .map((key) => (

                                            <option
                                                key={key.id}
                                                value={key.keyValue}
                                            >
                                                {key.keyValue}
                                            </option>

                                        ))
                                    }

                                </select>

                            </div>


                            <button
                                className="test-button"
                                onClick={testApi}
                                disabled={
                                    testingApi ||
                                    !selectedKey
                                }
                            >

                                {testingApi
                                    ? "Testing..."
                                    : "Test API"
                                }

                            </button>

                        </div>


                        {/* ==================================
                            TOKEN DISPLAY
                        ================================== */}

                        {remainingTokens !== null && (

                            <div className="rate-display">


                                <div className="rate-header">

                                    <span>
                                        Remaining Tokens
                                    </span>


                                    <strong>
                                        {remainingTokens} / 5
                                    </strong>

                                </div>


                                <div className="progress-background">

                                    <div
                                        className="progress-bar"
                                        style={{
                                            width:
                                                `${(
                                                    remainingTokens /
                                                    5
                                                ) * 100}%`
                                        }}
                                    >
                                    </div>

                                </div>


                                <p className="rate-info">

                                    {remainingTokens >= 5
                                        ? "Token bucket is full."
                                        : remainingTokens === 0
                                            ? "Rate limit reached. Tokens are refilling automatically."
                                            : "Tokens refill at 1 token per second."
                                    }

                                </p>

                            </div>

                        )}


                        {/* ==================================
                            API RESPONSE
                        ================================== */}

                        {apiResponse && (

                            <div className="api-response">

                                <span>
                                    API Response
                                </span>


                                <code>
                                    {apiResponse}
                                </code>

                            </div>

                        )}

                    </div>

                </section>


                {/* ==================================
                    API KEYS
                ================================== */}

                <section className="keys-section">


                    <div className="section-header">


                        <div>

                            <h2>
                                Your API Keys
                            </h2>

                            <p>
                                Create and manage your
                                API credentials.
                            </p>

                        </div>


                        <button
                            className="create-button"
                            onClick={createApiKey}
                            disabled={
                                creating ||
                                activeKeys >= 2
                            }
                        >

                            {creating
                                ? "Creating..."
                                : "+ Create API Key"
                            }

                        </button>

                    </div>


                    {/* ==================================
                        MESSAGES
                    ================================== */}

                    {message && (

                        <div className="success-message">

                            ✓ {message}

                        </div>

                    )}


                    {error && (

                        <div className="error-message">

                            {error}

                        </div>

                    )}


                    {/* ==================================
                        API KEY LIST
                    ================================== */}

                    {apiKeys.length === 0 ? (

                        <div className="empty-state">


                            <div className="empty-icon">
                                🔑
                            </div>


                            <h3>
                                No API keys yet
                            </h3>


                            <p>
                                Create your first API key
                                to start using the API.
                            </p>


                            <button
                                className="create-button"
                                onClick={createApiKey}
                            >
                                Create your first key
                            </button>

                        </div>

                    ) : (

                        <div className="key-list">


                            {apiKeys.map((apiKey) => (

                                <div
                                    className="key-card"
                                    key={apiKey.id}
                                >


                                    <div className="key-top">


                                        <div>


                                            <span
                                                className={
                                                    apiKey.active
                                                        ? "status active"
                                                        : "status revoked"
                                                }
                                            >

                                                <span className="status-dot">
                                                </span>


                                                {apiKey.active
                                                    ? "ACTIVE"
                                                    : "REVOKED"
                                                }

                                            </span>


                                            <div className="api-key">

                                                {apiKey.keyValue}

                                            </div>

                                        </div>


                                        {apiKey.active && (

                                            <button
                                                className="revoke-button"
                                                onClick={() =>
                                                    revokeApiKey(
                                                        apiKey.id
                                                    )
                                                }
                                            >
                                                Revoke
                                            </button>

                                        )}

                                    </div>


                                    <div className="key-details">


                                        <div>

                                            <span>
                                                Created
                                            </span>

                                            <strong>
                                                {formatDate(
                                                    apiKey.createdAt
                                                )}
                                            </strong>

                                        </div>


                                        <div>

                                            <span>
                                                Expires
                                            </span>

                                            <strong>
                                                {formatDate(
                                                    apiKey.expiresAt
                                                )}
                                            </strong>

                                        </div>


                                        <div>

                                            <span>
                                                Last Used
                                            </span>

                                            <strong>

                                                {apiKey.lastUsedAt
                                                    ? formatDate(
                                                        apiKey.lastUsedAt
                                                    )
                                                    : "Never"
                                                }

                                            </strong>

                                        </div>

                                    </div>

                                </div>

                            ))}

                        </div>

                    )}

                </section>

            </main>

        </div>
    );
}


// ==========================================
// Date formatting helper
// ==========================================

function formatDate(date) {

    if (!date) {
        return "N/A";
    }


    return new Date(date).toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );
}


export default Dashboard;