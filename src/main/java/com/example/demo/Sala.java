package com.example.demo;

public class Sala {
    private String nome;
    private int capacidade;
    private boolean possuiComputadores;
    private int espacoDisponivel;

    public Sala() {}

    public Sala(String nome, int capacidade, boolean possuiComputadores, int espacoDisponivel) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.possuiComputadores = possuiComputadores;
        this.espacoDisponivel = espacoDisponivel;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public boolean isPossuiComputadores() {
        return possuiComputadores;
    }

    public void setPossuiComputadores(boolean possuiComputadores) {
        this.possuiComputadores = possuiComputadores;
    }

    public int getEspacoDisponivel() {
        return espacoDisponivel;
    }

    public void setEspacoDisponivel(int espacoDisponivel) {
        this.espacoDisponivel = espacoDisponivel;
    }

    @Override
    public String toString() {
        return String.format("%s | Capacidade: %d | %s | Espaço: %d",
                nome,
                capacidade,
                possuiComputadores ? "Com computadores" : "Sem computadores",
                espacoDisponivel);
    }
}