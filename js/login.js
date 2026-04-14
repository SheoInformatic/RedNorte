const loginForm = document.getElementById("loginForm");
const loginMessage = document.getElementById("message");

if (loginForm) {
  loginForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const rol = document.getElementById("rol").value;

    const users = getUsers();
    const foundUser = users.find(
      (user) =>
        user.email.toLowerCase() === email.toLowerCase() &&
        user.password === password &&
        user.role === rol
    );

    if (!foundUser) {
      showLoginMessage("Credenciales o rol incorrectos.", "danger");
      return;
    }

    setCurrentUser(foundUser);
    showLoginMessage("Inicio de sesión exitoso.", "success");

    setTimeout(() => {
      if (foundUser.role === "ADMIN") {
        window.location.href = "admin-dashboard.html";
      } else {
        window.location.href = "patient-dashboard.html";
      }
    }, 800);
  });
}

function showLoginMessage(text, type) {
  loginMessage.className = `alert alert-${type} mt-3`;
  loginMessage.textContent = text;
  loginMessage.classList.remove("d-none");
}