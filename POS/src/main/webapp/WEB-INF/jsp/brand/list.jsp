<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            padding: 35px;
            border-radius: 12px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        /* HEADER */
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        h2 {
            margin: 0;
            font-weight: 600;
        }

        /* TABLE */
        table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }

        th, td {
            padding: 16px 12px;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            vertical-align: middle;
            text-align: center;
        }

        th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #ddd;
        }

        tr:hover {
            background-color: #f5f7f9;
        }

        /* COLUMN WIDTHS */
        th:nth-child(1), td:nth-child(1) {
            width: 60px;
        }

        th:nth-child(2), td:nth-child(2) {
            width: 180px;
            text-align: left;
            padding-left: 20px;
        }

        th:nth-child(3), td:nth-child(3) {
            width: 350px;
            text-align: left;
            word-wrap: break-word;
            word-break: break-word;
        }

        th:nth-child(4), td:nth-child(4) {
            width: 120px;
        }

        th:nth-child(5), td:nth-child(5) {
            width: 180px;
        }

        /* BUTTONS */
        .btn {
            padding: 6px 12px;
            text-decoration: none;
            border-radius: 5px;
            font-size: 13px;
            color: white;
            font-weight: 500;
        }

        .btn-add { background: #28a745; }
        .btn-edit { background: #007bff; }
        .btn-delete { background: #dc3545; }
        .btn-back { background: #6c757d; }

        /* ACTIONS */
        .actions {
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        /* EMPTY */
        .empty {
            text-align: center;
            padding: 40px;
            color: #888;
        }

        /* TOGGLE */
        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 26px;
        }

        .switch input {
            opacity: 0;
        }

        .slider {
            position: absolute;
            background-color: #dc3545;
            border-radius: 26px;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        .toggle-container {
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
        }

    </style>
</head>

<body>

<div class="container">

    <!-- HEADER -->
    <div class="header">
        <h2>Brand List</h2>
        <div>
            <a href="/" class="btn btn-back">Home</a>
            <a href="/brand/add" class="btn btn-add">+ Add Brand</a>
        </div>
    </div>

    <!-- EMPTY -->
    <c:if test="${empty brands}">
        <div class="empty">No brands available</div>
    </c:if>

    <!-- TABLE -->
    <c:if test="${not empty brands}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Brand Name</th>
                <th>Description</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="brand" items="${brands}">
                <tr>

                    <!-- ID -->
                    <td>${brand.id}</td>

                    <!-- NAME -->
                    <td><strong>${brand.identifier}</strong></td>

                    <!-- DESCRIPTION -->
                    <td>
                        <c:choose>
                            <c:when test="${empty brand.description}">
                                <span style="color:#aaa;">—</span>
                            </c:when>
                            <c:otherwise>
                                ${brand.description}
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <!-- STATUS -->
                    <td>
                        <div class="toggle-container"
                             onclick="window.location.href='/brand/toggle?identifier=${brand.identifier}'">

                            <label class="switch">
                                <input type="checkbox" ${brand.status ? "checked" : ""} disabled>
                                <span class="slider"></span>
                            </label>

                        </div>
                    </td>

                    <!-- ACTIONS -->
                    <td>
                        <div class="actions">
                            <a href="/brand/get?identifier=${brand.identifier}" class="btn btn-edit">
                                Edit
                            </a>
                            <a href="/brand/delete?identifier=${brand.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Delete this brand?');">
                                Delete
                            </a>
                        </div>
                    </td>

                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

</div>

</body>
</html>