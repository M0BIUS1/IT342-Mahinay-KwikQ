// KwikQ Auth

const API = '/api/auth';

async function apiRegister(name, email, password) {
    const res = await fetch(`${API}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
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
        role: data.role
    }));
}

function getAuth() {
    try {
        return JSON.parse(localStorage.getItem('kwikq_auth'));
    } catch {
        return null;
    }
}

function logout() {
    localStorage.removeItem('kwikq_auth');
    window.location.href = '/index.html';
}
