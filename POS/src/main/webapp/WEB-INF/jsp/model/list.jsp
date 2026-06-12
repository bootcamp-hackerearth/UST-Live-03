<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <title>Model List</title>
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', Arial, sans-serif;
            background: #fff8f0;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 90%;
            margin: auto;
            background: #efe3d9;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.15);
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }

        h2 {
            text-align: center;
            margin: 20px 0 30px;
            color: #4a2e2b;
            font-size: 24px;
            font-weight: 700;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 10px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            color: #fff8f0;
            background-color: #6b4a46;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: 0.2s ease;
        }

        .btn:hover {
            background-color: #543835;
        }

        .btn-edit {
            background-color: #6b4a46;
        }

        .btn-edit:hover {
            background-color: #543835;
        }

        .btn-delete {
            background-color: #6b4a46;
        }

        .btn-delete:hover {
            background-color: #543835;
        }

        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            border-radius: 14px;
            overflow: hidden;
        }

        th {
            background-color: #4a2e2b;
            color: #fff8f0;
            padding: 16px;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
            vertical-align: middle;
        }

        tr {
            height: 70px;
        }

        tbody tr {
            background: #fff8f0;
        }

        tbody tr:nth-child(even) {
            background: #eadfd6;
        }

        tbody tr:hover {
            background: #e2cec1;
        }

        td {
            padding: 14px 16px;
            font-size: 14px;
            color: #000;
            text-align: center;
            vertical-align: middle;
        }

        td:nth-child(3) form {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 4px;
        }

        .action-cell {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            white-space: nowrap;
        }

        .action-cell i {
            font-size: 14px;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 46px;
            height: 22px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cfc4bb;
            border-radius: 20px;
            transition: 0.4s;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 16px;
            width: 16px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            border-radius: 50%;
            transition: 0.4s;
        }

        input:checked + .slider {
            background-color: #6b4a46;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="top-bar">
        <a href="${pageContext.request.contextPath}/" class="btn">Home</a>
        <a href="${pageContext.request.contextPath}/model/add" class="btn">
            + Add Model
        </a>
    </div>
    <h2>Model List</h2>
    <table>
        <tr>
            <th>ID</th>
            <th>Identifier</th>
            <th>Model Name</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        <c:forEach var="model" items="${models}">
            <tr>
                 <td>${model.id}</td>
                 <td>${model.identifier}</td>
                 <td>${model.name}</td>
                 <td class="text-center">
                     <form action="${pageContext.request.contextPath}/model/toggleStatus" method="get">
                         <input type="hidden" name="identifier" value="${model.identifier}"/>
                             <label class="switch">
                                 <input type="checkbox" onchange="this.form.submit()"
                                     <c:if test="${model.status}">checked</c:if>>
                                     <span class="slider"></span>
                             </label>
                     </form>
                     <small class="text-primary">
                         <c:choose>
                             <c:when test="${model.status}">Active</c:when>
                             <c:otherwise>Inactive</c:otherwise>
                         </c:choose>
                     </small>
                 </td>
                <td class="action-cell">
                    <a href="${pageContext.request.contextPath}/model/get?identifier=${model.identifier}"
                       class="btn btn-edit"
                       title = "Edit Model">
                       <i class="fa-solid fa-pen"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/model/delete?identifier=${model.identifier}"
                       class="btn btn-delete"
                       onclick="return confirm('Are you sure you want to delete this model?');"
                       title = "Delete Model">
                       <i class="fa-solid fa-trash"></i>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>