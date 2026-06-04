<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Brand</title>

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

.save-btn {
    background: #6366f1;
}

.save-btn:hover {
    background: #4f46e5;
}

.cancel-btn {
    background: #64748b;
}

.cancel-btn:hover {
    background: #475569;
}

</style>

</head>

<body>

<div class="container">

    <h2>Add Brand</h2>

    <div class="form-card">

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form name="brandForm"
              action="${pageContext.request.contextPath}/brand/add"
              method="post">

            <div class="form-group">
                <label>Brand Name *</label>
                <input type="text" name="identifier"
                       value="${brand.identifier}"
                       placeholder="Enter brand name"
                       required
                       minlength="2">
            </div>

            <div class="form-group">
                <label>Description *</label>
                <textarea name="description" rows="3"
                          placeholder="Enter description"
                          required>${brand.description}</textarea>
            </div>

            <div class="btn-container">

                <button type="submit" class="btn save-btn">
                    Save
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
