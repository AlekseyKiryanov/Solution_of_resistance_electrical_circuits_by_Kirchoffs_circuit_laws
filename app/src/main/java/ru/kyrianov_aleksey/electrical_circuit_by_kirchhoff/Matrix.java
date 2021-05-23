package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

public class Matrix {

    public double[][] matrixMultiplication(double A[][], double B[][]) {

        int n = A.length;
        int m = B[0].length;
        int l = B.length;

        double[][] rez = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < l; k++) {
                    rez[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return (rez);
    }

    public double[][] matrixSummation(double A[][], double B[][]) {

        int n = A.length;
        int m = A[0].length;


        double[][] rez = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                rez[i][j] = A[i][j] + B[i][j];
            }
        }

        return (rez);
    }

    public double[] vectorSummation(double A[], double B[]) {

        int n = A.length;


        double[] rez = new double[n];

        for (int i = 0; i < n; i++) {

               rez[i] = A[i] + B[i];

        }

        return (rez);
    }

    public double[][] matrixSubtraction(double A[][], double B[][]) {

        int n = A.length;
        int m = A[0].length;


        double[][] rez = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                rez[i][j] = A[i][j] - B[i][j];
            }
        }

        return (rez);
    }

    public double[] vectorSubtraction(double A[], double B[]) {

        int n = A.length;



        double[] rez = new double[n];

        for (int i = 0; i < n; i++) {

                rez[i] = A[i] - B[i];

        }

        return (rez);
    }

    public double[] matrixAndVectorMultiplication(double A[][], double B[]) {

        int n = A.length;
        int l = B.length;

        double[] rez = new double[n];

        for (int i = 0; i < n; i++) {

            for (int k = 0; k < l; k++) {
                rez[i] += A[i][k] * B[k];
            }

        }

        return (rez);
    }

    public double[][] matrixTransposition(double A[][]) {

        int n = A.length;
        int m = A[0].length;


        double[][] rez = new double[m][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                rez[j][i] = A[i][j];
            }
        }

        return (rez);


    }

    public double[][] matrixAndVectorCombinationForGaus(double A[][], double B[]) {

        int n = A.length;
        int m = A[0].length;


        double[][] rez = new double[n][m + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                rez[i][j] = 1*A[i][j];
            }
        }

        for (int j = 0; j < n; j++) {
            rez[j][m] =1*B[j];
        }

        return (rez);
    }


}
