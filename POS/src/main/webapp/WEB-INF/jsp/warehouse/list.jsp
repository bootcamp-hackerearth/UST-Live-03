<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
        }

        .card {
            border-radius: 16px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.85);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
            background: linear-gradient(135deg, #d1d5db, #f3f4f6);
            color: #111827 !important;
            font-weight: 700;
        }

        table th {
            background: #e5e7eb;
            color: #111827;
        }

        table td {
            background: #f9fafb;
            color: #111827;
        }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            cursor: pointer;
            transition: 0.2s;
            color: white;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .icon-btn:hover {
            transform: scale(1.1);
        }

        .edit-btn {
            background: #3b82f6;
        }

        .delete-btn {
            background: #ef4444;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-12">

            <div class="card shadow-lg">

                <div class="card-header text-center">
                    <h4 class="mb-0">List of Warehouses</h4>
                </div>

                <div class="card-body">

                    <c:if test="${empty warehouses}">
                        <div class="alert alert-warning text-center">
                            No warehouses found
                        </div>
                    </c:if>

                    <c:if test="${not empty warehouses}">
                        <table class="table table-bordered table-hover text-center align-middle">

                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Warehouse Name</th>
                                <th>Region</th>
                                <th>Country</th>
                                <th>Location</th>
                                <th>Contact Name</th>
                                <th>Contact Number</th>
                                <th>Update</th>
                                <th>Delete</th>
                            </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="w" items="${warehouses}">
                                <tr>

                                    <td>${w.id}</td>
                                    <td>${w.identifier}</td>
                                    <td>${w.region}</td>
                                    <td>${w.country}</td>
                                    <td>${w.location}</td>
                                    <td>${w.contactName}</td>
                                    <td>${w.contactNumber}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/warehouse/get?identifier=${w.identifier}"
                                           class="icon-btn edit-btn">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                    </td>

                                    <td>
                                        <a href="${pageContext.request.contextPath}/warehouse/delete?identifier=${w.identifier}"
                                           class="icon-btn delete-btn"
                                           onclick="return confirm('Are you sure you want to delete this warehouse?');">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>

                                </tr>
                            </c:forEach>
                            </tbody>

                        </table>
                    </c:if>

                </div>

                <div class="card-footer text-center bg-light d-flex justify-content-center gap-3">

                    <a href="${pageContext.request.contextPath}/"
                       class="btn btn-secondary">
                        Home
                    </a>

                    <a href="${pageContext.request.contextPath}/warehouse/add"
                       class="btn btn-primary">
                        + Add New Warehouse
                    </a>

                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>