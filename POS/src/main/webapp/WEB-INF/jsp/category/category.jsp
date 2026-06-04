<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Category</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            height: 100vh;
            overflow: hidden;
            position: relative;
        }

        .blob {
            position: absolute;
            border-radius: 50%;
            filter: blur(60px);
            opacity: 0.5;
            animation: float 10s infinite ease-in-out;
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
            background: #93c5fd;
            bottom: -80px;
            right: -80px;
            animation-delay: 3s;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0px) scale(1);
            }
            50% {
                transform: translateY(-30px) scale(1.05);
            }
        }

        .main-container {
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            z-index: 2;
        }

        .card {
            width: 420px;
            padding: 25px;
            border-radius: 18px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            border: 1px solid rgba(255,255,255,0.4);

            box-shadow:
                0 20px 50px rgba(0,0,0,0.15),
                inset 0 1px 0 rgba(255,255,255,0.6);

            animation: fadeInUp 0.5s ease;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(40px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        h4 {
            color: #3b82f6;
            font-weight: 700;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            transition: 0.25s ease;
        }

        .form-control:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
        }

        .btn-success {
            background: #3b82f6;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            box-shadow: 0 8px 20px rgba(37,99,235,0.25);
            transition: 0.2s;
        }

        .btn-success:hover {
            background: #2563eb;
            transform: translateY(-2px);
        }

        .btn-outline-secondary {
            border-radius: 10px;
        }

        .alert {
            border-radius: 10px;
        }
    </style>
</head>

<body>
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="main-container">

    ${message}

    <div class="card">

        <h4 class="text-center mb-4">Edit Category</h4>

        <c:if test="${empty categoryDto}">
            <div class="alert alert-danger text-center">
                Category not found
            </div>
        </c:if>

        <c:if test="${not empty categoryDto}">
            <form:form action="/category/update"
                       method="post"
                       modelAttribute="categoryDto">

                <form:hidden path="id" value="${categoryDto.id}"/>

                <div class="mb-3">
                    <label for="identifier" class="form-label fw-semibold">Category Identifier</label>
                    <form:input id="identifier"
                                path="identifier"
                                cssClass="form-control"
                                placeholder="Enter identifier" />
                </div>

                <div class="mb-3">
                    <label for="name" class="form-label fw-semibold">Category Name</label>
                    <form:input id="name"
                                path="name"
                                cssClass="form-control"
                                placeholder="Enter category name" />
                </div>

                <div class="warehouse-group">
                        <span class="warehouse-label">SELECT SUPER CATEGORY</span>

                        <form:select path="superCategory" cssClass="form-control">

                            <form:option value="" label="ROOT"/>

                            <c:forEach var="category" items="${categories}">
                                <form:option value="${category.name}">
                                    ${category.name}
                                </form:option>
                            </c:forEach>

                        </form:select>
                </div>

                <div class="mb-3">
                    <label for="description" class="form-label fw-semibold">Description</label>
                    <form:textarea id="description"
                                   path="description"
                                   cssClass="form-control"
                                   placeholder="Enter description"
                                   required="true"/>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/category/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-success">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>

    </div>

</div>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const identifier =
            document.querySelector('input[name="identifier"]');

        const name =
            document.querySelector('input[name="name"]');

        const superCategory =
            document.querySelector('select[name="superCategory"]');

        const description =
            document.querySelector('textarea[name="description"]');

        function showError(element, message) {

            const small = document.createElement("small");
            small.className = "validation-error";
            small.style.color = "red";
            small.style.fontSize = "13px";
            small.innerText = message;

            element.parentNode.appendChild(small);

            element.focus();

            e.preventDefault();
            return false;
        }

        const idRegex = /^[A-Za-z0-9_-]+$/;

        if (identifier.value.trim().length < 2) {
            return showError(identifier, "Minimum 2 characters required");
        }

        if (!idRegex.test(identifier.value.trim())) {
            return showError(identifier, "Only letters, numbers, _ and - allowed");
        }

        const nameRegex = /^[A-Za-z\s]+$/;

        if (name.value.trim().length < 2) {
            return showError(name, "Minimum 2 characters required");
        }

        if (!nameRegex.test(name.value.trim())) {
            return showError(name, "Only letters and spaces allowed");
        }

        const descRegex = /^[A-Za-z0-9\s,]+$/;

        if (description.value.trim().length < 5) {
            return showError(description, "Minimum 5 characters required");
        }

        if (!descRegex.test(description.value.trim())) {
            return showError(description, "No special characters allowed");
        }

    });
});
</script>
</body>
</html>