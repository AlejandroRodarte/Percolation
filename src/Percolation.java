import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final WeightedQuickUnionUF qu;
    private final int n;
    private boolean[] status;

    public Percolation(int n) {

        if (n <= 0) throw new IllegalArgumentException();

        this.qu = new WeightedQuickUnionUF((n * n) + 2);
        this.n = n;
        this.status = new boolean[n * n];

        for (int i = 0; i < n * n; i++) {
            this.status[i] = false;
        }

    }

    public void open(int row, int col) {

        if (row < 1 || col < 1 || row > this.n || col > this.n) throw new IllegalArgumentException();

        if (!this.isOpen(row, col)) {

            int index = this.fromCoords(row, col);
            this.status[index - 1] = true;

            int[] neighbors = this.neighborIndexes(row, col);

            for (int neighbor : neighbors) {
                if (neighbor != -1 && this.validateRange(neighbor - 1) && this.status[neighbor - 1]) {
                    this.qu.union(index, neighbor);
                }
            }

            if (row == 1) {
                this.qu.union(0, index);
            }

            if (row == this.n) {
                this.qu.union((n * n) + 1, index);
            }

        }

    }

    public boolean isOpen(int row, int col) {
        if (row < 1 || col < 1 || row > this.n || col > this.n) throw new IllegalArgumentException();
        return this.status[this.fromCoords(row, col) - 1];
    }

    public boolean isFull(int row, int col) {
        int index = this.fromCoords(row, col);
        return isOpen(row, col) && (this.qu.find(0) == this.qu.find(index));
    }

    public int numberOfOpenSites() {

        int count = 0;

        for (boolean isOpen : this.status) {
            if (isOpen) {
                count++;
            }
        }

        return count;

    }

    public boolean percolates() {
        return this.qu.find(0) == this.qu.find((this.n * this.n) + 1);
    }

    public static void main(String[] args) {

        int n = 200;
        Percolation percolation = new Percolation(n);

//        percolation.open(1, 6);
//        percolation.open(2, 6);
//        percolation.open(3, 6);
//        percolation.open(4, 6);
//        percolation.open(5, 6);
//        percolation.open(5, 5);
//        percolation.open(4, 4);
//        percolation.open(3, 4);
//        percolation.open(2, 4);
//        percolation.open(2, 3);
//        percolation.open(2, 2);
//        System.out.println(percolation.isFull(2, 1));
//        percolation.open(2, 1);
//
//        System.out.println(percolation.isFull(2, 1));

        do {

            int row = StdRandom.uniform(1, n + 1);
            int col = StdRandom.uniform(1, n + 1);

            if (!percolation.isOpen(row, col)) {
                percolation.open(row, col);
            }

        } while (!percolation.percolates());

        int openSites = percolation.numberOfOpenSites();
        double vacancy = (double) openSites / (n * n);

        System.out.println("Total amount of sites: " + (n * n) + ".");
        System.out.println("Number of open sites: " + openSites + ".");
        System.out.println("Vacancy probability: " + vacancy + ".");

    }

    private int[] neighborIndexes(int row, int col) {

        int top = row == 1 ? -1 : this.fromCoords(row - 1, col);
        int right = col == this.n ? -1 : this.fromCoords(row, col + 1);
        int bottom = row == this.n ? -1 : this.fromCoords(row + 1, col);
        int left = col == 1 ? -1 : this.fromCoords(row, col - 1);

        return new int[]{ top, right, bottom, left };

    }

    private int fromCoords(int row, int col) {
        return ((row - 1) * this.n) + col;
    }

    private boolean validateRange(int index) {
        return index >= 0 && index <= (this.n * this.n) - 1;
    }

}
