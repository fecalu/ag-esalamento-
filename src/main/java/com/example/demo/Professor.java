package com.example.demo;

import java.util.*;

public class Professor {
    private String nome;
    private Map<DiaDaSemana, Set<Horario>> disponibilidade;
    private int cargaHorariaSemanal;

    public Professor() {
        this.disponibilidade = new HashMap<>();
    }

    public Professor(String nome) {
        this();
        this.nome = nome;
    }

    public void adicionarDisponibilidade(DiaDaSemana dia, Horario horario) {
        this.disponibilidade
                .computeIfAbsent(dia, k -> new HashSet<>())
                .add(horario);
    }

    public boolean podeDarAula(DiaDaSemana dia, Horario horario) {
        return disponibilidade.getOrDefault(dia, new HashSet<>()).contains(horario);
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHorariaSemanal() {
        return cargaHorariaSemanal;
    }

    public void setCargaHorariaSemanal(int cargaHoraria) {
        this.cargaHorariaSemanal = cargaHoraria;
    }

    public Map<DiaDaSemana, Set<Horario>> getDisponibilidade() {
        return new HashMap<>(disponibilidade);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Professor: ").append(nome).append("\nDisponibilidade:\n");
        disponibilidade.forEach((dia, horarios) -> {
            sb.append("- ").append(dia).append(": ");
            horarios.forEach(horario -> sb.append(horario).append(" "));
            sb.append("\n");
        });
        return sb.toString();
    }
}