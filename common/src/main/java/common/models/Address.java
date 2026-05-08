package common.models;

import java.io.Serializable;

/**
 * Адрес организации (почтовый индекс).
 */
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    private String zipCode;

    /** Создаёт адрес. */
    public Address(String zipCode) {
        if (zipCode != null && zipCode.length() > 19) {
            throw new IllegalArgumentException("zipCode cannot be longer than 19 characters");
        }
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address {zipCode='" + zipCode + "'}";
    }
}