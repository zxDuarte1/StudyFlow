const API_URL = "http://localhost:8080/api/auth";
const emailForm = document.getElementById('requestResetForm');

if (emailForm) {
    emailForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const emailValue = e.target.querySelector('input[name="email"]').value;

        try {
            const response = await fetch(`${API_URL}/forgot-password-request`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailValue })
            });

            const resultText = await response.text();

            if (response.ok) {
                showToast("Verifique seu e-mail para obter o código.", "success");
                
                localStorage.setItem('emailToVerify', emailValue);
                localStorage.setItem('isRecoveryMode', 'true');
                localStorage.removeItem('codeExpiration');

                setTimeout(() => {
                    window.location.href = "verify.html";
                }, 3000);    
            }  
            else { 
                showToast("Erro: " + resultText, "error");
            }
        } catch (err) {
            console.error(err);
            showToast("Erro de conexão com o servidor.", "error");
        }
    });
}