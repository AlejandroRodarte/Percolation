import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

// percolation stats utility class
public class PercolationStats {

    // confidence constant value (95%)
    private static final double CONFIDENCE = 1.96;

    // vacancy probability array for each trial test result
    private final double[] vacancies;

    // amount of trials
    private final int trials;

    // constructor
    public PercolationStats(int n, int trials) {

        // valid grid size and trial amount
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();

        // set trials and vacancies array
        this.trials = trials;
        this.vacancies = new double[trials];

        // for each trial, perform it and store its return value in the corresponding array element
        for (int i = 0; i < trials; i++) {
            this.vacancies[i] = this.performTrial(n);
        }

    }

    // get mean of vacancy probabilities
    public double mean() {
        return StdStats.mean(this.vacancies);
    }

    // get standard deviation of vacancy probabilities
    public double stddev() {
        return StdStats.stddev(this.vacancies);
    }

    // get 95% confidence vacancy probability's lower end value
    public double confidenceLo() {
        return this.mean() - ((CONFIDENCE * this.stddev()) / Math.sqrt(this.trials));
    }

    // get 95% confidence vacancy probability's higher end value
    public double confidenceHi() {
        return this.mean() + ((CONFIDENCE * this.stddev()) / Math.sqrt(this.trials));
    }

    // test client
    public static void main(String[] args) {

        // get from command line grid size and trials
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        // create stats object
        PercolationStats stats = new PercolationStats(n, trials);

        // print statistical results
        System.out.println("mean\t\t\t\t    = " + stats.mean());
        System.out.println("stddev\t\t\t\t    = " + stats.stddev());
        System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");

    }

    // trial execution method
    private double performTrial(int n) {

        // create brand new percolation instance
        Percolation percolation = new Percolation(n);

        // execute this while the system is not percolating
        do {

            // generate random (row, col) value
            int row = StdRandom.uniform(1, n + 1);
            int col = StdRandom.uniform(1, n + 1);

            // if site at (row, col) is not open, then open
            if (!percolation.isOpen(row, col)) {
                percolation.open(row, col);
            }

        } while (!percolation.percolates());

        // system percolates: get number of opened sites for this trial and divide it by the amount of sites
        int openSites = percolation.numberOfOpenSites();
        double vacancy = (double) openSites / (n * n);

        // return result
        return vacancy;

    }

}
