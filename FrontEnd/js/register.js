console.log("Arquivo register.js carregado com sucesso!");
const API_URL = "http://localhost:8080/api/auth";
const notify = (msg) => console.log(msg);

function isPasswordStrong(password) {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    return regex.test(password);
}

if (registerForm) {
  registerForm.addEventListener('submit', async (e) =>{
    e.preventDefault();
    const password = e.target.password.value;
    const confirmPassword = e.target.querySelector('input[placeholder="Confirmar senha"]').value;

    if (!isPasswordStrong(password)) {
        showToast("A senha deve ter pelo menos 8 caracteres,incluindo \nletras maiúsculas, minúsculas e números.","info");
        return;
    }
    if (password !== confirmPassword) {
        showToast("As senhas não coincidem!", "error");
        return;
    }

    const userData = {
      name: e.target.name.value,
      email: e.target.email.value,
      password: e.target.password.value
    };

try {
        const response = await fetch(`${API_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        const resultText = await response.text(); 

        if (response.ok) {
            localStorage.removeItem('codeExpiration');
            showToast("Cadastro realizado com sucesso!", "success");
            localStorage.setItem('emailToVerify', userData.email);
            setTimeout(() => window.location.href = "verify.html", 3000);
        } 

        else if (resultText.includes("Duplicate entry") || resultText.includes("Este e-mail já está cadastrado")) {
            showToast("Este e-mail já possui uma conta. Tente fazer login.", "error");
        } 
        else {
            showToast("Erro ao registrar: " + resultText, "error");
        }
    } catch (err) {

        console.error(err);
        showToast("Erro de conexão com o servidor.", "error");
    }
    });
}



