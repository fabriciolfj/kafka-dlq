# kafka-dlq
- nesse projeto demonstra o uso do dlt (dlq) com spring kafka
- desabilitamos o retry do spring kafka para demonstração, conforme abaixo:
```
    @KafkaListener(topics = { "persons"}, groupId = "persons")
    @RetryableTopic(attempts = "1", kafkaTemplate = "retryableTopicKafkaTemplate") //representa o número de tentativas antes de enviar a dlq
    public void handlePerson(final Person person, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("receive {}, source topic {}", person, topic);
    }
```
- para o dlt funcionar, precisamos de um método anotado com @DltHandler, na mesma classe do listener correspondente
- temos 3 configurações dlt
  - FAIL_ON_ERROR -> quando o consumidor não consegui processar o evento, o mesmo cai na dlt
  - ALWAYS_RETRY_ON_ERROR -> grante que o consumidor dlt tenta reprocessar o evento em caso de falha
  - NO_DLT -> desativa o DLT