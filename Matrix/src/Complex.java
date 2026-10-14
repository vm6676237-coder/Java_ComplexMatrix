public class Complex {

    private double re;
    private double im;

    public Complex () { re = 0; im = 0; }
    public Complex (double re) {this.re = re; im = 0;}
    public Complex (double re, double im) {this.re = re; this.im = im;}

    // АРИФМЕТИКА

    public Complex add(Complex other){
        return new Complex(this.re + other.re, this.im + other.im);
    }

    public Complex sub(Complex other){
        return new Complex(this.re - other.re, this.im - other.im);
    }

    public Complex mul(Complex other){
        return new Complex(this.re * other.re - this.im * other.im, this.re * other.im + this.im * other.re);
    }

    public Complex div(Complex other){
        if (other.re * other.re + other.im * other.im == 0) throw new ArithmeticException("Деление на ноль");
        return new Complex((this.re * other.re + this.im * other.im) / (other.re * other.re + other.im * other.im), (this.im * other.re - this.re * other.im) / (other.re * other.re + other.im * other.im));
    }

    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ

    public double GetRe (){ return re; }
    public double GetIm (){ return im; }

    public void SetRe (double x){ re = x; }
    public void SetIm (double x){ im = x; }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    public Complex negate(){
        return new Complex(-this.re, -this.im);
    }

    public double abs(){
        return Math.hypot(this.re, this.im);
    }

    public boolean isZero(){
        return this.re == 0 && this.im == 0;
    }

    // ВЫВОД КОМПЛЕКСНОГО ЧИСЛА

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
        if ((int)(re * 100000 * 100000) == 0) re = 0.0;
        if ((int)(im * 100000 * 100000) == 0) im = 0.0;

        if ((int)((re * 100000 - (int)(re * 100000)) * 100000) == 0) re = Math.round(re * kol((int)(re * 100000))) / kol((int)(re * 100000));
        if ((int)((im * 100000 - (int)(im * 100000)) * 100000) == 0) im = Math.round(im * kol((int)(im * 100000))) / kol((int)(im * 100000));

        if (im >= 0) System.out.print(re + "+" + im + "i");
        else {System.out.print(re); System.out.print(im + "i");}
    }

    public String toString() {
        if (im >= 0) return re + "+" + im + "i";
        else return re + "-" + Math.abs(im) + "i";
    }


}
