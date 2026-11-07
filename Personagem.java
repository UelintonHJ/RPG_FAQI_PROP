

import java.util.Random;

public abstract class Personagem {
	public static final int CUSTO_ESPECIAL = 5;
	public static final int RECUPERACAO_POR_RODADA = 2;

	private Random rand;
	private String nome;
	private int vida;
	private int energia;
	private int energiaInicial;
	private int ataque;
	private int defesa;
	private int agilidade;
	private boolean estaVivo;
	protected Random dado;

	public Personagem(String nome, int pontosEnergia, int pontosAtaque,
			int pontosDefesa, int pontosAgilidade) {
		this.nome = nome;
		this.vida = 100;
		this.energia = 10 + pontosEnergia;
		this.energiaInicial = 10 + pontosEnergia;
		this.ataque = 10 + pontosAtaque;
		this.defesa = 3 + pontosDefesa;
		this.agilidade = 1 + pontosAgilidade;
		this.estaVivo = true;
		dado = new Random();
	}

	public final int atacar() {
		//o ataque simples é assim para todo mundo e não pode ser modificado
		return this.ataque;
	}

	//o atacarEspecial verifica se o personagem tem energia suficiente e se sim
	//realiza o desconto da energia e chama o método executarAtaqueEspecial
	//(que é abstract e deve obrigatoriamente ser implementado por quem herda Personagem)
	public final int atacarEspecial() {
		if(this.energia >= CUSTO_ESPECIAL) {
			this.energia -= CUSTO_ESPECIAL;
			return executarAtaqueEspecial();
		}
		return 0;
	}

	public final int defender(int ataque) {
		//se esquivar, não recebe dano
		if(this.esquivar()) {
			Narrador.narrar(this.nome + " esquivou do ataque!");
			return 0;
		}
		//se não esquivar, então calcula o bloqueio e recebe dano
		int bloqueio = this.defesa + (int) Math.round(dado.nextInt(1,7)/2.0);
		//se o bloqueio for maior que o ataque, o dano não pode ser negativo  
		//pois senão irá realizar a "cura" do defensor
		int dano = Math.max(0, ataque - bloqueio);
		this.decrementaVida(dano);
		return dano;
	}

	// recuperação extra, somada à recuperação normal da rodada
	public final int descansar() {
		this.energia += 2;
		return this.defesa;
	}

	// recuperação que todo personagem vivo recebe ao final de cada rodada
	public final void recuperarEnergia() {
		this.energia += RECUPERACAO_POR_RODADA;
	}

	// verifica a esquiva, com chance de 1 de 10 (10%) + 10% para cada ponto de agilidade 
	public final boolean esquivar() {
		return dado.nextInt(1, 11) <= this.agilidade;
	}

	//TODO: pode ser melhorada e sobrescrita nas classes que herdam de Personagem
	public String estadoPersonagem() {
		String mensagem = "[" + this.nome + "] - " + this.vida + " / " + this.energia;
	    return mensagem;
	}

	public final void restaurar() {
		this.vida = 100;
		this.energia = this.energiaInicial;
		this.estaVivo = true;
	}

	public final boolean estaVivo() {
		return this.estaVivo;
	}

	// NOVO: a Main precisa do nome para narrar a batalha
	public final String getNome() {
		return this.nome;
	}

	public final int getEnergia() {
		return this.energia;
	}

	public final int getEnergiaInicial() {
		return this.energiaInicial;
	}

	public final int getAtaque() {
		return this.ataque;
	}
	
	public final int getDefesa() {
		return this.ataque;
	}

	public final int getVida() {
		return this.vida;
	}
	
	public final int getAgilidade() {
		return this.energia;
	}

	public final void decrementaEnergia(int valor) {
		this.energia = Math.max(0, this.energia - valor);
	}

	public void decrementaVida(int valor) {
		this.vida -= valor;
		//muda o status de estaVivo para false quando vida zera
		if(this.vida <= 0) {
			this.vida = 0;
			this.estaVivo = false;
		}
	}
	


	//TODO aluno: deve ser implementado nas classes que herdam de personagem 
	protected abstract int executarAtaqueEspecial();

}
