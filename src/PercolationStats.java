import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE = 1.96;

    private final double[] vacancies;
    private final int trials;

    public PercolationStats(int n, int trials) {

        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();

        this.trials = trials;
        this.vacancies = new double[trials];

        for (int i = 0; i < trials; i++) {
            this.vacancies[i] = this.performTrial(n);
        }

    }

    public double mean() {
        return StdStats.mean(this.vacancies);
    }

    public double stddev() {
        return StdStats.stddev(this.vacancies);
    }

    public double confidenceLo() {
        return this.mean() - ((CONFIDENCE * this.stddev()) / Math.sqrt(this.trials));
    }

    public double confidenceHi() {
        return this.mean() + ((CONFIDENCE * this.stddev()) / Math.sqrt(this.trials));
    }

    private double performTrial(int n) {

        Percolation percolation = new Percolation(n);

        do {

            int row = StdRandom.uniform(1, n + 1);
            int col = StdRandom.uniform(1, n + 1);

            if (!percolation.isOpen(row, col)) {
                percolation.open(row, col);
            }

        } while (!percolation.percolates());

        int openSites = percolation.numberOfOpenSites();
        double vacancy = (double) openSites / (n * n);

        return vacancy;

    }

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, trials);

        System.out.println("mean\t\t =" + stats.mean());
        System.out.println("stddev\t\t =" + stats.stddev());
        System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");

    }

}
