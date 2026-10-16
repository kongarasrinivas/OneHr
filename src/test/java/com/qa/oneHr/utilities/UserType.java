package com.qa.oneHr.utilities;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum UserType {
    SUPER_ADMIN("Super Admin", "SuperAdminUserName", "SuperAdminPwd"),
    EMPLOYEE("Employee", "employee.username", "employee.password"),
    MANAGER("Manager", "manager.username", "manager.password"),
    HR_ADMIN("HR Admin", "hradmin.username", "hradmin.password");

    private static final Map<String, UserType> BY_LABEL = Arrays.stream(values())
            .collect(Collectors.toMap(type -> type.label.toLowerCase(), type -> type));

    private final String label;
    private final String usernameKey;
    private final String passwordKey;

    UserType(String label, String usernameKey, String passwordKey) {
        this.label = label;
        this.usernameKey = usernameKey;
        this.passwordKey = passwordKey;
    }

    public static UserType fromLabel(String label) {
        UserType type = BY_LABEL.get(label.toLowerCase());
        if (type == null) {
            throw new IllegalArgumentException("Unknown user type: " + label);
        }
        return type;
    }

    // Single reusable method: adding a new role only means adding one enum
    // constant above with its config keys - no other code needs to change.
    public Credentials getCredentials() {
        String username = ConfigurationUtilities.getProperty(usernameKey);
        String password = ConfigurationUtilities.getProperty(passwordKey);
        if (username == null || password == null) {
            throw new IllegalStateException(
                "Missing '" + usernameKey + "' / '" + passwordKey + "' in configuration.properties for user type '" + label + "'");
        }
        return new Credentials(username, password);
    }

    public record Credentials(String username, String password) {}
}
