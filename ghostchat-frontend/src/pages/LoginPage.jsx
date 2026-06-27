import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/LoginPage.css";
import { API_URL } from "../config/api";

function LoginPage(){
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("token");

        if(token){
            navigate("/rooms");
        }
    }, [navigate]);

    const login = async () => {
        if(!email.trim() || !password.trim()){
            alert("Please enter email and password");
            return;
        }
        try{
            const response = await fetch(`${API_URL}/api/auth/login`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        email,
                        password
                    })
                }
            );

            const data = await response.json();

            if(data.token){
                localStorage.setItem("token", data.token);
                localStorage.setItem("username", data.username);
                localStorage.setItem("email", data.email);

                const pendingRoom = localStorage.getItem("pendingRoom");

                if(pendingRoom){
                    localStorage.removeItem("pendingRoom");
                    navigate(`/chat/${pendingRoom}`);
                } else {
                    navigate("/rooms");
                }
                
            } else {
                alert("Invalid email or password");
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
                    Ephemeral • Private • Secure Messaging
                </p>

                <input 
                    type="email"
                    placeholder="email" 
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />

                <input 
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

                <button className="login-btn" onClick={login}>Login</button>

                <div className="divider"></div>

                <button className="register-btn" onClick={() => navigate("/register")}>Create Account</button>

                <div className="login-footer">Messages disappear forever after everyone leaves the room</div>
            </div>
        </div>
    );
}

export default LoginPage;