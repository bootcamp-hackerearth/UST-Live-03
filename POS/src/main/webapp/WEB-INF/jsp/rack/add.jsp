<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Rack</title>

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
    width: 420px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h3 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

label {
    color: #ccc;
    font-size: 14px;
}

input, select {
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

select option {
    background: #1a1b26;
    color: #fff;
}

input:focus, select:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn {
    padding: 10px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    border: none;
    cursor: pointer;
}

.btn-primary {
    background: #00ffff;
    color: #000;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-secondary {
    background: #666;
    color: #fff;
}

.btn-secondary:hover {
    background: #555;
}

.btn-group {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 10px;
    color: #ff8080;
}
</style>
</head>

<body>

<div class="card">

    <h3>Add Rack</h3>

    <c:if test="${not empty message}">
        <div class="alert">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/rack/add"
               modelAttribute="rackDto">

        <label>Rack Name</label>
        <form:input
            path="identifier"
            placeholder="Enter rack name"
            required="true"
            minlength="2"
            maxlength="50"
            pattern="[A-Za-z0-9 ]+"
            title="Enter valid rack name"
        />

        <label>Select Shelf</label>
        <form:select
            path="shelfs"
            required="true"
        >
            <form:option value="">-- Select Shelf --</form:option>

            <c:forEach var="s" items="${shelfs}">
                <form:option value="${s.identifier}">
                    ${s.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/rack/list"
               class="btn btn-secondary">
                Cancel
            </a>

            <button type="submit" class="btn btn-primary">
                Save
            </button>
        </div>

    </form:form>

</div>

</body>
</html>