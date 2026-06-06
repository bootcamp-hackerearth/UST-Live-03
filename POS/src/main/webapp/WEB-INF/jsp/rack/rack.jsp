<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update Rack</title>

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

.container {
    width: 380px;
    padding: 35px;
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.06);
    backdrop-filter: blur(14px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 10px 35px rgba(0, 0, 0, 0.5);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 25px;
    font-weight: 600;
}

label {
    color: #bbb;
    font-size: 13px;
    display: block;
    margin-bottom: 5px;
}

input, select {
    width: 100%;
    padding: 11px;
    margin-bottom: 18px;
    border-radius: 10px;
    border: none;
    outline: none;
    background: rgba(255, 255, 255, 0.08);
    color: #fff;
}

select {
    appearance: none;
    cursor: pointer;
}

select option {
    background: #1a1b26;
    color: #fff;
}

.dropdown-wrapper {
    position: relative;
}

.dropdown-icon {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #00ffff;
    pointer-events: none;
    font-size: 14px;
}

input:focus, select:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 10px #00ffff;
}

input[readonly] {
    background: rgba(255,255,255,0.04);
    color: #888;
}

button {
    width: 100%;
    padding: 13px;
    border: none;
    border-radius: 10px;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 0 15px #00ffff;
}

.cancel-btn {
    display: block;
    text-align: center;
    margin-top: 12px;
    color: #aaa;
    text-decoration: none;
}

.cancel-btn:hover {
    color: #fff;
}

.message-box {
    margin-top: 12px;
    font-size: 13px;
    color: #ff8080;
    text-align: center;
}
</style>
</head>

<body>

<div class="container">
    <h2>Update Rack</h2>

    <form:form
        action="${pageContext.request.contextPath}/rack/update"
        method="post"
        modelAttribute="rackDto">

        <form:hidden path="id"/>

        <label>Rack Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Select Shelf</label>
        <div class="dropdown-wrapper">
            <form:select path="shelfs" required="true">
                <form:option value="">-- Select Shelf --</form:option>

                <c:forEach var="s" items="${shelfs}">
                    <form:option value="${s.identifier}">
                        ${s.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
            <span class="dropdown-icon">▼</span>
        </div>

        <label>Status</label>
        <div class="dropdown-wrapper">
            <form:select path="status" required="true">
                <form:option value="true" label="Active"/>
                <form:option value="false" label="Inactive"/>
            </form:select>
            <span class="dropdown-icon">▼</span>
        </div>

        <button type="submit">Update</button>

        <a href="${pageContext.request.contextPath}/rack/list"
           class="cancel-btn">Cancel</a>

        <c:if test="${not empty message}">
            <div class="message-box">${message}</div>
        </c:if>

    </form:form>
</div>

</body>
</html>