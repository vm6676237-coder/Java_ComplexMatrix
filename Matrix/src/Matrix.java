public class Matrix {
    private Complex[][] matrix;
    private int col;
    private int row;

    // КОНСТРУКТОРЫ

    public Matrix (){
        this.row = 0;
        this.col = 0;
        matrix = null;
    }

    public Matrix (int row, int col){
        this.row = row;
        this.col = col;
        this.matrix = new Complex[row][col];
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j)
                matrix[i][j] = new Complex(0, 0);
    }

    public Matrix (Complex[][] other){
        row = other.length;
        col = other[0].length;
        matrix = new Complex[row][col];
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j)
                matrix[i][j] = other[i][j];

    }

    public Matrix (Matrix other){
        row = other.row;
        col = other.col;
        matrix = new Complex[row][col];
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j)
                matrix[i][j] = other.matrix[i][j];

    }

    // ДОСТУПЫ и ИЗМЕНЕНИЯ

    private void CheckBounds(int r, int c){
        if (r < 0 || r >= row || c < 0 || c >= col) throw new IndexOutOfBoundsException("Неверный ввод строки или столбца аргумента функции");
    }
    private void RequireSameSize(Matrix other){
        if (row != other.row || col != other.col) throw new IllegalArgumentException("Размеры матриц не совпадают");
    }

    public int row() {return row;}
    public int col() {return col;}

    public Complex get(int r, int c) {
        CheckBounds(r, c);
        return matrix[r][c];
    }
    public void set(int r, int c, Complex other) {
        CheckBounds(r, c);
        if (other == null) throw new NullPointerException("Комплексное число не задано");
        matrix[r][c] = other;
    }

    public boolean IsSquare(){ return row == col; }

    private Complex[][] CopyData() {
        Complex[][] copy = new Complex[row][col];
        for (int i = 0; i < row; ++i)
            copy[i] = matrix[i].clone();
        return copy;
    }

    private static void SwapRows(Complex[][] other, int i, int j) {
        Complex[] help = other[i];
        other[i] = other[j];
        other[j] = help;
    }



    // АРИФМЕТИЧЕСКИЕ ОПЕРАЦИИ НАД МАТРИЦАМИ

    public Matrix add(Matrix other){
        RequireSameSize(other);
        Matrix help = new Matrix(row, col);
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j)
                help.matrix[i][j] = matrix[i][j].add(other.matrix[i][j]);

        return help;
    }

    public Matrix sub(Matrix other){
        RequireSameSize(other);
        Matrix help = new Matrix(row, col);
        for (int i = 0; i < row; ++i)
            for (int j = 0; j < col; ++j)
                help.matrix[i][j] = matrix[i][j].sub(other.matrix[i][j]);

        return help;
    }

    public Matrix mul(Matrix other){
        if (col != other.row) throw new IllegalArgumentException("Длина строка первой матрицы не равна длине столбца второй матрицы");

        Matrix help = new Matrix(row, other.col);
        for (int i = 0; i < row; ++i){
            for (int j = 0; j < other.col; ++j){
                Complex help1 = new Complex();
                for (int k = 0; k < col; ++k)
                    help1 = help1.add(matrix[i][k].mul(other.matrix[k][j]));
                help.matrix[i][j] = help.matrix[i][j].add(help1);
            }
        }

        return help;
    }

    public Matrix mul(Complex other){
        Matrix help = new Matrix(row, col);
        for (int i = 0; i < row; ++i){
            for (int j = 0; j < col; ++j){
                help.matrix[i][j] = other.mul(matrix[i][j]);
            }
        }

        return help;
    }

    // ОПЕРАЦИИ НАД МАТРИЦАМИ

    public Matrix transpose() {
        Matrix help = new Matrix(col, row);
        for (int i = 0; i < row; ++i){
            for (int j = 0; j < col; ++j){
                help.matrix[j][i] = matrix[i][j];
            }
        }
        return help;
    }

    public Complex determinant() {
        if (!(IsSquare())) throw new IllegalStateException("Невозможно определить детерминант. Матрица не является квадратной");
        if (row == 0) return new Complex(1, 0);

        Complex[][] help = CopyData();
        Complex det = new Complex(1, 0);

        for (int numb = 0; numb < row; ++numb){
            int ucazatel = numb;
            double BestAbs = help[numb][numb].abs();
            for (int i = numb + 1; i < row; ++i){
                double a = help[i][numb].abs();
                if (a > BestAbs){
                    BestAbs = a;
                    ucazatel = i;
                }
            }

            if (BestAbs == 0.0) return new Complex(0, 0);

            if (ucazatel != numb) {
                SwapRows(help, ucazatel, numb);
                //for (int i = 0; i < numb - ucazatel; ++i)
                    det = det.negate();
            }

            Complex diogonal = help[numb][numb];
            det = det.mul(diogonal);

            for (int i = numb + 1; i < row; ++i){
                if (help[i][numb].isZero()) continue;
                Complex mnogitel = help[i][numb].div(diogonal);
                for (int j = numb; j < row; ++j){
                    help[i][j] = help[i][j].sub(mnogitel.mul(help[numb][j]));
                }
            }


        }

        return det;

    }

    public Matrix inverse() {
        if (!IsSquare())
            throw new IllegalStateException("Обратная матрица существует только для квадратной");

        int n = row;
        Complex[][] aug = new Complex[n][2 * n];


        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j)
                aug[i][j] = matrix[i][j];
            for (int j = 0; j < n; ++j)
                aug[i][n + j] = (i == j) ? new Complex(1, 0) : new Complex(0, 0);
        }


        for (int k = 0; k < n; ++k) {
            // выбор ведущего элемента
            int pivot = k;
            double bestAbs = aug[k][k].abs();
            for (int i = k + 1; i < n; ++i) {
                double a = aug[i][k].abs();
                if (a > bestAbs) {
                    bestAbs = a;
                    pivot = i;
                }
            }

            if (bestAbs == 0.0)
                throw new ArithmeticException("Матрица вырождена, обратной не существует");

            if (pivot != k) SwapRows(aug, pivot, k);


            Complex pivotVal = aug[k][k];
            Complex inv = new Complex(1, 0).div(pivotVal);
            for (int j = 0; j < 2 * n; ++j)
                aug[k][j] = aug[k][j].mul(inv);


            for (int i = 0; i < n; ++i) {
                if (i == k) continue;
                Complex factor = aug[i][k];
                if (factor.isZero()) continue;
                for (int j = 0; j < 2 * n; ++j)
                    aug[i][j] = aug[i][j].sub(factor.mul(aug[k][j]));
            }
        }


        Matrix result = new Matrix(n, n);
        for (int i = 0; i < n; ++i)
            for (int j = 0; j < n; ++j)
                result.matrix[i][j] = aug[i][n + j];

        return result;
    }

    public Matrix div(Matrix other){
        if (this.col != other.row) throw new IllegalArgumentException("Деление выполнить не удается. Длины строки первой и столбца второй матриц не равны");
        if (!(other.IsSquare())) throw new IllegalArgumentException("Вторая матрица не является квадратной");

        return  this.mul(other.inverse());
    }


    // ВЫВОД МАТРИЦЫ

    private double kol(int help){
        int k = 0;
        for (int i = 1; i < 6; ++i)
            if (help % 10 == 0) { k += 1; help = help / 10; }
            else break;
        double res = 1.0;
        for (int i = 0; i < 5-k; ++i)
            res *= 10.0;
        return res;
    }

    public void Print(){
        for (int i = 0; i < matrix.length; ++i){
            for (int j = 0; j < matrix[0].length; ++j){
                if ((int)(matrix[i][j].GetRe() * 100000 * 100000) == 0) matrix[i][j].SetRe(0.0);
                if ((int)(matrix[i][j].GetIm() * 100000 * 100000) == 0) matrix[i][j].SetIm(0.0);

                if ((int)((matrix[i][j].GetRe() * 100000 - (int)(matrix[i][j].GetRe() * 100000)) * 100000) == 0) matrix[i][j].SetRe(Math.round(matrix[i][j].GetRe() * kol((int)(matrix[i][j].GetRe() * 100000))) / kol((int)(matrix[i][j].GetRe() * 100000)));
                if ((int)((matrix[i][j].GetIm() * 100000 - (int)(matrix[i][j].GetIm() * 100000)) * 100000) == 0) matrix[i][j].SetIm(Math.round(matrix[i][j].GetIm() * kol((int)(matrix[i][j].GetIm() * 100000))) / kol((int)(matrix[i][j].GetIm() * 100000)));

                if (matrix[i][j].GetIm() >= 0) System.out.print(matrix[i][j].GetRe() + "+" + matrix[i][j].GetIm() + "i" + " ");
                else { System.out.print(matrix[i][j].GetRe()); System.out.print(matrix[i][j].GetIm() + "i" + " ");}
            }
            System.out.println();
        }
    }


    public String toString(){
        String result = "";
        for (int i = 0; i < row; ++i){
            for (int j = 0; j < col; ++j){
                result += matrix[i][j] + "\t";
            }
            result += "\n";
        }
        return result;
    }





}
