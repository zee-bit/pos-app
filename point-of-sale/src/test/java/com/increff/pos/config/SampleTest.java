package com.increff.pos.service;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

public class SampleTest {

	@Test
	public void testFiles() {
		InputStream is = null;
		is = SampleTest.class.getResourceAsStream("/com/increff/pos/brand.tsv");
		assertNotNull(is);
	}

}
