package algorithm;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class HoffmansLB implements Runnable {

    private ColEdge[] e;
    private int n;
    private int m;

    private int lowerBound;

    public HoffmansLB (int n, int m, ColEdge[] e) {
        this.n = n;
        this.e = e;
        this.m = m;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    @Override
    public void run() {
        try {
            double [][] adjacency = new double[n][n];
            for (int i = 0; i < m; i++) {
                adjacency[(e[i].u)-1][(e[i].v)-1] = 1;
                adjacency[(e[i].v)-1][(e[i].u)-1] = 1;
            }
            RealMatrix A = MatrixUtils.createRealMatrix(adjacency);
            EigenDecomposition eigen = new EigenDecomposition(A);

            double first = eigen.getRealEigenvalue(0);
            double last = eigen.getRealEigenvalue(n-1);

            lowerBound = (int) Math.ceil(1 - (first / last));
        } catch (Exception ex) {

        }
    }

}

