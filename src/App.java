import java.util.Scanner;
import java.util.regex.Pattern;

/*
 *PROG5121 Part 1 – Registration and Login feature.
 */
public class App {

    private String storedUsername;
    private String storedPassword;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn = false;

    /**
     * Username must contain an underscore and be no longer than 5 characters.
     */
    public boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    /**
     * Password:
     * - at least 8 characters
     * - at least one capital letter
     * - at least one number
     * - at least one special character
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    /**
     * South African cell number must start with international code (+27)
     * followed by a number that is no more than 10 characters long.
     */
    public boolean checkCellPhoneNumber(String cell) {
        if (cell == null) return false;
        // +27 followed by 9 digits (total length of the number part <= 10)
        String regex = "^\\+27[0-9]{9}$";
        return Pattern.matches(regex, cell);
    }

    /**
     * Returns the appropriate registration message.
     * Stores the details only when all three checks pass.
     */
    public String registerUser(String username, String password, String cell,
                               String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cell)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        // All checks passed – store the details
        this.storedUsername = username;
        this.storedPassword = password;
        this.firstName = firstName;
        this.lastName = lastName;

        return "Username successfully captured.\n"
             + "Password successfully captured.\n"
             + "Cell phone number successfully added.";
    }

    /**
     * Verifies that the entered credentials match the stored ones.
     */
    public boolean loginUser(String username, String password) {
        if (storedUsername == null || storedPassword == null) {
            return false;
        }
        isLoggedIn = storedUsername.equals(username)
                  && storedPassword.equals(password);
        return isLoggedIn;
    }

    /**
     * Returns the login status message.
     */
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + ", " + lastName
                 + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // ---------- Helper for main ----------
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // ---------- Console driver ----------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        App login = new App();

        System.out.println("=== QuickChat Registration ===");
        System.out.print("Enter first name: ");
        String firstName = sc.nextLine().trim();
        System.out.print("Enter last name: ");
        String lastName = sc.nextLine().trim();

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        System.out.print("Enter SA cell phone number (e.g. +27838968976): ");
        String cell = sc.nextLine().trim();

        String regResult = login.registerUser(username, password, cell, firstName, lastName);
        System.out.println(regResult);

        // Only proceed to login if registration was successful
        if (regResult.contains("successfully")) {
            System.out.println("\n=== Login ===");
            System.out.print("Username: ");
            String loginUser = sc.nextLine().trim();
            System.out.print("Password: ");
            String loginPass = sc.nextLine().trim();

            System.out.println(login.returnLoginStatus(loginUser, loginPass));
        }

        sc.close();
    }
}


