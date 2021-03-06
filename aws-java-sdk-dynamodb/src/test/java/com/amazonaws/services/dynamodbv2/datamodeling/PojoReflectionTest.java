/*
 * Copyright 2014-2016 Amazon Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit test on reflecting domain classes with getter or field annotations. It
 * also tests the scenario when annotated properties are inherited from the
 * superclass.
 */
public class PojoReflectionTest {

    /**
     * Tests reflecting a model class that uses getter annotations.
     */
    @Test
    public void testGetterAnnotations() {
        validateModel(PojoWithGetterAnnotations.class);
    }

    /**
     * Tests reflecting a model class that uses field annotations.
     */
    @Test
    public void testFieldAnnotations() {
        validateModel(PojoWithFieldAnnotations.class);
    }

    /**
     * Tests reflecting a model class that uses both getter and field
     * annotations.
     */
    @Test
    public void testMixedAnnotations() {
        validateModel(PojoWithMixedAnnotations.class);
    }

    /**
     * Validates that the reflected information from the POJO class mathes the
     * model defined in both PojoWithGetterAnnotations and
     * PojoWithFieldAnnotations.
     */
    private void validateModel(Class<?> clazz) {
        final DynamoDBMappingsRegistry.Mappings mappings = DynamoDBMappingsRegistry.instance().mappingsOf(clazz);

        // There should be 7 relevant getters (ignoredAttr is excluded)
        assertEquals(7, mappings.getMappings().size());

        for (final DynamoDBMappingsRegistry.Mapping m : mappings.getMappings()) {
            // Check that getAttributeName returns the expected attribute name
            assertEquals(
                    expectedAttributeNames.get(m.getter().getName()),
                    m.getAttributeName());

            // @DynamoDBAutoGeneratedKey
            if (m.getter().getName().equals("getAutogeneratedRangeKey")) {
                assertTrue(m.isAutoGeneratedKey());
            }

            // @DynamoDBVersionAttribute
            if (m.getter().getName().equals("getVersionedAttr")) {
                assertTrue(m.isVersion());
            }
        }

        // Key getters
        assertEquals("getHashKey", mappings.getHashKey().getter().getName());
        assertEquals("hashKey", mappings.getHashKey().getAttributeName());
        assertEquals("getAutogeneratedRangeKey", mappings.getRangeKey().getter().getName());
        assertEquals("autogeneratedRangeKey", mappings.getRangeKey().getAttributeName());
    }

    /**
     * A POJO model that uses getter annotations.
     */
    @DynamoDBTable(tableName="table")
    private static class PojoWithGetterAnnotations {

        private String hashKey;
        private String autogeneratedRangeKey;
        private String indexHashKey;
        private String indexRangeKey;
        private String attrWithAttrAnnotation;
        private String versionedAttr;
        private String customMarshallingAttr;
        private String ignoredAttr;

        @DynamoDBHashKey
        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        public String getAutogeneratedRangeKey() {
            return autogeneratedRangeKey;
        }

        public void setAutogeneratedRangeKey(String autogeneratedRangeKey) {
            this.autogeneratedRangeKey = autogeneratedRangeKey;
        }

        @DynamoDBIndexHashKey(globalSecondaryIndexName="index")
        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        @DynamoDBIndexRangeKey(globalSecondaryIndexName="index")
        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        @DynamoDBAttribute(attributeName="real-attribute-name")
        public String getAttrWithAttrAnnotation() {
            return attrWithAttrAnnotation;
        }

        public void setAttrWithAttrAnnotation(String attrWithAttrAnnotation) {
            this.attrWithAttrAnnotation = attrWithAttrAnnotation;
        }

