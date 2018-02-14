<html>
    <body>
        <p>${statusLog}</p>
        <form action="connecttomysql" method="get">
            <table>
                <tr>
                    <td>Transfer Recipient:</td>
                    <td>${transferRecipient}</td>
                </tr>
                <tr>
                    <td>Transfer Amount:</td>
                    <td>${transferAmount}</td>
                </tr>
                <tr>
                    <td>Enter code:</td>
                    <td><input type="text" name="transferCode"></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Finalize transfer" name="finalizeTransfer"></td>
                    <td><input type="submit" value="Log Out" name="logOut"></td>
                </tr>
            </table>
        </form>
    </body>
</html>