<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Model List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 900px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            background: rgba(75,108,183,0.08);
            border-radius: 50%;
        }

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75,108,183,0.08);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th {
            background: #4b6cb7;
            color: #fff;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
            font-size: 14px;
        }

        tr:hover {
            background: #f7f9ff;
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
        }

        .alert-warning {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            background: #fff3cd;
            color: #856404;
        }

        .footer-actions {
            margin-top: 20px;
            text-align: center;
        }

        .btn-add {
            padding: 12px 18px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
        }

        /* ✅ Toggle switch */
        .toggle-switch {
            position: relative;
            width: 52px;
            height: 26px;
            display: inline-block;
        }

        .toggle-switch input {
            display: none;
        }

        .toggle-slider {
            position: absolute;
            inset: 0;
            background-color: #e74c3c; /* red */
            border-radius: 30px;
            transition: .3s;
            cursor: pointer;
        }

        .toggle-slider:before {
            content: "";
            position: absolute;
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: .3s;
        }

        .toggle-switch input:checked + .toggle-slider {
            background-color: #2ecc71; /* green */
        }

        .toggle-switch input:checked + .toggle-slider:before {
            transform: translateX(26px);
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="${pageContext.request.contextPath}/" class="back-icon">←</a>
    <a href="${pageContext.request.contextPath}/" class="home-link">Home</a>

    <h2>List of Models</h2>

    <c:if test="${empty models}">
        <div class="alert-warning">No Model found</div>
    </c:if>

    <c:if test="${not empty models}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Model Name</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="model" items="${models}">
                <tr>
                    <td>${model.id}</td>
                    <td>${model.identifier}</td>

                    <td>
                        <form action="${pageContext.request.contextPath}/model/toggleStatus"
                              method="post" style="margin:0;">
                            <input type="hidden" name="identifier"
                                   value="${model.identifier}"/>

                            <label class="toggle-switch">
                                <input type="checkbox"
                                       ${model.status ? "checked" : ""}
                                       onchange="this.form.submit()"/>
                                <span class="toggle-slider"></span>
                            </label>
                        </form>
                    </td>

                    <td>
                        <a href="${pageContext.request.contextPath}/model/get?identifier=${model.identifier}"
                           class="action-icon" title="Edit">✏️</a>

                        <a href="${pageContext.request.contextPath}/model/delete?identifier=${model.identifier}"
                           class="action-icon"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this model?');">🗑</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-actions">
        <a href="${pageContext.request.contextPath}/model/add"
           class="btn-add">+ Add New Model</a>
    </div>

</div>

</body>
</html>