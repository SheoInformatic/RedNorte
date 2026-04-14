const registerForm = document.getElementById("registerForm");
const registerMessage = document.getElementById("registerMessage");

if (registerForm) {
  registerForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const nombre = document.getElementById("nombre").value.trim();
    const apellido = document.getElementById("apellido").value.trim();
    const rut = document.getElementById("rut").value.trim();
    const diagnostico = document.getElementById("diagnostico").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const users = getUsers();
    const exists = users.some((user) => user.email.toLowerCase() === email.toLowerCase());

    if (exists) {
      showRegisterMessage("Ya existe un usuario con ese correo.", "danger");
      return;
    }

    const newUser = {
      id: Date.now(),
      nombre,
      apellido,
      rut,
      diagnostico,
      email,
      password,
      role: "PACIENTE"
    };

    users.push(newUser);
    saveUsers(users);
    showRegisterMessage("Paciente registrado correctamente.", "success");
    registerForm.reset();

    setTimeout(() => {
      window.location.href = "index.html";
    }, 1200);
  });
}

function showRegisterMessage(text, type) {
  registerMessage.className = `alert alert-${type} mt-2`;
  registerMessage.textContent = text;
  registerMessage.classList.remove("d-none");
}