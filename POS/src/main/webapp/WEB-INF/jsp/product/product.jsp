<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Product</title>

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

input[readonly] {
    background: #f1f5f9;
    cursor: not-allowed;
}

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

    document.getElementById("categoryError").innerText = "";

    const categories = document.querySelectorAll('[name="category"]:checked');

    if (categories.length === 0) {
        document.getElementById("categoryError").innerText = "Select at least one category";
        valid = false;
    }

    return valid;
}
</script>

</head>

<body>

<div class="container">

    <h2>Edit Product</h2>

    <div class="form-container">

        <c:if test="${not empty message}">
            <div class="error-box">${message}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/product/update"
              method="post"
              onsubmit="return validateForm()">

            <input type="hidden" name="identifier" value="${product.identifier}" />

            <div class="form-group">
                <label>Product Name</label>
                <input type="text" value="${product.identifier}" readonly />
            </div>

            <div class="form-group">
                <label>Categories</label>

                <div class="checkbox-list">
                    <c:forEach var="cat" items="${categoryList}">

                        <c:set var="isSelected" value="false" />

                        <c:forEach var="pcat" items="${product.category}">
                            <c:if test="${pcat == cat.identifier}">
                                <c:set var="isSelected" value="true" />
                            </c:if>
                        </c:forEach>

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

                            <input type="checkbox"
                                   name="category"
                                   value="${cat.identifier}"
                                   <c:if test="${isSelected}">checked</c:if> />
                        </div>

                    </c:forEach>
                </div>

                <div class="field-error" id="categoryError"></div>
            </div>

            <div class="form-group">
                <label>Brand</label>
                <select name="brand" required>
                    <option value="">Select Brand</option>
                    <c:forEach var="b" items="${brandList}">
                        <option value="${b.identifier}"
                            <c:if test="${product.brand == b.identifier}">
                                selected
                            </c:if>>
                            ${b.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Model</label>
                <select name="model" required>
                    <option value="">Select Model</option>
                    <c:forEach var="m" items="${modelList}">
                        <option value="${m.identifier}"
                            <c:if test="${product.model == m.identifier}">
                                selected
                            </c:if>>
                            ${m.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Unit</label>
                <select name="unit" required>
                    <option value="">Select Unit</option>
                    <c:forEach var="u" items="${unitList}">
                        <option value="${u.identifier}"
                            <c:if test="${product.unit == u.identifier}">
                                selected
                            </c:if>>
                            ${u.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="btn-container">
                <button type="submit" class="btn save-btn">Update Product</button>

                <a href="${pageContext.request.contextPath}/product/list" class="btn cancel-btn">
                    Cancel
                </a>
            </div>

        </form>

    </div>

</div>

</body>
</html>