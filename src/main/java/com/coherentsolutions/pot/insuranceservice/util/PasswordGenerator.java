package com.coherentsolutions.pot.insuranceservice.util;

import java.security.SecureRandom;

/**
 * Utility class for generating secure random passwords.
 * 
 * <p>Generates passwords that meet common security requirements:
 * - At least 8 characters long
 * - Contains uppercase letters
 * - Contains lowercase letters
 * - Contains numbers
 * - Contains special characters
 */
public class PasswordGenerator {

  private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
  private static final String NUMBERS = "0123456789";
  private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

  private static final SecureRandom RANDOM = new SecureRandom();

  public static String generateSecurePassword() {
    StringBuilder password = new StringBuilder();

    // Ensure at least one character from each required category
    password.append(getRandomChar(UPPERCASE_LETTERS));
    password.append(getRandomChar(LOWERCASE_LETTERS));
    password.append(getRandomChar(NUMBERS));
    password.append(getRandomChar(SPECIAL_CHARACTERS));

    // Fill the rest with random characters from all categories
    String allCharacters = UPPERCASE_LETTERS + LOWERCASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS;
    for (int i = 4; i < 12; i++) {
      password.append(getRandomChar(allCharacters));
    }

    // Shuffle the password to make it more random
    return shuffleString(password.toString());
  }

  public static String generateSecurePassword(int length) {
    if (length < 8) {
      throw new IllegalArgumentException("Password length must be at least 8 characters");
    }

    StringBuilder password = new StringBuilder();

    // Ensure at least one character from each required category
    password.append(getRandomChar(UPPERCASE_LETTERS));
    password.append(getRandomChar(LOWERCASE_LETTERS));
    password.append(getRandomChar(NUMBERS));
    password.append(getRandomChar(SPECIAL_CHARACTERS));

    // Fill the rest with random characters from all categories
    String allCharacters = UPPERCASE_LETTERS + LOWERCASE_LETTERS + NUMBERS + SPECIAL_CHARACTERS;
    for (int i = 4; i < length; i++) {
      password.append(getRandomChar(allCharacters));
    }

    // Shuffle the password to make it more random
    return shuffleString(password.toString());
  }

  private static char getRandomChar(String characters) {
    return characters.charAt(RANDOM.nextInt(characters.length()));
  }

  private static String shuffleString(String input) {
    char[] characters = input.toCharArray();
    for (int i = characters.length - 1; i > 0; i--) {
      int index = RANDOM.nextInt(i + 1);
      char temp = characters[index];
      characters[index] = characters[i];
      characters[i] = temp;
    }
    return new String(characters);
  }
} 
