package com.example.demo;

import java.util.*;

public class Individuo {
    private List<Disciplina> disciplinas;
    private List<Sala> salas;
    private List<List<Alocacao>> cromossomo;
    private double notaAvaliacao;

    public static class Alocacao {
        int salaIndex;
        DiaDaSemana dia;
        Horario horario;

        public Alocacao(int salaIndex, DiaDaSemana dia, Horario horario) {
            this.salaIndex = salaIndex;
            this.dia = dia;
            this.horario = horario;
        }
    }

    public Individuo(List<Disciplina> disciplinas, List<Sala> salas) {
        this.disciplinas = new ArrayList<>(disciplinas);
        this.salas = new ArrayList<>(salas);
        this.cromossomo = new ArrayList<>();
        this.notaAvaliacao = 0.0;

        // Alteração: Inicialização com alocações em horários distintos
        for (Disciplina d : disciplinas) {
            List<Alocacao> alocacoes = new ArrayList<>();
            Professor p = d.getProfessor();
            List<Map.Entry<DiaDaSemana, Set<Horario>>> disponibilidade = new ArrayList<>(p.getDisponibilidade().entrySet());
            Set<String> horariosUsados = new HashSet<>(); // Para garantir horários distintos

            int horasRestantes = d.getHorasSemanais();
            while (horasRestantes > 0 && !disponibilidade.isEmpty()) {
                int idx = (int) (Math.random() * disponibilidade.size());
                DiaDaSemana dia = disponibilidade.get(idx).getKey();
                List<Horario> horarios = new ArrayList<>(disponibilidade.get(idx).getValue());
                if (horarios.isEmpty()) {
                    disponibilidade.remove(idx);
                    continue;
                }
                Horario horario = horarios.get((int) (Math.random() * horarios.size()));
                String chaveHorario = dia + "_" + horario.getHorario();

                // Verifica se o horário já foi usado para esta disciplina
                if (!horariosUsados.contains(chaveHorario)) {
                    int salaIndex = (int) (Math.random() * salas.size());
                    alocacoes.add(new Alocacao(salaIndex, dia, horario));
                    horariosUsados.add(chaveHorario);
                    horasRestantes--;
                }
            }
            this.cromossomo.add(alocacoes);
        }
    }

    public void avaliacao() {
        double nota = 1000.0; // Alteração: Base maior para permitir diferenciação
        Map<Professor, Integer> horasAlocadas = new HashMap<>();
        Map<Disciplina, Integer> aulasAlocadas = new HashMap<>();

        // Contabiliza horas alocadas
        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina disciplina = disciplinas.get(i);
            List<Alocacao> alocacoes = cromossomo.get(i);
            aulasAlocadas.put(disciplina, alocacoes.size());
            Professor prof = disciplina.getProfessor();
            horasAlocadas.put(prof, horasAlocadas.getOrDefault(prof, 0) + alocacoes.size());
        }

        // Penaliza carga horária das disciplinas
        for (Disciplina disciplina : disciplinas) {
            int aulasNecessarias = disciplina.getHorasSemanais();
            int aulasAtuais = aulasAlocadas.getOrDefault(disciplina, 0);
            if (aulasAtuais < aulasNecessarias) {
                nota -= 200 * (aulasNecessarias - aulasAtuais); // Penalização aumentada
            } else if (aulasAtuais > aulasNecessarias) {
                nota -= 300 * (aulasAtuais - aulasNecessarias); // Penalização por excesso
            }
        }

        // Penaliza carga horária dos professores
        for (Professor professor : horasAlocadas.keySet()) {
            int diferenca = horasAlocadas.get(professor) - professor.getCargaHorariaSemanal();
            if (diferenca > 0) {
                nota -= 400 * diferenca; // Penalização alta por excesso
            } else if (diferenca < 0) {
                nota -= 200 * Math.abs(diferenca); // Penalização por falta
            }
        }

        // Verifica conflitos e requisitos
        Map<String, List<Alocacao>> alocacoesPorHorario = new HashMap<>();
        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina d1 = disciplinas.get(i);
            List<Alocacao> alocacoes = cromossomo.get(i);
            Set<String> horariosDisciplina = new HashSet<>(); // Para verificar horários únicos por disciplina

