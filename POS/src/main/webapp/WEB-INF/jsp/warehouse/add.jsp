<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            width: 520px;
            padding: 36px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            background-color: #1e293b;
            color: #ffffff;
        }

        .btn-submit:hover {
            background-color: #0f172a;
        }

        .back-center {
            margin-top: 20px;
            text-align: center;
        }

        .back-center a {
            text-decoration: none;
            color: #1e293b;
            font-size: 14px;
            font-weight: 600;
            padding: 8px 18px;
            border-radius: 6px;
            background-color: #e2e8f0;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #e0f2fe;
            color: #0369a1;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/warehouse/add">

        <input
            type="hidden"
            name="id"
            value="${warehouseDto.id}"
        />

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${warehouseDto.identifier}"
                placeholder="Enter warehouse identifier"
                required
            />
        </div>

        <div class="form-group">
            <label>Region</label>
            <input
                type="text"
                name="region"
                value="${warehouseDto.region}"
                placeholder="Enter region"
                required
            />
        </div>

        <div class="form-group">
            <label>City</label>
            <input
                type="text"
                name="city"
                value="${warehouseDto.city}"
                placeholder="Enter city"
                required
            />
        </div>

        <div class="form-group">
            <label>State</label>
            <input
                type="text"
                name="state"
                value="${warehouseDto.state}"
                placeholder="Enter state"
                required
            />
        </div>

        <div class="form-group">
            <label>Country</label>
            <input
                type="text"
                name="country"
                value="${warehouseDto.country}"
                placeholder="Enter country"
                required
            />
        </div>

        <div class="form-group">
            <label>Capacity</label>
            <input
                type="text"
                name="capacity"
                value="${warehouseDto.capacity}"
                placeholder="e.g. 10,000 sq.ft / 500 units"
                required
            />
        </div>

        <div class="form-group">
            <label>Contact Name</label>
            <input
                type="text"
                name="contactName"
                value="${warehouseDto.contactName}"
                placeholder="Enter contact person name"
                required
            />
        </div>

        <div class="form-group">
            <label>Contact Number</label>
            <input
                type="tel"
                name="contactNumber"
                value="${warehouseDto.contactNumber}"
                placeholder="Enter mobile number"
                maxlength="10"
                pattern="^[0-9]{10}$"
                oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                required
            />
        </div>

        <button type="submit" class="btn-submit">
            Save Warehouse
        </button>

        <div class="back-center">
            <a href="${pageContext.request.contextPath}/warehouse/list">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>