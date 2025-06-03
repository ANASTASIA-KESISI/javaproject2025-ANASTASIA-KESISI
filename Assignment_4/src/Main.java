import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int rows = getIntInput("Enter number of rows (e.g. 10–30):", 15);
        int cols = getIntInput("Enter number of columns (e.g. 10–30):", 20);

        JFrame frame = new JFrame("Interactive Map Game");
        GamePanel game = new GamePanel(rows, cols);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static int getIntInput(String message, int defaultValue) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message, defaultValue);
            if (input == null) System.exit(0); // User cancelled
            try {
                int value = Integer.parseInt(input);
                if (value >= 5 && value <= 50) return value;
                JOptionPane.showMessageDialog(null, "Please enter a number between 5 and 50.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }
}

