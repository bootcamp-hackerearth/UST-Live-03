<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>
    <style>
        body {
            min-height: 100vh;
            background-color: #FFF8F0;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Arial, sans-serif;
        }

        .container {
            background: #ffffff;
            width: 600px;
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
            grid-template-columns: 180px 1fr; /* label | input */
            align-items: center;
            gap: 10px;
        }

        label {
            color: #4B2E2B;
            font-weight: 600;
            text-align: right;
        }

        input {
            width: 100%;
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #ccb7b2;
            font-size: 14px;
        }

        input:focus {
            border-color: #4B2E2B;
            outline: none;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            margin-top: 20px;
            border-radius: 12px;
            border: none;
            background-color: #4B2E2B;
            color: #FFF8F0;
            font-weight: 600;
        }

        .btn-submit:hover {
            background-color: #3a2421;
        }

        .back-center {
            text-align: center;
            margin-top: 20px;
        }

        .back-center a {
            background-color: #e8dcd2;
            color: #4B2E2B;
            padding: 10px 20px;
            border-radius: 12px;
            text-decoration: none;
        }

        .back-center a:hover {
            background-color: #d8c6b8;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Add Warehouse</h2>
    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/warehouse/add">
        <input type="hidden" name="id" value="${warehouseDto.id}" />
        <div class="form-group">
            <label>Identifier</label>
            <input type="text" name="identifier"
                value="${warehouseDto.identifier}"
                placeholder="Enter warehouse identifier"
                required/>
        </div>
        <div class="form-group">
            <label>Region</label>
            <input type="text" name="region"
                value="${warehouseDto.region}"
                placeholder="Enter region"
                required/>
        </div>
        <div class="form-group">
            <label>City</label>
            <input type="text" name="city"
                value="${warehouseDto.city}"
                placeholder="Enter city"
                required/>
        </div>
        <div class="form-group">
            <label>State</label>
            <input type="text" name="state"
                value="${warehouseDto.state}"
                placeholder="Enter state"
                required/>
        </div>
        <div class="form-group">
            <label>Country</label>
            <input type="text" name="country"
                value="${warehouseDto.country}"
                placeholder="Enter country"
                required/>
        </div>
        <div class="form-group">
            <label>Capacity</label>
            <input type="text" name="capacity"
                value="${warehouseDto.capacity}"
                placeholder="e.g. 10,000 sq.ft / 500 units"
                required/>
        </div>
        <div class="form-group">
            <label>Contact Name</label>
            <input type="text" name="contactName"
                value="${warehouseDto.contactName}"
                placeholder="Enter contact person name"
                required/>
        </div>
        <div class="form-group">
            <label>Contact Number</label>
            <input type="tel" name="contactNumber"
                class="form-control"
                placeholder="Enter mobile number"
                maxlength="10"
                pattern="^[0-9]{10}$"
                oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                required/>
        </div>
        <button type="submit" class="btn-submit">Save Warehouse</button>
        <div class="back-center">
            <a href="${pageContext.request.contextPath}/warehouse/list">Back</a>
        </div>
    </form>
</div>
</body>
</html>