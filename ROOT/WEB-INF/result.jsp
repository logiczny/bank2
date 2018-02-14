<html>
    <body>
        <p>${statusLog}</p>
        <table>
            <form action="connecttomysql" method="get">
                <!-- <input type="submit" value="Log In" name="gotoLogIn"> -->
                <input type="submit" value="Log Out" name="logOut">
                <input type="submit" value="Money Transfer" name="doMoneyTransfer"> 
            </form>
            <tr>
                <td>User name:</td>
                <td>${userName}</td>
            </tr>
            <tr>
                <td>Account balance:</td>
                <td>${accountBalance}</td>
            </tr>
        </table>
    </body>
</html>