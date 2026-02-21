const API_URL = "http://localhost:8080/api/auth";
const notify = (msg) => console.log(msg);

const loginForm = document.getElementById('loginForm');

if (loginForm) {
    loginForm.addEventListener('submit', async (e) =>{
        e.preventDefault();

        const credentials = {
            email: e.target.email.value,
            password: e.target.password.value
        }

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {'Content-Type': 'Application/json'},
            body: JSON.stringify(credentials)
        });

        if (response.ok) {
            const token = await response.text();
            localStorage.setItem('token', token)

            alert("Bem-Vindo ao Study flow ");
            window.location.href = "dashboard.html";

        }else{ 
            const msg = await response.text();
            alert(msg);
        }
    } catch (err) {
        alert("Erro ao realizar login!")
    }
  });
}