<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Money Transfer - RSMV Bank</title>
    <link rel="stylesheet" href="style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <style>
        body { background: linear-gradient(135deg, #6d6676 0%, #2b2630 100%); font-family: Arial, Helvetica, sans-serif; }
        .wrap {
            width: 760px;
            margin: 40px auto;
            display: flex;
            justify-content: center;
        }
        .card {
            width: 520px;
            background: linear-gradient(180deg, rgba(30,30,35,0.95), rgba(18,18,20,0.95));
            color: #fff;
            border-radius: 18px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.6);
        }
        .logo-center { text-align:center; margin-bottom: 6px; }
        .logo-center img { width:84px; height:auto; }
        h1 { text-align:center; margin: 6px 0 18px 0; font-size: 22px; color: #fff; }
        label { display:block; color:#d0cfe0; margin-top: 12px; font-size:14px; }
        select, input[type="number"], input[type="text"] {
            width:100%; padding:12px; margin-top:6px; border-radius:8px; border: none; background: #1f1f24; color:#fff;
            box-shadow: inset 0 1px 0 rgba(255,255,255,0.02);
        }
        .btn {
            display:block; width:100%; padding:12px; margin-top:18px; border-radius:26px;
            background: linear-gradient(90deg,#ff6b6b,#ff4db2); color:#fff; font-weight:700; font-size:16px;
            border: none; cursor:pointer; box-shadow: 0 6px 18px rgba(255,77,178,0.18);
        }
        .help { color:#bbb; font-size:13px; margin-top:8px; }
        .result-box { margin-top:16px; padding:12px; border-radius:8px; background:#0b1220; color:#fff; border:1px solid rgba(255,255,255,0.04)}
        .result-box.success { border-color: rgba(0,200,83,0.2); background: linear-gradient(90deg,#032a1c,#08221b) }
        .result-box.error { border-color: rgba(255,0,0,0.15); background: linear-gradient(90deg,#2a0b0b,#1a0b0b) }
    </style>
</head>
<body>

<div class="wrap">
    <div class="card">
        <div class="logo-center">
            <img src="logo.PNG" alt="RSMV Logo">
        </div>

        <h1>Money Transfer</h1>

        <form action="MoneyServlet" method="post" id="moneyForm">
            <label for="action">Operation</label>
            <select name="action" id="actionDropdown" onchange="toggleFields()" required>
                <option value="self_transfer">Self Transfer (to another account)</option>
                <option value="pay_contact">Pay a Contact</option>
                <option value="balance">View Balance</option>
            </select>

            <div id="amountArea">
                <label for="amount">Amount (₹)</label>
                <input type="number" name="amount" id="amountInput" placeholder="Enter amount" min="1" step="0.01">
            </div>

            <div id="toAccountArea" style="display:none;">
                <label for="to_account">Destination Account No</label>
                <input type="text" name="to_account" id="toAccountInput" placeholder="Eg: ACC0002">
                <div class="help">For self transfer, provide the receiving account number.</div>
            </div>

            <div id="contactArea" style="display:none;">
                <label for="contact">Contact Name</label>
                <input type="text" name="contact" id="contactInput" placeholder="Eg: Ritik Sharma">
                <div class="help">For paying a contact, provide contact name.</div>
            </div>

            <button class="btn" type="submit">Submit</button>
        </form>

        <div id="result" style="margin-top:12px;">
            <%= request.getAttribute("message") %>
        </div>
    </div>
</div>

<script>
function toggleFields() {
    var action = document.getElementById('actionDropdown').value;
    document.getElementById('amountArea').style.display = (action === 'balance') ? 'none' : 'block';
    document.getElementById('toAccountArea').style.display = (action === 'self_transfer') ? 'block' : 'none';
    document.getElementById('contactArea').style.display = (action === 'pay_contact') ? 'block' : 'none';
}
window.onload = toggleFields;
</script>

</body>
</html>
