<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <!-- Same font as dashboard -->
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg: #f4f6f9;
            --card-bg: #ffffff;
            --border: #e2e8f0;
            --text-primary: #0f172a;
            --text-secondary: #334155;
            --text-muted: #64748b;
            --accent: #1d4ed8;
            --danger: #dc2626;
            --shadow-sm: 0 1px 3px rgba(0,0,0,0.08);
        }

        body {
            margin: 0;
            font-family: 'IBM Plex Sans', sans-serif;
            background: var(--bg);
            color: var(--text-primary);
        }

        .page-wrapper {
            max-width: 520px;
            margin: 60px auto;
            padding: 0 20px;
        }

        .page-header {
            margin-bottom: 20px;
        }

        .page-header h1 {
            font-size: 22px;
            font-weight: 600;
        }

        .page-header p {
            font-size: 13.5px;
            color: var(--text-muted);
            margin-top: 4px;
        }

        .register-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: 10px;
            padding: 28px;
            box-shadow: var(--shadow-sm);
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.8px;
            color: var(--text-muted);
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid var(--border);
            font-size: 14px;
            font-family: inherit;
        }

        select[multiple] {
            height: 120px;
        }

        small {
            font-size: 11px;
            color: var(--text-muted);
        }

        .alert {
            padding: 10px 14px;
            margin-bottom: 18px;
            border-radius: 6px;
            font-size: 13px;
            text-align: center;
            background: #fee2e2;
            border: 1px solid #fca5a5;
            color: var(--danger);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            background: var(--accent);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.95;
        }
    </style>
</head>

<body>

<div class="page-wrapper">

    <div class="page-header">
        <h1>User Registration</h1>
        <p>Create and assign roles to new POS users</p>
    </div>

    <div class="register-card">

        <form:form action="register" method="post" modelAttribute="userDto">

            <!-- ERROR MESSAGE -->
            <c:if test="${success == false}">
                <div class="alert">
                    ${message}
                </div>
            </c:if>

            <!-- NAME -->
            <div class="form-group">
                <label>Name</label>
                <form:input path="name" required="required"/>
            </div>

            <!-- EMAIL -->
            <div class="form-group">
                <label>Email</label>
                <form:input path="username"
                            type="email"
                            required="required"
                            pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\.com$"
                            title="Email must end with .com"/>
            </div>

            <!-- ROLES -->
            <div class="form-group">
                <label>Roles</label>
                <form:select path="roles" multiple="true" required="required">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
                <small>Select one or more roles</small>
            </div>

            <!-- PHONE -->
            <div class="form-group">
                <label>Phone Number</label>
                <form:input path="phoneNo"
                            maxlength="10"
                            minlength="10"
                            pattern="[0-9]{10}"
                            required="required"/>
            </div>

            <!-- PASSWORD -->
            <div class="form-group">
                <label>Password</label>
                <form:password path="password"
                               minlength="6"
                               required="required"/>
            </div>

            <!-- SUBMIT -->
            <input type="submit" value="Register User" class="btn-submit"/>

        </form:form>

    </div>

</div>

</body>
</html>