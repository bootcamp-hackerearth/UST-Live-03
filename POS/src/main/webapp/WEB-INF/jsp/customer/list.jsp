<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Customer List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>

        *{
            margin:0;
            padding:0;
            box-sizing:border-box;
        }

        body{
            font-family:'Poppins',sans-serif;
            background:#f5f5f5;
            min-height:100vh;
            display:flex;
            justify-content:center;
            align-items:center;
            padding:30px;
        }

        .container{
            width:1200px;
            background:#fff;
            border-radius:20px;
            padding:35px 55px;
            box-shadow:0 20px 45px rgba(0,0,0,0.12);
            position:relative;
        }

        /* Back Button */

        .back-btn{
            position:absolute;
            top:22px;
            left:22px;
            width:48px;
            height:48px;
            border-radius:50%;
            background:#f1f3f8;
            display:flex;
            align-items:center;
            justify-content:center;
            text-decoration:none;
            color:#4b6cb7;
            font-size:24px;
            box-shadow:0 4px 10px rgba(0,0,0,0.1);
        }

        h2{
            text-align:center;
            color:#4b6cb7;
            font-size:28px;
            font-weight:600;
            margin-bottom:30px;
        }

        table{
            width:100%;
            border-collapse:collapse;
            table-layout:fixed;
        }

        thead{
            background:#5372bc;
            color:white;
        }

        th{
            padding:16px;
            font-size:14px;
            font-weight:600;
            text-align:center;
        }

        td{
            padding:18px 12px;
            text-align:center;
            border-bottom:1px solid #ececec;
            font-size:14px;
            word-wrap:break-word;
        }

        tbody tr:hover{
            background:#fafafa;
        }

        .action-btn{
            text-decoration:none;
            font-size:18px;
            margin:0 6px;
        }

        .edit{
            color:#ff7b42;
        }

        .delete{
            color:#4f6fff;
        }

        .alert{
            text-align:center;
            padding:15px;
            border-radius:10px;
            background:#fff3cd;
            color:#856404;
            margin-bottom:20px;
        }

        .footer-buttons{
            display:flex;
            justify-content:center;
            gap:16px;
            margin-top:28px;
        }

        .btn{
            text-decoration:none;
            padding:12px 24px;
            border-radius:14px;
            color:white;
            font-weight:600;
            transition:0.3s;
        }

        .btn-home{
            background:#6c757d;
        }

        .btn-add{
            background:linear-gradient(135deg,#4b6cb7,#182848);
        }

        .btn:hover{
            transform:translateY(-2px);
        }

    </style>
</head>

<body>

<div class="container">

    <a href="javascript:history.back()" class="back-btn">←</a>

    <h2>List of Customers</h2>

    <c:if test="${empty customers}">
        <div class="alert">
            No customers found
        </div>
    </c:if>

    <c:if test="${not empty customers}">

        <table>

            <thead>
            <tr>
                <th>ID</th>
                <th>Identifier</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Type</th>
                <th>Address</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>

            <c:forEach items="${customers}" var="customer">

                <tr>

                    <td>${customer.id}</td>
                    <td>${customer.identifier}</td>
                    <td>${customer.phoneno}</td>
                    <td>${customer.email}</td>
                    <td>${customer.partytype}</td>
                    <td>${customer.address}</td>

                    <td>

                        <a href="/customer/get?identifier=${customer.identifier}"
                           class="action-btn edit"
                           title="Edit">
                            ✏️
                        </a>

                        <a href="/customer/delete?identifier=${customer.identifier}"
                           class="action-btn delete"
                           title="Delete"
                           onclick="return confirm('Are you sure you want to delete this customer?');">
                            🗑️
                        </a>

                    </td>

                </tr>

            </c:forEach>

            </tbody>

        </table>

    </c:if>

    <div class="footer-buttons">

        <a href="/" class="btn btn-home">
            Home
        </a>

        <a href="/customer/add" class="btn btn-add">
            + Add Customer
        </a>

    </div>

</div>

</body>
</html>