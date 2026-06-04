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
            background: linear-gradient(135deg, #667eea, #764ba2);
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

                <!-- Message -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success text-center">
                        ${message}
                    </div>
                </c:if>

                <!-- Add Node Form -->
                <form:form method="post"
                           action="${pageContext.request.contextPath}/node/add"
                           modelAttribute="nodeDto">

                    <!-- NODE NAME -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Name</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter node name"/>
                    </div>

                    <!-- PATH -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Path</label>
                        <form:input path="path"
                                    cssClass="form-control"
                                    placeholder="/user/list or /node/list"/>
                    </div>

                    <!-- ROLES -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Authorized Roles</label>

                        <form:select path="roles" multiple="true"
                                     cssClass="form-control">
                            <form:options items="${rolesList}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>

                        <small class="text-muted">
                            Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
                        </small>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Node
                        </button>
                    </div>

                </form:form>

                <!-- BACK TO LIST LINK -->
                <div class="text-center mt-3">
                    <a href="/node/list"
                       class="btn btn-outline-secondary btn-sm">
                        ← Back to Node List
                    </a>
                </div>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>
    </div>
</div>
</body>
</html>
