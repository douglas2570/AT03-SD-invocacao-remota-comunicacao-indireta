import paho.mqtt.client as mqtt
import json
import time

# ==============================================================================
# CONFIGURAÇÕES E VARIÁVEIS GLOBAIS
# ==============================================================================
# Configuração para rodar localmente com o Mosquitto
BROKER = 'localhost'
PORT = 1883
TOPIC_ALERTAS = 'caldeira/temperatura/alertas'

# ==============================================================================
# FUNÇÕES DE CALLBACK
# ==============================================================================

def on_connect(client, userdata, flags, rc, properties=None):
    """
    Callback executado quando a conexão com o broker é estabelecida.
    """
    if rc == 0:
        print(f"[ALARMS] Conectado ao Broker local ({BROKER}:{PORT})")
        # Subscreve ao tópico de alertas com Qualidade de Serviço 1 (QoS 1)
        # QoS 1 garante que a mensagem chegue pelo menos uma vez.
        client.subscribe(TOPIC_ALERTAS, qos=1)
        print(f"[ALARMS] Monitorando tópico: {TOPIC_ALERTAS}")
        print("-" * 50)
        print("AGUARDANDO ALERTAS DO SISTEMA...")
        print("-" * 50)
    else:
        print(f"[ALARMS] Falha na conexão. Código: {rc}")

def on_message(client, userdata, msg):
    """
    Callback principal: Este script age como um 'Observador Desacoplado'.
    Ele não sabe quem enviou a mensagem (se foi o CAT ou outro serviço),
    ele apenas reage ao evento publicado no tópico de interesse.
    """
    try:
        # Decodifica o payload para string
        payload_str = msg.payload.decode('utf-8')
        
        # Tenta formatar se for JSON para ficar mais bonito, senão exibe raw
        try:
            payload_dict = json.loads(payload_str)
            mensagem_formatada = f"Tipo: {payload_dict.get('tipo')}\nDetalhe: {payload_dict.get('mensagem')}"
        except json.JSONDecodeError:
            mensagem_formatada = payload_str

        # Exibição visualmente destacada no console
        print("\n" + "="*60)
        print(f"            [!!! ALARME DISPARADO !!!]")
        print("="*60)
        print(f"Tópico : {msg.topic}")
        print("-" * 60)
        print(f"Conteúdo:\n{mensagem_formatada}")
        print("="*60 + "\n")

    except Exception as e:
        print(f"[ERRO] Falha ao processar alerta: {e}")

# ==============================================================================
# EXECUÇÃO PRINCIPAL
# ==============================================================================
if __name__ == "__main__":
    # Inicializa o cliente MQTT
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    
    # Define os callbacks
    client.on_connect = on_connect
    client.on_message = on_message
    
    print("Iniciando Serviço Alarms (Monitoramento de Segurança)...")
    
    try:
        # Conecta ao broker local
        client.connect(BROKER, PORT, 60)
        
        # Loop infinito: Mantém o script rodando apenas escutando eventos.
        # Isso demonstra o Desacoplamento Temporal: o serviço fica aqui disponível
        # esperando eventos acontecerem, sem travar o restante do sistema.
        client.loop_forever()
        
    except ConnectionRefusedError:
        print("ERRO CRÍTICO: Não foi possível conectar ao 'localhost'.")
        print("Certifique-se de que o Mosquitto está rodando.")
    except KeyboardInterrupt:
        print("\nServiço Alarms encerrado.")