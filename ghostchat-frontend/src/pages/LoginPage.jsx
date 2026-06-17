import { useState } from "react";
import { useNavigate } from "react-router-dom";

function LoginPage(){
    const [username, setUsername] = useState("");
    const navigate = useNavigate();

    const login = () => {
        localStorage.setItem("username", username);
        navigate("/rooms")
    };

    return (
        <div>
            <h1>GhostChat Login</h1>
            <input 
                placeholder="Username" 
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />

            <button onClick={login}>Continue</button>
        </div>
    );
}

export default LoginPage;