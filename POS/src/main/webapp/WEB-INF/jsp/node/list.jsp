<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Node Management</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Inter', sans-serif;
            background: #d1d5db;
        }

        /* MAIN CONTAINER */
        .container {
            width: 90%;
            max-width: 900px;
            margin: 80px auto;
            background: #f1f5f9;
            padding: 35px;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        /* TITLE */
        h2 {
            text-align: center;
            margin-bottom: 25px;
            font-size: 22px;
            color: #0891b2;
            font-weight: 600;
        }

        /* TABLE */
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #0891b2;
            color: white;
            padding: 12px;
            font-size: 13px;
            text-align: center;
        }

        td {
            padding: 14px 10px;
            border-bottom: 1px solid #e2e8f0;
            font-size: 13px;
            text-align: center;
        }

        tr:hover {
            background: rgba(8,145,178,0.05);
        }

        /* ROLES */
        .roles {
            color: #475569;
            font-size: 12px;
        }

        /* BUTTON BASE */
        .btn {
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            text-decoration: none;
            font-weight: 600;
            color: white;
            transition: 0.25s;
            display: inline-block;
        }

        /* EDIT */
        .edit-btn {
            background: linear-gradient(135deg, #0891b2, #0e7490);
        }

        .edit-btn:hover {
            background: linear-gradient(135deg, #0e7490, #075985);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(8,145,178,0.4);
        }

        /* DELETE */
        .delete-btn {
            background: linear-gradient(135deg, #ef4444, #dc2626);
        }

        .delete-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(239,68,68,0.4);
        }

        /* EMPTY */
        .empty {
            text-align: center;
            padding: 30px;
            color: #475569;
            font-size: 14px;
        }

        /* FOOTER */
        .footer {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 25px;
        }

        .home-btn {
            background: #64748b;
        }

        .add-btn {
            background: linear-gradient(135deg, #0891b2, #0e7490);
        }

        .add-btn:hover {
            background: linear-gradient(135deg, #0e7490, #075985);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(8,145,178,0.4);
        }
    </style>
</head>

<body>

<div class="container">

    <h2>List of Nodes</h2>

    <c:choose>
        <c:when test="${empty nodes}">
            <div class="empty">
                No nodes available
            </div>
        </c:when>

        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>Identifier</th>
                    <th>Path</th>
                    <th>Roles</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="node" items="${nodes}">
                    <tr>
                        <td>${node.identifier}</td>
                        <td>${node.path}</td>

                        <td class="roles">
                            <c:forEach var="role" items="${node.roles}" varStatus="s">
                                ${role}<c:if test="${!s.last}">, </c:if>
                            </c:forEach>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/node/get?identifier=${node.identifier}"
                               class="btn edit-btn">Edit</a>

                            <a href="${pageContext.request.contextPath}/node/delete?identifier=${node.identifier}"
                               class="btn delete-btn">
                                Delete
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="footer">
        <a href="/" class="btn home-btn">Home</a>

        <a href="${pageContext.request.contextPath}/node/add"
           class="btn add-btn">Add Node</a>
    </div>

</div>

</body>
</html>