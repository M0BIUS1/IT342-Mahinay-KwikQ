// KwikQ Auth

const API = '/api/auth';
const REDIRECT_STATE_KEY = 'kwikq_redirect_state';

function normalizeRole(role) {
    let value = (role || '').toString().trim().toUpperCase();

    if (value.startsWith('ROLE_')) {
        value = value.substring(5);
    }

    if (value === 'USER' || value === 'PATRON') {
        return 'STUDENT';
    }

    if (value === 'STAFF') {
        return 'ADMIN';
    }

    return value;
}

async function apiRegister(name, email, password, role) {
    const res = await fetch(`${API}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password, role })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Registration failed');
    return data;
}

async function apiLogin(email, password) {
    const res = await fetch(`${API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Invalid email or password');
    return data;
}

function saveAuth(data) {
    localStorage.setItem('kwikq_auth', JSON.stringify({
        token: data.token,
        id: data.id,
        name: data.name,
        email: data.email,
        role: normalizeRole(data.role)
    }));
}

function getAuth() {
    try {
        const auth = JSON.parse(localStorage.getItem('kwikq_auth'));
        if (!auth) {
            return null;
        }

        if (!auth.token) {
            localStorage.removeItem('kwikq_auth');
            return null;
        }

        auth.role = normalizeRole(auth.role);
        return auth;
    } catch {
        return null;
    }
}

function logout() {
    localStorage.removeItem('kwikq_auth');
    window.location.href = '/index.html';
}

async function callProtected(path) {
    return apiRequest(path);
}

async function apiRequest(path, options = {}) {
    const auth = getAuth();
    if (!auth || !auth.token) {
        throw new Error('Please sign in first');
    }

    const res = await fetch(path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${auth.token}`,
            ...(options.headers || {})
        }
    });

    const text = await res.text();
    const data = text ? JSON.parse(text) : {};
    if (!res.ok) {
        throw new Error(data.message || `Request failed (${res.status})`);
    }
    return data;
}

function getDashboardPathByRole(role) {
    switch (normalizeRole(role)) {
        case 'ADMIN':
            return '/admin-dashboard.html';
        case 'STUDENT':
        default:
            return '/student-dashboard.html';
    }
}

function redirectToDashboard(auth) {
    if (!auth) {
        window.location.href = '/index.html';
        return;
    }

    const role = normalizeRole(auth.role);
    const target = getDashboardPathByRole(role);
    const current = window.location.pathname;

    // Stop redirect loops when an unexpected role value maps back to the same page.
    if (current === target && role !== 'ADMIN' && role !== 'STUDENT') {
        localStorage.removeItem('kwikq_auth');
        sessionStorage.removeItem(REDIRECT_STATE_KEY);
        window.location.href = '/index.html';
        return;
    }

    const now = Date.now();
    let state = null;
    try {
        state = JSON.parse(sessionStorage.getItem(REDIRECT_STATE_KEY) || 'null');
    } catch {
        state = null;
    }

    if (state && state.target === target && (now - state.timestamp) < 2500) {
        const nextCount = (state.count || 1) + 1;
        sessionStorage.setItem(REDIRECT_STATE_KEY, JSON.stringify({
            target,
            count: nextCount,
            timestamp: now
        }));

        // Break redirect ping-pong loops by resetting auth state once detected.
        if (nextCount >= 3) {
            localStorage.removeItem('kwikq_auth');
            sessionStorage.removeItem(REDIRECT_STATE_KEY);
            if (current !== '/index.html') {
                window.location.replace('/index.html');
            }
            return;
        }
    } else {
        sessionStorage.setItem(REDIRECT_STATE_KEY, JSON.stringify({
            target,
            count: 1,
            timestamp: now
        }));
    }

    if (current !== target) {
        window.location.replace(target);
    }
}

// Mobile enhancements: add a nav toggle and responsive table wrappers
document.addEventListener('DOMContentLoaded', () => {
    try {
        const nav = document.querySelector('nav');
        if (nav) {
            // wrap existing links in .nav-links if not already
            let links = nav.querySelectorAll('a');
            if (links.length > 1) {
                const wrapper = document.createElement('div');
                wrapper.className = 'nav-links';
                // move links into wrapper
                links.forEach(a => wrapper.appendChild(a.cloneNode(true)));
                // remove old anchors
                nav.querySelectorAll('a').forEach(a => a.remove());
                // add logo/title if exists
                const logo = nav.querySelector('.logo') || null;
                if (logo) {
                    nav.insertBefore(logo, nav.firstChild);
                }
                // add toggle button
                const btn = document.createElement('button');
                btn.className = 'nav-toggle';
                btn.setAttribute('aria-expanded', 'false');
                btn.innerHTML = '☰';
                btn.addEventListener('click', () => {
                    const expanded = btn.getAttribute('aria-expanded') === 'true';
                    btn.setAttribute('aria-expanded', String(!expanded));
                    nav.classList.toggle('nav-expanded');
                });
                nav.appendChild(btn);
                nav.appendChild(wrapper);
            }
        }

        // Wrap tables for responsive behavior
        if (window.matchMedia && window.matchMedia('(max-width: 768px)').matches) {
            document.querySelectorAll('table').forEach(table => {
                if (!table.closest('.table-responsive')) {
                    const wrap = document.createElement('div');
                    wrap.className = 'table-responsive';
                    table.parentNode.insertBefore(wrap, table);
                    wrap.appendChild(table);
                    // add data-labels based on thead
                    const headers = Array.from(table.querySelectorAll('thead th')).map(h => h.textContent.trim());
                    table.querySelectorAll('tbody tr').forEach(row => {
                        Array.from(row.children).forEach((td, i) => {
                            td.setAttribute('data-label', headers[i] || '');
                            td.style.setProperty('--label', '"' + (headers[i] || '') + '"');
                            td.dataset.label = headers[i] || '';
                            // set :before content via inline style is not possible; we'll set via attribute and CSS will use attr(data-label)
                        });
                    });
                }
            });
        }
    } catch (e) {
        // non-fatal
        console.warn('Mobile enhancements failed:', e);
    }
});
