<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category List</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
        }

        .container {
            max-width: 1000px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        h2 { margin: 0; }

        .btn {
            padding: 8px 15px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 13px;
            color: white;
            font-weight: bold;
        }

        .btn-back   { background-color: #6c757d; }
        .btn-add    { background-color: #28a745; }
        .btn-edit   { background-color: #007bff; padding: 6px 12px; }
        .btn-delete { background-color: #dc3545; padding: 6px 12px; }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background-color: #f8f9fa;
            padding: 12px 15px;
            border-bottom: 2px solid #dee2e6;
            text-align: left;
            white-space: nowrap;
        }

        td {
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
            text-align: left;
            vertical-align: middle;  /* ✅ all cells vertically centered */
        }

        /* ✅ Fixed column widths */
        th:nth-child(1), td:nth-child(1) { width: 60px;  }   /* ID */
        th:nth-child(2), td:nth-child(2) { width: 220px; }   /* Identifier */
        th:nth-child(3), td:nth-child(3) { width: 200px; }   /* Super Category */
        th:nth-child(4), td:nth-child(4) { width: 100px; }   /* Status */
        th:nth-child(5), td:nth-child(5) { width: auto;  }   /* Actions */

        tr:hover { background-color: #fafafa; }

        .empty-msg {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        .text-muted { color: #999; }

        /* TOGGLE */
        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 26px;
        }

        .switch input { opacity: 0; width: 0; height: 0; }

        .slider {
            position: absolute;
            background-color: #dc3545;
            border-radius: 26px;
            top: 0; left: 0; right: 0; bottom: 0;
            transition: 0.3s;
            cursor: pointer;
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
            transition: 0.3s;
        }

        input:checked + .slider { background-color: #28a745; }
        input:checked + .slider:before { transform: translateX(24px); }

        /* ✅ Toggle left-aligned to match other columns */
        .toggle-container {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            cursor: pointer;
        }

        /* ✅ Action buttons aligned in a row */
        .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
        }
    </style>
</head>

<body>

<div class="container">

    <!-- HEADER -->
    <div class="header">
        <h2>Category List</h2>
        <div style="display: flex; gap: 8px;">
            <a href="/" class="btn btn-back">Home</a>
            <a href="/category/add" class="btn btn-add">+ Add Category</a>
        </div>
    </div>

    <!-- EMPTY -->
    <c:if test="${empty categories}">
        <div class="empty-msg">No categories available</div>
    </c:if>

    <!-- TABLE -->
    <c:if test="${not empty categories}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Category Identifier</th>
                    <th>Super Category</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
            <c:forEach var="category" items="${categories}">
                <tr>

                    <td>${category.id}</td>

                    <td>${category.identifier}</td>

                    <!-- SUPER CATEGORY -->
                    <td>
                        <c:choose>
                            <c:when test="${empty category.superCategory}">
                                <span class="text-muted">—</span>
                            </c:when>
                            <c:otherwise>
                                ${category.superCategory}
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <!-- STATUS TOGGLE -->
                    <td>
                        <div class="toggle-container"
                             onclick="window.location.href='${pageContext.request.contextPath}/category/toggle?identifier=${category.identifier}'">
                            <label class="switch">
                                <input type="checkbox" ${category.status ? "checked" : ""} disabled>
                                <span class="slider"></span>
                            </label>
                        </div>
                    </td>

                    <!-- ACTIONS -->
                    <td>
                        <div class="action-buttons">
                            <a href="/category/get?identifier=${category.identifier}" class="btn btn-edit">
                                Edit
                            </a>
                            <a href="/category/delete?identifier=${category.identifier}"
                               class="btn btn-delete"
                               onclick="return confirm('Delete this category?');">
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