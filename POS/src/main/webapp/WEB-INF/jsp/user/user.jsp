<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html >
<head>
    <title>Update User</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

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
            width: 520px;
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

        /*  Back Icon (same everywhere) */
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
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            margin-bottom: 6px;
            display: block;
            font-weight: 500;
            font-size: 14px;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
        }

        .form-control:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        select[multiple] {
            height: 120px;
        }

        .badge {
            font-size: 12px;
            background: #6c757d;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 12px;
            border: none;
            cursor: pointer;
            font-weight: 600;
            font-size: 15px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            transition: 0.25s ease;
        }

        .btn-submit:hover {
            transform: scale(1.05);
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
            background: #d1ecf1;
            color: #0c5460;
        }

        .footer-text {
            text-align: center;
            margin-top: 15px;
            font-size: 12px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="card-container">

    <!-- ✅ Back to User List -->
    <a href="/user/list" class="back-icon">←</a>

    <h2>Update User</h2>

    <!-- ✅ Message -->
    <c:if test="${not empty message}">
        <div class="alert">
            ${message}
        </div>
    </c:if>

    <form:form action="/user/update" method="post" modelAttribute="userDto">

        <!-- ID (hidden) -->
        <form:input path="id" type="hidden"/>

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username" cssClass="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        cssClass="form-control"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        title="Enter exactly 10 digits"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')" />
        </div>

        <div class="form-group">
            <label>Roles</label>

            <div class="mb-2 text-muted">
                Current:
                <c:forEach var="r" items="${userDto.roles}">
                    <span class="badge me-1">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <button type="submit" class="btn-submit">
            Update User
        </button>

    </form:form>

    <div class="footer-text">
        User Management System
    </div>

</div>

</body>
</html>