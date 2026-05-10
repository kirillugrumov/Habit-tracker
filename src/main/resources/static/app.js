const state = {
    currentTab: "today",
    loading: false,
    todayBusy: false,
    status: { type: "", message: "" },
    users: [],
    habits: [],
    categories: [],
    goals: [],
    logs: [],
    filters: {
        username: "",
        categoryName: "",
        habitName: "",
        page: 0
    },
    searchResult: null,
    modal: {
        type: null,
        mode: "create",
        id: null
    }
};

const tabs = [
    { id: "today", label: "Today" },
    { id: "habits", label: "Habits" },
    { id: "users", label: "Users" },
    { id: "categories", label: "Categories" },
    { id: "goals", label: "Goals" },
    { id: "logs", label: "Activity" }
];

const app = document.getElementById("app");

const HABIT_SEARCH_PAGE_SIZE = 12;

document.addEventListener("DOMContentLoaded", async () => {
    renderApp();
    await loadAllData();
});

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

function setStatus(type, message) {
    state.status = { type, message };
    renderApp();
}

function clearStatus() {
    state.status = { type: "", message: "" };
}

function openModal(type, mode = "create", id = null) {
    state.modal = { type, mode, id };
    renderApp();
}

function closeModal() {
    state.modal = { type: null, mode: "create", id: null };
    renderApp();
}

function toggleTab(tabId) {
    state.currentTab = tabId;
    clearStatus();
    renderApp();
}

