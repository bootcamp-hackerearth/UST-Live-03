<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update User</title>

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
        transition: 0.2s ease;
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
        margin-bottom: 16px;
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

    .badge {
        background: #e5e7eb;
        color: #374151;
        font-size: 12px;
        padding: 4px 8px;
        border-radius: 8px;
        margin-right: 5px;
    }

    .error {
        margin-top: 5px;
        margin-bottom: 10px;
        text-align: left;
        color: #e11d48;
        font-size: 12px;
    }

    button {
        width: 100%;
        padding: 14px;
        margin-top: 10px;
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

    .footer-link {
        text-align: center;
        margin-top: 18px;
    }

    .footer-link a {
        color: #64748b;
        text-decoration: none;
        font-size: 14px;
    }

    .footer-link a:hover {
        color: #4f46e5;
    }
</style>
</head>

<body>

<div class="edit-card">

    <a href="/user/list" class="back-icon">←</a>

    <h2>Update User</h2>

    <form:form action="/user/update" method="post" modelAttribute="user">

        <form:hidden path="id"/>

        <label>Name</label>
        <form:input path="name" required="true"/>
        <form:errors path="name" cssClass="error"/>

        <label>Email</label>
        <form:input path="username" type="email" required="true"/>
        <form:errors path="username" cssClass="error"/>

        <label>Phone Number</label>
        <form:input path="phoneNo"
                    pattern="[0-9]{10}"
                    maxlength="10"
                    inputmode="numeric"
                    title="Enter a valid 10-digit phone number"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                    required="true"/>
        <form:errors path="phoneNo" cssClass="error"/>

        <label>Roles</label>
        <div style="margin-bottom:8px;">
            Current:
            <c:forEach var="r" items="${user.roles}">
                <span class="badge">${r}</span>
            </c:forEach>
        </div>

        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <button type="submit">Update User</button>

    </form:form>

    <c:if test="${not empty message}">
        <p class="error" style="text-align:center;">${message}</p>
    </c:if>

    <div class="footer-link">
        <a href="/user/list">← Back to User List</a>
    </div>

</div>

</body>
</html>