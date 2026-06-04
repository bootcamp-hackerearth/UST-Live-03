<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Poppins', sans-serif;
            height: 100vh;
            display: flex;
        }

        .left-panel {
            flex: 1;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 40px;
        }

        .left-panel h1 {
            font-size: 30px;
            margin-bottom: 10px;
        }

        .left-panel p { opacity: 0.8; }

        .right-panel {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #f4f6fb;
        }

        .card {
            width: 420px;
            background: #fff;
            padding: 35px 30px;
            border-radius: 14px;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #333;
        }

        .form-group { margin-bottom: 16px; }

        .form-group label.field-label {
            display: block;
            font-size: 13px;
            color: #555;
            margin-bottom: 5px;
        }

        .input-box { position: relative; }

        .input-box i {
            position: absolute;
            top: 50%;
            left: 12px;
            transform: translateY(-50%);
            color: #888;
            font-size: 14px;
        }

        .input-box input {
            width: 100%;
            padding: 11px 11px 11px 36px;
            border-radius: 8px;
            border: 1.5px solid #ccc;
            font-size: 14px;
            font-family: 'Poppins', sans-serif;
            transition: border-color 0.2s;
        }

        .input-box input:focus {
            outline: none;
            border-color: #4b6cb7;
        }

        .input-box input.input-error { border-color: #e74c3c; }

        #roleList {
            border: 1.5px solid #ccc;
            border-radius: 8px;
            padding: 8px;
            background: #fafafa;
            max-height: 140px;
            overflow-y: auto;
            transition: border-color 0.2s;
        }

        #roleList.input-error { border-color: #e74c3c; }

        .role-row {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 6px 5px;
            border-radius: 6px;
            cursor: pointer;
        }

        .role-row:hover { background: #f0f2ff; }

        .role-row input[type="checkbox"] {
            width: 15px;
            height: 15px;
            accent-color: #4b6cb7;
            cursor: pointer;
        }

        .role-row label {
            margin: 0;
            cursor: pointer;
            font-size: 13px;
            color: #333;
        }

        #selectedRoles {
            margin-top: 8px;
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }

        .chip {
            background: #4b6cb7;
            color: white;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .chip:hover { background: #3953a4; }

        .error-msg {
            font-size: 12px;
            color: #e74c3c;
            margin-top: 4px;
            display: none;
        }

        .error-msg.show { display: block; }

        .error {
            font-size: 12px;
            color: #e74c3c;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background: #4b6cb7;
            border: none;
            color: white;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            font-family: 'Poppins', sans-serif;
            margin-top: 4px;
            transition: background 0.2s;
        }

        .btn:hover { background: #3953a4; }

        .login-link {
            text-align: center;
            margin-top: 14px;
            font-size: 13px;
            color: #555;
        }

        .login-link a {
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 500;
        }

        .login-link a:hover { text-decoration: underline; }
    </style>
</head>

<body>

<div class="left-panel">
    <h1>Welcome!</h1>
    <p>Create your account to get started</p>
</div>

<div class="right-panel">
    <div class="card">
        <h2>Register</h2>

        <form:form action="register" method="post" modelAttribute="userDto" onsubmit="return validateForm()">

            <div class="form-group">
                <div class="input-box">
                    <i class="fa fa-user"></i>
                    <form:input path="name" id="name" placeholder="Full Name"/>
                </div>
                <div class="error-msg" id="nameErr">Full name is required.</div>
                <form:errors path="name" cssClass="error"/>
            </div>

            <div class="form-group">
                <div class="input-box">
                    <i class="fa fa-envelope"></i>
                    <form:input path="username" id="email" type="email" placeholder="Email Address"/>
                </div>
                <div class="error-msg" id="emailErr">Please enter a valid email address.</div>
                <form:errors path="username" cssClass="error"/>
            </div>

            <div class="form-group">
                <label class="field-label">Roles</label>
                <div id="roleList">
                    <c:forEach items="${roles}" var="role">
                        <div class="role-row">
                            <input type="checkbox"
                                   id="${role.identifier}"
                                   name="roles"
                                   value="${role.identifier}"
                                   onchange="toggleRole(this)">
                            <label for="${role.identifier}">${role.identifier}</label>
                        </div>
                    </c:forEach>
                </div>
                <div id="selectedRoles"></div>
                <div class="error-msg" id="roleErr">Please select at least one role.</div>
                <form:errors path="roles" cssClass="error"/>
            </div>

            <div class="form-group">
                <div class="input-box">
                    <i class="fa fa-phone"></i>
                    <form:input path="phoneNo" id="phone" placeholder="Phone Number (10 digits)"/>
                </div>
                <div class="error-msg" id="phoneErr">Please enter a valid 10-digit phone number.</div>
                <form:errors path="phoneNo" cssClass="error"/>
            </div>

            <div class="form-group">
                <div class="input-box">
                    <i class="fa fa-lock"></i>
                    <form:password path="password" id="password" placeholder="Password (min 8 characters)"/>
                </div>
                <div class="error-msg" id="passErr">Password must be at least 8 characters.</div>
                <form:errors path="password" cssClass="error"/>
            </div>

            <input type="submit" value="Register" class="btn"/>

            <div class="login-link">
                Already have an account? <a href="login">Login</a>
            </div>

        </form:form>
    </div>
</div>

<script>
    function toggleRole(cb) {
        const selected = document.getElementById('selectedRoles');

        if (cb.checked) {
            cb.closest('.role-row').style.display = 'none';

            const chip = document.createElement('div');
            chip.className = 'chip';
            chip.dataset.value = cb.value;
            chip.innerHTML = cb.value + ' <i class="fa fa-times" style="font-size:10px;"></i>';

            chip.onclick = function () {
                cb.checked = false;
                cb.closest('.role-row').style.display = 'flex';
                chip.remove();
                clearError('roleList', 'roleErr');
            };

            selected.appendChild(chip);
        }

        clearError('roleList', 'roleErr');
    }

    function clearError(inputId, errId) {
        const input = document.getElementById(inputId);
        const err = document.getElementById(errId);
        if (input) input.classList.remove('input-error');
        if (err) err.classList.remove('show');
    }

    function showError(inputId, errId, msg) {
        const input = document.getElementById(inputId);
        const err = document.getElementById(errId);
        if (input) input.classList.add('input-error');
        if (err) {
            if (msg) err.textContent = msg;
            err.classList.add('show');
        }
    }

    ['name', 'email', 'phone', 'password'].forEach(function (id) {
        var el = document.getElementById(id);
        if (!el) return;

        el.addEventListener('input', function () {
            var errMap = { name: 'nameErr', email: 'emailErr', phone: 'phoneErr', password: 'passErr' };
            clearError(id, errMap[id]);
        });
    });

    function validateForm() {
        var valid = true;

        var name = document.getElementById('name');
        if (!name || name.value.trim() === '') {
            showError('name', 'nameErr', 'Full name is required.');
            valid = false;
        } else clearError('name', 'nameErr');

        var email = document.getElementById('email');
        var emailVal = email ? email.value.trim() : '';
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailVal) {
            showError('email', 'emailErr', 'Email is required.');
            valid = false;
        } else if (!emailRegex.test(emailVal)) {
            showError('email', 'emailErr', 'Please enter a valid email address.');
            valid = false;
        } else clearError('email', 'emailErr');

        var checked = document.querySelectorAll('input[name="roles"]:checked');
        if (checked.length === 0) {
            document.getElementById('roleList').classList.add('input-error');
            document.getElementById('roleErr').classList.add('show');
            valid = false;
        } else clearError('roleList', 'roleErr');

        var phone = document.getElementById('phone');
        var phoneVal = phone ? phone.value.trim() : '';
        var phoneRegex = /^\d{10}$/;

        if (!phoneVal) {
            showError('phone', 'phoneErr', 'Phone number is required.');
            valid = false;
        } else if (!phoneRegex.test(phoneVal)) {
            showError('phone', 'phoneErr', 'Please enter a valid 10-digit phone number.');
            valid = false;
        } else clearError('phone', 'phoneErr');

        var pass = document.getElementById('password');
        var passVal = pass ? pass.value : '';

        if (!passVal) {
            showError('password', 'passErr', 'Password is required.');
            valid = false;
        } else if (passVal.length < 8) {
            showError('password', 'passErr', 'Password must be at least 8 characters.');
            valid = false;
        } else clearError('password', 'passErr');

        if (!valid) {
            var firstErr = document.querySelector('.input-error, #roleList.input-error');
            if (firstErr) firstErr.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }

        return valid;
    }
</script>

</body>
</html>
