// main.js - Simple shared functions

const API_BASE_URL = (
  localStorage.getItem("BLOGHUB_API_BASE_URL") ||
  window.API_BASE_URL ||
  (window.location.hostname === "localhost" ||
  window.location.hostname === "127.0.0.1"
    ? "http://localhost:8082"
    : "https://bloghub-backend.onrender.com")
).replace(/\/$/, "");

function apiUrl(path) {
  if (!path) return API_BASE_URL;
  if (/^https?:\/\//i.test(path)) return path;
  return `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;
}

// Theme toggle
function updateThemeIcons(theme) {
  const iconClass = theme === "dark" ? "fas fa-sun" : "fas fa-moon";
  document
    .querySelectorAll(
      ".theme-toggle i, .theme-toggle-login i, .theme-toggle-register i",
    )
    .forEach((icon) => {
      icon.className = iconClass;
    });
}

function getUserName() {
  return (sessionStorage.getItem("userName") || "").trim();
}

function getUserRole() {
  return sessionStorage.getItem("userRole") || "USER";
}

function getProfileInitial() {
  const userName = getUserName();
  return userName ? userName.charAt(0).toUpperCase() : "U";
}

function applyTheme(theme) {
  const root = document.documentElement;
  if (root) {
    root.setAttribute("data-theme", theme);
  }

  if (document.body) {
    document.body.setAttribute("data-theme", theme);
  }

  updateThemeIcons(theme);
}

function toggleTheme() {
  const currentTheme =
    document.body?.getAttribute("data-theme") ||
    document.documentElement.getAttribute("data-theme") ||
    "light";
  const newTheme = currentTheme === "dark" ? "light" : "dark";

  applyTheme(newTheme);
  localStorage.setItem("theme", newTheme);
}

function createIconButton(className, iconClass, onClickName, ariaLabel) {
  const button = document.createElement("button");
  button.type = "button";
  button.className = className;
  button.setAttribute("onclick", onClickName);
  button.setAttribute("aria-label", ariaLabel);
  button.innerHTML = `<i class="${iconClass}"></i>`;
  return button;
}

function createProfileButton() {
  const button = document.createElement("button");
  button.type = "button";
  button.className = "nav-profile-btn";
  button.setAttribute("aria-label", "Open profile menu");
  button.setAttribute("aria-haspopup", "true");
  button.setAttribute("aria-expanded", "false");
  button.title = getUserName() || "Profile";
  button.textContent = getProfileInitial();
  button.addEventListener("click", () => {
    if (window.innerWidth <= 770) {
      toggleMobileMenu();
    } else {
      toggleProfileMenu();
    }
  });
  return button;
}

function closeProfileMenu() {
  const profileMenu = document.querySelector(".nav-profile-menu");
  if (!profileMenu) return;
  profileMenu.classList.remove("open");
}

function toggleProfileMenu() {
  const profileMenu = document.querySelector(".nav-profile-menu");
  if (!profileMenu) return;
  profileMenu.classList.toggle("open");
}

function ensureDesktopProfileMenu(navActions) {
  let profileMenu = navActions.querySelector(".nav-profile-menu");
  if (!profileMenu) {
    profileMenu = document.createElement("div");
    profileMenu.className = "nav-profile-menu";
    profileMenu.innerHTML = `
      <div class="nav-profile-meta">
        <strong>${getUserName() || "User"}</strong>
        <span>${getUserRole()}</span>
      </div>
      <button type="button" class="nav-profile-item" data-action="theme">
        <i class="fas fa-circle-half-stroke"></i>
        <span>Toggle Theme</span>
      </button>
      <button type="button" class="nav-profile-item nav-profile-item-danger" data-action="logout">
        <i class="fas fa-right-from-bracket"></i>
        <span>Logout</span>
      </button>
    `;

    profileMenu
      .querySelector('[data-action="theme"]')
      .addEventListener("click", () => {
        toggleTheme();
      });

    profileMenu
      .querySelector('[data-action="logout"]')
      .addEventListener("click", async () => {
        closeProfileMenu();
        await logout();
      });

    navActions.append(profileMenu);
  } else {
    const nameNode = profileMenu.querySelector(".nav-profile-meta strong");
    const roleNode = profileMenu.querySelector(".nav-profile-meta span");
    if (nameNode) nameNode.textContent = getUserName() || "User";
    if (roleNode) roleNode.textContent = getUserRole();
  }
}

function ensureNavActionControls(navActions) {
  if (!navActions) return;

  let profileButton = navActions.querySelector(".nav-profile-btn");
  if (!profileButton) {
    profileButton = createProfileButton();
    navActions.append(profileButton);
  } else {
    profileButton.textContent = getProfileInitial();
    profileButton.title = getUserName() || "Profile";
  }

  if (!navActions.querySelector(".theme-toggle")) {
    navActions.append(
      createIconButton(
        "theme-toggle",
        "fas fa-moon",
        "toggleTheme()",
        "Toggle theme",
      ),
    );
  }

  ensureDesktopProfileMenu(navActions);
}

function getCurrentPage() {
  const rawPath = window.location.pathname || "";
  const pageName = rawPath.split("/").pop() || "index.html";
  return pageName.toLowerCase();
}

function getActiveNavHref(pageName) {
  const pageToHrefMap = {
    "": "index.html",
    "index.html": "index.html",
    "posts.html": "posts.html",
    "post.html": "posts.html",
    "post-update.html": "posts.html",
    "categories.html": "categories.html",
    "post-create.html": "post-create.html",
    "users.html": "users.html",
    "user-create.html": "users.html",
    "category-create.html": "category-create.html",
  };

  return pageToHrefMap[pageName] || null;
}

function setActiveNavLink() {
  const navLinks = document.querySelectorAll(".nav-menu .nav-link");
  if (!navLinks.length) return;

  navLinks.forEach((link) => {
    link.classList.remove("active");
  });

  const currentPage = getCurrentPage();
  const activeHref = getActiveNavHref(currentPage);
  if (!activeHref) return;

  const targetLink = Array.from(navLinks).find((link) => {
    const href = (link.getAttribute("href") || "").toLowerCase();
    return href === activeHref;
  });

  if (targetLink) {
    targetLink.classList.add("active");
  }
}

function toggleMenu() {
  toggleSidebar();
}

function closeMenu() {
  closeSidebar();
}

function getMobileMenuOverlay() {
  return document.getElementById("mobile-nav-overlay");
}

function closeMobileMenu() {
  const navMenu = document.querySelector(".nav-menu");
  const overlay = getMobileMenuOverlay();
  const drawer = overlay?.querySelector(".mobile-nav-drawer");
  const profileButton = document.querySelector(".nav-profile-btn");
  if (!navMenu) return;
  navMenu.classList.remove("active");
  if (overlay) {
    overlay.classList.remove("active");
  }
  if (drawer) {
    drawer.classList.remove("active");
  }
  if (profileButton) {
    profileButton.setAttribute("aria-expanded", "false");
  }
}

function openMobileMenu() {
  const navMenu = document.querySelector(".nav-menu");
  const overlay = getMobileMenuOverlay();
  const drawer = overlay?.querySelector(".mobile-nav-drawer");
  const profileButton = document.querySelector(".nav-profile-btn");
  if (!navMenu) return;
  if (window.innerWidth > 770) {
    return;
  }
  buildMobileMenuLinks();
  navMenu.classList.remove("active");
  if (overlay) {
    overlay.classList.add("active");
  }
  if (drawer) {
    drawer.classList.add("active");
  }
  if (profileButton) {
    profileButton.setAttribute("aria-expanded", "true");
  }
}

function toggleMobileMenu() {
  const overlay = getMobileMenuOverlay();
  const isOpen = overlay?.classList.contains("active");
  if (isOpen) {
    closeMobileMenu();
  } else {
    openMobileMenu();
  }
}

function openSidebar() {
  openMobileMenu();
}

function closeSidebar() {
  closeMobileMenu();
}

function toggleSidebar() {
  toggleMobileMenu();
}

function createMobileMenuOverlay() {
  if (getMobileMenuOverlay()) return;

  const overlay = document.createElement("div");
  overlay.id = "mobile-nav-overlay";
  overlay.className = "mobile-nav-overlay";
  overlay.innerHTML = `
    <aside class="mobile-nav-drawer" role="dialog" aria-label="Mobile navigation menu">
      <button type="button" class="mobile-nav-close" aria-label="Close menu">&times;</button>
      <div class="mobile-nav-user">
        <span class="mobile-nav-avatar">${getProfileInitial()}</span>
        <div class="mobile-nav-user-text">
          <strong>${getUserName() || "User"}</strong>
          <span>${getUserRole()}</span>
        </div>
      </div>
      <nav class="mobile-nav-links"></nav>
      <button type="button" class="btn-secondary mobile-nav-theme-toggle">Toggle Theme</button>
      <button type="button" class="btn-primary mobile-nav-logout">Logout</button>
    </aside>
  `;

  overlay.addEventListener("click", (event) => {
    if (event.target === overlay) {
      closeSidebar();
    }
  });

  overlay
    .querySelector(".mobile-nav-close")
    .addEventListener("click", closeSidebar);

  overlay
    .querySelector(".mobile-nav-theme-toggle")
    .addEventListener("click", () => {
      toggleTheme();
    });

  overlay
    .querySelector(".mobile-nav-logout")
    .addEventListener("click", async () => {
      closeSidebar();
      await logout();
    });

  document.body.appendChild(overlay);
}

function buildMobileMenuLinks() {
  const overlay = getMobileMenuOverlay();
  const desktopLinks = document.querySelectorAll(".nav-menu .nav-link");
  if (!overlay || desktopLinks.length === 0) return;

  const isAdminUser = getUserRole() === "ADMIN";
  const linksContainer = overlay.querySelector(".mobile-nav-links");
  linksContainer.innerHTML = "";

  const linkOrder = [
    "index.html",
    "posts.html",
    "categories.html",
    "users.html",
    "category-create.html",
    "post-create.html",
  ];

  const normalizedLinks = Array.from(desktopLinks);
  const orderedLinks = [];

  linkOrder.forEach((href) => {
    const match = normalizedLinks.find(
      (link) => (link.getAttribute("href") || "").toLowerCase() === href,
    );
    if (match) {
      orderedLinks.push(match);
    }
  });

  normalizedLinks.forEach((link) => {
    if (!orderedLinks.includes(link)) {
      orderedLinks.push(link);
    }
  });

  orderedLinks.forEach((link) => {
    if (link.classList.contains("admin-only") && !isAdminUser) {
      return;
    }

    const mobileLink = document.createElement("a");
    mobileLink.href = link.getAttribute("href") || "#";
    mobileLink.className = "mobile-nav-link";
    mobileLink.textContent = link.textContent || "";
    if (link.classList.contains("active")) {
      mobileLink.classList.add("active");
    }

    mobileLink.addEventListener("click", () => {
      closeSidebar();
    });

    linksContainer.appendChild(mobileLink);
  });

  const avatar = overlay.querySelector(".mobile-nav-avatar");
  const nameNode = overlay.querySelector(".mobile-nav-user-text strong");
  const roleNode = overlay.querySelector(".mobile-nav-user-text span");
  if (avatar) avatar.textContent = getProfileInitial();
  if (nameNode) nameNode.textContent = getUserName() || "User";
  if (roleNode) roleNode.textContent = getUserRole();
}

function initMobileMenu() {
  const navContainer = document.querySelector(".nav-container");
  const navMenu = document.querySelector(".nav-menu");
  const navActions = document.querySelector(".nav-actions");

  if (!navContainer || !navMenu || !navActions) return;

  ensureNavActionControls(navActions);
  createMobileMenuOverlay();
  buildMobileMenuLinks();
  navMenu.querySelectorAll(".nav-link").forEach((link) => {
    link.addEventListener("click", () => {
      if (window.innerWidth <= 770) {
        closeSidebar();
      }
    });
  });

  document.addEventListener("click", (event) => {
    if (
      !event.target.closest(".nav-actions") &&
      !event.target.closest(".nav-menu") &&
      !event.target.closest(".mobile-nav-drawer")
    ) {
      closeSidebar();
      closeProfileMenu();
    }
  });

  window.addEventListener("resize", () => {
    if (window.innerWidth > 770) {
      closeSidebar();
    }
    buildMobileMenuLinks();
    closeProfileMenu();
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeSidebar();
      closeProfileMenu();
    }
  });
}

// Initialize theme
function initTheme() {
  const savedTheme = localStorage.getItem("theme") || "dark";
  applyTheme(savedTheme);
}

// Simple API GET (with session cookies)
async function apiGet(url) {
  const response = await fetch(url, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
    credentials: "include", // ← Important: Send cookies with request
  });

  if (response.status === 401 || response.status === 403) {
    window.location.href = "login.html";
    return null;
  }

  if (!response.ok) throw new Error("Request failed");
  return await response.json();
}

// Simple API POST (with session cookies)
async function apiPost(url, data) {
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include", // ← Important: Send cookies with request
    body: JSON.stringify(data),
  });

  if (response.status === 401 || response.status === 403) {
    window.location.href = "login.html";
    return null;
  }

  if (!response.ok) throw new Error("Request failed");
  return await response.json();
}

// Simple API DELETE (with session cookies)
async function apiDelete(url) {
  const response = await fetch(url, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
    credentials: "include", // ← Important: Send cookies with request
  });

  if (response.status === 401 || response.status === 403) {
    window.location.href = "login.html";
    return null;
  }

  if (!response.ok) throw new Error("Request failed");
  return true;
}

// Simple API PUT (with session cookies)
async function apiPut(url, data) {
  const response = await fetch(url, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    credentials: "include", // ← Important: Send cookies with request
    body: JSON.stringify(data),
  });

  if (response.status === 401 || response.status === 403) {
    window.location.href = "login.html";
    return null;
  }

  if (!response.ok) throw new Error("Request failed");
  return await response.json();
}

// Show toast notification
function showToast(message, type = "info") {
  const toast = document.getElementById("toast");
  if (!toast) return;

  toast.textContent = message;
  toast.className = `toast ${type} show`;

  setTimeout(() => {
    toast.classList.remove("show");
  }, 3000);
}

// Update navbar with user info
function updateNavbar() {
  const navActions = document.querySelector(".nav-actions");
  if (!navActions) return;

  setActiveNavLink();
  ensureNavActionControls(navActions);
}

// Logout function - calls backend to invalidate session
async function logout() {
  try {
    // Call backend logout endpoint to invalidate session
    await fetch(apiUrl("/api/auth/logout"), {
      method: "POST",
      credentials: "include", // Send session cookie
    });
  } catch (error) {
    console.error("Logout error:", error);
  }

  // Clear frontend session data
  sessionStorage.clear();
  window.location.href = "login.html";
}

// Check if user is admin
function isAdmin() {
  return sessionStorage.getItem("userRole") === "ADMIN";
}

// Hide elements for non-admin users
function applyRoleBasedUI() {
  if (!isAdmin()) {
    // Hide admin-only elements
    document.querySelectorAll(".admin-only").forEach((el) => {
      el.style.display = "none";
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  initTheme();
  setActiveNavLink();
  initMobileMenu();
});

if (document.readyState === "loading") {
  const earlyTheme = localStorage.getItem("theme") || "dark";
  document.documentElement.setAttribute("data-theme", earlyTheme);
} else {
  initTheme();
}
