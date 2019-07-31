import java.io.PrintWriter;

public class LowerBoundTop implements Runnable {
    private int n;
    private ColEdge[] e;
    private PrintWriter writer;

    public LowerBoundTop(int n, ColEdge[] e, PrintWriter writer){
        this.n = n;
        this.e = e;
        this.writer = writer;
    }

    @Override
    public void run() {
        MaxClique ff = new MaxClique();
        ff.start(n, e, writer);
    }
}
