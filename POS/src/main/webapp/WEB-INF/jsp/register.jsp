<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Registration</title>

<style>
    * { box-sizing: border-box; margin: 0; padding: 0; }

    body {
        font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        background: radial-gradient(circle at top left, #f8fafc, #e2e8f0);
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        color: #334155;
        padding: 20px;
    }

    .edit-card {
        position: relative;
        width: 100%;
        max-width: 460px;
        background: rgba(255, 255, 255, 0.95);
        padding: 40px;
        border-radius: 20px;
        box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1),
                    0 10px 10px -5px rgba(0,0,0,0.04);
        border: 1px solid rgba(255,255,255,0.3);
        backdrop-filter: blur(10px);
    }

    h2 {
        text-align: center;
        margin-bottom: 25px;
        font-weight: 700;
        color: #1e293b;
    }

    /* Back icon */
    .back-icon {
        position: absolute;
        top: 16px;
        left: 16px;
        width: 36px;
        height: 36px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #eef2ff;
        color: #3730a3;
        text-decoration: none;
        font-size: 18px;
        transition: 0.2s;
    }

    .back-icon:hover {
        background: #3730a3;
        color: #fff;
        transform: translateX(-3px);
    }

    label {
        font-size: 13px;
        font-weight: 600;
        color: #64748b;
        margin-bottom: 6px;
        display: block;
    }

    input, select {
        width: 100%;
        padding: 12px 14px;
        margin-bottom: 18px;
        border-radius: 10px;
        border: 1px solid #e2e8f0;
        background: #fcfcfd;
        font-size: 14px;
    }

    input:focus, select:focus {
        outline: none;
        border-color: #6366f1;
        box-shadow: 0 0 0 4px rgba(99,102,241,0.1);
        background: #fff;
    }

    select[multiple] {
        height: 120px;
    }

    .error {
        margin-bottom: 15px;
        text-align: center;
        color: #e11d48;
        background: #fff1f2;
        padding: 10px;
        border-radius: 8px;
        font-size: 13px;
    }

    button {
        width: 100%;
        padding: 14px;
        border-radius: 12px;
        border: none;
        background: #1e293b;
        color: #fff;
        font-weight: 600;
        font-size: 15px;
        cursor: pointer;
        transition: 0.2s ease;
    }

    button:hover {
        background: #0f172a;
        transform: translateY(-1px);
        box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
    }
</style>
</head>

<body>

<div class="edit-card">

    <a href="${pageContext.request.contextPath}/user/list" class="back-icon">←</a>

    <h2>User Registration</h2>

    <!-- Error from controller -->
    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <label>Name</label>
        <form:input path="name" required="true"/>

        <label>Email</label>
        <form:input path="username"
                    type="email"
                    required="true"
                    pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                    title="Enter a valid Gmail (example@gmail.com)"/>

        <label>Roles</label>
        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Mobile Number</label>
        <form:input path="phoneNo"
                    type="tel"
                    maxlength="10"
                    required="true"
                    inputmode="numeric"
                    pattern="[0-9]{10}"
                    title="Enter a valid 10-digit mobile number"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

        <label>Password</label>
        <form:password path="password"
                       required="true"
                       pattern=".{6,}"
                       title="minimum contain 6 character"/>

        <button type="submit">Register</button>

    </form:form>

</div>

</body>
</html>