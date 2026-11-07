
import java.util.Random;
import java.util.Scanner;

public class Main {

	// Um único Scanner para todo o programa (vários Scanners sobre System.in causam leituras perdidas)
	private static final Scanner scanner = new Scanner(System.in);
	private static final Random sorteio = new Random();

	// Ações possíveis em um turno
	private static final int ATAQUE_SIMPLES = 1;
	private static final int ATAQUE_ESPECIAL = 2;
	private static final int DESCANSAR = 3;

	public static void main(String[] args) {
		Personagem jogador = null;
		int opcao;

		do {
			System.out.println("\n=== RPG FAQI ===");
			System.out.println("1 - Iniciar Batalha");
			System.out.println("2 - Criar seu Jogador");
			System.out.println("0 - Sair");
			System.out.print("Digite a opção desejada: ");
			opcao = lerInteiro(0, 2);

			switch (opcao) {
				case 1:
					if (jogador == null) {
						System.out.println("Você ainda não tem um personagem. Vamos criá-lo!");
						jogador = criarPersonagem();
					}
					batalhar(jogador, criarAdversario());
					break;
				case 2:
					jogador = criarPersonagem();
					System.out.println("Personagem criado! " + jogador.estadoPersonagem());
					break;
				case 0:
					System.out.println("Até a próxima!");
					break;
			}
		} while (opcao != 0);
	}

	// ============================== BATALHA ==============================

	public static void batalhar(Personagem jogador, Personagem adversario) {
		jogador.restaurar();
		adversario.restaurar();
		System.out.println("\n=== " + jogador.getNome() + " x " + adversario.getNome() + " ===");

		while (jogador.estaVivo() && adversario.estaVivo()) {
			System.out.println(jogador.estadoPersonagem());
			System.out.println(adversario.estadoPersonagem());

			// *********************** TURNO DO JOGADOR ***********************
			int acaoJogador = escolherAcaoJogador(jogador);
			executarAcao(jogador, adversario, acaoJogador);

			// *********************** TURNO DA MÁQUINA ***********************
			// só age se os dois continuam vivos (o especial do Guerreiro custa vida a ele mesmo!)
			if (jogador.estaVivo() && adversario.estaVivo()) {
				int acaoMaquina = escolherAcaoMaquina(adversario);
				executarAcao(adversario, jogador, acaoMaquina);
			}

			// ******************** FIM DA RODADA: RECUPERAÇÃO ********************
			if (jogador.estaVivo() && adversario.estaVivo()) {
				jogador.recuperarEnergia();
				adversario.recuperarEnergia();
				Narrador.narrar("Fim da rodada: cada um recupera " + Personagem.RECUPERACAO_POR_RODADA
						+ " de energia.");
			}

		}

		registrarResultado(jogador, adversario);
	}

	
	 // Aplica a ação escolhida. O "atacante" calcula a força do golpe e o "alvo"
	 // calcula quanto dano realmente sofre (esquiva + bloqueio) no método defender().
	
	private static void executarAcao(Personagem atacante, Personagem alvo, int acao) {
		if (acao == ATAQUE_SIMPLES) {
			int forca = atacante.atacar();
			Narrador.narrar(atacante.getNome() + " usa ATAQUE SIMPLES (força " + forca + ")");
			aplicarDano(alvo, forca);

		} else if (acao == ATAQUE_ESPECIAL) {
			int forca = atacante.atacarEspecial();
			Narrador.narrar(atacante.getNome() + " usa ATAQUE ESPECIAL (força " + forca + ")");
			aplicarDano(alvo, forca);

		} else if (acao == DESCANSAR) {
			atacante.descansar();
			Narrador.narrar(atacante.getNome() + " descansa e recupera 2 de energia extra.");
		}
	}

	private static void aplicarDano(Personagem alvo, int forca) {
		int dano = alvo.defender(forca);
		if (dano > 0) {
			Narrador.narrar(alvo.getNome() + " sofreu " + dano + " de dano.");
		} else {
			Narrador.narrar(alvo.getNome() + " não sofreu dano.");
		}
	}

