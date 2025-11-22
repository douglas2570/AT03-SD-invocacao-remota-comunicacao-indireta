import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Cliente HTTP para a Calculadora Distribuída.
 * Envia requisições POST para o servidor e implementa uma política de Retry (tentativas).
 */
public class CalculadoraClientHTTP {

    // URL do servidor HTTP local.
    private static final String URL_CALCULADORA = "http://localhost:8080/calcular";

    // --- Configurações da Política de Retry ---
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_BACKOFF_MS = 1000; // 1 segundo inicial

    public static void main(String[] args) {
        // --- Demonstração de todas as operações ---

        // 1. Soma (operacao=1)
        enviarEReceber("20", "5", 1);

        // 2. Subtração (operacao=2)
        enviarEReceber("30", "12", 2);

        // 3. Multiplicação (operacao=3)
        enviarEReceber("4", "7.5", 3);

        // 4. Divisão (operacao=4)
        enviarEReceber("100", "4", 4);

        // 5. Teste de Divisão por Zero (espera-se um erro do servidor)
        enviarEReceber("10", "0", 4);
    }

    /**
     * Envia a requisição HTTP com política de Retry.
     * @param oper1 Primeiro operando.
     * @param oper2 Segundo operando.
     * @param operacao Código da operação (1=soma, 2=subtrai, 3=multiplica, 4=divide).
     */
    public static void enviarEReceber(String oper1, String oper2, int operacao) {

        String postData = String.format("oper1=%s&oper2=%s&operacao=%d", oper1, oper2, operacao);
        String operacaoSimbolo = getOperacaoSimbolo(operacao);

        System.out.printf("\n--- Testando: %s %s %s ---\n", oper1, operacaoSimbolo, oper2);

        // --- Lógica de Retry com Backoff Exponencial ---
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            long delay = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt - 1);

            try {
                String resultado = realizarRequisicao(postData);
                System.out.printf("  [SUCESSO] Resposta do Servidor: %s\n", resultado);
                return; // Sai do método se for bem-sucedido

            } catch (IOException e) {
                // Trata falhas de I/O (ex: servidor offline, timeout).
                System.err.printf("  [TENTATIVA %d/%d] Falha: %s\n", attempt, MAX_RETRIES, e.getMessage());

                // Se for a última tentativa, não espera e falha.
                if (attempt < MAX_RETRIES) {
                    System.out.printf("  Aguardando %d ms antes de tentar novamente...\n", delay);
                    try {
                        TimeUnit.MILLISECONDS.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    System.err.println("  [FALHA FATAL] Todas as tentativas falharam.");
                }
            }
        }
    }

    /**
     * Realiza a requisição POST para o servidor.
     * @param postData Dados no formato 'x=a&y=b'.
     * @return A resposta JSON do servidor como String.
     * @throws IOException Se houver falha na comunicação de rede.
     */
    private static String realizarRequisicao(String postData) throws IOException {
        // Usa HttpURLConnection para requisições HTTP (não HTTPS, já que é localhost).
        URL url = new URL(URL_CALCULADORA);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // --- Configuração da Conexão ---
        conn.setReadTimeout(10000); // Timeout de leitura (10 segundos)
        conn.setConnectTimeout(15000); // Timeout de conexão (15 segundos)
        conn.setRequestMethod("POST");
        conn.setDoOutput(true); // Indica que iremos escrever dados no corpo (POST)

        // Define o tipo de conteúdo que estamos enviando.
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // --- 1. Envio dos Parâmetros ---
        try (OutputStream os = conn.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {

            writer.write(postData);
            writer.flush(); // Força o envio dos dados.
        }

        // --- 2. Recebimento e Tratamento da Resposta ---
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { // Status 200 OK
            // Usa try-with-resources para garantir o fechamento do BufferedReader
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } else {
            // Se o código de resposta não for 200 OK, levanta uma exceção para o Retry tratar.
            throw new IOException("HTTP code: " + responseCode);
        }
    }

    private static String getOperacaoSimbolo(int operacao) {
        switch (operacao) {
            case 1: return "+";
            case 2: return "-";
            case 3: return "*";
            case 4: return "/";
            default: return "?";
        }
    }
}