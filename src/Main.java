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

    // returns the element if found, else RareElement with -1 on index
    private static RareElement getRareElement(RareElement[] line, int index)
    {
        for (RareElement element: line)
        {
            if (element.index == index)
                return element;
        }

        RareElement elem = new RareElement();
        elem.index = -1;
        return elem;
    }

    private static RareElement[][] RareMultiply(RareElement[][] A, RareElement[][] B, int n)
    {
        RareElement[][] mmResult = new RareElement[n][n];
        int lineCount = 0;

        for (int lineA = 0; lineA < n; lineA++)
        {
            RareElement diagonalElem = null;
            RareElement[] tempResultLine = new RareElement[n];
            int elemCount = 0;

            // TODO Check if line vector exists?
            for (int columnB = 0; columnB < n ; columnB++)
            {
                // calculate element
                double result = 0;

                for (int k = 0; k < A[lineA].length ;k++ )
                {
                    RareElement bElement = getRareElement(B[A[lineA][k].index], columnB);
                    if (bElement.index != -1)
                        result+= ( bElement.value * A[lineA][k].value) ;
                }

                // remember diagonal element to add it later
                if (result != 0) {

                    RareElement resultElem = new RareElement();
                    resultElem.index = columnB;
                    resultElem.value = result;

                    if (lineA == columnB) {
                        diagonalElem = resultElem;
                    }
                    else {
                        tempResultLine[elemCount] = resultElem;
                        elemCount++;
                    }
                }

            }

            // add the diagonal elem
            if (diagonalElem != null) {
                tempResultLine[elemCount] = diagonalElem;
                elemCount++;
            }

            // copy each element in a new vector with exact size

            RareElement[] resultLine = new RareElement[elemCount];
            System.arraycopy(tempResultLine, 0, resultLine, 0, elemCount);

            // TODO Can find number of lines before?
            mmResult[lineCount] = resultLine;
            lineCount++;
        }

        return mmResult;
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

        /*double[][] example1 = {
                {1.0, 2.0, 0, 0, 4.0},
                {0, 9.0, 0, 0, 21.0},
                {-4.0, 0, -5.9, -1.0, 0},
                {99.0, 7.0, 7.0, 5.0, -0.9},
                {51.4, 0, 0, 0, -73.1}
        };*/

        double[][] example1 = {
                {1.0},
                {0},
                {-4.0},
                {99.0},
                {51.4}
        };



        Matrix ex = new Matrix(example);
        Matrix ex1 = new Matrix(example1);

        ex.print(5, 1);
        ex1.print(5, 1);

        RareElement[][] res = memorizeRareMatrix(ex);
        RareElement[][] res1 = memorizeRareMatrix(ex1);

        RareElement[][] res2 = RareMultiply(res, res1, 5);

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

        System.out.println();

        for(i = 0; i < 5; i++)
        {
            int size = res1[i].length;

            for (j = 0; j < size; j++)
            {
                System.out.print(res1[i][j].toString());
                System.out.print(' ');
            }

            if (size!= 0)
                System.out.println();
            else System.out.println("(,)");
        }

        System.out.println();

        for(i = 0; i < 5; i++)
        {
            int size = res2[i].length;

            for (j = 0; j < size; j++)
            {
                System.out.print(res2[i][j].toString());
                System.out.print(' ');
            }

            if (size!= 0)
                System.out.println();
            else System.out.println("(,)");
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

            if (rowIndex < m)
            {
                value = x.get(rowIndex, rowIndex);

                if (Math.abs(value) > eps) {
                    memResult[rowIndex][memResultColIndex] = new RareElement();
                    memResult[rowIndex][memResultColIndex].value = value;
                    memResult[rowIndex][memResultColIndex].index = rowIndex;
                }
            }
        }

        return memResult;
    }
}
