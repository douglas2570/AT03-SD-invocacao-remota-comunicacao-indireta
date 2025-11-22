import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Servidor HTTP da Calculadora.
 * Recebe requisições POST no endpoint /calcular e retorna o resultado em JSON.
 * Utiliza a API interna do Java (com.sun.net.httpserver).
 */
public class CalculadoraServerHTTP {

    private static final int PORTA = 8080;

    public static void main(String[] args) throws IOException {
        // Cria uma instância do servidor que escuta no endereço localhost e na porta 8080.
        HttpServer server = HttpServer.create(new InetSocketAddress(PORTA), 0);

        // Define o contexto (endpoint) para lidar com as requisições de cálculo.
        server.createContext("/calcular", new CalculadoraHandler());

        // Define um executor para lidar com as requisições, permitindo processamento assíncrono.
        server.setExecutor(null);

        server.start();
        System.out.println("Servidor HTTP da Calculadora iniciado na porta " + PORTA + ". Aguardando requisições...");
    }

    /**
     * Manipulador de requisições HTTP (Handler) para o endpoint /calcular.
     */
    static class CalculadoraHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 200;

            // O servidor só aceita requisições POST.
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                statusCode = 405; // Método Não Permitido
                response = "{\"erro\": \"Apenas requisições POST são permitidas.\"}";
            } else {
                // Lê o corpo da requisição POST (onde estão os parâmetros oper1, oper2, operacao).
                InputStream is = exchange.getRequestBody();
                String requestBody = new String(is.readAllBytes());

                // Processa a requisição e calcula o resultado.
                response = processarRequisicao(requestBody);
            }

            // Define o tipo de conteúdo da resposta como JSON.
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            // Envia o status e o corpo da resposta de volta ao cliente.
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        /**
         * Extrai os parâmetros e realiza o cálculo.
         * @param requestBody Corpo da requisição no formato "oper1=X&oper2=Y&operacao=Z".
         * @return String JSON com o resultado ou erro.
         */
        private String processarRequisicao(String requestBody) {
            Map<String, String> params = parseQuery(requestBody);

            try {
                double oper1 = Double.parseDouble(params.get("oper1"));
                double oper2 = Double.parseDouble(params.get("oper2"));
                int operacao = Integer.parseInt(params.get("operacao"));

                double resultado;
                String operacaoString;

                switch (operacao) {
                    case 1:
                        resultado = oper1 + oper2;
                        operacaoString = "Soma";
                        break;
                    case 2:
                        resultado = oper1 - oper2;
                        operacaoString = "Subtração";
                        break;
                    case 3:
                        resultado = oper1 * oper2;
                        operacaoString = "Multiplicação";
                        break;
                    case 4:
                        if (oper2 == 0) {
                            return "{\"erro\": \"Divisão por zero não é permitida.\"}";
                        }
                        resultado = oper1 / oper2;
                        operacaoString = "Divisão";
                        break;
                    default:
                        return "{\"erro\": \"Operação inválida. Use 1 (soma), 2 (subtração), 3 (multiplicação) ou 4 (divisão).\"}";
                }

                // Retorna o resultado no formato JSON
                return String.format("{\"oper1\": %f, \"oper2\": %f, \"operacao\": \"%s\", \"resultado\": %f}",
                        oper1, oper2, operacaoString, resultado);

            } catch (NumberFormatException | NullPointerException e) {
                return "{\"erro\": \"Parâmetros numéricos ou de operação inválidos ou ausentes.\"}";
            }
        }

        /**
         * Converte a string de query (ex: "a=1&b=2") em um Map de chave/valor.
         */
        private Map<String, String> parseQuery(String query) {
            Map<String, String> result = new HashMap<>();
            if (query == null || query.isEmpty()) {
                return result;
            }
            try {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (idx > 0) {
                        String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                        String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        result.put(key, value);
                    }
                }
            } catch (Exception e) {
                // Ignora o erro de decodificação para manter a robustez.
            }
            return result;
        }
    }
}