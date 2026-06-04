<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }

        .page-container {
            max-width: 1300px;
            margin: 50px auto;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .page-title {
            font-size: 38px;
            font-weight: 700;
            color: #2c3e50;
            margin: 0;
        }

        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 25px;
        }

        .card-header h4 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
        }

        .table {
            margin-bottom: 0;
        }

        .table th {
            background-color: #f8f9fa;
            color: #495057;
            font-weight: 600;
            text-align: center;
            border-bottom: 2px solid #dee2e6;
        }

        .table td {
            text-align: center;
            vertical-align: middle;
        }

        .table tbody tr:hover {
            background-color: #f8f9fa;
        }

        .card-footer {
            background: white;
            border-top: 1px solid #e9ecef;
            padding: 20px;
        }

        .btn-add {
            padding: 10px 20px;
            font-weight: 500;
        }

        .btn-back {
            min-width: 100px;
        }

        .action-buttons {
            display: flex;
            justify-content: center;
            gap: 8px;
        }

        .role-badge {
            background-color: #e7f1ff;
            color: #0d6efd;
            border: 1px solid #b6d4fe;
            font-weight: 500;
            padding: 6px 10px;
            border-radius: 20px;
            display: inline-block;
            margin: 2px;
        }

        .empty-box {
            padding: 50px;
            text-align: center;
            color: #6c757d;
            font-size: 18px;
        }

        .empty-box a {
            text-decoration: none;
            font-weight: 500;
        }

        .path-code {
            background-color: #f1f3f5;
            color: #495057;
            padding: 5px 8px;
            border-radius: 6px;
            font-size: 13px;
        }

        .muted-text {
            color: #6c757d;
            font-size: 14px;
        }

        form {
            margin: 0;
        }
    </style>
</head>

<body>

<div class="container-fluid">

    <div class="page-container">

        <div class="page-header">

            <h1 class="page-title">
                Node Management
            </h1>

            <a href="${pageContext.request.contextPath}/node/add"
               class="btn btn-primary btn-add">
                + Add Node
            </a>

        </div>

        <div class="card">

            <div class="card-header">
                <h4>Node List</h4>
            </div>

            <div class="card-body p-0">

                <c:if test="${not empty success}">
                    <div class="alert alert-success text-center m-3">
                        ${success}
                    </div>
                </c:if>

                <c:choose>

                    <c:when test="${not empty nodes}">

                        <div class="table-responsive">
                            <table class="table table-hover">

                                <thead>
                                <tr>
                                    <th style="width: 10%">ID</th>
                                    <th style="width: 25%">Node Name</th>
                                    <th style="width: 25%">Path</th>
                                    <th style="width: 20%">Roles</th>
                                    <th style="width: 20%">Actions</th>
                                </tr>
                                </thead>

                                <tbody>

                                <c:forEach var="node" items="${nodes}">
                                    <tr>

                                        <td>${node.id}</td>

                                        <td>${node.identifier}</td>

                                        <td>
                                            <span class="path-code">
                                                ${node.path}
                                            </span>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty node.roles}">
                                                    <c:forEach var="role" items="${node.roles}">
                                                        <span class="role-badge">
                                                            ${role}
                                                        </span>
                                                    </c:forEach>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="muted-text">No Roles</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <div class="action-buttons">

                                                <form action="${pageContext.request.contextPath}/node/get"
                                                      method="get">
                                                    <input type="hidden"
                                                           name="identifier"
                                                           value="${node.identifier}" />

                                                    <button type="submit"
                                                            class="btn btn-outline-primary btn-sm">
                                                        Edit
                                                    </button>
                                                </form>

                                                <form action="${pageContext.request.contextPath}/node/delete"
                                                      method="get"
                                                      onsubmit="return confirm('Are you sure you want to delete this node?');">
                                                    <input type="hidden"
                                                           name="identifier"
                                                           value="${node.identifier}" />

                                                    <button type="submit"
                                                            class="btn btn-outline-danger btn-sm">
                                                        Delete
                                                    </button>
                                                </form>

                                            </div>
                                        </td>

                                    </tr>
                                </c:forEach>

                                </tbody>

                            </table>
                        </div>

                    </c:when>

                    <c:otherwise>
                        <div class="empty-box">
                            <p>No Nodes Found</p>

                            <a href="${pageContext.request.contextPath}/node/add"
                               class="btn btn-primary">
                                + Create First Node
                            </a>
                        </div>
                    </c:otherwise>

                </c:choose>

            </div>

            <div class="card-footer d-flex justify-content-between">

                <a href="${pageContext.request.contextPath}/"
                   class="btn btn-secondary btn-back">
                    Back
                </a>

            </div>

        </div>

    </div>

</div>

</body>
</html>