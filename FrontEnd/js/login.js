const API_URL = "http://localhost:8080/api/auth";

const loginForm = document.getElementById('loginForm');

document.addEventListener('DOMContentLoaded', () => {
    const reason = localStorage.getItem('logout_reason');
    if (reason) {
        showToast(reason, "error");
        localStorage.removeItem('logout_reason'); 
    }
});

if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const emailInput = e.target.querySelector('input[name="email"]');
        const passwordInput = e.target.querySelector('input[name="password"]');

        const credentials = {
            email: emailInput.value,
            password: passwordInput.value
        };

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(credentials)
            });

            const data = await response.json();

            if (response.ok) {

                localStorage.setItem('token', data.token);
                localStorage.setItem('userName', data.nome); 
                localStorage.setItem('userEmail', data.email);

                showToast("Bem-Vindo ao Studyflow!", "success");
                
                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 2000);

            } else if (data.message === "USER_NOT_VERIFIED" || response.status === 403) {
                localStorage.setItem('isRecoveryMode', 'false');
                localStorage.setItem('emailToVerify', credentials.email);
                
                showToast("Sua conta ainda não foi verificada. Redirecionando...", "info");
                
                setTimeout(() => {
                    window.location.href = "verify.html";
                }, 2000);

            } else {

                showToast("Erro: " + (data.message || "Credenciais inválidas"));
            }
        } catch (err) {
            console.error("Erro no login:", err);
            showToast("Erro de conexão com o servidor.", "error");
        }
    });
}