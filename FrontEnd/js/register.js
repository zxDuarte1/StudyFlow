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
        alert("A senha deve ter pelo menos 8 caracteres, incluindo letras maiúsculas, minúsculas e números.");
        return;
    }
    if (password !== confirmPassword) {
        alert("As senhas não coincidem!");
        return;
    }

    const userData = {
      name: e.target.name.value,
      email: e.target.email.value,
      password: e.target.password.value
    };

    try{
      const response = await fetch(`${API_URL}/register`,{
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(userData)
      });

      if (response.ok) {
        localStorage.setItem('emailToVerify', userData.email);
        alert("Cadastro realizado! Verifique seu e-mail");
        window.location.href = "verify.html";
      } else{
        const error = await response.text();
        alert("Erro" + error)
      }
    } catch(err){
      alert("Erro ao conectar com o servidor.")
    }
  });
}



