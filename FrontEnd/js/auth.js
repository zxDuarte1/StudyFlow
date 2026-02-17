const button = document.getElementById("loginBtn");

button.addEventListener("mousemove", (e) => {
  const rect = button.getBoundingClientRect();
  const x = e.clientX - rect.left - rect.width / 2;
  const y = e.clientY - rect.top - rect.height / 2;

  button.style.transform = `translate(${x * 0.1}px, ${y * 0.1}px)`;
  button.style.boxShadow = `${-x * 0.2}px ${-y * 0.2}px 15px rgba(74, 233, 1, 0.4)`;
});

button.addEventListener("mouseleave", () => {
  button.style.transform = "translate(0, 0)";
  button.style.boxShadow = "none";
});
