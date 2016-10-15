package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import roblox.listmerge.IntegerList;

public class TestListMerge {

	static final Integer[] dataSet1 = {
			0,2,3
	};

	static final Integer[] dataSet2 = {
			-1,1,3,4,5
	};
	
	static final Integer[] result1n2 = {
			-1,0,1,2,3,3,4,5
	};

	/**
	 * Handles negatives and duplicates -- and demonstrates builder syntax.
	 */
	@Test
	public void testHandlesNegativesAndDuplicates() {
		List<Integer> leftData =  Arrays.asList(dataSet1);
		List<Integer> rightData = Arrays.asList(dataSet2);
		List<Integer> expectedResult = Arrays.asList(result1n2);

		assertTrue( (IntegerList.newBuilder(leftData).mergeWith(rightData)).build().equals(expectedResult) );
	}

	/**
	 * When either list is null, throws IllegalArgumentException.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testHandlesNulls() {
		List<Integer> left = null;
		List<Integer> right = Arrays.asList(dataSet1);

		IntegerList.mergeLists(left, right);
	}

	/**
	 * When other list is empty, returns first list. 
	 */
	@Test
	public void testRightIsEmpty() {
		List<Integer> left = Arrays.asList(dataSet1);
		List<Integer> right = Collections.<Integer>emptyList();

		List<Integer> result = IntegerList.mergeLists(left, right);
		assertEquals(result, left);
	}

	/**
	 * When both are empty, returns first list. 
	 */
	@Test
	public void testBothAreEmpty() {
		List<Integer> left = new ArrayList<Integer>();
		List<Integer> right = Collections.<Integer>emptyList();
		List<Integer> result = IntegerList.mergeLists(left, right);
		assertEquals(result, left);
	}

	/**
	 * Make sure we get an exception if we attempt to test for equality before calling build(). 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEqualsBombsWithoutBuild() {
		List<Integer> leftData =  Arrays.asList(dataSet1);
		List<Integer> rightData = Arrays.asList(dataSet2);

		IntegerList.newBuilder(leftData).mergeWith(rightData).equals(result1n2);
	}
}
