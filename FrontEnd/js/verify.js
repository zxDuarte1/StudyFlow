const API_URL = "http://localhost:8080/api/auth";
const verifyForm = document.getElementById('verifyForm');

if (verifyForm) {
    verifyForm.addEventListener('submit', async (e) => {
        e.preventDefault(); 
        
        const code = document.getElementById('verificationCode').value;
        const email = localStorage.getItem('emailToVerify');

        if (!email) {
            alert("Erro: E-mail não encontrado. Tente se registrar novamente.");
            return;
        }

        try {
            const response = await fetch(`${API_URL}/verify`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, code })
            });

            if (response.ok) {
                alert("E-mail verificado com sucesso!");
                localStorage.removeItem('emailToVerify');
                window.location.href = "login.html";
            } else {
                const errorMsg = await response.text();
                alert("Código inválido: " + errorMsg);
            }
        } catch (err) {
            alert("Erro de conexão com o servidor.");
        }
    });
}

const resendBtn = document.getElementById('resendBtn');
if (resendBtn) {
    resendBtn.addEventListener('click', async () => {
        const email = localStorage.getItem('emailToVerify');

        if (!email) {
            alert("Erro: E-mail não encontrado no navegador.");
            return;
        }
        resendBtn.disabled = true;
        resendBtn.innerText = "Enviando...";

        try {
            const response = await fetch(`${API_URL}/resend-code?email=${encodeURIComponent(email)}`, {
                method: 'POST'
            });

            if (response.ok) {
                alert("Novo código enviado com sucesso para: " + email);
            } else {
                const error = await response.text();
                alert("Erro ao reenviar: " + error);
            }
        } catch (err) {
            alert("Erro de conexão com o servidor.");
        } finally {
            resendBtn.disabled = false;
            resendBtn.innerText = "Reenviar Código";
        }
    });
}