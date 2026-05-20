package fileIO;

import entity.BankAccount;
import java.io.*;

public class BankAccountFileIO {

    private static final String FILE_NAME = "data/accounts.txt";
    private static final String TEMP_FILE = "data/temp.txt";

    // ── Initialisation ──

    public static void createFileIfNotExists() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create database file: " + e.getMessage());
            }
        }
    }

    // ── Helper: count valid lines in file ──

    private static int countRecords() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (BankAccount.fromLine(line) != null) count++;
            }
        } catch (IOException ignored) {}
        return count;
    }

    // ── Validation ──

    public static boolean idExists(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromLine(line);
                if (acc != null && acc.getAccountId().equals(id))
                    return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    // ── CREATE ───

    public static boolean addAccount(BankAccount acc) {
        createFileIfNotExists();
        if (idExists(acc.getAccountId())) return false;
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            pw.println(acc.toLine());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ── READ (returns Object[][]) ───

    public Object[][] readAll() {
        createFileIfNotExists();
        int total       = countRecords();
        Object[][] rows = new Object[total][6];
        int idx         = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < total) {
                BankAccount acc = BankAccount.fromLine(line);
                if (acc != null) {
                    rows[idx] = acc.toRow();
                    idx++;
                }
            }
        } catch (IOException ignored) {}
        return rows;
    }

    // ── UPDATE ───

    public static boolean updateAccount(BankAccount acc) {
        File inputFile = new File(FILE_NAME);
        File tempFile  = new File(TEMP_FILE);
        boolean found  = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                BankAccount existing = BankAccount.fromLine(line);
                if (existing != null && existing.getAccountId().equals(acc.getAccountId())) {
                    bw.write(acc.toLine());
                    found = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
        } else {
            tempFile.delete();
        }
        return found;
    }

    // ── DELETE ───

    public static boolean deleteAccount(String id) {
        File inputFile = new File(FILE_NAME);
        File tempFile  = new File(TEMP_FILE);
        boolean found  = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                BankAccount existing = BankAccount.fromLine(line);
                if (existing != null && existing.getAccountId().equals(id)) {
                    found = true;
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (found) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
        } else {
            tempFile.delete();
        }
        return found;
    }

    // ── SEARCH (returns Object[][]) ───

    public static Object[][] searchAccounts(String keyword) {
        String kw = keyword.toLowerCase();

        // First pass: count matches
        int matchCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromLine(line);
                if (acc != null &&
                   (acc.getAccountId().toLowerCase().contains(kw) ||
                    acc.getAccountName().toLowerCase().contains(kw))) {
                    matchCount++;
                }
            }
        } catch (IOException ignored) {}

        // Second pass: fill array
        Object[][] results = new Object[matchCount][6];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < matchCount) {
                BankAccount acc = BankAccount.fromLine(line);
                if (acc != null &&
                   (acc.getAccountId().toLowerCase().contains(kw) ||
                    acc.getAccountName().toLowerCase().contains(kw))) {
                    results[idx] = acc.toRow();
                    idx++;
                }
            }
        } catch (IOException ignored) {}

        return results;
    }
}