<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <style>
        body {
            background-color: #f6f7f9;
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

          h2 {
    text-align: center;
    margin-top: 10px;
            }

        .topbar {
            height: 56px;
            background-color: #020617;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .top-title { color: #e5e7eb; font-weight: 600; }

        .logout-btn {
            background: #dc2626;
            border: none;
            color: white;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
        }

        .card {
            width: 420px;
            margin: 60px auto;
            background: white;
            padding: 26px;
            border-radius: 12px;
            position: relative;
            box-shadow: 0 8px 20px rgba(0,0,0,.08);
        }

        .back-btn {
             position: absolute;
                        top: 18px;
                        left: 18px;
                        padding: 6px 14px;
                        background: #eef0f3;
                        text-decoration: none;
                        border-radius: 6px;
                        font-size: 13px;
                        font-weight: 600;
                        color: #374151;
        }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            width: 100%;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
        }

        select[multiple] {
            height: 110px;
        }

        button {
            margin-top: 22px;
            width: 100%;
            background: #2563eb;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 20px;
            font-weight: 600;
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">
    <a href="${pageContext.request.contextPath}/racks/list" class="back-btn">Back</a>

    <h2>Add Rack</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form modelAttribute="racksDto"
               action="${pageContext.request.contextPath}/racks/add"
               method="post">

        <label>Rack Name</label>
        <form:input path="identifier" required="true"/>

        <label>Shelfs</label>
        <form:select path="shelfs" multiple="true">
            <c:forEach var="s" items="${shelves}">
                <form:option value="${s.identifier}">
                    ${s.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <button type="submit">Add Rack</button>
    </form:form>
</div>

</body>
</html>