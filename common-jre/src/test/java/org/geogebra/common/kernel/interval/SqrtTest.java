package org.geogebra.common.kernel.interval;

import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SqrtTest {

	@Test
	public void sqrtOfSqrtXAtZeroShouldBeZero() {
		// sqrt(sqrt([0])) == 0
		Interval inner = zero().sqrt();
		Interval outer = inner.sqrt();
		assertEquals(zero(), outer);
	}

	@Test
	public void sqrtOfSqrtXAroundZeroShouldBeZero() {
		// sqrt(sqrt([low..0..high])) == 0
		Interval inner = aroundZero().sqrt();
		Interval outer = inner.sqrt();
		assertEquals(zero(), outer);
	}

	private Interval aroundZero() {
		return interval(-1E-6, 1E-6);
	}

	@Test
	public void sqrtOfXInverseShouldBePositiveInfinityAtZero() {
		// sqrt(1/[0]) == [0, ∞]
		assertEquals(interval(0, POSITIVE_INFINITY), zero().multiplicativeInverse().sqrt());
	}

	@Test
	public void sqrtOfXInverseShouldBePositiveInfinityAroundZero() {
		// 1/sqrt([0]) == ∞
		assertEquals(invertedInterval(0, 1.0 / Math.sqrt(1E-6)),
				aroundZero().multiplicativeInverse().sqrt());
	}

	@Test
	public void testSqrtSecCotX() {
		Interval cot = piHalf().cot();
		Interval sec = cot.sec();
		Interval result = sec.sqrt();
		assertEquals(IntervalConstants.one(), result);
	}

	@Test
	public void SqrtOfInverseOfXInverseShouldBeZeroAtZero() {
		Interval result =
				interval(0).multiplicativeInverse().multiplicativeInverse();
		Interval root = result.sqrt();
		assertEquals(zero(), root);
	}

	@Test
	public void SqrtOfInverseOfXInverseShouldBeZeroAroundZero() {
		Interval result =
				aroundZero().multiplicativeInverse().multiplicativeInverse();
		Interval root = result.sqrt();
		assertEquals(interval(0, 0.001), root);
	}

	@Test
	public void testSqrtSqrtOfXInverse() {
		Interval result =
				interval(0).sqrt().sqrt();
		Interval inverse = result.multiplicativeInverse();
		assertEquals(interval(POSITIVE_INFINITY), inverse);
	}
}
