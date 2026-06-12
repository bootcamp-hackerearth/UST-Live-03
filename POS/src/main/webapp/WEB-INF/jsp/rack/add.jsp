<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <style>
        body {
            font-family: Arial;
            background: #f4f7f6;
        }

        .card {
            width: 400px;
            margin: 100px auto;
            padding: 30px;
            background: white;
            border-radius: 8px;
        }

        label {
            display: block;
            margin-top: 15px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
        }

        select[multiple] {
            height: 120px;
        }

        .btn {
            margin-top: 20px;
            width: 100%;
            padding: 10px;
            background: #28a745;
            color: white;
            border: none;
        }

        .error {
            color: red;
            margin-bottom: 10px;
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

        small {
            font-size: 11px;
            color: #666;
        }
    </style>
</head>

<body>

<a href="/rack/list" class="back-btn">← Back</a>
<div class="card">

    <h2>Add Rack</h2>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <form:form action="/rack/add" method="post" modelAttribute="racks">

        <label>Identifier</label>
        <form:input path="identifier" required="true"/>

        <!-- SHELF MULTI SELECT -->
        <label>Shelves</label>
        <form:select path="shelf" multiple="true">
            <c:forEach var="shelf" items="${shelf}">
                <form:option value="${shelf.identifier}">
                    ${shelf.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <small>Hold Ctrl (Windows) or Cmd (Mac) to select multiple</small>

        <!-- STATUS DROPDOWN -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <button type="submit" class="btn">Save</button>

    </form:form>

</div>

</body>
</html>