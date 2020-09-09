import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

// percolation model
public class Percolation {

    // quick union model
    private final WeightedQuickUnionUF qu;

    // grid size
    private final int n;

    // site status tracker (open = true; closed = false)
    private boolean[] status;

    // tracker that answers the question: is the open site connected to the top through a chain of open sites?
    private boolean[] top;

    // tracker that answers the question: is the open site connected to the bottom through a chain of open sites?
    private boolean[] bottom;

    // does the system percolate?
    private boolean percolates;

    // open site count for this percolation system
    private int count;

    // constructor
    public Percolation(int n) {

        // invalid n catch
        if (n <= 0) throw new IllegalArgumentException();

        // initialize quick union tree
        // i.e. 5-by-5 grid should create 5 * 5 (25) nodes
        this.qu = new WeightedQuickUnionUF(n * n);

        // store grid size
        this.n = n;

        // initialize bit trackers for each site
        // i.e. a 5-by-5 grid will require 25 sites to be tracked through booleans
        this.status = new boolean[n * n];
        this.top = new boolean[n * n];
        this.bottom = new boolean[n * n];

        // the system initially will NOT percolate
        this.percolates = false;

        // the system initially will have 0 open sites
        this.count = 0;

        // set all trackers to false (no open sites, no nodes can reach initially to either the top or bottom)
        for (int i = 0; i < n * n; i++) {
            this.status[i] = false;
            this.top[i] = false;
            this.bottom[i] = false;
        }

    }

    // test if site at (row, col) is open
    // (1, 1) is considered the upper-left node in the grid
    public boolean isOpen(int row, int col) {

        // row and col values should BOTH be between 1 and n
        if (row < 1 || col < 1 || row > this.n || col > this.n) throw new IllegalArgumentException();

        // get flattened index based on the row and column
        int index = this.fromCoords(row, col);

        // return the status boolean for that site
        return this.status[index];

    }

    // open site request at (row, col) in the grid
    // (1, 1) is considered the upper-left node in the grid
    public void open(int row, int col) {

        // row and col should be between 1 and n
        if (row < 1 || col < 1 || row > this.n || col > this.n) throw new IllegalArgumentException();

        // if the site is NOT open
        if (!this.isOpen(row, col)) {

            // get flattened index based on the row and column
            int index = this.fromCoords(row, col);

            // set the status bit for that site to true and increase the open site count
            this.status[index] = true;
            this.count++;

            // if the site is located at the top, the top bit tracker should also be set to true
            // (top sites can reach to the top since they are already there)
            if (row == 1) {
                this.top[index] = true;
            }

            // if the site is located at the bottom, the bottom tip tracker should also be set to true
            // (bottom sites can reach to the bottom since they are already there)
            if (row == this.n) {
                this.bottom[index] = true;
            }

            // get the "neighbor" (adjacent) sites' flattened indexes in relation to the site that was just opened
            int[] neighbors = this.neighborIndexes(row, col);

            // loop through each neighbor's site flattened index
            for (int neighbor : neighbors) {

                // if the index is valid, within range (i.e. a node at the top can NOT have a neighbor site upwards),
                // and such neighbor site is open
                if (neighbor != -1 && this.validateRange(neighbor) && this.status[neighbor]) {

                    // get the neighbor's root
                    int root = this.qu.find(neighbor);

                    // if the current site is anywhere in the middle layers (thus not connected to the top by default)
                    // AND the neighbor's root provides a path to reach the top, set the current site's top tracker bit
                    // also to true
                    if (!this.top[index] && this.top[root]) {
                        this.top[index] = true;
                    }

                    // if the current site is anywhere in the middle layers (thus not connected to the bottom by default)
                    // AND the neighbor's root provides a path to reach the bottom, set the current site's bottom tracker
                    // bit also to true
                    if (!this.bottom[index] && this.bottom[root]) {
                        this.bottom[index] = true;
                    }

                    // unite current site and neighbor site in the quick-union map
                    this.qu.union(index, neighbor);

                    // get the new root that is common for both the current and neighbor sites
                    // note: executing this.qu.find(neighbor) yields the same result
                    int newRoot = this.qu.find(index);

                    // update the new, common root's top and bottom tracker bits to the ones set in the current site
                    this.top[newRoot] = this.top[index];
                    this.bottom[newRoot] = this.bottom[index];

                }

            }

            // if a particular site has both the top and bottom tracker bits set, it means that it provides a path
            // for the system to percolate
            if (this.top[index] && this.bottom[index]) {
                this.percolates = true;
            }

        }

    }

    // isFull: is a particular open site at (row, col) able to reach the top through a path of open sites?
    public boolean isFull(int row, int col) {

        // get flattened index from coordinates
        int index = this.fromCoords(row, col);

        // get the root according to this flattened index from the QU tree
        int root = this.qu.find(index);

        // the site will be declared full if the site is open and if its root's top tracker bit is set to true
        return this.isOpen(row, col) && this.top[root];

    }

    // number of open sites: return count tracker
    public int numberOfOpenSites() {
        return this.count;
    }

    // system percolates? return the proper field
    public boolean percolates() {
        return this.percolates;
    }

    // test client with a 4096-by-4096 grid
    public static void main(String[] args) {

        int n = 4096;
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

        System.out.println("Total amount of sites: " + (n * n) + ".");
        System.out.println("Number of open sites: " + openSites + ".");
        System.out.println("Vacancy probability: " + vacancy + ".");

    }

    // get the neighbor sites' flattened indexes related to a site at (row, col)
    private int[] neighborIndexes(int row, int col) {

        // upper neighbor: only available for sites that are NOT in the first row (yield -1 if so);
        // get flattened index by offsetting row up by one level
        int up = row == 1 ? -1 : this.fromCoords(row - 1, col);

        // right neighbor: only available for sites that are NOT in the last column (yield -1 if so);
        // get flattened index by offsetting column right by one level
        int right = col == this.n ? -1 : this.fromCoords(row, col + 1);

        // downwards neighbor: only available for sites that are NOT in the last row (yield -1 if so);
        // get flattened index by offsetting row down by one level
        int down = row == this.n ? -1 : this.fromCoords(row + 1, col);

        // left neighbor: only available for sites that are NOT in the first column (yield -1 if so);
        // get flattened index by offsetting column left by one level
        int left = col == 1 ? -1 : this.fromCoords(row, col - 1);

        // return neighbor array
        return new int[]{ up, right, down, left };

    }

    // get flattened index given a (row, col) coordinate pair
    private int fromCoords(int row, int col) {
        // i.e. site located at (4, 3) in a 5-by-5 grid (n = 5) has its tracking data located at position 17
        // in the status/top/bottom uni-dimensional arrays (where 0 is the first element position in the array)
        // row = 4
        // col = 3
        // n = 5
        // flattened index = ((4 - 1) * 5) + (3 - 1) = (3 * 5) + 2 = 15 + 2 = 17
        // thus, access status[17], top[17] and bottom[17] to set/get associated information to this site
        return ((row - 1) * this.n) + (col - 1);
    }

    // validate flattened index range; should be between 0 and (n * n) - 1
    // i.e. a 5-by-5 matrix flattened is represented by an array from element 0 to element 24
    private boolean validateRange(int index) {
        return index >= 0 && index <= (this.n * this.n) - 1;
    }

}
