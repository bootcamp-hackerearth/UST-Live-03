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
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .card {
            width: 450px;
            margin: 80px auto;
            background: #fff;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        select[multiple] {
            height: 130px;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/rack/list" class="back-btn">
    ← Back
</a>

<div class="card">
    <h2>Add Rack</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">
            ${message}
        </div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/rack/add"
               method="post"
               modelAttribute="rack">

        <label>Rack Name</label>
        <form:input path="identifier" required="true"/>
        <form:errors path="identifier" cssClass="error-msg"/>

        <label>Select Shelves</label>
        <form:select path="shelfs" multiple="true">
            <c:forEach var="shelf" items="${shelves}">
                <form:option value="${shelf.identifier}">
                    ${shelf.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">Deactive</form:option>
        </form:select>

        <input type="submit" value="Add Rack" class="btn-submit"/>

    </form:form>
</div>

</body>
</html>