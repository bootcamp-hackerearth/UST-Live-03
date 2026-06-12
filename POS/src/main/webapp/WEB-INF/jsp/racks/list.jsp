<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Racks List</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            font-family: 'Segoe UI', sans-serif;
        }

        .card {
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.85);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #d1d5db, #f3f4f6);
            font-weight: 700;
        }

        table th { background: #e5e7eb; }
        table td { background: #f9fafb; }

        .icon-btn {
            border: none;
            padding: 6px 10px;
            border-radius: 8px;
            color: white;
        }

        .edit-btn { background: #3b82f6; }
        .delete-btn { background: #ef4444; }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            background-color: #dc2626;
            border-radius: 24px;
            top: 0; left: 0; right: 0; bottom: 0;
            transition: 0.4s;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #22c55e;
        }

        input:checked + .slider:before {
            transform: translateX(22px);
        }

        .toggle-container {
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="col-md-10 mx-auto">

        <div class="card shadow-lg">

            <div class="card-header text-center">
                <h4>List of Racks</h4>
            </div>

            <div class="card-body">

                <c:if test="${empty racks}">
                    <div class="alert alert-warning text-center">
                        No racks found
                    </div>
                </c:if>

                <c:if test="${not empty racks}">
                    <table class="table table-bordered text-center align-middle">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Rack Name</th>
                            <th>Shelves</th>
                            <th>Status</th>
                            <th>Update</th>
                            <th>Delete</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="rack" items="${racks}">
                            <tr>
                                <td>${rack.id}</td>
                                <td>${rack.identifier}</td>
                                <td>${rack.shelves}</td>

                                <td>
                                    <div class="toggle-container"
                                         onclick="window.location.href='${pageContext.request.contextPath}/racks/toggle?identifier=${rack.identifier}'">

                                        <label class="switch">
                                            <input type="checkbox"
                                                   ${rack.status eq 'ACTIVE' ? 'checked' : ''}
                                                   disabled>
                                            <span class="slider"></span>
                                        </label>
                                    </div>
                                </td>

                                <td>
                                    <a href="${pageContext.request.contextPath}/racks/get?identifier=${rack.identifier}"
                                       class="icon-btn edit-btn">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>
                                </td>

                                <td>
                                    <a href="${pageContext.request.contextPath}/racks/delete?identifier=${rack.identifier}"
                                       class="icon-btn delete-btn"
                                       onclick="return confirm('Delete this rack?')">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>

            <div class="card-footer text-center">
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Home</a>
                <a href="${pageContext.request.contextPath}/racks/add" class="btn btn-primary">+ Add Rack</a>
            </div>

        </div>
    </div>
</div>
</body>
</html>