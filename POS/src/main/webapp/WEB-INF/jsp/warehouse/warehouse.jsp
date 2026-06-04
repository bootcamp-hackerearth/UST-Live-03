<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Warehouse</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Poppins', Arial, sans-serif;
            background-color: #f4f6fb;
        }

        .card {
            width: 450px;
            background-color: #ffffff;
            padding: 32px;
            border-radius: 12px;
            box-shadow: 0 12px 30px rgba(0,0,0,0.1);
        }

        h3 {
            text-align: center;
            color: #4e73df;
            margin-bottom: 24px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 14px;
            font-weight: 600;
            color: #444;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            background-color: #f9fafc;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background-color: #ffffff;
        }
        select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ccd3e0;
            background: #f9fafc;
            font-size: 14px;
        }

        select:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background: #ffffff;
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #4e73df;
            color: #ffffff;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 8px;
            box-shadow: 0 6px 14px rgba(78,115,223,0.35);
        }

        button:hover {
            background-color: #224abe;
        }

        .message {
            color: red;
            text-align: center;
            margin-bottom: 12px;
            font-size: 14px;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 14px;
            color: #4e73df;
            font-weight: 600;
            text-decoration: none;
        }

        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="card">
    <h3>Update Warehouse</h3>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form:form method="post"
               action="${pageContext.request.contextPath}/warehouse/update"
               modelAttribute="warehouseDto">
        <div class="form-group">
            <label>Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>
        <div class="form-group">
            <label>Location</label>
            <form:input path="location" required="required"/>
        </div>
        <div class="form-group">
            <label>Contact Person</label>
            <form:input path="contactPerson" required="required"/>
        </div>
        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNumber"
                        type="tel"
                        required="required"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        placeholder="Enter 10-digit phone number"
                        oninput="this.value = this.value.replace(/[^0-9]/g, '').slice(0,10);"/>
        </div>
                        <div class="form-group">
                            <label>Status</label>
                            <form:select path="status" cssClass="form-control">
                                <form:option value="true">Active</form:option>
                                <form:option value="false">Inactive</form:option>
                            </form:select>
                        </div>
        <button type="submit">Update Warehouse</button>
        <a href="${pageContext.request.contextPath}/warehouse/list" class="back-link">
            Back to Warehouse List
        </a>
    </form:form>
</div>
</body>
</html>