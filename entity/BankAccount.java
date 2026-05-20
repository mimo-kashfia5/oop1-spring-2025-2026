package entity;

public class BankAccount {

    private String accountId;
    private String accountName;
    private String accountType;
    private double balance;
    private String email;
    private String phone;


    public BankAccount(String accountId, String accountName, String accountType, String balanceStr, String email) {
        this.accountId   = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.email       = email;
        try { this.balance = Double.parseDouble(balanceStr); }
        catch (NumberFormatException e) { this.balance = 0.0; }
    }


    public BankAccount(String accountId, String accountName, String accountType, double balance, String email) {
        this.accountId   = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance     = balance;
        this.email       = email;
    }

    public BankAccount(String accountId, String accountName, String accountType, double balance, String email, String phone) {
        this(accountId, accountName, accountType, balance, email);
        this.phone = phone;
    }

    public String getAccountId()   { return accountId; }
    public String getAccountName() { return accountName; }
    public String getAccountType() { return accountType; }
    public double getBalance()     { return balance; }
    public String getEmail()       { return email; }
    public String getPhone()       { return phone != null ? phone : ""; }

    public void setAccountId(String accountId)     { this.accountId   = accountId; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setBalance(double balance)         { this.balance     = balance; }
    public void setEmail(String email)             { this.email       = email; }
    public void setPhone(String phone)             { this.phone       = phone; }

    
    public String toLine() {
        return accountId + "," + accountName + "," + accountType + ","
             + balance   + "," + email       + "," + getPhone();
    }

   
    public static BankAccount fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] d = line.split(",", -1);
       
        if (d.length == 5) {
            return new BankAccount(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim());
        } else if (d.length >= 6) {
            BankAccount acc = new BankAccount(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(), d[4].trim());
            acc.setPhone(d[5].trim());
            return acc;
        }
        return null;
    }

    
    public Object[] toRow() {
        return new Object[]{ accountId, accountName, email, getPhone(), accountType, balance };
    }
}