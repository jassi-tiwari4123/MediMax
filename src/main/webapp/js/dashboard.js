/**
 * dashboard.js — shared JS for all dashboard pages.
 *
 * Features:
 *   - Sidebar toggle
 *   - Auto-dismiss alerts
 *   - Active nav link highlighting
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── Sidebar toggle ───────────────────────────────────────────
    const toggleBtn = document.getElementById('sidebarToggle');
    const sidebar   = document.getElementById('sidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('collapsed');
        });
    }

    // ── Auto-dismiss alerts after 4 seconds ──────────────────────
    document.querySelectorAll('.alert.alert-success, .alert.alert-info').forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            bsAlert.close();
        }, 4000);
    });

    // ── Mark active nav link based on current URL ─────────────────
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-link').forEach(function (link) {
        if (link.href && currentPath.includes(new URL(link.href).pathname)) {
            link.classList.add('active');
        }
    });
});
