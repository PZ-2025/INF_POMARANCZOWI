package com.orange.bookmanagment.order.converter;

import com.orange.bookmanagment.order.model.OrderedBook;
import com.orange.bookmanagment.order.util.GsonUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class OrderedBookConverter implements AttributeConverter<List<OrderedBook>, String> {

    @Override
    public String convertToDatabaseColumn(List<OrderedBook> attribute) {
       final StringBuilder sb = new StringBuilder();

       for (int i = 0; i < attribute.size(); i++) {
           sb.append(attribute.get(i).toString());
           if(i != attribute.size() - 1) sb.append(",");
       }
       return sb.toString();
    }

    @Override
    public List<OrderedBook> convertToEntityAttribute(String dbData) {
        String[] data = dbData.split(",");

        final List<OrderedBook> books = new ArrayList<>();

        for (String s : data) {
            final OrderedBook orderedBook = GsonUtil.fromJson(s, OrderedBook.class);
            books.add(orderedBook);
        }

        return books;
    }
}
