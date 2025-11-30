import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MatrixGUI extends JFrame {
    private JTextField filePathField;
    private JButton loadButton;
    private JButton processButton;
    private JTable originalTable;
    private JTable resultTable;
    private JLabel statusLabel;

    private double[][] currentMatrix;
    private int matrixSize;

    public MatrixGUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Обробка матриць - Лабораторна №6");
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Готовий до роботи");
        add(statusLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setSize(800, 600);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Шлях до файлу:"));
        filePathField = new JTextField(30);
        panel.add(filePathField);

        loadButton = new JButton("Завантажити з файлу");
        loadButton.addActionListener(new LoadButtonListener());
        panel.add(loadButton);

        processButton = new JButton("Обробити матрицю");
        processButton.setEnabled(false);
        processButton.addActionListener(new ProcessButtonListener());
        panel.add(processButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        originalTable = new JTable();
        JScrollPane originalScrollPane = new JScrollPane(originalTable);
        originalScrollPane.setBorder(BorderFactory.createTitledBorder("Вихідна матриця"));
        panel.add(originalScrollPane);

        resultTable = new JTable();
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("Результат обробки"));
        panel.add(resultScrollPane);

        return panel;
    }

    private class LoadButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String filePath = filePathField.getText().trim();
                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(MatrixGUI.this,
                            "Введіть шлях до файлу!", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                loadMatrixFromFile(filePath);
                displayMatrixInTable(originalTable, currentMatrix);
                processButton.setEnabled(true);
                statusLabel.setText("Матрицю завантажено успішно");

            } catch (IOException ex) {
                handleException("Помилка читання файлу: " + ex.getMessage(), ex);
            } catch (NumberFormatException ex) {
                handleException("Невірний формат чисел у файлі!", ex);
            } catch (NegativeMatrixElementException ex) {
                handleException(ex.getMessage(), ex);
            } catch (Exception ex) {
                handleException("Невідома помилка: " + ex.getMessage(), ex);
            }
        }
    }

    private class ProcessButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (currentMatrix == null) {
                    JOptionPane.showMessageDialog(MatrixGUI.this,
                            "Спочатку завантажте матрицю!", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                checkForNegativeElements(currentMatrix);

                int minCol = findColumnWithMinElement(currentMatrix);
                double[][] rearrangedMatrix = rearrangeColumns(currentMatrix, minCol);

                displayMatrixInTable(resultTable, rearrangedMatrix);
                statusLabel.setText("Обробка завершена. Стовпець з мінімумом: " + minCol);

            } catch (NegativeMatrixElementException ex) {
                handleException(ex.getMessage(), ex);
            } catch (Exception ex) {
                handleException("Помилка обробки матриці: " + ex.getMessage(), ex);
            }
        }
    }

    private void loadMatrixFromFile(String filePath) throws IOException, NumberFormatException, NegativeMatrixElementException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Файл порожній");
            }

            matrixSize = Integer.parseInt(line.trim());
            currentMatrix = new double[matrixSize][matrixSize];

            for (int i = 0; i < matrixSize; i++) {
                line = reader.readLine();
                if (line == null) {
                    throw new IOException("Недостатньо даних у файлі");
                }

                String[] elements = line.trim().split("\\s+");
                if (elements.length != matrixSize) {
                    throw new IOException("Невірна кількість елементів у рядку " + (i + 1));
                }

                for (int j = 0; j < matrixSize; j++) {
                    currentMatrix[i][j] = Double.parseDouble(elements[j]);
                }
            }

            checkForNegativeElements(currentMatrix);
        }
    }

    private void checkForNegativeElements(double[][] matrix) throws NegativeMatrixElementException {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] < 0) {
                    throw new NegativeMatrixElementException(
                            "Знайдено від'ємний елемент у позиції [" + i + "][" + j + "]: " + matrix[i][j]);
                }
            }
        }
    }

    private void displayMatrixInTable(JTable table, double[][] matrix) {
        if (matrix == null) return;

        String[] columnNames = new String[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            columnNames[i] = "Стовпець " + (i + 1);
        }

        Object[][] data = new Object[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                data[i][j] = String.format("%.2f", matrix[i][j]);
            }
        }

        table.setModel(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private int findColumnWithMinElement(double[][] matrix) {
        double min = matrix[0][0];
        int minCol = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] < min) {
                    min = matrix[i][j];
                    minCol = j;
                }
            }
        }
        return minCol;
    }

    private double[][] rearrangeColumns(double[][] matrix, int minCol) {
        int n = matrix.length;
        double[][] result = new double[n][n];

        for (int j = 0; j < n; j++) {
            int sourceCol = (minCol + j) % n;
            for (int i = 0; i < n; i++) {
                result[i][j] = matrix[i][sourceCol];
            }
        }
        return result;
    }

    private void handleException(String message, Exception ex) {
        statusLabel.setText("Помилка: " + message);
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }

}