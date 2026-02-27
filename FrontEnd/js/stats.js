if (!token) {
    localStorage.setItem('logout_reason', 'Sessão expirada ou não autorizado.');
    window.location.href = "login.html";

}