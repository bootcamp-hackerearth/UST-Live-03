<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Warehouse</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.card {
    width: 450px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h4 {
    text-align: center;
    margin-bottom: 20px;
    color: #fff;
}

.form-label {
    color: #ccc;
    font-size: 14px;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;

    border-radius: 8px;
    border: none;
    outline: none;

    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn-primary {
    background: #00ffff;
    color: #000;
    border: none;
    border-radius: 8px;
    padding: 10px 16px;
    font-weight: bold;
    cursor: pointer;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-outline-secondary {
    border-radius: 8px;
    padding: 10px 16px;
    text-decoration: none;
    color: #ccc;
    border: 1px solid #ccc;
}

.btn-outline-secondary:hover {
    color: #fff;
    border-color: #fff;
}

.message {
    text-align: center;
    margin-bottom: 10px;
    color: #ff8080;
}

.alert {
    text-align: center;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    color: #ff8080;
}
</style>
</head>

<body>

<div class="card">

    <h4>Edit Warehouse</h4>

    <div class="message">${message}</div>

    <c:if test="${empty warehouse}">
        <div class="alert">
            Warehouse not found
        </div>
    </c:if>

    <c:if test="${not empty warehouse}">
        <form:form action="/warehouse/update"
                   method="post"
                   modelAttribute="warehouse">

            <form:hidden path="id" value="${warehouse.id}"/>

            <label class="form-label">Warehouse Name</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        readonly="true"
                        required="true"/>

            <label class="form-label">Country</label>
            <form:input path="country"
                        cssClass="form-control"
                        required="true"/>

            <label class="form-label">Pincode</label>
            <form:input path="pincode"
                        cssClass="form-control"
                        required="true"/>

            <label class="form-label">Address</label>
            <form:input path="address"
                        cssClass="form-control"
                        required="true"/>

            <div style="display:flex; justify-content: space-between;">
                <a href="/warehouse/list" class="btn-outline-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn-primary">
                    Update
                </button>
            </div>

        </form:form>
    </c:if>

</div>

</body>
</html>