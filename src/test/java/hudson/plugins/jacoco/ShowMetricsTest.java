package hudson.plugins.jacoco;

import static org.junit.Assert.*;

import org.junit.Test;

public class ShowMetricsTest {
	
	@Test
	public void test() {
		
		ShowMetrics showMetrics = new ShowMetrics(false, false, false, false, false, false);
	
		assertFalse(showMetrics.isShowInstruction());
		assertFalse(showMetrics.isShowBranch());
		assertFalse(showMetrics.isShowComplexity());
		assertFalse(showMetrics.isShowLine());
		assertFalse(showMetrics.isShowMethod());
		assertFalse(showMetrics.isShowClass());
		
		showMetrics.setShowBranch(true);
		assertTrue(showMetrics.isShowBranch());
		
		showMetrics.setShowClass(true);
		assertTrue(showMetrics.isShowClass());
		
		showMetrics.setShowComplexity(true);
		assertTrue(showMetrics.isShowComplexity());
		
		showMetrics.setShowInstruction(true);
		assertTrue(showMetrics.isShowInstruction());
		
		showMetrics.setShowLine(true);
		assertTrue(showMetrics.isShowLine());
		
		showMetrics.setShowMethod(true);
		assertTrue(showMetrics.isShowMethod());

	}
}
