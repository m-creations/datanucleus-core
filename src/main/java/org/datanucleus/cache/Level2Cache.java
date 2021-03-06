/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 


Contributors:
    ...
**********************************************************************/
package org.datanucleus.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Interface for any Level 2 Cache used internally.
 * Provides the typical controls required internally and including the JDO2/JPA1 L2 cache methods.
 * <p>
 * JDO and JPA allow the use of a level 2 (L2) cache, with the cache shared between
 * PersistenceManagers/EntityManagers. The objects in the level 2 cache don't pertain to any
 * one manager. The L2 cache in DataNucleus is as follows :-
 * </p>
 * <p>
 * The L2 cache stores an object of type <i>org.datanucleus.cache.CachedPC</i> and is keyed
 * by the identity of the object. The <i>CachedPC</i> contains a persistable object (<b>not</b>
 * connected to an ObjectProvider), together with the indicators for which fields are loaded, as
 * well as values for any relation fields. The persistable object will have values as null for
 * any relation fields, and it is these relation field values stored in the <i>CachedPC</i>
 * that provide linkage to other objects. 
 * </p>
 * <p>
 * The relation field values also do not store actual objects; they store the identities of the 
 * related objects. For example if an object X has a 1-1 relation with another persistable 
 * object Y then in the relation field values for X for that field we store the identity of
 * Y. Similarly if the field is a Collection, then the relation field values will be a 
 * Collection of identities of the related objects. This provides isolation of each object
 * in the L2 cache (so objects aren't storing references to other objects and so allowing garbage
 * collection etc).
 * </p>
 * <p>
 * Objects are stored in the L2 cache in the following situations
 * </p>
 * <ul>
 * <li>An object is retrieved (from the datastore) within a transaction, and it is
 * stored in the L2 cache if no object with that identity already exists there.</li>
 * <li>At commit() of the transaction any object that has been modified during that
 * transaction will be stored/updated in the L2 cache if its persistable object is
 * still in memory in the PM/EM (could have been garbage collected since flushing)</li>
 * </ul>
 * <p>
 * Each class can be configured to be <i>cacheable</i> or not. The default for a persistable
 * class is to be cacheable. Configuration is performed via annotations or XML metadata.
 * If a class is not cacheable then objects of that type aren't stored in the L2 cache.
 * </p>
 */
public interface Level2Cache extends Serializable
{
    /**
     * Method to close the cache when no longer needed. Provides a hook to release resources etc.
     */
    void close();

    /**
     * Evict the parameter instance from the second-level cache.
     * @param oid the object id of the instance to evict.
     */
    void evict (Object oid);

    /**
     * Evict the parameter instances from the second-level cache.
     * All instances in the PersistenceManager's cache are evicted
     * from the second-level cache.
     */
    void evictAll ();

    /**
     * Evict the parameter instances from the second-level cache.
     * @param oids the object ids of the instance to evict.
     */
    void evictAll (Object[] oids);

    /**
     * Evict the parameter instances from the second-level cache.
     * @param oids the object ids of the instance to evict.
     */
    void evictAll (Collection oids);

    /**
     * Evict the parameter instances from the second-level cache.
     * @param pcClass the class of instances to evict
     * @param subclasses if true, evict instances of subclasses also
     */
    void evictAll (Class pcClass, boolean subclasses);

    /**
     * Pin the parameter instance in the second-level cache.
     * @param oid the object id of the instance to pin.
     */
    void pin (Object oid);

    /**
     * Pin the parameter instances in the second-level cache.
     * @param oids the object ids of the instances to pin.
     */
    void pinAll (Collection oids);

    /**
     * Pin the parameter instances in the second-level cache.
     * @param oids the object ids of the instances to pin.
     */
    void pinAll (Object[] oids);

    /**
     * Pin instances in the second-level cache.
     * @param pcClass the class of instances to pin
     * @param subclasses if true, pin instances of subclasses also
     */
    void pinAll (Class pcClass, boolean subclasses);

    /**
     * Unpin the parameter instance from the second-level cache.
     * @param oid the object id of the instance to unpin.
     */
    void unpin(Object oid);

    /**
     * Unpin the parameter instances from the second-level cache.
     * @param oids the object ids of the instance to evict.
     */
    void unpinAll(Collection oids);

    /**
     * Unpin the parameter instance from the second-level cache.
     * @param oids the object id of the instance to evict.
     */
    void unpinAll(Object[] oids);

    /**
     * Unpin instances from the second-level cache.
     * @param pcClass the class of instances to unpin
     * @param subclasses if true, unpin instances of subclasses also
     */
    void unpinAll(Class pcClass, boolean subclasses);

    /**
     * Accessor for the number of pinned objects in the cache.
     * @return Number of pinned objects
     */
    int getNumberOfPinnedObjects();
    
    /**
     * Accessor for the number of unpinned objects in the cache.
     * @return Number of unpinned objects
     */
    int getNumberOfUnpinnedObjects();

    /**
     * Accessor for the total number of objects in the L2 cache.
     * @return Number of objects
     */
    int getSize();

    /**
     * Accessor for an object from the cache.
     * @param oid The Object ID
     * @return The L2 cacheable object
     */
    CachedPC get(Object oid);

    /**
     * Accessor for a collection of objects from the cache.
     * @param oids The Object IDs
     * @return Map of the objects, keyed by the oids that are found
     */
    Map<Object, CachedPC> getAll(Collection oids);

    /**
     * Method to put an object in the cache.
     * @param oid The Object id for this object
     * @param pc The L2 cacheable persistable object
     * @return The value previously associated with this oid
     */
    CachedPC put(Object oid, CachedPC pc);

    /**
     * Method to put several objects into the cache.
     * @param objs Map of cacheable object keyed by its oid.
     */
    void putAll(Map<Object, CachedPC> objs);

    /**
     * Accessor for whether the cache is empty.
     * @return Whether it is empty.
     */
    boolean isEmpty();

    /**
     * Accessor for whether an object with the specified id is in the cache
     * @param oid The object id
     * @return Whether it is in the cache
     */
    boolean containsOid(Object oid);

    /**
     * Representation of a class whose objects will be pinned when put into the L2 cache.
     */
    class PinnedClass
    {
        Class cls;
        boolean subclasses;
        
        /**
         * Constructor
         * @param cls the class
         * @param subclasses sub classes
         */
        public PinnedClass(Class cls, boolean subclasses)
        {
            this.cls = cls;
            this.subclasses = subclasses;
        }

        public int hashCode()
        {
            return cls.hashCode() ^ (subclasses ? 0 : 1);
        }

        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (!(obj instanceof PinnedClass))
            {
                return false;
            }
            PinnedClass other = (PinnedClass)obj;
            return other.cls.getName().equals(cls.getName()) && other.subclasses == subclasses;
        }
    }
}