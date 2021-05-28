package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

import android.util.Log;

import java.util.ArrayList;

public class ElectricalCircuit {
    private int H = 20;
    private int W = 20;
    ElectricElement[][] real_matrix;
    private ArrayList<Coord> uzels;
    private ArrayList<Coord> elements;

    private static final String TAG = "MyApp";


    public ElectricalCircuit(int h, int w) {
        this.W = w;
        this.H = h;
        this.real_matrix = new ElectricElement[H][W];
        this.uzels = new ArrayList<>();
        this.elements = new ArrayList<>();

        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {

                real_matrix[i][j] = new ElectricElement('N', i, j, false, 0);
            }
        }
    }

    private int[][] big_matrix_incedenciy;
    private double[][] matrix_incedenciy;
    private double[][] matrix_conductivity;
    private double[] R_matrix;
    private double[] E_matrix;
    private double[] J_matrix;
    private int real_length;

    public void setElement(ElectricElement e, int y, int x) {
        real_matrix[y][x] = e;
    }

    public void solve() {
        // for (int i=0; i<uzels.size(); i++){
        //     real_matrix[uzels.get(i).getY()][uzels.get(i).getX()].setPrinted(false);
        // }
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                real_matrix[i][j].setPrinted(false);
            }
        }
        Log.i(TAG, String.valueOf(uzels.size()) + " " + String.valueOf(elements.size()));
        Matrix matrix = new Matrix();
        SystemOfEquations abc = new SystemOfEquations();
        makeBigMatrixIncedenciy();
        //printLogArrayA(big_matrix_incedenciy);
        makeMatrixIncedenciy();
        Log.i(TAG, "Big_matrix_Icedenciy");
        printLogArrayA(big_matrix_incedenciy);
        Log.i(TAG, "Matrix_Icedenciy");
        printLogArrayB(matrix_incedenciy);
        makeEMatrix();
        Log.i(TAG, "E_vector");
        printLogArrayC(E_matrix);
        makeRMatrix();
        Log.i(TAG, "R_vector");
        printLogArrayC(R_matrix);
        makeJMatrix();
        Log.i(TAG, "J_vector");
        printLogArrayC(J_matrix);
        makeMatrixConductivity();
        Log.i(TAG, "Conductivity");
        printLogArrayB(matrix_conductivity);
        Log.i(TAG, "Solve");
        double[][] T;
        T = matrix.matrixTransposition(matrix_incedenciy);
        printLogArrayB(T);

        double[][] W;
        W = matrix.matrixMultiplication(matrix_conductivity, T);
        printLogArrayB(W);

        double[][] D;
        D = matrix.matrixMultiplication(matrix_incedenciy, W);
        printLogArrayB(D);

        double[] B;
        B = matrix.matrixAndVectorMultiplication(matrix_incedenciy, matrix.vectorSubtraction(J_matrix, matrix.matrixAndVectorMultiplication(matrix_conductivity, E_matrix)));
        printLogArrayC(B);

        Log.i(TAG, "Gauss");
        double[] V;
        V = abc.solve(matrix.matrixAndVectorCombinationForGaus(D, B));
        printLogArrayC(V);

        double[] V1;
        V1 = matrix.vectorSummation(matrix.matrixAndVectorMultiplication(T, V), E_matrix);
        printLogArrayC(V1);
        double[] I1;
        I1 = matrix.matrixAndVectorMultiplication(matrix_conductivity, V1);
        printLogArrayC(I1);


        for (int b = 0; b < elements.size(); b++) {
            setIElement(I1[b], elements.get(b).getY(), elements.get(b).getX());
        }
        for (int b = 0; b < elements.size(); b++) {
            setUElement(V1[b], elements.get(b).getY(), elements.get(b).getX());
        }
    }


    private void makeBigMatrixIncedenciy() {
        big_matrix_incedenciy = new int[elements.size()][elements.size()];
        int k = 0;
        int i = 0;

        for (int a = 0; a < elements.size(); a++) {
            for (int b = 0; b < elements.size(); b++) {
                big_matrix_incedenciy[a][b] = 0;
            }
        }
        while (k < uzels.size()) {
            Log.i(TAG, String.valueOf(k) + " " + String.valueOf(i));
            while ((k < uzels.size()) && (isPrintedElement(uzels.get(k).getY(), uzels.get(k).getX()) == true)) {
                k++;
            }
            Log.i(TAG, String.valueOf(k) + " " + String.valueOf(i));
            if (k < uzels.size()) {
                printInMatrixIncedenciy(uzels.get(k).getY(), uzels.get(k).getX(), i);
                k++;
                i++;
            }

        }
        real_length = i - 1;
    }

    private void makeMatrixIncedenciy() {
        matrix_incedenciy = new double[real_length][elements.size()];
        for (int aaaa = 0; aaaa < matrix_incedenciy.length; aaaa++) {
            for (int bbbb = 0; bbbb < matrix_incedenciy[0].length; bbbb++) {
                matrix_incedenciy[aaaa][bbbb] = (double) big_matrix_incedenciy[aaaa][bbbb];
            }
        }
    }


    private void printLogArrayA(int[][] A) {
        String s = "";
        for (int a = 0; a < A.length; a++) {
            for (int b = 0; b < A[0].length; b++) {
                s = s + String.valueOf(A[a][b]) + " ";
            }
            Log.i(TAG, s);
            s = "";
        }
    }

    private void printLogArrayB(double[][] A) {
        String s = "";
        for (int a = 0; a < A.length; a++) {
            for (int b = 0; b < A[0].length; b++) {
                s = s + String.valueOf(A[a][b]) + " ";
            }
            Log.i(TAG, s);

            s = "";
        }
    }

    private void printLogArrayC(double[] A) {
        String s = "";

        for (int b = 0; b < A.length; b++) {
            s = s + String.valueOf(A[b]) + " ";


        }
        Log.i(TAG, s);
        s = "";
    }


    private void makeRMatrix() {
        R_matrix = new double[elements.size()];
        for (int b = 0; b < elements.size(); b++) {
            R_matrix[b] = getRElement(elements.get(b).getY(), elements.get(b).getX());
        }
    }


    private void makeEMatrix() {
        E_matrix = new double[elements.size()];
        for (int b = 0; b < elements.size(); b++) {
            E_matrix[b] = getEElement(elements.get(b).getY(), elements.get(b).getX());
        }
    }


    private void makeJMatrix() {
        J_matrix = new double[elements.size()];
        for (int b = 0; b < elements.size(); b++) {
            J_matrix[b] = getJElement(elements.get(b).getY(), elements.get(b).getX());
        }
    }


    private void makeMatrixConductivity() {
        matrix_conductivity = new double[elements.size()][elements.size()];
        for (int a = 0; a < elements.size(); a++) {
            for (int b = 0; b < elements.size(); b++) {
                matrix_conductivity[a][b] = 0;
            }
        }
        for (int b = 0; b < elements.size(); b++) {
            matrix_conductivity[b][b] = (double) (1 / R_matrix[b]);
        }
    }


    private void printInMatrixIncedenciy(int y, int x, int i) {
        setPrintedElement(true, y, x);
        if ((y - 1 >= 0) && ((giveTypeElement(y - 1, x) == 'R') || (giveTypeElement(y - 1, x) == 'J') || (giveTypeElement(y - 1, x) == 'E'))) {
            if (isDownElement(y - 1, x)) {
                big_matrix_incedenciy[i][getNumberElement(y - 1, x)] = -1;
            } else {
                big_matrix_incedenciy[i][getNumberElement(y - 1, x)] = 1;
            }
        } else if ((y - 1 >= 0) && ((giveTypeElement(y - 1, x) == 'X') && (isPrintedElement(y - 1, x)) == false)) {
            printInMatrixIncedenciy(y - 1, x, i);
        }

        if ((x - 1 >= 0) && ((giveTypeElement(y, x - 1) == 'R') || (giveTypeElement(y, x - 1) == 'J') || (giveTypeElement(y, x - 1) == 'E'))) {
            if (isRightElement(y, x - 1)) {
                big_matrix_incedenciy[i][getNumberElement(y, x - 1)] = -1;
            } else {
                big_matrix_incedenciy[i][getNumberElement(y, x - 1)] = 1;
            }
        } else if ((x - 1 >= 0) && ((giveTypeElement(y, x - 1) == 'X') && (isPrintedElement(y, x - 1)) == false)) {
            printInMatrixIncedenciy(y, x - 1, i);
        }
        if ((y + 1 < H) && ((giveTypeElement(y + 1, x) == 'R') || (giveTypeElement(y + 1, x) == 'J') || (giveTypeElement(y + 1, x) == 'E'))) {
            if (isUpElement(y + 1, x)) {
                big_matrix_incedenciy[i][getNumberElement(y + 1, x)] = -1;
            } else {
                big_matrix_incedenciy[i][getNumberElement(y + 1, x)] = 1;
            }
        } else if ((y + 1 < H) && ((giveTypeElement(y + 1, x) == 'X') && (isPrintedElement(y + 1, x)) == false)) {
            printInMatrixIncedenciy(y + 1, x, i);
        }
        if ((x + 1 < W) && ((giveTypeElement(y, x + 1) == 'R') || (giveTypeElement(y, x + 1) == 'J') || (giveTypeElement(y, x + 1) == 'E'))) {
            if (isLeftElement(y, x + 1)) {
                big_matrix_incedenciy[i][getNumberElement(y, x + 1)] = -1;
            } else {
                big_matrix_incedenciy[i][getNumberElement(y, x + 1)] = 1;
            }
        } else if ((x + 1 < W) && ((giveTypeElement(y, x + 1) == 'X') && (isPrintedElement(y, x + 1)) == false)) {
            printInMatrixIncedenciy(y, x + 1, i);
        }
    }


    public void addElement(int y, int x) {
        elements.add(new Coord(y, x));
    }

    public void dellElement(int n) {
        elements.remove(n);
    }


    public void resetNumbersElement() {
        for (int i = 0; i < elements.size(); i++) {
            setNumberElement(i, elements.get(i).getY(), elements.get(i).getX());
        }
    }

    public void dellUsel(int y, int x) {
        for (int i = 0; i < uzels.size(); i++) {
            if ((uzels.get(i).getY() == y) && (x == uzels.get(i).getX())) {
                uzels.remove(i);
                break;
            }
        }

    }

    public void addUzel(int y, int x) {
        uzels.add(new Coord(y, x));
    }


    public char giveTypeElement(int y, int x) {
        return real_matrix[y][x].getType();
    }

    public void setTypeElement(char t, int y, int x) {
        real_matrix[y][x].setType(t);
    }

    public void setUpElement(boolean t, int y, int x) {
        real_matrix[y][x].setUp(t);
    }

    public void setDownElement(boolean t, int y, int x) {
        real_matrix[y][x].setDown(t);
    }

    public void setRightElement(boolean t, int y, int x) {
        real_matrix[y][x].setRight(t);
    }

    public void setLeftElement(boolean t, int y, int x) {
        real_matrix[y][x].setLeft(t);
    }

    public boolean isUpElement(int y, int x) {
        return real_matrix[y][x].isUp();
    }

    public boolean isDownElement(int y, int x) {
        return real_matrix[y][x].isDown();
    }

    public boolean isLeftElement(int y, int x) {
        return real_matrix[y][x].isLeft();
    }

    public boolean isRightElement(int y, int x) {
        return real_matrix[y][x].isRight();
    }

    public boolean isPrintedElement(int y, int x) {
        return real_matrix[y][x].isPrinted();
    }

    public void setPrintedElement(boolean t, int y, int x) {
        real_matrix[y][x].setPrinted(t);
    }


    public void setRElement(double R, int y, int x) {
        real_matrix[y][x].setR(R);
    }

    public void setEElement(double R, int y, int x) {
        real_matrix[y][x].setE(R);
    }

    public void setJElement(double R, int y, int x) {
        real_matrix[y][x].setJ(R);
    }

    public void setUElement(double R, int y, int x) {
        real_matrix[y][x].setU(R);
    }

    public void setIElement(double R, int y, int x) {
        real_matrix[y][x].setI(R);
    }

    public void setNumberElement(int R, int y, int x) {
        real_matrix[y][x].setNumber(R);
    }

    public double getRElement(int y, int x) {
        return real_matrix[y][x].getR();
    }


    public void setPicturElement(int R, int y, int x) {
        real_matrix[y][x].setPicture(R);
    }

    public int getPictureElement(int y, int x) {
        return real_matrix[y][x].getPicture();
    }

    public double getEElement(int y, int x) {
        return real_matrix[y][x].getE();
    }

    public double getJElement(int y, int x) {
        return real_matrix[y][x].getJ();
    }


    public double getUElement(int y, int x) {
        return real_matrix[y][x].getU();
    }

    public double getIElement(int y, int x) {
        return real_matrix[y][x].getI();
    }

    public int getNumberElement(int y, int x) {
        return real_matrix[y][x].getNumber();
    }

}

