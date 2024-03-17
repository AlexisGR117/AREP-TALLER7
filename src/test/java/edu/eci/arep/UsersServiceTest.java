package edu.eci.arep;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UsersServiceTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UsersServiceTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(UsersServiceTest.class);
    }

    public void testHashPassword() throws NoSuchAlgorithmException {
        String password = "password123";
        byte[] expectedHash = {-17, -110, -73, 120, -70, -2, 119, 30, -119, 36, 91, -119, -20, -68, 8, -92, 74, 78, 22, 108, 6, 101, -103, 17, -120, 31, 56, 61, 68, 115, -23, 79};
        byte[] actualHash = UsersService.hashPassword(password);
        assertTrue(Arrays.equals(expectedHash, actualHash));
    }

    public void testHashPasswordWithEmptyPassword() throws NoSuchAlgorithmException {
        String password = "";
        byte[] expectedHash = {-29, -80, -60, 66, -104, -4, 28, 20, -102, -5, -12, -56, -103, 111, -71, 36, 39, -82, 65, -28, 100, -101, -109, 76, -92, -107, -103, 27, 120, 82, -72, 85};
        byte[] actualHash = UsersService.hashPassword(password);
        assertTrue(Arrays.equals(expectedHash, actualHash));
    }


    public void testVerifyValidLogin() throws NoSuchAlgorithmException {
        String name = "Alexis";
        String password = "password123";
        UsersService.addUser(name, password);
        assertTrue(UsersService.verifyPassword(name, password));
    }

    public void testVerifyInvalidPassword() throws NoSuchAlgorithmException {
        String name = "Alexis";
        String password = "password123";
        UsersService.addUser(name, password);
        assertFalse(UsersService.verifyPassword(name, "123password"));
    }

    public void testVerifyInvalidUser() throws NoSuchAlgorithmException {
        String name = "Alexis";
        String password = "password123";
        UsersService.addUser(name, password);
        assertFalse(UsersService.verifyPassword("Jefer", password));
    }

}