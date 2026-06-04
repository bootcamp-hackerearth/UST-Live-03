<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.5;
            animation: float 8s ease-in-out infinite;
        }

        .blob1 {
            width: 280px;
            height: 280px;
            background: #3b82f6;
            top: -80px;
            left: -80px;
        }

        .blob2 {
            width: 240px;
            height: 240px;
            background: #60a5fa;
            bottom: -80px;
            right: -80px;
            animation-delay: 2s;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(25px); }
        }

        .card-glass {
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

        h4 {
            text-align: center;
            color: #3b82f6;
            font-weight: 700;
            margin-bottom: 25px;
        }

        label {
            font-weight: 600;
            font-size: 14px;
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

        .btn-primary-custom {
            background: linear-gradient(135deg, #93c5fd, #3b82f6);
            border: none;
            border-radius: 10px;
            color: white;
            font-weight: 600;
            padding: 10px 18px;

            box-shadow: 0 10px 25px rgba(37,99,235,0.25);
            transition: 0.2s ease;
        }

        .btn-primary-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 15px 30px rgba(37,99,235,0.35);
        }

        .btn-outline-custom {
            border-radius: 10px;
            font-weight: 500;
        }

        a {
            color: #3b82f6;
            text-decoration: none;
        }
    </style>
</head>

<body>
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="card-glass">

    <h4>Edit Node</h4>

    <c:if test="${empty nodeDto}">
        <div class="alert alert-danger text-center">
            Node not found
        </div>
    </c:if>

    <form:form action="/node/update"
               method="post"
               modelAttribute="nodeDto">

        <form:hidden path="id"/>

        <div class="mb-3">
            <label for="identifier">Node Name</label>
            <form:input id="identifier" path="identifier"
                        cssClass="form-control"
                        readonly="true"/>
        </div>

        <div class="mb-3">
            <label for="path">Node Path</label>
            <form:input id="path" path="path"
                        cssClass="form-control"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label for="checkbox-list">Node Roles</label>
            <div class="checkbox-list">
                <c:forEach var="role" items="${roles}">
                    <label for="${role.identifier}" class="checkbox-item">
                        <form:checkbox id="${role.identifier}" path="roles" value="${role.identifier}"/>
                        <span>${role.identifier}</span>
                    </label>
                </c:forEach>
            </div>
        </div>

        <div class="d-flex justify-content-between mt-3">
            <a href="/node/list" class="btn btn-outline-secondary btn-outline-custom">
                Cancel
            </a>
            <button type="submit" class="btn-primary-custom">
                Update
            </button>
        </div>

    </form:form>

</div>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const identifier =
            document.querySelector('input[name="identifier"]');

        const path =
            document.querySelector('input[name="path"]');

        const roleCheckboxes =
            document.querySelectorAll('input[name="roles"]');

        function showError(element, message) {

            const small = document.createElement("small");

            small.className = "validation-error";
            small.style.color = "red";
            small.style.fontSize = "13px";
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

        if (identifier.value.trim() === "") {
            return showError(
                identifier,
                "Node name is required"
            );
        }

        if (identifier.value.trim().length < 2) {
            return showError(
                identifier,
                "Node name must be at least 2 characters"
            );
        }

        const identifierRegex = /^[A-Za-z0-9\s_-]+$/;

        if (!identifierRegex.test(identifier.value.trim())) {
            return showError(
                identifier,
                "Only letters, numbers, spaces, _ and - are allowed"
            );
        }

        if (path.value.trim() === "") {
            return showError(
                path,
                "Node path is required"
            );
        }

        if (path.value.trim().length < 2) {
            return showError(
                path,
                "Node path must be at least 2 characters"
            );
        }

        const pathRegex = /^\/[A-Za-z0-9/_-]*$/;

        if (!pathRegex.test(path.value.trim())) {
            return showError(
                path,
                "Path must start with / and contain valid characters"
            );
        }

        const roleSelected =
            [...roleCheckboxes].some(cb => cb.checked);

        if (!roleSelected) {

            const roleGroup =
                document.querySelector(".checkbox-list");

            return showError(
                roleGroup,
                "Select at least one role"
            );
        }

    });
});
</script>
</body>
</html>
