
public class Guerreiro extends Personagem{
	
	public Guerreiro(String nomeJogador, int pontosExtrasEnergia,  int pontosExtrasAtaque, 
			int pontosExtrasDefesa, int pontosExtrasSorte, int pontosExtrasAgilidade) {
		
		super(nomeJogador, pontosExtrasEnergia, pontosExtrasAtaque+5, pontosExtrasDefesa, 
				pontosExtrasAgilidade);
	}
	
	@Override
	protected int executarAtaqueEspecial() {
		super.decrementaEnergia(1);
		super.decrementaVida(3);
		return super.getAtaque() + dado.nextInt(1,10)*2; 
	}

}
