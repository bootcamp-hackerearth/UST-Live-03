<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Model Product</title>

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
    width: 360px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

.form-group {
    margin-bottom: 15px;
}

label {
    color: #ccc;
    font-size: 13px;
}

input, select {
    width: 100%;
    padding: 10px;
    margin-top: 6px;

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

button {
    width: 100%;
    padding: 12px;
    margin-top: 10px;

    border: none;
    border-radius: 8px;

    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
    transform: scale(1.03);
}

.message {
    text-align: center;
    color: #ff8080;
    margin-bottom: 10px;
    font-size: 13px;
}
</style>
</head>

<body>

<div class="container">

    <h2>Add Model Product</h2>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <form:form method="post"
               action="/modelproduct/add"
               modelAttribute="modelProductDto">

        <div class="form-group">
            <label>Model Name</label>
            <form:input path="identifier" placeholder="Enter model name"/>
        </div>

        <div class="form-group">
            <label>Status</label>
            <form:select path="status">
                <form:option value="">-- Select Status --</form:option>
                <form:option value="true">Active</form:option>
                <form:option value="false">Inactive</form:option>
            </form:select>
        </div>

        <button type="submit">Save</button>

    </form:form>

</div>

</body>
</html>