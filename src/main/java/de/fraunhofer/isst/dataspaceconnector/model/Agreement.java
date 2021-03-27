package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URI;

/**
 * A contract agreement is an agreement between two parties on access and usage behaviours.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Agreement extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The agreement id on provider side.
     */
    private URI remoteId;

    /**
     * The definition of the contract.
     **/
    private String value;
}