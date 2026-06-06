<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Category</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card-container {
    width: 420px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h2 {
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

input[readonly] {
    background: rgba(255,255,255,0.05);
    color: #aaa;
}

.btn {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    margin-top: 10px;
}

.btn-submit {
    background: #00ffff;
    color: #000;
}

.btn-submit:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-cancel {
    display: block;
    text-align: center;
    background: #666;
    color: #fff;
    text-decoration: none;
}

.btn-cancel:hover {
    background: #555;
}

.alert-danger {
    text-align: center;
    padding: 10px;
    margin-bottom: 12px;
    color: #ff8080;
}

.footer {
    text-align: center;
    margin-top: 10px;
    color: #aaa;
    font-size: 12px;
}
</style>
</head>

<body>

<div class="card-container">

    <h2>Edit Category</h2>

    <c:if test="${empty categoryDto}">
        <div class="alert-danger">
            Category not found
        </div>
    </c:if>

    <c:if test="${not empty categoryDto}">
        <form:form action="/category/update"
                   method="post"
                   modelAttribute="categoryDto">

            <form:hidden path="id"/>

            <label>Category</label>
            <form:input path="identifier" readonly="true"/>

            <label>Super Category</label>
            <form:select path="superCategory">
                <form:option value="">-- Select Super Category --</form:option>
                <form:options items="${categories}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>

            <button type="submit" class="btn btn-submit">
                Update Category
            </button>

            <a href="/category/list" class="btn btn-cancel">
                Cancel
            </a>

        </form:form>
    </c:if>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>