<body>
    <p>${logStatus}</p>
    <form action="connecttomysql" method="post">  
        <table>
            <tr>
                <td>Enter user name:</td> 
                <td><input type="text" name="name"></td>
            </tr>
            <tr>
                <td>Enter password:</td>
                <td><input type="password" name="password"></td>
            </tr>
            <tr>
                <td><input type="submit" value="Login" name="logIn"></td>
                <td><input type="submit" value="Create New User" name="gotoNewUser"></td>
            </tr>
        </table>  
    </form>  
<body>