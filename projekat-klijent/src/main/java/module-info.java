module stud_sluzba_desktop_client {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.web;
    requires spring.core;
    requires spring.webflux;
    requires reactor.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;

    requires jasperreports;
    exports org.raflab.studsluzbadesktopclient;
    exports org.raflab.studsluzbadesktopclient.dto;
    exports org.raflab.studsluzbadesktopclient.controllers;
    exports org.raflab.studsluzbadesktopclient.services;
    opens org.raflab.studsluzbadesktopclient.services to spring.core, javafx.fxml;
    opens org.raflab.studsluzbadesktopclient.controllers to spring.core, javafx.fxml;
    opens org.raflab.studsluzbadesktopclient to javafx.fxml, spring.beans, spring.context, spring.core;
    exports org.raflab.studsluzbadesktopclient.coder;
    opens org.raflab.studsluzbadesktopclient.coder to javafx.fxml, spring.beans, spring.context, spring.core;
    exports org.raflab.studsluzbadesktopclient.app;
    opens org.raflab.studsluzbadesktopclient.app to javafx.fxml, spring.beans, spring.context, spring.core;

    requires java.sql;
    requires java.desktop;
}