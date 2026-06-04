<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Rack Management</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background: #F4F5F7;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            width: 95%;
            max-width: 1100px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 20px;
            color: #FFFFFF;
            text-align: center;
        }

        .content-body {
            padding: 30px;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 22px;
        }

        h2 {
            margin: 0;
            font-size: 18px;
            color: #1F2937;
        }

        .btn-add {
            padding: 10px 22px;
            background: #0B3C5D;
            color: white;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            white-space: nowrap;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 14px;
        }

        thead {
            background: #F9FAFB;
            border-bottom: 2px solid #E5E7EB;
        }

        th {
            padding: 15px;
            text-transform: uppercase;
            font-size: 12px;
            text-align: left;
            font-weight: 700;
            color: #4B5563;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #F3F4F6;
            vertical-align: middle;
        }

        tbody tr:hover {
            background: #F9FAFB;
        }

        .btn-action {
            padding: 6px 14px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 700;
            text-decoration: none;
            display: inline-block;
            margin-right: 8px;
        }

        .btn-view {
            background: #E5E7EB;
            color: #1F2937;
        }

        .btn-delete {
            background: #FEE2E2;
            color: #DC2626;
        }

        .back-link {
            display: block;
            margin-top: 24px;
            text-align: center;
            font-weight: 600;
            font-size: 14px;
            color: #6B7280;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>

    <div class="content-body">

        <div class="top-bar">
            <h2>Rack Management</h2>
            <a href="${pageContext.request.contextPath}/rack/add" class="btn-add">
                + Add New Rack
            </a>
        </div>

        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Rack Identifier</th>
                <th>Shelves</th>
                <th>Actions</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="rack" items="${racks}">
                <tr>
                    <td>${rack.id}</td>

                    <td style="font-weight:600;">
                        ${rack.identifier}
                    </td>

                    <td>
                        <c:forEach var="s" items="${rack.shelfs}" varStatus="st">
                            ${s}<c:if test="${!st.last}">, </c:if>
                        </c:forEach>
                    </td>

                    <td>
                        <a href="${pageContext.request.contextPath}/rack/get?identifier=${rack.identifier}"
                           class="btn-action btn-view">View / Edit</a>

                        <a href="${pageContext.request.contextPath}/rack/delete?identifier=${rack.identifier}"
                           class="btn-action btn-delete"
                           onclick="return confirm('Delete this rack?')">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/" class="back-link">
            ← Back to Homepage
        </a>

    </div>
</div>

</body>
</html>