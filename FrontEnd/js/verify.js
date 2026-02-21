const API_URL = "http://localhost:8080/api/auth";
const notify = (msg) => console.log(msg);
const verifyForm = document.getElementById('verifyForm');
if (verifyForm) {
    verifyForm.addEventListener('submit', async (e) =>{
        e.preventDefault();

        const code = document.getElementById('verificationCode').value;
        const email = localStorage.getItem('emailToVerify');

        if (!email) {
            alert("Erro: E-mail não encontrado. Tente se registrar novamente");
            return
        }

        try{
            const response = await fetch(`${API_URL}/verify`,{
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ email, code})
            });
            if (response.ok) {
                alert("E-mail verificado com sucesso! Agora faça login.");
                localStorage.removeItem('emailToVerify');
                window.localStorage.href = "login.html";
            } else{
                alert("Código inválido ou expirado");
            }
        }catch(err){
            alert("Erro ao verificar código.")
        }
    });

    document.getElementById('resendBtn').addEventListener('click', async () =>{
        const email = localStorage.getItem('emailToVerify');
        await fetch(`${API_URL}/resend-code?email=${email}`,{method: 'POST'});
        alert("Novo Código enviado");
    });
}