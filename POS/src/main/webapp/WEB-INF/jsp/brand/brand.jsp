<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Brand</title>

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

.form-card {
    background: #ffffff;
    border-radius: 14px;
    padding: 30px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.06);
}

.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    font-size: 14px;
    margin-bottom: 6px;
    font-weight: 500;
}

input, textarea {
    width: 100%;
    padding: 10px;
    border-radius: 6px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
}

input:focus, textarea:focus {
    outline: none;
    border-color: #6366f1;
}

input[disabled] {
    background: #f1f5f9;
    cursor: not-allowed;
}

.error {
    background: #fee2e2;
    color: #b91c1c;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    font-size: 14px;
}

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
    let desc = document.forms["brandForm"]["description"].value.trim();

    if (desc.length > 0 && desc.length < 3) {
        alert("Description must be at least 3 characters if provided");
        return false;
    }

    return true;
}
</script>

</head>

<body>

<div class="container">

    <h2>Edit Brand</h2>

    <div class="form-card">

        <c:if test="${not empty message}">
            <div class="error">${message}</div>
        </c:if>

        <form name="brandForm"
              action="${pageContext.request.contextPath}/brand/update"
              method="post"
              onsubmit="return validateForm()">

            <input type="hidden" name="identifier" value="${brand.identifier}" />

            <div class="form-group">
                <label>Brand Name</label>
                <input type="text"
                       value="${brand.identifier}"
                       disabled>
            </div>

            <div class="form-group">
                <label>Description</label>
                <textarea name="description" rows="3"
                          placeholder="Enter description" required>${brand.description}</textarea>
            </div>

            <div class="btn-container">

                <button type="submit" class="btn update-btn">
                    Update
                </button>

                <a href="${pageContext.request.contextPath}/brand/list"
                   class="btn cancel-btn">
                    Cancel
                </a>

            </div>

        </form>

    </div>

</div>

</body>
</html>