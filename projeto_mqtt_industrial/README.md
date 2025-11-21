
# 📡  Projeto MQTT (Monitoramento IoT)

Simulação de um sistema de monitoramento de temperatura industrial (caldeira) utilizando arquitetura **Pub/Sub** para desacoplamento entre produtores e consumidores de informação.

---

## 📺 Demonstração

**Vídeo de Execução do Projeto:** https://drive.google.com/drive/folders/1xe5Me5SfbVVhaxTKLE1CEXHNUzam6QQT?usp=sharing

---

## 🏗️ Arquitetura do Sistema

O sistema é composto por três componentes autônomos que se comunicam exclusivamente através de um **Broker MQTT**:

### 🔥 Sensor de Temperatura (`sensor_temperatura.py`)
- Simula a leitura de um sensor de caldeira.  
- Publica dados **JSON** a cada **60 segundos** no tópico:  


### 📊 Serviço CAT – *Compute Average Temperature* (`servico_cat.py`)
- Assina o tópico de dados.  
- Calcula a **média móvel** das temperaturas (janela de **120s**).  
- Detecta anomalias:  
- Temperatura **> 200°C**  
- Aumento repentino **> 5°C**  
- Publica alertas no tópico:  


### 🚨 Serviço Alarms (`servico_alarms.py`)
- Consumidor final.  
- Assina o tópico de alertas e exibe **notificações visuais** no console para o operador.

---

## 🛠️ Pré-requisitos

- Python 3.x instalado  
- Mosquitto Broker instalado e rodando localmente (porta padrão **1883**)  
- Biblioteca Python **Paho-MQTT**:
```bash
pip install paho-mqtt
```


## 🚀 Como Executar

#Para verificar o funcionamento do desacoplamento, recomenda-se abrir 3 terminais e executar os scripts na seguinte ordem:

1️ Inicie o Monitoramento (Alarms)
```bash
python servico_alarms.py
```
```bash
2️ Inicie o Processamento (CAT)
python servico_cat.py
```
```bash
3️ Inicie o Sensor
python sensor_temperatura.py
```