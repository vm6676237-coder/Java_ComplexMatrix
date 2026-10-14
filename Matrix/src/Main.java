import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static final int MAX_MATRICES = 1000;
    static Matrix[] matrices = new Matrix[MAX_MATRICES];

    public static void main(String[] args) {

        System.out.println("=== Калькулятор матриц над комплексными числами ===");

        while (true) {
            System.out.println();
            System.out.println("Доступные матрицы: " + listAvailable());
            System.out.println();
            System.out.println("Выберите действие:");
            System.out.println("  1) Создать матрицу");
            System.out.println("  2) Сложить две матрицы");
            System.out.println("  3) Вычесть две матрицы");
            System.out.println("  4) Умножить две матрицы");
            System.out.println("  5) Умножить матрицу на комплексное число");
            System.out.println("  6) Транспонировать матрицу");
            System.out.println("  7) Найти детерминант");
            System.out.println("  8) Найти обратную матрицу");
            System.out.println("  9) Разделить матрицу на матрицу");
            System.out.println(" 10) Показать матрицу");
            System.out.println(" 11) Удалить матрицу");
            System.out.println("  0) Выход");
            System.out.print("Ваш выбор: ");

            int choice = readInt();

            if (choice == 0) {
                System.out.println("Пока!");
                return;
            }

            try {
                if (choice == 1) doCreate();
                else if (choice == 2) doBinary("add");
                else if (choice == 3) doBinary("sub");
                else if (choice == 4) doBinary("mul");
                else if (choice == 5) doScalar();
                else if (choice == 6) doTranspose();
                else if (choice == 7) doDeterminant();
                else if (choice == 8) doInverse();
                else if (choice == 9) doBinary("div");
                else if (choice == 10) doShow();
                else if (choice == 11) doDelete();
                else System.out.println("Нет такого пункта.");
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    // ---------- ОПЕРАЦИИ ----------

    static void doCreate() {
        int slot = firstFree();
        if (slot < 0) {
            System.out.println("Хранилище заполнено (1000 матриц). Удалите что-нибудь.");
            return;
        }

        System.out.println("Матрица будет сохранена как " + nameOf(slot) + ".");

        System.out.print("Сколько строк? ");
        int r = readInt();
        System.out.print("Сколько столбцов? ");
        int c = readInt();

        if (r <= 0 || c <= 0) {
            System.out.println("Размеры должны быть положительными.");
            return;
        }

        Matrix m = new Matrix(r, c);
        System.out.println("Введите элементы построчно в формате: re im (можно через пробел или запятую).");
        for (int i = 0; i < r; ++i) {
            for (int j = 0; j < c; ++j) {
                System.out.print("  [" + i + "][" + j + "]: ");
                double[] pair = readComplexLine();
                m.set(i, j, new Complex(pair[0], pair[1]));
            }
        }

        matrices[slot] = m;
        System.out.println("Матрица " + nameOf(slot) + " создана:");
        printMatrix(m);
    }

    static void doBinary(String op) {
        int ia = askMatrix("первую");
        if (ia < 0) return;
        int ib = askMatrix("вторую");
        if (ib < 0) return;

        Matrix a = matrices[ia];
        Matrix b = matrices[ib];

        Matrix result;
        if (op.equals("add"))      result = a.add(b);
        else if (op.equals("sub")) result = a.sub(b);
        else if (op.equals("mul")) result = a.mul(b);
        else if (op.equals("div")) result = a.div(b);
        else throw new IllegalArgumentException("Неизвестная операция");

        String resName = saveResult(result, nameOf(ia) + " " + symbolOf(op) + " " + nameOf(ib));
        System.out.println("Результат сохранён как " + resName + ":");
        printMatrix(result);
    }

    static void doScalar() {
        int ia = askMatrix("исходную");
        if (ia < 0) return;

        System.out.print("Скаляр re im: ");
        double[] pair = readComplexLine();
        Complex s = new Complex(pair[0], pair[1]);

        Matrix result = matrices[ia].mul(s);
        String resName = saveResult(result, nameOf(ia) + " * (" + s + ")");
        System.out.println("Результат сохранён как " + resName + ":");
        printMatrix(result);
    }

    static void doTranspose() {
        int ia = askMatrix("исходную");
        if (ia < 0) return;

        Matrix result = matrices[ia].transpose();
        String resName = saveResult(result, "T(" + nameOf(ia) + ")");
        System.out.println("Результат сохранён как " + resName + ":");
        printMatrix(result);
    }

    static void doDeterminant() {
        int ia = askMatrix("исходную");
        if (ia < 0) return;

        Complex det = matrices[ia].determinant();
        System.out.print("det(" + nameOf(ia) + ") = ");
        System.out.printf("%.6f%+.6fi%n", det.GetRe(), det.GetIm());
    }

    static void doInverse() {
        int ia = askMatrix("исходную");
        if (ia < 0) return;

        Matrix result = matrices[ia].inverse();
        String resName = saveResult(result, "inv(" + nameOf(ia) + ")");
        System.out.println("Результат сохранён как " + resName + ":");
        printMatrix(result);
    }

    static void doShow() {
        int ia = askMatrix("нужную");
        if (ia < 0) return;

        System.out.println("Матрица " + nameOf(ia) + ":");
        printMatrix(matrices[ia]);
    }

    static void doDelete() {
        int ia = askMatrix("ненужную");
        if (ia < 0) return;

        matrices[ia] = null;
        System.out.println("Матрица " + nameOf(ia) + " удалена.");
    }

    // ---------- РАБОТА С ХРАНИЛИЩЕМ ----------

    static int firstFree() {
        for (int i = 0; i < MAX_MATRICES; ++i) {
            if (matrices[i] == null) return i;
        }
        return -1;
    }

    static String saveResult(Matrix m, String description) {
        int slot = firstFree();
        if (slot < 0) {
            System.out.println("Хранилище заполнено — результат не сохранён (только показан).");
            return "(без имени)";
        }
        matrices[slot] = m;
        return nameOf(slot) + "  [" + description + "]";
    }

    static String listAvailable() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < MAX_MATRICES; ++i) {
            if (matrices[i] != null) {
                if (!first) sb.append(", ");
                sb.append(nameOf(i));
                first = false;
            }
        }
        return first ? "(пусто)" : sb.toString();
    }

    static int askMatrix(String hint) {
        System.out.print("Укажите " + hint + " матрицу (например, A, B, ...; 0 — отмена): ");
        String token = sc.nextLine().trim();
        if (token.equals("0")) return -1;

        int idx = indexOfName(token);
        if (idx < 0 || idx >= MAX_MATRICES) {
            System.out.println("Нет такой матрицы.");
            return -1;
        }
        if (matrices[idx] == null) {
            System.out.println("Матрица " + token + " не создана.");
            return -1;
        }
        return idx;
    }

    // ---------- ИМЕНА (0 -> A, 1 -> B, ..., 25 -> Z, 26 -> AA, ...) ----------

    static String nameOf(int index) {
        StringBuilder sb = new StringBuilder();
        int n = index;
        while (true) {
            sb.insert(0, (char) ('A' + n % 26));
            n = n / 26 - 1;
            if (n < 0) break;
        }
        return sb.toString();
    }

    static int indexOfName(String name) {
        int result = 0;
        for (int i = 0; i < name.length(); ++i) {
            char ch = Character.toUpperCase(name.charAt(i));
            if (ch < 'A' || ch > 'Z') return -1;
            result = result * 26 + (ch - 'A' + 1);
        }
        return result - 1;
    }

    static String symbolOf(String op) {
        if (op.equals("add")) return "+";
        if (op.equals("sub")) return "-";
        if (op.equals("mul")) return "*";
        if (op.equals("div")) return "/";
        return "?";
    }

    // ---------- ВЫВОД МАТРИЦЫ ----------

    static void printMatrix(Matrix m) {
        Matrix copy = copyOf(m);
        copy.Print();
    }

    static Matrix copyOf(Matrix src) {
        Matrix copy = new Matrix(src.row(), src.col());
        for (int i = 0; i < src.row(); ++i) {
            for (int j = 0; j < src.col(); ++j) {
                Complex v = src.get(i, j);
                copy.set(i, j, new Complex(v.GetRe(), v.GetIm()));
            }
        }
        return copy;
    }

    // ---------- ЧТЕНИЕ ЧИСЕЛ С ЗАЩИТОЙ ----------

    static int readInt() {
        while (true) {
            try {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Ожидалось целое число, повторите: ");
            }
        }
    }

    static double readDouble() {
        while (true) {
            try {
                String line = sc.nextLine().trim().replace(',', '.');
                if (line.isEmpty()) continue;
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Ожидалось число, повторите: ");
            }
        }
    }

    static double[] readComplexLine() {
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            // нормализуем разделители: запятые и точки-с-запятой -> пробел
            line = line.replace(',', ' ').replace(';', ' ');

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.print("Ожидались два числа (re im), повторите: ");
                continue;
            }
            try {
                // replace('.', '.') — на случай локали с запятой как десятичным разделителем,
                // но здесь запятая уже превращена в пробел, так что просто парсим
                double re = Double.parseDouble(parts[0]);
                double im = Double.parseDouble(parts[1]);
                return new double[] { re, im };
            } catch (NumberFormatException e) {
                System.out.print("Не удалось разобрать числа, повторите: ");
            }
        }
    }

}