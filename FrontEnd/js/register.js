console.log("Arquivo register.js carregado com sucesso!");
const API_URL = "http://localhost:8080/api/auth";


const notify = (msg) => console.log(msg);

const registerForm = document.getElementById('registerForm');
if (registerForm) {
  registerForm.addEventListener('submit', async (e) =>{
    e.preventDefault();
    const userData = {
      username: e.target.username.value,
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



