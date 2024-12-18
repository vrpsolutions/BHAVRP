package cujae.inf.ic.om.matrix;

import java.util.ArrayList;

/* Clase que modela una matriz */
public class NumericMatrix {
	private static final int DEFAULT_ROW_CAPACITY = 5;
	private static final int DEFAULT_COL_CAPACITY = 5;
	
	private double [][] data;
	private int colCount;
	private int rowCount;
	
	public NumericMatrix(){
		this(DEFAULT_ROW_CAPACITY,DEFAULT_COL_CAPACITY );
	}

	public NumericMatrix(int rowCapacity,int colCapacity ){
		if (rowCapacity < 0 || colCapacity < 0)
			throw new IllegalArgumentException();
		data = new double [rowCapacity][colCapacity];
		colCount = colCapacity;
		rowCount = rowCapacity;
	}

	public NumericMatrix(double [][] source ){
		this(source, source.length,source[0].length); 
	}

	public NumericMatrix(double [][] source, int rowCount, int colCount){
		this(source.length,source[0].length); 
		addAll(source, rowCount, colCount);
	}

	public NumericMatrix(NumericMatrix source){
		this(source.getRowLength(),source.getColLength()); 
		addAll(source);
	}

	private  void verifyRange(int row1, int col1, int row2, int col2){
		checkIndexsInRange (row1,col1);
		checkIndexsInRange (row2,col2);
		if (row1 > row2 || col1 > col2) 
			throw new IndexOutOfBoundsException();
	}

	private  void checkIndexsInRange (int row, int col){
		checkRowInRange(row);
		checkColInRange(col);
	}

	private  void checkRowInRange (int row){
		if (row >= rowCount )
			throw new IndexOutOfBoundsException();
	}

	private  void checkColInRange (int col){
		if ( col >= colCount)
			throw new IndexOutOfBoundsException();
	}


	private void ensureColCapacity(int minCapacity)
	{   
		int current = getColLength();
		double [][] newData = new double[getRowLength()][Math.max(current + DEFAULT_COL_CAPACITY, minCapacity)];
		for(int i=0 ; i< getRowCount();i++)
			System.arraycopy(data[i], 0, newData[i], 0, colCount);
		data = newData;
	}

	private void ensureRowCapacity(int minCapacity)
	{   
		int current = getRowLength();
		double [][] newData = new double[Math.max(current + DEFAULT_ROW_CAPACITY, minCapacity)][getColLength()];
		for(int i=0 ;i< getRowCount();i++)
			System.arraycopy(data[i], 0, newData[i], 0, colCount);
		data = newData;
	}


	public  int getColCount() {
		return colCount;
	}

	public  int getColLength() {
		return data[0].length;
	}

	public  int getRowCount() {
		return rowCount;
	}

	public  int getRowLength() {
		return data.length;
	}

	public double setItem(int row, int col, double elem){
		checkIndexsInRange(row,col);
		double result = data[row][col];
		data[row][col] = elem;
		return result;
	}

	public double getItem(int row, int col){
		checkIndexsInRange(row,col);
		return data[row][col];
	}

