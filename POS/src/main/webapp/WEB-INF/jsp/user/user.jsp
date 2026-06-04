<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

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
            width: 460px;
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

        .btn-submit:hover {
            opacity: 0.95;
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
    </style>
</head>

<body>

<div class="card-container">

    <a href="/user/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Update User</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-danger">
            ${message}
        </div>
    </c:if>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <form:hidden path="id"/>

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        required="true"
                        pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com$"
                        title="Email must be valid and end with .com"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        pattern="[0-9]{10}"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                        title="Enter exactly 10 digit phone number"/>
        </div>

        <div class="form-group">
            <label>Roles</label>

            <div class="roles-preview">
                Current:
                <c:forEach var="r" items="${userDto.roles}">
                    <span class="badge">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <input type="submit" value="Update User" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>