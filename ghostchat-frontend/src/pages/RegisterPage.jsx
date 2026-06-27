import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";
import { API_URL } from "../config/api";

function RegisterPage() {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [username, setUsername] = useState("");

    const navigate = useNavigate();

    const handleRegister = async () => {
        if(!username.trim() || !email.trim() || !password.trim()){
            alert("Please fill all the fields");
            return;
        }
        try {
            const response = await fetch(`${API_URL}/api/auth/register`,
                {
                    method: "POST",
                    headers: {
                        "content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        username,
                        email,
                        password
                    })
                }
            );
            if(response.ok){
                alert("Registration Successful");
                navigate("/");
            } else {
                alert("Registration Failed");
            }
        } catch(error){
            console.error(error);
            alert("Server Error");
        }
    };

    return (
    <div className="login-page">
        <div className="login-card">

            <h1>GhostChat</h1>

            <p className="login-subtitle">
                Create your secure account
            </p>

            <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />

            <input
                type="text"
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />

            <button
                className="login-btn"
                onClick={handleRegister}
            >
                Create Account
            </button>

            <div className="divider"></div>

            <button
                className="register-btn"
                onClick={() => navigate("/")}
            >
                Back to Login
            </button>

            <div className="login-footer">
                Private • Anonymous • Ephemeral Messaging
            </div>

        </div>
    </div>
);
}

export default RegisterPage;