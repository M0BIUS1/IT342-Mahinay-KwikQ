function initAdminQueue() {
    const searchInput = document.getElementById('adminQueueSearch');
    const searchBtn = document.getElementById('adminQueueSearchBtn');
    const booksBody = document.getElementById('adminQueueBooksBody');
    const queuePanel = document.getElementById('adminBookQueuePanel');
    const queueTitle = document.getElementById('adminQueueForBookTitle');
    const queueBody = document.getElementById('adminBookQueueBody');

    if (!searchInput || !searchBtn || !booksBody || !queuePanel || !queueBody) return;

    async function loadBooks(query = '') {
        booksBody.innerHTML = '<tr><td colspan="3">Loading...</td></tr>';
        try {
            const res = await apiRequest(`/api/books/search?query=${encodeURIComponent(query)}&page=0&size=50`);
            const items = res.content || [];
            if (!items.length) {
                booksBody.innerHTML = '<tr><td colspan="3">No books found.</td></tr>';
                return;
            }

            booksBody.innerHTML = items.map(b => `
                <tr>
                    <td>${b.title}</td>
                    <td>${b.queueSize || '-'}</td>
                    <td><button class="btn-action" data-action="viewQueue" data-bookid="${b.id}" data-title="${b.title}">View Queue</button></td>
                </tr>
            `).join('');
        } catch (err) {
            booksBody.innerHTML = `<tr><td colspan="3">${err.message}</td></tr>`;
        }
    }

    async function viewBookQueue(bookId, title) {
        queueBody.innerHTML = '<tr><td colspan="4">Loading queue...</td></tr>';
        queuePanel.style.display = 'block';
        queueTitle.textContent = `Queue for: ${title}`;
        try {
            const list = await apiRequest(`/api/queues/admin/book/${bookId}`);
            if (!Array.isArray(list) || !list.length) {
                queueBody.innerHTML = '<tr><td colspan="4">No queue entries.</td></tr>';
                return;
            }

            queueBody.innerHTML = list.map((q, idx) => `
                <tr>
                    <td>${idx + 1}</td>
                    <td>${q.userName || (q.user && (q.user.name || q.user.email)) || q.userId}</td>
                    <td>${new Date(q.requestedAt).toLocaleString()}</td>
                    <td>
                        <button class="btn-action danger" data-action="removeQueue" data-queueid="${q.id}">Remove</button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            queueBody.innerHTML = `<tr><td colspan="4">${err.message}</td></tr>`;
        }
    }

    document.addEventListener('click', async (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;
        const action = btn.getAttribute('data-action');

        if (action === 'viewQueue') {
            const bookId = btn.getAttribute('data-bookid');
            const title = btn.getAttribute('data-title') || '';
            if (!bookId) return;
            await viewBookQueue(bookId, title);
        }

        if (action === 'removeQueue') {
            const queueId = btn.getAttribute('data-queueid');
            if (!queueId) return;
            if (!confirm('Remove this queue entry?')) return;
            try {
                await apiRequest(`/api/queues/admin/${queueId}`, { method: 'DELETE' });
                alert('Removed queue entry. Refreshing list.');
                // refresh currently visible queue (try to find bookId from title text)
                const titleText = queueTitle.textContent.replace('Queue for: ', '');
                // attempt to find book row by title to reload queue
                const rowBtn = Array.from(document.querySelectorAll('button[data-action="viewQueue"]')).find(b => b.getAttribute('data-title') === titleText);
                if (rowBtn) {
                    rowBtn.click();
                } else {
                    // fallback: reload books
                    loadBooks(searchInput.value.trim());
                }
            } catch (err) {
                alert(err.message);
            }
        }
    });

    searchBtn.addEventListener('click', () => loadBooks(searchInput.value.trim()));
    searchInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') loadBooks(searchInput.value.trim()); });

    // initial load
    loadBooks();
}

window.initAdminQueue = initAdminQueue;
