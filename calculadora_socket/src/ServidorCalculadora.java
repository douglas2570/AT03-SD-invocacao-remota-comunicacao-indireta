import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe que implementa o servidor da calculadora usando Sockets.
 * Responsável por escutar conexões, receber a expressão e delegar o cálculo.
 */
public class ServidorCalculadora {

    // A porta que o servidor irá escutar por requisições de clientes.
    private static final int PORTA = 12345;

    // Instância da lógica de cálculo que interpretará a expressão.
    private InterpretadorAritmetico interpretador = new InterpretadorAritmetico();

    /**
     * Ponto de entrada do programa Servidor.
     */
    public static void main(String[] args) {
        new ServidorCalculadora().iniciar();
    }

    /**
     * Configura e inicia o socket de escuta do servidor.
     * O servidor roda em um loop infinito esperando por conexões.
     */
    public void iniciar() {
        // Tenta criar o ServerSocket, que lida com a escuta na porta definida.
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA + ". Aguardando conexão...");

            // Loop principal do servidor (roda indefinidamente).
            while (true) {
                // 'serverSocket.accept()' bloqueia a execução até que um cliente se conecte.
                // clientSocket é o Socket específico para comunicação com esse cliente.
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                    // --- Configuração dos Streams de I/O ---
                    // BufferedReader: Lê dados enviados pelo cliente (requisição).
                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                    // PrintWriter: Envia dados para o cliente (resposta), com autoFlush ativado (true).
                    PrintWriter saida = new PrintWriter(clientSocket.getOutputStream(), true);

                    // Lê a expressão enviada pelo cliente (o 'protocolo' da nossa aplicação).
                    String requisicao = entrada.readLine();
                    if (requisicao != null) {
                        System.out.println("Requisição recebida: " + requisicao);

                        // Delega a expressão para a lógica de cálculo.
                        String resultado = processarRequisicao(requisicao);

                        // Envia o resultado ou a mensagem de erro de volta ao cliente.
                        saida.println(resultado);
                        System.out.println("Resultado enviado: " + resultado);
                    }
                } catch (Exception e) {
                    // Trata erros que ocorrem durante a comunicação com um cliente específico.
                    System.err.println("Erro ao lidar com o cliente: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Trata erros fatais (ex: porta já em uso, falha ao abrir o servidor).
            System.err.println("Erro fatal no servidor: " + e.getMessage());
        }
    }

    /**
     * Método responsável por validar e calcular a expressão.
     * @param expressao A string contendo a expressão a ser calculada.
     * @return O resultado do cálculo ou uma string de erro.
     */
    private String processarRequisicao(String expressao) {
        try {
            // Chama o método que avalia a expressão completa, respeitando precedência e parênteses.
            double resultado = interpretador.avaliarExpressao(expressao);

            // Retorna o resultado numérico convertido para string.
            return String.valueOf(resultado);

        } catch (IllegalArgumentException | ArithmeticException e) {
            // Captura erros da lógica de cálculo (ex: formato inválido, divisão por zero).
            return "ERRO: " + e.getMessage();
        }
    }
}