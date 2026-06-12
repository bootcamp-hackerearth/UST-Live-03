<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Warehouse</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: system-ui, sans-serif;
    background-color: #f1f5f9;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
}

.container {
    width: 100%;
    max-width: 420px;
    background: #ffffff;
    padding: 40px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 20px 25px rgba(0,0,0,0.1);
}

h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 24px;
}

.input-group {
    margin-bottom: 18px;
}

label {
    font-size: 13px;
    font-weight: 600;
    display: block;
    margin-bottom: 6px;
    color: #64748b;
}

input, textarea {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    font-family: inherit;
}

textarea {
    resize: none;
    height: 80px;
}

button {
    width: 100%;
    padding: 14px;
    margin-top: 10px;
    background: #0f172a;
    color: white;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
}

button:hover {
    background: #020617;
}

.error {
    margin-top: 16px;
    padding: 10px;
    background: #fff1f2;
    color: #be123c;
    text-align: center;
    border-radius: 6px;
}

.link-btn {
    display: block;
    margin-top: 25px;
    text-align: center;
    color: #64748b;
    text-decoration: none;
}

.link-btn:hover {
    color: #4f46e5;
}
</style>
</head>

<body>

<div class="container">

    <h2>Edit Warehouse</h2>

    <form:form action="${pageContext.request.contextPath}/warehouse/update"
               method="post"
               modelAttribute="warehouse">

        <form:hidden path="id"/>

        <div class="input-group">
            <label>Warehouse Identifier</label>
            <form:input path="identifier" readonly="true"/>
        </div>

        <div class="input-group">
            <label>Region</label>
            <form:input path="region" required="true"/>
        </div>

                <div class="input-group">
                    <label>Country</label>
                    <form:input path="country" required="true"/>
                </div>

        <div class="input-group">
            <label>Address</label>
            <form:textarea path="address" required="true"/>
        </div>

        <div class="mb-3">
                   <label>Phone Number</label>
                     <form:input path="phoneNo"
                      pattern="[0-9]{10}"
                      maxlength="10"
                      inputmode="numeric"
                      title="Enter a valid 10-digit phone number"
                      oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                      required="true"
                      cssClass="input-control"/>
                  <form:errors path="phoneNo" cssClass="input-control" type="tel"/>
          </div>

        <button type="submit">Save Changes</button>

        <a href="${pageContext.request.contextPath}/warehouse/list"
           class="link-btn">
            ← Back to Warehouse List
        </a>

    </form:form>

    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

</div>

</body>
</html>