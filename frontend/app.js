const API_BASE_URL = "http://localhost:8080/api";

let authHeader = null;
let currentUser = null;

function showAlert(message, type = "success") {
    const box = document.getElementById("alert-box");
    box.textContent = message;
    box.className = `alert alert-${type}`;
}

function clearAlert() {
    const box = document.getElementById("alert-box");
    box.className = "alert hidden";
}

function getHeaders(includeJson = false) {
    const headers = {};
    if (includeJson) {
        headers["Content-Type"] = "application/json";
    }
    if (authHeader) {
        headers["Authorization"] = authHeader;
    }
    return headers;
}

/**
 * Uniwersalna obsługa odpowiedzi z API, w tym formatu RFC 9457 ProblemDetail
 */
async function handleApiResponse(response) {
    if (response.status === 204) {
        return null;
    }
    const contentType = response.headers.get("content-type") || "";
    const isJson = contentType.includes("application/json") || contentType.includes("application/problem+json");
    const data = isJson ? await response.json() : await response.text();

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error("401 Unauthorized: Zaloguj się poprawnymi danymi.");
        }
        if (response.status === 403) {
            throw new Error("403 Forbidden: Brak uprawnień do wykonania tej operacji.");
        }
        if (data && typeof data === "object" && data.detail) {
            throw new Error(`${data.title || "Błąd"}: ${data.detail}`);
        }
        throw new Error(`Błąd HTTP ${response.status}`);
    }
    return data;
}

async function login() {
    clearAlert();
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;

    if (!username || !password) {
        showAlert("Podaj login oraz hasło.", "error");
        return;
    }

    authHeader = "Basic " + btoa(`${username}:${password}`);

    try {
        const response = await fetch(`${API_BASE_URL}/auth/me`, {
            method: "GET",
            headers: getHeaders()
        });
        currentUser = await handleApiResponse(response);

        const badge = document.getElementById("auth-status");
        badge.textContent = `Zalogowano: ${currentUser.username} [${currentUser.roles.join(", ")}]`;
        badge.className = "badge badge-auth";

        showAlert(`Zalogowano pomyślnie jako ${currentUser.username}.`, "success");
        await loadReservations();
    } catch (err) {
        authHeader = null;
        currentUser = null;
        showAlert(err.message, "error");
    }
}

function logout() {
    authHeader = null;
    currentUser = null;
    const badge = document.getElementById("auth-status");
    badge.textContent = "Niezalogowany (Gość)";
    badge.className = "badge badge-guest";
    document.querySelector("#reservations-table tbody").innerHTML = "";
    showAlert("Wylogowano z aplikacji.", "success");
}

async function loadRooms() {
    try {
        const response = await fetch(`${API_BASE_URL}/rooms`);
        const rooms = await handleApiResponse(response);

        const tbody = document.querySelector("#rooms-table tbody");
        const select = document.getElementById("reservation-room-id");
        tbody.innerHTML = "";
        select.innerHTML = '<option value="">-- Wybierz salę --</option>';

        rooms.forEach(room => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${room.id}</td>
                <td><strong>${room.name}</strong></td>
                <td>${room.building}</td>
                <td>${room.capacity} os.</td>
            `;
            tbody.appendChild(tr);

            const option = document.createElement("option");
            option.value = room.id;
            option.textContent = `${room.name} (${room.capacity} miejsc)`;
            select.appendChild(option);
        });
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function createRoom(event) {
    event.preventDefault();
    clearAlert();

    const payload = {
        name: document.getElementById("room-name").value.trim(),
        building: document.getElementById("room-building").value.trim(),
        capacity: Number(document.getElementById("room-capacity").value),
        equipment: document.getElementById("room-equipment").value.trim()
    };

    try {
        const response = await fetch(`${API_BASE_URL}/rooms`, {
            method: "POST",
            headers: getHeaders(true),
            body: JSON.stringify(payload)
        });
        await handleApiResponse(response);
        showAlert(`Dodano nową salę: ${payload.name}`, "success");
        event.target.reset();
        await loadRooms();
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function loadReservations() {
    if (!authHeader) {
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            headers: getHeaders()
        });
        const reservations = await handleApiResponse(response);

        const tbody = document.querySelector("#reservations-table tbody");
        tbody.innerHTML = "";

        reservations.forEach(res => {
            const tr = document.createElement("tr");
            const startFormatted = res.startTime.replace("T", " ").slice(0, 16);
            const endFormatted = res.endTime.replace("T", " ").slice(11, 16);
            const statusClass = res.status === "CONFIRMED" ? "status-confirmed" : "status-cancelled";
            const actionBtn = res.status === "CONFIRMED"
                ? `<button class="btn btn-danger" onclick="cancelReservation(${res.id})">Anuluj</button>`
                : `<span>—</span>`;

            tr.innerHTML = `
                <td><strong>${res.roomName}</strong></td>
                <td>${res.title}</td>
                <td>${startFormatted} - ${endFormatted}</td>
                <td>${res.bookedBy}</td>
                <td class="${statusClass}">${res.status}</td>
                <td>${actionBtn}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        showAlert(err.message, "error");
    }
}

async function createReservation(event) {
    event.preventDefault();
    clearAlert();

    // TODO (ZADANIE 5 - Fetch API POST):
    // Odczytaj wartości z pól formularza:
    // - reservation-room-id (jako Number)
    // - reservation-title
    // - reservation-start
    // - reservation-end
    // Następnie wyślij żądanie POST na `${API_BASE_URL}/reservations` z nagłówkami `getHeaders(true)`,
    // obsłuż odpowiedź przez `handleApiResponse(response)` i odśwież listę wywołując `loadReservations()`.
    showAlert("TODO (Zadanie 5): Dokończ implementację funkcji createReservation() w pliku frontend/app.js", "error");
}

async function cancelReservation(id) {
    clearAlert();

    // TODO (ZADANIE 6 - Fetch API PATCH):
    // Wyślij żądanie PATCH na `${API_BASE_URL}/reservations/${id}/cancel` z nagłówkami `getHeaders()`,
    // obsłuż odpowiedź przez `handleApiResponse(response)` i odśwież listę wywołując `loadReservations()`.
    showAlert(`TODO (Zadanie 6): Dokończ implementację funkcji cancelReservation(${id}) w pliku frontend/app.js`, "error");
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("btn-login").addEventListener("click", login);
    document.getElementById("btn-logout").addEventListener("click", logout);
    document.getElementById("btn-refresh-rooms").addEventListener("click", loadRooms);
    document.getElementById("btn-refresh-reservations").addEventListener("click", loadReservations);
    document.getElementById("create-room-form").addEventListener("submit", createRoom);
    document.getElementById("create-reservation-form").addEventListener("submit", createReservation);

    // Ustaw domyślne daty na jutro
    const tomorrow = new Date(Date.now() + 24 * 60 * 60 * 1000);
    const yyyy = tomorrow.getFullYear();
    const mm = String(tomorrow.getMonth() + 1).padStart(2, "0");
    const dd = String(tomorrow.getDate()).padStart(2, "0");
    document.getElementById("reservation-start").value = `${yyyy}-${mm}-${dd}T13:00`;
    document.getElementById("reservation-end").value = `${yyyy}-${mm}-${dd}T15:00`;

    loadRooms();
});
