import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Tabuleiro {
	
	public static final String CAMPO_VAZIO = "t0";
	public static final String CAMPO_DISPONIVEL = "t-3";
	public static final String CAMPO_MARCADO = "t-4";
	public static final String CAMPO_UM = "t1";
	public static final String CAMPO_DOIS = "t2";
	public static final String CAMPO_TRES = "t3";
	public static final String CAMPO_QUATRO = "t4";
	public static final String CAMPO_CINCO = "t5";
	public static final String CAMPO_SEIS= "t6";
	public static final String CAMPO_SETE = "t7";
	public static final String HAPPY_FACE = "face0";
	
	private static Tabuleiro tabuleiro;
	private Campo[][] matrizCampos;
	private int tamanho;
	private WebDriver driver;
	
	public static synchronized Tabuleiro getInstance() {
		if(tabuleiro == null) {
			tabuleiro = new Tabuleiro();
		}
		return tabuleiro;
	}
	
	private Tabuleiro(){
		driver = new ChromeDriver();
//		System.setProperty("webdriver.chrome.driver", "C:\\Users\\NAJA INFORMATICA\\Downloads\\a\\chromedriver.exe");
		driver.manage().window().maximize();
		driver.get("http://campo-minado-pro.com/");
	}
	
	public void trocaDificuldade() {
		driver.findElement(By.id("difficulty")).click();
	}

	public void run() throws InterruptedException {
//		driver.wait(1000);
		clicaCampoAleatorio();
		while(driver.findElement(By.id("face")).getAttribute("src").contains("media/face0.png")) {
			boolean achou = false;
			boolean continua = false;
			do{
				continua = marcaMinasObvias();
				System.out.println("Marcou mina obvia");
//				clicaCamposLivresEmVoltaMatriz();
//				clicaCamposLivresEmVoltaMatriz();
			}while(continua);
			
			if(!achou) {
//				clicaCamposLivresEmVoltaMatriz();
				clicaCampoAleatorio();
			}
		}
	}
	
	public void inicializaMatriz(int tamanho) {
		this.tamanho = tamanho;
		matrizCampos = new Campo[tamanho][tamanho];
		for(int i = 0; i < tamanho; i++) {
			for(int j = 0; j < tamanho; j++) {
				Campo campo =  new Campo(i, j);
				campo.setNumId("tile" + String.valueOf((i * this.tamanho) + j));
				campo.setStatusCampo(CAMPO_DISPONIVEL);
				campo.setDispensado(false);
				matrizCampos[i][j] = campo;
			}
		}
		System.out.println("Inicializou matriz " + tamanho + " x " +  tamanho);
	}
	
	public boolean marcaMinasObvias() {
		boolean achou = false;
		for(int i = 0; i < tamanho; i++) {
			for(int j = 0; j < tamanho; j++) {
				Campo campo = matrizCampos[i][j];
				if(!campoTemMinasEmVolta(campo)) {
					continue;
				}
				
				if(retornaQuantidadeMinasEmVoltaRestante(campo) == retornaCamposEmVoltaClicaveis(campo).size()
						&& !isMinasMarcadasSuficientes(campo)) {
					ArrayList<Campo> camposEmVolta = (ArrayList<Campo>) retornaCamposEmVoltaClicaveis(campo);
					
					for(Campo campoEmvolta : camposEmVolta) {
//						if(!isMinasMarcadasSuficientes(campoEmvolta) 
//								&& retornaQuantidadeMinasEmVoltaRestante(campoEmvolta) == retornaCamposEmVoltaClicaveis(campoEmvolta).size()) {
							achou = true;
							marcaMinaEmCampo(campoEmvolta);
//						}
						
					}
					
					for(int k = 0; k < 9; k++) {
						clicaCamposLivresEmVoltaCampoMarcado(camposEmVolta);
					}
					
					if(achou) {
						return achou;
					}
				}
			}
		}
		
		return achou;
	}
	
	public boolean isMinasMarcadasSuficientes(Campo campo) {
		if(retornaQuantidadeMinasEmVoltaRestante(campo) == -3) {
			return false;
		}
//		System.out.println(retornaQuantidadeMinasEmVolta(campo) + " " + retornaCamposJaMarcadosEmVolta(campo).size());
		
		return retornaCamposJaMarcadosEmVolta(campo).size() == retornaQuantidadeMinasEmVoltaTotal(campo);
	}
	
	public int getTamanho() {
		return tamanho;
	}
	
	public void atualizaCampos() {
		
		for(int i = 0; i < tamanho; i++) {
			for(int j = 0; j < tamanho; j++) {
				Campo campo = matrizCampos[i][j];
				if(campo.getStatusCampo().equals(CAMPO_MARCADO) || campo.getStatusCampo().equals(CAMPO_VAZIO) || campo.isDispensado()) {
					continue;
				}
				String numId = "tile" + String.valueOf((i * this.tamanho) + j);
				WebElement elemento = driver.findElement(By.id(numId));
				String imgFull = elemento.getAttribute("src");
				String novoStatus = imgFull.substring(imgFull.lastIndexOf('/') + 1, imgFull.lastIndexOf('.'));
				if(campo.getStatusCampo().equals(novoStatus)) {
					continue;
				}
				campo.setStatusCampo(novoStatus);
				matrizCampos[i][j].setStatusCampo(novoStatus);
				System.out.println("Atualizou campo (" + i + "," + j + ")");
				
				if(novoStatus.equals(CAMPO_VAZIO) || 
						(campoPossuiMinasEmVolta(matrizCampos[i][j]) && isMinasMarcadasSuficientes(matrizCampos[i][j]))) {
					campo.setDispensado(true);
				}
				
//				retornaCampo(numId).setStatusCampo(statusCampo);
			}
		}
		
	}
	
	public boolean campoPossuiMinasEmVolta(Campo campo) {
		return (campo.getStatusCampo().equals(CAMPO_UM) || campo.getStatusCampo().equals(CAMPO_DOIS) || campo.getStatusCampo().equals(CAMPO_TRES) || campo.getStatusCampo().equals(CAMPO_QUATRO));
	}
	
	public boolean isCampoClicavel(String numId) {
		Campo campo = retornaCampo(numId);
		return campo != null && campo.getStatusCampo().equals(CAMPO_DISPONIVEL);
	}
	
	public boolean isCampoClicavel(Campo campo) {
		return campo != null && campo.getStatusCampo().equals(CAMPO_DISPONIVEL);
	}
	
	public boolean isCampoClicavelOuMina(Campo campo) {
		return campo != null && campo.getStatusCampo().equals(CAMPO_DISPONIVEL) || campo.getStatusCampo().equals(CAMPO_MARCADO);
	}
	
	public boolean campoTemMinasEmVolta(Campo campo) {
		return campo.getStatusCampo().equals(CAMPO_UM) || campo.getStatusCampo().equals(CAMPO_DOIS) || campo.getStatusCampo().equals(CAMPO_TRES) || campo.getStatusCampo().equals(CAMPO_QUATRO) || campo.getStatusCampo().equals(CAMPO_CINCO);
	}
	
	public int retornaPosicaoX(String numId) {
		int num = converteIdParaInt(numId);
		
		if(num > tamanho) {
			return 0;
		}else {
			return num % tamanho;
		}
	}
	public int retornaPosicaoY(String numId) {
		int num = converteIdParaInt(numId);
		
		if(num > tamanho) {
			return 0;
		}else {
			return num % tamanho;
		}
	}
	
	public static int converteIdParaInt(String numId) {
		return Integer.valueOf(numId.replaceAll("tile", ""));
	}
	
	public Campo retornaCampo(String numId) {
		int posicaoX = retornaPosicaoX(numId);
		int posicaoY = retornaPosicaoY(numId);
		
		return this.matrizCampos[posicaoX][posicaoY];
	}
	
	public Campo retornaCampo(int posicaoX, int posicaoY) {
		if(posicaoX < 0 || posicaoY < 0 || posicaoX == (this.tamanho) || posicaoY == (this.tamanho)) {
			return null;
		}
		
		return this.matrizCampos[posicaoX][posicaoY];
	}
	
	public List<Campo> retornaCamposEmVoltaClicaveis(Campo campo) {
		int posicaoX = campo.getPosicaoX();
		int posicaoY = campo.getPosicaoY();
		List<Campo> camposClicaveis = new ArrayList<Campo>();
		List<Campo> camposEmVolta = new ArrayList<Campo>();
		
		if(retornaCampo(posicaoX - 1, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY - 1));
		}
		if(retornaCampo(posicaoX - 1, posicaoY) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY));
		}
		if(retornaCampo(posicaoX - 1, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY + 1));
		}
		if(retornaCampo(posicaoX, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX, posicaoY - 1));
		}
		if(retornaCampo(posicaoX, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX, posicaoY + 1));
		}
		if(retornaCampo(posicaoX + 1, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY - 1));
		}
		if(retornaCampo(posicaoX + 1, posicaoY) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY));
		}
		if(retornaCampo(posicaoX + 1, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY + 1));
		}
		
		for (Campo campoEmVolta : camposEmVolta) {
			if(campoEmVolta == null) {
				continue;
			}
			if(isCampoClicavel(campoEmVolta)) {
				camposClicaveis.add(campoEmVolta);
			}
		}
		
		return camposClicaveis;
	}
	
	public List<Campo> retornaCamposEmVoltaClicaveisOuMina(Campo campo) {
		List<Campo> camposClicaveis = new ArrayList<Campo>();
		List<Campo> camposEmVolta = retornaCamposEmVolta(campo);
		
		for (Campo campoEmVolta : camposEmVolta) {
			if(campoEmVolta == null) {
				continue;
			}
			if(isCampoClicavelOuMina(campoEmVolta)) {
				camposClicaveis.add(campoEmVolta);
			}
		}
		
		return camposClicaveis;
	}
	
	public List<Campo> retornaCamposJaMarcadosEmVolta(Campo campo) {
		List<Campo> camposComMinaMarcada = new ArrayList<Campo>();
		List<Campo> camposEmVolta = retornaCamposEmVolta(campo);
		
		
		for (Campo campoEmVolta : camposEmVolta) {
			if(campoEmVolta == null) {
				continue;
			}
			if(campoEmVolta.getStatusCampo().equals(CAMPO_MARCADO)) {
				camposComMinaMarcada.add(campoEmVolta);
			}
		}
		
		return camposComMinaMarcada;
	}
	
	public List<Campo> retornaCamposEmVolta(Campo campo){
		int posicaoX = campo.getPosicaoX();
		int posicaoY = campo.getPosicaoY();
		List<Campo> camposEmVolta = new ArrayList<Campo>();
		
		if(retornaCampo(posicaoX - 1, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY - 1));
		}
		if(retornaCampo(posicaoX - 1, posicaoY) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY));
		}
		if(retornaCampo(posicaoX - 1, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX - 1, posicaoY + 1));
		}
		if(retornaCampo(posicaoX, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX, posicaoY - 1));
		}
		if(retornaCampo(posicaoX, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX, posicaoY + 1));
		}
		if(retornaCampo(posicaoX + 1, posicaoY - 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY - 1));
		}
		if(retornaCampo(posicaoX + 1, posicaoY) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY));
		}
		if(retornaCampo(posicaoX + 1, posicaoY + 1) != null) {
			camposEmVolta.add(retornaCampo(posicaoX + 1, posicaoY + 1));
		}
		
		return camposEmVolta;
	}
	
	public List<Campo> retornaCamposComNumeroEmVolta(Campo campo){
		List<Campo> camposEmVolta = retornaCamposEmVolta(campo);
		List<Campo> camposComNumerosEmVolta = new ArrayList<Campo>();
		
		for (Campo campo2 : camposEmVolta) {
			if(campoPossuiMinasEmVolta(campo2)) {
				camposComNumerosEmVolta.add(campo2);
			}
		}
		
		return camposComNumerosEmVolta;
	}
	
	
	public int retornaQuantidadeMinasEmVoltaRestante(Campo campo) {
		int quantidadeTotalMinasEmVolta = Integer.valueOf(campo.getStatusCampo().replace("t", ""));
		int quantidadeCamposJaMarcados = retornaCamposJaMarcadosEmVolta(campo).size();
		return quantidadeTotalMinasEmVolta - quantidadeCamposJaMarcados;
	}
	
	public int retornaQuantidadeMinasEmVoltaTotal(Campo campo) {
		return Integer.valueOf(campo.getStatusCampo().replace("t", ""));
	}
	
	public void clicaCampoAleatorio() {
		Campo campo = matrizCampos[retornaNumeroAleatorio(0, tamanho - 1)][retornaNumeroAleatorio(0, tamanho - 1)];
		if(isCampoClicavel(campo) && !campo.isDispensado()) {
			clicaCampo(campo);
			System.out.println("clicou campo aleatório posicao (" + campo.getPosicaoX() + "," + campo.getPosicaoY() + ")");
			atualizaCampos();
		}else {
			clicaCampoAleatorio();
		}
	}
	
	public void clicaCampo(Campo campo) {
		driver.findElement(By.id(campo.getNumId())).click();
		System.out.println("Clicou campo posicao (" + campo.getPosicaoX() + "," + campo.getPosicaoY() + ")");
	}
	
	public void marcaMinaEmCampo(Campo campo) {
		if(!campo.getStatusCampo().equals(CAMPO_MARCADO)){
			driver.findElement(By.id("flipbuttons")).click();
			clicaCampo(campo);
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			matrizCampos[campo.getPosicaoX()][campo.getPosicaoY()].setStatusCampo(CAMPO_MARCADO);
			campo.setStatusCampo(CAMPO_MARCADO);
			System.out.println("Marcou campo posicao (" + campo.getPosicaoX() + "," + campo.getPosicaoY() + ")");
			driver.findElement(By.id("flipbuttons")).click();
		}
	}
	
	public boolean clicaCamposLivresEmVoltaMatriz() {
		boolean achou = false;
		for(int i = 0; i < tamanho; i++) {
			for(int j = 0; j < tamanho; j++) {
				Campo campo = matrizCampos[i][j];
				if(campo.isDispensado()) {
					continue;
				}
				if(retornaQuantidadeMinasEmVoltaRestante(campo) == 0) {
					achou = true;
					clicaCampo(campo);
					atualizaCampos();
				}
			}
		}
		
		return achou;
	}
	
	public void clicaCamposLivresEmVoltaCampoMarcado(ArrayList<Campo> camposMarcados) {
		for (Campo campoMarcado : camposMarcados) {
			ArrayList<Campo> camposComNumeroEmVolta = (ArrayList<Campo>) retornaCamposComNumeroEmVolta(campoMarcado);
			for (Campo campoComNumero : camposComNumeroEmVolta) {
				if(retornaCamposEmVoltaClicaveis(campoComNumero).size() == 0) {
					continue;
				}
				if(retornaQuantidadeMinasEmVoltaRestante(campoComNumero) == 0) {
					clicaCampo(campoComNumero);
					atualizaCampos();
				}
			}
		}		
	}
	
	public ArrayList<Campo> retornaMinasMarcadasEmVolta(Campo campo) {
		ArrayList<Campo> camposEmVolta = (ArrayList<Campo>) retornaCamposEmVolta(campo);
		ArrayList<Campo> camposMarcadosEmVolta = new ArrayList<Campo>();
		
		for (Campo campoEmVolta : camposEmVolta) {
		if(campoEmVolta.getStatusCampo().equals(CAMPO_MARCADO)) {
				camposMarcadosEmVolta.add(campoEmVolta);
			}
			
		}
		
		return camposMarcadosEmVolta;
	}
	
	private static int retornaNumeroAleatorio(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("Maximo tem que ser menor que o minimo");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
