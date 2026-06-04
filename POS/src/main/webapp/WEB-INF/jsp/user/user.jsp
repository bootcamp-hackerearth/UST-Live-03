<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow-x: hidden;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.5;
            animation: float 8s ease-in-out infinite;
        }

        .blob1 {
            width: 300px;
            height: 300px;
            background: #3b82f6;
            top: -80px;
            left: -80px;
        }

        .blob2 {
            width: 250px;
            height: 250px;
            background: #60a5fa;
            bottom: -80px;
            right: -80px;
            animation-delay: 2s;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(25px); }
        }

        .update-card {
            width: 480px;
            padding: 35px 40px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px);
            box-shadow: 0 25px 50px rgba(0,0,0,0.15);
            animation: fadeIn 0.6s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        h3 {
            text-align: center;
            color: #3b82f6;
            font-weight: 700;
            margin-bottom: 25px;
        }

        label {
            font-weight: 600;
            font-size: 14px;
            margin-bottom: 4px;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            padding: 10px;
            transition: 0.2s;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .checkbox-list {
            display: flex;
            flex-direction: column;
            gap: 7px;
            margin-top: 6px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 12px;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            cursor: pointer;
            transition: border-color 0.2s, background 0.2s;
        }

        .checkbox-item:hover {
            border-color: #3b82f6;
            background: #eff6ff;
        }

        .checkbox-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            min-width: 16px;
            margin: 0;
            padding: 0;
            accent-color: #3b82f6;
            cursor: pointer;
        }

        .checkbox-item span {
            font-size: 13px;
            color: #374151;
            font-weight: 500;
        }

        .checkbox-item:has(input:checked) {
            border-color: #3b82f6;
            background: #eff6ff;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            border-radius: 10px;
            border: none;

            background: linear-gradient(135deg, #93c5fd, #3b82f6);
            color: white;
            font-weight: 600;

            box-shadow: 0 10px 25px rgba(37,99,235,0.25);
            transition: 0.2s ease;
        }

        .btn-update:hover {
            transform: translateY(-2px);
            box-shadow: 0 15px 30px rgba(37,99,235,0.35);
        }

        a {
            color: #3b82f6;
            text-decoration: none;
            font-weight: 500;
        }

        a:hover {
            text-decoration: underline;
        }

        small{
            color: red;
        }
    </style>
</head>

<body>
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="update-card">
    <h3>Update User</h3>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <form:input type="hidden" path="id"/>

        <div class="mb-3">
            <label for="name">Name</label>
            <form:input id="name" path="name" cssClass="form-control" required="true"/>
        </div>

        <div class="mb-3">
            <label for="email">Email</label>
            <form:input id="email" path="username" type="email"
                        cssClass="form-control"
                        placeholder="example@email.com"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label for="phoneNo">Phone Number</label>
            <form:input id="phoneNo" path="phoneNo" type="number"
                        cssClass="form-control"
                        placeholder="10-digit phone number"
                        required="true"/>
        </div>

        <div class="mb-3 roles-group">
            <label for="checkbox-list">Roles</label>
            <div id="checkbox-list" class="checkbox-list">
                <c:forEach var="role" items="${roles}">
                    <label for="${role.identifier}" class="checkbox-item">
                        <form:checkbox id="${role.identifier}" path="roles" value="${role.identifier}"/>
                        <span>${role.identifier}</span>
                    </label>
                </c:forEach>
            </div>
        </div>

        <button type="submit" class="btn-update">Update User</button>

    </form:form>

    <div class="text-center mt-3">
        <a href="/user/list">← Back to User List</a>
    </div>

</div>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const name = document.querySelector('input[name="name"]');
        const email = document.querySelector('input[name="username"]');
        const phone = document.querySelector('input[name="phoneNo"]');
        const password = document.querySelector('input[name="password"]');
        const roleCheckboxes =
            document.querySelectorAll('input[name="roles"]');

        function showError(element, message) {

            const small = document.createElement("small");
            small.className = "validation-error";
            small.innerText = message;

            if (
                element.tagName === "INPUT" ||
                element.tagName === "SELECT" ||
                element.tagName === "TEXTAREA"
            ) {
                element.parentNode.appendChild(small);
                element.focus();
            }
            else {
                element.appendChild(small);
            }
            e.preventDefault();

            return false;
        }

        const nameRegex = /^[A-Za-z\s]+$/;

        if (name.value.trim().length < 3) {
            return showError(
                name,
                "Name must be at least 3 characters"
            );
        }

        if (!nameRegex.test(name.value.trim())) {
            return showError(
                name,
                "Name should contain only letters"
            );
        }

        const emailRegex =
            /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (!emailRegex.test(email.value.trim())) {
            return showError(
                email,
                "Enter valid email address"
            );
        }

        const roleSelected =
            [...roleCheckboxes].some(cb => cb.checked);

        if (!roleSelected) {

            const roleGroup =
                document.querySelector(".roles-group");

            return showError(
                roleGroup,
                "Select at least one role"
            );
        }

        const phoneRegex = /^[6-9][0-9]{9}$/;

        if (!phoneRegex.test(phone.value.trim())) {
            return showError(
                phone,
                "Enter valid 10-digit Indian mobile number"
            );
        }

        if (password.value.length < 6) {
            return showError(
                password,
                "Password must be at least 6 characters"
            );
        }

    });
});
</script>
</body>
</html>
