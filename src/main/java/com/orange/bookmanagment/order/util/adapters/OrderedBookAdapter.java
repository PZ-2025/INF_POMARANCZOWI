package com.orange.bookmanagment.order.util.adapters;

import com.google.gson.*;
import com.orange.bookmanagment.order.model.Order;
import com.orange.bookmanagment.order.model.OrderedBook;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter Gson do serializacji i deserializacji obiektów {@link OrderedBook} do/z formatu JSON.
 *
 * <p>Klasa OrderedBookAdapter implementuje interfejsy {@link JsonSerializer} i {@link JsonDeserializer}
 * biblioteki Gson, umożliwiając niestandardową konwersję obiektów OrderedBook. Adapter obsługuje
 * złożoną strukturę obiektu zawierającą zagnieżdżone obiekty autora i wydawcy.</p>
 *
 * <p>Podczas deserializacji adapter wykonuje szczegółową walidację wszystkich pól JSON,
 * sprawdzając ich obecność, typ i strukturę. W przypadku błędów rzuca odpowiednie wyjątki
 * {@link JsonParseException} z opisowymi komunikatami.</p>
 *
 * <p>Podczas serializacji adapter tworzy płaską strukturę JSON, wyciągając dane z zagnieżdżonych
 * obiektów wydawcy i autorów do głównego obiektu JSON dla lepszej czytelności.</p>
 *
 * @since 1.0
 * @see OrderedBook
 * @see OrderedBook.OrderedBookAuthor
 * @see OrderedBook.OrderedBookPublisher
 * @see JsonSerializer
 * @see JsonDeserializer
 */
public class OrderedBookAdapter implements JsonDeserializer<OrderedBook>, JsonSerializer<OrderedBook> {

