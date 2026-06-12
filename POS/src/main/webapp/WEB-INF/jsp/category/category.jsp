<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Category</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    padding: 40px 20px;
}

.container {
    width: 95%;
    max-width: 500px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 25px;
    font-weight: 600;
    color: #0f172a;
}

.form-container {
    background: #ffffff;
    border-radius: 12px;
    padding: 25px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 6px;
    font-size: 14px;
    font-weight: 500;
}

input, select {
    width: 100%;
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
}

input:focus, select:focus {
    outline: none;
    border-color: #6366f1;
}

/* Readonly */
input[readonly] {
    background: #f1f5f9;
    cursor: not-allowed;
}

/* Error styles */
.error-box {
    background: #fee2e2;
    color: #7f1d1d;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    font-size: 13px;
}

.field-error {
    color: #dc2626;
    font-size: 12px;
    margin-top: 4px;
}

/* Buttons */
.btn-container {
    text-align: center;
    margin-top: 20px;
}

.btn {
    display: inline-block;
    padding: 10px 18px;
    margin: 6px;
    border-radius: 6px;
    border: none;
    font-size: 14px;
    cursor: pointer;
    text-decoration: none;
    color: white;
}

.save-btn {
    background: #6366f1;
}

.save-btn:hover {
    background: #4f46e5;
}

.cancel-btn {
    background: #0ea5e9;
}

.cancel-btn:hover {
    background: #0284c7;
}

</style>

<script>
function validateForm() {

    let valid = true;
    document.querySelectorAll(".field-error").forEach(e => e.innerText = "");

    const identifier = document.querySelector('[name="identifier"]');
    const superCategory = document.querySelector('[name="superCategory"]');

    if (!identifier.value.trim()) {
        showError(identifier, "Category name missing");
        valid = false;
    }

    // Prevent self-parent
    if (superCategory.value &&
        identifier.value.trim().toLowerCase() === superCategory.value.toLowerCase()) {
        showError(superCategory, "Category cannot be its own parent");
        valid = false;
    }

    return valid;
}

function showError(input, message) {
    const errorDiv = input.nextElementSibling;
    if (errorDiv) errorDiv.innerText = message;
}
</script>

</head>

<body>

<div class="container">

    <h2>Edit Category</h2>

    <div class="form-container">

        <!-- Backend Message -->
        <c:if test="${not empty message}">
            <div class="error-box">${message}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/category/update"
              method="post"
              onsubmit="return validateForm()">

            <!-- Hidden Identifier -->
            <input type="hidden" name="identifier" value="${category.identifier}" />

            <!-- Category Name (READ ONLY) -->
            <div class="form-group">
                <label>Category Name</label>
                <input type="text" value="${category.identifier}" readonly />
            </div>

            <!-- Super Category Dropdown -->
            <div class="form-group">
                <label>Super Category</label>

                <select name="superCategory">
                    <option value="">-- None (Top Level Category) --</option>

                    <c:forEach var="cat" items="${categoryList}">
                        <option value="${cat.identifier}"
                            <c:if test="${cat.identifier == category.superCategory}">
                                selected
                            </c:if>>
                            ${cat.identifier}
                        </option>
                    </c:forEach>
                </select>

                <div class="field-error"></div>
            </div>

            <div class="btn-container">
                <button type="submit" class="btn save-btn">Update Category</button>

                <a href="${pageContext.request.contextPath}/category/list" class="btn cancel-btn">
                    Cancel
                </a>
            </div>

        </form>

    </div>

</div>

</body>
</html>