<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Product</title>

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
    max-width: 520px;
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

/* Checkbox list (same style as sample) */
.checkbox-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 6px;
    max-height: 140px;
    overflow-y: auto;
    background: #f9fafb;
}

.checkbox-item {
    display: grid;
    grid-template-columns: 1fr 30px;
    align-items: center;
    padding: 8px 6px;
    font-size: 14px;
    border-radius: 6px;
}

.checkbox-item:hover {
    background-color: #eef2ff;
}

.checkbox-item input[type="checkbox"] {
    justify-self: center;
}

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

    const name = document.querySelector('[name="identifier"]');
    const categories = document.querySelectorAll('[name="category"]:checked');

    if (!name.value.trim()) {
        showError(name, "Product name is required");
        valid = false;
    }

    if (categories.length === 0) {
        document.getElementById("categoryError").innerText = "Select at least one category";
        valid = false;
    }

    return valid;
}

function showError(input, message) {
    const errorDiv = input.nextElementSibling;
    errorDiv.innerText = message;
}
</script>

</head>

<body>

<div class="container">

    <h2>Add Product</h2>

    <div class="form-container">

        <c:if test="${not empty error}">
            <div class="error-box">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/product/add"
              method="post"
              onsubmit="return validateForm()">

            <!-- Product Name -->
            <div class="form-group">
                <label>Product Name</label>
                <input type="text" name="identifier" required />
                <div class="field-error"></div>
            </div>

            <!-- Categories (Checkbox Multi Select) -->
            <div class="form-group">
                <label>Categories</label>

                <div class="checkbox-list">
                    <c:forEach var="cat" items="${categoryList}">
                        <div class="checkbox-item">
                            <span>
                                <c:choose>
                                    <c:when test="${not empty cat.superCategory}">
                                        ${cat.superCategory} (${cat.identifier})
                                    </c:when>
                                    <c:otherwise>
                                        ${cat.identifier}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                            <input type="checkbox" name="category" value="${cat.identifier}" />
                        </div>
                    </c:forEach>
                </div>

                <div class="field-error" id="categoryError"></div>
            </div>

            <!-- Brand -->
            <div class="form-group">
                <label>Brand</label>
                <select name="brand" required>
                    <option value="">Select Brand</option>
                    <c:forEach var="b" items="${brandList}">
                        <option value="${b.identifier}">${b.identifier}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Model -->
            <div class="form-group">
                <label>Model</label>
                <select name="model" required>
                    <option value="">Select Model</option>
                    <c:forEach var="m" items="${modelList}">
                        <option value="${m.identifier}">${m.identifier}</option>
                    </c:forEach>
                </select>
            </div>

            <!-- Unit -->
            <div class="form-group">
                <label>Unit</label>
                <select name="unit" required>
                    <option value="">Select Unit</option>
                    <c:forEach var="u" items="${unitList}">
                        <option value="${u.identifier}">${u.identifier}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="btn-container">
                <button type="submit" class="btn save-btn">Save Product</button>

                <a href="${pageContext.request.contextPath}/product/list" class="btn cancel-btn">
                    Cancel
                </a>
            </div>

        </form>

    </div>

</div>

</body>
</html>