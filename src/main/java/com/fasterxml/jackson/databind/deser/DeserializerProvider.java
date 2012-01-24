package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.*;

/**
 * Abstract class that defines API used by {@link ObjectMapper} and
 * {@link JsonDeserializer}s to obtain deserializers capable of
 * re-constructing instances of handled type from JSON content.
 */
public abstract class DeserializerProvider
{
    protected DeserializerProvider() { }

    /*
    /**********************************************************
    /* Fluent factory methods
    /**********************************************************
     */
    
    /**
     * Method that sub-classes need to override, to ensure that fluent-factory
     * methods will produce proper sub-type.
     */
    public abstract DeserializerProvider withFactory(DeserializerFactory factory);
    
    /**
     * Method that is to configure {@link DeserializerFactory} that provider has
     * to use specified deserializer provider, with highest precedence (that is,
     * additional providers have higher precedence than default one or previously
     * added ones)
     */
    public abstract DeserializerProvider withAdditionalDeserializers(Deserializers d);

    public abstract DeserializerProvider withAdditionalKeyDeserializers(KeyDeserializers d);
    
    public abstract DeserializerProvider withDeserializerModifier(BeanDeserializerModifier modifier);

    public abstract DeserializerProvider withAbstractTypeResolver(AbstractTypeResolver resolver);

    /**
     * Method that will construct a new instance with specified additional value instantiators
     * (i.e. does NOT replace existing ones)
     */
    public abstract DeserializerProvider withValueInstantiators(ValueInstantiators instantiators);
    
    /*
    /**********************************************************
    /* General deserializer locating method
    /**********************************************************
     */

    /**
     * Method called to get hold of a deserializer for a value of given type;
     * or if no such deserializer can be found, a default handler (which
     * may do a best-effort generic serialization or just simply
     * throw an exception when invoked).
     *<p>
     * Note: this method is only called for value types; not for keys.
     * Key deserializers can be accessed using {@link #findKeyDeserializer}.
     *
     * @param config Deserialization configuration
     * @param propertyType Declared type of the value to deserializer (obtained using
     *   'setter' method signature and/or type annotations
     * @param property Object that represents accessor for property value; field,
     *    setter method or constructor parameter.
     *
     * @throws JsonMappingException if there are fatal problems with
     *   accessing suitable deserializer; including that of not
     *   finding any serializer
     */
    public abstract JsonDeserializer<Object> findValueDeserializer(DeserializationConfig config,
            JavaType propertyType, BeanProperty property)
        throws JsonMappingException;
    
    /**
     * Method called to locate deserializer for given type, as well as matching
     * type deserializer (if one is needed); and if type deserializer is needed,
     * construct a "wrapped" deserializer that can extract and use type information
     * for calling actual deserializer.
     *<p>
     * Since this method is only called for root elements, no referral information
     * is taken.
     */
    public abstract JsonDeserializer<Object> findTypedValueDeserializer(DeserializationConfig config,
            JavaType type, BeanProperty property)
        throws JsonMappingException;

    /**
     * Method called to get hold of a deserializer to use for deserializing
     * keys for {@link java.util.Map}.
     *
     * @throws JsonMappingException if there are fatal problems with
     *   accessing suitable key deserializer; including that of not
     *   finding any serializer
     */
    public abstract KeyDeserializer findKeyDeserializer(DeserializationConfig config,
            JavaType keyType, BeanProperty property)
        throws JsonMappingException;

    /**
     * Method called to find out whether provider would be able to find
     * a deserializer for given type, using a root reference (i.e. not
     * through fields or membership in an array or collection)
     */
    public abstract boolean hasValueDeserializerFor(DeserializationConfig config, JavaType type);
    
    /*
    /**********************************************************
    /* Access to caching aspects
    /**********************************************************
     */

    /**
     * Method that can be used to determine how many deserializers this
     * provider is caching currently 
     * (if it does caching: default implementation does)
     * Exact count depends on what kind of deserializers get cached;
     * default implementation caches only dynamically constructed deserializers,
     * but not eagerly constructed standard deserializers (which is different
     * from how serializer provider works).
     *<p>
     * The main use case for this method is to allow conditional flushing of
     * deserializer cache, if certain number of entries is reached.
     */
    public abstract int cachedDeserializersCount();

    /**
     * Method that will drop all dynamically constructed deserializers (ones that
     * are counted as result value for {@link #cachedDeserializersCount}).
     * This can be used to remove memory usage (in case some deserializers are
     * only used once or so), or to force re-construction of deserializers after
     * configuration changes for mapper than owns the provider.
     */
    public abstract void flushCachedDeserializers();
}