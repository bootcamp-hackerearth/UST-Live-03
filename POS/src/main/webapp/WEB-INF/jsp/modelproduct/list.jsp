<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Model Product List</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

<style>
*{
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body{
    min-height:100vh;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
    padding:40px 20px;
}

.container{
    max-width:1200px;
    margin:auto;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.15);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h2{
    color:#fff;
    font-size:28px;
    font-weight:600;
    margin-bottom:25px;
}

.table-wrapper{
    overflow-x:auto;
}

table{
    width:100%;
    border-collapse:collapse;
}

thead{
    background:#2f4f59;
}

th{
    color:#00ffff;
    padding:14px;
    text-align:center;
    font-weight:600;
}

td{
    color:#ddd;
    padding:14px;
    text-align:center;
    border-bottom:1px solid rgba(255,255,255,0.08);
}

tr:hover{
    background:rgba(255,255,255,0.04);
}

.status-active{
    color:#00ff99;
    font-weight:600;
}

.status-inactive{
    color:#ff6666;
    font-weight:600;
}

.action-btn{
    padding:8px 14px;
    border-radius:8px;
    text-decoration:none;
    font-size:13px;
    font-weight:600;
    display:inline-block;
    margin:0 3px;
    transition:.3s;
}

.update-btn{
    background:#ffc107;
    color:#000;
}

.update-btn:hover{
    box-shadow:0 0 10px #ffc107;
}

.delete-btn{
    background:#ff4d4d;
    color:#fff;
}

.delete-btn:hover{
    box-shadow:0 0 10px #ff4d4d;
}

.no-data{
    text-align:center;
    color:#ccc;
    padding:25px;
}

.footer{
    margin-top:25px;
    display:flex;
    justify-content:center;
    gap:15px;
}

.home-btn{
    background:#6c757d;
    color:#fff;
    padding:12px 18px;
    border-radius:10px;
    text-decoration:none;
    font-weight:600;
    transition:.3s;
}

.home-btn:hover{
    box-shadow:0 0 10px #6c757d;
}

.add-btn{
    background:#00ffff;
    color:#000;
    padding:12px 18px;
    border-radius:10px;
    text-decoration:none;
    font-weight:600;
    transition:.3s;
}

.add-btn:hover{
    box-shadow:0 0 15px #00ffff,0 0 30px #00ffff;
}
</style>
</head>

<body>
<div class="container">

    <h2>Model Product List</h2>
    <div class="table-wrapper">

        <table>

            <thead>
                <tr>
                    <th>Model Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>

            <tbody>
                <c:choose>

                    <c:when test="${not empty modelProducts}">
                        <c:forEach var="m" items="${modelProducts}">
                            <tr>

                                <td>${m.identifier}</td>
                                <td>${m.description}</td>

                                <td>
                                    <span class="${m.status ? 'status-active' : 'status-inactive'}">
                                        ${m.status ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>

                                    <a href="${pageContext.request.contextPath}/modelproduct/get?identifier=${m.identifier}"
                                       class="action-btn update-btn">
                                        Update
                                    </a>

                                    <a href="${pageContext.request.contextPath}/modelproduct/delete?identifier=${m.identifier}"
                                       class="action-btn delete-btn"
                                       onclick="return confirm('Are you sure you want to delete this model product?')">
                                        Delete
                                    </a>
                                </td>
                            </tr>

                        </c:forEach>

                    </c:when>

                    <c:otherwise>

                        <tr>
                            <td colspan="4" class="no-data">
                                No Model Products Found
                            </td>
                        </tr>

                    </c:otherwise>

                </c:choose>
            </tbody>
        </table>

    </div>

    <div class="footer">

        <a href="${pageContext.request.contextPath}/" class="home-btn">
            Home
        </a>

        <a href="${pageContext.request.contextPath}/modelproduct/add" class="add-btn">
            +Add Model
        </a>

    </div>
</div>

</body>
</html>