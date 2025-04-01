function printInvoice() {
    let invoiceContent = document.getElementById("invoiceContent").innerHTML;
    let printWindow = window.open("", "_blank", "width=800,height=600");
    printWindow.document.open();
    printWindow.document.write(`
            <html>
                <head>
                    <title>Hóa đơn</title>
                    <style>
                        body { font-family: Arial, sans-serif; padding: 20px; }
                        h2 { text-align: center; }
                        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                        table, th, td { border: 1px solid black; text-align: center; padding: 8px; }
                        .total { font-weight: bold; text-align: right; }
                    </style>
                </head>
                <body>
                    <h2>HÓA ĐƠN BÁN HÀNG</h2>
                    ${invoiceContent}
                    <script>
                        window.onload = function() {
                            window.print();
                        };
                    </script>
                </body>
            </html>
        `);
    printWindow.document.close();
}

