<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Rack</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
                  rel="stylesheet">
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 55%;
            margin: auto;
            background: #ffffff;
            padding: 32px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .checkbox-group {
            margin-top: 8px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
        }

        .checkbox-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            cursor: pointer;
        }

        .checkbox-item label {
            margin: 0;
            font-size: 14px;
            cursor: pointer;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-group {
            margin-top: 22px;
            text-align: center;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #0f172a;
        }

        .back-btn {
            background-color: #e2e8f0;
            color: #1e293b;
            margin-left: 12px;
        }

        .back-btn:hover {
            background-color: #cbd5e1;
        }

        .message {
            color: #dc2626;
            text-align: center;
            margin-bottom: 14px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Rack</h2>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/rack/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${rack.id}" readonly />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${rack.identifier}" readonly/>
        </div>

        <div class="form-group">
            <label>Rack Name</label>
            <input type="text" name="name" value="${rack.name}" />
        </div>

        <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

        <label>Assigned Shelfs</label>

        <c:forEach items="${shelfs}" var="s">
            <div class="checkbox-item">

                <input type="checkbox"
                       id="shelf_${s.identifier}"
                       name="shelfs"
                       value="${s.identifier}"
                       <c:if test="${fn:contains(rack.shelfs, s.identifier)}">checked</c:if> />

                <label for="shelf_${s.identifier}">
                    ${s.name}
                </label>

            </div>
        </c:forEach>

               <input type="hidden" name="status" value="${rack.status}" />

        <div class="btn-group">
            <button type="submit" class="btn">Update Rack</button>
            <a href="${pageContext.request.contextPath}/rack/list"
               class="btn back-btn">Back</a>
        </div>

    </form>
</div>

</body>
</html>