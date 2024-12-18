package cujae.inf.ic.om.matrix;

import java.util.Arrays;

/* Clase que modela una fila o columna */
public class NumericArray {

	private static final int DEFAULT_CAPACITY = 5;

	/**
	 * The number of elements in this list.
	 * @serial the list size
	 */
	private int count;

	/**
	 * Where the data is stored.
	 */
	private double [] data;

	/**
	 * Construct a new IntegerArray with the default capacity (20).
	 */
	public NumericArray()
	{
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Construct a new IntegerArray with the supplied initial capacity.
	 *
	 * @param capacity initial capacity of this IntegerArray
	 * @throws IllegalArgumentException if capacity is negative
	 */
	public NumericArray(int capacity)
	{
		// Must explicitly check, to get correct exception.
		if (capacity < 0)
			throw new IllegalArgumentException();
		data = new double[capacity];
	}

	/**
	 * Construct a new IntegerArray, and initialize it with the elements
	 * in the supplied array. 
	 *
	 * @param source the array whose elements will initialize this list
	 * @throws NullPointerException if source is null
	 */
	public NumericArray(double[] source)
	{
		this(source.length);
		addAll(source, source.length);
	}
	
	public NumericArray(double[] source, int count)
	{
		this(source.length);
		addAll(source,count);
	}

	/**
	 * Construct a new IntegerArray from another, and initialize it with the elements
	 * in the supplied instance. 
	 *
	 * @param source the collection whose elements will initialize this list
	 * @throws NullPointerException if source is null
	 */
	public NumericArray(NumericArray source)
	{
		this(source.getCount());
		addAll(source);
	}

	/**
	 * Appends the supplied element to the end of this list.
	 * The element, e, can be an object of any type or null.
	 *
	 * @param e the element to be appended to this list
	 * @return true, the add will always succeed
	 */
	public boolean add(double elem)
	{

		if (count == data.length)
			ensureCapacity(count + 1);
		data[count++] = elem;
		return true;
	}

	/**
	 * Adds the supplied element at the specified index, shifting all
	 * elements currently at that index or higher one to the right.
	 * 
	 * @param index the index at which the element is being added
	 * @param elem the item being added
	 * @throws IndexOutOfBoundsException if index < 0 || index > count()
	 */
	public void add(int index, double elem)
	{
		checkIndexInRange(index);
		if (count == data.length)
			ensureCapacity(count + 1);
		if (index != count)
			System.arraycopy(data, index, data, index + 1, count - index);
		data[index] = elem;
		count++;
	}

	/** Add each element in the supplied array to this List.  
	 *
	 * @param source a array containing elements to be added to this List
	 * @return true if the list was modified, in other words source is not empty
	 * @throws NullPointerException if source is null
	 */
	public boolean addAll(double[] source, int count)
	{
		for (int i = 0; i < count; i++) 
		{
			add(source[i]);
		}
		return true;
	}

	/** Add each element in the supplied Collection to this List.  
	 *
	 * @param source a IntegerArray containing elements to be added to this List
	 * @return true if the list was modified, in other words source is not empty
	 * @throws NullPointerException if source is null
	 */
	public boolean addAll(NumericArray source)
	{
		for (int i = 0; i < source.getCount(); i++) 
		{
			add(source.getItem(i));
		}
		return true;
	}
   
	public int getLength (){
		return data.length;
	}
	/**
	 * Checks that the index is in the range of existing elements (exclusive).
	 *
	 * @param index the index to check
	 * @throws IndexOutOfBoundsException if index >= count
	 */
	private void checkIndexInRange(int index)
	{
		// Implementation note: we do not check for negative ranges here, since
		// use of a negative index will cause an ArrayIndexOutOfBoundsException,
		// a subclass of the required exception, with no effort on our part.
		if (index >= count)
			throw new IndexOutOfBoundsException("Index: " + index + ", count: "
					+ count);
	}

	/**
	 * Removes all elements from this List
	 */
	public void clear()
	{
		if (count > 0)
		{	         
			Arrays.fill(data, 0, count, 0);
			count = 0;
		}
	}

	/**
	 * Guarantees that this list will have at least enough capacity to
	 * hold minCapacity elements. This implementation will grow the list to
	 * max(current * 2, minCapacity) if (minCapacity &gt; current). The JCL says
	 * explictly that "this method increases its capacity to minCap", while
	 * the JDK 1.3 online docs specify that the list will grow to at least the
	 * count specified.
	 *
	 * @param minCapacity the minimum guaranteed capacity
	 */
	private void ensureCapacity(int minCapacity)
	{
		int current = data.length;

		if (minCapacity > current)
		{
			double[] newData = new double[Math.max(current * 2, minCapacity)];
			System.arraycopy(data, 0, newData, 0, count);
			data = newData;
		}
	}
	
	/**
	 * Returns true iff element is in this IntegerArray.
	 *
	 * @param e the element whose inclusion in the List is being tested
	 * @return true if the list contains e
	 */
	public boolean exists(double elem)
	{
		return FindPositionElement(elem) != -1;
	}

	/**
	 * Retrieves the element at the user-supplied index.
	 *
	 * @param index the index of the element we are fetching
	 * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt;= count()
	 */
	public double getItem(int index)
	{
		checkIndexInRange(index);
		return data[index];
	}

	public int getCount() 
	{
		return count;
	}

	/**
	 * Returns the lowest index at which element appears in this collection, or
	 * -1 if it does not appear.
	 *
	 * @param elem the element whose inclusion in the List is being tested
	 * @return the index where elem was found
	 */
	public int FindPositionElement(double elem)
	{
		for (int i = 0; i < count; i++)
			if (elem == data[i])
				return i;
		return -1;
	}

	/**
	 * Checks if the list is empty.
	 *
	 * @return true if there are no elements
	 */
	public boolean isEmpty()
	{
		return count == 0;
	}

	/**
	 * Returns the highest index at which element appears in this List, or
	 * -1 if it does not appear.
	 *
	 * @param e the element whose inclusion in the List is being tested
	 * @return the index where e was found
	 */
	public int FindPositionElementFromEnd(double elem)
	{
		for (int i = count - 1; i >= 0; i--)
			if (elem == data[i])
				return i;
		return -1;
	}

	/**
	 * Removes the element at the user-supplied index.
	 *
	 * @param index the index of the element to be removed
	 * @return the removed Object
	 * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt;= count()
	 */
	public double remove(int index)
	{
		checkIndexInRange(index);
		double r = data[index];
		if (index != --count)
			System.arraycopy(data, index + 1, data, index, count - index);
		data[count] = 0;
		return r;
	}

	/**
	 * Sets the element at the specified index.  The new element, elem,
	 * is an Integer Value.
	 *
	 * @param index the index at which the element is being set
	 * @param elem the integer value to be set
	 * @return the element previously at the specified index
	 * @throws IndexOutOfBoundsException if index < 0 || index >= 0
	 */
	public double set(int index, double elem)
	{
		checkIndexInRange(index);
		double result = data[index];
		data[index] = elem;
		return result;
	}
		
	/** Calculates the average of the values stored 
	 * 
	 * @return float the average of the values stored in data.
	 */
	public float average() 
	{
		double sum=0;
		for (int i = 0; i < data.length; i++) 
		{
			sum += data[i];
		}
		
		return (float)sum/data.length;
		
	}
	
	/** Return the elements which value is equal than a given pNumber.
	 * @param pNumber the given value to compare with.
	 * @return
	 */
	public int CountEqualNumber(int pNumber) 
	{
		
		return 0;
	}
	
	/** Count the elements which value is biggest than a given pNumber.
	 * @param pNumber the given value to compare with.
	 * @return
	 */
	public int CountBiggerThan(int pNumber)
	{
		
		return 0;
	}

	/** Count the elements which value is minor than a given pNumber.
	 * @param pNumber the given value to compare with.
	 * @return
	 */
	public int CountSmallerThan(int pNumber)
	{
		
		return 0;
	}
	
	
	/** Return the index of the last ocurrence of the biggest element of the 
	 * collection.
	 * 
	 * @return int 	 the index of the biggest.
	 */
	public int IndexBiggerValue() {
		
		return -1;
	}
	
	/** Return the index of the last ocurrence of the lowest element of the 
	 * collection.
	 * 
	 * @return int 	 the index of the biggest.
	 */
	public int IndexLowerValue() {
		
		return -1;
	}
	
	/** Calculates the sum of the all the elements of the collection.
	 * 
	 * @return int 	 
	 * */
	public int Sum() {
		
		return -1;
	}
}