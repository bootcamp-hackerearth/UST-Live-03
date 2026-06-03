<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <style>
        body {
            background-color: #f1f3f6;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
        }

        .card {
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }

        select[multiple] {
            height: 130px;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow-sm">

                <div class="card-header text-center py-3">
                    <h5 class="mb-0">Edit Node</h5>
                </div>

                <div class="card-body p-4">

                    <c:if test="${empty node}">
                        <div class="alert alert-danger text-center">
                            Node not found
                        </div>
                    </c:if>

                    <c:if test="${not empty node}">
                        <form:form method="post"
                                   action="/node/update"
                                   modelAttribute="node">

                            <div class="mb-3">
                                <label class="form-label">Node Identifier</label>
                                <form:input path="identifier"
                                            cssClass="form-control"
                                            readonly="true"/>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Node Path</label>
                                <form:input path="path"
                                            cssClass="form-control"
                                            required="true"/>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Roles</label>
                                <form:select path="roles"
                                             multiple="true"
                                             cssClass="form-control">
                                    <form:options items="${roles}"
                                                  itemValue="identifier"
                                                  itemLabel="identifier"/>
                                </form:select>
                                <div class="form-text">
                                    Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                                </div>
                            </div>

                            <div class="d-flex justify-content-between mt-4">
                                <a href="${pageContext.request.contextPath}/node/list"
                                   class="btn btn-secondary">
                                    Cancel
                                </a>

                                <button type="submit" class="btn btn-primary">
                                    Update Node
                                </button>
                            </div>

                        </form:form>
                    </c:if>
                </div>
                <div class="card-footer text-muted small text-center">
                    POS Management System
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>