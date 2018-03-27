import Jama.Matrix;

import java.io.*;

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
    public static void main(String args[]) throws IOException
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

        //ex.print(5, 1);
        //ex1.print(5, 1);

        RareElement[][] res = memorizeRareMatrix(ex);
        RareElement[][] res1 = memorizeRareMatrix(ex1);

        //RareElement[][] res2 = RareMultiply(res, res1, 5);

        //Matrix aMatrix, bMatrix, aOribMatrix, aPlusbMatrix;
        //aMatrix = readMatrixFromFile("a.txt");
        //bMatrix = readMatrixFromFile("b.txt");
        //aMatrix.print(10, 5);
        //bMatrix.print(10, 5);

        //Matrix aArray = readArrayFromFile("a.txt");
        //aArray.print(1, 5);
    }

    private static Matrix readMatrixFromFile(String filename) throws IOException
    {
        Matrix matrixRead;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        int index = 1, n = 0, m = 3, rowIndex = 0, colIndex = 0;

        while((line = bufferedReader.readLine()) != null)
        {
            if(index == 2022)
            {
                String[] dimensions = line.split(" ");
                n = Integer.parseInt(dimensions[0]);
                m = Integer.parseInt(dimensions[1]);
                line = bufferedReader.readLine();
                break;
            }
            index++;
        }

        matrixRead = new Matrix(n, m);

        while((line = bufferedReader.readLine()) != null)
        {
            String[] stringValues = line.split(", ");

            double[] values = new double[m];

            for(colIndex = 0; colIndex < m; colIndex ++)
            {
                values[colIndex] = Double.parseDouble(stringValues[colIndex].trim());
                matrixRead.set(rowIndex, colIndex, values[colIndex]);
            }

            rowIndex++;
        }

        return matrixRead;
    }

    private static Matrix readArrayFromFile(String filename) throws IOException
    {
        Matrix arrayRead;
        int n, index = 0;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String line;
        double value;

        n = Integer.parseInt(bufferedReader.readLine().trim());
        arrayRead = new Matrix(n, 1);
        //citesc linia goala
        line = bufferedReader.readLine();

        while(index < 2018)
        {
            line = bufferedReader.readLine().trim();

            value = Double.parseDouble(line);
            arrayRead.set(index, 0, value);

            index++;
        }

        return arrayRead;
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
}