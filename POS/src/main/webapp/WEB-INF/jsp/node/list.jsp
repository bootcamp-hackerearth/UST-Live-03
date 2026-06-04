<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Node Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #eef2ff, #e0e7ff);
            min-height: 100vh;
        }

        body, h1, h2, h3, h4, h5, h6,
        p, span, td, th, label, small {
            color: black;
        }

        .card {
            border-radius: 15px;
        }

        .table th {
            background-color: #343a40;
            color: white; /* keep header readable */
        }

        a.node-link {
            text-decoration: none;
            font-weight: 500;
            color: black;
        }

        a.node-link:hover {
            text-decoration: underline;
        }

        .role-text {
            display: inline-block;
            margin: 2px 6px;
            font-size: 15px;
            color: black;
            font-weight: 500;
        }

        .action-icons a.text-primary {
            color: #0d6efd !important;
        }

        .action-icons a.text-danger {
            color: #dc3545 !important;
        }

        .action-icons a {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
        }

        .action-icons a:hover {
            opacity: 0.7;
        }

        .text-muted {
            color: black !important;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">

        <div class="card-body">

            <h3 class="text-center mb-4">Node Management</h3>

            <c:if test="${empty nodes}">
                <div class="alert alert-warning text-center">
                    No nodes found
                </div>
            </c:if>

            <c:if test="${not empty nodes}">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover align-middle text-center">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Identifier</th>
                            <th>Path</th>
                            <th>Roles</th>
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="node" items="${nodes}">
                            <tr>

                                <td>${node.id}</td>

                                <td>
                                    <a class="node-link"
                                       href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}">
                                        ${node.identifier}
                                    </a>
                                </td>

                                <td>${node.path}</td>

                                <td>
                                    <c:if test="${empty node.roles}">
                                        <span>No roles</span>
                                    </c:if>

                                    <c:forEach var="role" items="${node.roles}">
                                        <span class="role-text">${role}</span>
                                    </c:forEach>
                                </td>

                                <td class="action-icons">

                                    <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                                       class="text-primary"
                                       title="Edit">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>

                                    <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                                       class="text-danger"
                                       title="Delete"
                                       onclick="return confirm('Are you sure you want to delete this node?');">
                                        <i class="bi bi-trash"></i>
                                    </a>

                                </td>

                            </tr>
                        </c:forEach>
                        </tbody>

                    </table>
                </div>
            </c:if>

        </div>

        <div class="card-footer text-center">
            <div class="d-flex justify-content-center gap-3">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                    Home
                </a>

                <a href="${pageContext.request.contextPath}/node/add" class="btn btn-success">
                    + Add New Node
                </a>
            </div>

            <div class="small mt-2">
                Node Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>
