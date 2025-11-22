import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe que implementa o cliente da calculadora.
 * Responsável por se conectar ao servidor, enviar a expressão e receber o resultado.
 */
public class ClienteCalculadora {

    // Endereço do servidor. 'localhost' significa a própria máquina.
    private static final String ENDERECO_SERVIDOR = "localhost";
    // A porta que o cliente tentará se conectar. Deve ser a mesma do servidor.
    private static final int PORTA = 12345;

    /**
     * Ponto de entrada do programa Cliente.
     */
    public static void main(String[] args) {
        new ClienteCalculadora().iniciar();
    }

    /**
     * Inicia a conexão com o servidor e gerencia a interação com o usuário.
     */
    public void iniciar() {
        // Tenta estabelecer a conexão com o servidor e abrir os streams de I/O.
        try (
                // Cria o Socket e tenta a conexão.
                Socket socket = new Socket(ENDERECO_SERVIDOR, PORTA);

                // PrintWriter: Stream de saída para enviar dados ao servidor.
                PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
                // BufferedReader: Stream de entrada para ler a resposta do servidor.
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                // Scanner: Usado para ler a entrada do teclado do usuário.
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao Servidor em " + ENDERECO_SERVIDOR + ":" + PORTA);
            System.out.println("Digite a expressão completa (suporta múltiplos valores e parênteses).");
            System.out.println("Exemplo: 5 + 3 * (10 / 2 - 1) ou 10 + 20 - 5");
            System.out.print("> ");

            // 1. Lê a expressão digitada pelo usuário.
            String operacao = scanner.nextLine();

            // 2. Envia a expressão completa como uma string para o servidor.
            saida.println(operacao);

            // 3. Aguarda e lê a resposta enviada pelo servidor.
            String resposta = entrada.readLine();

            // 4. Exibe o resultado ou a mensagem de erro.
            System.out.println("\n--- RESULTADO DO SERVIDOR ---");
            System.out.println("Resultado: " + resposta);
            System.out.println("-----------------------------\n");

        } catch (Exception e) {
            // Trata erros de conexão (ex: servidor desligado, porta errada).
            System.err.println("Erro ao conectar ou comunicar com o servidor: " + e.getMessage());
        }
    }
}