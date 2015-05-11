package ru.shishmakov.client.config;

/**
 * Empty marker class.<br/>
 * It used to identify package for type-safe in Spring {@code @ComponentScan(basePackageClasses = )}.
 *
 * @author Dmitriy Shishmakov
 */
public abstract class PackageMarker {
    private PackageMarker() {
    }
}
