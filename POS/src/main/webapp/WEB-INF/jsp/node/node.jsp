<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
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
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-submit {
            margin-top: 12px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-cancel {
            margin-top: 10px;
            width: 100%;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }

        .alert-danger {
            background: #fdecea;
            color: #c62828;
        }

        .roles-preview {
            font-size: 12px;
            color: #555;
            margin-bottom: 6px;
        }

        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 6px;
            background: #e0e0e0;
            margin-right: 4px;
            font-size: 11px;
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
            background: rgba(75, 108, 183, 0.08);
            transition: all 0.25s ease;
        }

        .home-link:hover {
            background: #4b6cb7;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
            transform: translateY(-2px);
        }

    </style>
</head>

<body>

<div class="card-container">

    <a href="/node/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Edit Node</h2>

    <c:if test="${empty nodeDto}">
        <div class="alert alert-danger">
            Node not found
        </div>
    </c:if>

    <c:if test="${not empty nodeDto}">
        <form:form action="/node/update"
                   method="post"
                   modelAttribute="nodeDto">

            <form:hidden path="id" value="${nodeDto.id}"/>

            <div class="form-group">
                <label>Node Name</label>
                <form:input path="identifier" placeholder="Enter node name" required="true" readonly="true"/>
            </div>

            <div class="form-group">
                <label>Node Path</label>
                <form:input path="path" placeholder="Enter path" required="true"/>
            </div>

            <div class="form-group">
                <label>Current Roles</label>
                <div class="roles-preview">
                    <c:forEach var="r" items="${nodeDto.roles}">
                        <span class="badge">${r}</span>
                    </c:forEach>
                </div>
            </div>

            <div class="form-group">
                <label>Update Roles</label>
                <form:select path="roles" multiple="true">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
            </div>

            <input type="submit" value="Update Node" class="btn-submit"/>

            <a href="/node/list" class="btn-cancel">Cancel</a>

        </form:form>
    </c:if>

</div>

</body>
</html>