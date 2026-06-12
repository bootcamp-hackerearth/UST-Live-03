<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        @media (max-width: 768px) {

            body {
                overflow-y: auto;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-card {
                width: 100%;
                padding: 25px;
            }
        }

        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: #f7f7fb;
            min-height: 100vh;
            overflow-x: hidden;
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
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
            position: relative;
            z-index: 2;
        }

        .form-card {
            width: 95%;
            max-width: 1050px;

            padding: 30px;

            border-radius: 20px;

            background: rgba(255, 255, 255, 0.75);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            border: 1px solid rgba(255, 255, 255, 0.4);

            box-shadow:
                0 20px 50px rgba(0,0,0,0.15),
                inset 0 1px 0 rgba(255,255,255,0.6);

            animation: fadeInUp 0.6s ease;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 18px 25px;
        }

        .full-width {
            grid-column: 1 / -1;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            transition: 0.25s ease;
            min-height: 45px;
        }

        textarea.form-control {
            resize: none;
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

        .form-card h4 {
            text-align: center;
            margin-bottom: 25px;
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

        .btn-primary-custom {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;

            background: #3b82f6;
            color: white;
            font-weight: 600;

            box-shadow: 0 10px 25px rgba(37,99,235,0.25);

            transition: all 0.2s ease;
        }

        .btn-primary-custom:hover {
            background: #2563eb;
            transform: translateY(-2px);
            box-shadow: 0 14px 30px rgba(37,99,235,0.35);
        }

        .back-link {
            text-align: center;
            margin-top: 15px;
        }

        .back-link a {
            color: #3b82f6;
            text-decoration: none;
            font-weight: 500;
        }

        .back-link a:hover {
            color: #2563eb;
            text-decoration: underline;
        }

        .alert {
            border-radius: 10px;
        }

        .custom-toast {
            position: fixed;
            bottom: 30px;
            left: 50%;
            transform: translateX(-50%) translateY(20px);

            min-width: 260px;
            max-width: 80%;

            padding: 14px 18px;
            border-radius: 14px;

            text-align: center;

            font-size: 14px;
            font-weight: 500;
            color: rgba(31, 59, 59, 0.9);

            background: rgba(255, 255, 255, 0.18);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);

            background-image: linear-gradient(
                135deg,
                rgba(255, 255, 255, 0.25),
                rgba(255, 255, 255, 0.08)
            );

            border: 1px solid rgba(255, 255, 255, 0.35);
            box-shadow:
                0 12px 30px rgba(0, 0, 0, 0.12),
                inset 0 1px 0 rgba(255, 255, 255, 0.4);

            z-index: 9999;
            opacity: 0;
            animation: toastIn 0.4s ease forwards;
        }

        .custom-toast::before {
            content: "";
            position: absolute;
            inset: -2px;
            border-radius: inherit;
            background: radial-gradient(
                circle at center,
                rgba(74, 166, 163, 0.25),
                transparent 70%
            );
            z-index: -1;
            filter: blur(12px);
        }

        @keyframes toastIn {
            from {
                opacity: 0;
                transform: translateX(-50%) translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateX(-50%) translateY(0);
            }
        }

        .custom-toast.hide {
            opacity: 0;
            transform: translateX(-50%) translateY(20px);
            transition: all 0.4s ease;
        }
    </style>
</head>
<body>

<!-- BACKGROUND BLOBS -->
<div class="blob blob1"></div>
<div class="blob blob2"></div>

<div class="main-container">

    <div class="form-card">

        <h4>Add New Product</h4>
            <c:if test="${not empty message}">
                <div id="customToast" class="custom-toast">
                    ${message}
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>

        <form:form method="post"
                   action="/product/add"
                   modelAttribute="productDto">

            <div class="form-grid">

            <!-- IDENTIFIER -->
            <div class="mb-3">
                <label for="identifier" class="form-label fw-semibold">
                    Product Identifier
                </label>

               <form:input id="identifier"
                           path="identifier"
                           cssClass="form-control"
                           placeholder="Enter product identifier"
                           required="true"
                           pattern="[A-Za-z0-9_-]{3,30}"
                           title="3–30 characters. Letters, numbers, dash (-) or underscore (_) only." />
            </div>

            <div class="mb-3">
                <label for="name" class="form-label fw-semibold">
                    Product Name
                </label>

                <form:input id="name"
                            path="name"
                            cssClass="form-control"
                            placeholder="Enter product name"
                            required="true"
                            pattern="[A-Za-z ]{3,100}"
                            title="3–100 characters. Letters and spaces only." />
            </div>

            <div class="mb-3">
                <label for="brandName" class="form-label fw-semibold">
                    Select Brand
                </label>

                <form:select path="brandName"
                             id="brandName"
                             cssClass="form-control"
                             required="true">

                    <form:option value="">
                        -- Select Brand --
                    </form:option>

                    <c:forEach var="brand" items="${brands}">
                        <form:option value="${brand.identifier}">
                            ${brand.name}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label for="model" class="form-label fw-semibold">
                    Select Model
                </label>

                <form:select path="model"
                             id="model"
                             cssClass="form-control"
                             required="true">

                    <form:option value="">
                        -- Select Model --
                    </form:option>

                    <c:forEach var="model" items="${models}">
                        <form:option value="${model.identifier}">
                            ${model.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>

            </div>

            <div class="mb-3">
                <label for="category" class="form-label fw-semibold">
                    Select Category
                </label>

                <form:select path="category"
                             id="category"
                             cssClass="form-control"
                             multiple="true"
                             size="4"
                             required="true">

                    <c:forEach var="category" items="${categories}">
                        <form:option value="${category.name}">
                            ${category.name}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3">
                <label for="unit" class="form-label fw-semibold">
                    Select Unit
                </label>

                <form:select path="unit"
                             id="unit"
                             cssClass="form-control"
                             required="true">

                    <form:option value="">
                        -- Select Unit --
                    </form:option>

                    <c:forEach var="unit" items="${units}">
                        <form:option value="${unit.identifier}">
                            ${unit.identifier}
                        </form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div class="mb-3 full-width">
                <label for="description" class="form-label fw-semibold">
                    Description
                </label>

                <form:textarea id="description"
                               path="description"
                               cssClass="form-control"
                               placeholder="Enter description"
                               rows="4"
                               required="true"
                               minlength="10" maxlength="500"
                               title="Description must be between 10 and 500 characters."/>
            </div>

            </div>

            <div class="full-width">
                <button type="submit" class="btn-primary-custom">
                    Add Product
                </button>
            </div>

        </form:form>

        <div class="back-link">
            <a href="/product/list">← Back to List</a>
        </div>

    </div>

</div>
<script>
document.addEventListener("mousemove", (e) => {
    const x = (window.innerWidth / 2 - e.clientX) / 30;
    const y = (window.innerHeight / 2 - e.clientY) / 30;

    document.querySelector(".blob1").style.transform =
        `translate(${x}px, ${y}px)`;

    document.querySelector(".blob2").style.transform =
        `translate(${x * -1}px, ${y * -1}px)`;
});
</script>
<script>
window.addEventListener("DOMContentLoaded", function () {
    const toast = document.getElementById("customToast");

    if (toast) {
        setTimeout(() => {
            toast.classList.add("hide");
            setTimeout(() => toast.remove(), 400);
        }, 3500);
    }
});
</script>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    if (!form) return;

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const identifier =
            document.querySelector('input[name="identifier"]');

        const name =
            document.querySelector('input[name="name"]');

        const brandName =
            document.querySelector('select[name="brandName"]');

        const model =
            document.querySelector('select[name="model"]');

        const category =
            document.querySelector('select[name="category"]');

        const unit =
            document.querySelector('select[name="unit"]');

        const description =
            document.querySelector('textarea[name="description"]');

        function showError(element, message) {

            const small = document.createElement("small");

            small.className = "validation-error";
            small.style.color = "red";
            small.style.fontSize = "13px";
            small.style.display = "block";
            small.style.marginTop = "5px";

            small.innerText = message;

            element.parentNode.appendChild(small);

            element.focus();

            e.preventDefault();

            return false;
        }

        const identifierRegex = /^[A-Za-z0-9_-]{3,30}$/;

        if (!identifierRegex.test(identifier.value.trim())) {
            return showError(
                identifier,
                "3–30 characters. Letters, numbers, - or _ only"
            );
        }

        const nameRegex = /^[A-Za-z ]{3,100}$/;

        if (!nameRegex.test(name.value.trim())) {
            return showError(
                name,
                "3–100 characters. Letters and spaces only"
            );
        }

        if (brandName.value.trim() === "") {
            return showError(
                brandName,
                "Please select a brand"
            );
        }

        if (model.value.trim() === "") {
            return showError(
                model,
                "Please select a model"
            );
        }

        const selectedCategories =
            [...category.options].filter(option => option.selected);

        if (selectedCategories.length === 0) {
            return showError(
                category,
                "Please select at least one category"
            );
        }

        if (unit.value.trim() === "") {
            return showError(
                unit,
                "Please select a unit"
            );
        }

        if (description.value.trim().length < 10) {
            return showError(
                description,
                "Description must be at least 10 characters"
            );
        }

        if (description.value.trim().length > 500) {
            return showError(
                description,
                "Description cannot exceed 500 characters"
            );
        }

        const descriptionRegex = /^[A-Za-z0-9\s,.]+$/;

        if (!descriptionRegex.test(description.value.trim())) {
            return showError(
                description,
                "Description should not contain special characters"
            );
        }
    });

});
</script>
</body>
</html>