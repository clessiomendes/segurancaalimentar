package com.opencsv.bean;

import com.opencsv.CSVReader;
import com.opencsv.ICSVParser;
import com.opencsv.exceptions.CsvBadConverterException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/*
 * Copyright 2007 Kyle Miller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Maps data to objects using the column names in the first row of the CSV file
 * as reference. This way the column order does not matter.
 *
 * @param <T> Type of the bean to be returned
 */
public class HeaderColumnNameMappingStrategy<T> extends AbstractMappingStrategy<String, String, ComplexFieldMapEntry<String, String, T>, T> {

    /**
     * Given a header name, this map allows one to find the corresponding
     * {@link BeanField}.
     */
    protected FieldMapByName<T> fieldMap = null;

    /** Holds a {@link java.util.Comparator} to sort columns on writing. */
    protected Comparator<String> writeOrder = null;
    
    /**
     * Default constructor.
     */
    public HeaderColumnNameMappingStrategy() {
    }
    
    @Override
    public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
        // Validation
        if(type == null) {
            throw new IllegalStateException(ResourceBundle
                    .getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                    .getString("type.unset"));
        }
        
        // Read the header
        String[] header = ObjectUtils.defaultIfNull(reader.readNext(), ArrayUtils.EMPTY_STRING_ARRAY);
        headerIndex.initializeHeaderIndex(header);

