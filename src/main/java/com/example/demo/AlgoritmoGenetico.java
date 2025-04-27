package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            populacao.add(new Individuo(disciplinas, salas));
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

            // Elitismo: mantém os 2 melhores
            novaPopulacao.add(populacao.get(0).clone());
            novaPopulacao.add(populacao.get(1).clone());

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

            // Remove duplicados (opcional)
            populacao = populacao.stream().distinct().collect(Collectors.toList());
        }

        return melhorSolucao;
    }

    private Individuo selecionarPai() {
        // Torneio entre 3 indivíduos aleatórios
        return List.of(
                        populacao.get((int) (Math.random() * populacao.size())),
                        populacao.get((int) (Math.random() * populacao.size())),
                        populacao.get((int) (Math.random() * populacao.size()))
                ).stream()
                .max((i1, i2) -> Double.compare(i1.getNotaAvaliacao(), i2.getNotaAvaliacao()))
                .get();
    }

    public List<Individuo> crossover(Individuo pai1, Individuo pai2) {
        List<Individuo> filhos = new ArrayList<>();
        List<Integer> cromossomoFilho1 = new ArrayList<>();
        List<Integer> cromossomoFilho2 = new ArrayList<>();

        // Crossover de um ponto
        int pontoCorte = pai1.getCromossomo().size() / 2;

        // Filho 1: primeira parte do pai1 + segunda parte do pai2
        cromossomoFilho1.addAll(pai1.getCromossomo().subList(0, pontoCorte));
        cromossomoFilho1.addAll(pai2.getCromossomo().subList(pontoCorte, pai2.getCromossomo().size()));

        // Filho 2: primeira parte do pai2 + segunda parte do pai1
        cromossomoFilho2.addAll(pai2.getCromossomo().subList(0, pontoCorte));
        cromossomoFilho2.addAll(pai1.getCromossomo().subList(pontoCorte, pai1.getCromossomo().size()));

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
            if (Math.random() < 0.1) { // 10% de chance de mutação
                individuo.getCromossomo().set(i, (int) (Math.random() * individuo.getSalas().size()));
            }
        }
    }
}