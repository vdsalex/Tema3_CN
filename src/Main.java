import Jama.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

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
        if (line!= null)
        {
            for (RareElement elem : line) {
                if (elem!=null)
                {
                    if (elem.index == index)
                        return elem;
                }
            }
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
                if (Math.abs(result) > eps) {

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

        double[][] example1 = {
                {1.0, 2.0, 0, 0, 4.0},
                {0, 9.0, 0, 0, 21.0},
                {-4.0, 0, -5.9, -1.0, 0},
                {99.0, 7.0, 7.0, 5.0, -0.9},
                {51.4, 0, 0, 0, -73.1}
        };

        /*double[][] example1 = {
                {1.0},
                {0},
                {-4.0},
                {99.0},
                {51.4}
        };
*/

        RareElement[][] res, res1, res2;

        /*Matrix ex = new Matrix(example);
        Matrix ex1 = new Matrix(example1);


        //ex.print(5, 1);
        //ex1.print(5, 1);
        res = memorizeRareMatrix(ex);
        res1 = memorizeRareMatrix(ex1);

        res2 = RareMultiply(res, res1, res.length);

        printRareMatrix(res);
        printRareMatrix(res1);
        printRareMatrix(res2);
        */

        Instant start = Instant.now();

        RareElement[][] aMatrix, bMatrix, vector, aorib, aplusb;

        aMatrix = readMatrixFromFile("a.txt");
        bMatrix = readMatrixFromFile("b.txt");
        aorib = readMatrixFromFile("aorib.txt");

        res = RareMultiply(aMatrix,bMatrix, aMatrix.length);

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));

    }

    private static RareElement[][] readMatrixFromFile(String filename) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

        RareElement[][] outRareMatrix = new RareElement[100000][5000];
        String textLine;
        int line = 0 ,column = 0;
        double value;
        textLine = bufferedReader.readLine();
        int vectorSize = Integer.parseInt(textLine);
        int maxLine = 0;

        RareElement[][] outRareVector = new RareElement[vectorSize][1];

        // read empty line
        bufferedReader.readLine();

        while(!(textLine = bufferedReader.readLine()).isEmpty() )
        {
            value = Double.parseDouble(textLine);

            if (Math.abs(value) > eps) {

                RareElement tempRare = getRareElement(outRareVector[line], column);
                if (tempRare.index == -1) {
                    // daca n-a fost gasit in linie deja

                    RareElement elem = new RareElement();
                    elem.value = value;
                    elem.index = column;
                    outRareVector[line][0] = elem;
                } else {
                    // a fost gasit deja
                    tempRare.value += value;
                }
            }
            line++;
        }

        // read matrix

        while((textLine = bufferedReader.readLine()) != null)
        {
            String[] stringValues = textLine.split(", ");

            value = Double.parseDouble(stringValues[0]);
            line = Integer.parseInt(stringValues[1]);
            column = Integer.parseInt(stringValues[2]);

            if (Math.abs(value) > eps) {

                if (line> maxLine)
                    maxLine = line;

                RareElement tempRare = getRareElement(outRareMatrix[line], column);
                if (tempRare.index == -1) {
                    // daca n-a fost gasit in linie deja

                    RareElement elem = new RareElement();
                    elem.value = value;
                    elem.index = column;
                    outRareMatrix[line][column] = elem;
                } else {
                    // a fost gasit deja
                    tempRare.value += value;
                }
            }
        }

        // compact the rare matrix
        RareElement[][] compactedMatrix = new RareElement[maxLine + 1][];

        for (int i = 0; i < maxLine +1;i++)
        {
            // check how many elements on line
            int elemPerLine = 0 ;
            for (RareElement rareElem:outRareMatrix[i])
            {
                if (rareElem!=null)
                    elemPerLine++;
            }
            RareElement[] rareLine = new RareElement[elemPerLine];

            RareElement diagElem = null;
            int elemCounter = 0;
            for (RareElement rareElem:outRareMatrix[i])
            {
                if (rareElem!=null)
                {
                    if (rareElem.index == i)
                        diagElem = rareElem;
                    else
                    {
                        rareLine[elemCounter] = rareElem;
                        elemCounter++;
                    }

                }
            }

            if (diagElem != null)
                rareLine[elemCounter] = diagElem;

            compactedMatrix[i] = rareLine;
        }

        return compactedMatrix;
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

    private static boolean thereAreAtMost10NonzeroElements(RareElement[][] x)
    {
        int i, j;
        int n = x.length;
        int m;

        for(i = 0; i < n; i++)
        {
            m = x[i].length;

            if(m > 10)return false;
        }

        return true;
    }

    private static boolean areEqual(RareElement[][] x, RareElement[][] y)
    {
        int n = x.length;
        int m1, i, j, m2;

        for(i = 0; i < n; i++)
        {
            m1 = x[i].length;
            m2 = y[i].length;

            if(m1 != m2)
            {
                return false;
            }

            j = 0;

            while(j < m1)
            {
                if(x[i][j].index != y[i][j].index || x[i][j].value != y[i][j].value)
                {
                    return false;
                }

                j++;
            }
        }

        return true;
    }

}
