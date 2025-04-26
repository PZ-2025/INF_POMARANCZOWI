package com.orange.bookmanagment.book.model.enums;

/**
 * <p>Enum representing the status of a book in the system.</p>
 *
 * <p>Possible statuses are:</p>
 * <ul>
 *   <li>AVAILABLE - The book is available for borrowing.</li>
 *   <li>BORROWED - The book is currently borrowed.</li>
 *   <li>RESERVED - The book is reserved by a user.</li>
 *   <li>LOST - The book is lost.</li>
 * </ul>
 */
public enum BookStatus {
    AVAILABLE, BORROWED, RESERVED, LOST;


    BookStatus() {
    }

    public static boolean existsByName(String name){
        for(BookStatus bookStatus : BookStatus.values()){
            if(bookStatus.name().equals(name)){
                return true;
            }
        }
        return false;
    }
}
