<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Warehouse</title>
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            font-family: "Segoe UI", Arial, sans-serif;
            padding-top: 40px;
        }

        .container {
            width: 650px;
            margin: auto;
            background: #ffffff;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 18px 35px rgba(75, 46, 43, 0.25);
        }

        h2 {
            text-align: center;
            color: #4B2E2B;
            margin-bottom: 30px;
            font-weight: 600;
        }

        form {
            display: flex;
            flex-direction: column;
            gap: 18px;
        }

        .form-group {
            display: grid;
            grid-template-columns: 180px 1fr;
            align-items: center;
            gap: 12px;
        }

        label {
            text-align: right;
            font-weight: 600;
            color: #4B2E2B;
        }

        input {
            width: 100%;
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #ccb7b2;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f5ede8;
        }

        input:focus {
            border-color: #4B2E2B;
            outline: none;
        }

        .btn-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 20px;
        }

        .btn {
            padding: 12px 22px;
            border-radius: 12px;
            border: none;
            font-weight: 600;
            background-color: #4B2E2B;
            color: #FFF8F0;
        }

        .btn:hover {
            background-color: #3a2421;
        }

        .back-btn {
            background-color: #e8dcd2;
            color: #4B2E2B;
        }

        .back-btn:hover {
            background-color: #d8c6b8;
        }

        .message {
            color: #8d3c36;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Update Warehouse</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form action="${pageContext.request.contextPath}/warehouse/update" method="post">
        <div class="form-group">
            <label>ID</label>
            <input type="text" name="id" value="${warehouse.id}" readonly />
        </div>
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier" value="${warehouse.identifier}" readonly />
        </div>
        <div class="form-group">
            <label>Region</label>
            <input type="text" name="region" value="${warehouse.region}" required />
        </div>
        <div class="form-group">
            <label>City</label>
            <input type="text" name="city" value="${warehouse.city}" required />
        </div>
        <div class="form-group">
            <label>State</label>
            <input type="text" name="state" value="${warehouse.state}" required />
        </div>
        <div class="form-group">
            <label>Country</label>
            <input type="text" name="country" value="${warehouse.country}" required />
        </div>
        <div class="form-group">
            <label>Capacity</label>
            <input type="text" name="capacity" value="${warehouse.capacity}" required />
        </div>
        <div class="form-group">
            <label>Contact Name</label>
            <input type="text" name="contactName" value="${warehouse.contactName}" required />
        </div>
        <div class="form-group">
            <label>Contact Number</label>
            <input type="tel"
                name="contactNumber"
                placeholder="Enter mobile number"
                pattern="[0-9]{10}"
                maxlength="10"
                inputmode="numeric"
                value="${warehouse.contactNumber}"
            required>
        </div>
        <div class="btn-group">
            <button type="submit" class="btn">Update Warehouse</button>
            <a href="${pageContext.request.contextPath}/warehouse/list"
               class="btn back-btn">Back</a>
        </div>
    </form>
</div>
</body>
</html>