        @DynamoDBVersionAttribute
        public String getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(String versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        @DynamoDBMarshalling(marshallerClass=RandomUUIDMarshaller.class)
        public String getCustomMarshallingAttr() {
            return customMarshallingAttr;
        }

        public void setCustomMarshallingAttr(String customMarshallingAttr) {
            this.customMarshallingAttr = customMarshallingAttr;
        }

        @DynamoDBIgnore
        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    /**
     * The same model as defined in PojoWithGetterAnnotations, but uses field
     * annotations instead.
     */
    @DynamoDBTable(tableName="table")
    private static class PojoWithFieldAnnotations {

        @DynamoDBHashKey
        private String hashKey;

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        private String autogeneratedRangeKey;

        @DynamoDBIndexHashKey(globalSecondaryIndexName="index")
        private String indexHashKey;

        @DynamoDBIndexRangeKey(globalSecondaryIndexName="index")
        private String indexRangeKey;

        @DynamoDBAttribute(attributeName="real-attribute-name")
        private String attrWithAttrAnnotation;

        @DynamoDBVersionAttribute
        private String versionedAttr;

        @DynamoDBMarshalling(marshallerClass=RandomUUIDMarshaller.class)
        private String customMarshallingAttr;

        @DynamoDBIgnore
        private String ignoredAttr;

        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        public String getAutogeneratedRangeKey() {
            return autogeneratedRangeKey;
        }

        public void setAutogeneratedRangeKey(String autogeneratedRangeKey) {
            this.autogeneratedRangeKey = autogeneratedRangeKey;
        }

        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        public String getAttrWithAttrAnnotation() {
            return attrWithAttrAnnotation;
        }

        public void setAttrWithAttrAnnotation(String attrWithAttrAnnotation) {
            this.attrWithAttrAnnotation = attrWithAttrAnnotation;
        }


        public String getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(String versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        public String getCustomMarshallingAttr() {
            return customMarshallingAttr;
        }

        public void setCustomMarshallingAttr(String customMarshallingAttr) {
            this.customMarshallingAttr = customMarshallingAttr;
        }

        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    /**
     * The same model as defined in PojoWithGetterAnnotations, but uses both getter and field
     * annotations.
     */
    @DynamoDBTable(tableName="table")
    private static class PojoWithMixedAnnotations {

        @DynamoDBHashKey
        private String hashKey;

        private String autogeneratedRangeKey;

        @DynamoDBIndexHashKey(globalSecondaryIndexName="index")
        private String indexHashKey;

        private String indexRangeKey;

        @DynamoDBAttribute(attributeName="real-attribute-name")
        private String attrWithAttrAnnotation;

        private String versionedAttr;

        @DynamoDBMarshalling(marshallerClass=RandomUUIDMarshaller.class)
        private String customMarshallingAttr;

        private String ignoredAttr;

        public String getHashKey() {
            return hashKey;
        }

        public void setHashKey(String hashKey) {
            this.hashKey = hashKey;
        }

        @DynamoDBRangeKey
        @DynamoDBAutoGeneratedKey
        public String getAutogeneratedRangeKey() {
            return autogeneratedRangeKey;
        }

        public void setAutogeneratedRangeKey(String autogeneratedRangeKey) {
            this.autogeneratedRangeKey = autogeneratedRangeKey;
        }

        public String getIndexHashKey() {
            return indexHashKey;
        }

        public void setIndexHashKey(String indexHashKey) {
            this.indexHashKey = indexHashKey;
        }

        @DynamoDBIndexRangeKey(globalSecondaryIndexName="index")
        public String getIndexRangeKey() {
            return indexRangeKey;
        }

        public void setIndexRangeKey(String indexRangeKey) {
            this.indexRangeKey = indexRangeKey;
        }

        public String getAttrWithAttrAnnotation() {
            return attrWithAttrAnnotation;
        }

        public void setAttrWithAttrAnnotation(String attrWithAttrAnnotation) {
            this.attrWithAttrAnnotation = attrWithAttrAnnotation;
        }

        @DynamoDBVersionAttribute
        public String getVersionedAttr() {
            return versionedAttr;
        }

        public void setVersionedAttr(String versionedAttr) {
            this.versionedAttr = versionedAttr;
        }

        public String getCustomMarshallingAttr() {
            return customMarshallingAttr;
        }

        public void setCustomMarshallingAttr(String customMarshallingAttr) {
            this.customMarshallingAttr = customMarshallingAttr;
        }

        @DynamoDBIgnore
        public String getIgnoredAttr() {
            return ignoredAttr;
        }

        public void setIgnoredAttr(String ignoredAttr) {
            this.ignoredAttr = ignoredAttr;
        }
    }

    @SuppressWarnings("serial")
    private static final Map<String, String> expectedAttributeNames = new HashMap<String, String>(){{
        put("getHashKey", "hashKey");
        put("getAutogeneratedRangeKey", "autogeneratedRangeKey");
        put("getIndexHashKey", "indexHashKey");
        put("getIndexRangeKey", "indexRangeKey");
        put("getAttrWithAttrAnnotation", "real-attribute-name"); // w/ attribute name override
        put("getVersionedAttr", "versionedAttr");
        put("getCustomMarshallingAttr", "customMarshallingAttr");
    }};


    @Test
    public void testInheritedProperties() {
        // Base class
        final DynamoDBMappingsRegistry.Mappings mappings1 = DynamoDBMappingsRegistry.instance().mappingsOf(BaseTablePojo.class);
        assertEquals(3, mappings1.getMappings().size());
        assertEquals("getParentHashKeyWithFieldAnnotation", mappings1.getHashKey().getter().getName());
        assertEquals("parentHashKeyWithFieldAnnotation", mappings1.getHashKey().getAttributeName());
        assertEquals("getParentRangeKeyWithGetterAnnotation", mappings1.getRangeKey().getter().getName());
        assertEquals("parentRangeKeyWithGetterAnnotation", mappings1.getRangeKey().getAttributeName());

        // Subclass pojo inherits the key getters, and defines an attribute that is ignored in the superclass
        final DynamoDBMappingsRegistry.Mappings mappings2 = DynamoDBMappingsRegistry.instance().mappingsOf(TablePojoSubclass.class);
        assertEquals(4, mappings2.getMappings().size());
        assertEquals(mappings1.getHashKey().getter(), mappings2.getHashKey().getter());
        assertEquals("parentHashKeyWithFieldAnnotation", mappings2.getHashKey().getAttributeName());
        assertEquals(mappings1.getRangeKey().getter(), mappings2.getRangeKey().getter());
        assertEquals("parentRangeKeyWithGetterAnnotation", mappings2.getRangeKey().getAttributeName());
    }

    @DynamoDBTable(tableName="table")
    private static class BaseTablePojo {

        @DynamoDBHashKey
        private String parentHashKeyWithFieldAnnotation;
        private String parentRangeKeyWithGetterAnnotation;
        private String parentAttrWithNoAnnotation;
        @DynamoDBIgnore
        private String parentIgnoredAttr;

        public String getParentHashKeyWithFieldAnnotation() {
            return parentHashKeyWithFieldAnnotation;
        }

        public void setParentHashKeyWithFieldAnnotation(
                String parentHashKeyWithFieldAnnotation) {
            this.parentHashKeyWithFieldAnnotation = parentHashKeyWithFieldAnnotation;
        }

        @DynamoDBRangeKey
        public String getParentRangeKeyWithGetterAnnotation() {
            return parentRangeKeyWithGetterAnnotation;
        }

        public void setParentRangeKeyWithGetterAnnotation(
                String parentRangeKeyWithGetterAnnotation) {
            this.parentRangeKeyWithGetterAnnotation = parentRangeKeyWithGetterAnnotation;
        }

        public String getParentAttrWithNoAnnotation() {
            return parentAttrWithNoAnnotation;
        }

        public void setParentAttrWithNoAnnotation(String parentAttrWithNoAnnotation) {
            this.parentAttrWithNoAnnotation = parentAttrWithNoAnnotation;
        }

        public String getParentIgnoredAttr() {
            return parentIgnoredAttr;
        }

        public void setParentIgnoredAttr(String parentIgnoredAttr) {
            this.parentIgnoredAttr = parentIgnoredAttr;
        }
    }

    /**
     * Subclass of BaseTablePojo that inherits all the key attribtues, and
     * declared the parentIgnoredAttr which is ignored in the superclass.
     */
    @DynamoDBTable(tableName="table")
    private static class TablePojoSubclass extends BaseTablePojo {

        // Not ignored by the subclass
        private String parentIgnoredAttr;

        @Override
        public String getParentIgnoredAttr() {
            return parentIgnoredAttr;
        }

        @Override
        public void setParentIgnoredAttr(String parentIgnoredAttr) {
            this.parentIgnoredAttr = parentIgnoredAttr;
        }
    }
}
