import Jama.Matrix;

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

        double[][] example = {
                {1.0, 2.0, 0, 0, 4.0},
                {0, 9.0, 0, 0, 21.0},
                {-4.0, 0, -5.9, -1.0, 0},
                {99.0, 7.0, 7.0, 5.0, -0.9},
                {51.4, 0, 0, 0, -73.1}
        };

        Matrix ex = new Matrix(example);

        ex.print(5, 1);

        RareElement[][] res = memorizeRareMatrix(ex);

        System.out.println();
        int i, j;

        for(i = 0; i < 5; i++)
        {
            int size = res[i].length;

            for (j = 0; j < size; j++)
            {
                System.out.print(res[i][j].toString());
                System.out.print(' ');
            }

            System.out.println();
        }
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
}
