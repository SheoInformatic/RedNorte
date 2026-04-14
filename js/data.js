const STORAGE_KEYS = {
  users: "rednorte_users",
  bookings: "rednorte_bookings",
  currentUser: "rednorte_current_user"
};

function seedInitialData() {
  const users = JSON.parse(localStorage.getItem(STORAGE_KEYS.users)) || [];
  const bookings = JSON.parse(localStorage.getItem(STORAGE_KEYS.bookings)) || [];

  if (users.length === 0) {
    const defaultUsers = [
      {
        id: 1,
        nombre: "Admin",
        apellido: "Sistema",
        rut: "11.111.111-1",
        diagnostico: "N/A",
        email: "admin@salud.cl",
        password: "123456",
        role: "ADMIN"
      },
      {
        id: 2,
        nombre: "María",
        apellido: "Pérez",
        rut: "22.222.222-2",
        diagnostico: "Control general",
        email: "maria@salud.cl",
        password: "123456",
        role: "PACIENTE"
      }
    ];

    localStorage.setItem(STORAGE_KEYS.users, JSON.stringify(defaultUsers));
  }

  if (bookings.length === 0) {
    localStorage.setItem(STORAGE_KEYS.bookings, JSON.stringify([]));
  }
}

function getUsers() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.users)) || [];
}

function saveUsers(users) {
  localStorage.setItem(STORAGE_KEYS.users, JSON.stringify(users));
}

function getBookings() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.bookings)) || [];
}

function saveBookings(bookings) {
  localStorage.setItem(STORAGE_KEYS.bookings, JSON.stringify(bookings));
}

function setCurrentUser(user) {
  localStorage.setItem(STORAGE_KEYS.currentUser, JSON.stringify(user));
}

function getCurrentUser() {
  return JSON.parse(localStorage.getItem(STORAGE_KEYS.currentUser));
}

function clearCurrentUser() {
  localStorage.removeItem(STORAGE_KEYS.currentUser);
}

seedInitialData();