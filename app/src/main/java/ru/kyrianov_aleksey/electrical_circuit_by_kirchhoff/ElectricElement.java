package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

public class ElectricElement {
    private boolean uzel; // то узел или элемент
    private char type; //N=пусто J=иточник тока E=источник ЭДС R=резистор X=узел
    private double I = 0; //Ток
    private double R = 0; //Сопротивление
    private double U = 0; //Напряжение
    private double J = 0;
    private double E = 0;//ЭДС или генер
    private int number = 0;
    private int picture = 0;

    private int x = 0;
    private int y = 0;

    private boolean up; // стороны подключения для узлов
    private boolean down;
    private boolean right;
    private boolean left;
    private boolean printed;

    public ElectricElement(char type, int y, int x, boolean printed, int picture) {
        this.type = type;
        this.picture = picture;
        this.x = x;
        this.y = y;
        this.printed = printed;
    }

    public ElectricElement(char type, double i, double r, double u, double j, double e, int picture, boolean up, boolean down, boolean right, boolean left) {
        this.type = type;
        I = i;
        R = r;
        U = u;
        J = j;
        E = e;
        this.picture = picture;
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
    }

    public double getE() {
        return E;
    }

    public void setE(double e) {
        E = e;
    }

    public double getJ() {
        return J;
    }

    public void setJ(double j) {
        J = j;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public char getType() {
        return type;
    }

    public void setUzel(boolean uzel) {
        this.uzel = uzel;
    }

    public void setType(char type) {
        this.type = type;
    }



    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }

    public double getI() {
        return I;
    }

    public void setI(double i) {
        this.I = i;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        this.R = r;
    }

    public double getU() {
        return U;
    }

    public void setU(double u) {
        this.U = u;
    }
}
