<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;

            --border: #e2e8f0;

            --danger: #dc2626;
            --danger-bg: #fee2e2;
            --danger-border: #fecaca;

            --success: #16a34a;
            --success-bg: #dcfce7;
            --success-border: #86efac;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            padding: 40px 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: var(--text);
        }

        /* BACK BUTTON */
        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;

            width: 42px;
            height: 42px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);

            border: 1px solid var(--border);
            color: var(--text);

            text-decoration: none;
            font-size: 18px;

            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
        }

        .container-box {
            width: 100%;
            max-width: 820px;
        }

        /* CARD */
        .card {
            background: var(--glass);
            backdrop-filter: blur(16px);

            border-radius: var(--radius);
            border: 1px solid var(--border);

            box-shadow: var(--shadow);
            padding: 30px;
        }

        h3 {
            text-align: center;
            font-weight: 600;
            margin-bottom: 22px;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-bottom: 4px;
        }

        .form-control {
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;

            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            font-weight: 600;

            transition: all 0.2s ease;
        }

        .btn-update:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.3);
        }

        .server-msg {
            text-align: center;
            padding: 10px 14px;
            border-radius: 10px;
            font-size: 13px;
            margin-bottom: 14px;
        }

        .server-msg.success {
            background: var(--success-bg);
            color: var(--success);
            border: 1px solid var(--success-border);
        }

        .server-msg.error {
            background: var(--danger-bg);
            color: var(--danger);
            border: 1px solid var(--danger-border);
        }

        .invalid-feedback {
            font-size: 12px;
            color: var(--danger);
            margin-top: 4px;
        }

        .error-border {
            border: 1px solid var(--danger) !important;
        }

        .toast-message {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background: #0f172a;
            color: white;
            padding: 12px 16px;
            border-radius: 10px;
            box-shadow: var(--shadow);
            opacity: 0;
            transform: translateY(20px);
            transition: 0.3s;
            font-size: 13px;
            z-index: 999;
        }

        .toast-message.show {
            opacity: 1;
            transform: translateY(0);
        }
    </style>

    <!-- ORIGINAL JS UNCHANGED -->
    <script>
        function validateForm() {

            document.getElementById("emailErr").innerText = "";
            document.getElementById("phoneErr").innerText = "";

            let emailInput = document.getElementsByName("username")[0];
            let phoneInput = document.getElementsByName("phoneNo")[0];

            emailInput.classList.remove("error-border");
            phoneInput.classList.remove("error-border");

            let name = document.getElementsByName("name")[0].value.trim();
            let email = emailInput.value.trim();
            let phone = phoneInput.value.trim();

            if (!name || !email || !phone) {
                showToast("All fields are required");
                return false;
            }

            let valid = true;

            let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;
            if (!emailPattern.test(email)) {
                document.getElementById("emailErr").innerText = "Enter a valid email address";
                emailInput.classList.add("error-border");
                valid = false;
            }

            let phonePattern = /^[0-9]{10}$/;
            if (!phonePattern.test(phone)) {
                document.getElementById("phoneErr").innerText = "Phone must be exactly 10 digits";
                phoneInput.classList.add("error-border");
                valid = false;
            }

            return valid;
        }

        function showToast(msg) {
            const toast = document.getElementById("toast");
            toast.innerText = msg;
            toast.classList.add("show");

            setTimeout(() => {
                toast.classList.remove("show");
            }, 2500);
        }

        document.addEventListener("DOMContentLoaded", function () {
            const phoneInput = document.getElementsByName("phoneNo")[0];

            phoneInput.addEventListener("input", function () {
                this.value = this.value.replace(/[^0-9]/g, '').slice(0, 10);
            });

            phoneInput.addEventListener("paste", function (e) {
                let paste = (e.clipboardData || window.clipboardData).getData("text");
                if (!/^\d+$/.test(paste)) {
                    e.preventDefault();
                    showToast("Only digits allowed in phone number");
                }
            });
        });
    </script>
</head>

<body>

<a href="${pageContext.request.contextPath}/user/list" class="back-arrow">←</a>

<div class="container-box">

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType == 'success' ? 'success' : 'error'}">
            ${message}
        </div>
    </c:if>

    <div class="card">

        <h3>Update User</h3>

        <form:form action="${pageContext.request.contextPath}/user/update"
                   method="post"
                   modelAttribute="user"
                   onsubmit="return validateForm()">

            <form:hidden path="id"/>

            <div class="row">

                <div class="col-md-6">
                    <div class="mb-3">
                        <label>Name</label>
                        <form:input path="name" cssClass="form-control"/>
                    </div>

                    <div class="mb-3">
                        <label>Email</label>
                        <form:input path="username" cssClass="form-control"/>
                        <div id="emailErr" class="invalid-feedback"></div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="mb-3">
                        <label>Phone Number</label>
                        <form:input path="phoneNo"
                                    cssClass="form-control"
                                    maxlength="10"
                                    oninput="this.value = this.value.replace(/[^0-9]/g, '').slice(0,10)" />
                        <div id="phoneErr" class="invalid-feedback"></div>
                    </div>
                </div>

            </div>

            <div class="mb-3">
                <label>Roles</label>
                <form:select path="roles" multiple="true" cssClass="form-control">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
            </div>

            <button type="submit" class="btn-update">Update User</button>

        </form:form>

    </div>
</div>

<div id="toast" class="toast-message"></div>

</body>
</html>