<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Rack</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    color: #1e293b;
}

.container {
    width: 420px;
    background: #ffffff;
    padding: 28px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    font-weight: 600;
    color: #0f172a;
}

label {
    font-size: 13px;
    color: #475569;
}

input {
    width: 100%;
    padding: 10px;
    margin: 6px 0 14px 0;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    background: #f8fafc;
}

input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 6px rgba(99,102,241,0.25);
    background: #ffffff;
}

.shelf-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 6px;
    max-height: 160px;
    overflow-y: auto;
    margin-bottom: 16px;
    background: #f9fafb;
}

.shelf-item {
    display: grid;
    grid-template-columns: 1fr 30px;
    align-items: center;
    padding: 8px 6px;
    font-size: 14px;
    border-radius: 6px;
}

.shelf-item:hover {
    background-color: #eef2ff;
}

.shelf-item input[type="checkbox"] {
    justify-self: center;
}

button {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    font-weight: 600;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 5px 15px rgba(99,102,241,0.3);
}

.bottom-error {
    margin-top: 12px;
    padding: 10px;
    text-align: center;
    border-radius: 6px;
    background: #fee2e2;
    color: #b91c1c;
    font-size: 13px;
}

.link {
    text-align: center;
    margin-top: 14px;
}

.link a {
    color: #4f46e5;
    text-decoration: none;
    font-size: 13px;
}

.link a:hover {
    text-decoration: underline;
}

</style>
</head>

<body>

<div class="container">

<h2>Create Rack</h2>

<form:form modelAttribute="rackDto"
           action="${pageContext.request.contextPath}/rack/add"
           method="post"
           onsubmit="return validateShelves()">

    <label>Rack Name</label>
    <form:input path="identifier" required="true"/>

    <label>Shelves</label>

    <div class="shelf-list">

        <c:forEach var="s" items="${shelves}">
            <div class="shelf-item">
                <span>${s.identifier}</span>
                <form:checkbox path="shelves" value="${s.identifier}" class="shelf-checkbox" />
            </div>
        </c:forEach>

        <span id="shelfError" style="color:red;"></span>

    </div>

    <label>Description</label>
    <form:input path="description" required="true"/>

    <button type="submit">Save Rack</button>

</form:form>

<c:if test="${not empty error}">
    <div class="bottom-error">
        ${error}
    </div>
</c:if>

<div class="link">
    <a href="${pageContext.request.contextPath}/rack/list">
        View All Racks
    </a>
</div>

</div>

<script>
function validateShelves() {

    const checkboxes = document.querySelectorAll(".shelf-checkbox");
    const checked = Array.from(checkboxes).some(cb => cb.checked);
    const error = document.getElementById("shelfError");

    if (!checked) {
        error.innerText = "Please select at least one shelf";
        return false;
    }

    error.innerText = "";
    return true;
}
</script>

</body>
</html>