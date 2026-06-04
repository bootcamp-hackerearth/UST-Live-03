<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Warehouse</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 75%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
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

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-group {
            margin-top: 20px;
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
            margin-bottom: 12px;
            padding: 10px;
            border-radius: 6px;
            background-color: #fee2e2;
            color: #991b1b;
            text-align: center;
            font-size: 13px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Warehouse</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/warehouse/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${warehouse.id}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${warehouse.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Region</label>
            <input
                type="text"
                name="region"
                value="${warehouse.region}"
                required
            />
        </div>

        <div class="form-group">
            <label>City</label>
            <input
                type="text"
                name="city"
                value="${warehouse.city}"
                required
            />
        </div>

        <div class="form-group">
            <label>State</label>
            <input
                type="text"
                name="state"
                value="${warehouse.state}"
                required
            />
        </div>

        <div class="form-group">
            <label>Country</label>
            <input
                type="text"
                name="country"
                value="${warehouse.country}"
                required
            />
        </div>

        <div class="form-group">
            <label>Capacity</label>
            <input
                type="text"
                name="capacity"
                value="${warehouse.capacity}"
                required
            />
        </div>

        <div class="form-group">
            <label>Contact Name</label>
            <input
                type="text"
                name="contactName"
                value="${warehouse.contactName}"
                required
            />
        </div>

        <div class="form-group">
            <label>Contact Number</label>
            <input
                type="tel"
                name="contactNumber"
                value="${warehouse.contactNumber}"
                placeholder="Enter mobile number"
                pattern="[0-9]{10}"
                maxlength="10"
                inputmode="numeric"
                required
            />
        </div>

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Warehouse
            </button>

            <a
                href="${pageContext.request.contextPath}/warehouse/list"
                class="btn back-btn"
            >
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>