<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            width: 420px;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 26px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
            letter-spacing: 0.4px;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
            background-color: #ffffff;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        select[multiple] {
            height: 120px;
        }

        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: 26px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
        }

        .btn-update {
            background-color: #1e293b;
            color: #ffffff;
        }

        .btn-update:hover {
            background-color: #0f172a;
            box-shadow: 0 6px 18px rgba(15, 23, 42, 0.35);
        }

        .btn-cancel {
            background-color: #e2e8f0;
            color: #1e293b;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            text-align: center;
            font-size: 13px;
        }

        .btn-cancel:hover {
            background-color: #cbd5e1;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update User</h2>
     <c:if test="${not empty message}">
        <div class="message">
           ${message}
        </div>
     </c:if>

    <form action="${pageContext.request.contextPath}/user/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${user.id}" readonly>
        </div>

        <div class="form-group">
            <label>Username</label>
            <input type="email" name="username" value="${user.username}">
        </div>

        <div class="form-group">
            <label>Name</label>
            <input type="text" name="name" value="${user.name}">
        </div>

        <div class="form-group">
                   <label>Phone Number</label>
                   <input type="tel"
                          name="phoneNo"
                          placeholder="Enter mobile number"
                          pattern="[0-9]{10}"
                          maxlength="10"
                          inputmode="numeric"
                          value="${user.phoneNo}"
                          required>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <select name="roles" multiple>
                <c:forEach items="${roles}" var="role">
                    <option value="${role.identifier}"
                        <c:if test="${user.roles.contains(role.identifier)}">
                            selected="selected"
                        </c:if>>
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="btn-group">
            <button type="submit" class="btn btn-update">
                Update User
            </button>

            <a href="${pageContext.request.contextPath}/user/list"
               class="btn btn-cancel">
                Cancel
            </a>
        </div>

    </form>
</div>

</body>
</html>