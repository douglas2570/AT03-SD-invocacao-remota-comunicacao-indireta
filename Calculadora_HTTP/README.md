## Visão Geral do Projeto

Este projeto demonstra a comunicação Cliente-Servidor utilizando o protocolo HTTP/POST e dados no formato JSON. O Cliente implementa uma política de Retry (tentativas) com Backoff Exponencial para aumentar a resiliência contra falhas temporárias de rede.

### 🛠️ Pré-requisitos
Java Development Kit (JDK) 8 ou superior instalado.

### 🏃 Como Rodar o Projeto (Localhost)
Você deve iniciar o Servidor primeiro e, em seguida, o Cliente, em terminais separados.

#### Passo 1: Compilação
Navegue até o diretório do projeto no terminal e compile ambos os arquivos:
```bash
javac *.java
```
#### Passo 2: Iniciar o Servidor
Abra o Terminal 1 e execute o servidor. Ele começará a escutar na porta 8080.
```bash
java CalculadoraServerHTTP
```
- Saída esperada: ``Servidor HTTP da Calculadora iniciado na porta 8080. Aguardando requisições...``
- Mantenha este terminal aberto.

#### Passo 3: Iniciar o Cliente
Abra o Terminal 2 e execute o cliente. Ele irá demonstrar as quatro operações e um teste de divisão por zero, aplicando a lógica de retry se necessário.
```bash
java CalculadoraClientHTTP
```

### Teste o Retry:
Para testar a política de retry, simplesmente feche o Terminal 1 (desligando o servidor) e, em seguida, execute o Cliente no Terminal 2. Você verá o cliente tentar se conectar 3 vezes com um tempo de espera crescente antes de desistir.


