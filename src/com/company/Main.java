package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static int[][] grid;

    private static boolean loadGrid(String csvFilePath) {
        try {
            File csvFile = new File(csvFilePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));

            String line;
            String[] splitLine;
            int index = 0;

            grid = new int[9][];

            while (bufferedReader.ready() || index < 9) {
                line = bufferedReader.readLine() + ",0"; // hack
                splitLine = line.split(",");

                if (splitLine.length != 10) {
                    System.out.println("Invalid CSV format");
                    return false;
                }

                grid[index] = new int[9];
                for (int i = 0; i < 9; ++i) {
                    if (splitLine[i].isEmpty()) {
                        grid[index][i] = 0;
                    } else {
                        grid[index][i] = Integer.parseInt(splitLine[i]);
                    }
                }
                ++index;
            }

            if (index != 9) {
                System.out.println("Invalid CSV format");
                return false;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean validSudokuGrid(Boolean isFinalGrid) {
        Set<Integer> unique = new HashSet<>(9);

        // Check grid lines and validate numbers range
        int i, j;
        for (i = 0; i < 9; ++i) {
            unique.clear();
            for (j = 0; j < 9; ++j) {
                if (!isFinalGrid && grid[i][j] == 0) {
                    continue;
                }

                if (grid[i][j] < 1 || grid[i][j] > 9) {
                    System.out.println("Invalid value " + grid[i][j] + " at cell(" + i + ", " + j + ")");
                    return false;
                }

                if (!unique.add(grid[i][j])) {
                    System.out.println("Invalid line " + i + ". Element " + grid[i][j] + " is reoccurring at cell(" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        // Check grid columns
        for (i = 0; i < 9; ++i) {
            unique.clear();
            for (j = 0; j < 9; ++j) {
                if (!isFinalGrid && grid[j][i] == 0) {
                    continue;
                }

                if (!unique.add(grid[j][i])) {
                    System.out.println("Invalid column " + i + ". Element " + grid[j][i] + " is reoccurring at cell(" + j + ", " + i + ")");
                    return false;
                }
            }
        }

        // Check grid quadrants
        int mr, mc;
        for (int r = 0; r < 9; r += 3) {
            for (int c = 0; c < 9; c += 3) {
                unique.clear();
                mr = r + 3;
                mc = c + 3;
                for (i = r; i < mr; ++i) {
                    for (j = c; j < mc; ++j) {
                        if (!isFinalGrid && grid[i][j] == 0) {
                            continue;
                        }

                        if (!unique.add(grid[i][j])) {
                            System.out.println("Invalid quadrant " + (r + (c / 3)));
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing CSV file path");
            return;
        }

        if (args.length > 1) {
            System.out.println("Too many arguments, expecting only CSV file path");
            return;
        }

        if (!loadGrid(args[0])) {
            return;
        }

        System.out.println("Valid sudoku grid: " + validSudokuGrid(true));
    }
}
