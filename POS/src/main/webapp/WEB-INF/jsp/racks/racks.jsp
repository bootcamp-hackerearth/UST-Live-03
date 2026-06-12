<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
    <title>View Rack</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }

        .card {
            width: 420px;
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6);
            border-radius: 15px 15px 0 0;
            text-align: center;
            font-weight: 600;
            color: #111827;
        }

        .table td {
            background: #f9fafb;
            color: #111827;
            font-weight: 500;
        }

        .label-cell {
            font-weight: 600;
            color: #374151;
            width: 40%;
            background: #e5e7eb !important;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">

    <div class="card-header">
        <h4>Rack Details</h4>
    </div>

    <div class="card-body">

        <c:if test="${empty racks}">
            <div class="alert alert-danger text-center">
                Rack not found
            </div>
        </c:if>

        <c:if test="${not empty racks}">
            <table class="table table-bordered">

                <tr>
                    <td class="label-cell">ID</td>
                    <td>${racks.id}</td>
                </tr>

                <tr>
                    <td class="label-cell">Rack Name</td>
                    <td>${racks.identifier}</td>
                </tr>

                <tr>
                    <td class="label-cell">Shelves</td>
                    <td>
                        <c:forEach var="s" items="${racks.shelves}">
                            <span class="badge bg-primary me-1">${s}</span>
                        </c:forEach>
                    </td>
                </tr>
            </table>
        </c:if>

        <div class="text-center mt-3">
            <a href="/racks/list" class="btn btn-secondary">
                Back
            </a>
        </div>
    </div>
</div>
</body>
</html>