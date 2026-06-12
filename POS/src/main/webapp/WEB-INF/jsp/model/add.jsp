<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Model</title>

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

/* Make textarea same as input */
textarea {
    height: 40px;
    resize: none;
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

    <h2>Add Model</h2>

    <div class="form-card">

        <!-- Backend Error -->
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form name="modelForm"
              action="${pageContext.request.contextPath}/model/add"
              method="post">

            <!-- Model Name -->
            <div class="form-group">
                <label>Model Name *</label>
                <input type="text" name="identifier"
                       value="${model.identifier}"
                       placeholder="Enter model name"
                       required
                       minlength="2">
            </div>

            <!-- Description -->
            <div class="form-group">
                <label>Description *</label>
                <textarea name="description"
                          placeholder="Enter description"
                          required
                          minlength="2">${model.description}</textarea>
            </div>

            <!-- Buttons -->
            <div class="btn-container">

                <button type="submit" class="btn save-btn">
                    Save
                </button>

                <a href="${pageContext.request.contextPath}/model/list"
                   class="btn cancel-btn">
                    Cancel
                </a>

            </div>

        </form>

    </div>

</div>

</body>
</html>