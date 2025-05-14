package com.orange.bookmanagment.shared.enums;

/**
 * Enum reprezentujący status książki w systemie.
 *
 * <p>Dostępne statusy:</p>
 * <ul>
 *   <li>{@code AVAILABLE} – Książka dostępna do wypożyczenia.</li>
 *   <li>{@code BORROWED} – Książka aktualnie wypożyczona.</li>
 *   <li>{@code RESERVED} – Książka zarezerwowana przez użytkownika.</li>
 *   <li>{@code LOST} – Książka została zgubiona.</li>
 * </ul>
 */
public enum BookStatus {
    AVAILABLE,
    BORROWED,
    RESERVED,
    LOST;

    BookStatus() { }

    /**
     * Sprawdza, czy podana nazwa odpowiada któremukolwiek ze statusów książki.
     *
     * @param name nazwa statusu do sprawdzenia
     * @return {@code true}, jeśli status istnieje; w przeciwnym razie {@code false}
     */
    public static boolean existsByName(String name) {
        for (BookStatus bookStatus : BookStatus.values()) {
            if (bookStatus.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
