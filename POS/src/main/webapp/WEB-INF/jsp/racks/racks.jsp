<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Rack</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

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

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        select[multiple] {
            height: 130px;
        }

        .btn-submit {
            margin-top: 12px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-cancel {
            margin-top: 10px;
            display: block;
            text-align: center;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border-radius: 10px;
            text-decoration: none;
            font-size: 14px;
        }

        .error-message {
            margin-bottom: 16px;
            padding: 10px;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
            text-align: center;
            font-size: 13px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="card-container">

    <h2>Edit Rack</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <!-- ✅ Rack Edit Form -->
    <form:form method="post"
               action="${pageContext.request.contextPath}/racks/update"
               modelAttribute="racksDto">

        <!-- Rack Name (readonly, identifier should not change) -->
        <div class="form-group">
            <label>Rack Name</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <!-- Shelf Multi Select -->
        <div class="form-group">
            <label>Shelf Name</label>
            <form:select path="shelfs" multiple="true">
                <c:forEach var="s" items="${shelf}">
                    <form:option value="${s.identifier}">
                        ${s.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
            <small style="font-size:11px;color:#666;">
                Hold Ctrl (Windows) / Cmd (Mac) to select multiple shelves
            </small>
        </div>

        <input type="submit" value="Update Rack" class="btn-submit"/>
        <a href="${pageContext.request.contextPath}/racks/list" class="btn-cancel">
            Cancel
        </a>

    </form:form>

</div>

</body>
</html>
``