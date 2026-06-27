import { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { API_URL } from "../config/api";

function InvitePage() {
    const { roomCode } = useParams();
    const navigate = useNavigate();


    useEffect(() => {

    const handleInvite = async () => {

        const token = localStorage.getItem("token");

        const params = new URLSearchParams(window.location.search);
        const inviteToken = params.get("token");

        if(inviteToken){
            const response = await fetch(
                `${API_URL}/api/rooms/validate/${inviteToken}`
            );

            if (!response.ok) {
                alert("Invalid invite");
                navigate("/");
                return;
            }

            const valid = await response.json();

            if(!valid){
                alert("Invite already used");
                navigate("/");
                return;
            }
        }

        if(!token){
            localStorage.setItem("pendingRoom", roomCode);
            navigate("/");
            return;
        }

        navigate(`/chat/${roomCode}?token=${inviteToken}`);
    };

    handleInvite();

}, [roomCode, navigate]);

    return <h2>Joining room...</h2>;
}

export default InvitePage;