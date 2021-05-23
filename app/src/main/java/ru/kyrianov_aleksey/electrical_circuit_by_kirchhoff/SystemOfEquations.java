package ru.kyrianov_aleksey.electrical_circuit_by_kirchhoff;

public class SystemOfEquations {
    private double u;

    public SystemOfEquations() {
    }

    public double[] solve(double[][] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            double maxabs = Math.abs(a[i][i]);
            int k = i;
            for (int l = i + 1; l < n; l++) {
                if (Math.abs(a[l][i]) > maxabs) {
                    maxabs = Math.abs(a[l][i]);
                    k = l;
                }
            }
            if (k != i) {
                for (int j = i; j < n + 1; j++) {
                    u = a[i][j];
                    a[i][j] = a[k][j];
                    a[k][j] = u;
                }
            }
            u = a[i][i];
            for (int j = i; j < n + 1; j++) {
                a[i][j] = (double) a[i][j] / u;

            }
            for (int l = i + 1; l < n; l++) {
                u=a[l][i];
                for (int j=i+1; j < n+1; j++){
                    a[l][j]=a[l][j]-a[i][j]*u;
                }
            }
        }
        double[] rez = new double[n];
        rez[n-1]=a[n-1][n+1-1];
        for (int i=n-1-1; i>=0; i--){
            rez[i]=a[i][n+1-1];
            for(int j=i+1; j<n;j++){
                rez[i]=rez[i]-a[i][j]*rez[j];
            }
        }



        return (rez);
    }


}