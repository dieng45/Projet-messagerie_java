package com.association.messagerieg2.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static  final String PERSISTENCE_UNIT_NAME ="PERSISTENCE";
    private static EntityManagerFactory factory;

    public static EntityManagerFactory getFactoryEntityManagerFactory(){
        if (factory == null){
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory;
    }

    public static void shutdown(){
        if (factory != null){
            factory.close();
        }
    }
}
