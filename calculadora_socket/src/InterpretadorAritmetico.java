import java.util.Stack;

/**
 * Classe responsável por avaliar expressões aritméticas complexas
 * que incluem múltiplos números, operadores (+, -, *, /) e parênteses.
 * Utiliza o algoritmo baseado em pilhas para respeitar a ordem de precedência.
 */
public class InterpretadorAritmetico {

    /**
     * Avalia uma expressão aritmética complexa (ex: "3 * (5 + 2) - 1").
     * @param expressao A string da expressão.
     * @return O resultado do cálculo.
     * @throws IllegalArgumentException Se a expressão for mal formada ou inválida.
     */
    public double avaliarExpressao(String expressao) {
        // --- 1. Pré-processamento e Tokenização (preparar para análise) ---

        // Remove todos os espaços em branco para facilitar a tokenização.
        String tokenizedExpression = expressao.replaceAll("\\s+", "");
        // Trata a conversão de números negativos em expressões (ex: 5*-3 -> 5 * -3).
        tokenizedExpression = tokenizedExpression.replaceAll("(?<=[^\\d])(-)(\\d)", "$1 $2");
        // Adiciona espaços ao redor de todos os operadores e parênteses para separá-los como 'tokens'.
        tokenizedExpression = tokenizedExpression.replaceAll("([+\\-*/()])", " $1 ");
        // Normaliza a expressão e separa em um array de tokens (números e operadores).
        tokenizedExpression = tokenizedExpression.trim().replaceAll("\\s+", " ");

        String[] tokens = tokenizedExpression.split(" ");

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Expressão vazia ou inválida.");
        }

        // --- 2. Algoritmo de Avaliação Baseado em Pilhas ---

        // Pilha para armazenar os valores numéricos (operandos).
        Stack<Double> valores = new Stack<>();
        // Pilha para armazenar os operadores e parênteses.
        Stack<Character> operadores = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.isEmpty()) {
                continue;
            }

            char primeiroChar = token.charAt(0);

            // Caso 1: O token é um número.
            if (Character.isDigit(primeiroChar) || token.matches("-?\\d+(\\.\\d+)?")) {
                try {
                    valores.push(Double.parseDouble(token)); // Coloca o número na pilha de valores.
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Token inválido na expressão: " + token);
                }
            }
            // Caso 2: Parêntese de abertura.
            else if (primeiroChar == '(') {
                operadores.push(primeiroChar); // Parênteses de abertura vão direto para a pilha de operadores.
            }
            // Caso 3: Parêntese de fechamento.
            else if (primeiroChar == ')') {
                // Enquanto não encontrar o parêntese de abertura correspondente, resolve as operações internas.
                while (!operadores.isEmpty() && operadores.peek() != '(') {
                    aplicarOperacao(valores, operadores);
                }
                // Verifica se faltou parêntese de abertura.
                if (operadores.isEmpty()) throw new IllegalArgumentException("Parênteses não balanceados.");

                operadores.pop(); // Remove o parêntese de abertura da pilha.
            }
            // Caso 4: O token é um operador (+, -, *, /).
            else if (isOperador(primeiroChar)) {
                char op = primeiroChar;
                // Aplica operações anteriores que têm precedência maior ou igual (da esquerda para a direita).
                while (!operadores.isEmpty() && temPrecedencia(op, operadores.peek())) {
                    aplicarOperacao(valores, operadores);
                }
                operadores.push(op); // Coloca o operador atual na pilha.
            } else {
                throw new IllegalArgumentException("Caractere desconhecido: " + token);
            }
        }

        // --- 3. Finalização ---

        // Aplica todas as operações restantes na pilha.
        while (!operadores.isEmpty()) {
            aplicarOperacao(valores, operadores);
        }

        // Se a expressão foi válida, a pilha de valores deve ter exatamente um resultado.
        if (valores.size() != 1 || !operadores.isEmpty()) {
            throw new IllegalArgumentException("Estrutura da expressão inválida.");
        }

        return valores.pop();
    }

    // --- Métodos Auxiliares ---

    /** Verifica se um caractere é um operador aritmético básico. */
    private boolean isOperador(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Determina se o operador op2 tem precedência (ou igualdade, para associatividade) sobre op1.
     * Isso define se op2 (no topo da pilha) deve ser executado antes de op1 (o novo operador).
     */
    private boolean temPrecedencia(char op1, char op2) {
        // Parênteses de abertura e fechamento não afetam a precedência diretamente.
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        // Multiplicação e Divisão (op1) NÃO têm precedência sobre Soma e Subtração (op2) no topo da pilha.
        // *Na verdade, a lógica é: se op1 é mais fraco (ex: '+') e op2 é mais forte (ex: '*'), op2 deve ser executado PRIMEIRO.
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    /**
     * Executa a operação no topo da pilha de operadores, usando os dois valores do topo da pilha de valores.
     */
    private void aplicarOperacao(Stack<Double> valores, Stack<Character> operadores) {
        char op = operadores.pop();
        if (valores.size() < 2) {
            throw new IllegalArgumentException("Faltam operandos para o operador " + op);
        }
        // A ordem é importante: b é o segundo operando (removido primeiro), a é o primeiro.
        double b = valores.pop();
        double a = valores.pop();

        switch (op) {
            case '+':
                valores.push(a + b);
                break;
            case '-':
                valores.push(a - b);
                break;
            case '*':
                valores.push(a * b);
                break;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Divisão por zero!");
                }
                valores.push(a / b);
                break;
        }
    }
}