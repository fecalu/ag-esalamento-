package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class AlgoritmoGeneticoEnsalamentoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AlgoritmoGeneticoEnsalamentoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ===== PROFESSORES =====
		Professor prof1 = new Professor("Carlos Wagner");
		prof1.adicionarDisponibilidade(DiaDaSemana.SEGUNDA, Horario.HORARIO_1);
		prof1.adicionarDisponibilidade(DiaDaSemana.SEGUNDA, Horario.HORARIO_2);
		prof1.adicionarDisponibilidade(DiaDaSemana.SEGUNDA, Horario.HORARIO_3);
		prof1.adicionarDisponibilidade(DiaDaSemana.SEGUNDA, Horario.HORARIO_4);
		prof1.setCargaHorariaSemanal(4);

		Professor prof2 = new Professor("Roberto Reis");
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_1);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_2);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_3);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_4);
		prof2.setCargaHorariaSemanal(4);

		Professor prof3 = new Professor("Dadilton");
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_1);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_2);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_3);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_4);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_1);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_2);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_3);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_4);
		prof3.setCargaHorariaSemanal(8);

		Professor prof4 = new Professor("Jose Nunes");
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_1);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_2);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_3);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_4);
		prof4.setCargaHorariaSemanal(4);

		// ===== DISCIPLINAS =====
		Disciplina sd = new Disciplina("Sistemas Distribuídos", null, prof1, true, 25, 4);
		Disciplina gp = new Disciplina("Gerenciamento de Projetos", null, prof2, false, 40, 4);
		Disciplina ia = new Disciplina("Inteligência Artificial", null, prof3, true, 20, 4);
		Disciplina pm = new Disciplina("Programação Móvel", null, prof3, true, 25, 4);
		Disciplina dj = new Disciplina("Desenvolvimento de Jogos", null, prof4, true, 30, 4);

		// ===== SALAS =====
		Sala sala05 = new Sala("Sala 05", 25, true, 25);
		Sala sala209 = new Sala("Sala 209", 40, false, 40);
		Sala sala04 = new Sala("Sala 04", 25, true, 25);
		Sala sala02 = new Sala("Sala 02", 30, true, 30);

		// ===== LISTAS FINAIS =====
		List<Disciplina> disciplinas = List.of(sd, gp, ia, pm, dj);
		List<Sala> salas = List.of(sala05, sala209, sala04, sala02);

		// Configuração do algoritmo genético
		AlgoritmoGenetico ag = new AlgoritmoGenetico(300);

		// Execução do algoritmo (1000 gerações)
		Individuo melhorSolucao = ag.resolve(1000, disciplinas, salas);

		// Exibição dos resultados
		System.out.println("\n=== MELHOR SOLUÇÃO ENCONTRADA ===");
		System.out.println(melhorSolucao.toString());

		// Análise de conflitos e carga horária
		System.out.println("\n=== ANÁLISE DE CONFLITOS ===");
		analisarConflitos(melhorSolucao);

		System.out.println("\n=== CARGA HORÁRIA DOS PROFESSORES ===");
		verificarCargaHorariaCompleta(melhorSolucao);
	}

	private void verificarCargaHorariaCompleta(Individuo individuo) {
		Map<Professor, Integer> horasAlocadas = new HashMap<>();
		Map<Professor, List<String>> detalhesAlocacao = new HashMap<>();

		for (int i = 0; i < individuo.getDisciplinas().size(); i++) {
			Disciplina disciplina = individuo.getDisciplinas().get(i);
			Professor prof = disciplina.getProfessor();
			List<Individuo.Alocacao> alocacoes = individuo.getCromossomo().get(i);

			horasAlocadas.put(prof, horasAlocadas.getOrDefault(prof, 0) + alocacoes.size());

			for (Individuo.Alocacao aloc : alocacoes) {
				detalhesAlocacao.computeIfAbsent(prof, k -> new ArrayList<>())
						.add(disciplina.getNome() + " (" + aloc.dia.getDia() + ", " + aloc.horario.getHorario() + ")");
			}
		}

		horasAlocadas.forEach((prof, horas) -> {
			System.out.printf("\nProfessor %s: %d/%d horas alocadas%n",
					prof.getNome(), horas, prof.getCargaHorariaSemanal());

			if (detalhesAlocacao.containsKey(prof)) {
				System.out.println("Detalhes das aulas:");
				detalhesAlocacao.get(prof).forEach(System.out::println);
			}
		});
	}

	private void analisarConflitos(Individuo individuo) {
		// Mapa para armazenar alocações por horário (chave: dia_horario)
		Map<String, List<Map.Entry<Integer, Individuo.Alocacao>>> alocacoesPorHorario = new HashMap<>();

		// Preenche o mapa com alocações e seus índices de disciplina
		for (int i = 0; i < individuo.getDisciplinas().size(); i++) {
			List<Individuo.Alocacao> alocacoes = individuo.getCromossomo().get(i);
			for (Individuo.Alocacao aloc : alocacoes) {
				String chave = aloc.dia + "_" + aloc.horario.getHorario();
				alocacoesPorHorario.computeIfAbsent(chave, k -> new ArrayList<>()).add(Map.entry(i, aloc));
			}
		}

		// Analisa conflitos em cada horário
		for (Map.Entry<String, List<Map.Entry<Integer, Individuo.Alocacao>>> entry : alocacoesPorHorario.entrySet()) {
			List<Map.Entry<Integer, Individuo.Alocacao>> alocacoes = entry.getValue();
			if (alocacoes.size() > 1) {
				Set<Sala> salasUsadas = new HashSet<>();
				Set<Professor> professores = new HashSet<>();
				for (Map.Entry<Integer, Individuo.Alocacao> alocEntry : alocacoes) {
					int disciplinaIdx = alocEntry.getKey();
					Individuo.Alocacao aloc = alocEntry.getValue();
					Sala sala = individuo.getSalas().get(aloc.salaIndex);
					Professor prof = individuo.getDisciplinas().get(disciplinaIdx).getProfessor();

					// Verifica conflito de sala
					if (!salasUsadas.add(sala)) {
						System.out.println("Conflito de sala: " + sala.getNome() + " no horário " + entry.getKey());
					}

					// Verifica conflito de professor
					if (!professores.add(prof)) {
						System.out.println("Conflito de professor: " + prof.getNome() + " no horário " + entry.getKey());
					}
				}
			}
		}

		// Se não houver conflitos
		if (alocacoesPorHorario.values().stream().noneMatch(alocs -> alocs.size() > 1)) {
			System.out.println("Nenhum conflito detectado.");
		}
	}
}