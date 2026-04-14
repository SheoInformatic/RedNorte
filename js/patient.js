document.addEventListener("DOMContentLoaded", () => {
  const currentUser = getCurrentUser();

  if (
    window.location.pathname.includes("patient-dashboard.html") ||
    window.location.pathname.includes("booking.html")
  ) {
    if (!currentUser || currentUser.role !== "PACIENTE") {
      window.location.href = "index.html";
      return;
    }
  }

  loadPatientProfile();
  renderPatientBookings();
  setupBookingForm();
});

function loadPatientProfile() {
  const currentUser = getCurrentUser();
  if (!currentUser) return;

  const profileNombre = document.getElementById("profileNombre");
  const profileApellido = document.getElementById("profileApellido");
  const profileRut = document.getElementById("profileRut");
  const profileDiagnostico = document.getElementById("profileDiagnostico");

  if (profileNombre) profileNombre.textContent = currentUser.nombre;
  if (profileApellido) profileApellido.textContent = currentUser.apellido;
  if (profileRut) profileRut.textContent = currentUser.rut;
  if (profileDiagnostico) profileDiagnostico.textContent = currentUser.diagnostico;
}

function setupBookingForm() {
  const form = document.getElementById("bookingForm");
  if (!form) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    const currentUser = getCurrentUser();
    const fecha = document.getElementById("fechaAtencion").value;
    const centro = document.getElementById("centroAtencion").value;
    const especialidad = document.getElementById("especialidad").value;

    const bookings = getBookings();
    bookings.push({
      id: Date.now(),
      patientId: currentUser.id,
      fecha,
      centro,
      especialidad,
      estado: "Reservada"
    });

    saveBookings(bookings);
    showBookingMessage("Reserva creada correctamente.", "success");
    form.reset();

    setTimeout(() => {
      window.location.href = "patient-dashboard.html";
    }, 1000);
  });
}

function renderPatientBookings() {
  const tbody = document.getElementById("patientBookingsBody");
  if (!tbody) return;

  const currentUser = getCurrentUser();
  const bookings = getBookings().filter((booking) => booking.patientId === currentUser.id);

  if (bookings.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="4" class="text-center text-muted">No tienes reservas aún.</td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = bookings
    .map(
      (booking) => `
      <tr>
        <td>${booking.fecha}</td>
        <td>${booking.centro}</td>
        <td>${booking.especialidad}</td>
        <td><span class="badge text-bg-info">${booking.estado}</span></td>
      </tr>
    `
    )
    .join("");
}

function showBookingMessage(text, type) {
  const message = document.getElementById("bookingMessage");
  if (!message) return;

  message.className = `alert alert-${type} mt-2`;
  message.textContent = text;
  message.classList.remove("d-none");
}