package com.orange.bookmanagment.order.service;

import com.orange.bookmanagment.order.exception.InvalidOrderArgumentException;
import com.orange.bookmanagment.order.exception.OrderNotFoundException;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.web.requests.OrderCreateRequest;
import com.orange.bookmanagment.order.web.requests.OrderPriorityUpdateRequest;
import com.orange.bookmanagment.order.web.requests.OrderStatusUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

/**
 * Interfejs serwisu obsługującego logikę biznesową związaną z zamówieniami książek.
 *
 * <p>OrderService definiuje kontrakt dla wszystkich operacji biznesowych na zamówieniach,
 * włączając w to tworzenie, pobieranie, aktualizację i finalizację zamówień. Serwis stanowi
 * warstwę abstrakcji między kontrolerami a repozytoriami, enkapsulując złożoną logikę biznesową.</p>
 *
 * <p>Interfejs obsługuje różne sposoby wyszukiwania zamówień (po ID, statusie, priorytecie,
 * dostawcy, dacie), zarządzanie cyklem życia zamówienia oraz walidację danych wejściowych.</p>
 *
 * <p>Wszystkie metody modyfikujące dane wykonują odpowiednią walidację i rzucają specjalistyczne
 * wyjątki w przypadku błędów. Serwis obsługuje także stronicowanie dla wydajnego przetwarzania
 * dużych zbiorów danych.</p>
 *
 * @since 1.0
 * @see Order
 * @see OrderCreateRequest
 * @see OrderStatusUpdateRequest
 * @see OrderPriorityUpdateRequest
 * @see InvalidOrderArgumentException
 * @see OrderNotFoundException
 */
public interface OrderService {

   /**
    * Tworzy nowe zamówienie na podstawie danych z żądania.
    *
    * <p>Metoda waliduje dane wejściowe, tworzy nową instancję zamówienia z automatycznym
    * ustawieniem statusu na PLACED oraz czasów zamówienia. Wykonuje również walidację
    * biznesową danych zamówienia.</p>
    *
    * @param orderCreateRequest obiekt zawierający dane nowego zamówienia; nie może być null
    * @return utworzone i zapisane zamówienie z przypisanym ID
    * @throws InvalidOrderArgumentException jeśli dane zamówienia są nieprawidłowe lub niekompletne
    * @throws IllegalArgumentException jeśli orderCreateRequest jest null
    */
   Order createOrder(OrderCreateRequest orderCreateRequest) throws InvalidOrderArgumentException;

   /**
    * Pobiera zamówienie na podstawie unikalnego identyfikatora.
    *
    * @param id identyfikator zamówienia do pobrania; musi być dodatni
    * @return zamówienie o podanym identyfikatorze
    * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje
    * @throws IllegalArgumentException jeśli id jest ujemne lub równe zero
    */
   Order getOrderById(long id) throws OrderNotFoundException;

   /**
    * Pobiera wszystkie zamówienia o określonym priorytecie.
    *
    * @param orderPriority priorytet zamówień jako string (np. "HIGH", "MEDIUM", "LOW"); nie może być null ani pusty
    * @return lista zamówień o podanym priorytecie; może być pusta ale nigdy null
    * @throws InvalidOrderArgumentException jeśli podany priorytet nie jest prawidłową wartością enum
    * @throws IllegalArgumentException jeśli orderPriority jest null lub pusty
    */
   List<Order> getOrdersByOrderPriority(String orderPriority) throws InvalidOrderArgumentException;

   /**
    * Pobiera wszystkie zamówienia o określonym statusie.
    *
    * @param orderStatus status zamówień jako string (np. "PLACED", "PROCESSING", "COMPLETED"); nie może być null ani pusty
    * @return lista zamówień o podanym statusie; może być pusta ale nigdy null
    * @throws InvalidOrderArgumentException jeśli podany status nie jest prawidłową wartością enum
    * @throws IllegalArgumentException jeśli orderStatus jest null lub pusty
    */
   List<Order> getOrdersByOrderStatus(String orderStatus) throws InvalidOrderArgumentException;

   /**
    * Pobiera zamówienia spełniające jednocześnie kryteria priorytetu i statusu.
    *
    * <p>Metoda pozwala na precyzyjne filtrowanie zamówień według dwóch kryteriów jednocześnie,
    * co jest użyteczne przy złożonych zapytaniach biznesowych.</p>
    *
    * @param orderPriority priorytet zamówień jako string; nie może być null ani pusty
    * @param orderStatus status zamówień jako string; nie może być null ani pusty
    * @return lista zamówień spełniających oba kryteria; może być pusta ale nigdy null
    * @throws InvalidOrderArgumentException jeśli którykolwiek z parametrów nie jest prawidłową wartością enum
    * @throws IllegalArgumentException jeśli którykolwiek z parametrów jest null lub pusty
    */
   List<Order> getOrdersByOrderPriorityAndStatus(String orderPriority, String orderStatus) throws InvalidOrderArgumentException;

