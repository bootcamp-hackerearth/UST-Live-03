<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        .card {
            width: 360px;
            margin: 40px auto;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            background: #ffffff;
            border: 1px solid teal;
            border-radius: 16px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 600;
            color: teal;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        label {
            display: block;
            margin-top: 12px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            height: 34px;
            padding: 8px 10px;
            margin-top: 4px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        select[multiple] {
            height: 90px;
            padding: 6px;
            border-radius: 12px;
        }

        button {
            margin-top: 16px;
            width: 100%;
            height: 34px;
            background: teal;
            color: white;
            border: none;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="app-title">POS Application</div>

    <a href="${pageContext.request.contextPath}/racks/list" class="back-btn">
        Back
    </a>

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