const API_BASE = '/api/extensions';

document.addEventListener('DOMContentLoaded', () => {
    loadExtensions();
    setupEventListeners();
});

function setupEventListeners() {
    document.getElementById('add-btn').addEventListener('click', addCustomExtension);
    document.getElementById('custom-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addCustomExtension();
        }
    });
}

async function loadExtensions() {
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error('Failed to load extensions');

        const data = await response.json();
        renderFixedExtensions(data.fixed);
        renderCustomExtensions(data.custom);
    } catch (error) {
        console.error('Error loading extensions:', error);
    }
}

function renderFixedExtensions(extensions) {
    const container = document.getElementById('fixed-extensions');
    container.innerHTML = extensions.map(ext => `
        <label>
            <input type="checkbox"
                   data-extension="${ext.extension}"
                   ${ext.blocked ? 'checked' : ''}>
            ${ext.extension}
        </label>
    `).join('');

    container.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            updateFixedExtension(e.target.dataset.extension, e.target.checked);
        });
    });
}

function renderCustomExtensions(extensions) {
    const container = document.getElementById('custom-extensions');
    document.getElementById('custom-count').textContent = extensions.length;

    container.innerHTML = extensions.map(ext => `
        <span class="tag">
            ${ext.extension}
            <button class="delete-btn" data-id="${ext.id}" type="button">X</button>
        </span>
    `).join('');

    container.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            deleteCustomExtension(e.target.dataset.id);
        });
    });
}

async function updateFixedExtension(extension, blocked) {
    try {
        const response = await fetch(`${API_BASE}/fixed`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ extension, blocked })
        });

        if (!response.ok) {
            const error = await response.json();
            alert(error.message);
            loadExtensions();
        }
    } catch (error) {
        console.error('Error updating fixed extension:', error);
        loadExtensions();
    }
}

async function addCustomExtension() {
    const input = document.getElementById('custom-input');
    const extension = input.value.trim();

    if (!extension) {
        alert('확장자를 입력해주세요');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/custom`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ extension })
        });

        if (!response.ok) {
            const error = await response.json();
            alert(error.message);
            return;
        }

        input.value = '';
        loadExtensions();
    } catch (error) {
        console.error('Error adding custom extension:', error);
    }
}

async function deleteCustomExtension(id) {
    try {
        const response = await fetch(`${API_BASE}/custom/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const error = await response.json();
            alert(error.message);
            return;
        }

        loadExtensions();
    } catch (error) {
        console.error('Error deleting custom extension:', error);
    }
}