   /**
    * Wyszukuje wszystkie zamówienia od określonego dostawcy.
    *
    * @param supplier nazwa dostawcy; nie może być null ani pusty
    * @return lista zamówień od podanego dostawcy; może być pusta ale nigdy null
    * @throws IllegalArgumentException jeśli supplier jest null lub pusty
    */
   List<Order> findOrdersBySupplier(String supplier);

   /**
    * Wyszukuje wszystkie zamówienia złożone w określonej dacie.
    *
    * @param orderDate data zamówienia w formacie string (np. "2024-01-15"); nie może być null ani pusty
    * @return lista zamówień z podanej daty; może być pusta ale nigdy null
    * @throws IllegalArgumentException jeśli orderDate jest null, pusty lub ma nieprawidłowy format
    */
   List<Order> findOrdersByOrderDate(String orderDate);

   /**
    * Pobiera stronicowaną listę wszystkich zamówień.
    *
    * <p>Metoda umożliwia efektywne przeglądanie dużych zbiorów zamówień poprzez
    * podział na strony o określonej wielkości.</p>
    *
    * @param page numer strony do pobrania (zaczyna się od 0); musi być nieujemny
    * @param size rozmiar strony (liczba elementów na stronie); musi być dodatni
    * @return strona zamówień z informacjami o stronicowaniu
    * @throws IllegalArgumentException jeśli page jest ujemny lub size nie jest dodatni
    */
   Page<Order> findOrders(int page, int size);

   /**
    * Pobiera zbiór wszystkich dostępnych priorytetów zamówień.
    *
    * <p>Metoda przydatna do wypełniania list rozwijanych w interfejsie użytkownika
    * oraz walidacji danych wejściowych.</p>
    *
    * @return zbiór nazw priorytetów jako stringi; nigdy null ale może być pusty
    */
   Set<String> getOrderPriorities();

   /**
    * Pobiera zbiór wszystkich dostępnych statusów zamówień.
    *
    * <p>Metoda przydatna do wypełniania list rozwijanych w interfejsie użytkownika
    * oraz walidacji danych wejściowych.</p>
    *
    * @return zbiór nazw statusów jako stringi; nigdy null ale może być pusty
    */
   Set<String> getOrderStatuses();

   /**
    * Aktualizuje status zamówienia na podstawie żądania.
    *
    * <p>Metoda waliduje nowy status, sprawdza czy zamówienie istnieje, a następnie
    * aktualizuje status wraz z czasem ostatniej modyfikacji.</p>
    *
    * @param orderStatusUpdateRequest obiekt zawierający ID zamówienia i nowy status; nie może być null
    * @return zaktualizowane zamówienie
    * @throws InvalidOrderArgumentException jeśli nowy status jest nieprawidłowy
    * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje
    * @throws IllegalArgumentException jeśli orderStatusUpdateRequest jest null
    */
   Order updateOrderStatus(OrderStatusUpdateRequest orderStatusUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException;

   /**
    * Aktualizuje priorytet zamówienia na podstawie żądania.
    *
    * <p>Metoda waliduje nowy priorytet, sprawdza czy zamówienie istnieje, a następnie
    * aktualizuje priorytet wraz z czasem ostatniej modyfikacji.</p>
    *
    * @param orderPriorityUpdateRequest obiekt zawierający ID zamówienia i nowy priorytet; nie może być null
    * @return zaktualizowane zamówienie
    * @throws InvalidOrderArgumentException jeśli nowy priorytet jest nieprawidłowy
    * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje
    * @throws IllegalArgumentException jeśli orderPriorityUpdateRequest jest null
    */
   Order updateOrderPriority(OrderPriorityUpdateRequest orderPriorityUpdateRequest) throws InvalidOrderArgumentException, OrderNotFoundException;

   /**
    * Finalizuje zamówienie zmieniając jego status na końcowy.
    *
    * <p>Metoda oznacza zamówienie jako zakończone, co zazwyczaj oznacza zmianę statusu
    * na COMPLETED lub podobny stan końcowy. Aktualizuje również czas ostatniej modyfikacji.</p>
    *
    * @param id identyfikator zamówienia do finalizacji; musi być dodatni
    * @return sfinalizowane zamówienie z zaktualizowanym statusem
    * @throws OrderNotFoundException jeśli zamówienie o podanym ID nie istnieje
    * @throws IllegalArgumentException jeśli id jest ujemne lub równe zero
    */
   Order finishOrder(long id) throws OrderNotFoundException;
}