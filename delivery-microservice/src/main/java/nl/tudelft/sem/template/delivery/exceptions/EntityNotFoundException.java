package nl.tudelft.sem.template.delivery.exceptions;

import org.springframework.ui.Model;

import javax.persistence.Entity;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String entityName, long id) {
        super("Could not find entity " + entityName + " with id " + id);
    }

    public EntityNotFoundException(Class<?> clazz, long id) {
        this(clazz.getSimpleName(), id);
    }
}
