package com.submeter.service;

import com.submeter.data.db.DatabaseInitializer;

public class DatabaseSetup {
    public void initializeDb(String dburl,String userName,String password,String driverClassName) throws Exception {
        //1. Kill the old connection engine first
        DatabaseInitializer.initializeDb(dburl,userName,password,driverClassName);
//        DatabaseInitializer.initializeDb();
    }

    public void initializeWithDefault(String androidOrDesktop) throws Exception {
        /**
         * Pass "Android" for android , and for Desktop "Desktop"
         */
        DatabaseInitializer.initializeDefaultDb(androidOrDesktop);
    }
}
