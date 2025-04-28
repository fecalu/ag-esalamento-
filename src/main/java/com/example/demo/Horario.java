package com.example.demo;

public enum Horario {
    HORARIO_1("19h00 às 19h50"),
    HORARIO_2("19h50 às 20h40"),
    HORARIO_3("20h50 às 21h40"),
    HORARIO_4("21h40 às 22h30");

    private final String horario;

    Horario(String horario) {
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }
}