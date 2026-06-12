<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            background: #ffffff
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center">Edit Role</h4>

        <c:if test="${empty roleDto}">
            <div class="alert alert-danger text-center">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty roleDto}">
            <form:form action="/role/update"
                       method="post"
                       modelAttribute="roleDto">

                <form:hidden path="id"/>

                <div class="mb-4">
                    <label class="form-label">Role Name</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                placeholder="Enter role"
                                readonly="true"/>
                </div>

                <div class="mb-4">
                                    <label class="form-label">Description</label>
                                    <form:input path="description"
                                                cssClass="form-control"
                                                placeholder="Enter description"
                                                required="true"/>
                                </div>


                <div class="d-flex justify-content-between">
                    <a href="/role/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>
        <div class="text-center mt-3">
                <a href="/role/list">← Back to Role List</a>
            </div>

    </div>

     <c:if test="${not empty message}">
            <div style="
                background:#f8d7da;
                color:#721c24;
                padding:10px;
                margin-bottom:15px;
                border-radius:4px;
                text-align:center;">
                ${message}
            </div>
        </c:if>
</div>

</body>
</html>