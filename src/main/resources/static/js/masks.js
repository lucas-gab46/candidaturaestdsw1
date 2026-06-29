/**
 * Input Masks and Validations
 * Client-side formatting for CPF, CNPJ, Phone, and other fields
 */

// Apply mask to phone number (10 or 11 digits)
function maskPhone(input) {
    let value = input.value.replace(/\D/g, '');
    
    if (value.length <= 10) {
        value = value.replace(/(\d{0})(\d{2})(\d{0,4})(\d{0,4})/, function(match, p1, p2, p3, p4) {
            if (p2) return '(' + p2 + ') ' + p3 + (p4 ? '-' + p4 : '');
            return '';
        });
    } else {
        value = value.replace(/(\d{0})(\d{2})(\d{0,5})(\d{0,4})/, function(match, p1, p2, p3, p4) {
            if (p2) return '(' + p2 + ') ' + p3 + (p4 ? '-' + p4 : '');
            return '';
        });
    }
    
    input.value = value;
}

// Apply mask to CPF (###.###.###-##)
function maskCPF(input) {
    let value = input.value.replace(/\D/g, '');
    
    if (value.length > 11) {
        value = value.substring(0, 11);
    }
    
    value = value.replace(/(\d{0})(\d{3})(\d{0,3})(\d{0,3})(\d{0,2})/, function(match, p1, p2, p3, p4, p5) {
        if (p2) return p2 + '.' + p3 + (p4 ? '.' + p4 + (p5 ? '-' + p5 : '') : '');
        return '';
    });
    
    input.value = value;
}

// Apply mask to CNPJ (##.###.###/####-##)
function maskCNPJ(input) {
    let value = input.value.replace(/\D/g, '');
    
    if (value.length > 14) {
        value = value.substring(0, 14);
    }
    
    value = value.replace(/(\d{0})(\d{2})(\d{0,3})(\d{0,3})(\d{0,4})(\d{0,2})/, function(match, p1, p2, p3, p4, p5, p6) {
        if (p2) return p2 + '.' + p3 + (p4 ? '.' + p4 + '/' + p5 + (p6 ? '-' + p6 : '') : '');
        return '';
    });
    
    input.value = value;
}

// Validate CPF (check digits)
function validateCPF(cpf) {
    let value = cpf.replace(/\D/g, '');
    
    // Check if length is 11
    if (value.length !== 11) {
        return false;
    }
    
    // Check if all digits are the same
    if (/^(\d)\1{10}$/.test(value)) {
        return false;
    }
    
    // Validate first check digit
    let sum = 0;
    for (let i = 0; i < 9; i++) {
        sum += parseInt(value.charAt(i)) * (10 - i);
    }
    let remainder = sum % 11;
    let digit1 = remainder < 2 ? 0 : 11 - remainder;
    
    if (parseInt(value.charAt(9)) !== digit1) {
        return false;
    }
    
    // Validate second check digit
    sum = 0;
    for (let i = 0; i < 10; i++) {
        sum += parseInt(value.charAt(i)) * (11 - i);
    }
    remainder = sum % 11;
    let digit2 = remainder < 2 ? 0 : 11 - remainder;
    
    return parseInt(value.charAt(10)) === digit2;
}

// Validate CNPJ (check digits)
function validateCNPJ(cnpj) {
    let value = cnpj.replace(/\D/g, '');
    
    // Check if length is 14
    if (value.length !== 14) {
        return false;
    }
    
    // Check if all digits are the same
    if (/^(\d)\1{13}$/.test(value)) {
        return false;
    }
    
    // Validate first check digit
    let weights1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    let sum = 0;
    for (let i = 0; i < 12; i++) {
        sum += parseInt(value.charAt(i)) * weights1[i];
    }
    let remainder = sum % 11;
    let digit1 = remainder < 2 ? 0 : 11 - remainder;
    
    if (parseInt(value.charAt(12)) !== digit1) {
        return false;
    }
    
    // Validate second check digit
    let weights2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    sum = 0;
    for (let i = 0; i < 13; i++) {
        sum += parseInt(value.charAt(i)) * weights2[i];
    }
    remainder = sum % 11;
    let digit2 = remainder < 2 ? 0 : 11 - remainder;
    
    return parseInt(value.charAt(13)) === digit2;
}

// Validate phone number (10 or 11 digits)
function validatePhone(phone) {
    let value = phone.replace(/\D/g, '');
    return value.length === 10 || value.length === 11;
}

// Validate email format
function validateEmail(email) {
    let regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Validate password strength (minimum 6 characters)
function validatePassword(password) {
    return password && password.length >= 6;
}

// Set up input masks on page load
document.addEventListener('DOMContentLoaded', function() {
    // Apply masks to inputs with specific IDs or classes
    const phoneInputs = document.querySelectorAll('input[name="telefone"], input.phone-mask');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function() {
            maskPhone(this);
        });
    });
    
    const cpfInputs = document.querySelectorAll('input[name="cpf"], input.cpf-mask');
    cpfInputs.forEach(input => {
        input.addEventListener('input', function() {
            maskCPF(this);
        });
    });
    
    const cnpjInputs = document.querySelectorAll('input[name="cnpj"], input.cnpj-mask');
    cnpjInputs.forEach(input => {
        input.addEventListener('input', function() {
            maskCNPJ(this);
        });
    });
    
    // Form submission validation
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            let isValid = true;
            
            // Validate CPF if present
            const cpfInput = form.querySelector('input[name="cpf"]');
            if (cpfInput && cpfInput.value.trim()) {
                if (!validateCPF(cpfInput.value)) {
                    cpfInput.classList.add('is-invalid');
                    isValid = false;
                } else {
                    cpfInput.classList.remove('is-invalid');
                }
            }
            
            // Validate CNPJ if present
            const cnpjInput = form.querySelector('input[name="cnpj"]');
            if (cnpjInput && cnpjInput.value.trim()) {
                if (!validateCNPJ(cnpjInput.value)) {
                    cnpjInput.classList.add('is-invalid');
                    isValid = false;
                } else {
                    cnpjInput.classList.remove('is-invalid');
                }
            }
            
            // Validate phone if present
            const phoneInput = form.querySelector('input[name="telefone"]');
            if (phoneInput && phoneInput.value.trim()) {
                if (!validatePhone(phoneInput.value)) {
                    phoneInput.classList.add('is-invalid');
                    isValid = false;
                } else {
                    phoneInput.classList.remove('is-invalid');
                }
            }
            
            // Validate email if present
            const emailInput = form.querySelector('input[type="email"]');
            if (emailInput && emailInput.value.trim()) {
                if (!validateEmail(emailInput.value)) {
                    emailInput.classList.add('is-invalid');
                    isValid = false;
                } else {
                    emailInput.classList.remove('is-invalid');
                }
            }
            
            // Validate password if present
            const passwordInput = form.querySelector('input[name="senha"]');
            if (passwordInput && passwordInput.value.trim()) {
                if (!validatePassword(passwordInput.value)) {
                    passwordInput.classList.add('is-invalid');
                    isValid = false;
                } else {
                    passwordInput.classList.remove('is-invalid');
                }
            }
            
            if (!isValid) {
                e.preventDefault();
            }
        });
    });
});
