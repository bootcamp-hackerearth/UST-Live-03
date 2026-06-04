<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>

<body class="bg-light d-flex justify-content-center align-items-center" style="height:100vh;">

<div class="card p-4 shadow" style="width: 450px;">

    <h4 class="text-center mb-3 text-primary">Edit Node</h4>

    <c:if test="${empty nodeDto}">
        <div class="alert alert-danger text-center">
            Node not found
        </div>
    </c:if>

    <c:if test="${not empty nodeDto}">
        <form:form action="/node/update"
                   method="post"
                   modelAttribute="nodeDto"
                   onsubmit="return validateRoles()">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label>Node Name</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            readonly="true"/>
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
                            <form:checkbox path="roles"
                                           value="${role.identifier}"
                                           cssClass="roleCheckbox"/>
                            ${role.identifier}
                        </label>
                    </c:forEach>
                </div>

                <small id="rolesError" class="text-danger"></small>
            </div>

            <div class="d-flex justify-content-between mt-3">
                <a href="/node/list" class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update
                </button>
            </div>

        </form:form>
    </c:if>

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