	public void addAll(double[][] source, int rowCount, int colCount)
	{
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++)
				data [i][j] = source[i][j];
		}
		this.rowCount = rowCount;
		this.colCount = colCount;
	}

	public void addAll(NumericMatrix source){
		for (int i = 0; i < source.getRowCount(); i++) {
			for (int j = 0; j < source.getColCount(); j++)
				data [i][j] = source.getItem(i,j);
		}
		rowCount = source.getRowCount();
		colCount = source.getColCount();
	}

	public void addColZero(){
		// Insert a new col with 0
		if(colCount == getColLength())
			ensureColCapacity(getColLength() + 1);
		for(int i=0 ;i< rowCount; i++)
			data[i][colCount]= 0;
		colCount++;
	}

	public void addRowZero(){
		// Insert a new row with 0
		if(rowCount == getRowLength())
			ensureRowCapacity(getRowLength() + 1);
		for(int j=0 ;j< colCount; j++)
			data[rowCount][j]= 0;
		rowCount++;
	}

	public  float average (int row1, int col1, int row2, int col2){
		double sum = sum(row1, col1, row2, col2);
		return (float)(sum/((row2-row1+1)*(col2-col1+1)));
	}

	public  float average (){
		return average(0, 0, rowCount-1, colCount-1);
	}

	public double sum(int row1, int col1, int row2, int col2){
		verifyRange(row1,col1,row2,col2);
		double sum = 0;
		for(int i=row1; i<=row2; i++)
			for(int j=col1; j<=col2;j++)
				sum+= data[i][j];
		return sum;   
	}

	public double sum(){
		return sum(0,0,rowCount-1,colCount-1);   
	}

	public void sum (NumericMatrix matrix){
		if(rowCount != matrix.getRowCount() || colCount != matrix.getColCount())
			throw new IllegalArgumentException();  
			for(int i=0; i< rowCount;i++)
				for(int j=0; j<colCount; j++)
					data[i][j]+= matrix.getItem(i,j);   
	}

	public void addCol(int col, NumericArray newCol){
		if(col > colCount || newCol.getCount()!= rowCount)
			throw new IndexOutOfBoundsException();  
		if(colCount == getColLength())
			ensureColCapacity(getColLength() + 1);

		for(int j= colCount; j>col;j--){
			for(int i=0;i<rowCount;i++)
				data[i][j]=data[i][j-1];
		}

		for(int i=0;i<rowCount;i++)
			data[i][col]= newCol.getItem(i); 

		colCount++; 
	}

	public void addCol(NumericArray newCol){
		addCol(colCount, newCol);
	}

	public void addRow(int row, NumericArray newRow){
		if(row > rowCount || newRow.getCount()!= colCount)
			throw new IndexOutOfBoundsException();  
		if(rowCount == getRowLength())
			ensureRowCapacity(getRowLength() + 1);

		for(int i= rowCount; i>row;i--){
			for(int j=0;j<colCount;j++)
				data[i][j]=data[i-1][j];
		}

		for(int j=0;j<colCount;j++)
			data[row][j]= newRow.getItem(j); 

		rowCount++;
	}

	public void addRow(NumericArray newRow){
		addRow(rowCount, newRow);
	}

	public int countEqualThan(int row1, int col1, int row2, int col2,double value){
		verifyRange(row1,col1,row2,col2);
		int count = 0;
		for(int i = row1; i <= row2; i++)
			for(int j = col1; j <= col2; j++)
				if (data[i][j] == value)
					count++;
		return count;
	}

	public int countEqualThan(double value){
		return countEqualThan(0, 0, rowCount-1, colCount-1, value);	
	}

	public int countBiggerThan(int row1, int col1, int row2, int col2,double value){
		verifyRange(row1,col1,row2,col2);
		int count = 0;
		for(int i = row1; i <= row2; i++)
			for(int j = col1; j <= col2; j++)
				if (data[i][j] > value)
					count++;
		return count;
	}

	public int countBiggerThan(double value){
		return countBiggerThan(0, 0, rowCount-1, colCount-1, value);
	}

	public int countLowerThan(int row1, int col1, int row2, int col2,double value){
		verifyRange(row1,col1,row2,col2);
		int count = 0;
		for(int i = row1; i <= row2; i++)
			for(int j = col1; j <= col2; j++)
				if (data[i][j] < value)
					count++;
		return count;
	}

	public int countLowerThan(double value){
		return countLowerThan(0, 0, rowCount-1, colCount-1, value);
	}

	public NumericArray getCol(int col){
		checkColInRange(col);
		double [] tempCol = new double [getRowCount()];

		for(int i = 0; i < rowCount; i++)
			tempCol[i] = data[i][col];

		NumericArray colSelected = new NumericArray(tempCol);

		return colSelected;
	}
	
    public NumericArray getRow(int row){
		checkRowInRange(row);
		double [] tempRow = new double [getColCount()];

		for(int i = 0; i < colCount; i++)
			tempRow[i] = data[row][i];
		
		NumericArray rowSelected = new NumericArray(tempRow);
		
		return rowSelected;
	}
	
	public NumericArray deleteCol(int col){
		checkColInRange(col);
		double [] tempCol = new double [getRowCount()];

		for(int i= 0; i<rowCount;i++)
			tempCol[i]=data[i][col];

		NumericArray deletedCol = new NumericArray(tempCol);

		for(int j= col +1; j<colCount;j++){
			for(int i=0;i<rowCount;i++)
				data[i][j-1]=data[i][j];
		}

		clear(0,colCount-1,rowCount-1,colCount-1);
		colCount--;  
		return deletedCol;
	}

	public NumericArray deleteRow(int row){
		checkRowInRange(row);
		double [] tempRow = new double [getColCount()];

		for(int j= 0; j<colCount;j++)
			tempRow[j]=data[row][j];

		NumericArray deletedRow = new NumericArray(tempRow);

		for(int i= row +1; i <rowCount;i++){
			for(int j=0;j<colCount;j++)
				data[i-1][j]=data[i][j];
		}

		clear(rowCount-1,0,rowCount-1,colCount-1);
		rowCount--;  
		return deletedRow;
	}

	public void fillValue(int row1, int col1, int row2, int col2,double value){
		verifyRange(row1,col1,row2,col2);
		for(int i = row1; i <= row2; i++)
			for(int j = col1; j <= col2; j++)
				data[i][j] = value;
	}

	public void fillValue(double value){
		fillValue(0, 0, rowCount-1, colCount-1, value);
	}

	public void clear (int row1, int col1, int row2, int col2){
		fillValue(row1, col1, row2, col2, 0);
	}

	public void clear (){
		clear(0, 0, rowCount-1, colCount-1);
	}

	public RowCol find(int row1, int col1, int row2, int col2,double value){
		verifyRange(row1, col1, row2, col2);
		boolean found = false;
		RowCol rowCol = new RowCol(-1,-1);
		int i = row1;
		while (i <=row2 && !found){
			int j = col1;
			while (j <= col2 && !found){
				if(data[i][j] == value)
				{
					rowCol.setRow(i);
					rowCol.setCol(j);
					found = true;
				}
				else
					j++;
			}
			if(!found)
				i++;
		}
		return rowCol;
	}

	public RowCol find(double value){
		return find(0, 0, rowCount-1, colCount-1, value);
	}

	public RowCol indexBiggerValue(int row1, int col1, int row2, int col2){
		verifyRange(row1, col1, row2, col2);
		RowCol rowCol = new RowCol();
		double bigger = data[row1][col1];
		rowCol.setRow(row1);
		rowCol.setCol(col1);
		for (int i = row1; i <= row2; i++)
			for (int j = col1; j <= col2; j++)
			{
				if (data[i][j] > bigger)
				{
					bigger = data[i][j];
					rowCol.setRow(i);
					rowCol.setCol(j); 
				}
			}	
		return rowCol;
	}

	public RowCol indexBiggerValue(){
		return indexBiggerValue(0, 0, rowCount-1, colCount-1);
	}

	public RowCol indexLowerValue(int row1, int col1, int row2, int col2){
		verifyRange(row1, col1, row2, col2);
		RowCol rowCol = new RowCol();
		double lower = data[row1][col1];
		rowCol.setRow(row1);
		rowCol.setCol(col1);
		for (int i = row1; i <= row2; i++)
			for (int j = col1; j <= col2; j++)
				if (data[i][j] < lower)
				{
					lower = data[i][j];
					rowCol.setRow(i);
					rowCol.setCol(j); 
				}
		return rowCol;
	}

	public RowCol indexLowerValue(){
		return indexLowerValue(0, 0, rowCount-1, colCount-1);
	}

	public NumericMatrix multiply(NumericMatrix matrix){
		if(colCount != matrix.getRowCount())
			throw new IllegalArgumentException();  
			double[][] tempMatrix = new double[rowCount][matrix.getColCount()];
			NumericMatrix newMatrix = new NumericMatrix(tempMatrix);
			double value;
			for(int i=0; i< rowCount;i++)
				for(int k=0;k<matrix.getColCount();k++){
					value = 0;
					for(int j=0;j<colCount;j++)
						value += data[i][j]* matrix.getItem(j,k);
					newMatrix.setItem(i,k,value);  
				}
			return newMatrix; 
	}
	
	public boolean fullMatrix(double value){
		boolean isFull = false;
		RowCol maxValue = indexBiggerValue();
		RowCol minValue = indexLowerValue();
		
		if(data[maxValue.getRow()][maxValue.getCol()] == data[minValue.getRow()][minValue.getCol()])
			isFull = true;
		
		return isFull;
	}
	
	public boolean fullMatrix(int row1, int col1, int row2, int col2, double value) {
        boolean isFull = false;
        RowCol maxValue = indexBiggerValue(row1, col1, row2, col2);
        RowCol minValue = indexLowerValue(row1, col1, row2, col2);

        if (data[maxValue.getRow()][maxValue.getCol()] == data[minValue.getRow()][minValue.getCol()])
            isFull = true;


        return isFull;
    }
	
	public int selectValue(int valueRef, ArrayList<Integer> listValues, boolean function){
		double bestValue = -1; 
		double actValue = -1;
		int index = -1;

		bestValue = data[valueRef][listValues.get(0)];
		index = listValues.get(0);

		for(int i = 1; i < listValues.size(); i++){
			actValue = data[valueRef][listValues.get(i)];

			if(function)
			{
				if(bestValue > actValue)
				{
					bestValue = actValue;
					index = listValues.get(i);
				}
			}
			else
			{
				if(bestValue < actValue)
				{
					bestValue = actValue;
					index = listValues.get(i);
				}
			}
		}
		return index;
	}
}