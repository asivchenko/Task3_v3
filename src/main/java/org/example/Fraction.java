package org.example;
import org.example.Cache;
import org.example.Mutator;
public class Fraction  implements Fractionable {
    private int num;
    private int denum;
    public int count=0;


    public Fraction(int num, int denum) {
        this.num = num;
        this.denum = denum;
    }

    @Override
    @Mutator
    public void setNum(int num) {
        this.num = num;
    }


    @Override
    @Mutator
    public void setDenum(int denum) {
        this.denum = denum;
    }

    @Override
    @Cache(5000)
    public double doubleValue() {
        count++;    // счетчик наличие срабатываения подтверждает что работает операция работы метода
        //  System.out.println("Вызов из doublevalue результат=" + (double) num / denum);
        return (double) num / denum;
    }
    /*   мне кажется достаточного счетчика count при вызове метода cache
    @Override
    public String toString () {
        count++;
        return "Count="+ count + "num =" + num + " denum=" + denum;
    }
    */
}