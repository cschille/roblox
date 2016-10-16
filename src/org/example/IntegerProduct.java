package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Q2. Write a function and list unit test cases for the following.
 * 
 * A function that takes a list of integers and returns the maximum product that
 * can be derived from any three integers in the list.
 * 
 * An [IllegalArgumentException] is thrown for any input values that will not
 * produce a valid Int32 product.
 
 * Approach
 * In the absence of negative values, simply sort and return 2 largest values.  With
 * negatives, it becomes more complicated: we need to return the largest of the
 * 4 possible combinations:
 * 
 *   1) first 3 positives (descending order)
 *   2) first positive (descending order) and first 2 negatives (ascending order)
 *      to cancel the negatives
 *   3) first 3 negatives (descending order) -- best of the worst scenario
 *   4) first 2 positives (ascending order) and first negative (descending order) --
 *      another best of the worst
 *   
 * @author cschille
 *
 */
public class IntegerProduct {

	public static final int RUN_LENGTH = 3;

	/**
	 * A class to manage the evaluation of each of the scenarios enumerated above, one
	 * at a time.
	 * 
	 * A structure to manage the value and factors for each case enumerated above.
	 * Unfortunately, we have to make the class static to be able to do
	 * static initialization.  Otherwise, we could get a built-in reference to the parent.
	 * 
	 * @author cschille
	 *
	 */
	static class MaxIntegerProductCandidate implements Comparable<MaxIntegerProductCandidate> {

		/**
		 *  Masks to indicate how many selections to take from each list.
		 */
		enum Scenario {
			SCENARIO1, 		// 1) first three positives (desc) -- simple case
			SCENARIO2, 		// 2) first positive (desc) and first 2 negatives (asc) -- cancel the negatives
			SCENARIO3,		// 3) first 3 negatives (desc) -- best of the worst scenario
			SCENARIO4;		// 4) first 2 positives (asc) and first negative (desc) -- another best of the worst
		};

		/**
		 * Map indicating how many values to take from each list for each scenario.
		 */
		private static final Map<Scenario, Map<SortedIntegerListType, Integer>> ScenarioMasks;
		static {
			ScenarioMasks = new HashMap<Scenario, Map<SortedIntegerListType, Integer>>();

			Map<SortedIntegerListType, Integer> scenario1 = new HashMap<>();
			scenario1.put(SortedIntegerListType.POSITIVES_ASC, 0);
			scenario1.put(SortedIntegerListType.NEGATIVES_ASC, 0);
			scenario1.put(SortedIntegerListType.POSITIVES_DESC, 3);
			scenario1.put(SortedIntegerListType.NEGATIVES_DESC, 0);
			ScenarioMasks.put(Scenario.SCENARIO1, scenario1);

			Map<SortedIntegerListType, Integer> scenario2 = new HashMap<>();
			scenario2.put(SortedIntegerListType.POSITIVES_ASC, 0);
			scenario2.put(SortedIntegerListType.NEGATIVES_ASC, 2);
			scenario2.put(SortedIntegerListType.POSITIVES_DESC, 1);
			scenario2.put(SortedIntegerListType.NEGATIVES_DESC, 0);
			ScenarioMasks.put(Scenario.SCENARIO2, scenario2);

			Map<SortedIntegerListType, Integer> scenario3 = new HashMap<>();
			scenario3.put(SortedIntegerListType.POSITIVES_ASC, 0);
			scenario3.put(SortedIntegerListType.NEGATIVES_ASC, 0);
			scenario3.put(SortedIntegerListType.POSITIVES_DESC, 0);
			scenario3.put(SortedIntegerListType.NEGATIVES_DESC, 3);
			ScenarioMasks.put(Scenario.SCENARIO3, scenario3);

			Map<SortedIntegerListType, Integer> scenario4 = new HashMap<>();
			scenario4.put(SortedIntegerListType.POSITIVES_ASC, 2);
			scenario4.put(SortedIntegerListType.NEGATIVES_ASC, 0);
			scenario4.put(SortedIntegerListType.POSITIVES_DESC, 0);
			scenario4.put(SortedIntegerListType.NEGATIVES_DESC, 1);
			ScenarioMasks.put(Scenario.SCENARIO4, scenario4);
		}
		
		// Which Scenario is this?
		private Scenario scenario;
		
		// Three factors resulting in maxProduct
		private List<Integer> factors = null;
		
		// Maximum product possible from integers in rawList 
		private Integer maxProduct = null; 
		
		// Flag to indicate the product of these values would generate INT32 overflow
		boolean willOverflow = false;
		
		/**
		 * If the object returned by this constructor has a null value for factors, it shouldn't be used.
		 * @param scenario scenarios for calculating a max product as described above
		 * @param lists of the positive and negative integers in the list sorted by ASC and DESC
		 */
		MaxIntegerProductCandidate(Scenario scenario, Map<SortedIntegerListType, List<Integer>> lists) {
			this.scenario = scenario;
			factors = this.getFactors(lists);
			if (factors != null) {
				willOverflow = productOverflows();
				if (willOverflow) {
					throw new IllegalArgumentException("A product of " + RUN_LENGTH + " integers overflows INT32");
				}
				maxProduct = willOverflow ? Integer.MIN_VALUE : calcProduct();
			}
		}
		
