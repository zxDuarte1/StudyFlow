if (!token) {
    localStorage.setItem('logout_reason', 'Sessão expirada ou não autorizado.');

    window.location.href = "login.html";
}

const input = document.getElementById("taskInput");
const button = document.getElementById("addTaskBtn");
const list = document.getElementById("taskList");

button.addEventListener("click", addTask);

function addTask() {
    if (input.value.trim() !== "") {
        const task = document.createElement("div");
        task.className = "task-card";
        task.innerHTML = `
            <span>${input.value}</span>
            <button onclick="this.parentElement.remove()">
                <i class="fas fa-trash"></i>
            </button>
        `;
        list.appendChild(task);
        input.value = "";
    }
}