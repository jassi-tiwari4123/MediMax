package com.ohms.exception;

/**
 * ResourceNotFoundException — thrown when a requested entity doesn't exist in DB.
 *
 * Examples:
 *   - Doctor with id=99 not found
 *   - Appointment not found for given patient
 */
public class ResourceNotFoundException extends OhmsException {

    private final String resourceName;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(resourceName + " not found with id: " + resourceId);
        this.resourceName = resourceName;
        this.resourceId   = resourceId;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.resourceId   = null;
    }

    public String getResourceName() { return resourceName; }
    public Object getResourceId()   { return resourceId;   }
}
