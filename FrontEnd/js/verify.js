const API_URL = "http://localhost:8080/api/auth";
const verifyForm = document.getElementById('verifyForm');

if (verifyForm) {
    verifyForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const code = document.getElementById('verificationCode').value;
        const email = localStorage.getItem('emailToVerify');
        const isRecovery = localStorage.getItem('isRecoveryMode');

        if (!email) {
            showToast("Erro: E-mail não encontrado. Tente se registrar novamente.");
            return;
        }

        try {
            const response = await fetch(`${API_URL}/verify`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, code })
            });

            if (response.ok) {
                const isRecovery = localStorage.getItem('isRecoveryMode');

                if (isRecovery === 'true') {
                    showToast("Código aceito! Defina sua nova senha.", "success");
        
                    localStorage.removeItem('isRecoveryMode'); 
                    
                    setTimeout(() => {
                        window.location.href = "forgotPassword.html";
                    }, 2000);
                } else {

                    showToast("E-mail verificado com sucesso! Agora você pode entrar.", "success");
                    localStorage.removeItem('emailToVerify');
                    
                    setTimeout(() => {
                        window.location.href = "login.html";
                    }, 3000);
                }
            }else {
                const errorMsg = await response.text();
                showToast("Error: " + errorMsg);

            }
        } catch (err) {
            showToast("Erro de conexão com o servidor.");
        }
    });
}

const resendBtn = document.getElementById('resendBtn');
if (resendBtn) {
    resendBtn.addEventListener('click', async () => {
        const email = localStorage.getItem('emailToVerify');

        if (!email) {
            showToast("Erro: E-mail não encontrado no navegador.");
            return;
        }
        resendBtn.disabled = true;
        resendBtn.innerText = "Enviando...";

        try {
            const response = await fetch(`${API_URL}/resend-code?email=${encodeURIComponent(email)}`, {
                method: 'POST'
            });

            if (response.ok) {
                showToast("Novo código enviado com sucesso para: " + email);
                localStorage.removeItem('codeExpiration');
                startTimer(600);
            } else {
                const error = await response.text();
                showToast("Erro ao reenviar: " + error);
            }
        } catch (err) {
            showToast("Erro de conexão com o servidor.");
        } finally {
            resendBtn.disabled = false;
            resendBtn.innerText = "Reenviar Código";
        }
    });
    let countdown;

    function startTimer(durationInSeconds) {
        const timerDisplay = document.getElementById('timer');
        const resendBtn = document.getElementById('resendBtn');


        let expirationTime = localStorage.getItem('codeExpiration');

        if (!expirationTime) {

            expirationTime = Date.now() + (durationInSeconds * 1000);
            localStorage.setItem('codeExpiration', expirationTime);
        }


        if (countdown) clearInterval(countdown);


        const updateDisplay = () => {
            const now = Date.now();
            const timeLeft = Math.ceil((expirationTime - now) / 1000);

            if (timeLeft <= 0) {
                clearInterval(countdown);
                localStorage.removeItem('codeExpiration');
                timerDisplay.textContent = "Expirado";
                timerDisplay.style.color = "#ff4b4b";
                resendBtn.disabled = false;
                resendBtn.style.opacity = "1";
                return;
            }

            let minutes = Math.floor(timeLeft / 60);
            let seconds = timeLeft % 60;

            minutes = String(minutes).padStart(2, '0');
            seconds = String(seconds).padStart(2, '0');

            timerDisplay.textContent = `${minutes}:${seconds}`;


            if (timeLeft <= 60) {
                timerDisplay.style.color = "#ff4b4b";
            } else {
                timerDisplay.style.color = "#4caf50";
            }

        };


        updateDisplay();


        countdown = setInterval(updateDisplay, 1000);
    }


    document.addEventListener('DOMContentLoaded', () => {
        startTimer(600);
    });
}