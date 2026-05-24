// Polls /api/alerts every 5 minutes and shows a toast when low-stock items are detected.
// The manager dashboard already receives alerts server-side on page load;
// this script handles live updates without a full page refresh.

(function () {
    const POLL_INTERVAL = 5 * 60 * 1000;
    const alertContainer = document.getElementById('stock-alert-container');
    if (!alertContainer) return;

    function showToast(message, type) {
        const toast = document.createElement('div');
        toast.className = `alert alert-${type} alert-dismissible fade show shadow-sm`;
        toast.setAttribute('role', 'alert');
        toast.innerHTML = `<i class="fas fa-bell me-2"></i>${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
        alertContainer.appendChild(toast);
        setTimeout(() => {
            if (toast.parentNode) toast.parentNode.removeChild(toast);
        }, 8000);
    }

    function checkAlerts() {
        const badge = document.getElementById('alert-count-badge');
        if (!badge) return;
        const count = parseInt(badge.textContent || '0', 10);
        if (count > 0) {
            showToast(`${count} producto(s) con stock bajo. Revisa el inventario.`, 'warning');
        }
    }

    setInterval(checkAlerts, POLL_INTERVAL);
})();
