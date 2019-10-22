
public class Campo {
	private int posicaoX;
	private int posicaoY;
	private String numId;
	private String statusCampo;
	private boolean dispensado;
	
	public Campo(String numId) {
		Tabuleiro tabuleiro = Tabuleiro.getInstance();
		
		this.numId = numId;
		posicaoX = tabuleiro.retornaPosicaoX(numId);
		posicaoY = tabuleiro.retornaPosicaoY(numId);
	}
	public Campo(int posicaoX, int posicaoY) {
		this.posicaoX = posicaoX;
		this.posicaoY = posicaoY;
	}

	public String getStatusCampo() {
		return statusCampo;
	}

	public void setStatusCampo(String statusCampo) {
		this.statusCampo = statusCampo;
	}
	
	public void setNumId(String numId) {
		this.numId = numId;
	}
	
	public String getNumId() {
		return numId;
	}
	
	public int getPosicaoX() {
		return posicaoX;
	}
	
	public void setPosicaoX(int posicaoX) {
		this.posicaoX = posicaoX;
	}
	
	public int getPosicaoY() {
		return posicaoY;
	}
	
	public void setPosicaoY(int posicaoY) {
		this.posicaoY = posicaoY;
	}
	public boolean isDispensado() {
		return dispensado;
	}
	public void setDispensado(boolean dispensado) {
		this.dispensado = dispensado;
	}
}
