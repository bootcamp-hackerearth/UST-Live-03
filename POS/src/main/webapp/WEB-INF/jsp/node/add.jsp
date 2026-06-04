<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light d-flex justify-content-center align-items-center" style="height:100vh;">

<div class="card p-4 shadow" style="width: 450px;">

    <h4 class="text-center mb-3 text-primary">Add Node</h4>

    <c:if test="${not empty role}">
        <div class="alert alert-success text-center">
            ${role}
        </div>
    </c:if>

    <form:form method="post"
               action="/node/add"
               modelAttribute="nodeDto"
               onsubmit="return validateRoles()">

        <div class="mb-3">
            <label>Node Name *</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        placeholder="Enter node name"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>Node Path *</label>
            <form:input path="path"
                        cssClass="form-control"
                        placeholder="Enter node path"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>Roles *</label>
            <div>
                <c:forEach var="role" items="${roles}">
                    <label class="me-2">
                        <form:checkbox path="roles" value="${role.identifier}"
                        cssClass="roleCheckbox"/>
                        ${role.identifier}
                    </label>
                </c:forEach>
            </div>
            <small id="rolesError" class="text-danger"></small>
        </div>

        <button type="submit" class="btn btn-primary w-100">
            Add Node
        </button>

    </form:form>

    <div class="text-center mt-3">
        <a href="/node/list">← Back to List</a>
    </div>

</div>

</body>
<script>
function validateRoles() {

    const roles = document.querySelectorAll(".roleCheckbox:checked");
    const error = document.getElementById("rolesError");

    if (roles.length === 0) {
        error.textContent = "Please select at least one role";
        return false;
    }

    error.textContent = "";
    return true;
}
</script>
</html>
