<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .page-wrapper {
            width: 1100px;
            background: #f3efe9;
            padding: 34px 42px;
            box-sizing: border-box;
        }

        .top-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .page-title {
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
            margin: 0;
        }

        .top-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .back-btn {
            height: 48px;
            min-width: 120px;
            padding: 0 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: border-box;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1px;
            border: 2px solid #3f3f3f;
            background: transparent;
            color: #3f3f3f;
            transition: 0.3s;
        }

        .back-btn:hover {
            background: #3f3f3f;
            color: #ffffff;
        }

        .error-message {
            margin-bottom: 22px;
            padding: 12px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 13px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            text-align: left;
            padding: 14px 12px;
            font-size: 11px;
            letter-spacing: 2px;
            color: #8a8a8a;
            border-bottom: 3px solid #d6d1cb;
        }

        td {
            padding: 20px 12px;
            border-bottom: 2px solid #dedad5;
            color: #2f2f2f;
            font-size: 14px;
            vertical-align: middle;
        }

        .action-buttons {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .action-link {
            width: 44px;
            height: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: border-box;
            text-decoration: none;
            font-size: 18px;
            font-weight: 700;
            border: 2px solid #3f3f3f;
            transition: 0.3s;
        }

        .edit {
            background: #3f3f3f;
            color: #ffffff;
        }

        .edit:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .delete {
            background: transparent;
            color: #3f3f3f;
        }

        .delete:hover {
            background: #3f3f3f;
            color: #ffffff;
        }

        .empty-message {
            text-align: center;
            padding: 30px 0;
            color: #6b7280;
            font-size: 15px;
        }

    </style>

</head>

<body>

<div class="page-wrapper">

    <div class="top-section">

        <h2 class="page-title">
            User Management
        </h2>

        <div class="top-actions">

            <a href="${pageContext.request.contextPath}/"
               class="back-btn">

                ᐸ BACK

            </a>

        </div>

    </div>

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <c:if test="${empty users}">

        <div class="empty-message">

            No users found

        </div>

    </c:if>

    <c:if test="${not empty users}">

        <table>

            <thead>

            <tr>

                <th>EMAIL</th>
                <th>NAME</th>
                <th>PHONE</th>
                <th>ROLES</th>
                <th>ACTION</th>

            </tr>

            </thead>

            <tbody>

            <c:forEach var="user" items="${users}">

                <tr>

                    <td>${user.username}</td>

                    <td>${user.name}</td>

                    <td>${user.phoneNo}</td>

                    <td>${user.roles}</td>

                    <td>

                        <div class="action-buttons">

                            <a href="${pageContext.request.contextPath}/user/get?username=${user.username}"
                               class="action-link edit"
                               title="Edit">

                                ✎

                            </a>

                            <a href="${pageContext.request.contextPath}/user/delete?username=${user.username}"
                               class="action-link delete"
                               title="Delete"
                               onclick="return confirm('Delete this user?')">

                                🗑

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