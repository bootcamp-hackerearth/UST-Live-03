<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Node</title>

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    body {
        font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        background-color: #f8fafc;
        background-image: radial-gradient(#e2e8f0 0.5px, transparent 0.5px);
        background-size: 20px 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        color: #334155;
    }

    .container {
        width: 100%;
        max-width: 460px;
        background: #ffffff;
        padding: 40px;
        border-radius: 16px;
        border: 1px solid #e2e8f0;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
                    0 10px 10px -5px rgba(0, 0, 0, 0.04);
    }

    h2 {
        text-align: center;
        margin-bottom: 30px;
        font-weight: 700;
        font-size: 24px;
        color: #0f172a;
    }

    .input-group {
        margin-bottom: 20px;
    }

    label {
        font-size: 14px;
        font-weight: 600;
        margin-bottom: 6px;
        display: block;
        color: #64748b;
    }

    input[type="text"],
    select {
        width: 100%;
        padding: 12px 14px;
        border-radius: 10px;
        border: 1px solid #cbd5e1;
        font-size: 15px;
        background-color: #ffffff;
    }

    input:focus,
    select:focus {
        outline: none;
        border-color: #6366f1;
        box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
    }

    select[multiple] {
        min-height: 130px;
    }

    button {
        width: 100%;
        padding: 14px;
        border-radius: 12px;
        border: none;
        background: #1e293b;
        color: white;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        margin-top: 10px;
    }

    button:hover {
        background: #0f172a;
    }

    .link-btn {
        display: block;
        text-align: center;
        margin-top: 25px;
        color: #64748b;
        text-decoration: none;
        font-size: 14px;
        font-weight: 500;
    }

    .link-btn:hover {
        color: #4f46e5;
    }

    .error-message {
        margin-top: 20px;
        padding: 12px;
        text-align: center;
        border-radius: 10px;
        background: #fff1f2;
        border: 1px solid #fecdd3;
        color: #be123c;
        font-size: 14px;
        font-weight: 500;
    }
</style>
</head>

<body>

<div class="container">

    <h2>Edit Node</h2>

    <form:form action="${pageContext.request.contextPath}/node/update"
               method="post"
               modelAttribute="node">

        <form:hidden path="id"/>

        <div class="input-group">
            <label>Node Name</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="input-group">
            <label>Path</label>
            <form:input path="path" placeholder="/example/path" required="true"/>
        </div>

        <div class="input-group">
            <label>Assign Roles</label>
            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/node/list" class="link-btn">
            ← Back to Node List
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>