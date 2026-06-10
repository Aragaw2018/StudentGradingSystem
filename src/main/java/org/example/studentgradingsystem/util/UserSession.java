package org.example.studentgradingsystem.util;

public class UserSession {
    private static int userId;
    private static String username;
    private static String fullName;


    public static void init(int id, String user, String name) {
        userId = id;
        username = user;
        fullName = name;
    }

    public static int getUserId() { return userId; }
    public static String getFullName() { return fullName; }

    public static void clean() {
        userId = 0;
        username = null;
        fullName = null;
    }
}