        // Throw an exception if any required headers are missing
        List<FieldMapByNameEntry<T>> missingRequiredHeaders = fieldMap.determineMissingRequiredHeaders(header);
        if (!missingRequiredHeaders.isEmpty()) {
            String[] requiredHeaderNames = new String[missingRequiredHeaders.size()];
            List<Field> requiredFields = new ArrayList<>(missingRequiredHeaders.size());
            for(int i = 0; i < missingRequiredHeaders.size(); i++) {
                FieldMapByNameEntry fme = missingRequiredHeaders.get(i);
                if(fme.isRegexPattern()) {
                    requiredHeaderNames[i] = String.format(
                            ResourceBundle
                                    .getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                                    .getString("matching"),
                            fme.getName());
                }
                else {
                    requiredHeaderNames[i] = fme.getName();
                }
                requiredFields.add(fme.getField().getField());
            }
            String missingRequiredFields = StringUtils.join(requiredHeaderNames, ", ");
            String allHeaders = StringUtils.join(header, ',');
            CsvRequiredFieldEmptyException e = new CsvRequiredFieldEmptyException(type, requiredFields,
                    String.format(
                            ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                                    .getString("header.required.field.absent"),
                            missingRequiredFields, allHeaders));
            e.setLine(header);
            throw e;
        }
    }
    
    @Override
    protected Object chooseMultivaluedFieldIndexFromHeaderIndex(int index) {
        String[] s = headerIndex.getHeaderIndex();
        return index >= s.length ? null: s[index];
    }
    
    @Override
    public void verifyLineLength(int numberOfFields) throws CsvRequiredFieldEmptyException {
        if(!headerIndex.isEmpty()) {
            if (numberOfFields != headerIndex.getHeaderIndexLength()) {
                throw new CsvRequiredFieldEmptyException(type, ResourceBundle
                        .getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale)
                        .getString("header.data.mismatch"));
            }
        }
    }
    
    @Override
    public BeanField<T> findField(int col) throws CsvBadConverterException {
        BeanField<T> beanField = null;
        String columnName = getColumnName(col);
        if(StringUtils.isNotBlank(columnName)) {
            beanField = fieldMap.get(columnName.toUpperCase().trim());
        }
        return beanField;
    }
    
    @Override
    protected void loadFieldMap() throws CsvBadConverterException {
        boolean required;
        fieldMap = new FieldMapByName<>(errorLocale);
        fieldMap.setColumnOrderOnWrite(writeOrder);

        for (Field field : loadFields(getType())) {
            String columnName, locale, capture, format;

            // Always check for a custom converter first.
            if (field.isAnnotationPresent(CsvCustomBindByName.class)) {
                CsvCustomBindByName annotation = field.getAnnotation(CsvCustomBindByName.class);
                columnName = annotation.column().toUpperCase().trim();
                if(StringUtils.isEmpty(columnName)) {
                    columnName = field.getName().toUpperCase();
                }
                Class<? extends AbstractBeanField> converter = field
                        .getAnnotation(CsvCustomBindByName.class)
                        .converter();
                BeanField<T> bean = instantiateCustomConverter(converter);
                bean.setField(field);
                required = annotation.required();
                bean.setRequired(required);
                fieldMap.put(columnName, bean);
            }
            
            // Then check for a collection
            else if(field.isAnnotationPresent(CsvBindAndSplitByName.class)) {
                CsvBindAndSplitByName annotation = field.getAnnotation(CsvBindAndSplitByName.class);
                required = annotation.required();
                columnName = annotation.column().toUpperCase().trim();
                locale = annotation.locale();
                String splitOn = annotation.splitOn();
                String writeDelimiter = annotation.writeDelimiter();
                Class<? extends Collection> collectionType = annotation.collectionType();
                Class<?> elementType = annotation.elementType();
                Class<? extends AbstractCsvConverter> splitConverter = annotation.converter();
                capture = annotation.capture();
                format = annotation.format();
                
                CsvConverter converter = determineConverter(field, elementType, locale, splitConverter);
                if (StringUtils.isEmpty(columnName)) {
                    fieldMap.put(field.getName().toUpperCase(),
                            new BeanFieldSplit<T>(
                                    field, required, errorLocale, converter,
                                    splitOn, writeDelimiter, collectionType,
                                    capture, format));
                } else {
                    fieldMap.put(columnName, new BeanFieldSplit<T>(
                            field, required, errorLocale, converter, splitOn,
                            writeDelimiter, collectionType, capture, format));
                }
            }
            
            // Then for a multi-column annotation
            else if(field.isAnnotationPresent(CsvBindAndJoinByName.class)) {
                CsvBindAndJoinByName annotation = field.getAnnotation(CsvBindAndJoinByName.class);
                required = annotation.required();
                String columnRegex = annotation.column();
                locale = annotation.locale();
                Class<?> elementType = annotation.elementType();
                Class<? extends MultiValuedMap> mapType = annotation.mapType();
                Class<? extends AbstractCsvConverter> joinConverter = annotation.converter();
                capture = annotation.capture();
                format = annotation.format();
                
                CsvConverter converter = determineConverter(field, elementType, locale, joinConverter);
                if (StringUtils.isEmpty(columnRegex)) {
                    fieldMap.putComplex(field.getName(),
                            new BeanFieldJoinStringIndex<T>(
                                    field, required, errorLocale, converter,
                                    mapType, capture, format));
                } else {
                    fieldMap.putComplex(columnRegex, new BeanFieldJoinStringIndex<T>(
                            field, required, errorLocale, converter, mapType,
                            capture, format));
                }
            }

            // Otherwise it must be CsvBindByName.
            else {
                CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                required = annotation.required();
                columnName = annotation.column().toUpperCase().trim();
                locale = annotation.locale();
                capture = annotation.capture();
                format = annotation.format();
                CsvConverter converter = determineConverter(field, field.getType(), locale, null);

                if (StringUtils.isEmpty(columnName)) {
                    fieldMap.put(field.getName().toUpperCase(),
                            new BeanFieldSingleValue<T>(field, required,
                                    errorLocale, converter, capture, format));
                } else {
                    fieldMap.put(columnName, new BeanFieldSingleValue<T>(
                            field, required, errorLocale, converter, capture, format));
                }
            }
        }
    }

    private List<Field> loadFields(Class<? extends T> cls) {
        List<Field> fields = new LinkedList<>();
        for (Field field : FieldUtils.getAllFields(cls)) {
            if (field.isAnnotationPresent(CsvBindByName.class)
                    || field.isAnnotationPresent(CsvCustomBindByName.class)
                    || field.isAnnotationPresent(CsvBindAndSplitByName.class)
                    || field.isAnnotationPresent(CsvBindAndJoinByName.class)) {
                fields.add(field);
            }
        }
        setAnnotationDriven(!fields.isEmpty());
        return fields;
    }

    @Override
    protected FieldMap<String, String, ? extends ComplexFieldMapEntry<String, String, T>, T> getFieldMap() {return fieldMap;}
    
    @Override
    public String findHeader(int col) {
        return headerIndex.getByPosition(col);
    }
    
    @Override
    public Integer getColumnIndex(String name) {
        if (headerIndex.isEmpty()) {
            throw new IllegalStateException(ResourceBundle.getBundle(ICSVParser.DEFAULT_BUNDLE_NAME, errorLocale).getString("header.unread"));
        }
        return super.getColumnIndex(name);
    }

    /**
     * Sets the {@link java.util.Comparator} to be used to sort columns when
     * writing beans to a CSV file.
     * Behavior of this method when used on a mapping strategy intended for
     * reading data from a CSV source is not defined.
     *
     * @param writeOrder The {@link java.util.Comparator} to use. May be
     *   {@code null}, in which case the natural ordering is used.
     * @since 4.3
     */
    public void setColumnOrderOnWrite(Comparator<String> writeOrder) {
        this.writeOrder = writeOrder;
        if(fieldMap != null) {
            fieldMap.setColumnOrderOnWrite(this.writeOrder);
        }
    }
}
