package ru.shishmakov.config;

/**
 * Empty marker class.<br/>
 * It used to identify package for type-safe in Spring {@code @ComponentScan(basePackageClasses = )}.
 */
public abstract class PackageMarker {
    private PackageMarker() {
    }
}
