document.addEventListener("DOMContentLoaded", () => {
  const currentUser = getCurrentUser();

  if (window.location.pathname.includes("admin-dashboard.html") || window.location.pathname.includes("patients-admin.html")) {
    if (!currentUser || currentUser.role !== "ADMIN") {
      window.location.href = "index.html";
      return;
    }
  }

  const adminName = document.getElementById("adminName");
  if (adminName && currentUser) {
    adminName.textContent = `${currentUser.nombre} ${currentUser.apellido}`;
  }

  loadAdminStats();
  setupPatientForm();
  renderPatientsTable();
});

function loadAdminStats() {
  const totalPacientes = document.getElementById("totalPacientes");
  const totalReservas = document.getElementById("totalReservas");

  if (totalPacientes) {
    const patients = getUsers().filter((user) => user.role === "PACIENTE");
    totalPacientes.textContent = patients.length;
  }

  if (totalReservas) {
    totalReservas.textContent = getBookings().length;
  }
}

function setupPatientForm() {
  const form = document.getElementById("patientForm");
  const clearBtn = document.getElementById("clearFormBtn");

  if (!form) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    const id = document.getElementById("patientId").value;
    const nombre = document.getElementById("patientNombre").value.trim();
    const apellido = document.getElementById("patientApellido").value.trim();
    const rut = document.getElementById("patientRut").value.trim();
    const email = document.getElementById("patientEmail").value.trim();
    const diagnostico = document.getElementById("patientDiagnostico").value.trim();

    const users = getUsers();

    if (id) {
      const index = users.findIndex((u) => String(u.id) === String(id));
      if (index !== -1) {
        users[index] = {
          ...users[index],
          nombre,
          apellido,
          rut,
          email,
          diagnostico,
          role: "PACIENTE"
        };
      }
      showPatientMessage("Paciente actualizado correctamente.", "success");
    } else {
      users.push({
        id: Date.now(),
        nombre,
        apellido,
        rut,
        email,
        diagnostico,
        password: "123456",
        role: "PACIENTE"
      });
      showPatientMessage("Paciente creado correctamente.", "success");
    }

    saveUsers(users);
    form.reset();
    document.getElementById("patientId").value = "";
    renderPatientsTable();
  });

  if (clearBtn) {
    clearBtn.addEventListener("click", () => {
      form.reset();
      document.getElementById("patientId").value = "";
    });
  }
}

function renderPatientsTable() {
  const tbody = document.getElementById("patientsTableBody");
  if (!tbody) return;

  const patients = getUsers().filter((user) => user.role === "PACIENTE");

  if (patients.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-muted">No hay pacientes registrados.</td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = patients
    .map(
      (patient) => `
      <tr>
        <td>${patient.id}</td>
        <td>${patient.rut}</td>
        <td>${patient.nombre}</td>
        <td>${patient.apellido}</td>
        <td>${patient.email}</td>
        <td>${patient.diagnostico}</td>
        <td>
          <button class="btn btn-sm btn-warning me-2" onclick="editPatient(${patient.id})">
            <i class="bi bi-pencil-fill"></i>
          </button>
          <button class="btn btn-sm btn-danger" onclick="deletePatient(${patient.id})">
            <i class="bi bi-trash-fill"></i>
          </button>
        </td>
      </tr>
    `
    )
    .join("");
}

function editPatient(id) {
  const patient = getUsers().find((user) => user.id === id);
  if (!patient) return;

  document.getElementById("patientId").value = patient.id;
  document.getElementById("patientNombre").value = patient.nombre;
  document.getElementById("patientApellido").value = patient.apellido;
  document.getElementById("patientRut").value = patient.rut;
  document.getElementById("patientEmail").value = patient.email;
  document.getElementById("patientDiagnostico").value = patient.diagnostico;

  window.scrollTo({ top: 0, behavior: "smooth" });
}

function deletePatient(id) {
  const confirmDelete = confirm("¿Deseas eliminar este paciente?");
  if (!confirmDelete) return;

  const users = getUsers().filter((user) => user.id !== id);
  saveUsers(users);
  renderPatientsTable();
  showPatientMessage("Paciente eliminado correctamente.", "warning");
}

function showPatientMessage(text, type) {
  const message = document.getElementById("patientMessage");
  if (!message) return;

  message.className = `alert alert-${type} mt-2`;
  message.textContent = text;
  message.classList.remove("d-none");
}