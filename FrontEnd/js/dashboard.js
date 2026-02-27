  const token = localStorage.getItem('token');

if (!token) {
    localStorage.setItem('logout_reason', 'Sessão expirada ou não autorizado.');
    window.location.href = "login.html";
}
function toggleDropdown() {
    const dropdown = document.getElementById("profileDropdown");
    if(dropdown) {
        dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

document.addEventListener('DOMContentLoaded', () => {
   
    let nomeUsuario = localStorage.getItem('userName');
    
    if (!nomeUsuario || nomeUsuario === "undefined" || nomeUsuario === "null") {
        nomeUsuario = 'Estudante';
    }

   
    const navName = document.getElementById('userNameDisplay');
    if (navName) navName.innerText = nomeUsuario;

    const dashTitle = document.querySelector('.dashboard-title');
    if (dashTitle) dashTitle.innerText = `Olá, ${nomeUsuario} 👋`;

    const userImg = document.getElementById('userImg');
    if (userImg) {
        userImg.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(nomeUsuario)}&background=8b5cf6&color=fff`;
    }
});

window.onclick = function(event) {
    if (!event.target.closest('.nav-user-section')) {
        const drop = document.getElementById("profileDropdown");
        if(drop) drop.style.display = "none";
    }
}