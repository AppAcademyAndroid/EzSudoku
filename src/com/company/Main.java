package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {

    private static int[][] table;

    private static boolean loadTable(String csvFilePath) {
        try {
            File csvFile = new File(csvFilePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));

            String line;
            String[] splitLine;
            int index = 0;

            table = new int[9][];

            while (bufferedReader.ready() || index < 9) {
                line = bufferedReader.readLine() + ",0"; // hack
                splitLine = line.split(",");

                if (splitLine.length != 10) {
                    System.out.println("Invalid CSV format");
                    return false;
                }

                table[index] = new int[9];
                for (int i = 0; i < 9; ++i) {
                    if (splitLine[i].isEmpty()) {
                        table[index][i] = 0;
                    } else {
                        table[index][i] = Integer.parseInt(splitLine[i]);
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

    // TODO incomplete
    private static boolean validSudokuTable() {
        int sum;
        ArrayList<Integer> unique = new ArrayList<>(9);

        for (int i = 0; i < 9; ++i) {
            sum = 0;

            for (int j = 0; j < 9; ++j) {
                sum += table[i][j];
            }

            if (sum != 45) {
                System.out.println("Invalid sum at row " + (i + 1) + ": " + sum);
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("Missing CSV file path");
            return;
        }

        if (args.length > 1) {
            System.out.print("Too many arguments, expecting only CSV file path");
            return;
        }

        if (!loadTable(args[0])) {
            return;
        }

        System.out.println("Valid sudoku table: " + validSudokuTable());
    }
}
