/**
 * validation.js — client-side validation helpers.
 *
 * INTERVIEW POINT:
 *   Client-side validation improves UX (instant feedback) but is never
 *   a substitute for server-side validation (see ValidationUtil.java).
 *   Both layers must always exist.
 */

document.addEventListener('DOMContentLoaded', function () {

    // ── Password strength indicator ──────────────────────────────
    const pwdField = document.getElementById('regPassword');
    if (pwdField) {
        pwdField.addEventListener('input', function () {
            const val      = this.value;
            const strength = getPasswordStrength(val);
            showPasswordStrength(this, strength);
        });
    }

    // ── Confirm password match ───────────────────────────────────
    const confirmField = document.getElementById('confirmPassword');
    if (confirmField && pwdField) {
        confirmField.addEventListener('input', function () {
            if (this.value !== pwdField.value) {
                this.setCustomValidity('Passwords do not match.');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    // ── Phone number — digits only ────────────────────────────────
    document.querySelectorAll('input[name="phone"]').forEach(function (input) {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').slice(0, 10);
        });
    });

    // ── Appointment date — no past dates ─────────────────────────
    const apptDate = document.getElementById('appointmentDate');
    if (apptDate) {
        const today = new Date().toISOString().split('T')[0];
        apptDate.setAttribute('min', today);
    }
});

// ── Password strength logic ──────────────────────────────────

function getPasswordStrength(password) {
    let score = 0;
    if (password.length >= 8)             score++;
    if (/[A-Z]/.test(password))           score++;
    if (/[a-z]/.test(password))           score++;
    if (/[0-9]/.test(password))           score++;
    if (/[@$!%*?&]/.test(password))       score++;
    return score; // 0–5
}

function showPasswordStrength(inputEl, score) {
    let existing = inputEl.parentElement.querySelector('.pwd-strength');
    if (!existing) {
        existing = document.createElement('div');
        existing.className = 'pwd-strength mt-1';
        inputEl.parentElement.appendChild(existing);
    }

    const labels  = ['', 'Very Weak', 'Weak', 'Fair', 'Strong', 'Very Strong'];
    const classes = ['', 'text-danger', 'text-warning', 'text-info', 'text-primary', 'text-success'];

    existing.innerHTML = score > 0
        ? '<small class="' + classes[score] + '">Strength: ' + labels[score] + '</small>'
        : '';
}
