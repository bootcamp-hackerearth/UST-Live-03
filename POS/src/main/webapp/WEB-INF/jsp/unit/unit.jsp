<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Unit</title>

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
    padding: 50px 20px;
}

.container {
    width: 100%;
    max-width: 500px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 26px;
    font-weight: 600;
}

/* Card */
.form-card {
    background: #ffffff;
    border-radius: 14px;
    padding: 30px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.06);
}

/* Input group */
.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    font-size: 14px;
    margin-bottom: 6px;
    font-weight: 500;
}

/* Inputs */
input {
    width: 100%;
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
}

input:focus {
    outline: none;
    border-color: #6366f1;
}

/* Disabled */
input[disabled] {
    background: #f1f5f9;
    cursor: not-allowed;
}

/* Error */
.error {
    background: #fee2e2;
    color: #b91c1c;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    font-size: 14px;
}

/* Buttons */
.btn-container {
    text-align: center;
    margin-top: 25px;
}

.btn {
    display: inline-block;
    padding: 10px 18px;
    margin: 5px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
    color: white;
    border: none;
    cursor: pointer;
}

.update-btn {
    background: #6366f1;
}

.update-btn:hover {
    background: #4f46e5;
}

.cancel-btn {
    background: #64748b;
}

.cancel-btn:hover {
    background: #475569;
}

</style>

<script>
function validateForm() {
    let desc = document.forms["unitForm"]["description"].value.trim();

    if (desc === "") {
        alert("Description is required");
        return false;
    }

    if (desc.length < 3) {
        alert("Description must be at least 3 characters");
        return false;
    }

    return true;
}
</script>

</head>

<body>

<div class="container">

    <h2>Edit Unit</h2>

    <div class="form-card">

        <!-- Backend Message -->
        <c:if test="${not empty message}">
            <div class="error">${message}</div>
        </c:if>

        <form name="unitForm"
              action="${pageContext.request.contextPath}/unit/update"
              method="post"
              onsubmit="return validateForm()">

            <!-- Hidden Identifier -->
            <input type="hidden" name="identifier" value="${unit.identifier}" />

            <!-- Unit Name (Non-editable) -->
            <div class="form-group">
                <label>Unit</label>
                <input type="text"
                       value="${unit.identifier}"
                       disabled>
            </div>

            <!-- Description (Editable) -->
            <div class="form-group">
                <label>Description</label>
                <input type="text"
                       name="description"
                       value="${unit.description}"
                       placeholder="Enter description">
            </div>

            <!-- Buttons -->
            <div class="btn-container">

                <button type="submit" class="btn update-btn">
                    Update
                </button>

                <a href="${pageContext.request.contextPath}/unit/list"
                   class="btn cancel-btn">
                    Cancel
                </a>

            </div>

        </form>

    </div>

</div>

</body>
</html>