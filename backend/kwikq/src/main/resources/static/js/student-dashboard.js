function initStudentDashboard() {
    const searchInput = document.getElementById('studentSearchInput');
    const searchBtn = document.getElementById('studentSearchBtn');
    const resultsBody = document.getElementById('searchResultsBody');
    const myQueuesBody = document.getElementById('myQueuesBody');

    if (!searchInput || !searchBtn || !resultsBody || !myQueuesBody) return;

    async function doSearch() {
        resultsBody.innerHTML = '<tr><td colspan="4">Searching...</td></tr>';
        try {
            const q = searchInput.value.trim();
            const res = await apiRequest(`/api/books/search?query=${encodeURIComponent(q)}&page=0&size=20`);
            const items = res.content || [];
            if (!items.length) {
                resultsBody.innerHTML = '<tr><td colspan="4">No books found.</td></tr>';
                return;
            }

            resultsBody.innerHTML = items.map(b => `
                <tr>
                    <td>${b.title}</td>
                    <td>${b.author}</td>
                    <td>${b.category}</td>
                    <td>
                        <button class="btn-action" data-bookid="${b.id}" data-action="addQueue">Add to Queue</button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            resultsBody.innerHTML = `<tr><td colspan="4">${err.message}</td></tr>`;
        }
    }

    async function refreshMyQueues() {
        myQueuesBody.innerHTML = '<tr><td colspan="4">Loading your queues...</td></tr>';
        try {
            const res = await apiRequest('/api/queues/my-queues?page=0&size=50');
            const items = (res.content || [])
            if (!items.length) {
                myQueuesBody.innerHTML = '<tr><td colspan="4">You have no queue requests.</td></tr>';
                return;
            }

            myQueuesBody.innerHTML = items.map(q => `
                <tr>
                    <td>${q.bookTitle || q.book.title}</td>
                    <td>${q.position || q.pos || '-'}</td>
                    <td>${new Date(q.requestedAt).toLocaleString()}</td>
                    <td>
                        <button class="btn-action danger" data-action="cancelQueue" data-queueid="${q.id}">Cancel</button>
                    </td>
                </tr>
            `).join('');
        } catch (err) {
            myQueuesBody.innerHTML = `<tr><td colspan="4">${err.message}</td></tr>`;
        }
    }

    document.addEventListener('click', async (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;
        const action = btn.getAttribute('data-action');

        if (action === 'addQueue') {
            const bookId = btn.getAttribute('data-bookid');
            if (!bookId) return;
            btn.disabled = true;
            try {
                await apiRequest(`/api/queues/add/${bookId}`, { method: 'POST' });
                alert('Added to queue. Refreshing your queues...');
                await refreshMyQueues();
            } catch (err) {
                alert(err.message);
            } finally {
                btn.disabled = false;
            }
        }

        if (action === 'cancelQueue') {
            const queueId = btn.getAttribute('data-queueid');
            if (!queueId) return;
            if (!confirm('Cancel this queue request?')) return;
            try {
                await apiRequest(`/api/queues/${queueId}`, { method: 'DELETE' });
                await refreshMyQueues();
            } catch (err) {
                alert(err.message);
            }
        }
    });

    searchBtn.addEventListener('click', doSearch);
    searchInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') doSearch(); });

    // initial load
    refreshMyQueues();
}

// exported for inline callers
window.initStudentDashboard = initStudentDashboard;
