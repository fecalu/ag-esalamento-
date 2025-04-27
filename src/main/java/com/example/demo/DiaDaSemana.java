package com.example.demo;

public enum DiaDaSemana {
    SEGUNDA("segunda"),
    TERCA("terca"),
    QUARTA("quarta"),
    QUINTA("quinta"),
    SEXTA("sexta"),
    SABADO("sabado");

    private final String dia;

    DiaDaSemana(String dia) {
        this.dia = dia;
    }

    public String getDia() {
        return dia;
    }
}
