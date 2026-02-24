const API_URL = "http://localhost:8080/api/auth";

const passwordForm = document.getElementById('passwordForm');

if (passwordForm) {
    passwordForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const password = e.target.password.value;
        const passwordConfirm = e.target.password2.value;


        const regex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!regex.test(password)) {
            showToast("Senha muito fraca! Precisa de letra maiúscula, número e símbolo.", "info");
            return;
        }

        if (password !== passwordConfirm) {
            showToast("As senhas não coincidem!", "error");
            return;
        }

        try {
            const email = localStorage.getItem('emailToVerify');

            const response = await fetch(`${API_URL}/reset-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, newPassword: password })
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast("Senha alterada com sucesso!", "success");
                setTimeout(() => {
                    window.location.href = "login.html";
                }, 3000);
            } else {
                showToast("Erro: " + resultText, "error");
            }
        } catch (err) {
            showToast("Erro de conexão com o servidor.", "error");
        }
    });
}