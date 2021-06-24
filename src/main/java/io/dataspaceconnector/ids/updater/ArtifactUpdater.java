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
package io.dataspaceconnector.ids.updater;

import io.dataspaceconnector.resources.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.model.core.Artifact;
import io.dataspaceconnector.resources.ArtifactService;
import io.dataspaceconnector.ids.templates.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for updating artifacts when an IDS artifact is provided.
 */
@Component
@RequiredArgsConstructor
public class ArtifactUpdater
        implements InfomodelUpdater<de.fraunhofer.iais.eis.Artifact, Artifact> {
    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    @Override
    public final Artifact update(final de.fraunhofer.iais.eis.Artifact entity)
            throws ResourceNotFoundException {
        final var entityId = artifactService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var artifact = artifactService.get(entityId.get());
        final var template = MappingUtils.fromIdsArtifact(
                entity, artifact.isAutomatedDownload(), artifact.getRemoteAddress());
        return artifactService.update(entityId.get(), template.getDesc());
    }
}