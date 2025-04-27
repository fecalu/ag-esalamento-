package com.example.demo;

public class Disciplina {
    private String nome;
    private String horario;
    private Professor professor;
    private boolean necessitaComputador;
    private int numeroAlunos;

    public Disciplina() {}

    public Disciplina(String nome, String horario, Professor professor,
                      boolean necessitaComputador, int numeroAlunos) {
        this.nome = nome;
        this.horario = horario;
        this.professor = professor;
        this.necessitaComputador = necessitaComputador;
        this.numeroAlunos = numeroAlunos;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public boolean isNecessitaComputador() {
        return necessitaComputador;
    }

    public void setNecessitaComputador(boolean necessitaComputador) {
        this.necessitaComputador = necessitaComputador;
    }

    public int getNumeroAlunos() {
        return numeroAlunos;
    }

    public void setNumeroAlunos(int numeroAlunos) {
        this.numeroAlunos = numeroAlunos;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Prof: %s | %s | %s | Alunos: %d",
                nome,
                necessitaComputador ? "Precisa de PC" : "Não precisa de PC",
                professor.getNome(),
                horario,
                "Dia: " + getDiaFromHorario(),
                numeroAlunos);
    }

    private String getDiaFromHorario() {
        // Lógica para extrair dia do horário (implementar conforme necessidade)
        return "Segunda"; // Exemplo simplificado
    }
}