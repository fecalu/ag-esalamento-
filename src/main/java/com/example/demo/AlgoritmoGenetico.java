package com.example.demo;

import java.util.*;
import java.util.stream.Collectors;

public class AlgoritmoGenetico {
    private int tamanhoPopulacao;
    private List<Individuo> populacao;
    private int geracao;
    private Individuo melhorSolucao;

    public AlgoritmoGenetico(int tamanhoPopulacao) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.populacao = new ArrayList<>();
        this.geracao = 0;
        this.melhorSolucao = null;
    }

    public void inicializaPopulacao(List<Disciplina> disciplinas, List<Sala> salas) {
        for (int i = 0; i < tamanhoPopulacao; i++) {
            populacao.add(new Individuo(new ArrayList<>(disciplinas), new ArrayList<>(salas)));
        }
        melhorSolucao = populacao.get(0);
    }

    public void ordenaPopulacao() {
        populacao.sort((i1, i2) -> Double.compare(i2.getNotaAvaliacao(), i1.getNotaAvaliacao()));
    }

    public void melhorIndividuo(Individuo individuo) {
        if (melhorSolucao == null || individuo.getNotaAvaliacao() > melhorSolucao.getNotaAvaliacao()) {
            melhorSolucao = individuo.clone();
        }
    }

    public Individuo resolve(int numeroGeracoes, List<Disciplina> disciplinas, List<Sala> salas) {
        inicializaPopulacao(disciplinas, salas);

        // Avaliação inicial
        populacao.forEach(Individuo::avaliacao);
        ordenaPopulacao();
        melhorIndividuo(populacao.get(0));

        for (geracao = 0; geracao < numeroGeracoes; geracao++) {
            List<Individuo> novaPopulacao = new ArrayList<>();

            // Elitismo: mantém os 10% melhores
            int eliteSize = (int)(tamanhoPopulacao * 0.1);
            for (int i = 0; i < eliteSize; i++) {
                novaPopulacao.add(populacao.get(i).clone());
            }

            // Crossover para preencher o resto da população
            while (novaPopulacao.size() < tamanhoPopulacao) {
                Individuo pai1 = selecionarPai();
                Individuo pai2 = selecionarPai();
                List<Individuo> filhos = crossover(pai1, pai2);
                novaPopulacao.addAll(filhos);
            }

            populacao = novaPopulacao;

            // Avaliação e atualização
            populacao.forEach(Individuo::avaliacao);
            ordenaPopulacao();
            melhorIndividuo(populacao.get(0));

            // Remove indivíduos duplicados
            populacao = populacao.stream().distinct().collect(Collectors.toList());

            // Preenche com novos indivíduos se necessário
            while (populacao.size() < tamanhoPopulacao) {
                populacao.add(new Individuo(new ArrayList<>(disciplinas), new ArrayList<>(salas)));
            }
        }

        return melhorSolucao;
    }

    private Individuo selecionarPai() {
        int tamanhoTorneio = Math.max(5, (int)(populacao.size() * 0.1));
        Collections.shuffle(populacao);
        return populacao.subList(0, tamanhoTorneio).stream()
                .max((i1, i2) -> Double.compare(i1.getNotaAvaliacao(), i2.getNotaAvaliacao()))
                .get();
    }

    public List<Individuo> crossover(Individuo pai1, Individuo pai2) {
        List<Individuo> filhos = new ArrayList<>();
        List<List<Individuo.Alocacao>> cromossomoFilho1 = new ArrayList<>();
        List<List<Individuo.Alocacao>> cromossomoFilho2 = new ArrayList<>();

        for (int i = 0; i < pai1.getCromossomo().size(); i++) {
            if (Math.random() < 0.5) {
                cromossomoFilho1.add(new ArrayList<>(pai1.getCromossomo().get(i)));
                cromossomoFilho2.add(new ArrayList<>(pai2.getCromossomo().get(i)));
            } else {
                cromossomoFilho1.add(new ArrayList<>(pai2.getCromossomo().get(i)));
                cromossomoFilho2.add(new ArrayList<>(pai1.getCromossomo().get(i)));
            }
        }

        Individuo filho1 = new Individuo(pai1.getDisciplinas(), pai1.getSalas());
        filho1.setCromossomo(cromossomoFilho1);
        mutacao(filho1);

        Individuo filho2 = new Individuo(pai1.getDisciplinas(), pai1.getSalas());
        filho2.setCromossomo(cromossomoFilho2);
        mutacao(filho2);

        filhos.add(filho1);
        filhos.add(filho2);

        return filhos;
    }

    private void mutacao(Individuo individuo) {
        for (int i = 0; i < individuo.getCromossomo().size(); i++) {
            if (Math.random() < 0.15) {
                List<Individuo.Alocacao> alocacoes = individuo.getCromossomo().get(i);
                Disciplina d = individuo.getDisciplinas().get(i);
                Professor p = d.getProfessor();
                List<Map.Entry<DiaDaSemana, Set<Horario>>> disponibilidade = new ArrayList<>(p.getDisponibilidade().entrySet());
                Set<String> horariosUsados = new HashSet<>(); // Para evitar horários redundantes

                // Preenche horários já usados por esta disciplina
                for (Individuo.Alocacao aloc : alocacoes) {
                    horariosUsados.add(aloc.dia + "_" + aloc.horario.getHorario());
                }

                double acao = Math.random();
                if (acao < 0.3 && alocacoes.size() > 1) {
                    // Remove uma alocação
                    alocacoes.remove((int) (Math.random() * alocacoes.size()));
                } else if (acao < 0.6 && alocacoes.size() < d.getHorasSemanais() && !disponibilidade.isEmpty()) {
                    // Adiciona uma nova alocação
                    int tentativas = 10; // Limita tentativas para evitar loops infinitos
                    while (tentativas > 0 && !disponibilidade.isEmpty()) {
                        int idx = (int) (Math.random() * disponibilidade.size());
                        DiaDaSemana dia = disponibilidade.get(idx).getKey();
                        List<Horario> horarios = new ArrayList<>(disponibilidade.get(idx).getValue());
                        if (horarios.isEmpty()) {
                            disponibilidade.remove(idx);
                            continue;
                        }
                        Horario horario = horarios.get((int) (Math.random() * horarios.size()));
                        String chaveHorario = dia + "_" + horario.getHorario();

                        if (!horariosUsados.contains(chaveHorario)) {
                            int salaIndex = (int) (Math.random() * individuo.getSalas().size());
                            alocacoes.add(new Individuo.Alocacao(salaIndex, dia, horario));
                            horariosUsados.add(chaveHorario);
                            break;
                        }
                        tentativas--;
                    }
                } else if (!alocacoes.isEmpty()) {
                    // Altera uma alocação existente
                    int idxAloc = (int) (Math.random() * alocacoes.size());
                    int tentativas = 10;
                    while (tentativas > 0 && !disponibilidade.isEmpty()) {
                        int idxDisp = (int) (Math.random() * disponibilidade.size());
                        DiaDaSemana dia = disponibilidade.get(idxDisp).getKey();
                        List<Horario> horarios = new ArrayList<>(disponibilidade.get(idxDisp).getValue());
                        if (horarios.isEmpty()) {
                            disponibilidade.remove(idxDisp);
                            continue;
                        }
                        Horario horario = horarios.get((int) (Math.random() * horarios.size()));
                        String chaveHorario = dia + "_" + horario.getHorario();

                        if (!horariosUsados.contains(chaveHorario) || alocacoes.get(idxAloc).dia == dia && alocacoes.get(idxAloc).horario == horario) {
                            int salaIndex = (int) (Math.random() * individuo.getSalas().size());
                            alocacoes.set(idxAloc, new Individuo.Alocacao(salaIndex, dia, horario));
                            break;
                        }
                        tentativas--;
                    }
                }
            }
        }
    }
}