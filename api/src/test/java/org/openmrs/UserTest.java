package org.openmrs;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.util.RoleConstants;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
	
	private User user;
	
	@Before
	public void setUp() throws Exception {
		user = new User();
		user.addRole(new Role("Some Role", "This is a test role"));
	}
	
	@Test
	public void hasRole_shouldHaveRole() throws Exception {
		assertTrue(user.hasRole("Some Role"));
	}
	
	@Test
	public void hasRole_shouldNotHaveRole() throws Exception {
		assertFalse(user.hasRole("Not A Role"));
	}
	
	@Test
	public void hasRole_shouldHaveAnyRoleWhenSuperUser() throws Exception {
		user.addRole(new Role(RoleConstants.SUPERUSER));
		assertTrue(user.hasRole("Not A Role"));
	}
	
	@Test
	public void hasRole_shouldNotHaveAnyRoleWhenSuperWhenIgnoreSuperUserFlagIsTrue() throws Exception {
		user.addRole(new Role(RoleConstants.SUPERUSER));
		assertFalse(user.hasRole("Not A Role", true));
	}
	
	@Test
	public void isSuperUser_shouldBeSuperUser() throws Exception {
		user.addRole(new Role(RoleConstants.SUPERUSER));
		assertTrue(user.isSuperUser());
	}
	
	@Test
	public void isSuperUser_shouldNotBeSuperUser() throws Exception {
		assertFalse(user.isSuperUser());
	}

    /**
     * @verifies be case insensitive
     * @see User#containsRole(String)
     */
    @Test
    public void containsRole_shouldBeCaseInsensitive() throws Exception {
        user.addRole(new Role("Maternity Nurse"));
        assertTrue(user.containsRole("Maternity Nurse"));
        assertTrue(user.containsRole("maternity nurse"));
    }

    /**
     * @verifies return true if the user has the given role
     * @see User#containsRole(String)
     */
    @Test
    public void containsRole_shouldReturnTrueIfTheUserHasTheGivenRole() throws Exception {
        user.addRole(new Role("Maternity Nurse"));
        assertTrue(user.containsRole("Maternity Nurse"));
    }

    /**
     * @verifies return false if the user does not have the given role
     * @see User#containsRole(String)
     */
    @Test
    public void containsRole_shouldReturnFalseIfTheUserDoesNotHaveTheGivenRole() throws Exception {
        assertFalse(user.containsRole("Role Which Does Not Exist"));
    }
}
