<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>

    <style>
        body {
            background: #f6f7f9;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            margin: 0;
        }

        .topbar {
            height: 56px;
            background: #020617;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }

        .top-title {
            color: #e5e7eb;
            font-weight: 600;
        }

        .logout-btn {
            background: #dc2626;
            color: white;
            border: none;
            padding: 7px 16px;
            border-radius: 6px;
            font-weight: 600;
        }

        .card {
            width: 420px;
            margin: 60px auto;
            background: #ffffff;
            padding: 26px;
            border-radius: 12px;
            position: relative;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            background: #eef0f3;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            color: #374151;
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
        }

        label {
            margin-top: 14px;
            display: block;
            font-weight: 600;
        }

        input,
        select {
            width: 100%;
            height: 38px;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
            font-size: 14px;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            box-sizing: border-box;
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

        .error {
            color: #dc2626;
            text-align: center;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="top-title">POS Application</div>
    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="card">

    <a href="${pageContext.request.contextPath}/category/list" class="back-btn">Back</a>

    <h2>Add Category</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/category/add"
               method="post"
               modelAttribute="categoryDto">

        <label>Category Name</label>
        <form:input path="identifier" required="true"/>

        <!--FIXED Super Category dropdown -->
        <label>Super Category</label>
        <form:select path="superCategory">
            <form:option value="">-- Select Super Category --</form:option>
            <c:forEach var="cat" items="${categories}">
                <form:option value="${cat.identifier}">
                    ${cat.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <button type="submit">Add Category</button>

    </form:form>

</div>

</body>
</html>