	private static int escolherAcaoJogador(Personagem jogador) {
		while (true) {
			System.out.print("Sua vez: 1 - ataque simples; 2 - ataque especial (custa "
					+ Personagem.CUSTO_ESPECIAL + " de energia); 3 - descansar: ");
			int acao = lerInteiro(1, 3);
			if (acao == ATAQUE_ESPECIAL && jogador.getEnergia() < Personagem.CUSTO_ESPECIAL) {
				System.out.println("Energia insuficiente para o ataque especial! Escolha outra ação.");
			} else {
				return acao;
			}
		}
	}

	
	 // "Inteligência" simples da máquina:
	 // - com energia, executa o especial;
	 // - sem energia, 30% de chance de descansar e 70% de executar ataque simples
	
	private static int escolherAcaoMaquina(Personagem maquina) {
		int acao = 0;
		if(maquina.getEnergia() >= Personagem.CUSTO_ESPECIAL) {
			acao = ATAQUE_ESPECIAL;
		} else if(sorteio.nextInt(1,11)<=3){
			acao = DESCANSAR;
		} else {
			acao = ATAQUE_SIMPLES;
		}
		
		return acao;
	}

	private static void registrarResultado(Personagem jogador, Personagem adversario) {
		Narrador.narrar("\n=== FIM DA BATALHA ===");
		System.out.println(jogador.estadoPersonagem());
		System.out.println(adversario.estadoPersonagem());

		if (jogador.estaVivo() && !adversario.estaVivo()) {
			Narrador.narrar("Você venceu!");
		} else if (!jogador.estaVivo() && adversario.estaVivo()) {
			Narrador.narrar("Você perdeu!");
		} else {
			Narrador.narrar("Empate!");
		}
	}

	// ============================== CRIAÇÃO ==============================

	public static Personagem criarPersonagem() {
		System.out.print("Digite um nome para o seu jogador: ");
		String nomeJogador = scanner.nextLine().trim();

		System.out.println("Selecione a classe do seu jogador: ");
		System.out.println("1 - Guerreiro");
		// TODO aluno: inclua aqui as suas classes criadas (ex: 2 - Mago)
		int classeJogador = lerInteiro(1, 1);

		int pontosDisponiveis = 10;
		int pontosExtrasEnergia = lerPontos("energia", pontosDisponiveis);
		pontosDisponiveis -= pontosExtrasEnergia;
		int pontosExtrasAtaque = lerPontos("ataque", pontosDisponiveis);
		pontosDisponiveis -= pontosExtrasAtaque;
		int pontosExtrasDefesa = lerPontos("defesa", pontosDisponiveis);
		pontosDisponiveis -= pontosExtrasDefesa;
		int pontosExtrasSorte = lerPontos("sorte", pontosDisponiveis);
		pontosDisponiveis -= pontosExtrasSorte;
		int pontosExtrasAgilidade = lerPontos("agilidade", pontosDisponiveis);
		pontosDisponiveis -= pontosExtrasAgilidade;

		switch (classeJogador) {
			// TODO aluno: case 2: return new SuaClasse(...);
			default:
				return new Guerreiro(nomeJogador, pontosExtrasEnergia, pontosExtrasAtaque,
						pontosExtrasDefesa, pontosExtrasSorte, pontosExtrasAgilidade);
		}
	}

	public static Personagem criarAdversario() {
		String[] nomes = { "Agamenon", "Brutus", "Cassandra", "Draco" };
		String nome = nomes[sorteio.nextInt(nomes.length)];
		return new Guerreiro(nome, 2, 2, 2, 2, 2);
	}

	// ============================== LEITURA ==============================

	private static int lerPontos(String atributo, int disponiveis) {
		System.out.print("Dos " + disponiveis + " pontos disponíveis, quantos de " + atributo + " extra? ");
		return lerInteiro(0, disponiveis);
	}

	private static int lerInteiro(int min, int max) {
		while (true) {
			String linha = scanner.nextLine().trim();
			try {
				int valor = Integer.parseInt(linha);
				if (valor >= min && valor <= max) {
					return valor;
				}
			} catch (NumberFormatException e) {
				// cai na mensagem abaixo
			}
			System.out.print("Valor inválido!");
		}
	}
}
