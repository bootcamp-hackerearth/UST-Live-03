<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #ffffff;
            min-height: 100vh;
        }
        .card {
            border-radius: 16px;
        }
        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }
        table th {
            background-color: #0d6efd;
            color: white;
        }
        .status-label {
            font-size: 0.85rem;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-10">

            <div class="card shadow-lg">
                <div class="card-header bg-primary text-white text-center">
                    <h4 class="mb-0">List of Nodes</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty nodes}">
                        <div class="alert alert-warning text-center">
                            No nodes found
                        </div>
                    </c:if>

                    <c:if test="${not empty nodes}">
                        <table class="table table-bordered table-hover text-center align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Identifier</th>
                                    <th>Path</th>
                                    <th>Roles</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="node" items="${nodes}">
                                <tr>
                                    <td>${node.id}</td>
                                    <td>${node.identifier}</td>
                                    <td>${node.path}</td>
                                    <td>${node.roles}</td>

                                    <td>
                                        <form method="get"
                                              action="${pageContext.request.contextPath}/node/toggle"
                                              class="d-inline">

                                            <input type="hidden" name="identifier" value="${node.identifier}">
                                            <input type="hidden" name="status" value="${!node.status}">

                                            <div class="form-check form-switch d-flex justify-content-center align-items-center gap-2">
                                                <input class="form-check-input"
                                                       type="checkbox"
                                                       ${node.status ? "checked" : ""}
                                                       onchange="this.form.submit()">

                                                <span class="status-label ${node.status ? 'text-success' : 'text-danger'}">
                                                    ${node.status ? 'Active' : 'Inactive'}
                                                </span>
                                            </div>
                                        </form>
                                    </td>

                                    <td class="d-flex justify-content-center gap-2">
                                        <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                           class="btn btn-warning btn-sm">
                                            Edit
                                        </a>

                                        <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Are you sure you want to delete this node?');">
                                            Delete
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                        Home
                    </a>

                    <a href="${pageContext.request.contextPath}/node/add" class="btn btn-success">
                        + Add New Node
                    </a>
                </div>

            </div>

        </div>
    </div>
</div>

</body>
</html>