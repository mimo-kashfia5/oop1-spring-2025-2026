import gui.BankGUI;
import javax.swing.SwingUtilities;

public class Start {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankGUI::new);
    }
}