async function apiRequest(path, options = {}) {
    const response = await fetch(path, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";
    const payload = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

    if (!response.ok) {
        throw new Error(extractErrorMessage(payload, response.status));
    }

    return payload;
}

function clearFieldErrors(form) {
    form.querySelectorAll("label.modal-field.has-error").forEach((lab) => {
        lab.classList.remove("has-error");
        lab.querySelectorAll(".inline-error").forEach((node) => node.remove());
    });
}

function attachModalFieldHints(form) {
    const clearRow = (input) => {
        const wrap = input.closest("label.modal-field");
        if (!wrap || !wrap.classList.contains("has-error")) {
            return;
        }
        wrap.classList.remove("has-error");
        wrap.querySelectorAll(".inline-error").forEach((node) => node.remove());
    };
    ["input", "change"].forEach((evt) => {
        form.addEventListener(evt, (event) => {
            const tag = event.target?.tagName;
            if (
                tag === "INPUT"
                || tag === "TEXTAREA"
                || tag === "SELECT"
            ) {
                clearRow(event.target);
            }
        });
    });
}

function showFieldError(input, message) {
    const wrap = input.closest("label.modal-field") || input.parentElement;
    if (!wrap) {
        return;
    }
    wrap.classList.add("has-error");
    let span = wrap.querySelector(":scope > .inline-error");
    if (!span) {
        span = document.createElement("span");
        span.className = "inline-error";
        wrap.appendChild(span);
    }
    span.textContent = message;
}

function isValidEmail(value) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

function extractErrorMessage(payload, status) {
    if (!payload) {
        return `Request failed with status ${status}.`;
    }
    if (typeof payload === "string") {
        return payload;
    }
    if (payload.message) {
        return payload.message;
    }
    if (Array.isArray(payload.errors) && payload.errors.length > 0) {
        return payload.errors.map((item) => item.message || JSON.stringify(item)).join("; ");
    }
    if (Array.isArray(payload.details) && payload.details.length > 0) {
        return payload.details.map((item) => item.message || JSON.stringify(item)).join("; ");
    }
    return `Request failed with status ${status}.`;
}

function getLocalTodayIso() {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, "0");
    const d = String(now.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
}

function normalizeLogDate(value) {
    if (value == null || value === "") {
        return "";
    }
    if (typeof value === "string") {
        return value.slice(0, 10);
    }
    if (Array.isArray(value) && value.length >= 3) {
        const [year, month, day] = value;
        return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
    }
    return String(value).slice(0, 10);
}

function getTodayLogForHabit(habitId, todayIso = getLocalTodayIso()) {
    return state.logs.find(
        (log) => log.habitId === habitId && normalizeLogDate(log.logDate) === todayIso
    ) ?? null;
}

function sortedHabitsForTodayView() {
    return [...state.habits].sort((a, b) => {
        const ua = String(a.username || "").localeCompare(String(b.username || ""));
        if (ua !== 0) {
            return ua;
        }
        return String(a.name || "").localeCompare(String(b.name || ""));
    });
}

async function reloadHabitLogs() {
    const logs = await apiRequest("/api/habit-logs");
    state.logs = logs;
}

async function loadAllData() {
    try {
        state.loading = true;
        renderApp();

        const [users, habits, categories, goals, logs] = await Promise.all([
            apiRequest("/api/users"),
            apiRequest("/api/habits"),
            apiRequest("/api/categories"),
            apiRequest("/api/goals"),
            apiRequest("/api/habit-logs")
        ]);

        state.users = users;
        state.habits = habits;
        state.categories = categories;
        state.goals = goals;
        state.logs = logs;
        await runHabitSearch();
    } catch (error) {
        setStatus("error", error.message);
    } finally {
        state.loading = false;
        renderApp();
    }
}

async function runHabitSearch() {
    const params = new URLSearchParams();
    if (state.filters.username.trim()) {
        params.set("username", state.filters.username.trim());
    }
    if (state.filters.categoryName.trim()) {
        params.set("categoryName", state.filters.categoryName.trim());
    }
    if (state.filters.habitName.trim()) {
        params.set("habitName", state.filters.habitName.trim());
    }
    params.set("page", String(state.filters.page));
    params.set("size", String(HABIT_SEARCH_PAGE_SIZE));

    const endpoint = `/api/habits/search/jpql?${params.toString()}`;

    state.searchResult = await apiRequest(endpoint);
}

function getSelectedValues(select) {
    return Array.from(select.selectedOptions).map((option) => Number(option.value));
}

function getHabitsByUser(userId) {
    return state.habits.filter((habit) => habit.userId === userId);
}

function getHabitsByCategory(categoryId) {
    return state.habits.filter((habit) => (habit.categories || []).some((category) => category.id === categoryId));
}

function findEntity(type, id) {
    const map = {
        user: state.users,
        habit: state.habits,
        category: state.categories,
        goal: state.goals,
        log: state.logs
    };
    return (map[type] || []).find((item) => item.id === id) || null;
}

function getModalTitle() {
    const labels = {
        user: "User",
        habit: "Habit",
        category: "Category",
        goal: "Goal",
        log: "Activity"
    };
    return `${state.modal.mode === "edit" ? "Edit" : "Create"} ${labels[state.modal.type] || ""}`;
}

async function handleUserSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    clearFieldErrors(form);

    let valid = true;
    const usernameEl = form.username;
    const emailEl = form.email;
    const u = usernameEl.value.trim();
    const e = emailEl.value.trim();
    if (!u) {
        showFieldError(usernameEl, "Enter a username.");
        valid = false;
    }
    if (!e) {
        showFieldError(emailEl, "Enter an email.");
        valid = false;
    } else if (!isValidEmail(e)) {
        showFieldError(emailEl, "Enter a valid email address.");
        valid = false;
    }
    if (!valid) {
        return;
    }

    const payload = { username: u, email: e };

    try {
        if (state.modal.mode === "edit" && state.modal.id) {
            await apiRequest(`/api/users/${state.modal.id}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            setStatus("success", "User updated.");
        } else {
            await apiRequest("/api/users", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            setStatus("success", "User created.");
        }
        closeModal();
        await loadAllData();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function handleHabitSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    clearFieldErrors(form);

    const nameEl = form.name;
    const userEl = form.userId;
    const nameVal = nameEl.value.trim();
    let valid = true;
    if (!nameVal) {
        showFieldError(nameEl, "Enter a habit name.");
        valid = false;
    }
    const uidRaw = userEl.value;
    const uid = Number(uidRaw);
    if (!userEl.disabled && (!uidRaw || !Number.isFinite(uid))) {
        showFieldError(userEl, "Choose an owner.");
        valid = false;
    } else if (userEl.disabled && (!uidRaw || !Number.isFinite(uid))) {
        showFieldError(userEl, "Owner is unavailable for this habit.");
        valid = false;
    }
    if (!valid) {
        return;
    }

    const payload = {
        name: nameVal,
        description: form.description.value.trim(),
        userId: uid,
        categoryIds: getSelectedValues(form.categoryIds)
    };

    try {
        if (state.modal.mode === "edit" && state.modal.id) {
            await apiRequest(`/api/habits/${state.modal.id}`, {
                method: "PUT",
                body: JSON.stringify({
                    name: payload.name,
                    description: payload.description,
                    categoryIds: payload.categoryIds
                })
            });
            setStatus("success", "Habit updated.");
        } else {
            await apiRequest("/api/habits", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            setStatus("success", "Habit created.");
        }
        closeModal();
        await loadAllData();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function handleCategorySubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    clearFieldErrors(form);
    const nameEl = form.name;
    const n = nameEl.value.trim();
    if (!n) {
        showFieldError(nameEl, "Enter a category name.");
        return;
    }
    const payload = {
        name: n,
        description: form.description.value.trim()
    };

    try {
        if (state.modal.mode === "edit" && state.modal.id) {
            await apiRequest(`/api/categories/${state.modal.id}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            setStatus("success", "Category updated.");
        } else {
            await apiRequest("/api/categories", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            setStatus("success", "Category created.");
        }
        closeModal();
        await loadAllData();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function handleGoalSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    clearFieldErrors(form);

    const nameEl = form.name;
    const habitEl = form.habitId;
    const condEl = form.condition;

    let valid = true;
    const n = nameEl.value.trim();
    if (!n) {
        showFieldError(nameEl, "Enter a goal name.");
        valid = false;
    }
    const habitVal = Number(habitEl.value);
    if (!habitEl.value || !Number.isFinite(habitVal)) {
        showFieldError(habitEl, "Choose a habit.");
        valid = false;
    }
    const c = condEl.value.trim();
    if (!c) {
        showFieldError(condEl, "Describe the condition.");
        valid = false;
    }
    if (!valid) {
        return;
    }

    const payload = {
        name: n,
        condition: c,
        habitId: habitVal
    };

    try {
        if (state.modal.mode === "edit" && state.modal.id) {
            await apiRequest(`/api/goals/${state.modal.id}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            setStatus("success", "Goal updated.");
        } else {
            await apiRequest("/api/goals", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            setStatus("success", "Goal created.");
        }
        closeModal();
        await loadAllData();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function handleLogSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    clearFieldErrors(form);
    const habitEl = form.habitId;
    const hid = Number(habitEl.value);
    if (!habitEl.value || !Number.isFinite(hid)) {
        showFieldError(habitEl, "Choose a habit.");
        return;
    }
    try {
        await apiRequest("/api/habit-logs", {
            method: "POST",
            body: JSON.stringify({ habitId: hid })
        });
        setStatus("success", "Activity saved.");
        closeModal();
        await reloadHabitLogs();
        renderApp();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function markHabitCompleteToday(habitId) {
    if (state.todayBusy) {
        return;
    }
    state.todayBusy = true;
    clearStatus();
    renderApp();
    try {
        await apiRequest("/api/habit-logs", {
            method: "POST",
            body: JSON.stringify({ habitId })
        });
        await reloadHabitLogs();
        setStatus("success", "Marked complete for today.");
    } catch (error) {
        setStatus("error", error.message);
    } finally {
        state.todayBusy = false;
        renderApp();
    }
}

async function undoHabitCompletionToday(logId) {
    if (state.todayBusy) {
        return;
    }
    state.todayBusy = true;
    clearStatus();
    renderApp();
    try {
        await apiRequest(`/api/habit-logs/${logId}`, { method: "DELETE" });
        await reloadHabitLogs();
        setStatus("success", "Today's completion cleared.");
    } catch (error) {
        setStatus("error", error.message);
    } finally {
        state.todayBusy = false;
        renderApp();
    }
}

async function completeAllHabitsToday() {
    if (state.todayBusy) {
        return;
    }
    const todayIso = getLocalTodayIso();
    const incomplete = state.habits.filter((habit) => !getTodayLogForHabit(habit.id, todayIso));
    if (!incomplete.length) {
        return;
    }
    state.todayBusy = true;
    clearStatus();
    renderApp();
    try {
        await apiRequest("/api/habit-logs/bulk", {
            method: "POST",
            body: JSON.stringify({
                logs: incomplete.map((habit) => ({ habitId: habit.id }))
            })
        });
        await reloadHabitLogs();
        setStatus("success", `Marked ${incomplete.length} habit(s) complete for today.`);
    } catch (error) {
        setStatus("error", error.message);
    } finally {
        state.todayBusy = false;
        renderApp();
    }
}

async function confirmDelete(entityType, id) {
    const endpoints = {
        users: `/api/users/${id}`,
        habits: `/api/habits/${id}`,
        categories: `/api/categories/${id}`,
        goals: `/api/goals/${id}`,
        logs: `/api/habit-logs/${id}`
    };

    if (!window.confirm("Delete this item?")) {
        return;
    }

    try {
        await apiRequest(endpoints[entityType], { method: "DELETE" });
        setStatus("success", "Item deleted.");
        await loadAllData();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function handleSearchSubmit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    state.filters.username = form.username.value;
    state.filters.categoryName = form.categoryName.value;
    state.filters.habitName = form.habitName.value;
    state.filters.page = 0;

    try {
        await runHabitSearch();
        renderApp();
    } catch (error) {
        setStatus("error", error.message);
    }
}

async function changeSearchPage(delta) {
    state.filters.page = Math.max(0, state.filters.page + delta);
    try {
        await runHabitSearch();
        renderApp();
    } catch (error) {
        setStatus("error", error.message);
    }
}

function renderApp() {
    app.innerHTML = `
        <div class="app-shell">
            <aside class="sidebar">
                <div class="sidebar-brand">
                    <div>
                        <h1>Habit Tracker</h1>
                        <p>Glass daily flow — stay on streak.</p>
                    </div>
                </div>
                <nav class="sidebar-nav">
                    ${tabs.map((tab) => `
                        <button class="nav-item ${state.currentTab === tab.id ? "active" : ""}" data-tab="${tab.id}">
                            ${tab.label}
                        </button>
                    `).join("")}
                </nav>
            </aside>
            <main class="main-view">
            <div class="main-content">
                <header class="topbar">
                    <div>
                        <h2>${getPageTitle()}</h2>
                        <p>${getPageSubtitle()}</p>
                    </div>
                    <div class="topbar-actions">
                        ${renderPrimaryAction()}
                    </div>
                </header>
               
                ${state.status.message ? `<div class="flash ${state.status.type}">${escapeHtml(state.status.message)}</div>` : ""}
                ${state.loading ? `<section class="panel"><p>Loading...</p></section>` : renderCurrentTab()}
                </div>
                <footer class="app-footer">
        
    <div class="footer-content">
        <p>
            Habit-Tracker — это учебное Spring Boot приложение,
            представляющее собой REST API для отслеживания привычек.
            Финальной целью является создание полноценного backend-сервиса
            с подключением к базе данных, реализующего операции выбора,
            кастомизации привычек и ведения статистики выполнения.
        </p>

        <div class="footer-meta">
            <span>Built with Spring Boot</span>
            <span>•</span>
            <a href="mailto:kirillxxxoio@gmail.com">
                kirillxxxoio@gmail.com
            </a>
        </div>
    </div>
</footer>
            </main>
            ${renderModal()}
        </div>
    `;

    bindAppEvents();
}

function getPageTitle() {
    const titles = {
        today: "Today",
        habits: "Habits",
        users: "Users",
        categories: "Categories",
        goals: "Goals",
        logs: "Activity"
    };
    return titles[state.currentTab];
}

function getPageSubtitle() {
    if (state.currentTab === "today") {
        const todayIso = getLocalTodayIso();
        const total = state.habits.length;
        const done = total
            ? state.habits.filter((habit) => getTodayLogForHabit(habit.id, todayIso)).length
            : 0;
        if (!total) {
            return "Add routines under Habits — your checklist for today appears here.";
        }
        return `${done} / ${total} done today · comparing to your device's date (${todayIso}). Server uses its own timezone.`;
    }
    const subtitles = {
        habits: "Manage routines, assign owners and connect categories.",
        users: "People who own and track habits.",
        categories: "Flexible labels for organizing habits.",
        goals: "Targets linked to specific habits.",
        logs: "Recorded completions and timeline entries."
    };
    return subtitles[state.currentTab];
}

function renderPrimaryAction() {
    if (state.currentTab === "today") {
        const todayIso = getLocalTodayIso();
        const incomplete = state.habits.filter((habit) => !getTodayLogForHabit(habit.id, todayIso));
        const bulkDisabled =
            state.todayBusy || state.habits.length === 0 || incomplete.length === 0;
        return `
            <button class="primary-button" type="button" data-complete-all-today ${bulkDisabled ? "disabled" : ""}>
                Complete all today
            </button>
            <button class="ghost-button ghost-button--glass" type="button" data-tab="habits">Manage habits</button>
        `;
    }
    const actions = {
        habits: `<button class="primary-button" data-open-create="habit">New habit</button>`,
        users: `<button class="primary-button" data-open-create="user">New user</button>`,
        categories: `<button class="primary-button" data-open-create="category">New category</button>`,
        goals: `<button class="primary-button" data-open-create="goal">New goal</button>`,
        logs: `<button class="primary-button" data-open-create="log">New activity</button>`
    };
    return actions[state.currentTab];
}

function renderCurrentTab() {
    switch (state.currentTab) {
        case "today":
            return renderTodayTab();
        case "habits":
            return renderHabitsTab();
        case "users":
            return renderUsersTab();
        case "categories":
            return renderCategoriesTab();
        case "goals":
            return renderGoalsTab();
        case "logs":
            return renderLogsTab();
        default:
            return renderTodayTab();
    }
}

function renderTodayTab() {
    const todayIso = getLocalTodayIso();
    const habits = sortedHabitsForTodayView();

    if (!habits.length) {
        return `
            <section class="today-empty glass-panel">
                <h3>No habits yet</h3>
                <p>Create your first habit to see it on today's list.</p>
                <button class="primary-button" type="button" data-tab="habits">Go to Habits</button>
            </section>
        `;
    }

    return `
        <section class="today-list" aria-busy="${state.todayBusy}">
            ${habits.map((habit) => {
                const log = getTodayLogForHabit(habit.id, todayIso);
                const done = Boolean(log);
                const busy = state.todayBusy;
                return `
                    <article class="today-card glass-card ${done ? "today-card--done" : ""}">
                        <div class="today-card-body">
                            <h3>${escapeHtml(habit.name)}</h3>
                            <p>${escapeHtml(habit.description || "No description")}</p>
                            <div class="today-card-meta">${escapeHtml(habit.username)}</div>
                            <div class="chip-row">
                                <span class="chip ${done ? "chip--done-state" : "chip--muted"}">${done ? "Done today" : "Pending"}</span>
                                ${(habit.categories || []).map((category) => `
                                    <span class="chip">${escapeHtml(category.name)}</span>
                                `).join("")}
                            </div>
                        </div>
                        <div class="today-card-actions">
                            <button type="button" class="btn-gradient today-action-btn" data-complete-habit="${habit.id}"
                                ${busy || done ? "disabled" : ""} aria-label="Mark complete">
                                Complete
                            </button>
                            <button type="button" class="btn-outline-light today-action-btn" data-undo-log="${log?.id ?? ""}"
                                ${busy || !log ? "disabled" : ""} aria-label="Undo completion">
                                Undo
                            </button>
                        </div>
                    </article>
                `;
            }).join("")}
        </section>
    `;
}

function renderHabitsTab() {
    const pageHabits = state.searchResult?.content ?? [];
    const hasFilters =
        Boolean(state.filters.username.trim())
        || Boolean(state.filters.categoryName.trim())
        || Boolean(state.filters.habitName.trim());
    const total = state.searchResult?.totalElements ?? 0;
    const emptyHint =
        total === 0 && !hasFilters
            ? "No habits yet. Create one to get started."
            : "Nothing matches — try broader filters or reset.";

    return `
        <section class="panel habits-search-panel">
            <div class="panel-heading">
                <h3>Search habits</h3>
            </div>
            <form id="search-form" class="habits-search-form" novalidate>
                <div class="habits-search-fields">
                    <input name="habitName" value="${escapeHtml(state.filters.habitName)}"
                        placeholder="Habit name" autocomplete="off">
                    <input name="username" value="${escapeHtml(state.filters.username)}"
                        placeholder="Username" autocomplete="off">
                    <input name="categoryName" value="${escapeHtml(state.filters.categoryName)}"
                        placeholder="Category" autocomplete="off">
                </div>
                <div class="habits-search-actions">
                    <button class="primary-button" type="submit">Search</button>
                    <button class="ghost-button" type="button" data-reset-filter="true">Reset</button>
                </div>
            </form>
        </section>
        <section class="habits-browse">
            <div class="entity-grid">
            ${pageHabits.map((habit) => `
                <article class="entity-card">
                    <div class="entity-card-body">
                        <h3>${escapeHtml(habit.name)}</h3>
                        <p>${escapeHtml(habit.description || "No description")}</p>
                        <div class="meta-line">${escapeHtml(habit.username)}</div>
                        <div class="chip-row">
                            ${(habit.categories || []).map((category) => `<span class="chip">${escapeHtml(category.name)}</span>`).join("") || `<span class="chip muted">No categories</span>`}
                        </div>
                    </div>
                    <div class="entity-card-actions">
                        <button type="button" class="entity-btn entity-btn--secondary" data-open-edit="habit" data-id="${habit.id}">Edit</button>
                        <button type="button" class="entity-btn entity-btn--danger" data-delete="habits" data-id="${habit.id}">Delete</button>
                    </div>
                </article>
            `).join("") || `<div class="habits-empty empty-state">${escapeHtml(emptyHint)}</div>`}
            </div>
            ${state.searchResult ? `
                <div class="pager habits-pager">
                    <button type="button" class="ghost-button" data-page-dir="-1" ${state.searchResult.first ? "disabled" : ""}>Previous</button>
                    <span>Page ${state.searchResult.number + 1} of ${Math.max(state.searchResult.totalPages || 1, 1)} · ${state.searchResult.totalElements ?? 0} total</span>
                    <button type="button" class="ghost-button" data-page-dir="1" ${state.searchResult.last ? "disabled" : ""}>Next</button>
                </div>
            ` : ""}
        </section>
    `;
}

function renderUsersTab() {
    return `
        <section class="entity-grid">
            ${state.users.map((user) => `
                <article class="entity-card">
                    <div class="entity-card-body">
                        <h3>${escapeHtml(user.username)}</h3>
                        <p>${escapeHtml(user.email)}</p>
                        <div class="linked-block">
                            ${getHabitsByUser(user.id).map((habit) => `<span class="chip">${escapeHtml(habit.name)}</span>`).join("") || `<span class="chip muted">No habits yet</span>`}
                        </div>
                    </div>
                    <div class="entity-card-actions">
                        <button type="button" class="entity-btn entity-btn--secondary" data-open-edit="user" data-id="${user.id}">Edit</button>
                        <button type="button" class="entity-btn entity-btn--danger" data-delete="users" data-id="${user.id}">Delete</button>
                    </div>
                </article>
            `).join("") || `<section class="panel"><div class="empty-state">No users yet.</div></section>`}
        </section>
    `;
}

function renderCategoriesTab() {
    return `
        <section class="entity-grid">
            ${state.categories.map((category) => `
                <article class="entity-card">
                    <div class="entity-card-body">
                        <h3>${escapeHtml(category.name)}</h3>
                        <p>${escapeHtml(category.description || "No description")}</p>
                        <div class="linked-block">
                            ${getHabitsByCategory(category.id).map((habit) => `<span class="chip">${escapeHtml(habit.name)}</span>`).join("") || `<span class="chip muted">No linked habits</span>`}
                        </div>
                    </div>
                    <div class="entity-card-actions">
                        <button type="button" class="entity-btn entity-btn--secondary" data-open-edit="category" data-id="${category.id}">Edit</button>
                        <button type="button" class="entity-btn entity-btn--danger" data-delete="categories" data-id="${category.id}">Delete</button>
                    </div>
                </article>
            `).join("") || `<section class="panel"><div class="empty-state">No categories yet.</div></section>`}
        </section>
    `;
}

function renderGoalsTab() {
    return `
        <section class="entity-grid">
            ${state.goals.map((goal) => `
                <article class="entity-card">
                    <div class="entity-card-body">
                        <h3>${escapeHtml(goal.name)}</h3>
                        <p>${escapeHtml(goal.condition)}</p>
                        <div class="meta-line">${escapeHtml(goal.habitName)}</div>
                    </div>
                    <div class="entity-card-actions">
                        <button type="button" class="entity-btn entity-btn--secondary" data-open-edit="goal" data-id="${goal.id}">Edit</button>
                        <button type="button" class="entity-btn entity-btn--danger" data-delete="goals" data-id="${goal.id}">Delete</button>
                    </div>
                </article>
            `).join("") || `<section class="panel"><div class="empty-state">No goals yet.</div></section>`}
        </section>
    `;
}

function renderLogsTab() {
    return `
        <section class="entity-grid">
            ${state.logs.map((log) => {
                const habit = state.habits.find((item) => item.id === log.habitId);
                return `
                    <article class="entity-card">
                        <div class="entity-card-body">
                            <h3>${escapeHtml(habit?.name || `Habit #${log.habitId}`)}</h3>
                            <p>${escapeHtml(log.logDate)}</p>
                        </div>
                        <div class="entity-card-actions">
                            <button type="button" class="entity-btn entity-btn--danger" data-delete="logs" data-id="${log.id}">Delete</button>
                        </div>
                    </article>
                `;
            }).join("") || `<section class="panel"><div class="empty-state">No activity yet.</div></section>`}
        </section>
    `;
}

function renderModal() {
    if (!state.modal.type) {
        return "";
    }

    const entity = state.modal.mode === "edit" ? findEntity(state.modal.type, state.modal.id) : null;
    return `
        <div class="modal-backdrop" data-close-modal="true">
           <div class="modal-card" role="dialog" aria-modal="true" onclick="event.stopPropagation()">
                <div class="modal-head">
                    <h3>${getModalTitle()}</h3>
                    <button class="icon-button" data-close-modal="true">Close</button>
                </div>
                ${renderModalForm(entity)}
            </div>
        </div>
    `;
}

function renderModalForm(entity) {
    switch (state.modal.type) {
        case "user":
            return `
                <form id="user-form" class="modal-form" novalidate>
                    <label class="modal-field">Username<input name="username" autocomplete="username" value="${escapeHtml(entity?.username || "")}"></label>
                    <label class="modal-field">Email<input name="email" type="email" autocomplete="email" value="${escapeHtml(entity?.email || "")}"></label>
                    <div class="modal-actions">
                        <button class="primary-button" type="submit">${state.modal.mode === "edit" ? "Save" : "Create"}</button>
                        <button class="ghost-button" type="button" data-close-modal="true">Cancel</button>
                    </div>
                </form>
            `;
        case "habit":
            return `
                <form id="habit-form" class="modal-form" novalidate>
                    <label class="modal-field">Name<input name="name" autocomplete="off" value="${escapeHtml(entity?.name || "")}"></label>
                    <label class="modal-field">Owner
                        <select class="modal-select" name="userId" ${state.modal.mode === "edit" ? "disabled" : ""}>
                            <option value="">Choose user</option>
                            ${state.users.map((user) => `<option value="${user.id}" ${(entity?.userId === user.id) ? "selected" : ""}>${escapeHtml(user.username)}</option>`).join("")}
                        </select>
                    </label>
                    <label class="modal-field">Description<textarea name="description">${escapeHtml(entity?.description || "")}</textarea></label>
                    <label class="modal-field">Categories
                        <select class="modal-select modal-select--multi" name="categoryIds" multiple size="${Math.min(6, Math.max(3, state.categories.length || 3))}">
                            ${state.categories.map((category) => `
                                <option value="${category.id}" ${(entity?.categories || []).some((item) => item.id === category.id) ? "selected" : ""}>${escapeHtml(category.name)}</option>
                            `).join("")}
                        </select>
                    </label>
                    <div class="modal-actions">
                        <button class="primary-button" type="submit">${state.modal.mode === "edit" ? "Save" : "Create"}</button>
                        <button class="ghost-button" type="button" data-close-modal="true">Cancel</button>
                    </div>
                </form>
            `;
        case "category":
            return `
                <form id="category-form" class="modal-form" novalidate>
                    <label class="modal-field">Name<input name="name" autocomplete="off" value="${escapeHtml(entity?.name || "")}"></label>
                    <label class="modal-field">Description<textarea name="description">${escapeHtml(entity?.description || "")}</textarea></label>
                    <div class="modal-actions">
                        <button class="primary-button" type="submit">${state.modal.mode === "edit" ? "Save" : "Create"}</button>
                        <button class="ghost-button" type="button" data-close-modal="true">Cancel</button>
                    </div>
                </form>
            `;
        case "goal":
            return `
                <form id="goal-form" class="modal-form" novalidate>
                    <label class="modal-field">Name<input name="name" autocomplete="off" value="${escapeHtml(entity?.name || "")}"></label>
                    <label class="modal-field">Habit
                        <select class="modal-select" name="habitId">
                            <option value="">Choose habit</option>
                            ${state.habits.map((habit) => `<option value="${habit.id}" ${(entity?.habitId === habit.id) ? "selected" : ""}>${escapeHtml(habit.name)}</option>`).join("")}
                        </select>
                    </label>
                    <label class="modal-field">Condition<textarea name="condition">${escapeHtml(entity?.condition || "")}</textarea></label>
                    <div class="modal-actions">
                        <button class="primary-button" type="submit">${state.modal.mode === "edit" ? "Save" : "Create"}</button>
                        <button class="ghost-button" type="button" data-close-modal="true">Cancel</button>
                    </div>
                </form>
            `;
        case "log":
            return `
                <form id="log-form" class="modal-form" novalidate>
                    <label class="modal-field">Habit
                        <select class="modal-select" name="habitId">
                            <option value="">Choose habit</option>
                            ${state.habits.map((habit) => `<option value="${habit.id}">${escapeHtml(habit.name)} (${escapeHtml(habit.username)})</option>`).join("")}
                        </select>
                    </label>
                    <div class="modal-actions">
                        <button class="primary-button" type="submit">Create</button>
                        <button class="ghost-button" type="button" data-close-modal="true">Cancel</button>
                    </div>
                </form>
            `;
        default:
            return "";
    }
}

function bindAppEvents() {
    document.querySelectorAll("[data-tab]").forEach((button) => {
        button.addEventListener("click", () => toggleTab(button.dataset.tab));
    });

    document.querySelectorAll("[data-open-create]").forEach((button) => {
        button.addEventListener("click", () => openModal(button.dataset.openCreate, "create"));
    });

    document.querySelectorAll("[data-open-edit]").forEach((button) => {
        button.addEventListener("click", () => openModal(button.dataset.openEdit, "edit", Number(button.dataset.id)));
    });

    document.querySelectorAll("[data-delete]").forEach((button) => {
        button.addEventListener("click", () => confirmDelete(button.dataset.delete, Number(button.dataset.id)));
    });

    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) {
        backdrop.addEventListener('click', (event) => {
            if (event.target === backdrop) {
                closeModal();
            }
        });
    }

    document.querySelectorAll('[data-close-modal="true"]:not(.modal-backdrop)').forEach((button) => {
        button.addEventListener('click', () => closeModal());
    });

    ["user-form", "habit-form", "category-form", "goal-form", "log-form"].forEach((formId) => {
        const form = document.getElementById(formId);
        if (form) {
            attachModalFieldHints(form);
        }
    });

    document.getElementById("user-form")?.addEventListener("submit", handleUserSubmit);
    document.getElementById("habit-form")?.addEventListener("submit", handleHabitSubmit);
    document.getElementById("category-form")?.addEventListener("submit", handleCategorySubmit);
    document.getElementById("goal-form")?.addEventListener("submit", handleGoalSubmit);
    document.getElementById("log-form")?.addEventListener("submit", handleLogSubmit);
    document.getElementById("search-form")?.addEventListener("submit", handleSearchSubmit);

    document.querySelector("[data-reset-filter='true']")?.addEventListener("click", async () => {
        state.filters = { username: "", categoryName: "", habitName: "", page: 0 };
        await runHabitSearch();
        renderApp();
    });

    document.querySelectorAll("[data-page-dir]").forEach((button) => {
        button.addEventListener("click", () => changeSearchPage(Number(button.dataset.pageDir)));
    });

    document.querySelector("[data-complete-all-today]")?.addEventListener("click", () => {
        completeAllHabitsToday();
    });

    document.querySelectorAll("[data-complete-habit]").forEach((button) => {
        button.addEventListener("click", () => {
            const habitId = Number(button.dataset.completeHabit);
            if (Number.isFinite(habitId)) {
                markHabitCompleteToday(habitId);
            }
        });
    });

    document.querySelectorAll("[data-undo-log]").forEach((button) => {
        button.addEventListener("click", () => {
            const logIdRaw = button.dataset.undoLog;
            if (logIdRaw === undefined || logIdRaw === "") {
                return;
            }
            const logId = Number(logIdRaw);
            if (Number.isFinite(logId) && logId > 0) {
                undoHabitCompletionToday(logId);
            }
        });
    });
}
