<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS | Register</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            background: linear-gradient(135deg, #0f766e, #020617);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;
            border-radius: 50%;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: 0.3s ease;
        }

        .back-btn:hover {
            transform: scale(1.05);
        }

        .back-btn::before {
            content: '';
            width: 10px;
            height: 10px;
            border-left: 3px solid #111;
            border-bottom: 3px solid #111;
            transform: rotate(45deg);
            margin-left: 4px;
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.97);
            border-radius: 18px;
            padding: 35px 40px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #0f172a;
        }

        .error {
            background: #fee2e2;
            color: #991b1b;
            padding: 10px;
            border-radius: 8px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 15px;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input, select {
            width: 100%;
            padding: 12px;
            margin-top: 8px;
            margin-bottom: 18px;
            border-radius: 10px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        select[multiple] {
            height: 120px;
        }

        input:focus, select:focus {
            border-color: #0f766e;
            box-shadow: 0 0 0 2px rgba(15,118,110,0.2);
            outline: none;
        }

        button {
            width: 100%;
            padding: 13px;
            border: none;
            border-radius: 25px;
            background: linear-gradient(135deg, #0f766e, #134e4a);
            color: white;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
        }

        .login-link {
            text-align: center;
            margin-top: 15px;
            font-size: 13px;
        }

        .login-link a {
            color: #0f766e;
            font-weight: bold;
            text-decoration: none;
        }
    </style>
</head>

<body>

<c:choose>
    <c:when test="${pageContext.request.userPrincipal ne null}">
        <a href="${pageContext.request.contextPath}/user/list"
           class="back-btn"></a>
    </c:when>
    <c:otherwise>
        <a href="${pageContext.request.contextPath}/login"
           class="back-btn"></a>
    </c:otherwise>
</c:choose>

<div class="card">

    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/register"
               method="post"
               modelAttribute="userDto"
               onsubmit="return validateForm();">

        <label>Name</label>
        <form:input path="name" id="name" required="true"/>

        <label>Email</label>
        <form:input path="username" id="username" required="true"/>

        <label>Roles</label>
        <form:select path="roles" id="roles" multiple="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Mobile Number</label>
        <form:input path="phoneNo"
                    id="phoneNo"
                    type="tel"
                    maxlength="10"
                    required="true"/>

        <label>Password</label>
        <form:password path="password" id="password" required="true"/>

        <button type="submit">Register</button>
    </form:form>

    <c:if test="${pageContext.request.userPrincipal eq null}">
        <div class="login-link">
            Already registered?
            <a href="${pageContext.request.contextPath}/login">Login here</a>
        </div>
    </c:if>

</div>

</body>
</html>