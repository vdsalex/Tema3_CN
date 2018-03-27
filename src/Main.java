import Jama.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static double eps;

    private static class RareElement
    {
        double value;
        int index;

        @Override
        public String toString()
        {
            return "(" + value + ", " + index + ")";
        }
    }

    public static void main(String args[])
    {
        eps = Math.pow(10, -7);

        double[][] matrix1 = {
                {102.5, 0, 2.5, 0, 9},
                {3.5, 104.88, 1.05, 0, 0.33},
                {0, 0, 100, 0, 0},
                {0, 1.3, 0, 101.3, 0},
                {0.73, 0, 0, 1.5, 102.23}
        };

        double[][] matrix2 = {
                {3.0, 1.0, 0, 2.0, 0},
                {3.5, 105, 0, 21, 0},
                {1.2, 51.3, 2.1, -5.1, 0},
                {0, 0, 4.4, 7.7, 0},
                {-1.9, 0, 31, 0, 13}
        };

        Matrix A = new Matrix(matrix1);
        Matrix B = new Matrix(matrix2);

        RareElement[][] rareA = memorizeRareMatrix(A);
        RareElement[][] rareB = memorizeRareMatrix(B);

        Matrix rareAPlusRareB = addRareMatrices(rareA, rareB);
    }

    private static Matrix addRareMatrices(RareElement[][] X, RareElement[][] Y)
    {
        int m = getColumnDimension(X);
        int m2 = getColumnDimension(Y);
        int i, j, k, k2, l1, l2;

        if(X.length != Y.length || m != m2)
        {
            try
            {
                throw new Exception("The two matrices must have the same dimensions!");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Matrix result = new Matrix(X.length, m);

        for(i = 0; i < X.length; i++)
        {
            l1 = X[i].length;
            k = 0;
            j = 0;

            // Las adunarea dintre elementele de pe diagonala la sfarsit..
            while(j < l1 - 1)
            {
                l2 = Y[i].length;

                if(Y[i][k].index == X[i][j].index)
                {
                    result.set(i, X[i][j].index, Y[i][k].value + X[i][j].value);
                }
                else
                {

                }

                j++;
            }
        }

        return result;
    }

    private static int getColumnDimension(RareElement[][] X)
    {
        int i, j, max = -1;

        for(i = 0; i < X.length; i++)
        {
            for(j = 0; j < X[i].length; j++)
            {
                if (X[i][j].index > max)
                {
                    max = X[i][j].index;
                }
            }
        }

        return max;
    }

    private static RareElement[][] memorizeRareMatrix(Matrix x)
    {
        int n = x.getRowDimension();
        int m = x.getColumnDimension();
        int countNonZeroInRow;
        double value;
        int rowIndex, colIndex, memResultColIndex;

        RareElement[][] memResult = new RareElement[n][];

        for(rowIndex = 0; rowIndex < n; rowIndex++)
        {
            memResultColIndex = 0;
            countNonZeroInRow = 0;

            // Calculez dimensiunea liniei fara elementele de zero.

            for (colIndex = 0; colIndex < m; colIndex++)
            {
                if(Math.abs(x.get(rowIndex, colIndex)) > eps)
                {
                    countNonZeroInRow++;
                }
            }

            memResult[rowIndex] = new RareElement[countNonZeroInRow];

            // Memorez elementele diferite de zero.

            for (colIndex = 0; colIndex < m; colIndex++)
            {
                value = x.get(rowIndex, colIndex);

                if(Math.abs(value) > eps && colIndex != rowIndex)
                {
                    memResult[rowIndex][memResultColIndex] = new RareElement();
                    memResult[rowIndex][memResultColIndex].value = value;
                    memResult[rowIndex][memResultColIndex].index = colIndex;
                    memResultColIndex++;
                }
            }

            value = x.get(rowIndex, rowIndex);

            if(Math.abs(value) > eps)
            {
                memResult[rowIndex][memResultColIndex] = new RareElement();
                memResult[rowIndex][memResultColIndex].value = value;
                memResult[rowIndex][memResultColIndex].index = rowIndex;
            }
        }

        return memResult;
    }

    private static void printRare(RareElement[][] x)
    {
        int i, j;

        for(i = 0; i < 5; i++)
        {
            int l = x[i].length;

            for(j = 0; j < l; j++)
            {
                System.out.print(x[i][j].toString());
                System.out.print(' ');
            }

            System.out.println();
        }
    }

    private static List<Integer> minMax(int a, int b)
    {
        List<Integer> minMax = new ArrayList<Integer>();

        if(a > b)
        {
            minMax.add(b);
            minMax.add(a);
        }
        else
        {
            minMax.add(a);
            minMax.add(b);
        }

        return minMax;
    }
}
