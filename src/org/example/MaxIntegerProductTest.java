package org.example;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

public class MaxIntegerProductTest {

	static class Testcase {
		private Integer[] dataset;
		private int expectedResult;
		private IntegerProduct integerProduct;
		
		static void buildAndTest(Integer[] dataset, int expectedResult) {
			Testcase testcase = buildTestcase(dataset, expectedResult);
			assertEquals(expectedResult, testcase.integerProduct.getMaxProduct());
		}

		static Testcase buildTestcase(Integer[] dataset, int expectedResult) {
			return new Testcase(dataset, expectedResult);
		}
		
		private Testcase(Integer[] dataset, int expectedResult) {
			this.dataset = dataset;
			this.expectedResult = expectedResult;
			integerProduct = new IntegerProduct(IntegerList.newBuilder(Arrays.asList(dataset)).build());
		}
		
	}
	
	@Test
	// [0,1,2,3,4,5,-1] => 60 (5*4*3)
	public void testCase1() {
		final Integer[] dataset = {
			0,1,2,3,4,5,-1
		};
		final int result = 60;
		Testcase.buildAndTest(dataset, result);
	}

	@Test
	// [-10,-9,-8,-7,6,-5] => 540 (-10*-9*6)
	public void testCase2() {
		final Integer[] dataset = {
			-10,-9,-8,-7,6,-5
		};
		final int result = 540;
		Testcase.buildAndTest(dataset, result);
	}

	@Test
	// [-10,-9,-8,-7,-6,-5] => -210 (-5*-6*-7)
	public void testCase3() {
		final Integer[] dataset = {
			-10,-9,-8,-7,-6,-5
		};
		final int result = -210;
		Testcase.buildAndTest(dataset, result);
	}

	@Test
	// [10,-9,-8,-7,6,-5] => 720 (10*-9*-8)
	public void testCase4() {
		final Integer[] dataset = {
			10,-9,-8,-7,-6,-5
		};
		final int result = 720;
		Testcase.buildAndTest(dataset, result);
	}


	@Test
	// [10,9,-8,-7,6,-5] => 560 (10*-8*-7)
	public void testCase5() {
		final Integer[] dataset = {
			10,9,-8,-7,6,-5
		};
		final int result = 560;
		Testcase.buildAndTest(dataset, result);
	}


	@Test
	// [10,-5,8,-7,6,9] => 
	// [10,9,8,6,-5,-7] => 720 (10*9*8)
	// ie, order doesn't matter
	public void testCase6() {
		final Integer[] dataset = {
			10,-9,-8,-7,-6,-5
		};
		final int result = 720;
		Testcase.buildAndTest(dataset, result);
	}

	@Test
	// [-10,5,4,3,2,-1] =>  60 (5,4,3)
	public void testCase7() {
		final Integer[] dataset = {
			-10,5,4,3,2,-1
		};
		final int result = 60;
		Testcase.buildAndTest(dataset, result);
	}

	@Test(expected = IllegalArgumentException.class)
	// [-10,5,4,3,2,-1] =>  60 (5,4,3)
	public void testOverflowHandling() {
		final Integer[] dataset = {
			Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE 
		};
		final int result = 60;
		Testcase.buildAndTest(dataset, result);
	}
}
