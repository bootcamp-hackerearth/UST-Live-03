<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>

<body>

<div class="container mt-5">
    <h2 class="mb-4">Add Node</h2>

    <p style="color:red;">${message}</p>

    <form:form method="post"
               action="${pageContext.request.contextPath}/node/add"
               modelAttribute="nodeDto">


        <div class="mb-3">
            <label class="form-label">Identifier</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        placeholder="USER"
                        required="required" />
        </div>

        <div class="mb-3">
            <label class="form-label">Node Path</label>
            <form:input path="path"
                        cssClass="form-control"
                        placeholder="/user/**"
                        required="required" />
        </div>
        <div class="mb-3">
            <label class="form-label">Roles</label>

            <c:forEach items="${roles}" var="role">
                <div class="form-check">
                    <form:checkbox path="roles"
                                   value="${role.identifier}"
                                   cssClass="form-check-input"
                                   id="role_${role.identifier}"/>

                    <label class="form-check-label" for="role_${role.identifier}">
                        ${role.identifier}
                    </label>
                </div>
            </c:forEach>
        </div>


        <button type="submit" class="btn btn-success me-2">
            Add Node
        </button>

        <a href="${pageContext.request.contextPath}/node/list"
           class="btn btn-secondary">
            Cancel
        </a>


    </form:form>
</div>

</body>
</html>