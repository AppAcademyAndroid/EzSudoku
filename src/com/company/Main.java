package com.company;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final ArrayList<Integer> ALL_POSSIBILITIES = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    private static int[][] grid;

    private static int[][] deepCopy(int[][] a) {
        if (a == null) {
            return null;
        }

        final int[][] result = new int[a.length][];
        for (int i = 0; i < a.length; ++i) {
            result[i] = Arrays.copyOf(a[i], a[i].length);
        }
        return result;
    }

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

    private static boolean validSudokuGrid(int[][] g, Boolean isFinalGrid) {
        Set<Integer> unique = new HashSet<>(9);

        // Check grid lines and validate numbers range
        int i, j;
        for (i = 0; i < 9; ++i) {
            unique.clear();
            for (j = 0; j < 9; ++j) {
                if (!isFinalGrid && g[i][j] == 0) {
                    continue;
                }

                if (g[i][j] < 1 || g[i][j] > 9) {
                    System.out.println("Invalid value " + g[i][j] + " at cell(" + i + ", " + j + ")");
                    return false;
                }

                if (!unique.add(g[i][j])) {
                    System.out.println("Invalid line " + i + ". Element " + g[i][j] + " is reoccurring at cell(" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        // Check grid columns
        for (i = 0; i < 9; ++i) {
            unique.clear();
            for (j = 0; j < 9; ++j) {
                if (!isFinalGrid && g[j][i] == 0) {
                    continue;
                }

                if (!unique.add(g[j][i])) {
                    System.out.println("Invalid column " + i + ". Element " + g[j][i] + " is reoccurring at cell(" + j + ", " + i + ")");
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
                        if (!isFinalGrid && g[i][j] == 0) {
                            continue;
                        }

                        if (!unique.add(g[i][j])) {
                            System.out.println("Invalid quadrant " + (r + (c / 3)));
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private static Point getNextCell(int[][] g) {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (g[i][j] == 0) {
                    return new Point(i, j);
                }
            }
        }

        return null;
    }

    private static ArrayList<Integer> getCellPossibilities(int[][] g, Point cell) {
        ArrayList<Integer> possibilities = new ArrayList<>(ALL_POSSIBILITIES);
        int i;

        // Check row
        for (i = 0; i < 9; ++i) {
            if (i != cell.y && g[cell.x][i] != 0) {
                possibilities.remove(Integer.valueOf(g[cell.x][i]));
            }
        }

        // Check column
        for (i = 0; i < 9; ++i) {
            if (i != cell.x && g[i][cell.y] != 0) {
                possibilities.remove(Integer.valueOf(g[i][cell.y]));
            }
        }

        // Check quadrant
        int r = (cell.x / 3) * 3;
        int c = (cell.y / 3) * 3;
        for (i = r; i < r + 3; ++i) {
            for (int j = c; j < c + 3; ++j) {
                if (!(i == cell.x && j == cell.y) && g[i][j] != 0) {
                    possibilities.remove(Integer.valueOf(g[i][j]));
                }
            }
        }

        return possibilities;
    }

    private static int[][] solve(int[][] g) {
        if (g == null) {
            return null;
        }

        Point c = getNextCell(g);
        if (c == null) {
            return validSudokuGrid(g, true) ? g : null;
        }

        int[][] r;
        ArrayList<Integer> p = getCellPossibilities(g, c);
        for (Integer i : p) {
            g[c.x][c.y] = i;
            r = solve(g);
            if (r != null) {
                return r;
            }
            g[c.x][c.y] = 0;
        }

        return null;
    }

    private static boolean solveSudoku() {
        if (!validSudokuGrid(grid, false)) {
            return false;
        }

        int[][] tmp = solve(deepCopy(grid));
        if (tmp != null) {
            grid = tmp;
            return true;
        }

        return false;
    }

    private static void printSudokuGrid(int[][] g) {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (j == 8) {
                    System.out.println(g[i][j]);
                } else {
                    System.out.print(g[i][j] + " ");
                }
            }
        }
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

        if (!solveSudoku()) {
            System.out.println("Impossible sudoku grid");
            return;
        }

        printSudokuGrid(grid);

        System.out.println("ez");
    }
}
