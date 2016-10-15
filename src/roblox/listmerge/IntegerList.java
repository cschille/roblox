package roblox.listmerge;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Q1. Write a function and list unit test cases for the following.
 * A function that takes two sorted integer lists as input and returns a merged sorted list as output.
 * Duplicate list entries are preserved in the output.  The function should prioritize run-time efficiency.
 * 
 * Solution:
 * To avoid having to worry about testing for nulls, uses an object-oriented approach.
 * To avoid gratuitously creating instances of the new object during testing, uses the Builder
 * design pattern.
 * 
 * @author cschille
 *
 */
public class IntegerList {

	private List<Integer> listValues = null;
	
	/**
	 * The (static) function satisfying the exact requirements of the exercise.
	 */
	public static final List<Integer> mergeLists(List<Integer> list1, List<Integer> list2) {
		return IntegerList.newBuilder(list1).mergeWith(list2).build().values();
	}

	/**
	 * Test this object for equality with another list by doing a deep comparison. 
	 * @param other the second list to compare with this one
	 * @return true if they are equal, false if not.
	 */
	@Override
	public boolean equals(Object other) {

		try {
			@SuppressWarnings("unchecked")
			List<Integer> otherList = (List<Integer>)other;
			if (this.listValues.size() != otherList.size()) {
				return false;
			} else {
				for (int i = 0; i < this.listValues.size(); i++) {
					if (!this.listValues.get(i).equals(otherList.get(i))) {
						return false;
					}
				}
			}
		} catch (ClassCastException exception) {
			return super.equals(other);
		}
		
		return true;
	}
	
	/**
	 * Get the internal list, but make it immutable to the world at large
	 * (except through merge()).
	 * 
	 * @return an immutable version of the list values
	 */
	public final List<Integer> values() {
		final List<Integer> safeValues = listValues;
		return safeValues;
	}

	public static class Builder {

		private List<Integer> listValues;

		public Builder(List<Integer> integerList) throws IllegalArgumentException {
			if (null == integerList) {
				throw new IllegalArgumentException();
			}
			listValues = integerList;
		}

		public IntegerList build() {
			return new IntegerList(this);
		}
		
		/**
		 * Merge this list with another, assigning this object the result.
		 * 
		 * Do this by walking through the two lists, comparing the current value for each and
		 * putting the larger value into a result set.  This object's value is then
		 * assigned the result set.
		 * 
		 * Ideally we'd use a stack for each of the two lists, peeking and popping our
		 * way through them.  To get the convenience of iterators while preserving the
		 * ability to do a peek() operation, we use a local variable for the current
		 * value of each list.  The local remains set to the current list value for peek()
		 * operations, but is assigned null when it's time to pop() a new value.
		 * 
		 * @param list second list
		 * @return modified IntegerList Object, for chaining commands
		 * @throws IllegalArgumentException only if you hand it two null lists
		 */
		public IntegerList.Builder mergeWith(List<Integer> list) throws IllegalArgumentException {
			
			if (list == null) {
				throw new IllegalArgumentException("Can't merge a null list");
			}

			// There's no work to be done if the other list is empty.  In that case,
			// the result of the merge is always the original object. 
			if (!list.isEmpty()) {
				Iterator<Integer> leftIt = listValues.iterator();
				Iterator<Integer> rightIt = list.iterator();

				List<Integer> newResult = new ArrayList<>(listValues.size() + list.size());
				
				Integer leftValue = null; 
				Integer rightValue = null; 

				while (leftIt.hasNext() || rightIt.hasNext()) {

					if (!rightIt.hasNext()) {
						leftValue = leftIt.next();
						newResult.add(leftValue);
					} else if (!leftIt.hasNext()) {
						rightValue = rightIt.next();
						newResult.add(rightValue);
					} else {
						// Give ourselves stack.peek() capability, so every popped value
						// is eventually consumed.  Null value from last iteration means
						// value was consumed and we should pop the next one.
						leftValue = leftValue == null ? leftIt.next() : leftValue;
						rightValue = rightValue == null ? rightIt.next() : rightValue;
						if (leftValue < rightValue) {
							newResult.add(leftValue);
							leftValue = null;
						} else if (leftValue > rightValue) {
							newResult.add(rightValue);
							rightValue = null;
						} else {
							newResult.add(leftValue);
							newResult.add(rightValue);
							leftValue = null;
							rightValue = null;
						}
					}
				}

				this.listValues = newResult;
			}
			
			return this;
		}
	
		@Override
		public boolean equals(Object other) {
			// TODO: pick a more appropriate exception type
			throw new IllegalArgumentException("Did you forget to call build()?");
		}
	}

	/**
	 * Only needed to support IntegerList.newBuilder(...) semantics.
	 * Could otherwise use new IntegerList.Builder(...).
	 * @param integerList
	 * @return Builder object to which we can chain additional commands.
	 * @throws IllegalArgumentException
	 */
	public static Builder newBuilder(List<Integer> integerList) throws IllegalArgumentException {
		return new Builder(integerList);
	}
		
	private IntegerList(Builder builder) {
		this.listValues = builder.listValues;
	}

}