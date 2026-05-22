function initBookManager() {
    const form = document.getElementById('bookForm');
    const status = document.getElementById('bookStatus');
    const tbody = document.getElementById('booksTableBody');
    const submitBtn = document.getElementById('bookSubmitBtn');
    const cancelBtn = document.getElementById('bookCancelBtn');
    const searchInput = document.getElementById('bookSearchInput');
    const categoryFilter = document.getElementById('bookCategoryFilter');
    const prevBtn = document.getElementById('booksPrevBtn');
    const nextBtn = document.getElementById('booksNextBtn');
    const pageInfo = document.getElementById('booksPageInfo');

    if (!form || !status || !tbody || !submitBtn || !cancelBtn || !searchInput || !categoryFilter || !prevBtn || !nextBtn || !pageInfo) {
        return;
    }

    let editingId = null;
    let currentPage = 1;
    const pageSize = 5;
    let totalPages = 1;

    function setStatus(message, isError) {
        status.textContent = message;
        status.style.display = 'block';
        status.style.color = isError ? '#b91c1c' : '#166534';
    }

    function clearStatus() {
        status.style.display = 'none';
        status.textContent = '';
    }

    function readForm() {
        return {
            title: document.getElementById('bookTitle').value.trim(),
            author: document.getElementById('bookAuthor').value.trim(),
            category: document.getElementById('bookCategory').value.trim(),
            uniqueCode: document.getElementById('bookCode').value.trim()
        };
    }

    function resetForm() {
        form.reset();
        editingId = null;
        submitBtn.textContent = 'Add Book';
        cancelBtn.style.display = 'none';
    }

    function renderBooks(pageItems) {
        if (!pageItems.length) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-cell">No books found for the current filter.</td></tr>';
        } else {
            tbody.innerHTML = pageItems
            .map((book) => `
                <tr>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.category}</td>
                    <td>${book.uniqueCode}</td>
                            <td>
                                <button class="btn-action" data-action="edit" data-id="${book.id}">Edit</button>
                                <button class="btn-action danger" data-action="delete" data-id="${book.id}">Delete</button>
                                <button class="btn-action" data-action="queue" data-id="${book.id}" data-title="${book.title}">Queue</button>
                            </td>
                </tr>
            `)
            .join('');

        }

        pageInfo.textContent = `Page ${currentPage} of ${Math.max(totalPages, 1)}`;
        prevBtn.disabled = currentPage <= 1;
        nextBtn.disabled = currentPage >= totalPages;
    }

    async function refreshCategoryFilter() {
        const categories = await apiRequest('/api/books/categories');
        const current = categoryFilter.value;

        categoryFilter.innerHTML = '<option value="">All categories</option>' +
            categories.map((category) => `<option value="${category}">${category}</option>`).join('');

        if (current && categories.includes(current)) {
            categoryFilter.value = current;
        }
    }

    async function loadBooks() {
        clearStatus();
        try {
            const query = searchInput.value.trim();
            const category = categoryFilter.value;
            const page = currentPage - 1;
            const result = await apiRequest(
                `/api/books?query=${encodeURIComponent(query)}&category=${encodeURIComponent(category)}&page=${page}&size=${pageSize}`
            );

            totalPages = Math.max(result.totalPages || 1, 1);
            if (currentPage > totalPages) {
                currentPage = totalPages;
                return loadBooks();
            }

            renderBooks(result.content || []);
        } catch (err) {
            setStatus(err.message, true);
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearStatus();

        const payload = readForm();
        if (!payload.title || !payload.author || !payload.category || !payload.uniqueCode) {
            setStatus('All book fields are required.', true);
            return;
        }

        submitBtn.disabled = true;
        submitBtn.textContent = editingId ? 'Saving...' : 'Adding...';

        try {
            if (editingId) {
                await apiRequest(`/api/books/${editingId}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                setStatus('Book updated successfully.', false);
            } else {
                await apiRequest('/api/books', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                setStatus('Book added successfully.', false);
            }

            resetForm();
            await loadBooks();
        } catch (err) {
            setStatus(err.message, true);
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = editingId ? 'Save Changes' : 'Add Book';
        }
    });

    cancelBtn.addEventListener('click', () => {
        clearStatus();
        resetForm();
    });

    searchInput.addEventListener('input', () => {
        currentPage = 1;
        loadBooks();
    });

    categoryFilter.addEventListener('change', () => {
        currentPage = 1;
        loadBooks();
    });

    prevBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            currentPage -= 1;
            loadBooks();
        }
    });

    nextBtn.addEventListener('click', () => {
        if (currentPage < totalPages) {
            currentPage += 1;
            loadBooks();
        }
    });

    tbody.addEventListener('click', async (e) => {
        const target = e.target;
        const action = target.getAttribute('data-action');
        const id = target.getAttribute('data-id');
        if (!action || !id) {
            return;
        }

        if (action === 'delete') {
            clearStatus();
            if (!window.confirm('Delete this book?')) {
                return;
            }

            try {
                await apiRequest(`/api/books/${id}`, { method: 'DELETE' });
                setStatus('Book deleted successfully.', false);
                if (editingId && String(editingId) === id) {
                    resetForm();
                }
                await loadBooks();
            } catch (err) {
                setStatus(err.message, true);
            }
            return;
        }

        if (action === 'edit') {
            clearStatus();
            try {
                const book = await apiRequest(`/api/books/${id}`);

                document.getElementById('bookTitle').value = book.title;
                document.getElementById('bookAuthor').value = book.author;
                document.getElementById('bookCategory').value = book.category;
                document.getElementById('bookCode').value = book.uniqueCode;
                editingId = book.id;
                submitBtn.textContent = 'Save Changes';
                cancelBtn.style.display = 'inline-flex';
            } catch (err) {
                setStatus(err.message, true);
            }
        }
        if (action === 'queue') {
            clearStatus();
            try {
                const bookId = id;
                const title = target.getAttribute('data-title') || '';
                const panel = document.getElementById('queuePanel');
                const bookTitleEl = document.getElementById('queueBookTitle');
                const listBody = document.getElementById('queueListBody');
                panel.style.display = 'block';
                bookTitleEl.textContent = title;
                listBody.innerHTML = '<tr><td colspan="4">Loading queue...</td></tr>';

                const result = await apiRequest(`/api/queues/book/${bookId}`);
                if (!Array.isArray(result) || !result.length) {
                    listBody.innerHTML = '<tr><td colspan="4">No queue entries for this book.</td></tr>';
                    return;
                }

                listBody.innerHTML = result.map((q, idx) => `
                    <tr>
                        <td>${idx + 1}</td>
                        <td>${q.userName || q.user.email || q.userId}</td>
                        <td>${new Date(q.requestedAt).toLocaleString()}</td>
                        <td>
                            <button class="btn-action danger" data-action="adminRemoveQueue" data-queueid="${q.id}">Remove</button>
                        </td>
                    </tr>
                `).join('');
            } catch (err) {
                setStatus(err.message, true);
            }
            return;
        }

        if (action === 'adminRemoveQueue') {
            clearStatus();
            const queueId = target.getAttribute('data-queueid');
            if (!queueId) return;
            if (!window.confirm('Remove this queue entry?')) return;
            try {
                await apiRequest(`/api/queues/admin/${queueId}`, { method: 'DELETE' });
                setStatus('Queue entry removed.', false);
                // refresh current panel
                const panel = document.getElementById('queuePanel');
                if (panel && panel.style.display !== 'none') {
                    // trigger a reload by clicking the same book row - simpler: reload books list
                    loadBooks();
                }
            } catch (err) {
                setStatus(err.message, true);
            }
            return;
        }
    });

    resetForm();
    refreshCategoryFilter().then(loadBooks).catch((err) => setStatus(err.message, true));
}
