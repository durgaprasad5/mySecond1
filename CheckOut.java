/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycart;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Helias
 */
@ManagedBean
@ApplicationScoped
public class CheckOut {

    public ArrayList<BillItems> items = new ArrayList<BillItems>();

    public String userId;
    public String invoiceId;
    public String amount;

    public String bankName;
    public String cardNumber;
    public String expiry;
    public String cvv;

    public String inVoiceData;

    public String getInVoiceData() {
        return inVoiceData;
    }

    public void setInVoiceData(String inVoiceData) {
        this.inVoiceData = inVoiceData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNumberOfItems(String userId) {
        try {

            SQL db = new SQL();
            db.createConnection();

            String sql1 = "select invoiceId from invoices where userId='" + userId + "' and status='N'";
            System.out.println("II-getNumberOfItems-1:" + sql1);
            ResultSet rset = db.queryRecord(sql1);

            boolean found = rset.next();
            if (found) {
                rset.last();
                int count = rset.getRow();
                rset.beforeFirst();
                if (rset.next()) {
                    this.userId = userId;
                    this.invoiceId = rset.getString(1);
                    return getItemsTableFromInvoiceId(this.invoiceId, userId);
                }
            } else {
                return "None";
            }
        } catch (Exception e) {
            System.out.println("<br /><b>Exception : " + e.toString() + "</b>");
            ConfigurationValues.notifications = "Failed To Fetch Items: None Exists : " + e.toString();
        }
        return "None";
    }

    public String getItemsTableFromInvoiceId(String invoice, String user) {
        this.items.clear();
        try {

            SQL db = new SQL();
            db.createConnection();

            String sql1 = "select *,(select product_name from items i where i.product_id=ii.product_id) as product_name from invoice_items ii where ii.invoiceId='" + invoice + "'";
            System.out.println("II-getNumberOfItems-1:" + sql1);
            ResultSet rset = db.queryRecord(sql1);

            ResultSetMetaData rsmd = rset.getMetaData();
            int numColumns = rsmd.getColumnCount();

            boolean found = rset.next();
            //out.println("<br><b>Sql Result</b>+"+query+"==>"+found);
            if (found) {
                rset.last();
                int count = rset.getRow();
                int row = 0;
                String line = "";
                rset.beforeFirst();
                while (rset.next()) {
                    row++;
                    BillItems data = new BillItems();
                    for (int i = 1; i <= numColumns; i++) {
                        // uncomment the following three lines and define bool least to initiate blocking those columns
                        boolean notDisplay = false;
                        notDisplay = (i == 1 || i == 2 || i == 3 || i == 6 || i == 7 || i == 8);
                        if (!notDisplay) {
                            if (i == 10) {
                                data.setProductName(rset.getString(i));
                            } else if (i == 5) {
                                data.setUnitPrice(Math.round(Double.parseDouble(rset.getString(i))));
                            } else if (i == 4) {
                                data.setQuantity(Integer.parseInt(rset.getString(i).split("\\.")[0]));
                            } else if (i == 9) {
                                data.setAmount(Math.round(Double.parseDouble(rset.getString(i))));
                            } else {

                            }
                        }
                    }
                    this.items.add(data);
                }
            }
            String resultHtml = "";

            resultHtml += "<table>";
            resultHtml += "<tr><td><font color='red' face='verdana' size='5'>" + ConfigurationValues.projectTitle + "</font></td><td></td></tr>";
            resultHtml += "<tr><td><font color='blue' face='courier' size='2'>Email:</font></td><td><font color='blue' face='courier' size='5'>admin@mycart.com</font></td></tr>";
            resultHtml += "<tr><td><font color='blue' face='courier' size='2'>Web:</font></td><td><font color='blue' face='courier' size='5'>http://www.mycart.com</font></td></tr>";
            resultHtml += "<tr><td><font color='blue' face='courier' size='2'>Phone:</font></td><td><font color='blue' face='courier' size='5'>9999999999</font></td></font></tr>";
            resultHtml += "</table><hr />";

            // customer details
            sql1 = "select * from users where userId='" + user + "'";
            System.out.println("CO-customerDetails-1:" + sql1);
            rset = db.queryRecord(sql1);
            found = rset.next();

            if (found) {
                rset.last();
                int count = rset.getRow();
                int row = 0;
                resultHtml += "<table>";
                rset.beforeFirst();
                if (rset.next()) {
                    resultHtml += "<tr><td><font color='red' face='courier' size='2'>Name</font></td><td><font color='red' face='courier' size='2'>" + rset.getString("name") + "</font></td></tr>";
                    resultHtml += "<tr><td><font color='red' face='courier' size='2'>Email</font></td><td><font color='red' face='courier' size='2'>" + rset.getString("email") + "</font></td></tr>";
                    resultHtml += "<tr><td><font color='red' face='courier' size='2'>Address</font></td><td><font color='red' face='courier' size='2'>" + rset.getString("address") + "</font></td></tr>";
                    resultHtml += "<tr><td><font color='red' face='courier' size='2'>ZipCode</font></td><td><font color='red' face='courier' size='2'>" + rset.getString("zip") + "</font></td></tr>";
                }
                resultHtml += "</table><hr />";
            }

            resultHtml += "<table border=0><caption><h3><font color='red'>INVOICE DATA</font></h3></caption><thead>";
            resultHtml += "<tr><th>Index</th><th>Product Name</th><th>Quantity</th><th>Unit Price</th><th>GrossPrice</th></tr>";
            // table
            double total = 0.0;
            for (int i = 0; i < items.size(); i++) {
                resultHtml += "<tr>";
                resultHtml += "<td>" + (i + 1) + "</td>";
                resultHtml += "<td>" + items.get(i).getProductName() + "</td>";
                resultHtml += "<td>" + items.get(i).getQuantity() + "</td>";
                resultHtml += "<td>" + items.get(i).getUnitPrice() + "</td>";
                resultHtml += "<td>" + items.get(i).getAmount() + "$</td>";
                total += items.get(i).getAmount();
                resultHtml += "<tr>";
            }

            resultHtml += "<tr>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<tr>";

            resultHtml += "<tr>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<td></td>";
            resultHtml += "<td><font color='red'>Total Price</font></td>";
            resultHtml += "<td><b><i>" + total + "$</i></b></td>";
            resultHtml += "<tr></table><hr /> ";

            this.amount = Double.toString(total);

            this.inVoiceData = resultHtml;
            return resultHtml;

        } catch (Exception e) {
            return ("<br /><b>Exception : " + e.toString() + "</b>");
        }
    }

    public String finalize(String user) {
        String ack = "2";
        try {
            SQL db = new SQL();
            db.createConnection();

            String sql1 = "select * from invoices where userId='" + user + "' and status='N' and invoiceId='" + this.invoiceId + "'";
            System.out.println("CO-finalize-1:" + sql1);
            ResultSet rset = db.queryRecord(sql1);

            boolean found = rset.next();
            if (found) {
                sql1 = "update invoices set status='Y',purchaseDate=now() where userId='" + user + "' and status='N' and invoiceId='" + this.invoiceId + "'";
                System.out.println("CO-finalize-1:" + sql1);
                db.updateRecord(sql1);
                updateStock(invoiceId);

                //mail settings
                DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                try {
                    String smtpServer = ConfigurationValues.getSmtp().trim();
                    String from = ConfigurationValues.getAdminMail().trim();
                    String password = ConfigurationValues.getAdminPassword().trim();
                    String subject = "Invoice for Your Purchase from " + ConfigurationValues.projectTitle;
                    String body = "\n";
                    body += this.inVoiceData + "\n\n";
                    body += "...Thank You...Please Visit Us For Very Good Shopping Experience...";
                    MailSender.send(smtpServer, getUserEmail(user), from, password, subject, body);

                } catch (Exception e) {
                    System.err.println("Mailing Exception : " + e.toString());
                }

                ConfigurationValues.notifications = "Successfully Checked Out! Will be delivered to the address specified in your profile";
                ack = "1";
            } else {
                ConfigurationValues.notifications = "Check Out Details Not Found";
                ack = "2";
            }
        } catch (Exception e) {
            System.out.println("<br /><b>Exception : " + e.toString() + "</b>");
            ConfigurationValues.notifications = "Failed To Check Out:" + e.toString();
            ack = "2";
        }
        return ack;
    }

    public String getUserEmail(String user) {
        try {
            SQL db = new SQL();
            db.createConnection();

            String sql1 = "select email from users where userId='" + user + "'";
            System.out.println("CO-user-email:" + sql1);
            ResultSet rset = db.queryRecord(sql1);

            boolean found = rset.next();
            if (found) {
                rset.last();
                int count = rset.getRow();
                rset.beforeFirst();
                if (rset.next()) {
                    return rset.getString(1);
                }
            } else {
                return "none";
            }
        } catch (Exception e) {
            System.out.println("<br /><b>Exception : " + e.toString() + "</b>");
            ConfigurationValues.notifications = "Failed To Get Email:" + e.toString();
        }
        return "none";
    }

    private void updateStock(String invoiceId) {
        try {
            SQL db = new SQL();
            db.createConnection();

            String sql1 = "select product_id,quantity from invoice_items where invoiceId='" + invoiceId + "'";
            System.out.println("CO-stockUpdate:" + sql1);
            ResultSet rset = db.queryRecord(sql1);

            boolean found = rset.next();
            if (found) {
                rset.last();
                int count = rset.getRow();
                rset.beforeFirst();
                while (rset.next()) {
                    updateStockWithIdAndQuantity(rset.getString(1), rset.getString(2));
                }
            }
            db.closeConnection();
        } catch (Exception e) {
            System.out.println("<br /><b>Exception : " + e.toString() + "</b>");
            ConfigurationValues.notifications = "Failed To Update StockI:" + e.toString();
        }
    }

    private void updateStockWithIdAndQuantity(String productId, String quantity) {
        try {
            SQL db = new SQL();
            db.createConnection();

            String sql1 = "update items set product_quantity=product_quantity-"+quantity+" where product_id="+productId;
            System.out.println("CO-stockUpdateII:" + sql1);
            db.updateRecord(sql1);
            db.closeConnection();
                    
        } catch (Exception e) {
            System.out.println("<br /><b>Exception : " + e.toString() + "</b>");
            ConfigurationValues.notifications = "Failed To Update Stock II:" + e.toString();
        }
    }

}
