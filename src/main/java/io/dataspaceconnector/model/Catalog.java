package io.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A catalog groups resources.
 */
@Entity
@Table(name = "catalog")
@SQLDelete(sql = "UPDATE catalog SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Catalog extends AbstractEntity {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the catalog.
     **/
    private String title;

    /**
     * The description of the catalog.
     **/
    private String description;

    /**
     * The offered resources grouped by the catalog.
     **/
    @ManyToMany
    private List<OfferedResource> offeredResources;

    /**
     * The requested resources grouped by the catalog.
     **/
    @ManyToMany
    private List<RequestedResource> requestedResources;
}