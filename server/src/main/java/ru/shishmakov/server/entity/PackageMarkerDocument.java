package ru.shishmakov.server.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Empty marker class.<br/>
 * It used to identify package for classes annotated with {@link Document}.
 *
 * @author Dmitriy Shishmakov
 */
public abstract class PackageMarkerDocument {
    private PackageMarkerDocument() {
    }
}
