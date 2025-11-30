import javax.swing.*;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        showConsoleMenu();
    }

    private static void showConsoleMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("ГОЛОВНЕ МЕНЮ - ЛАБОРАТОРНІ РОБОТИ");
            System.out.println("=".repeat(50));
            System.out.println("1 - Анімація обертового відрізка");
            System.out.println("2 - Обробка матриць з GUI");
            System.out.println("0 - Вихід");
            System.out.println("=".repeat(50));
            System.out.print("Оберіть завдання: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    SwingUtilities.invokeLater(RotatingSegment::createAndShowGUI);
                    System.out.println("Запущено анімацію обертового відрізка...");
                    break;

                case 2:
                    SwingUtilities.invokeLater(() -> {
                        new MatrixGUI().setVisible(true);
                    });
                    System.out.println("Запущено обробку матриць...");
                    break;

                case 0:
                    System.out.println("До побачення!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Невірний вибір! Спробуйте ще раз.");
                    break;
            }

            if (choice == 1 || choice == 2) {
                System.out.println("Графічне вікно запущено. Повернення до меню...");
            }
        }
    }
}