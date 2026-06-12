<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .update-card {
            width: 400px;
            background: #fff;
            padding: 24px 28px;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.25);
        }

        h3 {
            text-align: center;
            color: black;
            margin-bottom: 25px;
            font-weight: 600;
        }

        label {
            font-weight: 600;
            font-size: 14px;
            color: #333;
        }

        .form-control,
        select {
            border-radius: 8px;
            padding: 10px 12px;
        }

        select[multiple] {
            height: 120px;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            background: #4b6cb7;
            border: none;
            color: white;
            font-weight: 600;
            border-radius: 8px;
        }

        .btn-update:hover {
            background: #182848;
        }

        small {
            font-size: 11px;
            color: #666;
        }
    </style>
</head>

<body>
<div class="update-card">
    <h3>Update Node</h3>

    <form:form action="/node/update" method="post" modelAttribute="nodeDto">

        <form:input type="hidden" path="id"/>

        <div class="mb-3">
            <label>Name</label>
            <form:input path="identifier" cssClass="form-control" required="true" pattern="^[A-Za-z)-9_-]+$" readonly="true"/>
        </div>

         <div class="mb-3">
            <label>Path</label>
            <form:input path="path" cssClass="form-control" required="true" pattern="^/.*" title="Path must start with /"/>
        </div>


        <div class="mb-3">
            <label>Roles</label>

            <div class="mb-1 text-muted">
                Current:


<c:forEach var="r" items="${nodeDto.roles}">
    <span class="badge bg-secondary me-1">${r}</span>
</c:forEach>

            </div>


<form:select path="roles" multiple="true" cssClass="form-control">
    <form:options items="${roles}"
                  itemValue="identifier"
                  itemLabel="identifier"/>
</form:select>

            <small>
                Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple
            </small>
        </div>

        <button type="submit" class="btn-update">Update User</button>

    </form:form>

    <div class="text-center mt-3">
        <a href="/node/list">← Back to Node List</a>
    </div>

 <c:if test="${not empty message}">
        <div style="
            background:#f8d7da;
            color:#721c24;
            padding:10px;
            margin-bottom:15px;
            border-radius:4px;
            text-align:center;">
            ${message}
        </div>
    </c:if>

</div>

</body>
</html>