const API_BASE = '/api/extensions';

document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
});

function setupEventListeners() {
    // 추가 버튼
    document.getElementById('add-btn').addEventListener('click', addCustomExtension);
    document.getElementById('custom-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addCustomExtension();
        }
    });

    // 고정 확장자 체크박스
    document.querySelectorAll('#fixed-extensions input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', (e) => {
            updateFixedExtension(e.target.dataset.extension, e.target.checked);
        });
    });

    // 커스텀 확장자 삭제 버튼
    document.querySelectorAll('.delete-btn').forEach(btn => {
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
            location.reload();
        }
    } catch (error) {
        console.error('Error updating fixed extension:', error);
        location.reload();
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

        const result = await response.json();

        if (result.warning) {
            alert(`[주의] ${result.warning}`);
        }

        location.reload();
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

        location.reload();
    } catch (error) {
        console.error('Error deleting custom extension:', error);
    }
}
