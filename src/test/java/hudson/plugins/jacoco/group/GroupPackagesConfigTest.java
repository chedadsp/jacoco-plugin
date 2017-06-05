package hudson.plugins.jacoco.group;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GroupPackagesConfigTest {
	
	private GroupPackagesConfig config;
	
	@Before
    public void setUp() {
		PackagePrefix pPrefix = new PackagePrefix("test.package");
		
		List<PackagePrefix> prefixes = new LinkedList<> ();
		prefixes.add(pPrefix);
		
		Group group = new Group("test name", prefixes);
		
		List<Group> groups = new LinkedList<>();
		groups.add(group);
		
		config = new GroupPackagesConfig(true, groups);
	}
	
	@Test
	public void test() {
		assertNotNull(config);
		assertTrue(config.isShowMismatched());
		assertNotNull(config.getGroups());
		assertEquals(1, config.getGroups().size());
		
		config.setShowMismatched(false);
		assertFalse(config.isShowMismatched());
		
		config.setGroups(null);
		assertNull(config.getGroups());
	}

}
