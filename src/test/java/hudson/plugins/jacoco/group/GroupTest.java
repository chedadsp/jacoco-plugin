package hudson.plugins.jacoco.group;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class GroupTest {
	
	private Group group;
	
	@Before
    public void setUp() {
		PackagePrefix pPrefix = new PackagePrefix("test.package");
		
		List<PackagePrefix> prefixes = new LinkedList<> ();
		prefixes.add(pPrefix);
		
		group = new Group("test name", prefixes);
	}
	
	@Test
	public void test() {
		assertEquals("test name", group.getName());
		assertNotNull(group.getPrefixes());
		assertEquals(1, group.getPrefixes().size());
		
		Group group2 = new Group("test name", null);
		assertTrue(group.equals(group2));
	}

}
