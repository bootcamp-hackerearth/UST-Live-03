<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f4f7f6;
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

        .back-btn:hover {
            background: #5a6268;
        }

        .card {
            width: 460px;
            margin: 80px auto;
            background: white;
            padding: 30px 35px;
            border-radius: 10px;
            border: 1px solid #ddd;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
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
            font-size: 13px;
            box-sizing: border-box;
        }

        select[multiple] {
            height: 110px;
        }

        small {
            font-size: 11px;
            color: #999;
            margin-top: 3px;
            display: block;
        }

        .current-roles {
            margin-top: 6px;
            margin-bottom: 4px;
            font-size: 12px;
            color: #666;
        }

        .badge {
            display: inline-block;
            background: #e9ecef;
            color: #495057;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
            text-transform: uppercase;
            border: 1px solid #ddd;
            margin: 2px;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }

        .field-error {
            font-size: 12px;
            color: #b91c1c;
            margin-top: 3px;
            display: block;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
        }

        .btn-submit:hover {
            background: #0056b3;
        }
    </style>
</head>

<body>

<a href="/user/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Update User</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="/user/update" method="post" modelAttribute="user">

        <form:input type="hidden" path="id"/>

        <!-- NAME -->
        <label>Name</label>
        <form:input path="name" required="true"/>
        <form:errors path="name" cssClass="field-error"/>

        <!-- EMAIL -->
        <label>Email</label>
        <form:input path="username" type="email" required="true"/>
        <form:errors path="username" cssClass="field-error"/>

        <!-- PHONE -->
        <label>Phone Number</label>
        <form:input path="phoneNo"
                    pattern="[0-9]{10}"
                    title="Enter a valid 10 digit phone number"
                    required="true"/>
        <form:errors path="phoneNo" cssClass="field-error"/>

        <!-- ROLES -->
        <label>Roles</label>
        <div class="current-roles">
            Current:
            <c:forEach var="r" items="${user.roles}">
                <span class="badge">${r}</span>
            </c:forEach>
        </div>
        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>
        <small>Hold Ctrl (Windows) or Cmd (Mac) to select multiple</small>

        <!-- SUBMIT -->
        <input type="submit" value="Update User" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>