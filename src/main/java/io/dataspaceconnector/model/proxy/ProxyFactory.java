/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model.proxy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.util.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Factory class for the proxy.
 */
@Component
public class ProxyFactory extends AbstractFactory<Proxy, ProxyDesc> {

    /**
     * The default location.
     */
    public static final URI DEFAULT_LOCATION = URI.create("");

    @Override
    protected final Proxy initializeEntity(final ProxyDesc desc) {
        return new Proxy();
    }

    @Override
    public final boolean updateInternal(final Proxy proxy, final ProxyDesc desc) {
        final var hasUpdatedLocation = updateLocation(proxy, desc.getLocation());
        final var hasUpdatedExclusions = updateExclusions(proxy, desc.getExclusions());
        final var hasUpdatedAuthentication = updateAuthentication(proxy, desc.getAuthentication());

        return hasUpdatedLocation || hasUpdatedExclusions || hasUpdatedAuthentication;
    }

    private boolean updateExclusions(final Proxy proxy, final List<String> exclusions) {
        final var newExclusionList =
                MetadataUtils.updateStringList(proxy.getExclusions(), exclusions,
                        new ArrayList<>());
        newExclusionList.ifPresent(proxy::setExclusions);

        return newExclusionList.isPresent();
    }

    private boolean updateAuthentication(final Proxy proxy, final Authentication authentication) {
        if (proxy.getAuthentication() == null && authentication == null) {
            return false;
        }

        if (proxy.getAuthentication() != null && !proxy.getAuthentication().equals(authentication)) {
            return false;
        }

        proxy.setAuthentication(authentication);
        return true;
    }

    private boolean updateLocation(final Proxy proxy, final URI location) {
        final var newLocation =
                MetadataUtils.updateUri(proxy.getLocation(), location, DEFAULT_LOCATION);
        newLocation.ifPresent(proxy::setLocation);

        return newLocation.isPresent();
    }

}
