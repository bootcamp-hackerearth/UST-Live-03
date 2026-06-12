<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }

        .note-text {
            font-size: 12px;
            color: #6c757d;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Role</h4>

        <!-- ✅ Error Message -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center">
                ${error}
            </div>
        </c:if>

        <!-- ✅ Edit Role Form -->
        <form:form method="post"
                   action="${pageContext.request.contextPath}/role/update"
                   modelAttribute="roles">

            <!-- ✅ Hidden Role ID -->
            <form:hidden path="id"/>

            <!-- ✅ Role Name -->
            <div class="mb-4">
                <label class="form-label" for="identifier">Role Identifier</label>
                <form:input
                        id="identifier"
                        path="identifier"
                        cssClass="form-control"
                        required="true"
                        readOnly="true"/>

                <!-- ✅ Description -->
                <div class="note-text mt-1">
                    Note: Role identifier cannot be changed once created.
                </div>
            </div>

            <!-- ✅ Action Buttons -->
            <div class="d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/role/list"
                   class="btn btn-outline-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update
                </button>
            </div>

        </form:form>

    </div>
</div>

</body>
</html>