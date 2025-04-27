package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Individuo {
    private List<Disciplina> disciplinas;
    private List<Sala> salas;
    private List<Integer> cromossomo;
    private double notaAvaliacao;

    public Individuo(List<Disciplina> disciplinas, List<Sala> salas) {
        this.disciplinas = disciplinas;
        this.salas = salas;
        this.cromossomo = new ArrayList<>();
        this.notaAvaliacao = 0.0;

        // Inicialização aleatória do cromossomo
        for (int i = 0; i < disciplinas.size(); i++) {
            this.cromossomo.add((int) (Math.random() * salas.size()));
        }
    }

    public void avaliacao() {
        double nota = 100.0;

        // 1. Verifica conflitos de horário/sala/professor
        for (int i = 0; i < disciplinas.size(); i++) {
            for (int j = i + 1; j < disciplinas.size(); j++) {
                if (cromossomo.get(i).equals(cromossomo.get(j))) {
                    // Mesma sala
                    if (disciplinas.get(i).getHorario().equals(disciplinas.get(j).getHorario())) {
                        // Mesmo horário - conflito grave
                        nota -= 30;
                    }
                    if (disciplinas.get(i).getProfessor().equals(disciplinas.get(j).getProfessor())) {
                        // Mesmo professor - conflito
                        nota -= 20;
                    }
                }
            }
        }

        // 2. Verifica requisitos das disciplinas
        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina disciplina = disciplinas.get(i);
            Sala sala = salas.get(cromossomo.get(i));

            if (disciplina.isNecessitaComputador() && !sala.isPossuiComputadores()) {
                nota -= 15;
            }
            if (disciplina.getNumeroAlunos() > sala.getCapacidade()) {
                nota -= 25;
            }
            if (disciplina.getNumeroAlunos() < sala.getCapacidade() / 2) {
                nota -= 5; // Penaliza desperdício de espaço
            }
        }

        this.notaAvaliacao = Math.max(nota, 0);
    }

    public Individuo clone() {
        Individuo copia = new Individuo(this.disciplinas, this.salas);
        copia.cromossomo = new ArrayList<>(this.cromossomo);
        copia.notaAvaliacao = this.notaAvaliacao;
        return copia;
    }

    // Getters e Setters
    public List<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }

    public List<Sala> getSalas() {
        return new ArrayList<>(salas);
    }

    public List<Integer> getCromossomo() {
        return new ArrayList<>(cromossomo);
    }

    public void setCromossomo(List<Integer> cromossomo) {
        this.cromossomo = new ArrayList<>(cromossomo);
    }

    public double getNotaAvaliacao() {
        return notaAvaliacao;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== MELHOR SOLUÇÃO ENCONTRADA ===\n");
        sb.append("--- GRADE DE ALOCAÇÃO ---\n");
        sb.append("Nota: ").append(String.format("%.2f", notaAvaliacao)).append("\n\n");

        // Map para armazenar alocação por dia
        Map<DiaDaSemana, Map<String, String>> alocacoes = new HashMap<>();

        // Inicializa o mapa com "Livre" em todos os horários
        for (DiaDaSemana dia : DiaDaSemana.values()) {
            Map<String, String> horarios = new HashMap<>();
            for (Horario h : Horario.values()) {
                horarios.put(h.getHorario(), "Livre");
            }
            alocacoes.put(dia, horarios);
        }

        // Preenche o mapa com as alocações reais
        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina d = disciplinas.get(i);
            Sala s = salas.get(cromossomo.get(i));
            DiaDaSemana dia = d.getProfessor().getDisponibilidade().keySet().iterator().next(); // Obtém o dia da semana
            String horario = d.getHorario();

            // Atualiza a alocação com a disciplina, sala e professor
            alocacoes.get(dia).put(horario, String.format("%s (%s) - Prof. %s", d.getNome(), s.getNome(), d.getProfessor().getNome()));
        }

        // Formata a saída por dia
        for (DiaDaSemana dia : DiaDaSemana.values()) {
            sb.append(dia.getDia().toUpperCase()).append(":\n");

            // Para cada horário no dia, adiciona a alocação ou "Livre"
            for (Horario h : Horario.values()) {
                String alocacao = alocacoes.get(dia).get(h.getHorario());
                sb.append(String.format("%-15s: %s\n", h.getHorario(), alocacao));
            }

            sb.append("\n");
        }

        return sb.toString();
    }

}