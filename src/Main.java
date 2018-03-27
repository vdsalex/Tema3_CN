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

    private static RareElement[][] addRareMatrices(RareElement[][] X, RareElement[][] Y)
    {
        int n = X.length < Y.length ? Y.length : X.length;

        RareElement[][] mmResult = new RareElement[n][];
        int lineCount = 0;

        for (int line = 0; line < n; line++)
        {
            RareElement diagonalElem = null;
            RareElement[] tempResultLine = new RareElement[n];
            int elemCount = 0;

            // TODO Check if line vector exists?
            for (int element = 0; element < n ; element++)
            {
                double x,y;
                if (line >= X.length)
                    x = 0;
                else
                {
                    RareElement xRare = getRareElement(X[line], element);
                    if (xRare.index == -1)
                        x = 0;
                    else x = xRare.value;

                }

                if (line >= Y.length)
                    y = 0;
                else
                {
                    RareElement yRare = getRareElement(Y[line], element);
                    if (yRare.index == -1)
                        y = 0;
                    else y = yRare.value;
                }

                // calculate element
                double result = x + y;

                if (Math.abs(result) > eps) {

                    RareElement resultElem = new RareElement();
                    resultElem.index = element;
                    resultElem.value = result;

                    if (line == element) {
                        diagonalElem = resultElem;
                    } else {
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

    public static void printRareMatrix(RareElement[][] rare)
    {
        System.out.println();

        for(int i = 0; i < rare.length; i++)
        {
            int size = rare[i].length;

            for (int j = 0; j < size; j++)
            {
                System.out.print(rare[i][j].toString());
                System.out.print(' ');
            }

            if (size!= 0)
                System.out.println();
            else System.out.println("(,)");
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

        double[][] example1 = {
                {1.0, 2.0, 0, 0},
                {0, 9.0, 0, 0},
                {-4.0, 0, -5.9, -1.0},
                {99.0, 7.0, 7.0, 5.0},
        };

        /*double[][] example1 = {
                {1.0},
                {0},
                {-4.0},
                {99.0},
                {51.4}
        };
*/


        Matrix ex = new Matrix(example);
        Matrix ex1 = new Matrix(example1);

        ex.print(5, 1);
        ex1.print(5, 1);

        RareElement[][] res = memorizeRareMatrix(ex);
        RareElement[][] res1 = memorizeRareMatrix(ex1);

        RareElement[][] res2 = addRareMatrices(res, res1);

        printRareMatrix(res);
        printRareMatrix(res1);
        printRareMatrix(res2);
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
