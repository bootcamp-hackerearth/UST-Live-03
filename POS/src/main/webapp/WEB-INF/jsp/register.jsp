<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            width: 430px;
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

        input {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .role-box {
            border: 1px solid #ccc;
            border-radius: 8px;
            padding: 10px;
            max-height: 140px;
            overflow-y: auto;
        }

        .role-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 6px 0;
            font-size: 14px;
        }

        .role-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
        }

        .btn-submit {
            margin-top: 12px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4e73df, #2e59d9);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px 14px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            font-size: 14px;
            font-weight: 600;
            text-align: center;
            border: 1px solid #7dd3fc;
        }

        .back-btn {
            display: inline-block;
            margin-top: 18px;
            padding: 8px 18px;
            border-radius: 20px;
            border: 1px solid #4b6cb7;
            color: #4b6cb7;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
        }

        .back-btn:hover {
            background-color: #4b6cb7;
            color: #fff;
        }
    </style>
</head>
<body>
<div class="register-card">
    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>
    <h2>User Registration</h2>
    <form:form action="register" method="post" modelAttribute="userDto" onsubmit="return validateRoles()">
        <div class="form-group">
            <label>Name</label>
            <form:input path="name"/>
        </div>
        <div class="form-group">
            <label>Email</label>
            <form:input path="username" type="email"/>
        </div>
        <div class="form-group">
            <label>Roles <span style="color:red">*</span></label>
            <div class="role-box">
                <c:forEach items="${roles}" var="role">
                    <label class="role-item">
                        <input type="checkbox"
                               name="roles"
                               value="${role.identifier}" />
                        <span>${role.identifier}</span>
                    </label>
                </c:forEach>
            </div>
        </div>
        <div class="form-group">
            <label>Phone Number <span style="color:red">*</span></label>
            <form:input path="phoneNo"
                        placeholder="Enter 10-digit number"
                        required="required"
                        inputmode="numeric"
                        pattern="^[6-9][0-9]{9}$"
                        maxlength="10"
                        oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                        title="Enter valid 10-digit Indian mobile number"/>
        </div>
        <div class="form-group">
            <label>Password</label>
            <form:password path="password"/>
        </div>
        <input type="submit" value="Register" class="btn-submit"/>
        <div style="text-align:center;">
            <a href="/login" class="back-btn">Back</a>
        </div>
    </form:form>
</div>
<script>
    function validateRoles() {
        const checked = document.querySelectorAll('input[name="roles"]:checked').length;
        if (checked === 0) {
            alert("Please assign the role to user");
            return false;
        }
        return true;
    }
</script>
</body>
</html>