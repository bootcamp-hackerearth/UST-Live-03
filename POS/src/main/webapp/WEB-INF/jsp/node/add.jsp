<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #ffffff;
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control {
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Node</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty message}">
                    <div class="alert alert-danger text-center">
                        ${message}
                    </div>
                </c:if>

                <c:if test="${not empty success}">
                    <div class="alert alert-success text-center">
                        ${success}
                    </div>
                </c:if>

                <form:form method="post"
                           action="${pageContext.request.contextPath}/node/add"
                           modelAttribute="nodeDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter node identifier"
                                    required="true"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Path</label>
                        <form:input path="path"
                                    cssClass="form-control"
                                    placeholder="/example/path"
                                    required="true"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Roles</label>
                        <form:select path="roles"
                                     cssClass="form-control"
                                     multiple="true"
                                     required="true">
                            <c:forEach var="role" items="${roles}">
                                <form:option value="${role.identifier}">
                                    ${role.identifier}
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </div>


                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/node/list"
                           class="btn btn-secondary">
                            Back
                        </a>

                        <button type="submit" class="btn btn-primary">
                            Add Node
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>