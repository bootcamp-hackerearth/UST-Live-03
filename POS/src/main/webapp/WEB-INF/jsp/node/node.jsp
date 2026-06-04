<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Edit Node</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", Arial, sans-serif;
    background: #f8fafc;
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 450px;
    background: white;
    border-radius: 16px;
    padding: 30px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}

h3 {
    text-align: center;
    color: #0f172a;
    margin-bottom: 20px;
}

.message {
    background: #e0f2fe;
    color: #075985;
    padding: 10px;
    border-radius: 8px;
    font-size: 13px;
    text-align: center;
    margin-bottom: 15px;
}

label {
    font-size: 13px;
    font-weight: 600;
    color: #334155;
    display: block;
    margin-bottom: 6px;
}

input {
    width: 100%;
    padding: 11px;
    margin-bottom: 18px;
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    font-size: 14px;
}

.dropdown {
    position: relative;
    margin-bottom: 18px;
}

.dropdown-btn {
    width: 100%;
    padding: 11px;
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    background: #ffffff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    cursor: pointer;
}

.dropdown-content {
    display: none;
    position: absolute;
    width: 100%;
    background: #ffffff;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    margin-top: 6px;
    max-height: 200px;
    overflow-y: auto;
    z-index: 20;
    padding: 6px 0;
}

.dropdown.active .dropdown-content {
    display: block;
}

.role-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 14px;
    font-size: 13px;
    cursor: pointer;
}

.role-item:hover {
    background: #f1f5f9;
}

.role-item input {
    width: 16px;
    height: 16px;
}

.btn-group {
    display: flex;
    gap: 10px;
    margin-top: 20px;
}

button, a.btn {
    flex: 1;
    padding: 12px;
    border-radius: 8px;
    border: none;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    text-align: center;
    text-decoration: none;
    color: #fff;
}

.btn-save {
    background: #0f766e;
}

.btn-save:hover {
    background: #115e59;
}

.btn-cancel {
    background: #334155;
}

.btn-cancel:hover {
    background: #1e293b;
}
</style>

<script>
function toggleDropdown() {
    document.getElementById("roleDropdown").classList.toggle("active");
}

function updateSelectedRoles() {
    const checked = document.querySelectorAll("input[name='roles']:checked");
    const values = Array.from(checked).map(cb => cb.value);
    document.getElementById("selectedRoles").innerText =
        values.length ? values.join(", ") : "Select Roles";
}

document.addEventListener("click", function(e) {
    const dropdown = document.getElementById("roleDropdown");
    if (!dropdown.contains(e.target)) {
        dropdown.classList.remove("active");
    }
});

window.onload = updateSelectedRoles;
</script>
</head>

<body>

<div class="card">

    <h3>Edit Node</h3>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <c:if test="${empty node}">
        <div class="message">Node not found</div>
    </c:if>

    <c:if test="${not empty node}">
        <form:form action="${pageContext.request.contextPath}/node/update"
                   method="post"
                   modelAttribute="node">

            <form:hidden path="id"/>

            <label>Node Identifier</label>
            <form:input path="identifier" readonly="true"/>

            <label>Node Path</label>
            <form:input path="path" required="true"/>

            <label>Assign Roles</label>

            <div class="dropdown" id="roleDropdown">
                <div class="dropdown-btn" onclick="toggleDropdown()">
                    <span id="selectedRoles">Select Roles</span>
                    <span>▼</span>
                </div>

                <div class="dropdown-content">
                    <c:forEach items="${roles}" var="role">
                        <div class="role-item">
                            <input type="checkbox"
                                   name="roles"
                                   value="${role.identifier}"
                                   <c:forEach items="${node.roles}" var="r">
                                       <c:if test="${r eq role.identifier}">
                                           checked
                                       </c:if>
                                   </c:forEach>
                                   onchange="updateSelectedRoles()">
                            <span>${role.identifier}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <div class="btn-group">
                <button type="submit" class="btn-save">UPDATE NODE</button>

                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-cancel">
                    CANCEL
                </a>
            </div>

        </form:form>
    </c:if>

</div>

</body>
</html>