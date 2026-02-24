const API_URL = "http://localhost:8080/api/auth";
const notify = (msg) => console.log(msg);

const loginForm = document.getElementById('loginForm');

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

            const resultText = await response.text();

            if (response.ok) {
                localStorage.setItem('token', resultText);
                showToast("Bem-Vindo ao Studyflow!", "success");
                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 3000);

            }
            else if (resultText.includes("Email ainda não verificado") || resultText.includes("USER_NOT_VERIFIED")) {
                localStorage.removeItem('codeExpiration');
                showToast("Sua conta ainda não foi verificada. Redirecionando...", "info");
                localStorage.setItem('emailToVerify', credentials.email);
                setTimeout(() => {
                    window.location.href = "verify.html";
                }, 3000);

            }
            else {
                showToast("Erro: " + resultText);
            }
        } catch (err) {
            showToast("Erro de conexão com o servidor.", "error");
        }
    });
}