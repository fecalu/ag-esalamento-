package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

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

		Professor prof2 = new Professor("Roberto Reis");
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_1);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_2);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_3);
		prof2.adicionarDisponibilidade(DiaDaSemana.TERCA, Horario.HORARIO_4);

		Professor prof3 = new Professor("Dadilton");
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_1);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_2);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_3);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUARTA, Horario.HORARIO_4);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_1);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_2);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_3);
		prof3.adicionarDisponibilidade(DiaDaSemana.QUINTA, Horario.HORARIO_4);

		Professor prof4 = new Professor("Jose Nunes");
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_1);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_2);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_3);
		prof4.adicionarDisponibilidade(DiaDaSemana.SEXTA, Horario.HORARIO_4);

		// ===== DISCIPLINAS =====
		Disciplina sd = new Disciplina("Sistemas Distribuídos", "19h00 às 19h50", prof1, true, 25);
		Disciplina gp = new Disciplina("Gerenciamento de Projetos", "19h50 às 20h40", prof2, false, 40);
		Disciplina ia = new Disciplina("Inteligência Artificial", "20h50 às 21h40", prof3, true, 20);
		Disciplina pm = new Disciplina("Programação Móvel", "21h40 às 22h30", prof3, true, 25);
		Disciplina dj = new Disciplina("Desenvolvimento de Jogos", "19h00 às 19h50", prof4, true, 30);

		// ===== SALAS =====
		Sala sala05 = new Sala("Sala 05", 25, true, 25);
		Sala sala209 = new Sala("Sala 209", 40, false, 40);
		Sala sala04 = new Sala("Sala 04", 25, true, 25);
		Sala sala02 = new Sala("Sala 02", 30, true, 30);

		// ===== LISTAS FINAIS =====
		List<Disciplina> disciplinas = List.of(sd, gp, ia, pm, dj);
		List<Sala> salas = List.of(sala05, sala209, sala04, sala02);

		// Configuração do algoritmo genético
		AlgoritmoGenetico ag = new AlgoritmoGenetico(100); // População maior para melhor diversidade

		// Execução do algoritmo (200 gerações)
		Individuo melhorSolucao = ag.resolve(200, disciplinas, salas);

		// Exibição dos resultados
		System.out.println("\n=== MELHOR SOLUÇÃO ENCONTRADA ===");
		System.out.println(melhorSolucao.toString());

		// Análise de conflitos
		System.out.println("\n=== ANÁLISE DE CONFLITOS ===");
		analisarConflitos(melhorSolucao);
	}

	private void analisarConflitos(Individuo individuo) {
		boolean conflitosEncontrados = false;
		List<Disciplina> disciplinas = individuo.getDisciplinas();
		List<Integer> cromossomo = individuo.getCromossomo();
		List<Sala> salas = individuo.getSalas();

		// Verifica conflitos de professor
		for (int i = 0; i < disciplinas.size(); i++) {
			for (int j = i + 1; j < disciplinas.size(); j++) {
				if (disciplinas.get(i).getProfessor().equals(disciplinas.get(j).getProfessor()) &&
						disciplinas.get(i).getHorario().equals(disciplinas.get(j).getHorario())) {

					System.out.printf("CONFLITO: Professor %s alocado para %s e %s no mesmo horário (%s)\n",
							disciplinas.get(i).getProfessor().getNome(),
							disciplinas.get(i).getNome(),
							disciplinas.get(j).getNome(),
							disciplinas.get(i).getHorario());
					conflitosEncontrados = true;
				}
			}
		}

		// Verifica requisitos de computador
		for (int i = 0; i < disciplinas.size(); i++) {
			Disciplina d = disciplinas.get(i);
			Sala s = salas.get(cromossomo.get(i));

			if (d.isNecessitaComputador() && !s.isPossuiComputadores()) {
				System.out.printf("PROBLEMA: %s precisa de computador mas foi alocada na %s\n",
						d.getNome(), s.getNome());
				conflitosEncontrados = true;
			}
		}

		if (!conflitosEncontrados) {
			System.out.println("Nenhum conflito de alocação encontrado!");
		}
	}
}