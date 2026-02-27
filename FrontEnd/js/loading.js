function showLoader() {
    const loader = document.getElementById("loading-screen");
    loader.style.display = "flex";
    loader.classList.remove("hidden");
}

function hideLoader() {
    const loader = document.getElementById("loading-screen");
    setTimeout(() => {
        loader.classList.add("hidden");
    }, 1);
}

document.addEventListener("DOMContentLoaded", showLoader);

window.addEventListener("load", hideLoader);

window.addEventListener("pageshow", function (event) {
    if (event.persisted) {
        showLoader();
        hideLoader();
    }
});