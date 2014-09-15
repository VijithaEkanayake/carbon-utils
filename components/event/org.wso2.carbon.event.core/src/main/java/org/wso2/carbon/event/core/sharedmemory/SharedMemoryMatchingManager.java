/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.event.core.sharedmemory;

import org.wso2.carbon.event.core.delivery.MatchingManager;
import org.wso2.carbon.event.core.subscription.Subscription;
import org.wso2.carbon.event.core.exception.EventBrokerException;
import org.wso2.carbon.context.CarbonContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * in memory matching manager which keeps the topic and subscriptions in an hash map
 */
public class SharedMemoryMatchingManager implements MatchingManager, Serializable {

    private static final Log log = LogFactory.getLog(SharedMemoryMatchingManager.class);
    private static Cache<Integer, SharedMemorySubscriptionStorage> cache = null;
    private static boolean CacheInit = false;

    private static Cache<Integer, SharedMemorySubscriptionStorage> getTenantIDInMemorySubscriptionStorageCache() {
        if (CacheInit) {
            return Caching.getCacheManagerFactory().getCacheManager("inMemoryEventCacheManager").getCache("tenantIDInMemorySubscriptionStorageCache");
        } else {
            CacheManager cacheManager = Caching.getCacheManagerFactory().getCacheManager("inMemoryEventCacheManager");
            String cacheName = "tenantIDInMemorySubscriptionStorageCache";
            CacheInit = true;
            return cacheManager.<Integer, SharedMemorySubscriptionStorage>createCacheBuilder(cacheName).
                    setExpiry(CacheConfiguration.ExpiryType.MODIFIED, new CacheConfiguration.Duration(TimeUnit.SECONDS, 1000 * 24 * 3600)).
                    setExpiry(CacheConfiguration.ExpiryType.ACCESSED, new CacheConfiguration.Duration(TimeUnit.SECONDS, 1000 * 24 * 3600)).
                    setStoreByValue(false).build();

        }
//        return Caching.getCacheManagerFactory().getCacheManager("inMemoryEventCacheManager").getCache("tenantIDInMemorySubscriptionStorageCache");
    }

    public SharedMemoryMatchingManager() {
    }

    public void addSubscription(Subscription subscription) {
        SharedMemorySubscriptionStorage inMemorySubscriptionStorage =
                getTenantIDInMemorySubscriptionStorageCache().get(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        inMemorySubscriptionStorage.addSubscription(subscription);
    }

    public List<Subscription> getMatchingSubscriptions(String topicName) {
        SharedMemorySubscriptionStorage inMemorySubscriptionStorage =
                getTenantIDInMemorySubscriptionStorageCache().get(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        if(inMemorySubscriptionStorage != null) {
            return inMemorySubscriptionStorage.getMatchingSubscriptions(topicName);
        } else {
            return new ArrayList<Subscription>();
        }
    }

    public void unSubscribe(String subscriptionID) throws EventBrokerException {
        SharedMemorySubscriptionStorage inMemorySubscriptionStorage =
                getTenantIDInMemorySubscriptionStorageCache().get(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        inMemorySubscriptionStorage.unSubscribe(subscriptionID);
    }

    public void renewSubscription(Subscription subscription) throws EventBrokerException {
        SharedMemorySubscriptionStorage inMemorySubscriptionStorage =
                getTenantIDInMemorySubscriptionStorageCache().get(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        inMemorySubscriptionStorage.renewSubscription(subscription);
    }

    public void initializeTenant() throws EventBrokerException {
        if (getTenantIDInMemorySubscriptionStorageCache().get(CarbonContext.getThreadLocalCarbonContext().getTenantId()) == null){
            getTenantIDInMemorySubscriptionStorageCache().put(
                    CarbonContext.getThreadLocalCarbonContext().getTenantId(), new SharedMemorySubscriptionStorage());
        } else {
        }
    }
}
