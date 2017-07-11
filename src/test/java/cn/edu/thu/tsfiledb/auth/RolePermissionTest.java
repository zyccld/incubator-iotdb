package cn.edu.thu.tsfiledb.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.thu.tsfiledb.auth.dao.DBDao;
import cn.edu.thu.tsfiledb.auth.dao.RoleDao;
import cn.edu.thu.tsfiledb.auth.dao.RolePermissionDao;
import cn.edu.thu.tsfiledb.auth.model.Permission;
import cn.edu.thu.tsfiledb.auth.model.Role;
import cn.edu.thu.tsfiledb.auth.model.RolePermission;
import cn.edu.thu.tsfiledb.conf.TsfileDBConfig;
import cn.edu.thu.tsfiledb.conf.TsfileDBDescriptor;

public class RolePermissionTest {

	private DBDao dbDao = null;
	private RoleDao roleDao = null;
	private RolePermissionDao rolePermissionDao = null;
	private Statement statement;

	private Role role = new Role("role");
	private String nodeName = "nodeName";
	private String newNodeName = "newNodeName";
	private int permissionId;
	private RolePermission rolePermission = null;
	private TsfileDBConfig dbconfig = TsfileDBDescriptor.getInstance().getConfig();

	@Before
	public void setUp() throws Exception {
		dbconfig.derbyHome = "";
		dbDao = new DBDao();
		roleDao = new RoleDao();
		rolePermissionDao = new RolePermissionDao();
		permissionId = Permission.CREATE;

		dbDao.open();
		statement = DBDao.getStatement();

		// if role not exist, create role
		if (roleDao.getRole(statement, role.getRoleName()) == null) {
			roleDao.createRole(statement, role);
		}
		rolePermission = new RolePermission(roleDao.getRole(statement, role.getRoleName()).getId(), nodeName,
				permissionId);
	}

	@After
	public void tearDown() throws Exception {
		roleDao.deleteRole(statement, role.getRoleName());
		dbDao.close();
	}

	@Test
	public void test() {

		// create the role permission
		int state = 0;
		state = rolePermissionDao.deleteRolePermission(statement, rolePermission);
		state = rolePermissionDao.createRolePermission(statement, rolePermission);
		RolePermission permission = rolePermissionDao.getRolePermission(statement, rolePermission);
		assertEquals(1, state);
		assertNotNull(permission);
		assertEquals(rolePermission.getRoleId(), permission.getRoleId());
		assertEquals(rolePermission.getNodeName(), permission.getNodeName());
		assertEquals(rolePermission.getPermissionId(), permission.getPermissionId());
		// delete the role permission
		state = rolePermissionDao.deleteRolePermission(statement, rolePermission);
		assertEquals(1, state);
		permission = rolePermissionDao.getRolePermission(statement, rolePermission);
		assertNull(permission);

	}

	@Test
	public void testGetRoles() {
		RolePermission rolePermission1 = new RolePermission(roleDao.getRole(statement, role.getRoleName()).getId(),
				nodeName, Permission.CREATE);
		RolePermission rolePermission2 = new RolePermission(roleDao.getRole(statement, role.getRoleName()).getId(),
				nodeName, Permission.DELETE);
		rolePermissionDao.createRolePermission(statement, rolePermission1);
		rolePermissionDao.createRolePermission(statement, rolePermission2);
		RolePermission rolePermission3 = new RolePermission(roleDao.getRole(statement, role.getRoleName()).getId(),
				newNodeName, Permission.CREATE);
		rolePermissionDao.createRolePermission(statement, rolePermission3);
		assertEquals(3, rolePermissionDao.getRolePermissions(statement).size());
		assertEquals(1, rolePermissionDao.getRolePermissionByRoleAndNodeName(statement,
				roleDao.getRole(statement, role.getRoleName()).getId(), newNodeName).size());

	}

}