            for (Alocacao aloc : alocacoes) {
                Sala s1 = salas.get(aloc.salaIndex);
                String chaveHorario = aloc.dia + "_" + aloc.horario.getHorario();
                alocacoesPorHorario.computeIfAbsent(chaveHorario, k -> new ArrayList<>()).add(aloc);

                // Verifica se o horário já foi usado pela mesma disciplina
                if (!horariosDisciplina.add(chaveHorario)) {
                    nota -= 500; // Penalização alta por alocações redundantes
                }

                // Verifica requisitos da disciplina
                if (d1.isNecessitaComputador() && !s1.isPossuiComputadores()) {
                    nota -= 150;
                }
                if (d1.getNumeroAlunos() > s1.getCapacidade()) {
                    nota -= 200;
                }

                // Verifica disponibilidade do professor
                if (!d1.getProfessor().podeDarAula(aloc.dia, aloc.horario)) {
                    nota -= 300;
                }
            }
        }

        // Verifica conflitos de sala e professor
        for (List<Alocacao> alocacoes : alocacoesPorHorario.values()) {
            if (alocacoes.size() > 1) {
                Set<Sala> salasUsadas = new HashSet<>();
                Set<Professor> professores = new HashSet<>();
                for (Alocacao aloc : alocacoes) {
                    Sala sala = salas.get(aloc.salaIndex);
                    int disciplinaIdx = -1;
                    for (int i = 0; i < cromossomo.size(); i++) {
                        if (cromossomo.get(i).contains(aloc)) {
                            disciplinaIdx = i;
                            break;
                        }
                    }
                    Professor prof = disciplinas.get(disciplinaIdx).getProfessor();
                    if (!salasUsadas.add(sala)) {
                        nota -= 500; // Penalização aumentada para conflito de sala
                    }
                    if (!professores.add(prof)) {
                        nota -= 600; // Penalização aumentada para conflito de professor
                    }
                }
            }
        }

        this.notaAvaliacao = Math.max(nota, 0);
    }

    public Individuo clone() {
        Individuo copia = new Individuo(this.disciplinas, this.salas);
        List<List<Alocacao>> novoCromossomo = new ArrayList<>();
        for (List<Alocacao> alocacoes : this.cromossomo) {
            novoCromossomo.add(new ArrayList<>(alocacoes));
        }
        copia.setCromossomo(novoCromossomo);
        copia.notaAvaliacao = this.notaAvaliacao;
        return copia;
    }

    public List<Disciplina> getDisciplinas() {
        return new ArrayList<>(disciplinas);
    }

    public List<Sala> getSalas() {
        return new ArrayList<>(salas);
    }

    public List<List<Alocacao>> getCromossomo() {
        List<List<Alocacao>> copia = new ArrayList<>();
        for (List<Alocacao> alocacoes : cromossomo) {
            copia.add(new ArrayList<>(alocacoes));
        }
        return copia;
    }

    public void setCromossomo(List<List<Alocacao>> cromossomo) {
        this.cromossomo = new ArrayList<>();
        for (List<Alocacao> alocacoes : cromossomo) {
            this.cromossomo.add(new ArrayList<>(alocacoes));
        }
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

        Map<DiaDaSemana, Map<String, List<String>>> alocacoes = new HashMap<>();
        for (DiaDaSemana dia : DiaDaSemana.values()) {
            Map<String, List<String>> horarios = new HashMap<>();
            for (Horario h : Horario.values()) {
                horarios.put(h.getHorario(), new ArrayList<>());
            }
            alocacoes.put(dia, horarios);
        }

        for (int i = 0; i < disciplinas.size(); i++) {
            Disciplina d = disciplinas.get(i);
            List<Alocacao> alocList = cromossomo.get(i);
            for (Alocacao aloc : alocList) {
                Sala s = salas.get(aloc.salaIndex);
                alocacoes.get(aloc.dia).get(aloc.horario.getHorario())
                        .add(String.format("%s (%s) - Prof. %s", d.getNome(), s.getNome(), d.getProfessor().getNome()));
            }
        }

        for (DiaDaSemana dia : DiaDaSemana.values()) {
            sb.append(dia.getDia().toUpperCase()).append(":\n");
            for (Horario h : Horario.values()) {
                List<String> alocacao = alocacoes.get(dia).get(h.getHorario());
                String texto = alocacao.isEmpty() ? "Livre" : String.join("; ", alocacao);
                sb.append(String.format("%-15s: %s\n", h.getHorario(), texto));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}