		private List<Integer> getFactors(Map<SortedIntegerListType, List<Integer>> lists) {
			List<Integer> result = new ArrayList<Integer>(RUN_LENGTH);

			for (SortedIntegerListType listType : SortedIntegerListType.values()) {
				int valuesNeededFromCurrentList = ScenarioMasks.get(scenario).get(listType);
				if (valuesNeededFromCurrentList > 0) {
					List<Integer> values = SortedIntegerListType.bestOf(valuesNeededFromCurrentList, listType, lists);
					
					// If we got all the factors we expected, add them; otherwise, return a null to indicate
					// that this candidate shouldn't be considered
					if (null != values) {
						result.addAll(values);
					} else {
						// bail and return null
						return null;
					}
				}
			}

			return result;
		}

		private boolean productOverflows() {

			long longProduct = 1L;
			int intProduct = 1;
			for (int i = 0; i < RUN_LENGTH; i++) {
				longProduct *= factors.get(i);
				intProduct *=  factors.get(i);
			}
			return (intProduct != longProduct);
		}

		private int calcProduct() {
			int result = 1;
			for (int i = 0; i < RUN_LENGTH; i++) {
				result *= factors.get(i); 
			}
			return result;
		}
	
		@Override
		public int compareTo(MaxIntegerProductCandidate other) {
			// These first two cases are precautionary; we're supposed to blow up
			// if we're gonna overflow
			if (this.willOverflow) {
				return -1;
			} else if (other.willOverflow) {
				return 1;
			} else {
				return this.maxProduct - other.maxProduct;
			}
		}
	}

	//private IntegerList rawList;
	private MaxIntegerProductCandidate result;

	/**
	 * Lists of positive and negative integers ordered in both directions
	 * for ease of selecting best values.
	 *
	 * Note: this could be done without duplicating and reversing the
	 * positives and negatives list for greater efficiency at the cost
	 * of further complexity.
	 */
	enum SortedIntegerListType {
		POSITIVES_ASC, NEGATIVES_ASC,
		POSITIVES_DESC, NEGATIVES_DESC;
		

		/**
		 * Sort the ints into positive and negative ordered both ways and provide
		 * convenient array addressing. 
		 * @param rawList
		 * @return
		 */
		static Map<SortedIntegerListType, List<Integer>> generateSublists(List<Integer> rawList) {
			Map<SortedIntegerListType, List<Integer>> result = new HashMap<>();

			for (SortedIntegerListType key : SortedIntegerListType.values()) {
				result.put(key, new ArrayList<Integer>());
			}

			for (Integer value : rawList) {
				if (value > 0) {
					result.get(POSITIVES_ASC).add(value);
				} else {
					result.get(NEGATIVES_ASC).add(value);
				}
			}
			
			// Sort them
			Collections.sort(result.get(POSITIVES_ASC));
			Collections.sort(result.get(NEGATIVES_ASC));
			
			// Clone and reverse first two lists to get the other two
			result.get(POSITIVES_DESC).addAll(result.get(POSITIVES_ASC));
			Collections.reverse(result.get(POSITIVES_DESC));

			result.get(NEGATIVES_DESC).addAll(result.get(NEGATIVES_ASC));
			Collections.reverse(result.get(NEGATIVES_DESC));

			return result;
		}
		
		/**
		 * Convenience method for selecting the top-ranking (first) N values of the desired
		 * type from the computed Integer lists.
		 * @param count -- number of best selections desired
		 * @param type -- which list to select from
		 * @param lists -- the array of lists
		 * @return array of best selections, or null if the list isn't long enough
		 */
		static List<Integer> bestOf(int count, SortedIntegerListType type, Map<SortedIntegerListType, List<Integer>> lists) {
			List<Integer> list = lists.get(type);
			if (list.size() < count) {
				return null;
			} else {
				return lists.get(type).subList(0, count);
			}
		}
	};
	
	public IntegerProduct(IntegerList rawList) {
		if (null == rawList || rawList.values().size() < 3) {
			throw new IllegalArgumentException("IntegerList cannot be null or contain less than three Integers");
		}

		Map<SortedIntegerListType, List<Integer>> lists = SortedIntegerListType.generateSublists(rawList.values());

		List<MaxIntegerProductCandidate> candidates = new ArrayList<>();
		for (MaxIntegerProductCandidate.Scenario scenario : MaxIntegerProductCandidate.Scenario.values()) {
			MaxIntegerProductCandidate candidate = new MaxIntegerProductCandidate(scenario, lists);
			if (candidate.factors != null) {
				candidates.add(candidate); 
			}
		}
		
		Collections.sort(candidates);
		Collections.reverse(candidates);
		result = candidates.get(0);
	}
	
	// The answer
	public int getMaxProduct() {
		return result.maxProduct;
	}

	// The values that contributed to the answer
	public List<Integer> getFactors() {
		return result.factors;
	}

}