    /**
     * Deserializuje obiekt JSON do instancji {@link OrderedBook}.
     *
     * <p>Metoda wykonuje kompleksową walidację struktury JSON, sprawdzając:
     * <ul>
     * <li>Obecność wszystkich wymaganych pól (title, publisherName, publisherDescription, description, genre, coverImage, authors)</li>
     * <li>Poprawność typów danych dla każdego pola</li>
     * <li>Strukturę tablicy autorów i jej elementów</li>
     * <li>Poprawność zagnieżdżonych obiektów autorów</li>
     * </ul></p>
     *
     * <p>Oczekiwana struktura JSON:
     * <pre>
     * {
     *   "title": "string",
     *   "publisherName": "string",
     *   "publisherDescription": "string",
     *   "description": "string",
     *   "genre": "string",
     *   "coverImage": "string",
     *   "authors": [
     *     {
     *       "firstName": "string",
     *       "lastName": "string",
     *       "biography": "string"
     *     }
     *   ]
     * }
     * </pre></p>
     *
     * @param json element JSON do deserializacji; nie może być null
     * @param type typ docelowy deserializacji; nie używany w tej implementacji
     * @param jsonDeserializationContext kontekst deserializacji Gson; nie używany w tej implementacji
     * @return zdeserializowany obiekt OrderedBook z wszystkimi zagnieżdżonymi obiektami
     * @throws JsonParseException jeśli JSON nie jest obiektem
     * @throws JsonParseException jeśli brakuje wymaganych pól w JSON
     * @throws JsonParseException jeśli typy pól są nieprawidłowe (np. string zamiast array)
     * @throws JsonParseException jeśli struktura autorów jest nieprawidłowa
     * @throws JsonParseException jeśli obiekty autorów mają nieprawidłową strukturę
     */
    @Override
    public OrderedBook deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("not a JSON object!");
        }

        final JsonObject jsonObject = (JsonObject) json;
        final JsonElement title = jsonObject.get("title");
        final JsonElement publisherName = jsonObject.get("publisherName");
        final JsonElement publisherDescription = jsonObject.get("publisherDescription");
        final JsonElement description = jsonObject.get("description");
        final JsonElement genre = jsonObject.get("genre");
        final JsonElement coverImage = jsonObject.get("coverImage");
        final JsonElement authors = jsonObject.get("authors");

        if (title == null || publisherName == null || publisherDescription == null || description == null || genre == null || coverImage == null || authors == null) {
            throw new JsonParseException("Malformed orderedBook json string!");
        }

        if (!title.isJsonPrimitive() || !((JsonPrimitive) title).isString()) {
            throw new JsonParseException("Title is not a string!");
        }

        if (!authors.isJsonArray()) {
            throw new JsonParseException("Authors is not a json array!");
        }

        if (!publisherName.isJsonPrimitive() || !((JsonPrimitive) publisherName).isString()) {
            throw new JsonParseException("Publisher name is not a string!");
        }

        if (!publisherDescription.isJsonPrimitive() || !((JsonPrimitive) publisherDescription).isString()) {
            throw new JsonParseException("Publisher description is not a string!");
        }

        if (!description.isJsonPrimitive() || !((JsonPrimitive) description).isString()) {
            throw new JsonParseException("Description is not a string!");
        }

        if (!genre.isJsonPrimitive() || !((JsonPrimitive) genre).isString()) {
            throw new JsonParseException("Genre is not a string!");
        }

        if (!coverImage.isJsonPrimitive() || !((JsonPrimitive) coverImage).isString()) {
            throw new JsonParseException("Cover image is not a string!");
        }

        // Parsowanie listy autorów
        List<OrderedBook.OrderedBookAuthor> authorsList = new ArrayList<>();
        JsonArray authorsArray = authors.getAsJsonArray();

        for (JsonElement authorElement : authorsArray) {
            if (!authorElement.isJsonObject()) {
                throw new JsonParseException("Author element is not a JSON object!");
            }

            JsonObject authorObject = authorElement.getAsJsonObject();
            JsonElement firstName = authorObject.get("firstName");
            JsonElement lastName = authorObject.get("lastName");
            JsonElement biography = authorObject.get("biography");

            if (firstName == null || lastName == null || biography == null) {
                throw new JsonParseException("Author object is missing required fields!");
            }

            if (!firstName.isJsonPrimitive() || !((JsonPrimitive) firstName).isString()) {
                throw new JsonParseException("Author firstName is not a string!");
            }

            if (!lastName.isJsonPrimitive() || !((JsonPrimitive) lastName).isString()) {
                throw new JsonParseException("Author lastName is not a string!");
            }

            if (!biography.isJsonPrimitive() || !((JsonPrimitive) biography).isString()) {
                throw new JsonParseException("Author biography is not a string!");
            }

            OrderedBook.OrderedBookAuthor author = new OrderedBook.OrderedBookAuthor(
                    firstName.getAsString(),
                    lastName.getAsString(),
                    biography.getAsString()
            );

            authorsList.add(author);
        }

        OrderedBook.OrderedBookPublisher publisher = new OrderedBook.OrderedBookPublisher(
                publisherName.getAsString(),
                publisherDescription.getAsString()
        );

        return new OrderedBook(
                title.getAsString(),
                authorsList,
                publisher,
                description.getAsString(),
                genre.getAsString(),
                coverImage.getAsString()
        );
    }

    /**
     * Serializuje obiekt {@link OrderedBook} do formatu JSON.
     *
     * <p>Metoda tworzy płaską strukturę JSON, w której dane z zagnieżdżonych obiektów
     * wydawcy (publisher) są wyciągnięte do głównego poziomu jako publisherName i publisherDescription.
     * Lista autorów jest serializowana jako tablica obiektów JSON.</p>
     *
     * <p>Wygenerowana struktura JSON:
     * <pre>
     * {
     *   "title": "tytuł książki",
     *   "description": "opis książki",
     *   "genre": "gatunek",
     *   "coverImage": "url do okładki",
     *   "publisherName": "nazwa wydawcy",
     *   "publisherDescription": "opis wydawcy",
     *   "authors": [
     *     {
     *       "firstName": "imię autora",
     *       "lastName": "nazwisko autora",
     *       "biography": "biografia autora"
     *     }
     *   ]
     * }
     * </pre></p>
     *
     * @param orderedBook obiekt OrderedBook do serializacji; nie może być null
     * @param type typ serializowanego obiektu; nie używany w tej implementacji
     * @param jsonSerializationContext kontekst serializacji Gson; nie używany w tej implementacji
     * @return element JSON reprezentujący zserializowany obiekt OrderedBook
     * @throws NullPointerException jeśli orderedBook lub którekolwiek z jego wymaganych pól jest null
     */
    @Override
    public JsonElement serialize(OrderedBook orderedBook, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("title", orderedBook.title());
        jsonObject.addProperty("description", orderedBook.description());
        jsonObject.addProperty("genre", orderedBook.genre());
        jsonObject.addProperty("coverImage", orderedBook.coverImage());
        jsonObject.addProperty("publisherName", orderedBook.publisher().name());
        jsonObject.addProperty("publisherDescription", orderedBook.publisher().description());

        JsonArray authorsArray = new JsonArray();
        for (OrderedBook.OrderedBookAuthor author : orderedBook.authors()) {
            JsonObject authorObject = new JsonObject();
            authorObject.addProperty("firstName", author.firstName());
            authorObject.addProperty("lastName", author.lastName());
            authorObject.addProperty("biography", author.biography());
            authorsArray.add(authorObject);
        }
        jsonObject.add("authors", authorsArray);

        return jsonObject;
    }
}